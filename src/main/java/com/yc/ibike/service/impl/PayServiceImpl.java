package com.yc.ibike.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yc.ibike.bean.Bike;
import com.yc.ibike.bean.PayModel;
import com.yc.ibike.bean.User;
import com.yc.ibike.service.BikeService;
import com.yc.ibike.service.PayService;
import com.yc.ibike.service.UserService;
@Service
@Transactional
public class PayServiceImpl implements PayService {
	@Autowired
	private BikeService bikeService;
	@Autowired
	private UserService userService;
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private Logger logger = LogManager.getLogger();

	public static final int MONEYPERHOUR=4;
	
	@Override
	public void pay(PayModel pm) {
		//1.计算金额
		Long startTime=pm.getStartTime();
		Long endTime=pm.getEndTime();
		Long spendTime=endTime-startTime;
		double hours=(Double.parseDouble(spendTime+""))/(60*60);
		Integer h=(int)Math.ceil(hours);//小时数
		int payMoney=h*MONEYPERHOUR;//花费
		pm.setPayMoney(payMoney);
		pm.setLogTime(new Date().toLocaleString());
		//2.将数据保存到Mongo的payLog(uuid,phoneNum,openId,结账时间(年月日小时)起 (经纬),时间,止(经纬),时间,花费)
		this.mongoTemplate.save(pm,"billingLog");
		//3.修改单车的经纬度状态为1
		Query q=new Query(Criteria.where("id").is(pm.getBid()));
		Update u=new Update().set("latitude", pm.getLatitude()).set("longitude", pm.getLongtitude());
		//Double[] loc=new Double[]{pm.getLatitude(),pm.getLongtitude()};
		u.set("loc", new Double[]{pm.getLatitude(),pm.getLongtitude()}).set("status",1);
		mongoTemplate.updateFirst(q, u, Bike.class,"bike");
		//4.修改用户态:status,balance-花费
		Query qu=new Query(Criteria.where("phoneNum").is(pm.getPhoneNum()));
		User user=mongoTemplate.findOne(qu, User.class,"Users");
		Update uu=new Update().set("status",3);//用户的状态:0没有注册  1注册电话成功 2缴纳押金 3实名认证
		System.out.println(user.getBalance()-payMoney);
		uu.set("balance",user.getBalance()-payMoney);
		mongoTemplate.updateFirst(qu, uu, User.class,"Users");
		logger.info("结账成功");
	}
	
}
