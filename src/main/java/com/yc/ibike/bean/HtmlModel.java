package com.yc.ibike.bean;

import java.io.Serializable;
import java.util.Arrays;

public class HtmlModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8415337495655129507L;
	
	private String checkValue;

	public String getCheckValue() {
		return checkValue;
	}

	public void setCheckValue(String checkValue) {
		this.checkValue = checkValue;
	}

	@Override
	public String toString() {
		return "HtmlModel [checkValue=" + checkValue + "]";
	}
	
	
}
