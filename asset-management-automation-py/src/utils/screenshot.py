import os
from datetime import datetime
from typing import Optional
from selenium.webdriver.remote.webdriver import WebDriver
from .config_reader import ConfigReader
from .logger import Logger

class Screenshot:
    def __init__(self, driver: WebDriver):
        """初始化截图工具
        
        Args:
            driver: WebDriver 实例
        """
        self.driver = driver
        self.config = ConfigReader()
        self.logger = Logger()
        self.screenshots_dir = self.config.get_value('Paths', 'screenshots_dir', 'reports/screenshots')
        
        # 确保截图目录存在
        os.makedirs(self.screenshots_dir, exist_ok=True)
    
    def take_screenshot(self, name: str) -> Optional[str]:
        """保存截图
        
        Args:
            name: 截图名称
            
        Returns:
            截图文件路径或None
        """
        try:
            # 生成带时间戳的文件名
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            filename = f"{name}_{timestamp}.png"
            filepath = os.path.join(self.screenshots_dir, filename)
            
            # 保存截图
            if self.driver.save_screenshot(filepath):
                self.logger.log_screenshot(filepath)
                return filepath
            return None
        except Exception as e:
            self.logger.error(f"截图失败: {str(e)}")
            return None
    
    def take_screenshot_on_failure(self, test_name: str) -> Optional[str]:
        """在测试失败时保存截图
        
        Args:
            test_name: 测试名称
            
        Returns:
            截图文件路径或None
        """
        return self.take_screenshot(f"failure_{test_name}")
    
    def take_screenshot_on_success(self, test_name: str) -> Optional[str]:
        """在测试成功时保存截图
        
        Args:
            test_name: 测试名称
            
        Returns:
            截图文件路径或None
        """
        return self.take_screenshot(f"success_{test_name}")
    
    def take_screenshot_on_error(self, test_name: str) -> Optional[str]:
        """在测试出错时保存截图
        
        Args:
            test_name: 测试名称
            
        Returns:
            截图文件路径或None
        """
        return self.take_screenshot(f"error_{test_name}") 