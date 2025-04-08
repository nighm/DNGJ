package com.assetmanagement.tests;

import com.assetmanagement.pages.LoginPage;
import com.assetmanagement.utils.ConfigReader;
import com.assetmanagement.utils.TestDataReader;
import com.assetmanagement.listeners.AllureTestListener;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Feature("登录功能")
public class LoginTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginTest.class);

    private ConfigReader config;
    private LoginPage loginPage;

    @Override
    public void setUp() {
        super.setUp();
        config = new ConfigReader();
        loginPage = new LoginPage(driver);
    }

    @Test
    @DisplayName("测试管理员正常登录")
    @Description("验证管理员使用正确的凭据可以成功登录系统")
    @Story("管理员登录")
    public void testAdminLogin() {
        try {
            logger.info("开始测试管理员登录");
            
            AllureTestListener.addStep("输入登录信息", () -> {
                String username = config.getUsername();
                String password = config.getPassword();
                loginPage.login(username, password);
            });
            
            AllureTestListener.addStep("验证登录结果", () -> {
                assertTrue(loginPage.isLoggedIn(), "登录失败");
            });
            
            AllureTestListener.addScreenshot(driver, "管理员登录成功");
            logger.info("管理员登录测试通过");
        } catch (Exception e) {
            logger.error("管理员登录测试失败", e);
            AllureTestListener.addScreenshot(driver, "管理员登录失败");
            takeScreenshotOnFailure(e);
            throw e;
        }
    }

    @ParameterizedTest
    @MethodSource("provideLoginTestData")
    @DisplayName("测试不同用户登录")
    @Description("验证不同用户使用不同凭据的登录结果")
    @Story("用户登录")
    public void testLoginWithDifferentUsers(Map<String, String> testData) {
        try {
            String username = testData.get("username");
            String password = testData.get("password");
            String expectedResult = testData.get("expected_result");
            
            logger.info("开始测试用户登录 - 用户名: {}, 预期结果: {}", username, expectedResult);
            LoginPage loginPage = new LoginPage(driver);
            
            AllureTestListener.addDescription(String.format("测试用户: %s, 预期结果: %s", username, expectedResult));
            
            AllureTestListener.addStep("输入登录信息", () -> {
                loginPage.login(username, password);
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