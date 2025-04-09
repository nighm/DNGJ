package com.assetmanagement.tests;

import com.assetmanagement.pages.LoginPage;
import com.assetmanagement.utils.ConfigReader;
import com.assetmanagement.utils.TestDataReader;
import com.assetmanagement.utils.LoginHelper;
import com.assetmanagement.utils.CaptchaHandler;
import com.assetmanagement.listeners.AllureTestListener;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Epic("认证管理")
@Feature("用户登录")
public class LoginTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginTest.class);
    private ConfigReader config;
    private LoginPage loginPage;
    private LoginHelper loginHelper;
    private CaptchaHandler captchaHandler;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        config = new ConfigReader();
        loginHelper = new LoginHelper(driver, config);
        captchaHandler = new CaptchaHandler(driver, config);
        loginPage = new LoginPage(driver, loginHelper, captchaHandler);
    }

    @Test
    @Story("管理员登录")
    @Description("验证管理员可以使用正确的凭据成功登录")
    @Severity(SeverityLevel.BLOCKER)
    public void testAdminLogin() throws InterruptedException {
        logger.info("开始管理员登录测试");
        try {
            String username = config.getUsername();
            String password = config.getPassword();
            String loginUrl = config.getLoginUrl();
            
            logger.info("使用以下信息进行登录测试：");
            logger.info("URL: {}", loginUrl);
            logger.info("用户名: {}", username);
            
            // 访问登录页面
            loginPage.navigateToLoginPage(loginUrl);
            
            // 等待页面加载完成
            Thread.sleep(2000); // 给页面一些加载时间
            
            // 执行登录
            loginPage.login(username, password);
            
            // 验证登录结果
            assertTrue(loginPage.isLoggedIn(), "登录失败 - 未能检测到登录成功状态");
            logger.info("管理员登录测试成功完成");
            
        } catch (Exception e) {
            logger.error("登录测试过程中发生错误: {}", e.getMessage());
            takeScreenshotOnFailure(e);
            throw e;
        }
    }

    @ParameterizedTest
    @MethodSource("provideLoginTestData")
    @DisplayName("测试不同用户登录")
    @Description("验证不同用户使用不同凭据的登录结果")
    @Story("用户登录")
    public void testLoginWithDifferentUsers(Map<String, String> testData) throws InterruptedException {
        try {
            String username = testData.get("username");
            String password = testData.get("password");
            String expectedResult = testData.get("expected_result");
            
            logger.info("开始测试用户登录 - 用户名: {}, 预期结果: {}", username, expectedResult);
            
            AllureTestListener.addDescription(String.format("测试用户: %s, 预期结果: %s", username, expectedResult));
            
            // 访问登录页面
            loginPage.navigateToLoginPage(config.getLoginUrl());
            
            // 等待页面加载完成
            Thread.sleep(2000); // 给页面一些加载时间
            
            AllureTestListener.addStep("输入登录信息", () -> {
                try {
                    loginPage.login(username, password);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            
            AllureTestListener.addStep("验证登录结果", () -> {
                if ("success".equals(expectedResult)) {
                    assertTrue(loginPage.isLoggedIn(), "登录应该成功但失败了");
                } else {
                    assertTrue(loginPage.isLoginErrorDisplayed(), "登录应该失败但成功了");
                }
            });
            
            AllureTestListener.addScreenshot(driver, String.format("用户登录测试 - %s", username));
            logger.info("用户登录测试通过 - 用户名: {}", username);
            
        } catch (Exception e) {
            logger.error("用户登录测试失败 - 用户名: {}", testData.get("username"), e);
            AllureTestListener.addScreenshot(driver, String.format("用户登录失败 - %s", testData.get("username")));
            takeScreenshotOnFailure(e);
            throw e;
        }
    }

    private static Stream<Map<String, String>> provideLoginTestData() {
        List<Map<String, String>> testData = TestDataReader.getLoginTestData();
        return testData.stream();
    }
} 