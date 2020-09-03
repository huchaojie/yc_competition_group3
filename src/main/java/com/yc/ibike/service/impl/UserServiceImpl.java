package com.yc.ibike.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.mongodb.client.result.UpdateResult;
import com.yc.ibike.bean.User;
import com.yc.ibike.service.UserService;

@Transactional
@Service
public class UserServiceImpl implements UserService{
	private Logger logger=Logger.getLogger(UserServiceImpl.class);
	//操作redis中的v是对象类型的数据
	@Autowired
	private RedisTemplate redisTemplate;
	
	//操作redis中的字符串数据类型
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void genVerifyCode(String nationCode, String phoneNum) throws Exception {
		//短信接口的appKey
		//生成验证码
		String code=(int)((Math.random()*9+1)*1000)+"";
		logger.info("生成的验证码为:"+code);
		//SmsUtils.sendSms(code, new String[] {nationCode+phoneNum});    //TODO: 以后发送
		//将数据保存到redis中,redis的key手机号,value是验证码,有效时常120s
		stringRedisTemplate.opsForValue().set(phoneNum, code,120,TimeUnit.SECONDS);
	}

	@Override
	public boolean verify(User user){
		boolean flag=false;
		String phoneNum=user.getPhoneNum();
		String verifyCode=user.getVerifyCode();
		String code=stringRedisTemplate.opsForValue().get(phoneNum);
		String openId=user.getOpenId();
		String uuid=user.getUuid();
		System.out.println(user);
		if(verifyCode!=null &&verifyCode.equals(code)){
			//验证成功后 将用户信息保存到MOngo中
			//mongoTemplate.insert(user);
			int status=1;
			UpdateResult result=mongoTemplate.updateFirst(new Query(Criteria.where("openId").is(openId)), new Update().set("status", status).set("phoneNum", phoneNum),  User.class);
			if(  result.getModifiedCount() ==1 ) {
				return true;
			}else {
				return false;
			}
		}
		return flag;
	}

	@Override
	public boolean deposit(User user) {
		int status=2;//状态变为2
		int money=299;//押金数
		UpdateResult result=mongoTemplate.updateFirst(new Query( Criteria.where("phoneNum").is(user.getPhoneNum())),
				new Update().set("status", status).set("deposit", money), User.class);
		if(result.getModifiedCount()==1){
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public boolean identity(User user) {
		//TODO:调用第三方接口验证用户身份证是否是真实的
		int status=3;
		UpdateResult result=mongoTemplate.updateFirst(
				new Query( Criteria.where("phoneNum").is(user.getPhoneNum())),
				new Update().set("status", status)
							.set("name", user.getName())
							.set("idNum", user.getIdNum()),
				User.class);
		if(result.getModifiedCount()==1){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public boolean recharge(double balance, String phoneNum) {
		if(  balance<0) {
			throw new RuntimeException("充值金额不能为负数,当前为:"+balance);
		}
		boolean flag = true;
		// 更新用户的余额
		try {
			Query q=new Query(   Criteria.where("phoneNum").is(phoneNum)  );
			Update u=new Update().inc("balance", balance);
			UpdateResult res=mongoTemplate.updateFirst(q,u , User.class,"Users");
			System.out.println(res.getModifiedCount());
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	@Override
	public String redisSessionKey(String openId, String sessionKey) {
		String rsession=UUID.randomUUID().toString();
		//根据openid取出之前存的openid对应的sessionkey的值
		String oldSessionKey =stringRedisTemplate.opsForValue().get(openId);
		if(oldSessionKey!=null && !"".equals(oldSessionKey)){
			logger.info("oldSessionKey="+oldSessionKey);
			//删除之前Openid对应的缓存
			stringRedisTemplate.delete(oldSessionKey);
			logger.info("老的openid删除后="+stringRedisTemplate.opsForValue().get(oldSessionKey));
		}
		Gson g=new Gson();
		Map<String,String> m=new HashMap<String,String>();
		m.put("openId",openId);
		m.put("sessionKey", sessionKey);
		String s=g.toJson(m);
		stringRedisTemplate.opsForValue().set(rsession, s,30,TimeUnit.DAYS);
		stringRedisTemplate.opsForValue().set( openId, rsession, 30, TimeUnit.DAYS);
		return rsession;
	}

	@Override
	public void addMember(User u) {
		mongoTemplate.insert(u);
	}

	@Override
	public List<User> selectMember(String openid) {
		Query q=new Query(Criteria.where("openId").is(openid));
		return this.mongoTemplate.find(q, User.class,"Users");
	}

	@Override
	public List<User> findAllStaff() {
		return this.mongoTemplate.findAll(User.class,"Users");
	}

	@Override
	public List<User> findAllConsumer() {
		// TODO Auto-generated method stub
		return this.mongoTemplate.findAll(User.class,"Users");
	}
	
}
