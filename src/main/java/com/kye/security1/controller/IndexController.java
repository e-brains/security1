package com.kye.security1.controller;

import com.kye.security1.model.User;
import com.kye.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @GetMapping("/user")
    public @ResponseBody String user(){
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

}
