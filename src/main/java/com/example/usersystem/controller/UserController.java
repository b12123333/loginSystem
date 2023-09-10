package com.example.usersystem.controller;

import com.example.usersystem.entity.*;
import com.example.usersystem.entity.enums.ResultCode;
import com.example.usersystem.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "getSmsCode", method = RequestMethod.POST)
    public Boolean getSmsCode(@RequestParam("reqId") String reqId,
                              @RequestParam("mobileNo") String mobileNo) {
        GetSmsCodeReqVo getSmsCodeReqVo = GetSmsCodeReqVo.builder().reqId(reqId).mobileNo(mobileNo).build();
        return userService.getSmsCode(getSmsCodeReqVo);
    }

    @RequestMapping(value = "loginByMobile", method = RequestMethod.POST)
    public ApiResponse loginByMobile(@RequestParam("reqId") String reqId,
                                     @RequestParam("mobileNo") String mobileNo, @RequestParam("smsCode") String smsCode) throws BizException {
        LoginByMobileReqVo loginByMobileReqVo = LoginByMobileReqVo.builder().reqId(reqId).mobileNo(mobileNo)
                .smsCode(smsCode).build();
        LoginByMobileResVo loginByMobileResVo = userService.loginByMobile(loginByMobileReqVo);
        return ApiResponse.success(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getDesc(), loginByMobileResVo);
    }

    @RequestMapping(value = "loginExit", method = RequestMethod.POST)
    public Boolean loginExit(@RequestParam("userId") String userId,
                             @RequestParam("accessToken") String accessToken) {
        LoginExitReqVo loginExitReqVo = LoginExitReqVo.builder().userId(userId).accessToken(accessToken).build();
        return userService.loginExit(loginExitReqVo);
    }
}
