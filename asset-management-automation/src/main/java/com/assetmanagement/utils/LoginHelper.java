package com.assetmanagement.utils;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;

public class LoginHelper {
    private static final Logger logger = LoggerFactory.getLogger(LoginHelper.class);
    private final WebDriver driver;
    private final ConfigReader config;

    public LoginHelper(WebDriver driver, ConfigReader config) {
        this.driver = driver;
        this.config = config;
    }

    /**
     * 通过UI进行登录
     */
    public void loginViaUI(String username, String password) {
        driver.get(config.getLoginUrl());
        // UI登录逻辑...
    }

    /**
     * 通过Cookie方式登录（如果可用）
     */
    public void loginViaCookie(String username, String password) {
        try {
            // 先访问一下首页，确保域名正确
            driver.get(config.getBaseUrl());
            
            // 设置认证Cookie
            Cookie authCookie = new Cookie("auth_token", "test_token");
            driver.manage().addCookie(authCookie);
            
            // 刷新页面
            driver.navigate().refresh();
            logger.info("Successfully set auth cookie for user: {}", username);
        } catch (Exception e) {
            logger.error("Failed to login via cookie: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 通过LocalStorage方式登录（如果可用）
     */
    public void loginViaLocalStorage(String username, String password) {
        try {
            driver.get(config.getBaseUrl());
            
            if (driver instanceof WebStorage) {
                LocalStorage storage = ((WebStorage) driver).getLocalStorage();
                storage.setItem("auth_token", "test_token");
                storage.setItem("user_info", String.format("{\"username\":\"%s\"}", username));
                
                driver.navigate().refresh();
                logger.info("Successfully set local storage for user: {}", username);
            }
        } catch (Exception e) {
            logger.error("Failed to login via local storage: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 通过测试环境特殊URL登录（如果可用）
     */
    public void loginViaTestUrl(String username) {
        try {
            String testLoginUrl = String.format("%s/test-login?username=%s", 
                                              config.getBaseUrl(), username);
            driver.get(testLoginUrl);
            logger.info("Successfully accessed test login URL for user: {}", username);
        } catch (Exception e) {
            logger.error("Failed to login via test URL: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 尝试所有可用的登录方式
     */
    public void loginWithFallback(String username, String password) {
        Exception lastException = null;
        
        // 尝试所有登录方式
        try {
            loginViaCookie(username, password);
            return;
        } catch (Exception e) {
            lastException = e;
            logger.warn("Cookie login failed, trying next method...");
        }

        try {
            loginViaLocalStorage(username, password);
            return;
        } catch (Exception e) {
            lastException = e;
            logger.warn("LocalStorage login failed, trying next method...");
        }

        try {
            loginViaTestUrl(username);
            return;
        } catch (Exception e) {
            lastException = e;
            logger.warn("Test URL login failed, trying next method...");
        }

        try {
            loginViaUI(username, password);
            return;
        } catch (Exception e) {
            lastException = e;
            logger.error("All login methods failed");
        }

        if (lastException != null) {
            throw new RuntimeException("Failed to login with all available methods", lastException);
        }
    }
} 