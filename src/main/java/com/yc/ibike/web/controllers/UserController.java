package com.yc.ibike.web.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yc.ibike.bean.User;
import com.yc.ibike.bean.WeixinResponse;
import com.yc.ibike.service.UserService;
import com.yc.ibike.utils.HttpClientUtil;
import com.yc.ibike.web.models.JsonModel;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	private Logger logger = Logger.getLogger(UserController.class);
	private final String SECRET = "c519a9ce60ffec06b3f74394205beaf7";
	private final String APPID = "wx275aec689933ca52";
	private final String WXSERVER = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APPID + "&secret=" + SECRET + "&grant_type=authorization_code";
	
	
	@PostMapping("/onLogin")
	public @ResponseBody JsonModel onLogin(JsonModel jm, String jscode) {
		String wxurl = WXSERVER + "&js_code=" + jscode;
		logger.info("访问后台微信的code2session:" + wxurl);
		String ret = HttpClientUtil.sendHttpPost(wxurl);
		logger.info("微信返回的结果 " + ret);  //  { session_key:xxx, openid:xxx}
		if (ret == null || "".equals(ret)) {
			jm.setCode(0);
			logger.info("网络超时");
			jm.setMsg("网络超时");
			return jm;
		}
		//   spring mvc自带了  jackson的json解析器. 这个ObjectMapper就是它里面的核心类

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			// 逆序列化 ，将字符串中的有效信息取出  session_key,   openid

			WeixinResponse weixinResponse = objectMapper.readValue(ret, WeixinResponse.class);
			String session_key = weixinResponse.getSession_key();// 如果解密encryptData获取unionId，会用的到
			String openId = weixinResponse.getOpenid();// 微信小程序 用户唯一标识
			// 先查询mongo中这个openId存在不存在，存在不入库，不存在就入库
			List<User> memberList = userService.selectMember(openId);
			User u = null;
			if (memberList != null && memberList.size() > 0) {
				u = memberList.get(0);   // u中有  status
				logger.info("openId:" + openId + "在mongo中已经存在，不需要插入,信息为:" + u);
			} else {
				//如果没有这个用户的openid记录，则说明这次操作是一次注册
				u = new User();
				u.setOpenId(openId); // 新增一个openid属性
				u.setStatus(0);
				userService.addMember(u);
				logger.info("openId:" + openId + "对应的mongo不存在，插入数据库");
			}
			String rsession = userService.redisSessionKey(openId, session_key);
			// (7) 把新的sessionKey返回给小程序
			jm.setCode(1);
			Map<String,String> m=new HashMap<>();
			m.put("uuid",rsession);
			m.put("openid",openId);
			m.put("status",   u.getStatus() +""   );
			m.put("phoneNum", u.getPhoneNum());
			jm.setObj(m);
		} catch (Exception e) {
			e.printStackTrace();
			jm.setCode(0);
			jm.setMsg("微信返回的错误码" + e.getMessage());
			return jm;
		}
		return jm;
	}
	
	
	@PostMapping("/recharge")
	public @ResponseBody JsonModel recharge(JsonModel jm,double balance,String phoneNum) {
		boolean b=false;
		try {
			b=userService.recharge(balance,phoneNum);
			if( b ) {
				jm.setCode(1);
			}else {
				jm.setCode(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			jm.setCode(0);
			jm.setMsg(   e.getMessage() );
		}
		return jm;
	}

	
	@PostMapping("/genCode")
	public @ResponseBody JsonModel genSMSCode(JsonModel jm,String nationCode,String phoneNum){
		String msg="true";
		try {
			//生成4位随机数->调用短信接口发送验证码->将手机号对应的验证码保存到redis里面并限制时间
			userService.genVerifyCode(nationCode, phoneNum);
			jm.setCode(1);
		} catch (Exception e) {
			e.printStackTrace();
			jm.setCode(0);
		}
		return jm;
		
	}
	
	@PostMapping("/verify")
	public @ResponseBody JsonModel genRegister(JsonModel jm,User user){
		boolean flag=false;
		try {
			//从redis中获取数据并进行比较
			flag=userService.verify(user);
			if(flag){
				jm.setCode(1);
			}else{
				jm.setCode(0);
				jm.setMsg("校验码错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
			jm.setCode(0);
			jm.setMsg("错误原因:"+e.getMessage());
		}
		return jm;
	}
	
	@PostMapping("/deposit")
	public @ResponseBody JsonModel deposit(JsonModel jm,User user){
		boolean flag=userService.deposit(user);
		if(flag){
			jm.setCode(1);
		}else{
			jm.setCode(0);
		}
		return jm;
	}
	
	@PostMapping("/identity")
	public @ResponseBody JsonModel identity(JsonModel jm,User user){
		boolean flag=userService.identity(user);
		if(flag){
			jm.setCode(1);
		}else{
			jm.setCode(0);
		}
		return jm;
	}
	
	@PostMapping("/staff_info")
	public @ResponseBody JsonModel findAllStaff(JsonModel jm,User user){
		List<User> list=userService.findAllStaff();
		if(!list.isEmpty()){
			jm.setObj(list);
			jm.setCode(1);
		}else{
			jm.setCode(0);
		}
		return jm;
	}
	
	@PostMapping("/consumer_info")
	public @ResponseBody JsonModel findAllConsumer(JsonModel jm,User user){
		List<User> list=userService.findAllConsumer();
//		Map<String, Object> result = new HashMap<String, Object>();
//		result.put("data", list);
		if(!list.isEmpty()){
			jm.setObj(list);
			jm.setCode(1);
		}else{
			jm.setCode(0);
		}
		return jm;
	}
}
