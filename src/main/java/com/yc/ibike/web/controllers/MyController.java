package com.yc.ibike.web.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yc.ibike.bean.Bike;
import com.yc.ibike.bean.HtmlModel;
import com.yc.ibike.service.BikeService;
import com.yc.ibike.web.models.JsonModel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;


@Controller
@Api(value="大阳出行单车信息业务",tags={"单车信息","控制层"})
public class MyController {
	private Logger logger=LogManager.getLogger();
	
	@Autowired
	private BikeService bikeService;
	
	@RequestMapping(value="/findAllBike",method={RequestMethod.POST})
	@ApiOperation(value="查找所有的单车",notes="查找所有的单车")
	public @ResponseBody JsonModel findAllBike(@ApiIgnore JsonModel jm,Bike bike){
		List<Bike> list=bikeService.findAllBike();
		if(!list.isEmpty()){
			jm.setObj(list);
			jm.setCode(1);
		}else{
			jm.setCode(0);
		}
		return jm;
	}
	
	@RequestMapping(value="/findNearAll",method={RequestMethod.POST})
	@ApiOperation(value="查找最近的单车",notes="查找附近的10部单车")
	public @ResponseBody JsonModel findNearAll(@ApiIgnore JsonModel jm,@RequestBody Bike bike){
		List<Bike> list=bikeService.findNearAll(bike);
		jm.setCode(1);
		jm.setObj(list);
		return jm;
	}
	/*
	 * @ApiIgnore swagger页面不显示对象里面的值 并且无法接收参数
	 * */
	@ApiOperation(value="用户端开锁操作",notes="给指定的共享单车开锁，参数以json格式")
//	@ApiImplicitParams({@ApiImplicitParam(name="bid",value="1",required=true),
//						@ApiImplicitParam(name="latitude",value="20.1",required=true),
//						@ApiImplicitParam(name="longtitude",value="30.2",required=true),})
	@RequestMapping(value="/open",method=RequestMethod.POST)   //RequestMethod.GET
	public @ResponseBody JsonModel open(@ApiIgnore JsonModel jsonModel,@RequestBody Bike bike){  //加@RequestBody  只使用RequestMethod.POST  post成功
		logger.info("请求参数:"+bike);
		try {
			bikeService.open(bike);
			jsonModel.setCode(1);
		} catch (Exception e) {
			e.printStackTrace();
			jsonModel.setCode(0);
			jsonModel.setMsg(e.getMessage());
		}
		return jsonModel;
	}
	
	
		//报修
		@PostMapping("/repair")
		public @ResponseBody JsonModel repair(   JsonModel jm, Bike bike) {
			try {
				this.bikeService.reportMantinant(  bike );
				System.out.println("成功");
				jm.setCode(1);
			} catch (Exception e) {
				e.printStackTrace();
				jm.setCode(0);
				jm.setMsg(  e.getMessage() );
			}
			return jm;
		}
		
		//添加单车
			@PostMapping("/addBike")
			public @ResponseBody JsonModel addBike(   JsonModel jm, Bike bike) {
				try {
					bikeService.addNewBike(bike);
					System.out.println("add bike success"+bike.getLatitude());
					jm.setCode(1);
				} catch (Exception e) {
					e.printStackTrace();
					jm.setCode(0);
					jm.setMsg(  e.getMessage() );
				}
				return jm;
			}
			
			//删除单车
			@PostMapping("/delBike")
			public @ResponseBody JsonModel delBike(JsonModel jm, Bike bike) {
				try {
					System.out.println("The del bike bid:"+bike.getBid());
					jm.setCode(bikeService.delBike(bike.getBid()));
				} catch (Exception e) {
					e.printStackTrace();
					jm.setCode(0);
					jm.setMsg(  e.getMessage() );
				}
				return jm;
			}
			
			//删除单车
			@PostMapping("/delChekedBike")
			public @ResponseBody JsonModel delChekedBike(JsonModel jm, HtmlModel hm) {
				try {
					//System.out.println("The del bikes bid:"+bike.getBid());
					System.out.println("mycontroller");
					System.out.println(hm.getCheckValue());
					jm.setCode(bikeService.delChekedBike(hm.getCheckValue()));
				} catch (Exception e) {
					e.printStackTrace();
					jm.setCode(0);
					jm.setMsg(  e.getMessage() );
				}
				return jm;
			}

}
