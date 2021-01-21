package com.kye.security1.config.oauth.provider;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes; //getAttributes()를 받음
	
	public GoogleUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
		
	}
	
	@Override
	public String getProviderId() {
		// 구글은 sub
		return (String) attributes.get("sub");
	}

	@Override
	public String getProvider() {
		// google로 하드 코딩
		return "google";
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
