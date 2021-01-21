package com.kye.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.kye.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity //스프링 시큐리티 필터(내가 등록할 SecurityConfig)가 스프링 필터 체인에 등록이 됨
//@securedEnabled => Secured어노테이션이 있는 컨트롤러의 메서드에 권한 활성화
//@prePostEnabled => PreAuthodrize 어노테이션이 있는 컨트롤러의 메서드에 권한 활성화
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) 
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	@Bean //해당 메서드의 리턴되는 오브젝트를 빈으로 등록해 준다.
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder() ;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
		.antMatchers("/user/**").authenticated()  //인증만 있으면 user로 갈 수 있다.
		.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")  //manager로 오면 요기서 정의된 권한이 있어야 한다
		.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")  //admin으로 오면 요기서 정의된 권한이 있어야 함
		.anyRequest().permitAll()  //나머지 주소는 권한 없이 수행됨
		.and()
		.formLogin()
		.loginPage("/loginForm") // 권한이 필요한 페이지에 대해 로그인 페이지로 이동 시킨다.
		.loginProcessingUrl("/login") // 클라이언트 UI의 form action에서 '/login'주소가 호출이 되면 시큐리티가 가로채서 대신 로그인을 진행해 준다. (컨트롤러에 로그인을 안만들어도 됨)
		.defaultSuccessUrl("/") // 로그인 후에는 메인 홈으로 보낸다.
		.and()
		.oauth2Login()
		.loginPage("/loginForm")
		//구글로그인 인증된 뒤 후처리 필요 1. 코드받기(인증) 2.엑세스 토큰(권한받음) 3. 사용자프로필 정보를 가져옴 
		//4-1. 이 정보가 충분하다면 이를 토대로 회원가입을 자동으로 진행시키기도 함 
		//4-2. 이 정보에 내가 필요한 정보가 모자라는 경우 추가적인 회원가입 창이 나와서 정보를 더 받아야 함
		//Tip 구글 로그인이 완료되면 1,2,3번을 순차적으로 진행하는 것이 아니고 oauth2를 사용하면 엑세스토큰 + 사용자 프로필 정보를 한방에 받는다.
		//후처리가 필요한 경우 PrincipalOauth2UserService에서 후처리 기술
		.userInfoEndpoint()
		.userService(principalOauth2UserService);  //config/oauth/PrincipalOauth2UserService.java를 만들어서 여기서 Autowired를 통해 사용
		
	}
}
