package com.assetmanagement.tests;

import com.assetmanagement.utils.ConfigReader;
import com.assetmanagement.utils.ScreenshotUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {
    protected WebDriver driver;
    protected ConfigReader config;
    protected ScreenshotUtils screenshotUtils;
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @BeforeEach
    public void setUp() {
        config = new ConfigReader();
        boolean headless = config.isHeadless();
        int timeout = config.getTimeout();

        WebDriverManager.edgedriver().setup();
        EdgeOptions edgeOptions = new EdgeOptions();
        
        // 处理证书错误
        edgeOptions.setAcceptInsecureCerts(true);
        edgeOptions.addArguments("--ignore-certificate-errors");
        edgeOptions.addArguments("--ignore-ssl-errors");
        
        if (headless) {
            edgeOptions.addArguments("--headless");
        }
        
        driver = new EdgeDriver(edgeOptions);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(timeout, java.util.concurrent.TimeUnit.SECONDS);
        
        // 初始化截图工具
        screenshotUtils = new ScreenshotUtils(driver, config);
        
        logger.info("Edge WebDriver initialized with SSL certificate handling");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                screenshotUtils.takeScreenshot(this.getClass().getSimpleName());
            } catch (Exception e) {
                logger.error("Failed to take screenshot", e);
            }
            driver.quit();
            logger.info("Edge WebDriver closed");
        }
    }

    protected void takeScreenshotOnFailure(Throwable throwable) {
        try {
            String prefix = String.format("%s_failure_%s",
                this.getClass().getSimpleName(),
                throwable.getClass().getSimpleName());
            screenshotUtils.takeScreenshot(prefix);
        } catch (Exception e) {
            logger.error("Failed to take failure screenshot", e);
        }
    }
}