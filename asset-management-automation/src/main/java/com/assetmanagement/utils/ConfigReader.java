package com.assetmanagement.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class 



ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private Properties properties;
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

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

    public String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key, defaultValue);
        return resolveVariables(value);
    }

    private String resolveVariables(String value) {
        if (value == null) {
            return null;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(value);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = properties.getProperty(varName);
            
            if (replacement == null) {
                logger.warn("Variable ${" + varName + "} not found in properties");
                replacement = matcher.group(0); // 保持原样
            } else {
                // 递归解析变量
                replacement = resolveVariables(replacement);
            }
            
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        
        matcher.appendTail(result);
        return result.toString();
    }

    public String getBrowserType() {
        return getProperty("browser.type", "edge");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("browser.headless", "false"));
    }

    public int getTimeout() {
        return Integer.parseInt(getProperty("browser.timeout", "30"));
    }

    public String getBaseUrl() {
        return getProperty("base.url", "http://192.168.30.240");
    }

    public String getUsername() {
        return getProperty("admin.username", "super");
    }

    public String getPassword() {
        return getProperty("admin.password", "admin123");
    }

    public String getWebDriverPath(String browser) {
        return getProperty("webdriver." + browser + ".driver", "");
    }

    public String getLogLevel() {
        return getProperty("log.level", "INFO");
    }

    public String getLogFile() {
        return getProperty("log.file", "test.log");
    }

    public String getTestDataDir() {
        return getProperty("test.data.dir", "src/test/resources/testdata");
    }

    public String getLoginUrl() {
        String baseUrl = getBaseUrl();
        return baseUrl + "/login";
    }

    public String getScreenshotDir() {
        return getProperty("screenshot.dir", "logs/screenshots/");
    }

    public String getScreenshotFormat() {
        return getProperty("screenshot.format", "png");
    }

    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure", "true"));
    }
} 