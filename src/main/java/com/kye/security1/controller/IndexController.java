package com.kye.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kye.security1.config.auth.PrincipalDetails;
import com.kye.security1.model.User;
import com.kye.security1.repository.UserRepository;

@Controller //view 리턴
public class IndexController {

	@Autowired
	private UserRepository userRepository; 
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//일반 로그인 (구글 로그인 처리 안됨) 
	@GetMapping("/test/login")
	public @ResponseBody String loginTest(Authentication authentication, //의존성 주입 DI
			//@AuthenticationPrincipal UserDetails userDetails) (UserDetails타입을 구현한 PrincipalDetails로 바꿀 수 있다.)
			@AuthenticationPrincipal PrincipalDetails principal2Details) {
		//세션에서 getUser를 가져오는 두가지 방법
		//1. authentication에서 principal을 찾아 다운 캐스팅 하는 방법
		//2. @AuthenticationPrincipal 어노테이션을 이용해서 가져오는 방법
		System.out.println("/test/login =========start=======");
		//authentication.getPrincipal()은 리턴 타입이 Object이다 
		//그래서 우리가 필요로 하는 PrincipalDetails타입 캐스팅이 필요 (원래는 UserDetails로 받지만 구현체로 받음)
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("/authentication  getUser========" +  principalDetails.getUser());
		//@AuthenticationPrincipal 어노테이션을 이용해서 getUser를 찾을 수 도 있다.
		System.out.println("principal2Details getUsername ======== "+ principal2Details.getUser());
		return "세션정보 확인하기";
	}
	
	//구글 로그인 정상 처리 (일반 로그인 처리 안됨)
	@GetMapping("/test/oauth/login")
	public @ResponseBody String loginOAuthTest(Authentication authentication ,
			@AuthenticationPrincipal OAuth2User oauth2User2) { //의존성 주입 DI
		//세션에서 getAttributes(구글로그인 정보)를 가져오는 두가지 방법
		//1. authentication에서 principal을 찾아 다운 캐스팅 하는 방법
		//2. @AuthenticationPrincipal 어노테이션을 이용해서 가져오는 방법
		System.out.println("/test/oauth/login =========start=======");
		//authentication.getPrincipal()은 리턴 타입이 Object이다 
		//구글 로그인을 위해  OAuth2User 타입 캐스팅이 필요
		OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("oauth2User.getAttributes ========" +  oauth2User.getAttributes());

		System.out.println("oauth2User2.getAttributes ========" +  oauth2User2.getAttributes());
		return "OAuth 세션정보 확인하기";
	}
	
	//홈 화면
	@GetMapping({"", "/"})
	public String index() {
		// UI는 머스테치를 사용 : 기본폴더 /src/main/resources/
		// 뷰리졸버 설정 : templates을 prefix로 잡고 .mustache를 suffix로 잡는다 (application.yml에 설정)
		// 그러나 pom.xml에 mustache 의존성 설정을 했으면 yml에 기술하지 않아도 됨 
		return "index";  // src/main/resources/templates/
	}
	
	//PrincipalOauth2UserService에서 PrincipalDetails가 일반적인 로그인은 User만  
	//oauth로그인 경우 user와 attributes를 같이 들고 있도록 세션을 생성함
	//위에서 처럼 일반 로그인 (구글 로그인 처리 안됨) 과 구글 로그인 정상 처리 (일반 로그인 처리 안됨) 처럼 분기할 필요가 없다
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		//@AuthenticationPrincipal은 
		System.out.println("principalDetails ==== " + principalDetails.getUser());
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}	
	
	//login 화면
	//스프링이 가로채기를 하기 때문에 별도 작업이 필요함
	//SecurityConfig파일을 생성해서 권한처리하면 가로채기가 해제됨
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	//회원가입 화면
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}	
	
	//회원가입 수행
	@PostMapping("/join") 
	public String join(User user) {
		
		user.setRole("ROLE_USER");
		
		// 패스워드가 암호화 되지 않으면 시큐리티로 로그인을 할 수 없다. (암호처리)
		String rawPassword = user.getPassword();
		String encPassword =  bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		
		userRepository.save(user);  
		
		return "redirect:/loginForm";   //브라우저에서 바로 loginForm()을 호출한다.
	}

	@Secured("ROLE_ADMIN")  // 특정 메서드에 권한을 거는 경우 사용
	@GetMapping(value = "/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")  // 여러개 권한을 걸고 싶을 때
	@GetMapping(value = "/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
	
}
