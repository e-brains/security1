package com.kye.security1.auth;


import com.kye.security1.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// 시큐리티가 /login 주소 요청이 오면 스프링 시큐리티가 가로채서 로그인을 한다.
// 로그인 진행이 완료되면 시큐리티 session 인 Security ContextHolder 를 만들게 된다.
// 이 세션에 들어가는 오브젝트는 정해져 있는데 Authentication 타입의 객체이다.
// Authentication 안에는 User 정보가 들어있다.
// User 오브젝트의 타입은 UserDetails 타입이어야 한다.
// Security Session => Authentication => UserDetails 또는 OAuth2User
// 구글 로그인에 사용하기 위해 OAuth2User를 추가한다.
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user; // 컴포지션
    private Map<String, Object> attributes;  // 구글 로그인 시 sub(key) 데이터를 받는다.

    // 일반로그인 시 사용
    // UserDetails를 받기 위한 생성자로 user 객체를 넘겨 받아서 필드에 할당한다.
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // 구글 로그인 시 사용
    // OAuth2User을 받기 위한 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes){
        this.user = user;
        this.attributes = attributes;
    }

    // 해당 User의 권한을 리턴
    // 리턴 타입이 GrantedAuthority 타입으로 한정되어 있어서 추가 작업이 필요
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 계정이 만료되지 않았나?
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠기지 않았나?
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 패스워드가 만료되지 않았나?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화 되어 있나?
    @Override
    public boolean isEnabled() {
        // 1년 동안 회원이 로그인을 안하면 휴면 계정으로 하기로 정책을 정했다면
        // User객체에 loginDate 필드를 추가하고 여기서 user.getLoginDate() 형태로 가져다
        // 현재시간 - 로긴시간 > 1년을 초과하면 false로 셋팅하면 됨

        return true;
    }

    // OAuth2User로 인해 추가된 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return (String) attributes.get("sub");  //  sub는 구글의 primary key 이다.
    }
}
