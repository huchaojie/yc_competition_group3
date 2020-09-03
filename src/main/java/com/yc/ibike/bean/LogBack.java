package com.yc.ibike.bean;

import java.io.Serializable;

public class LogBack implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5138995443954206347L;
	
	
	public String ipaddr;
	public int times;
	
	public String getIpaddr() {
		return ipaddr;
	}
	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	@Override
	public String toString() {
		return "LogBack [ipaddr=" + ipaddr + ", times=" + times + "]";
	}
	
	
	
}
