package com.yc.ibike.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.yc.ibike.bean.PayModel;
import com.yc.ibike.service.LogService;

@Service
public class LogServiceImpl implements LogService{
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	@Override
	public void save(String log){
		mongoTemplate.save(log,"logs");
	}

	@Override
	public void savePayLog(String log) {
		mongoTemplate.save(log,"paylogs");
	}
	@Override
	public void saveEndRideLog(String log) {
		//{"openid":"oKIkL481mLC6Jg1HrrBd8WHsa1iU","phoneNum":"15074416232","bid":"5ef895bdf8fe19f3e138d669","lat":28.189122,"lon":112.960318,"province":"湖南省","city":"长沙市","district":"岳麓区","street":"橘洲路","street_number":"橘洲路","endTime":"1593529236","times":2}
		System.out.println("开始的:"+log);
		String[] timelog=log.split("\"times\":");
		
		String spendTime=timelog[1].split("}")[0];
		double hours=(Double.parseDouble(spendTime))/(60*60);
		Integer h=(int)Math.ceil(hours);//小时数
		int payMoney=h*4;//花费
		log= log.substring(0, log.length()-1);
		String tmplog=log.concat(",\"amount\":"+payMoney+"}");
		//System.out.println(tmplog);
		mongoTemplate.save(tmplog,"ridelogs");
	}

	@Override
	public void saveStartRideLog(String log) {
		mongoTemplate.save(log,"start_ridelogs");
		
	}

	@Override
	public void saveRepairLog(String log) {
		System.out.println("进来了");
		mongoTemplate.save(log,"repairlogs");
		
	}
}
