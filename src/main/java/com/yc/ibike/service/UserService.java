package com.yc.ibike.service;

import java.util.List;

import com.yc.ibike.bean.User;

public interface UserService {
	//获取注册的验证码
	public void genVerifyCode(String nationCode,String phoneNum) throws Exception;
	
	//注册表单的按钮
	public boolean verify(User user);
	
	/**
	 * 押金充值
	 * @param user
	 * @return
	 */
	public boolean deposit(User user);
	
	/**
	 * 身份验证
	 * @param user
	 * @return
	 */
	public boolean identity(User user);
	
	/**
	 * 充值
	 * @param balance
	 * @param phoneNum
	 * @return
	 */
	public boolean recharge(double balance, String phoneNum);
	
	
	/**
	 * 生成一个uuid,以它为键，  sessionkey和openid为值，存到 redis中，且设置超时时间为 30天。
	 * @param openId
	 * @param sessionKey
	 * @return
	 */
	public String redisSessionKey(String openId,String sessionKey);

	/**
	 * 添加用户到mongo的users集合
	 * @param u
	 */
	public void addMember(User u);

	/**

	 * 根据openid到 mongo中的 users集合中查是否有这个人.
	 * @param openid
	 * @return
	 */
	public List<User> selectMember(String openid) ;
	
	public List<User> findAllStaff();
	
	public List<User> findAllConsumer();
	
}
