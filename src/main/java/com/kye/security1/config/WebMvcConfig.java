package com.kye.security1.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	// 여기서 리졸버으 prefix와 suffix를 내가 원하는 대로 재설정 해준다. 
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		MustacheViewResolver reslover = new MustacheViewResolver();
		reslover.setCharset("UTF-8");
		reslover.setContentType("text/html; charset=UTF-8");
		reslover.setPrefix("classpath:/templates/");
		reslover.setSuffix(".html"); //mustache가 html을 인식한다.
		
		registry.viewResolver(reslover);
	}
	
}
