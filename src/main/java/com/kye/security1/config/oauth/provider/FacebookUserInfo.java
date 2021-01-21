package com.kye.security1.config.oauth.provider;

import java.util.Map;

public class FacebookUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes; //oauth2User.getAttributes()를 받음
	
	public FacebookUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
		
	}
	
	@Override
	public String getProviderId() {
		//페이스북은 id를 사용
		return (String) attributes.get("id");
	}

	@Override
	public String getProvider() {
		// facebook로 하드 코딩
		return "facebook";
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
