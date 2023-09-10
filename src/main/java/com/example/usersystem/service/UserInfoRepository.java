package com.example.usersystem.service;

import com.example.usersystem.entity.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Long>{

    UserInfo findByMobileNo(String mobileNO);
}
