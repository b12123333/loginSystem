package com.example.usersystem.service;

import com.example.usersystem.entity.*;
import com.example.usersystem.repository.UserInfoRepository;
import com.example.usersystem.repository.UserSmsCodeRepository;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;



@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private final UserSmsCodeRepository userSmsCodeRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;


    public boolean getSmsCode(GetSmsCodeReqVo getSmsCodeReqVo){
        //隨機生成6位簡訊驗證碼
        String smsCode = String.valueOf((int) (Math.random() * 100000 +1));

        //呼叫簡訊平台介面
        VonageClient client = VonageClient.builder().apiKey("1373a46c").apiSecret("SHgDJC3p4AGuke3Z").build();

        TextMessage message = new TextMessage("Vonage APIs",
                getSmsCodeReqVo.getMobileNo(),
                "簡訊驗證碼"+ smsCode
        );

        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            System.out.println("Message sent successfully.");
        } else {
            System.out.println("Message failed with error: " + response.getMessages().get(0).getErrorText());
        }
        //儲存使用者簡訊驗證碼資訊至簡訊驗證碼資訊表
        UserSmsCode userSmsCode = UserSmsCode.builder().
                mobileNo(getSmsCodeReqVo.getMobileNo()).
                smsCode(smsCode).
                sendTime(new Timestamp(new Date().getTime())).
                createTime(new Timestamp(new Date().getTime())).build();
        userSmsCodeRepository.save(userSmsCode);
        return true;
    }
    
    public LoginByMobileResVo loginByMobile(LoginByMobileReqVo loginByMobileReqVo) throws BizException {
        //1、短信驗證碼是否正確
        UserSmsCode userSmsCode = userSmsCodeRepository.findByMobileNo(loginByMobileReqVo.getMobileNo());
        if (userSmsCode == null) {
            throw new BizException(-1, "查無手機號碼");
        } else if (!userSmsCode.getSmsCode().equals(loginByMobileReqVo.getSmsCode())) {
            throw new BizException(-1, "驗證碼輸入錯誤");
        }
        //2、判斷用戶是否註冊
        UserInfo userInfo = userInfoRepository.findByMobileNo(loginByMobileReqVo.getMobileNo());
        if (userInfo == null) {
            //隨機生成用戶ID
            String userId = String.valueOf((int) (Math.random() * 100000 + 1));
            userInfo = UserInfo.builder().userId(userId).mobileNo(loginByMobileReqVo.getMobileNo()).isLogin("1")
                    .loginTime(new Timestamp(new Date().getTime())).createTime(new Timestamp(new Date().getTime()))
                    .build();
            //如尚未註冊，則默認註冊
            userInfoRepository.save(userInfo);
        } else {
            //如已註冊，更新資訊
            userInfo.setIsLogin("1");
            userInfo.setLoginTime(new Timestamp(new Date().getTime()));
            userInfoRepository.save(userInfo);
        }
        //3、生成一個隨機的、由大寫字母和數字組成的 Access Token
        String accessToken = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
        //將此訊息儲存在Redis
        //"accessToken": Redis 鍵的名稱。
        //userInfo: 要存儲的值，這可能是一個包含使用者信息的物件或其他數據。
        //30: 過期時間的數量，這裡是 30。
        //TimeUnit.DAYS: 過期時間的單位，這裡是天。
        redisTemplate.opsForValue().set("accessToken", userInfo, 30, TimeUnit.DAYS);

        //4、將accessToken封裝響應出去
        LoginByMobileResVo loginByMobileResVo = LoginByMobileResVo.builder().userId(userInfo.getUserId())
                .accessToken(accessToken).build();
        return loginByMobileResVo;
    }
    
    public boolean loginExit(LoginExitReqVo loginExitReqVo) {
        try {
            redisTemplate.delete(loginExitReqVo.getAccessToken());
            return true;
        } catch (Exception e) {
            log.error(e.toString() + "_" + e);
            return false;
        }
    }
}
