package com.yc.ibike.service.impl;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yc.ibike.bean.Bike;
import com.yc.ibike.dao.BikeDao;
import com.yc.ibike.service.BikeService;


@Service
@Transactional
public class BikeServiceImpl implements BikeService{
	@Autowired
	private BikeDao bikedao;
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private Logger logger=LogManager.getLogger(); 
	
	@Override
	public void open(Bike bike) {
		if(bike.getBid() == null){
			throw new RuntimeException("缺少单车编号");
		}
		Bike b=findByBid(bike.getBid());
		if(b==null){
			throw new RuntimeException("查无此车");
		}
		switch(b.getStatus()){
		case Bike.UNACTIVE:
			throw new RuntimeException("此车暂未启用,请更换一辆");
		case Bike.USING:
			throw new RuntimeException("此车正在骑行中,请更换一辆");
		case Bike.INTROUBLE:
			throw new RuntimeException("此车等待维修,请更换一辆");
		}
		bike.setStatus(Bike.USING);
		bikedao.updateBike(bike);
	}

	@Override
	@Transactional(readOnly=true,propagation=Propagation.SUPPORTS)
	public Bike findByBid(String bid) {
		Bike b=null;
		try {
			b=bikedao.findBike(bid);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return b;
	}

	@Override
	public Bike addNewBike(Bike bike) {
		Bike b=bikedao.addBike(bike);
//		String bid=b.getBid();
//		bike=findByBid(bid);
//		
//		//TODO:根据bid生成二维码
//		String qrcode=bid+"";
//		bike.setQrcode(qrcode);;
//		bikedao.updateBike(bike);//再跟新
		return bike;
	}

	@Override
	public List<Bike> findNearAll(Bike bike) {
		//集群版
		Point point = new Point(bike.getLatitude(), bike.getLongtitude());
		Query q=new Query(   
				    Criteria.where("loc").near(point)
				            .maxDistance(20)
				         )
				            .limit(10);
		
		List<Bike> list = mongoTemplate.find(  q, Bike.class);
		for (Bike b : list) {
			b.setBid(b.getId());
			b.setLatitude(b.getLoc()[0]);
			b.setLongtitude(b.getLoc()[1]);
		}

		return list;
		
		  //单节点版
//		Query query=new Query();
//		query.addCriteria(Criteria.where("status").is(1))
//						.addCriteria(Criteria.where("loc")
//						.near(new Point(bike.getLatitude(),bike.getLongtitude()))).limit(5);
//		List<Bike> list=this.mongoTemplate.find(query, Bike.class, "bike");
//		for(Bike b:list){
//			b.setBid(b.getId());
//			b.setId(null);
//			b.setLatitude(b.getLoc()[0]);
//			b.setLongtitude(b.getLoc()[1]);
//			b.setLoc(null);
//		}
//		return list;
	}

	@Override
	public void reportMantinant(Bike bike) {
		//1. 根据bid查出车的状态, 要报修的车不能是行驶状态 2
				Query q=new Query();
				q.addCriteria(    Criteria.where("id").is(bike.getBid()));
//				Bike torepair=this.mongoTemplate.findOne(q, Bike.class,"bike");
				Bike torepair=mongoTemplate.findById( bike.getBid(), Bike.class,"bike");
				if(  torepair==null) {
					throw new RuntimeException("查无此车登记:"+ bike.getBid());
				}
				if( torepair.getStatus()==2) {
					throw new RuntimeException("正在报修的车:"+ bike.getBid()+"正在行驶状态，为了您的安全,请锁车后再报修");
				}
				//2. 将此信息存入到  mongo中，并加入一个状态  handleStatus: 0 暂未处理  1已经处理 
				//TODO: 根据经纬度查询具体地址. ,存到  mongo 的 torepairbikes
				//this.mongoTemplate.insert(bike, "torepairbikes");
				//      以后处理完了，要加入  handler 处理人   handleTime 处理时间
				//3. 将此车的状态在  bike collection中更改为 3
				Update u=new Update();
				u.set("status", 3);
				this.mongoTemplate.updateFirst(q, u, Bike.class,"bike");
	}

	@Override
	public List<Bike> findAllBike() {
		List<Bike> list = mongoTemplate.findAll(Bike.class,"bike");
		for (Bike b : list) {
			b.setBid(b.getId());
			b.setLatitude(b.getLoc()[0]);
			b.setLongtitude(b.getLoc()[1]);
			b.setId(null);
			b.setLoc(null);
		}

		return list;
	}

	@Override
	public int delBike(String bid) {
		return bikedao.delBike(bid);
	}

	@Override
	public int delChekedBike(String checkValue) {
		return bikedao.delChekedBike(checkValue);
	}
	
}
