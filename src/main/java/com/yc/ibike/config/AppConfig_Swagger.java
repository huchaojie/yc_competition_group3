package com.yc.ibike.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@Configuration
@EnableSwagger2 //启用swagger  注解解析器
public class AppConfig_Swagger {
	//localhost:8080/bycicle/swagger-ui.html
	//是否开启swagger,正式环境一般是需要关闭的，课根据Springboot的多环环境配置进行设置
	@Value(value="${swagger.enabled}") //通过@Value 获取配置信息 复习@Environment @Value @ConfigurationProperties
	Boolean swaggerEnabled;
	@Bean
	public Docket createRestApi(){
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				//是否开启
				.enable(swaggerEnabled).select()
				//扫描的路径包 只要这些包中的类配有swagger注解  则启用这些注解
				.apis(RequestHandlerSelectors.basePackage("com.yc"))
				//指定路径处理 PathSelectors.any() 代表所有的路径
				.paths(PathSelectors.any()).build();
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("共享单车操作接口")
				.description("springboot | swagger")
				//作者信息
				.contact(new Contact("zyy","http://www.zyy.com","358343"))
				.version("1.0.0")
				.build();
	}

}
