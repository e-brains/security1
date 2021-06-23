package com.kye.security1;

import com.kye.security1.auth.PrincipalDetails;
import com.kye.security1.model.User;
import com.kye.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// SecurityConfig 설정에서 .loginProcessingUrl("/login")로 걸어 놨기 때문에
// "/login" 주소로 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어있는
// loadUserByUsername 함수가 실행된다.
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username은 클라이언트에서 동일한 id로 만들어져 있어야 함
        // 만약 클라이언트에서 id를 username2로 했다면 SecurityConfig에서
        // .usernameParameter("username2")를 추가해야 한다.

        // username이 실제 DB에 존재하는 지 먼저 체크한다.
        // username을 검색하는 메서드는 기본으로 제공되지 않기 때문에 repository로 가서 만들어야 함
        User userEntity = userRepository.findByUsername(username);

        // user 가 있으면
        // principalDetails 를 생성해서 리턴하면 Authentication에 들어감 (시큐리티 세션 생성)
        // 시큐리티 세션 > Authentication > UserDetails (PrincipalDetails)
        if (userEntity != null){
            return new PrincipalDetails(userEntity);
        }

        return null;
    }
}
