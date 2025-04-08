package com.assetmanagement.pages;

import com.assetmanagement.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class LoginPage {
    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final ConfigReader config;
    
    // 页面元素定位器
    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("loginBtn");
    private final By errorMessage = By.className("error-message");
    private final By dashboardElement = By.id("dashboard");
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.config = new ConfigReader();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getTimeout()));
    }
    
    public void navigateToLoginPage() {
        driver.get(config.getBaseUrl());
    }
    
    public void login(String username, String password) {
        try {
            logger.info("尝试使用用户名 {} 登录", username);
            
            // 等待并填写用户名
            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(usernameInput));
            usernameField.clear();
            usernameField.sendKeys(username);
            
            // 填写密码
            WebElement passwordField = driver.findElement(passwordInput);
            passwordField.clear();
            passwordField.sendKeys(password);
            
            // 点击登录按钮
            driver.findElement(loginButton).click();
            logger.info("登录表单已提交");
            
        } catch (Exception e) {
            logger.error("登录过程中发生错误", e);
            throw e;
        }
    }
    
    public boolean isLoggedIn() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(dashboardElement));
            logger.info("成功检测到仪表板元素，用户已登录");
            return true;
        } catch (Exception e) {
            logger.info("未检测到仪表板元素，用户未登录");
            return false;
        }
    }
    
    public boolean isLoginErrorDisplayed() {
        try {
            WebElement error = wait.until(ExpectedConditions.presenceOfElementLocated(errorMessage));
            boolean isDisplayed = error.isDisplayed();
            logger.info("登录错误消息显示状态: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.info("未检测到登录错误消息");
            return false;
        }
    }
} 