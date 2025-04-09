package com.assetmanagement.demo;

import com.assetmanagement.pages.LoginPage;
import com.assetmanagement.utils.ConfigReader;
import com.assetmanagement.utils.LoginHelper;
import com.assetmanagement.utils.CaptchaHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class LoginDemo {
    private static final Logger logger = LoggerFactory.getLogger(LoginDemo.class);

    public static void main(String[] args) {
        WebDriver driver = null;
        try {
            // 初始化WebDriver
            driver = new EdgeDriver();
            driver.manage().window().maximize();
            
            // 初始化配置和工具类
            ConfigReader config = new ConfigReader();
            LoginHelper loginHelper = new LoginHelper(driver, config);
            CaptchaHandler captchaHandler = new CaptchaHandler(driver, config);
            LoginPage loginPage = new LoginPage(driver, loginHelper, captchaHandler);
            
            // 获取登录信息
            String username = config.getProperty("admin.username", "");
            String password = config.getProperty("admin.password", "");
            String loginUrl = config.getProperty("login.url", "");
            
            logger.info("开始登录演示");
            logger.info("URL: {}", loginUrl);
            logger.info("用户名: {}", username);
            
            // 执行登录
            loginPage.navigateToLoginPage(loginUrl);
            loginPage.login(username, password);
            
            // 等待用户查看结果
            logger.info("登录完成，按回车键退出...");
            System.in.read();
            
        } catch (Exception e) {
            logger.error("登录演示过程中发生错误: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
} 