package com.assetmanagement.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class WebDriverManager {
    private static final Logger logger = LoggerFactory.getLogger(WebDriverManager.class);
    private static WebDriver driver;
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream("src/main/resources/config/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            logger.error("Error loading config.properties", e);
            throw new RuntimeException("Failed to load config.properties");
        }
    }

    private WebDriverManager() {
        // 私有构造函数，防止实例化
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            logger.info("初始化WebDriver...");
            io.github.bonigarcia.wdm.WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--remote-allow-origins=*");
            driver = new EdgeDriver(options);
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            logger.info("关闭WebDriver...");
            driver.quit();
            driver = null;
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
} 