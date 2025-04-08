package com.assetmanagement.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "test-output/screenshots/";

    public static void takeScreenshot(WebDriver driver, String testName) {
        try {
            // 创建截图目录
            File directory = new File(SCREENSHOT_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 生成截图文件名
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            File screenshotFile = new File(SCREENSHOT_DIR + fileName);

            // 截图并保存
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, screenshotFile);
            logger.info("Screenshot saved: {}", screenshotFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to take screenshot", e);
        }
    }

    public static void takeScreenshotOnFailure(WebDriver driver, String testName, Throwable throwable) {
        logger.error("Test failed: {}", testName, throwable);
        takeScreenshot(driver, testName + "_FAILURE");
    }
} 