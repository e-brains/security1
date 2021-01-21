package com.kye.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.kye.security1.model.User;

import lombok.Data;

//시큐리티가 /login주소 요청이 오면 가로채서 로그인을 진행 시킨다.
//로그인 진행이 완료되면 시큐리티 세션을 만들어 준다. (Security ContextHolder)
//세션 정보에는 오브젝트 타입인 Authentication타입의 객체가 들어간다.
//Security Session은 Authentication객체이어야 한다 그리고 Authentication안에는 
//User정보가 있어야 하는데 타입이 UserDetails 타입객체 이어야 한다.
//UserDetails를 구현한 PrincipalDetails는 UserDetails타입과 같기 때문에 Authentication에 넣을 수 있다.
//Security Session <= Authentication <= PrincipalDetails (=UserDetails) 
//@어노테이션을 안거는 이유는 나중에 강제로 new를 이용하여 사용할 예정
@Data
public class PrincipalDetails implements UserDetails, OAuth2User { //구글 로그인 처리를 위해 OAuth2User 추가

	private User user; //콤포지션
	private Map<String, Object> attributes;
	
	//일반 로그인 시 사용
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	//OAuth로그인 시 사용 (동시에  user와 attributes를 모두 들고 있다)
	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	
	//해당 유저의 권한을 리턴하는 곳
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

	//계정 만료 여부
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	//계정 잠김 여부
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	//비밀번호 만료 여부
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	//계정이 활성화 여부
	@Override
	public boolean isEnabled() {
		// 향후에 1년동안 회원이 로그인을 안하면 휴먼계정으로 하기로 했다면
		// 여기서 현재시간-로그인시간 => 1년을 초과하면 return false로 하면됨
		return true;
	}

	//OAuth2User가 추가 구현 되면서 아래 두개 메서드를 override해야 됨
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return null;
	}

}
