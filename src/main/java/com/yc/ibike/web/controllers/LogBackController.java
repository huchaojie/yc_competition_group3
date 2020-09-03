package com.yc.ibike.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yc.ibike.service.LogBackService;
import com.yc.ibike.web.models.JsonModel;

@Controller
public class LogBackController {
	@Autowired
	public LogBackService lbs;
	
	@PostMapping("/getpv_uv")
	@ResponseBody
	public JsonModel ready(JsonModel jsonModel){
		jsonModel.setObj(lbs.getPV_UV());
		jsonModel.setCode(1);
		return jsonModel;
	}
	@PostMapping("/getConsumerVisit")
	@ResponseBody
	public JsonModel getConsumerVisit(JsonModel jsonModel){
		jsonModel.setObj(lbs.getConsumerVisit());
		jsonModel.setCode(1);
		return jsonModel;
	}
}
