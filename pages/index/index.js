//index.js
//获取应用实例
const app = getApp()
var QQMapWX=require('../../libs/qqmap-wx-jssdk.min.js')
var qqmapsdk

Page({

  /**
   * 页面的初始数据
   */
  data: {
    latitude:0,
    longitude:0,
    controls:[],
    markers:[]
  },

  
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log("生命周期:->onload")
    //实例化API核心类
    qqmapsdk=new QQMapWX({
      key:'W6TBZ-X5U3D-TWZ4J-HCNAJ-3OY7T-DSBLD'
    })

    //1.获取当前对象的拷贝
    var that=this
    //2.创建一个地图的上下，对地图中的控件进行事件操作
    that.mapCtx=wx.createMapContext('map')
    //3.获取当前收集所在的位置(真机是手机真实位置，
    //模拟器则是在sensor面板中设置的位置)
    wx.getLocation({
      type:"wgs84",
      isHighAccuracy:true,
      success:function(res){
        console.log("wx.getlocation  " +res);
        that.setData({
          latitude:res.latitude,
          longitude:res.longitude,
        });
        
     //加载所有可用的附近的前10台车
        that.findNearAll(that.data.latitude,that.data.longitude)

      }
    });
     
    //4.在地图页中加入按钮
    //获取当前设备信息，窗口的宽高
    wx.getSystemInfo({
      success:function(res){
        //获取宽高
        var height=res.windowHeight;
        var width=res.windowWidth;
        //添加控件(图片)
        that.setData({
          controls:[
            {
              id:1,
              position:{
                left:width/2-10,
                top:height/2-20,
                width:20,
                height:35
               },
              iconPath:"../images/location.png",
              clickable:true
            },
            {
              id:2,
              position:{
                left:20,
                top:height-60,
                width:40,
                height:40
               },
              iconPath:"../images/img1.png",
              clickable:true
            },
            {
              id:3,
              position:{
                left:130,
                top:height-60,
                width:100,
                height:40
               },
              iconPath:"../images/qrcode.png",
              clickable:true
            },
            {
              id:4,
              position:{
                left:width-45,
                top:height-60,
                width:40,
                height:40
               },
              iconPath:"../images/pay.png",
              clickable:true
            },
            { //报修
              id: 6,
              iconPath: "../images/repair.png",
              position: {
                width: 35,
                height: 35,
                left: width - 42,
                top: height - 203
              },
              //是否可点击
              clickable:true
            } 
          ]
        })
      }
    });

     var phoneNum=wx.getStorageSync('phoneNum')
      if(phoneNum==null || phoneNum==''){
        wx.setStorageSync('status', 0)
        }
  
    },

  findNearAll(latitude,longitude){
    var that=this
    //console.log("findNearAll")
    //加载所有可用的附近的前10台车
    wx.request({
      url: wx.getStorageSync('url')+'/findNearAll',//"http://localhost:8080/bycicle/findNearAll"
      method:"POST",
      data:{
       latitude:latitude,
       longtitude:longitude,
       status:1
      },
      success:function(res){
        const bikes=res.data.obj.map(item=>{
          return {
            //id:item.bid,
            bid:item.bid,
            //title:item.bid+"",
            iconPath:"../images/bike.png",
            width:35,
            height:35,
            latitude:item.latitude,
            longitude:item.longtitude
            //status:item.status
          }
        });
        console.log(bikes);
        that.setData({
          markers:bikes
        });
      }
    });
  },

  controltap(e){
    var that=this;
    if(e.controlId==2){
      //复位
      that.mapCtx.moveToLocation();
    }else if(e.controlId==3){
      //获取全局变量status
      //当点扫码开锁时，先判断用户状态
      var status=wx.getStorageSync('status')
      console.log(status)
       if( status==0){
        //跳到注册页面
        wx.navigateTo({
          url: '../register/register',
        });
      }else if (status == 1) {
        wx.navigateTo({
          url: '../deposit/deposit',
        });
      } else if (status == 2) {
        wx.navigateTo({
          url: '../identity/identity',
        });
      } else if (status == 3) {
        that.scanCode()  //需要用that才能调用到
      }else if(status==4){
        wx.navigateTo({
          url:'../billing/billing'
        })
      }
    }else if(e.controlId==4){
      wx.navigateTo({
        url: '../pay/pay',
      });
    }else if(e.controlId==6){
      //TODO: 以后加入必须登录用户才能报修
      // var phoneNum=getApp().globalData.phoneNum;
      // if(  phoneNum==null|| phoneNum.equals("")){
      //   wx.showToast({
      //     title: '没有登录，不能报修',
      //   });
      //   return;
      // }
      wx.navigateTo({
        url: '../repair/repair',
      })
    }
  },
    //重构扫码代码
  scanCode:function(){ //scanCode是当前页面的一个成员方法
    var that=this
    //扫码
     wx.scanCode({
      success: (res) => {
        //编号
        var bid=res.result;
        //异步请求
        wx.request({
          url: wx.getStorageSync('url')+'/open',
          method:"POST",
          // data:"bid="+bid+"&latitude="+that.data.latitude
          // +"&longtitude="+that.data.longitude,
          data:{
            bid:bid,
            latitude:that.data.latitude,
            longtitude:that.data.longitude
          },
          dataType:"json",
          header:{
            "content-type":"application/json"
          },
          success:function(res){
            console.log(res)
            if(res.data.code==0){
              wx.showModal({
                title: '开锁失败',
                content:res.data.msg
              })
            }
            //TODO: 计费,计时
            console.log( res.data );
            if( res.data.code==1){
              addStartRideLog(wx.getStorageSync('phoneNum'),wx.getStorageSync('openid'),bid)
              //在本地存一下正在骑行的单车号
              wx.setStorageSync('bid', bid);
              wx.setStorageSync('status', 4);   //表示当前用户正在骑行中...
              getApp().globalData.status = 4;  //当前骑行中.
              wx.navigateTo({
                url: '../billing/billing?bid='+bid                });
            }
          }
        });
      },
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    console.log("生命周期:->onReady")
    var that=this;
    //发出请求到后台  记录日志
    //数据埋点：用于获取用户openid，当前地理位置,发出请求到后台
    //并保存到mongo的logs表中，用于后期日志分析
    //获取位置
    wx.getLocation({
      success:function(res){
        //获取经纬度
        var latitude=res.latitude;
        var longitude=res.longitude;
        //获取用户标识
        var openid=wx.getStorageSync('openid')
        wx.request({
          url: wx.getStorageSync('url')+'/log/savelog',
          data:{
            time:new Date(),   //客户端时间
            openid:openid,
            latitude:latitude,
            longitude:longitude,
            url:'index'
          },
          method:"POST"
        })
      }
    })
  },

  regionchange(e){
    var that=this
    //console.log(e+"调用了") 
    if(e.type=='end'){
      that.mapCtx.getCenterLocation({
        type: 'gcj02', //返回可以用于wx.openLocation的经纬度
        success (res) {
          //console.log(res.latitude+" "+res.longitude)
          that.findNearAll(res.latitude,res.longitude)
        }
      })
    }
    }
})
function addStartRideLog(phoneNum,openid,bid){
  //结束骑行数据埋点
  wx.getLocation({
    success:function(res){
      var lat=res.latitude
      var lon=res.longitude
      console.log(lat+" "+lon)
      //埋点  记录用户充值的行为信息  ，以后做数据分析
      //请求腾讯地图api查找省市区
      qqmapsdk.reverseGeocoder({
        location:{
          latitude:lat,
          longitude:lon
        },
        success:function(res){
          console.log("腾讯地图的结果:"+res)
          var address=res.result.address_component
          var province=address.province
          var city=address.city
          var district=address.district
          var street=address.street
          var street_number=address.street_number
          console.log("充值日志:"+province+","+city+","+district+","
          +province+","+city+","+street)
          var dt=new Date()
          var startTime=Date.parse(dt);
          wx.setStorageSync('start_lat', lat)
          wx.setStorageSync('start_lon', lon)
          wx.setStorageSync('start_province', province)
          wx.setStorageSync('start_city', city)
          wx.setStorageSync('start_district', district)
          wx.setStorageSync('start_street', street)
          wx.setStorageSync('start_street_number', street_number)
          wx.setStorageSync('start_startTime', startTime)
        }
      })
    }
  })
}