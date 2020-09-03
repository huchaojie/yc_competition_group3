package com.yc.ibike.service;

import java.util.List;

import com.yc.ibike.bean.LogBack;

public interface LogBackService {
	public String getPV_UV();
	
	public List<LogBack> getConsumerVisit();
}
