package com.yc.ibike.service.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yc.ibike.bean.LogBack;
import com.yc.ibike.dao.LogBackDao;
import com.yc.ibike.service.LogBackService;

@Service
@Transactional
public class LogBackServiceImpl implements LogBackService {
	
	//操作redis中的v是对象类型的数据
	@Autowired	
	public LogBackDao lbd; 
	
	@Override
	public String getPV_UV() {
		return lbd.getPV_UV();
	}
	
	public List<LogBack> getConsumerVisit(){
		return lbd.getConsumerVisit();
	}
	
}
