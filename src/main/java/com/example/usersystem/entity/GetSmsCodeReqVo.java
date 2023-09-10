package com.example.usersystem.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author joe
 */
@Data
@Builder
public class GetSmsCodeReqVo implements Serializable {

    public GetSmsCodeReqVo(String reqId, String mobileNo){
        this.reqId = reqId;
        this.mobileNo = mobileNo;
    }
    private String reqId;
    private String mobileNo;

}
