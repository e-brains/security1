package com.kye.security1.repository;

import com.kye.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// @Repository 어노테이션이 없어도 IoC 된다. 이유는 JpaRepository를 상속했기 때문임
public interface UserRepository extends JpaRepository<User, Integer>{

    // findBy 규칙 ->  sql을 만드는 문법 -> jpa query method
    // select * from user where username = 1?
    public User findByUsername(String username);

    // SELECT * FROM user WHERE username = 1? AND password = 2?
    // User findByUsernameAndPassword(String username, String password);

    // @Query(value = "select * from user", nativeQuery = true)
    // User find마음대로();
    // @Query 어노테이션을 사용하면 SQL 문을 내 맘대로 작성할 수 있다.


}
