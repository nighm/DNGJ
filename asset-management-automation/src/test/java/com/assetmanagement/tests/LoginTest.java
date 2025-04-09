package com.assetmanagement.tests;

import com.assetmanagement.pages.LoginPage;
import com.assetmanagement.utils.ConfigReader;
import com.assetmanagement.utils.TestDataReader;
import com.assetmanagement.utils.LoginHelper;
import com.assetmanagement.utils.CaptchaHandler;
import com.assetmanagement.utils.ScreenshotUtils;
import com.assetmanagement.listeners.AllureTestListener;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.net.ConnectException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@Epic("认证管理")
@Feature("用户登录")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginTest.class);
    private WebDriver driver;
    private LoginPage loginPage;
    private ConfigReader config;
    private ScreenshotUtils screenshotUtils;
    private LoginHelper loginHelper;
    private CaptchaHandler captchaHandler;
    private WebDriverWait wait;
    private static final int WAIT_TIME = 3000;

    @BeforeEach
    public void setUp() {
        logger.info("Setting up test environment");
        try {
            config = new ConfigReader();
            
            // 配置Edge选项
            EdgeOptions options = new EdgeOptions();
            if (Boolean.parseBoolean(config.getProperty("browser.headless", "false"))) {
                options.addArguments("--headless");
            }
            options.addArguments("--start-maximized");
            options.addArguments("--remote-allow-origins=*");
            
            // 初始化WebDriver
            driver = new EdgeDriver(options);
            screenshotUtils = new ScreenshotUtils(driver, config);
            
            // 初始化页面对象和工具类
            loginHelper = new LoginHelper(driver, config);
            captchaHandler = new CaptchaHandler(driver, config);
            loginPage = new LoginPage(driver, loginHelper, captchaHandler);
            wait = new WebDriverWait(driver, Duration.ofSeconds(
                Integer.parseInt(config.getProperty("webdriver.wait.timeout", "30"))
            ));
            
            logger.info("Test environment setup completed");
        } catch (Exception e) {
            logger.error("Failed to set up test environment: {}", e.getMessage());
            throw new RuntimeException("测试环境设置失败", e);
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            logger.info("Closing WebDriver");
            driver.quit();
        }
    }

    @Test
    @DisplayName("测试正常登录流程")
    public void testSuccessfulLogin() {
        try {
            String loginUrl = config.getProperty("login.url", "");
            String username = config.getProperty("admin.username", "");
            String password = config.getProperty("admin.password", "");
            
            logger.info("Starting login test with URL: {}", loginUrl);
            
            // 导航到登录页面
            loginPage.navigateToLoginPage(loginUrl);
            
            // 执行登录
            loginPage.login(username, password);
            
            // 验证登录结果
            assertTrue(loginPage.isLoggedIn(), "登录失败，用户未能成功登录");
            
            logger.info("Login test completed successfully");
            
        } catch (Exception e) {
            logger.error("Login test failed: {}", e.getMessage());
            screenshotUtils.takeScreenshot("login_failure");
            fail("登录测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试错误密码登录")
    public void testLoginWithWrongPassword() {
        try {
            String loginUrl = config.getProperty("login.url", "");
            String username = config.getProperty("admin.username", "");
            String wrongPassword = "wrongpassword123";
            
            logger.info("Starting wrong password login test");
            
            // 导航到登录页面
            loginPage.navigateToLoginPage(loginUrl);
            
            // 执行登录
            loginPage.login(username, wrongPassword);
            
            // 验证是否显示错误消息
            assertTrue(loginPage.isLoginErrorDisplayed(), "未显示登录错误消息");
            assertFalse(loginPage.isLoggedIn(), "错误密码登录不应该成功");
            
            logger.info("Wrong password login test completed successfully");
            
        } catch (Exception e) {
            logger.error("Wrong password login test failed: {}", e.getMessage());
            screenshotUtils.takeScreenshot("wrong_password_failure");
            fail("错误密码登录测试失败: " + e.getMessage());
        }
    }

    @Test
    @Story("管理员登录")
    @Description("验证管理员可以使用正确的凭据成功登录")
    @Severity(SeverityLevel.BLOCKER)
    public void testAdminLogin() {
        logger.info("开始管理员登录测试");
        try {
            String username = config.getUsername();
            String password = config.getPassword();
            String loginUrl = config.getLoginUrl();
            
            logger.info("使用以下信息进行登录测试：");
            logger.info("URL: {}", loginUrl);
            logger.info("用户名: {}", username);
            
            // 访问登录页面
            try {
                loginPage.navigateToLoginPage(loginUrl);
                // 等待登录页面加载完成
                wait.until(ExpectedConditions.urlContains("/login"));
            } catch (TimeoutException e) {
                logger.error("登录页面加载超时: {}", e.getMessage());
                throw new RuntimeException("登录页面加载失败，请检查服务器状态和网络连接", e);
            }
            
            // 执行登录
            loginPage.login(username, password);
            
            // 使用显式等待验证登录结果
            boolean loginSuccess = wait.until(driver -> loginPage.isLoggedIn());
            assertTrue(loginSuccess, "登录失败 - 未能检测到登录成功状态");
            logger.info("管理员登录测试成功完成");
            
        } catch (Exception e) {
            logger.error("登录测试过程中发生错误: {}", e.getMessage());
            screenshotUtils.takeScreenshot("admin_login_failure");
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
            String loginUrl = config.getLoginUrl();

            logger.info("使用以下信息进行登录测试：");
            logger.info("用户名: {}", username);
            logger.info("预期结果: {}", expectedResult);

            loginPage.navigateToLoginPage(loginUrl);
            loginPage.login(username, password);

            if ("success".equals(expectedResult)) {
                boolean loginSuccess = wait.until(driver -> loginPage.isLoggedIn());
                assertTrue(loginSuccess, "登录失败 - 用户: " + username);
            } else {
                boolean hasError = wait.until(driver -> loginPage.isLoginErrorDisplayed());
                assertTrue(hasError, "未显示预期的错误消息 - 用户: " + username);
            }
        } catch (Exception e) {
            logger.error("测试用户登录时发生错误: {}", e.getMessage());
            screenshotUtils.takeScreenshot("user_login_failure");
            throw e;
        }
    }

    private static Stream<Map<String, String>> provideLoginTestData() {
        TestDataReader testDataReader = new TestDataReader();
        return TestDataReader.getLoginTestData().stream();
    }
} 