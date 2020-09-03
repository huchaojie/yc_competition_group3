package com.yc.ibike.dao.impl;

import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.sql.DataSource;
import java.util.*;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;

import com.yc.ibike.bean.Bike;
import com.yc.ibike.dao.BikeDao;

@Repository  //表示由spring容器来托管这个Bean
public class BikeDaoImpl implements BikeDao {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	public void setDataSource(DataSource datasource){
		this.jdbcTemplate=new JdbcTemplate(datasource);
	}
	
	@Override
	public Bike addBike(Bike bike) {
		Map<Object,Object> m=new HashMap<Object,Object>();
		Double[] loc=new Double[2] ;
		loc[0]=bike.getLatitude();
		loc[1]=bike.getLongtitude();
		m.put("status", bike.getStatus());
		m.put("loc", loc);
		this.mongoTemplate.insert(m, "bike");
	
		return bike;
	}

	@Override
	public void updateBike(Bike bike) {  
//		String sql=new String("update bike set latitude=?,longtitude=?,status=?,qrcode=? where bid=?");
//		this.jdbcTemplate.update(connection->{
//			PreparedStatement ps=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
//			ps.setDouble(1, bike.getLatitude());
//			ps.setDouble(2, bike.getLongtitude());
//			ps.setInt(3, bike.getStatus());
//			ps.setString(4, bike.getQrcode());
//			ps.setString(5, bike.getBid());
//			return ps;
//		});
		Query q=new Query();
		q.addCriteria(    Criteria.where("id").is(bike.getBid()));
		Update u=new Update();
		u.set("status", bike.getStatus() );
		u.set("latitude", bike.getLatitude());
		u.set("longtitude", bike.getLongtitude());
		u.set("qrcode", bike.getQrcode());
		this.mongoTemplate.updateFirst(q, u, Bike.class,"bike");

		
	}

	@Override
	public Bike findBike(String bid) {
//		String sql="select * from bike where bid="+bid;  mysql版
//		return  (Bike) this.jdbcTemplate.queryForObject(sql, (resultSet,rowNum)->{
//			Bike b=new Bike();
//			b.setBid(resultSet.getString("bid"));
//			b.setLatitude(resultSet.getDouble("latitude"));
//			b.setLongtitude(resultSet.getDouble("longtitude"));
//			b.setQrcode(resultSet.getString("qrcode"));
//			b.setStatus(resultSet.getInt("status"));
//			return b;
//		});
		//Mongo版
		Bike b=mongoTemplate.findById( bid, Bike.class,"bike");
		return b;

	}

	
	@Override
	public int delBike(String bid) {
		Map<String,Object> m=new HashMap<String,Object>();
		m.put("_id",new ObjectId(bid));
		int res=(int)mongoTemplate.remove(m, "bike").getDeletedCount();
		return res;
	}

	@Override
	public int delChekedBike(String checkdValue) {
		
		List<String> li=new ArrayList<>();
		//[["5efb23fde9284a194c990521", "5efb23fde9284a194c990520", "5efb23fde9284a194c99051f", "5efb23fde9284a194c99051e"]]
		String[] res=checkdValue.split(",");
		for(String r:res ){
			li.add(r);
		}
		Query query = Query.query(Criteria.where("_id").in(li));
		return (int)mongoTemplate.remove(query, "bike").getDeletedCount();
	}
}
