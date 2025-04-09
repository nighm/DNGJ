from selenium.webdriver.remote.webdriver import WebDriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException, NoSuchElementException
from typing import Optional, Tuple, Union, List
import logging
import os
from datetime import datetime

from ..utils.webdriver_manager import WebDriverManager
from ..utils.logger import Logger

class BasePage:
    def __init__(self, driver: WebDriver, config_reader):
        """初始化页面对象
        
        Args:
            driver: WebDriver 实例
            config_reader: 配置读取器实例
        """
        self.driver = driver
        self.config_reader = config_reader
        self.logger = logging.getLogger(self.__class__.__name__)
        self.wait = WebDriverWait(
            driver,
            self.config_reader.get_value('Environment', 'explicit_wait', 10)
        )

    def find_element(self, locator: Tuple[By, str]) -> WebDriver:
        """查找单个元素
        
        Args:
            locator: 元素定位器 (By, value)
            
        Returns:
            找到的元素
            
        Raises:
            NoSuchElementException: 元素未找到
        """
        try:
            return self.wait.until(EC.presence_of_element_located(locator))
        except TimeoutException:
            self.logger.error(f"元素未找到: {locator}")
            raise NoSuchElementException(f"元素未找到: {locator}")

    def find_elements(self, locator: Tuple[By, str]) -> List[WebDriver]:
        """查找多个元素
        
        Args:
            locator: 元素定位器 (By, value)
            
        Returns:
            找到的元素列表
        """
        try:
            return self.wait.until(EC.presence_of_all_elements_located(locator))
        except TimeoutException:
            self.logger.warning(f"未找到任何元素: {locator}")
            return []

    def click(self, locator: Tuple[By, str]) -> None:
        """点击元素
        
        Args:
            locator: 元素定位器 (By, value)
        """
        element = self.find_element(locator)
        element.click()

    def send_keys(self, locator: Tuple[By, str], text: str) -> None:
        """向元素输入文本
        
        Args:
            locator: 元素定位器 (By, value)
            text: 要输入的文本
        """
        element = self.find_element(locator)
        element.clear()
        element.send_keys(text)

    def get_text(self, locator: Tuple[By, str]) -> str:
        """获取元素文本
        
        Args:
            locator: 元素定位器 (By, value)
            
        Returns:
            元素文本
        """
        element = self.find_element(locator)
        return element.text

    def is_element_present(self, locator: Tuple[By, str]) -> bool:
        """检查元素是否存在
        
        Args:
            locator: 元素定位器 (By, value)
            
        Returns:
            元素是否存在
        """
        try:
            self.find_element(locator)
            return True
        except NoSuchElementException:
            return False

    def wait_for_element_visible(self, locator: Tuple[By, str]) -> None:
        """等待元素可见
        
        Args:
            locator: 元素定位器 (By, value)
        """
        self.wait.until(EC.visibility_of_element_located(locator))

    def wait_for_element_clickable(self, locator: Tuple[By, str]) -> None:
        """等待元素可点击
        
        Args:
            locator: 元素定位器 (By, value)
        """
        self.wait.until(EC.element_to_be_clickable(locator))

    def get_current_url(self) -> str:
        """获取当前页面 URL
        
        Returns:
            当前页面 URL
        """
        return self.driver.current_url

    def get_page_title(self) -> str:
        """获取页面标题
        
        Returns:
            页面标题
        """
        return self.driver.title

    def navigate_to(self, url: str) -> None:
        """导航到指定 URL
        
        Args:
            url: 目标 URL
        """
        self.driver.get(url)

    def refresh_page(self) -> None:
        """刷新当前页面"""
        self.driver.refresh()

    def take_screenshot(self, filename: str) -> bool:
        """截取页面截图
        
        Args:
            filename: 截图文件名
            
        Returns:
            截图是否成功
        """
        try:
            self.driver.save_screenshot(filename)
            self.logger.info(f"截图已保存: {filename}")
            return True
        except Exception as e:
            self.logger.error(f"截图失败: {str(e)}")
            return False

    def wait_for_page_load(self) -> bool:
        """等待页面加载完成
        
        Returns:
            页面是否加载完成
        """
        try:
            self.wait.until(
                lambda driver: driver.execute_script('return document.readyState') == 'complete'
            )
            return True
        except TimeoutException:
            self.logger.error("页面加载超时")
            return False

    def take_screenshot(self, name: str) -> Optional[str]:
        """保存截图
        
        Args:
            name: 截图名称
            
        Returns:
            截图文件路径或None
        """
        try:
            screenshots_dir = self.config_reader.get_value('Paths', 'screenshots_dir')
            os.makedirs(screenshots_dir, exist_ok=True)
            
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            filename = f"{name}_{timestamp}.png"
            filepath = os.path.join(screenshots_dir, filename)
            
            if self.driver.save_screenshot(filepath):
                self.logger.log_screenshot(filepath)
                return filepath
            return None
        except Exception as e:
            self.logger.error(f"截图失败: {str(e)}")
            return None 