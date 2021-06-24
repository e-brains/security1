package com.kye.security1.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

// ORM - Object Relation Mapping
@Data
@NoArgsConstructor
@Entity
public class User {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String role;

    // oauth 로그인 구분자
    private String provider;
    private String providerId;

    @CreationTimestamp
    private Timestamp createDate;


    // 빌더 구성을 위한 생성자
    @Builder
    public User(String username, String password, String email, String role, String provider,
                String providerId, Timestamp createDate) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.createDate = createDate;
    }
}
