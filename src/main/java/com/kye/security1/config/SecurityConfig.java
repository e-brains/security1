package com.kye.security1.config;

import com.kye.security1.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration      // IoC 빈(bean)을 등록
@EnableWebSecurity  // 필터 체인 관리 시작 어노테이션 : 스프링 필터 체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
// securedEnabled  => Controller 에서 @Secured 어노테이션을 이용하여 접근제어를 할 수 있다.
// prePostEnabled  => Controller 에서 @PreAuthorize , @PostAuthorize 어노테이션을 이용하여 접근제어를 할 수 있다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean  // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해 준다.
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated() // 인증만 되면 접근 가능
                // admin 과 manager 권한을 가진 사람만 입장 가능
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                // admin 권한을 가진 사람만 입장 가능
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll() // 이외의 주소에는 모두 접근 가능
            .and()
                .formLogin()
                .loginPage("/loginForm") // 권한이 필요한 페이지에 로그인 없이 접근 시 내가 만든 login 페이지로 이동
                .loginProcessingUrl("/login") // /login 주소가 호출이 되면 시큐리티가 가로채서 대신 로그인을 진행한다.
                .defaultSuccessUrl("/") // 성공하면 루트로 이동
            .and()
                .oauth2Login()
                .loginPage("/loginForm")  // 구글로그인이 왼료된 뒤의 후처리가 필요함
                .userInfoEndpoint()
                .userService(principalOauth2UserService); // 후처리 : 엑세스 토큰과 사용자 프로필 정보를 한번에 받는다.


    }
}
