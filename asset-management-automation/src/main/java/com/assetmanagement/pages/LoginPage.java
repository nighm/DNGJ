package com.assetmanagement.pages;

import com.assetmanagement.utils.CaptchaHandler;
import com.assetmanagement.utils.LoginHelper;
import com.assetmanagement.utils.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

public class LoginPage {
    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final LoginHelper loginHelper;
    private final CaptchaHandler captchaHandler;
    private final ConfigReader config;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_SECONDS = 5;
    
    // 页面元素定位器
    private final By usernameInput = By.cssSelector("input[type='text'][placeholder='请输入用户名']");
    private final By passwordInput = By.cssSelector("input[type='password'][placeholder='请输入密码']");
    private final By captchaInput = By.cssSelector("input[type='text'][placeholder='请输入验证码']");
    private final By loginButton = By.cssSelector(".el-button--primary");
    private final By captchaImage = By.cssSelector(".captcha-img");
    private final By errorMessage = By.cssSelector(".el-message.el-message--error");

    public LoginPage(WebDriver driver, LoginHelper loginHelper, CaptchaHandler captchaHandler) {
        this.driver = driver;
        this.config = new ConfigReader();
        int timeout = Integer.parseInt(config.getProperty("webdriver.wait.timeout", "30"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        this.loginHelper = loginHelper;
        this.captchaHandler = captchaHandler;
        logger.info("LoginPage initialized with WebDriver and helpers");
    }

    public void navigateToLoginPage(String url) {
        logger.info("Navigating to login page: {}", url);
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                driver.get(url);
                
                // 等待页面加载完成
                wait.until(driver -> {
                    String readyState = ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState").toString();
                    logger.debug("Page ready state: {}", readyState);
                    return "complete".equals(readyState);
                });
                
                // 验证页面标题或其他元素以确保正确加载
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.titleContains("教育终端云平台"),
                    ExpectedConditions.presenceOfElementLocated(usernameInput)
                ));
                
                logger.info("Successfully navigated to login page");
                logger.debug("Page title: {}", driver.getTitle());
                logger.debug("Current URL: {}", driver.getCurrentUrl());
                return;
                
            } catch (TimeoutException e) {
                logger.error("Page load timeout (attempt {}): {}", retryCount + 1, e.getMessage());
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    logger.info("Retrying in {} seconds...", RETRY_DELAY_SECONDS);
                    try {
                        TimeUnit.SECONDS.sleep(RETRY_DELAY_SECONDS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("页面加载重试被中断", ie);
                    }
                }
            } catch (WebDriverException e) {
                logger.error("Failed to load login page (attempt {}): {}", retryCount + 1, e.getMessage());
                if (e.getMessage().contains("ERR_CONNECTION_REFUSED")) {
                    throw new RuntimeException("无法连接到服务器，请确认服务器是否正常运行", e);
                }
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    logger.info("Retrying in {} seconds...", RETRY_DELAY_SECONDS);
                    try {
                        TimeUnit.SECONDS.sleep(RETRY_DELAY_SECONDS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("页面加载重试被中断", ie);
                    }
                } else {
                    throw e;
                }
            }
        }
        throw new RuntimeException("页面加载失败，已达到最大重试次数");
    }

    public void login(String username, String password) {
        logger.info("Attempting to login with username: {}", username);
        int retryCount = 0;
        
        while (retryCount < MAX_RETRIES) {
            try {
                // 等待页面完全加载
                wait.until(driver -> {
                    String readyState = ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState").toString();
                    logger.debug("Login page ready state: {}", readyState);
                    return "complete".equals(readyState);
                });

                // 等待并输入用户名
                logger.debug("Waiting for username input field...");
                WebElement userElement = wait.until(ExpectedConditions.elementToBeClickable(
                    wait.until(ExpectedConditions.presenceOfElementLocated(usernameInput))
                ));
                userElement.clear();
                userElement.sendKeys(username);
                logger.debug("Username entered successfully");

                // 等待并输入密码
                logger.debug("Waiting for password input field...");
                WebElement passElement = wait.until(ExpectedConditions.elementToBeClickable(
                    wait.until(ExpectedConditions.presenceOfElementLocated(passwordInput))
                ));
                passElement.clear();
                passElement.sendKeys(password);
                logger.debug("Password entered successfully");

                // 处理验证码
                if (isCaptchaPresent()) {
                    logger.info("Captcha detected, attempting to handle...");
                    try {
                        captchaHandler.handleCaptcha();
                        logger.info("Captcha handled successfully");
                    } catch (InterruptedException e) {
                        logger.error("Captcha handling interrupted: {}", e.getMessage());
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("验证码处理被中断", e);
                    }
                }

                // 点击登录按钮
                logger.debug("Waiting for login button to be clickable...");
                WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    wait.until(ExpectedConditions.presenceOfElementLocated(loginButton))
                ));
                
                // 确保按钮在视图中
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loginBtn);
                wait.until(driver -> {
                    Rectangle rect = loginBtn.getRect();
                    return rect.getY() >= 0 && rect.getY() <= ((Long) ((JavascriptExecutor) driver)
                        .executeScript("return window.innerHeight")).intValue();
                });
                
                logger.debug("Clicking login button...");
                loginBtn.click();
                logger.info("Login form submitted successfully");

                // 等待登录结果
                wait.until(driver -> {
                    String currentUrl = driver.getCurrentUrl();
                    boolean urlChanged = !currentUrl.contains("login");
                    logger.debug("Checking login result - Current URL: {}", currentUrl);
                    
                    if (!urlChanged) {
                        // 检查是否有错误消息
                        try {
                            WebElement error = driver.findElement(errorMessage);
                            if (error.isDisplayed()) {
                                String errorText = error.getText();
                                logger.warn("Login error message displayed: {}", errorText);
                                if (errorText.contains("验证码错误")) {
                                    // 如果是验证码错误，刷新验证码并重试
                                    logger.info("Captcha error detected, will retry with new captcha");
                                    return true;
                                }
                                return true;
                            }
                        } catch (NoSuchElementException ignored) {
                            logger.debug("No error message found, continuing to wait for URL change");
                        }
                    }
                    return urlChanged;
                });
                
                if (!isLoggedIn()) {
                    logger.warn("URL changed but login status check failed");
                    throw new RuntimeException("登录可能失败，请检查登录状态");
                }
                
                logger.info("Login successful - Current URL: {}", driver.getCurrentUrl());
                return;
                
            } catch (TimeoutException e) {
                logger.error("Login operation timed out (attempt {}): {}", retryCount + 1, e.getMessage());
                logger.debug("Page source at timeout: {}", driver.getPageSource());
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    logger.info("Retrying login in {} seconds...", RETRY_DELAY_SECONDS);
                    try {
                        TimeUnit.SECONDS.sleep(RETRY_DELAY_SECONDS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("登录重试被中断", ie);
                    }
                }
            } catch (ElementClickInterceptedException e) {
                logger.error("Login button click intercepted (attempt {}): {}", retryCount + 1, e.getMessage());
                // 尝试使用JavaScript点击
                try {
                    WebElement loginBtn = driver.findElement(loginButton);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
                    logger.info("Attempted alternative click using JavaScript");
                } catch (Exception je) {
                    logger.error("JavaScript click also failed: {}", je.getMessage());
                }
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    logger.info("Retrying login in {} seconds...", RETRY_DELAY_SECONDS);
                    try {
                        TimeUnit.SECONDS.sleep(RETRY_DELAY_SECONDS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("登录重试被中断", ie);
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to perform login (attempt {}): {}", retryCount + 1, e.getMessage());
                logger.debug("Page source at failure: {}", driver.getPageSource());
                throw new RuntimeException("登录过程中发生未预期的错误: " + e.getMessage(), e);
            }
        }
        throw new RuntimeException("登录失败，已达到最大重试次数");
    }

    public boolean isLoggedIn() {
        try {
            // 检查URL是否包含dashboard或其他登录成功后的标识
            String currentUrl = driver.getCurrentUrl();
            boolean isLoggedIn = !currentUrl.contains("login") && 
                               (currentUrl.contains("/dashboard") || 
                                currentUrl.contains("/home") || 
                                currentUrl.contains("/asset"));
            
            // 额外检查页面上的用户信息元素
            if (isLoggedIn) {
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".user-info, .user-profile, .avatar")
                    ));
                } catch (TimeoutException e) {
                    logger.warn("User info element not found after URL change");
                    return false;
                }
            }
            
            logger.debug("Login status check - Current URL: {}, Is logged in: {}", currentUrl, isLoggedIn);
            return isLoggedIn;
        } catch (Exception e) {
            logger.warn("Failed to verify login status: {}", e.getMessage());
            return false;
        }
    }

    public boolean isLoginErrorDisplayed() {
        try {
            WebElement error = wait.until(ExpectedConditions.presenceOfElementLocated(errorMessage));
            boolean isDisplayed = error.isDisplayed();
            if (isDisplayed) {
                logger.debug("Login error message: {}", error.getText());
            }
            return isDisplayed;
        } catch (TimeoutException e) {
            logger.debug("No error message found within timeout period");
            return false;
        } catch (Exception e) {
            logger.warn("Error while checking for login error message: {}", e.getMessage());
            return false;
        }
    }

    private boolean isCaptchaPresent() {
        try {
            return !driver.findElements(captchaImage).isEmpty();
        } catch (Exception e) {
            logger.debug("Error checking for captcha presence: {}", e.getMessage());
            return false;
        }
    }
} 