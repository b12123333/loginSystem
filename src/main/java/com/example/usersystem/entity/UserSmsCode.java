package com.example.usersystem.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;

//@Builder註解會根據類別中的欄位自動生成建造者方法，同時還會處理必要的null檢查和預設值設定。
//使用@Builder註解後，你可以這樣建立User物件：
//
//User user = User.builder()
//                .id(1L)
//                .username("john_doe")
//                .email("john@example.com")
//                .build();
@Data
@Builder
@Entity
@Table(name = "user_sms_code")
public class UserSmsCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "mobile_no")
    private String mobileNo;
    @Column(name = "sms_code")
    private String smsCode;
    @Column(name = "send_time")
    private Timestamp sendTime;
    @Column(name = "create_time")
    private Timestamp createTime;

    public UserSmsCode() {
    }

    public UserSmsCode(int id, String mobileNo, String smsCode, Timestamp sendTime, Timestamp createTime) {
        this.id = id;
        this.mobileNo = mobileNo;
        this.smsCode = smsCode;
        this.sendTime = sendTime;
        this.createTime = createTime;
    }
}
