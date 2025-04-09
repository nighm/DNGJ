import logging
import os
from datetime import datetime
from typing import Optional
from ..utils.config_reader import ConfigReader

class Logger:
    def __init__(self):
        """初始化日志记录器"""
        self.config = ConfigReader()
        self.logger = logging.getLogger('AssetManagement')
        self.setup_logger()

    def setup_logger(self):
        """设置日志记录器"""
        # 设置日志级别
        log_level = self.config.get_value('Logging', 'log_level', 'INFO')
        self.logger.setLevel(getattr(logging, log_level))

        # 创建日志目录
        log_file = self.config.get_value('Logging', 'log_file')
        log_dir = os.path.dirname(log_file)
        os.makedirs(log_dir, exist_ok=True)

        # 创建文件处理器
        file_handler = logging.FileHandler(log_file, encoding='utf-8')
        file_handler.setLevel(getattr(logging, log_level))

        # 创建控制台处理器
        console_handler = logging.StreamHandler()
        console_handler.setLevel(getattr(logging, log_level))

        # 设置日志格式
        log_format = self.config.get_value('Logging', 'log_format')
        date_format = self.config.get_value('Logging', 'log_date_format')
        formatter = logging.Formatter(log_format, date_format)
        file_handler.setFormatter(formatter)
        console_handler.setFormatter(formatter)

        # 添加处理器
        self.logger.addHandler(file_handler)
        self.logger.addHandler(console_handler)

    def log_step(self, message: str):
        """记录测试步骤
        
        Args:
            message: 步骤描述
        """
        self.logger.info(f"[STEP] {message}")

    def log_screenshot(self, screenshot_path: str):
        """记录截图
        
        Args:
            screenshot_path: 截图文件路径
        """
        self.logger.info(f"[SCREENSHOT] {screenshot_path}")

    def log_test_start(self, test_name: str):
        """记录测试开始
        
        Args:
            test_name: 测试名称
        """
        self.logger.info(f"[TEST START] {test_name}")

    def log_test_end(self, test_name: str, status: str, duration: float):
        """记录测试结束
        
        Args:
            test_name: 测试名称
            status: 测试状态（passed/failed）
            duration: 测试持续时间（秒）
        """
        self.logger.info(f"[TEST END] {test_name} - Status: {status} - Duration: {duration:.2f}s")

    def log_error(self, message: str, error: Optional[Exception] = None):
        """记录错误信息
        
        Args:
            message: 错误描述
            error: 异常对象（可选）
        """
        if error:
            self.logger.error(f"[ERROR] {message} - {str(error)}", exc_info=True)
        else:
            self.logger.error(f"[ERROR] {message}")

    def log_warning(self, message: str):
        """记录警告信息
        
        Args:
            message: 警告描述
        """
        self.logger.warning(f"[WARNING] {message}")

    def log_debug(self, message: str):
        """记录调试信息
        
        Args:
            message: 调试描述
        """
        self.logger.debug(f"[DEBUG] {message}")

    def log_info(self, message: str):
        """记录一般信息
        
        Args:
            message: 信息描述
        """
        self.logger.info(f"[INFO] {message}")

    def log_performance(self, operation: str, duration: float):
        """记录性能信息
        
        Args:
            operation: 操作名称
            duration: 操作持续时间（秒）
        """
        self.logger.info(f"[PERFORMANCE] {operation} - Duration: {duration:.2f}s")

    def log_validation(self, validation_name: str, status: str, details: str = ""):
        """记录验证结果
        
        Args:
            validation_name: 验证名称
            status: 验证状态（passed/failed）
            details: 详细信息（可选）
        """
        if details:
            self.logger.info(f"[VALIDATION] {validation_name} - Status: {status} - Details: {details}")
        else:
            self.logger.info(f"[VALIDATION] {validation_name} - Status: {status}")

    def debug(self, message: str, *args, **kwargs):
        """记录调试信息"""
        self.logger.debug(message, *args, **kwargs)
    
    def info(self, message: str, *args, **kwargs):
        """记录一般信息"""
        self.logger.info(message, *args, **kwargs)
    
    def warning(self, message: str, *args, **kwargs):
        """记录警告信息"""
        self.logger.warning(message, *args, **kwargs)
    
    def error(self, message: str, *args, **kwargs):
        """记录错误信息"""
        self.logger.error(message, *args, **kwargs)
    
    def critical(self, message: str, *args, **kwargs):
        """记录严重错误信息"""
        self.logger.critical(message, *args, **kwargs)
    
    def exception(self, message: str, *args, **kwargs):
        """记录异常信息"""
        self.logger.exception(message, *args, **kwargs)

    def log_element_action(self, element_name: str, action: str) -> None:
        """记录元素操作"""
        self.debug(f"元素操作: {element_name} - {action}") 