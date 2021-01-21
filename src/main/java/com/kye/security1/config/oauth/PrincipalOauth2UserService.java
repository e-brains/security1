package com.kye.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.kye.security1.config.auth.PrincipalDetails;
import com.kye.security1.config.oauth.provider.FacebookUserInfo;
import com.kye.security1.config.oauth.provider.GoogleUserInfo;
import com.kye.security1.config.oauth.provider.NaverUserInfo;
import com.kye.security1.config.oauth.provider.OAuth2UserInfo;
import com.kye.security1.model.User;
import com.kye.security1.repository.UserRepository;


// 목적 1. userEntity와 oauth2User를 principal타입으로 묶기 위해 
//         2. oauth로 로그인 했을때 최초 로그인 이면 강제 회원가입을 진행하기 위함
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	//후처리되는 함수
	//구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
	//함수 종료 시 @AuthenticationPrincipal어노테이션이 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//		System.out.println("getClientRegistration == "+ userRequest.getClientRegistration());
//		System.out.println("getAccessToken == "+ userRequest.getAccessToken());
		//구글로그인 버튼 클릭 >> 구글로그인 창 >> 로그인 완료 >> code를 리턴(OAuth-Client라이브러리 >> AccessToken요청 
		//>> userRequest정보 받음 >> loadUser함수로 회원 프로필을 받음 		
//		System.out.println("getAttributes == "+ super.loadUser(userRequest).getAttributes());
		
		//구글로 부터 받은 정보 getAttributes == {sub=..... ......}
		//우리가 필요한 회원가입 정보 ( 우리사이트에 아래 정보로 강제 가입 진행 )
		//username = google+sub번호 (구글로그인 되었다는 구분 + sub 중복방지)
		//password = google 고정된 아무거나 (이것을 사용해서 직접 로그인 하는 경우는 없기 때문)
		//email = getAttributes에서 가져온 정보 그대로 사용
		//role = ROLE_USER
		//provider = google  (구글로그인로 로그인되었다는 구분을 위해 User객체에 추가)
		//providerId = sub번호
		OAuth2User oauth2User = super.loadUser(userRequest);
		System.out.println("getAttributes == "+ oauth2User.getAttributes());
		
		//구글 로그인자에 대한 강제 회원가입을 위한 user정보 만들기
//		String provider = userRequest.getClientRegistration().getRegistrationId(); //google 구분자
//		String providerId = oauth2User.getAttribute("sub"); //구글 아이디
//		String username = provider + "_" + providerId ; //google_103930494..... 유저명이 중복될일 이 없음
//		System.out.println("username ====" + username);
//		String email = oauth2User.getAttribute("email");
//		String password = bCryptPasswordEncoder.encode("getGoogle"); //별의미는 없지만 user정보를 하나씩 만들어준다는 의미정도
//		String role = "ROLE_USER";

		//페이스북 로그인을 추가하는 경우 위와 같이 하면 provider가 틀릴 경우 파라미터가 틀리기 때문에 유지보수가 어려워진다.
		//새로운 클래스를 만들어서 관리하는 것이 필요하다.
		//OAuth2UserInfo인터페이스를 만들고 이를 implements한 GoogleUserInfo , FacebookUserInfo클래스를 만든다.
		//스프링은 OAuth-Client에 구글, 페이스북, 트위터는 기본 provider로 지원한다. 그러나 네이버는 기본 지원하지 않는다.
		//먼저 네이버는 provider로 등록해줘야 한다. 
		//향후 외부 로그인 연동이 있으면 클래스만 추가하면 됨
		OAuth2UserInfo oauth2UserInfo = null;
		if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oauth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
		}else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("페이스북 로그인 요청");
			oauth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
		}else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			oauth2UserInfo = new NaverUserInfo( (Map)oauth2User.getAttributes().get("response") );
		}else {
			System.out.println("우리는 구글과 페이스북, naver 만 지원합니다.!!!");
		}
		
		//로그인자에 대한 강제 회원가입을 위한 user정보 만들기
		String provider = oauth2UserInfo.getProvider(); 
		String providerId = oauth2UserInfo.getProviderId(); 
		String username = provider + "_" + providerId ; 
		String email = oauth2UserInfo.getEmail();
		String password = bCryptPasswordEncoder.encode("getProvider"); //별의미는 없지만 user정보를 하나씩 만들어준다는 의미정도
		String role = "ROLE_USER"; 
		
		
		//이미회원가입이 되어있는 경우는 제외하위 위해 UserRepository를 활용
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
			System.out.println("이미 회원가입이 되어 있습니다.");
		}
		
		//PrincipalDetails는 일반적인 로그인은 User만 들고 있겠지만 oauth로그인 경우 user와 attributes를 같이 들고 있다.
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());  //PrincipalDetails가 세션정보로 들어간다.
	}
}
