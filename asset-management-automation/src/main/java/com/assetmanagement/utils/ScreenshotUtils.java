package com.assetmanagement.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private final WebDriver driver;
    private final ConfigReader config;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private final String screenshotDir;

    public ScreenshotUtils(WebDriver driver, ConfigReader config) {
        this.driver = driver;
        this.config = config;
        this.screenshotDir = config.getProperty("screenshot.dir", "logs/screenshots/");
        createScreenshotDirectory();
    }

    private void createScreenshotDirectory() {
        try {
            Path dirPath = Paths.get(screenshotDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                logger.info("Created screenshot directory: {}", dirPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create screenshot directory: {}", e.getMessage());
        }
    }

    public void takeScreenshot(String prefix) {
        if (driver == null) {
            logger.error("WebDriver is null, cannot take screenshot");
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            String fileName = String.format("%s_%s.png", prefix, timestamp);
            Path screenshotPath = Paths.get(screenshotDir, fileName);

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), screenshotPath);
            
            logger.info("Screenshot saved: {}", screenshotPath);
            
        } catch (IOException e) {
            logger.error("Failed to save screenshot: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to take screenshot: {}", e.getMessage());
        }
    }

    public void takeScreenshotOnFailure(String testName, Throwable throwable) {
        String prefix = String.format("%s_FAILURE_%s", 
            testName, 
            throwable.getClass().getSimpleName());
        takeScreenshot(prefix);
    }
} 