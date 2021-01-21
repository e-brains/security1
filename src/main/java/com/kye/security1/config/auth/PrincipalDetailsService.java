package com.kye.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kye.security1.model.User;
import com.kye.security1.repository.UserRepository;

//시큐리티 설정에서 loginProcessingUrl("/login")
//login요청이 오면 자동으로 UserDetailsService타입으로 DI되어 있는 loadUserByUsername함수가 실행 (규칙임)
@Service
public class PrincipalDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	//클라이언트에서 반드시 여기 username과 동일 변수명으로 사용해야 됨
	//리턴될 때 Authentication에 PrincipalDetails이 들어가고 Authentication은 세션에 들어간다. => 로그인 완료 
	//함수 종료 시 @AuthenticationPrincipal어노테이션이 만들어진다.
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userEntitiy = userRepository.findByUsername(username);
		if (userEntitiy != null) {
			return new PrincipalDetails(userEntitiy); 
		}
		return null;
	}
}
