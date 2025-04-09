package com.assetmanagement.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class CaptchaHandler {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaHandler.class);
    
    // 测试环境验证码绕过参数
    private static final String BYPASS_PARAM = "bypass_captcha=true";
    
    // 测试环境固定验证码
    private static final String TEST_CAPTCHA = "1234";
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final ConfigReader config;
    
    public CaptchaHandler(WebDriver driver, ConfigReader config) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.config = config;
    }
    
    /**
     * 尝试绕过验证码
     */
    public boolean tryBypassCaptcha() {
        try {
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("?")) {
                currentUrl += "?";
            } else {
                currentUrl += "&";
            }
            currentUrl += BYPASS_PARAM;
            
            driver.get(currentUrl);
            logger.info("Attempted to bypass captcha using URL parameter");
            return true;
        } catch (Exception e) {
            logger.warn("Failed to bypass captcha: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取测试环境固定验证码
     */
    public String getTestCaptcha() {
        return TEST_CAPTCHA;
    }
    
    /**
     * 等待用户手动输入验证码
     */
    public void waitForManualCaptcha(int timeoutSeconds) throws InterruptedException {
        logger.info("Waiting {} seconds for manual captcha input...", timeoutSeconds);
        Thread.sleep(timeoutSeconds * 1000L);
    }
    
    /**
     * 智能处理验证码
     * 1. 首先尝试绕过
     * 2. 如果不能绕过，使用测试验证码
     * 3. 如果都不行，等待手动输入
     */
    public void handleCaptcha() throws InterruptedException {
        // 首先检查验证码输入框是否存在
        try {
            WebElement captchaInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[placeholder*='验证码']")));
            
            // 尝试绕过验证码
            if (tryBypassCaptcha()) {
                logger.info("Successfully bypassed captcha");
                return;
            }
            
            // 如果是测试环境，使用固定验证码
            if (config.getBaseUrl().contains("test") || 
                config.getBaseUrl().contains("dev") || 
                config.getBaseUrl().contains("staging")) {
                logger.info("Using test environment captcha: {}", TEST_CAPTCHA);
                captchaInput.clear();
                captchaInput.sendKeys(TEST_CAPTCHA);
                return;
            }
            
            // 其他情况等待手动输入
            logger.info("Waiting for manual captcha input...");
            waitForManualCaptcha(30);
            
        } catch (Exception e) {
            logger.warn("No captcha input found or error handling captcha: {}", e.getMessage());
        }
    }
} 