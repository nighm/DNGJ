package com.assetmanagement.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private Properties properties;

    public ConfigReader() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config/config.properties"));
            logger.info("Configuration loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading configuration file", e);
            throw new RuntimeException("Failed to load configuration file", e);
        }
    }

    public String getBrowserType() {
        return "edge"; // 强制使用 Edge 浏览器
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("browser.headless", "false"));
    }

    public int getTimeout() {
        return Integer.parseInt(properties.getProperty("browser.timeout", "10"));
    }

    public String getBaseUrl() {
        return properties.getProperty("base.url", "http://192.168.30.240");
    }

    public String getUsername() {
        return properties.getProperty("admin.username", "super");
    }

    public String getPassword() {
        return properties.getProperty("admin.password", "admin123");
    }

    public String getWebDriverPath(String browser) {
        return properties.getProperty("webdriver." + browser + ".path");
    }

    public String getLogLevel() {
        return properties.getProperty("log.level", "INFO");
    }

    public String getLogFile() {
        return properties.getProperty("log.file", "test.log");
    }

    public String getTestDataDir() {
        return properties.getProperty("test.data.dir", "src/test/resources/testdata");
    }

    public String getLoginUrl() {
        String baseUrl = getBaseUrl();
        return baseUrl + "/asset/assetOver";
    }
} 