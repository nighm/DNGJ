package com.assetmanagement.listeners;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.UUID;

public class AllureTestListener {
    private static final Logger logger = LoggerFactory.getLogger(AllureTestListener.class);

    public static void addScreenshot(WebDriver driver, String name) {
        try {
            Allure.addAttachment(
                name,
                new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES))
            );
            logger.info("Screenshot added to Allure report: {}", name);
        } catch (Exception e) {
            logger.error("Failed to add screenshot to Allure report", e);
        }
    }

    public static void addStep(String name, Runnable action) {
        StepResult step = new StepResult().setName(name);
        String uuid = UUID.randomUUID().toString();
        Allure.getLifecycle().startStep(uuid, step);

        try {
            action.run();
            Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.PASSED));
        } catch (Exception e) {
            Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.FAILED));
            logger.error("Step failed: {}", name, e);
            throw e;
        } finally {
            Allure.getLifecycle().stopStep(uuid);
        }
    }

    public static void addDescription(String description) {
        Allure.description(description);
        logger.info("Added description to Allure report: {}", description);
    }

    public static void addAttachment(String name, String content, String type) {
        Allure.addAttachment(name, type, content);
        logger.info("Added attachment to Allure report: {}", name);
    }
} 