package com.yc.ibike.bike;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yc.ibike.bean.Bike;
import com.yc.ibike.bean.LogBack;
import com.yc.ibike.config.AppConfig;
import com.yc.ibike.dao.BikeDao;
import com.yc.ibike.dao.LogBackDao;
import com.yc.ibike.service.BikeService;
import com.yc.ibike.service.UserService;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppConfig.class})
public class AppTest extends TestCase{
	@Autowired
	private DataSource datasource;
	@Autowired
	private BikeDao bikeDao;
	@Autowired
	private BikeService bikeService;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private UserService userService;
	
	@Autowired 
	private LogBackDao lbd;
//	
//	@Test
//	public void testUserserviceRegister() throws Exception{
//		String result=userService.register("1");
//		System.out.println(result);
//	}
	
	@Test
	public void testJdbcTemplate() throws Exception{
		List<LogBack> li=lbd.getConsumerVisit();
		for(LogBack l:li){
			System.out.println(l.getIpaddr());
			l.toString();
		}
	}
	
	@Test
	public void testUserservice() throws Exception{
		userService.genVerifyCode("86", "15074416232");
	}
	
	@Test
	public void testNearBikes(){
		Bike b=new Bike();
		b.setLatitude(28.189122);
		b.setLongtitude(112.943867);
		List<Bike> list=bikeService.findNearAll(b);
		System.out.println(list);
	}
	
	@Test
	public void testMongoSource(){ //单节点
		System.out.println(mongoTemplate.getDb().getName());
		System.out.println(mongoTemplate.getCollectionNames());
	}
	
	@Test
	public void testMongoTemplate() {//集群
		System.out.println( mongoTemplate.getDb().getName() );
		System.out.println(  mongoTemplate.getCollectionNames()  );
	}
	
	
	@Test
	public void testDataSource() throws SQLException{
		assertNotNull(datasource);
		assertNotNull(datasource.getConnection());
	}
	
	@Test public void testAddNewBike(){
		Bike b=new Bike();
		Bike result=bikeDao.addBike(b);
		assertNotNull(result.getBid());
		System.out.println(result.getBid());
	} 
	
	@Test 
	public void testUpdateBike(){
		Bike b=bikeDao.findBike("1");
		b.setLatitude(20.9);
		b.setLongtitude(22.2);
		b.setStatus(1);
		bikeDao.updateBike(b);
	} 
	
	@Test 
	public void testFindBike(){
		Bike b=bikeDao.findBike("1");
		assertNotNull(b);
	} 
	
	@Test
	public void testServiceOpen(){
		Bike b=bikeService.findByBid("1");
		bikeService.open(b);
	}
	
	@Test
	public void testServiceAddNewBike(){
		
		Bike b=new Bike();
		Bike result=bikeService.addNewBike(b);
		System.out.println(result.getQrcode());
	}
	
	@Test
	public void testRecharge(){
		
		double balance=100.00;
		String phoneNum="112233";
		
		System.out.println(userService.recharge(balance, phoneNum));
	}
	
	@Test
	public void testRedisTemplate() {
		 stringRedisTemplate.opsForValue().set("hello", "world");
		 stringRedisTemplate.opsForValue().set("hello2", "world");
	}
	
	
	@Test  // 准备测试数据
	public void insertbike() {
		double x=28.2043941;
		double y=112.959854;
		for (int i = 0; i < 100; i++) {
			x+=0.0000010;
			for (int j = 0; j < 100; j++) {
				y+=0.0000003;
				Double loc[] = new Double[] { Double.valueOf(x), Double.valueOf(y) };
				Bike b=new Bike();
				b.setStatus(1);
				b.setLoc(  loc);
				b.setQrcode("");
				mongoTemplate.insert(b);
			}
		}
	}
}
