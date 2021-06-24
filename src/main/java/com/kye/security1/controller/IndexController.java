package com.kye.security1.controller;

import com.kye.security1.auth.PrincipalDetails;
import com.kye.security1.model.User;
import com.kye.security1.repository.UserRepository;
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

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @GetMapping({"", "/"})
    public String index(){
        return "index";
    }

    // 일반 로그인과 구글 로그인을 통합해서 User 객체를 찾을 수 있는 PrincipalDetails 적용
    // 따로 분기할 필요가 없다.
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("indexController ==> principalDetails ==> "+principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }

    // 스프링 시큐리티가 해당 주소를 낚아 채는데 SecurityConfig 파일을 생성하면 작동 안함
    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    // 회원 가입 화면으로 이동
    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    // 회원 가입 수행 ( 회원 정보 저장 )
    @PostMapping("/join")
    public String join(User user){

        // role 셋팅
        user.setRole("ROLE_USER");

        // 패스워드 암호화
        String rwPwd = user.getPassword();
        String encPwd = bCryptPasswordEncoder.encode(rwPwd);
        user.setPassword(encPwd);

        userRepository.save(user);
        return "redirect:/loginForm"; // redirect 는 indexController 에 있는 "/loginForm"을 호출해 준다.
    }

    // SecurityConfig에서 @EnableGlobalMethodSecurity 어노테이션을 활성화하면 사용 가능
    // 하나의 권한을 걸고 싶을 때
    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

    // SecurityConfig에서 @EnableGlobalMethodSecurity 어노테이션을 활성화하면 사용 가능
    // 여러개의 권한을 걸고 싶을 때
    // 본 메서드가 실행 되기 전에 수행
    // @PostAuthorize 는 메서드가 수행된 이후 수행
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터 정보";
    }


    /*********************************************
     * 일반 로그인 했을 때 User 객체를 찾는 두 가지 방법
     * 스프링 시큐리티 세션 내부의 Autnentication에 UserDetails 객체를 넣어준다.
     ***********************************************/
    // 1. Authentication 클래스를 이용하는 방법
    @GetMapping("/test1/login")
    public @ResponseBody String oneAuthentication(Authentication authentication){

        // authentication.getPrincipal()의 리턴 타입이 object이므로
        // PrincipalDetails 나 UserDetails로 다운 캐스팅 가능
        // 그런데 User 객체는 PrincipalDetails에서 찾을 수 있을므로 이넘을 사용한다.
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        // principalDetails.getUser()를 통해 User 객체를 찾을 수 있다.
        System.out.println("principalDetails.getUser() =====> "+principalDetails.getUser());

        return "Authentication 클래스를 이용해서 User 객체 확인하기";
    }

    // 2. @AuthenticationPrincipal 어노테이션을 이용하는 방법
    @GetMapping("/test2/login")
    public @ResponseBody String twoPrincipal(@AuthenticationPrincipal PrincipalDetails principalDetails){

        // @AuthenticationPrincipal 을 이용하여 세션에 있는 UserDetails에 접근하면 되는데 여기서는
        // User 객체를 바로 가져올 수 없다. 그래서 우리가 UserDetails를 상속해서 만든 PrincipalDetails로
        // 가져올 수 있다.
        System.out.println("principalDetails.getUser() =====> "+ principalDetails.getUser());

        return "PrincipalDetails 클래스를 이용해서 User 객체 확인하기";
    }

    /*********************************************
     * 구글 로그인 했을 때 User 객체를 찾는 두 가지 방법
     * PrincipalOauth2UserService => loadUser(userRequest) 가 호출되면
     * 스프링 시큐리티 세션 내부의 Autnentication에 OAuth2User 객체를 넣어준다.
     * *********************************************/
    // 1. Authentication 클래스를 이용하는 방법
    @GetMapping("/test3/login")
    public @ResponseBody String oneOauthLogin(Authentication authentication){

        // authentication.getPrincipal()의 리턴 타입이 object이므로 OAuth2User로 다운 캐스팅 가능
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // oAuth2User.getAttributes()로 구글로 부터 얻은 사용자 정보를 가져올 수 있다.
        System.out.println("oneOauthLogin: oAuth2User.getAttributes() =====> "+ oAuth2User.getAttributes());

        return "oAuth2User.getAttributes()를 이용해서 User 정보 확인하기";
    }

    // 2. @AuthenticationPrincipal 어노테이션을 이용하는 방법
    @GetMapping("/test4/login")
    public @ResponseBody String twoOauthLogin(@AuthenticationPrincipal OAuth2User oAuth2User){

        // @AuthenticationPrincipal 을 이용하여 세션에 있는 UserDetails에 접근하면 되는데 여기서는
        // User 객체를 바로 가져올 수 없다. 그래서 우리가 UserDetails를 상속해서 만든 PrincipalDetails로
        // 가져올 수 있다.
        System.out.println("twoOauthLogin : oAuth2User.getAttributes() =====> "+ oAuth2User.getAttributes());

        return "PrincipalDetails 클래스를 이용해서 User 객체 확인하기";
    }
}
