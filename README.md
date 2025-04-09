# 资产管理系统自动化测试项目

## 项目概述
本项目是一个基于Java的自动化测试框架，用于测试资产管理系统的功能。项目使用Selenium进行Web自动化测试，使用JMeter进行性能测试，并使用Allure生成测试报告。

## 技术栈
- Java (OpenJDK 21.0.6)
- Selenium WebDriver
- Microsoft Edge (v134.0.3124.95)
- JMeter (5.6.3)
- Maven
- TestNG
- Allure

## 项目结构
```
asset-management-automation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/assetmanagement/
│   │   │       ├── pages/        # 页面对象
│   │   │       ├── utils/        # 工具类
│   │   │       └── config/       # 配置类
│   │   └── resources/
│   │       ├── config/           # 配置文件
│   │       └── testdata/         # 测试数据
│   └── test/
│       ├── java/
│       │   └── com/assetmanagement/
│       │       ├── listeners/     # 测试监听器
│       │       └── tests/         # 测试用例
│       └── resources/
│           └── config/            # 测试配置文件
├── scripts/                       # Python脚本
├── docs/                          # 项目文档
├── jmeter/                        # JMeter测试脚本
├── test-output/                   # 测试报告输出
└── pom.xml                        # Maven配置文件
```

## 环境要求
- JDK 21+
- Maven 3.9+
- Microsoft Edge 134+
- Python 3.8+ (用于运行Python脚本)

## 快速开始
1. 克隆项目
2. 安装依赖：`mvn clean install`
3. 运行测试：`mvn test`
4. 生成报告：`mvn allure:report`

## 测试用例
- 登录功能测试
- 资产管理功能测试
- 性能测试

## 报告生成
测试报告使用Allure生成，位于 `test-output/allure-results` 目录下。

## 维护者
- 项目负责人：[姓名]
- 测试团队：[团队名称]

## 许可证
[许可证类型] 