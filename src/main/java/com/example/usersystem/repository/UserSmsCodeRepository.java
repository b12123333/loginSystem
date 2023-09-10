package com.example.usersystem.repository;

import com.example.usersystem.entity.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserSmsCodeRepository extends JpaRepository<UserSmsCode,Long>{

    UserSmsCode findByMobileNo(String mobileNO);
}
