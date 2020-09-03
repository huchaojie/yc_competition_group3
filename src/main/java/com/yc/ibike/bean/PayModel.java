package com.yc.ibike.bean;

import java.io.Serializable;


public class PayModel implements Serializable{
	
	private static final long serialVersionUID = 6866122797992225800L;
	private Double longtitude;
	private Double latitude;
	private String uuid;       //关于用户会话的id    相当于  sessionid

    private String openId;
    private String phoneNum;
    private Long startTime;
    private Long endTime;
    private Long totalTime;
    private Integer payMoney;
    private String logTime;
    private String bid;
	public Double getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(Double longtitude) {
		this.longtitude = longtitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public Long getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}
	public Integer getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(Integer payMoney) {
		this.payMoney = payMoney;
	}
	public String getLogTime() {
		return logTime;
	}
	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	@Override
	public String toString() {
		return "PayModel [longtitude=" + longtitude + ", latitude=" + latitude + ", uuid=" + uuid + ", openId=" + openId
				+ ", phoneNum=" + phoneNum + ", startTime=" + startTime + ", endTime=" + endTime + ", totalTime="
				+ totalTime + ", payMoney=" + payMoney + ", logTime=" + logTime + ", bid=" + bid + "]";
	}
    
    
}
