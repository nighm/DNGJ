package com.assetmanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "src/main/resources/config/config.properties";

    static {
        try {
            logger.info("加载配置文件: {}", CONFIG_FILE);
            properties.load(new FileInputStream(CONFIG_FILE));
        } catch (IOException e) {
            logger.error("加载配置文件失败", e);
            throw new RuntimeException("无法加载配置文件", e);
        }
    }

    private ConfigManager() {
        // 私有构造函数，防止实例化
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
} 