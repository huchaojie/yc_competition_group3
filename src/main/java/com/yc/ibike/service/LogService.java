package com.yc.ibike.service;

import com.yc.ibike.bean.PayModel;

public interface LogService {
	/**
	 * 保存操作日志
	 * */
	public void save(String log);
	
	/**
	 * 保存充值日志
	 * */
	public void savePayLog(String log);
	
	/**
	 * 结束骑行日志
	 * */
	public void saveEndRideLog(String log);
	
	/**
	 * 开始骑行日志
	 * */
	public void saveStartRideLog(String log);
	
	/**
	 * 报修日志
	 * @param log
	 */
	public void saveRepairLog(String log);
}
