package com.assetmanagement.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

public class AssetPage {
    private static final Logger logger = LoggerFactory.getLogger(AssetPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//a[contains(text(),'资产管理')]")
    private WebElement assetManagementLink;

    @FindBy(xpath = "//div[contains(@class,'asset-list')]")
    private WebElement assetList;

    @FindBy(xpath = "//select[@id='searchType']")
    private WebElement searchTypeSelect;

    @FindBy(xpath = "//input[@id='searchValue']")
    private WebElement searchValueInput;

    @FindBy(xpath = "//button[contains(text(),'搜索')]")
    private WebElement searchButton;

    @FindBy(xpath = "//div[contains(@class,'no-results')]")
    private WebElement noResultsMessage;

    public AssetPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void navigateToAssetManagement() {
        logger.info("导航到资产管理页面");
        try {
            wait.until(ExpectedConditions.elementToBeClickable(assetManagementLink)).click();
            wait.until(ExpectedConditions.visibilityOf(assetList));
        } catch (Exception e) {
            logger.error("导航到资产管理页面失败", e);
            throw new RuntimeException("导航到资产管理页面失败", e);
        }
    }

    public void searchAsset(String searchType, String searchValue) {
        logger.info("搜索资产 - 类型: {}, 值: {}", searchType, searchValue);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(searchTypeSelect));
            org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(searchTypeSelect);
            select.selectByVisibleText(searchType);

            searchValueInput.clear();
            searchValueInput.sendKeys(searchValue);
            searchButton.click();
            
            // 等待搜索结果加载
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.error("搜索资产失败", e);
            throw new RuntimeException("搜索资产失败", e);
        }
    }

    public boolean isAssetFound(String searchValue) {
        logger.info("检查资产是否找到: {}", searchValue);
        try {
            return wait.until(ExpectedConditions.visibilityOf(assetList))
                    .getText().contains(searchValue);
        } catch (Exception e) {
            logger.error("检查资产是否找到失败", e);
            return false;
        }
    }

    public boolean isNoAssetFound() {
        logger.info("检查是否没有找到资产");
        try {
            return wait.until(ExpectedConditions.visibilityOf(noResultsMessage))
                    .isDisplayed();
        } catch (Exception e) {
            logger.error("检查是否没有找到资产失败", e);
            return false;
        }
    }

    public boolean isAssetListDisplayed() {
        logger.info("检查资产列表是否显示");
        try {
            return wait.until(ExpectedConditions.visibilityOf(assetList))
                    .isDisplayed();
        } catch (Exception e) {
            logger.error("检查资产列表显示状态失败", e);
            return false;
        }
    }
} 