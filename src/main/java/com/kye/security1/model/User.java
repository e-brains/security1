package com.kye.security1.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

// ORM - Object Relation Mapping
@Data
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

}
