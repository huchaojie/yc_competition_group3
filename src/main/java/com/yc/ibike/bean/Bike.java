package com.yc.ibike.bean;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.data.annotation.Id;

public class Bike implements Serializable{
	private static final long serialVersionUID = 1L;
	private String bid;
	private String qrcode;
	private Double latitude;
	private Double longtitude;
	private int status;
	
	@Id  // 对应到mongo    _id
	private String id;
	private Double[] loc=new Double[2];
	
	private String phoneNum;
	private String[] types;
	private String openid;
	
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String[] getTypes() {
		return types;
	}
	public void setTypes(String[] types) {
		this.types = types;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public Double[] getLoc() {
		return loc;
	}
	public void setLoc(Double[] loc) {
		this.loc = loc;
	}
	public static final int UNACTIVE=0;  //未启用
	public static final int LOCK=1;      //启用未解锁
	public static final int USING=2;	//开锁使用中
	public static final int INTROUBLE=3;	//报修

	
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(Double longtitude) {
		this.longtitude = longtitude;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Bike [bid=" + bid + ", qrcode=" + qrcode + ", latitude=" + latitude + ", longtitude=" + longtitude
				+ ", status=" + status + ", id=" + id + ", loc=" + Arrays.toString(loc) + ", phoneNum=" + phoneNum
				+ ", types=" + Arrays.toString(types) + ", openid=" + openid + "]";
	}
	
	

}
