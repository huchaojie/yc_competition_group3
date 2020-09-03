package com.yc.ibike.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.velocity.runtime.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import com.yc.ibike.bean.LogBack;
import com.yc.ibike.dao.LogBackDao;

@Repository
public class LogBackDaoImpl implements LogBackDao {
	//操作redis中的v是对象类型的数据
	@Autowired
	private RedisTemplate redisTemplate;

	//操作redis中的字符串数据类型
	@Autowired
	private StringRedisTemplate stringRedisTemp;
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource datasource){
		this.jdbcTemplate=new JdbcTemplate(datasource);
	}
	
	@Override
	public String getPV_UV() {
		return stringRedisTemp.opsForValue().get("accesslog_analysis_total_pv")+","+stringRedisTemp.opsForValue().get("accesslog_analysis_total_uv");
	}

	@Override
	public List<LogBack> getConsumerVisit() {
		String sql="select ipaddr,times from accesslogsum";
		List<LogBack> result=new ArrayList<>();
		this.jdbcTemplate.query(sql, new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				LogBack lb=new LogBack();
				lb.setIpaddr(rs.getString("ipaddr"));
				lb.setTimes(rs.getInt("times"));
				result.add(lb);
				//System.out.println(result.size());
			}
		});
		return result;
//		return  (List<LogBack>) this.jdbcTemplate.queryforqueryForObject(sql, (resultSet,rowNum)->{
//			
//			System.out.println(rowNum);
//			LogBack lb=new LogBack();
//			lb.setIpaddr(resultSet.getString("ipaddr"));
//			lb.setTimes(resultSet.getInt("times"));
//			result.add(lb);
//			System.out.println(result.size());
//			return result;
//		});
		
	}

}
