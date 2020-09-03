package com.yc.ibike.dao;

import java.util.List;

import com.yc.ibike.bean.LogBack;

public interface LogBackDao {
	
	/**
	 * 获取pv_uv
	 * @return
	 */
	public String getPV_UV();
	
	/**
	 * 获取每个用户的总浏览次数
	 */
	public List<LogBack> getConsumerVisit();
}
