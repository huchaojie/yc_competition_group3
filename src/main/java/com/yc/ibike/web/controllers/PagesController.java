package com.yc.ibike.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yc.ibike.bean.PayModel;
import com.yc.ibike.bean.User;
import com.yc.ibike.web.models.JsonModel;

@Controller
public class PagesController {

	@PostMapping("/user_info")
	public @ResponseBody JsonModel payMoney(JsonModel jm,PayModel pm){
		try {
			User user=new User();
			user.setName("张三");
			user.setPhoneNum("123456789");
			jm.setObj(user);
		} catch (Exception e) {
			e.printStackTrace();
			jm.setCode(0);
			jm.setMsg(e.getMessage());
		}
		return jm;
	}
}
