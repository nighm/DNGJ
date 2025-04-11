from setuptools import setup, find_packages

setup(
    name="auto-login-demo",
    version="0.1.0",
    packages=find_packages(),
    install_requires=[
        "selenium>=4.16.0",
        "msedge-selenium-tools>=3.141.4",
        "python-dotenv>=1.0.0",
        "pillow>=10.2.0",
        "easyocr>=1.7.1",
        "psutil>=5.9.8"
    ],
    python_requires=">=3.8",
    author="Your Name",
    author_email="your.email@example.com",
    description="自动化登录测试项目",
    long_description=open("README.md").read(),
    long_description_content_type="text/markdown",
    classifiers=[
        "Development Status :: 3 - Alpha",
        "Intended Audience :: Developers",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
    ],
)
