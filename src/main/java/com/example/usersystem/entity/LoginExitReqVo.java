package com.example.usersystem.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author joe
 */
@Data
@Builder
public class LoginExitReqVo implements Serializable {

    private String userId;
    private String accessToken;

}
