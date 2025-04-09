# 资产管理系统自动化测试项目 (Python版)

## 项目简介
本项目是资产管理系统的自动化测试框架，使用Python + Selenium实现。主要功能包括：
- 登录功能测试
- 资产管理功能测试
- 权限管理测试
- 性能测试

## 技术栈
- Python 3.8+
- Selenium 4
- Pytest
- Allure报告框架
- Edge WebDriver

## 项目结构
```
asset-management-automation-py/
├── src/                    # 源代码目录
│   ├── pages/             # 页面对象
│   ├── utils/             # 工具类
│   └── config/            # 配置类
├── tests/                 # 测试用例
├── docs/                  # 项目文档
├── resources/             # 资源文件
│   ├── config/           # 配置文件
│   └── testdata/         # 测试数据
├── reports/              # 测试报告
│   ├── screenshots/      # 测试截图
│   └── allure-results/   # Allure报告
├── requirements.txt      # 项目依赖
└── README.md            # 项目说明
```

## 环境配置
1. 安装Python 3.8+
2. 安装依赖：
   ```bash
   pip install -r requirements.txt
   ```
3. 安装Edge浏览器和对应版本的WebDriver

## 运行测试
1. 运行所有测试：
   ```bash
   pytest
   ```

2. 运行特定测试：
   ```bash
   pytest tests/test_login.py
   ```

3. 生成Allure报告：
   ```bash
   pytest --alluredir=./reports/allure-results
   allure serve ./reports/allure-results
   ```

## 注意事项
1. 运行测试前请确保配置文件正确
2. 确保Edge浏览器和WebDriver版本匹配
3. 测试失败时会自动截图保存 