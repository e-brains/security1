package com.kye.security1.config.oauth.provider;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes; //oauth2User.getAttributes()를 받음
	
	//response={id=171131346, email=getinthere@naver.com, name=홍길동}
	public NaverUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
		
	}
	
	@Override
	public String getProviderId() {
		//naver는 id를 사용
		return (String) attributes.get("id");
	}

	@Override
	public String getProvider() {
		// naver로 하드 코딩
		return "naver";
	}

	@Override
	public String getEmail() {
			return (String) attributes.get("email");
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

}
