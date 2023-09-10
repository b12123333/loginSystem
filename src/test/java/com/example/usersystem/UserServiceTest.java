package com.example.usersystem;

import com.example.usersystem.entity.*;
import com.example.usersystem.repository.UserInfoRepository;
import com.example.usersystem.repository.UserSmsCodeRepository;
import com.example.usersystem.service.UserService;
import com.vonage.client.VonageClient;
import com.vonage.client.application.Application;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserServiceTest {

    @MockBean
    private VonageClient vonageClient;

    @MockBean
    private UserSmsCodeRepository userSmsCodeRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private UserInfoRepository userInfoRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSmsCode() {
        // 模擬簡訊驗證平台
        TextMessage message = new TextMessage("Vonage APIs",
                "0920936276",
                "簡訊驗證碼"+ "123456"
        );

        // 模擬的VonageClient
        vonageClient = mock(VonageClient.class);
        SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);

        when(vonageClient.getSmsClient().submitMessage(message))
                .thenReturn(response);

        // 測試
        GetSmsCodeReqVo smsCodeReqVo = GetSmsCodeReqVo.builder().mobileNo("0920936276").build();
        boolean result = userService.getSmsCode(smsCodeReqVo);

        // 返回結果
        assertTrue(result);

        // 簡訊驗證碼是否保存至資料庫
//        UserSmsCode userSmsCode = UserSmsCode.builder().
//                mobileNo("0920936276").
//                smsCode("123456").
//                build();
        verify(userSmsCodeRepository).save(any(UserSmsCode.class));

//        ArgumentCaptor<UserSmsCode> captor = ArgumentCaptor.forClass(UserSmsCode.class);
//        verify(userSmsCodeRepository).save(captor.capture());
//
//        // 獲取參數
//        UserSmsCode capturedUserSmsCode = captor.getValue();
//
//        // 因為資料創建的時候有時間欄位，測試時的時間跟驗證簡訊保存時的時間應該有所不同，因此只挑選手機號碼跟簡訊驗證碼進行比對
//        assertEquals("0920936276", capturedUserSmsCode.getMobileNo());
//        assertEquals("123456", capturedUserSmsCode.getSmsCode());
    }

    @Test
    void testLoginByMobile_Success() throws BizException {
        // 模擬資料
        LoginByMobileReqVo request = LoginByMobileReqVo.builder()
                .mobileNo("0920936276")
                .smsCode("123456")
                .build();
        UserSmsCode userSmsCode = UserSmsCode.builder().
                mobileNo("0920936276").
                smsCode("123456").
                build();
        UserInfo userInfo = UserInfo.builder()
                .id(1)
                .userId("barry")
                .mobileNo("0920936276")
                .isLogin("1")
                .password("a222930246")
                .nickName("su")
                .build(); // User is not registered

        when(userSmsCodeRepository.findByMobileNo(request.getMobileNo())).thenReturn(userSmsCode);
        when(userInfoRepository.findByMobileNo(request.getMobileNo())).thenReturn(userInfo);

        // 測試方法
        LoginByMobileResVo result = userService.loginByMobile(request);

        // Assertions
        assertNotNull(result);
        assertNotNull(result.getUserId());
        assertNotNull(result.getAccessToken());

        verify(userSmsCodeRepository).findByMobileNo(request.getMobileNo());
        verify(userInfoRepository).findByMobileNo(request.getMobileNo());
        verify(userInfoRepository).save(any(UserInfo.class));
        verify(redisTemplate).opsForValue().set(eq("accessToken"), any(UserInfo.class), eq(30L), eq(TimeUnit.DAYS));
    }

    @Test
    void testLoginExit_Success() {
        // 模擬資料
        LoginExitReqVo request = LoginExitReqVo.builder().userId("barry").accessToken("1qaz2wsc").build();

        // 測試方法
        boolean result = userService.loginExit(request);

        // Assertions
        assertTrue(result);

        verify(redisTemplate).delete(request.getAccessToken());
    }

    @Test
    void testLoginExit_Failure() {
        // 模擬資料
        LoginExitReqVo request = LoginExitReqVo.builder().userId("barry").accessToken(null).build();

        // 拋出錯誤
        doThrow(new RuntimeException("Redis error")).when(redisTemplate).delete(request.getAccessToken());

        // 測試方法
        boolean result = userService.loginExit(request);

        // Assertions
        assertFalse(result);
    }
}
