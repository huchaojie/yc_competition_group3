package com.yc.ibike.dao;

import com.yc.ibike.bean.Bike;

public interface BikeDao {
	/**
	 * 新增一辆车入库
	 * */
	public Bike addBike(Bike bike);
	
	/**
	 * 跟新操作(对应业务中的入库，上线,解锁)
	 * */
	public void updateBike(Bike bike);
	
	/**
	 * 找车
	 * */
	public Bike findBike(String bid);
	
	public int delBike(String bid);
	
	public int delChekedBike(String checkdValue);
}
