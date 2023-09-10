package com.example.usersystem.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author joe
 */
@Data
@Builder
@Entity
@Table(name = "user_info")
public class UserInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "nick_name")
    private String nickName;
    @Column(name = "mobile_no")
    private String mobileNo;
    @Column(name = "password")
    private String password;
    @Column(name = "is_login")
    private String isLogin;
    @Column(name = "login_time")
    private Timestamp loginTime;
    @Column(name = "is_del")
    private String isDel;
    @Column(name = "create_time")
    private Timestamp createTime;

    public UserInfo() {
    }

    public UserInfo(int id, String userId, String nickName, String mobileNo, String password, String isLogin,
            Timestamp loginTime, String isDel, Timestamp createTime) {
        this.id = id;
        this.userId = userId;
        this.nickName = nickName;
        this.mobileNo = mobileNo;
        this.password = password;
        this.isLogin = isLogin;
        this.loginTime = loginTime;
        this.isDel = isDel;
        this.createTime = createTime;
    }
}
