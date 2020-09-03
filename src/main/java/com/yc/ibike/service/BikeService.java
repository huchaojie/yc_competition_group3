package com.yc.ibike.service;

import java.util.List;

import com.yc.ibike.bean.Bike;

public interface BikeService {
	/**
	 * 开锁
	 * 	1)bid必须
	 * 	2)根据bid查车
	 * 	3)车的状态
	 * */
	public void open(Bike bike);

	/**
	 *根据Bid查车
	 * @param bid
	 * @return
	 */
	public Bike findByBid(String bid);

	/**
	 * 新车上架:生成bid 和二维码
	 */
	public Bike addNewBike(Bike bike);

	public List<Bike> findNearAll(Bike bike);
	
	public List<Bike> findAllBike();
	
	/**
	 * 报修
	 * @param bike
	 */
	public void reportMantinant(Bike bike);
	
	/**
	 * 删除单车
	 * @param bike
	 * @return
	 */
	public int delBike(String bid);

	public int delChekedBike(String checkValue);

}
