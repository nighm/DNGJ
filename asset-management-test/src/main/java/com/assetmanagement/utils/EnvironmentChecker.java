package com.assetmanagement.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentChecker {
    private static final List<String> errors = new ArrayList<>();
    private static final List<String> warnings = new ArrayList<>();

    public static void checkEnvironment() {
        errors.clear();
        warnings.clear();

        // 检查Java环境
        checkJavaEnvironment();
        
        // 检查Maven环境
        checkMavenEnvironment();
        
        // 检查浏览器环境
        checkBrowserEnvironment();
        
        // 检查WebDriver
        checkWebDriver();
        
        // 检查配置文件
        checkConfigFiles();
        
        // 输出检查结果
        printCheckResults();
    }

    private static void checkJavaEnvironment() {
        String javaVersion = System.getProperty("java.version");
        if (javaVersion == null) {
            errors.add("Java环境未安装");
        } else {
            System.out.println("Java版本: " + javaVersion);
        }
    }

    private static void checkMavenEnvironment() {
        String mavenHome = System.getenv("M2_HOME");
        if (mavenHome == null) {
            errors.add("Maven环境未配置");
        } else {
            System.out.println("Maven路径: " + mavenHome);
        }
    }

    private static void checkBrowserEnvironment() {
        // 检查Edge浏览器
        if (isEdgeInstalled()) {
            System.out.println("Edge浏览器已安装");
        } else {
            errors.add("Edge浏览器未安装");
        }

        // 检查Chrome浏览器
        if (isChromeInstalled()) {
            System.out.println("Chrome浏览器已安装");
        } else {
            warnings.add("Chrome浏览器未安装");
        }

        // 检查Firefox浏览器
        if (isFirefoxInstalled()) {
            System.out.println("Firefox浏览器已安装");
        } else {
            warnings.add("Firefox浏览器未安装");
        }
    }

    private static void checkWebDriver() {
        // 检查Edge WebDriver
        if (isEdgeDriverInstalled()) {
            System.out.println("Edge WebDriver已安装");
        } else {
            errors.add("Edge WebDriver未安装");
        }

        // 检查Chrome WebDriver
        if (isChromeDriverInstalled()) {
            System.out.println("Chrome WebDriver已安装");
        } else {
            warnings.add("Chrome WebDriver未安装");
        }

        // 检查Firefox WebDriver
        if (isFirefoxDriverInstalled()) {
            System.out.println("Firefox WebDriver已安装");
        } else {
            warnings.add("Firefox WebDriver未安装");
        }
    }

    private static void checkConfigFiles() {
        String[] requiredConfigFiles = {
            "src/main/resources/config/config.properties",
            "src/main/resources/config/testdata.properties"
        };

        for (String configFile : requiredConfigFiles) {
            Path path = Paths.get(configFile);
            if (!Files.exists(path)) {
                errors.add("配置文件不存在: " + configFile);
            } else {
                System.out.println("配置文件存在: " + configFile);
            }
        }
    }

    private static boolean isEdgeInstalled() {
        String[] edgePaths = {
            "/usr/bin/microsoft-edge",
            "/usr/bin/microsoft-edge-stable",
            "/usr/bin/msedge"
        };
        return checkExecutableExists(edgePaths);
    }

    private static boolean isChromeInstalled() {
        String[] chromePaths = {
            "/usr/bin/google-chrome",
            "/usr/bin/google-chrome-stable",
            "/usr/bin/chromium"
        };
        return checkExecutableExists(chromePaths);
    }

    private static boolean isFirefoxInstalled() {
        String[] firefoxPaths = {
            "/usr/bin/firefox",
            "/usr/bin/firefox-esr"
        };
        return checkExecutableExists(firefoxPaths);
    }

    private static boolean isEdgeDriverInstalled() {
        String[] edgeDriverPaths = {
            "/usr/bin/msedgedriver",
            "/usr/local/bin/msedgedriver"
        };
        return checkExecutableExists(edgeDriverPaths);
    }

    private static boolean isChromeDriverInstalled() {
        String[] chromeDriverPaths = {
            "/usr/bin/chromedriver",
            "/usr/local/bin/chromedriver"
        };
        return checkExecutableExists(chromeDriverPaths);
    }

    private static boolean isFirefoxDriverInstalled() {
        String[] firefoxDriverPaths = {
            "/usr/bin/geckodriver",
            "/usr/local/bin/geckodriver"
        };
        return checkExecutableExists(firefoxDriverPaths);
    }

    private static boolean checkExecutableExists(String[] paths) {
        for (String path : paths) {
            File file = new File(path);
            if (file.exists() && file.canExecute()) {
                return true;
            }
        }
        return false;
    }

    private static void printCheckResults() {
        System.out.println("\n环境检查结果:");
        System.out.println("=============");
        
        if (errors.isEmpty() && warnings.isEmpty()) {
            System.out.println("✓ 所有环境检查通过");
            return;
        }

        if (!errors.isEmpty()) {
            System.out.println("\n错误:");
            for (String error : errors) {
                System.out.println("✗ " + error);
            }
        }

        if (!warnings.isEmpty()) {
            System.out.println("\n警告:");
            for (String warning : warnings) {
                System.out.println("! " + warning);
            }
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException("环境检查失败，请解决上述错误后重试");
        }
    }
} 