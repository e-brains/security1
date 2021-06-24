package com.kye.security1.oauth;

import com.kye.security1.auth.PrincipalDetails;
import com.kye.security1.model.User;
import com.kye.security1.repository.UserRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

     // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 메서드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        /********************************************************************************************
         * 프로세스
         * 구글 로그인 버튼 클릭 => 구글 로그인 창 => 로그인 완료 => code를 리턴(OAuth-Client라이브러리가 받아줌)
         * => AccessToken 요청  ( 여기까지 userRequest 정보 )
         * userRequest를 이용하여 loadUser 함수 호출
         * loadUser는 토큰을 가지고 구글로 부터 회원 프로필 수신해 준다.
         * 사용자 정보는 userRequest.getAttribues()로 읽어 올수 있다.
         ********************************************************************************************/

        // 내부적으로 우리쪽에 회원 가입을 시키기 위해 User 정보를 가공한다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //System.out.println("PrincipalOauth2UserService : oAuth2User.getAttributes() ====> " + oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getClientId();  // google
        String providerId = oAuth2User.getAttribute("sub");
        String username = provider+"_"+providerId;  // google_12344........  중복이 없다.
        String password = bCryptPasswordEncoder.encode(("학습관리시스템")); // 큰 의미는 없지만 강제 생성을 위해 작성
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        // 기존에 이미 가입되어 있는지 여부 확인 (구글 로그인이 최초인지 확인하기 위해서)
        User userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }else {
            System.out.println("이미 과거에 구글 로그인을 하면서 자동 회원가입이 되어 있다.");
        }

        // PrincipalDetails는 UserDetails와 OAuth2User를 implementation하고 있기 때문에 사용 가능
        // OAuth2User를 넘기는 생성자를 추가했기 때문에 파라미터 2개 넘김
        // 생성되는 순간 2개의 파라미터는 Authentication 에 들어간다.
        // Authentication => User 객체와 Attributes Map을 모두 들고 있게 된다.
        // 본 메서드 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다.
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
