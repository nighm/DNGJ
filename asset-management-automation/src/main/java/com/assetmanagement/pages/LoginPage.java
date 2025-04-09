package com.assetmanagement.pages;

import com.assetmanagement.utils.CaptchaHandler;
import com.assetmanagement.utils.LoginHelper;
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
    private final LoginHelper loginHelper;
    private final CaptchaHandler captchaHandler;
    
    // 页面元素定位器
    private final By usernameInput = By.cssSelector("input[placeholder*='用户名']");
    private final By passwordInput = By.cssSelector("input[placeholder*='密码']");
    private final By captchaInput = By.cssSelector("input[placeholder*='验证码']");
    private final By loginButton = By.cssSelector("button[type='button']");
    private final By captchaImage = By.cssSelector("img[src*='captcha']");

    public LoginPage(WebDriver driver, LoginHelper loginHelper, CaptchaHandler captchaHandler) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.loginHelper = loginHelper;
        this.captchaHandler = captchaHandler;
        logger.info("LoginPage initialized with WebDriver and helpers");
    }

    public void navigateToLoginPage(String url) {
        logger.info("Navigating to login page: {}", url);
        try {
            driver.get(url);
            // 等待页面加载完成
            wait.until(driver -> ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("return document.readyState").equals("complete"));
            logger.info("Successfully navigated to login page");
            
            // 记录页面标题和URL以便调试
            logger.debug("Page title: {}", driver.getTitle());
            logger.debug("Current URL: {}", driver.getCurrentUrl());
            
        } catch (Exception e) {
            logger.error("Failed to load login page: {}", e.getMessage());
            logger.debug("Page source at failure: {}", driver.getPageSource());
            throw e;
        }
    }

    public void login(String username, String password) throws InterruptedException {
        logger.info("Attempting to login with username: {}", username);
        
        try {
            // 等待并输入用户名
            logger.debug("Waiting for username input field...");
            WebElement userElement = wait.until(ExpectedConditions.presenceOfElementLocated(usernameInput));
            userElement.clear();
            userElement.sendKeys(username);
            logger.debug("Username entered successfully");

            // 等待并输入密码
            logger.debug("Waiting for password input field...");
            WebElement passElement = wait.until(ExpectedConditions.presenceOfElementLocated(passwordInput));
            passElement.clear();
            passElement.sendKeys(password);
            logger.debug("Password entered successfully");

            // 处理验证码
            captchaHandler.handleCaptcha();

            // 点击登录按钮
            logger.debug("Clicking login button...");
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            loginBtn.click();
            logger.info("Login form submitted successfully");

            // 等待登录结果
            wait.until(driver -> !driver.getCurrentUrl().contains("login"));
            
        } catch (Exception e) {
            logger.error("Failed to perform login: {}", e.getMessage());
            logger.debug("Page source at failure: {}", driver.getPageSource());
            throw e;
        }
    }

    public boolean isLoggedIn() {
        try {
            // 检查URL是否包含dashboard或其他登录成功后的标识
            String currentUrl = driver.getCurrentUrl();
            boolean isLoggedIn = !currentUrl.contains("login") && 
                               (currentUrl.contains("/dashboard") || 
                                currentUrl.contains("/home") || 
                                currentUrl.contains("/asset"));
            logger.debug("Login status check - Current URL: {}, Is logged in: {}", currentUrl, isLoggedIn);
            return isLoggedIn;
        } catch (Exception e) {
            logger.warn("Failed to verify login status: {}", e.getMessage());
            return false;
        }
    }

    public boolean isLoginErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(By.className("el-message--error"))).isDisplayed();
        } catch (Exception e) {
            logger.warn("No error message found: {}", e.getMessage());
            return false;
        }
    }
} 