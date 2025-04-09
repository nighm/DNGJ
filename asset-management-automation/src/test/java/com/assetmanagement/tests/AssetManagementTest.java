package com.assetmanagement.tests;

import com.assetmanagement.pages.AssetPage;
import com.assetmanagement.utils.ConfigReader;
import com.assetmanagement.utils.TestDataReader;
import com.assetmanagement.listeners.AllureTestListener;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Epic("资产管理")
@Feature("资产操作")
public class AssetManagementTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(AssetManagementTest.class);
    private ConfigReader config;
    private AssetPage assetPage;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        config = new ConfigReader();
        assetPage = new AssetPage(driver);
    }

    @Test
    @Story("资产列表查看")
    @Description("验证资产列表显示功能")
    @Severity(SeverityLevel.BLOCKER)
    public void testAssetListDisplay() {
        logger.info("开始资产列表显示测试");
        try {
            // 登录系统
            loginToSystem();
            
            // 进入资产管理模块
            assetPage.navigateToAssetManagement();
            
            // 验证资产列表显示
            assertTrue(assetPage.isAssetListDisplayed(), "资产列表未正确显示");
            logger.info("资产列表显示测试成功完成");
            
        } catch (Exception e) {
            logger.error("资产列表显示测试过程中发生错误: {}", e.getMessage());
            throw e;
        }
    }

    @ParameterizedTest
    @MethodSource("provideAssetTestData")
    @DisplayName("测试资产搜索")
    @Description("验证资产搜索功能")
    @Story("资产搜索")
    public void testAssetSearch(Map<String, String> testData) {
        try {
            String searchType = testData.get("search_type");
            String searchValue = testData.get("search_value");
            String expectedResult = testData.get("expected_result");
            
            logger.info("开始资产搜索测试 - 搜索类型: {}, 搜索值: {}", searchType, searchValue);
            
            AllureTestListener.addDescription(String.format("搜索类型: %s, 搜索值: %s, 预期结果: %s", 
                searchType, searchValue, expectedResult));
            
            // 登录系统
            loginToSystem();
            
            // 进入资产管理模块
            assetPage.navigateToAssetManagement();
            
            // 执行搜索
            assetPage.searchAsset(searchType, searchValue);
            
            // 验证搜索结果
            if ("found".equals(expectedResult)) {
                assertTrue(assetPage.isAssetFound(searchValue), "资产应该被找到但未找到");
            } else {
                assertTrue(assetPage.isNoAssetFound(), "资产不应该被找到但找到了");
            }
            
            AllureTestListener.addScreenshot(driver, String.format("资产搜索测试 - %s", searchType));
            logger.info("资产搜索测试通过 - 搜索类型: {}", searchType);
            
        } catch (Exception e) {
            logger.error("资产搜索测试失败 - 搜索类型: {}", testData.get("search_type"), e);
            AllureTestListener.addScreenshot(driver, String.format("资产搜索失败 - %s", testData.get("search_type")));
            takeScreenshotOnFailure(e);
            throw e;
        }
    }

    private void loginToSystem() {
        // 实现登录逻辑
        // 这里可以调用LoginPage的登录方法
    }

    private static Stream<Map<String, String>> provideAssetTestData() {
        List<Map<String, String>> testData = TestDataReader.readCsvData("assets.csv");
        return testData.stream();
    }
} 