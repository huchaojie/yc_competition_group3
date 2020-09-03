package com.yc.ibike.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yc.ibike.bean.PayModel;
import com.yc.ibike.service.LogService;
import com.yc.ibike.web.models.JsonModel;

@Controller
public class LogController {
	@Autowired
	private LogService logService;
	
	@PostMapping("/log/savelog")
	@ResponseBody
	public JsonModel ready(JsonModel jsonModel,@RequestBody String log){
		logService.save(log);
		jsonModel.setCode(1);
		return jsonModel;
	}
	
	@PostMapping("/log/savePayLog")
	@ResponseBody
	public JsonModel savepaylog(JsonModel jsonModel,@RequestBody String log){
		logService.savePayLog(log);
		jsonModel.setCode(1);
		return jsonModel;
	}
	
	
	@PostMapping("/log/saveEndRideLog")
	@ResponseBody
	public JsonModel saveEndRideLog(JsonModel jsonModel,@RequestBody String log){
		logService.saveEndRideLog(log);
		jsonModel.setCode(1);
		return jsonModel;
	}
	
	
	@PostMapping("/log/saveStartRideLog")
	@ResponseBody
	public JsonModel saveStartRideLog(JsonModel jsonModel,@RequestBody String log){
		logService.saveStartRideLog(log);
		jsonModel.setCode(1);
		return jsonModel;
	}
	
	@PostMapping("/log/saveRepairLog")
	@ResponseBody
	public JsonModel saveRepairLog(JsonModel jsonModel,@RequestBody String log){
		logService.saveRepairLog(log);
		jsonModel.setCode(1);
		return jsonModel;
	}
	
	
	
}
