package com.yc.ibike.config;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;

@Configuration
@ComponentScan(basePackages="com.yc")
@EnableTransactionManagement //查找spring  托管bean
public class AppConfig{
	
	private Logger log=Logger.getLogger(AppConfig.class);
	
	//因为用了  %D， 所以要开启undertow记时:  
	@Bean
	public UndertowServletWebServerFactory undertowServletWebServerFactory() {
		        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
		        factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
		            @Override
		            public void customize(Undertow.Builder builder) {
		                builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true);
		            }
		        });
		        return factory;
		    }
	
	/*common日志格式: 
		   %h %l %u %t \"%r\" %s %b
		   %h: 远程主机名
		   %l:  远程主机逻辑名: 经常是 -
		   %u:   远程受信用户名: 经常是 -
		   %t: 日期和时间，用 Common Log Format格式
		   %r: Http请求的第一行
		   %s: 响应状态码
		   %b: 发送的字节数（不包括头域),如是 - 表明没有字节发送， 

		combined日志格式: 
		   %h %l %u %t \"%r\" %s %b \"%{i,Referer}\" \"%{i,User-Agent}\

		   "%{i,Referer}\":   从http请求头中获取 来源地址
		   %{i,User-Agent}:   从http请求头中获取 userAgent
*/
	
	
	
	@Bean   // 键[字符串]: 值[对象]
	public RedisTemplate redsiTemplate(  JedisConnectionFactory conn   ) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(conn);
        template.afterPropertiesSet();
        return template;
	}
	
	@Bean     // 键[字符串]: 值[字符串]
	public StringRedisTemplate stringRedisTemplate(     JedisConnectionFactory conn      ) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(conn);
        template.afterPropertiesSet();
        return template;
	}

	
	@Bean    //  MongoTemplate由spring 托管
    @Primary
    public MongoTemplate template() {
        return new MongoTemplate(factory());
    }
	
	 /**
     * 功能描述: 创建数据库名称对应的工厂，数据库名称可以通过配置文件导入
     * @param
     * @return:org.springframework.data.mongodb.MongoDbFactory
     * @since: v1.0
     */
    @Bean("mongoDbFactory")
    public MongoDbFactory factory() {
        return new SimpleMongoDbFactory(client(), "mybike");
    }
    
    /**
     * 功能描述: 配置client，client中传入的ip和端口可以通过配置文件读入
     *
     * @param
     * @return:com.mongodb.MongoClient
     */
    @Bean("mongoClient")
    public MongoClient client() {
       // return new MongoClient("192.168.6.200", 27017);  单节点
    	List<ServerAddress> list=new ArrayList<ServerAddress>();
    	ServerAddress sa1=new ServerAddress("192.168.6.200",23000);  //集群
    	ServerAddress sa2=new ServerAddress("192.168.6.201",23000);
    	ServerAddress sa3=new ServerAddress("192.168.6.202",23000);
    	list.add( sa1 );
    	list.add( sa2 );
    	list.add( sa3 );
    	return new MongoClient(   list );
    }

	
	
	@Bean
	public DriverManagerDataSource dataSource(){
		DriverManagerDataSource dataSource=new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://192.168.6.200:3306/ibike?serverTimezone=GMT");  
		// "serverTimezone=GMT" The server time zone value ‘�й���׼ʱ��’ is 
		//unrecognized or represents more than one time zone.
		//You must configure either the server or JDBC driver 
		//(via the serverTimezone configuration property) to use a more specifc time zone value if you want to utilize time zone support.		
		dataSource.setUsername("root");
		dataSource.setPassword("a");
		log.info("创建数据源"+dataSource);
		return dataSource;
	}
	
	@Bean
	public DataSourceTransactionManager tx(DriverManagerDataSource ds){
		log.info("创建事务管理器"+ds);
		DataSourceTransactionManager dtm=new DataSourceTransactionManager();
		dtm.setDataSource(ds);
		return dtm;
	}
}
