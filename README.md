# Auto Login Demo

自动化登录测试项目

## 项目描述

这是一个用于自动化登录测试的 Python 项目，主要功能包括：
- 自动化登录流程
- 验证码处理
- 错误处理和日志记录
- 性能监控

## 安装

1. 克隆项目：
```bash
git clone [项目地址]
cd auto-login-demo
```

2. 创建虚拟环境：
```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
# 或
.\venv\Scripts\activate  # Windows
```

3. 安装依赖：
```bash
pip install -r requirements.txt
```

## 配置

1. 复制环境变量模板：
```bash
cp .env.example .env
```

2. 编辑 `.env` 文件，设置必要的配置项：
- LOGIN_URL: 登录页面URL
- USERNAME: 用户名
- PASSWORD: 密码
- 其他配置项...

## 使用

运行主程序：
```bash
python src/main.py
```

## 项目结构

```
auto-login-demo/
├── src/                # 源代码目录
│   ├── config/        # 配置文件
│   ├── pages/         # 页面对象
│   └── utils/         # 工具类
├── tests/             # 测试目录
├── docs/              # 文档目录
├── logs/              # 日志目录
├── screenshots/       # 截图目录
├── .env              # 环境变量
├── .gitignore        # Git忽略文件
├── requirements.txt   # 项目依赖
├── setup.py          # 项目安装配置
└── README.md         # 项目说明
```

## 开发规范

- 代码风格遵循 PEP 8
- 使用类型注解
- 编写单元测试
- 提交前运行代码检查

## 许可证

MIT License
