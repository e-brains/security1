package com.kye.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kye.security1.model.User;

//JpaRepository가 기본 CRUD를 가지고 있다.
//@Repository라는 어노테이션을 명시하지 않아도 빈으로 등록 된다. 이유는 JpaRepository를 상속했기 때문임
public interface UserRepository extends JpaRepository<User, Integer>{
	
	//findBy(규칙) + Username(문법) : Jpa query method로 검색
	//select * from user where username = 1?
	public User findByUsername(String username);
}
