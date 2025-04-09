from selenium.webdriver.common.by import By
from selenium.webdriver.remote.webelement import WebElement
from typing import Optional, Tuple
import time
import os
from datetime import datetime

from .base_page import BasePage
from ..utils.config_reader import ConfigReader
from ..utils.logger import Logger

class LoginPage(BasePage):
    # 元素定位器
    USERNAME_INPUT = (By.CSS_SELECTOR, "input[placeholder='用户名']")
    PASSWORD_INPUT = (By.CSS_SELECTOR, "input[placeholder='密码']")
    CAPTCHA_INPUT = (By.CSS_SELECTOR, "input[placeholder='验证码']")
    LOGIN_BUTTON = (By.CSS_SELECTOR, "button.el-button--primary")
    ERROR_MESSAGE = (By.CSS_SELECTOR, "div.el-message__content")
    CAPTCHA_IMAGE = (By.CSS_SELECTOR, "img.captcha-image")

    def __init__(self, driver, config_reader=None):
        """初始化登录页面
        
        Args:
            driver: WebDriver 实例
            config_reader: 配置读取器实例，如果为None则创建新实例
        """
        if config_reader is None:
            config_reader = ConfigReader()
        super().__init__(driver, config_reader)
        self.logger = Logger()
        self.base_url = self.config_reader.get_value('Environment', 'base_url')
        self.screenshots_dir = self.config_reader.get_value('Paths', 'screenshots_dir')
        os.makedirs(self.screenshots_dir, exist_ok=True)

    def take_screenshot(self, scenario: str, status: str = "error") -> Optional[str]:
        """保存截图
        
        Args:
            scenario: 测试场景名称
            status: 截图状态（error/success）
            
        Returns:
            截图文件路径或None
        """
        try:
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            filename = f"login_{scenario}_{status}_{timestamp}.png"
            filepath = os.path.join(self.screenshots_dir, filename)
            
            if self.driver.save_screenshot(filepath):
                self.logger.log_screenshot(filepath)
                return filepath
            return None
        except Exception as e:
            self.logger.error(f"截图失败: {str(e)}")
            return None

    def navigate_to_login_page(self) -> bool:
        """导航到登录页面
        
        Returns:
            是否成功导航到登录页面
        """
        try:
            self.logger.log_step("导航到登录页面")
            self.navigate_to(f"{self.base_url}/login")
            if self.wait_for_page_load():
                self.take_screenshot("navigate", "success")
                return True
            return False
        except Exception as e:
            self.logger.error(f"导航到登录页面失败: {str(e)}")
            self.take_screenshot("navigate", "error")
            return False

    def input_username(self, username: str) -> bool:
        """输入用户名
        
        Args:
            username: 用户名
            
        Returns:
            是否输入成功
        """
        try:
            self.logger.log_step(f"输入用户名: {username}")
            self.send_keys(self.USERNAME_INPUT, username)
            return True
        except Exception as e:
            self.logger.error(f"输入用户名失败: {str(e)}")
            self.take_screenshot("input_username", "error")
            return False

    def input_password(self, password: str) -> bool:
        """输入密码
        
        Args:
            password: 密码
            
        Returns:
            是否输入成功
        """
        try:
            self.logger.log_step("输入密码")
            self.send_keys(self.PASSWORD_INPUT, password)
            return True
        except Exception as e:
            self.logger.error(f"输入密码失败: {str(e)}")
            self.take_screenshot("input_password", "error")
            return False

    def input_captcha(self, captcha: str) -> bool:
        """输入验证码
        
        Args:
            captcha: 验证码
            
        Returns:
            是否输入成功
        """
        try:
            self.logger.log_step(f"输入验证码: {captcha}")
            self.send_keys(self.CAPTCHA_INPUT, captcha)
            return True
        except Exception as e:
            self.logger.error(f"输入验证码失败: {str(e)}")
            self.take_screenshot("input_captcha", "error")
            return False

    def click_login_button(self) -> bool:
        """点击登录按钮
        
        Returns:
            是否点击成功
        """
        try:
            self.logger.log_step("点击登录按钮")
            self.click(self.LOGIN_BUTTON)
            return True
        except Exception as e:
            self.logger.error(f"点击登录按钮失败: {str(e)}")
            self.take_screenshot("click_login", "error")
            return False

    def get_error_message(self) -> Optional[str]:
        """获取错误信息
        
        Returns:
            错误信息或None
        """
        try:
            if self.is_element_present(self.ERROR_MESSAGE):
                error_text = self.get_text(self.ERROR_MESSAGE)
                self.take_screenshot("error_message", "error")
                return error_text
            return None
        except Exception as e:
            self.logger.error(f"获取错误信息失败: {str(e)}")
            self.take_screenshot("get_error", "error")
            return None

    def is_login_successful(self) -> bool:
        """检查是否登录成功
        
        Returns:
            是否登录成功
        """
        try:
            # 检查是否跳转到首页
            current_url = self.get_current_url()
            is_success = current_url.endswith('/dashboard') or current_url.endswith('/index')
            if is_success:
                self.take_screenshot("login_success", "success")
            return is_success
        except Exception as e:
            self.logger.error(f"检查登录状态失败: {str(e)}")
            self.take_screenshot("check_login_status", "error")
            return False

    def login(self, username: str, password: str, captcha: Optional[str] = None) -> bool:
        """执行登录操作
        
        Args:
            username: 用户名
            password: 密码
            captcha: 验证码（可选）
            
        Returns:
            是否登录成功
        """
        try:
            self.logger.log_step(f"开始登录流程 - 用户名: {username}")
            
            # 导航到登录页面
            if not self.navigate_to_login_page():
                return False
            
            # 输入用户名和密码
            if not self.input_username(username):
                return False
            if not self.input_password(password):
                return False
            
            # 处理验证码
            if captcha is not None:
                if not self.input_captcha(captcha):
                    return False
            
            # 点击登录按钮
            if not self.click_login_button():
                return False
            
            # 等待登录结果
            self.wait_for_page_load()
            
            # 检查登录结果
            if self.is_login_successful():
                self.logger.log_step("登录成功")
                return True
            else:
                error_message = self.get_error_message()
                if error_message:
                    self.logger.error(f"登录失败: {error_message}")
                else:
                    self.logger.error("登录失败: 未知错误")
                self.take_screenshot("login_failure", "error")
                return False
                
        except Exception as e:
            self.logger.error(f"登录过程发生异常: {str(e)}")
            self.take_screenshot("login_exception", "error")
            return False

    def logout(self) -> bool:
        """执行登出操作
        
        Returns:
            是否登出成功
        """
        try:
            self.logger.log_step("执行登出操作")
            # TODO: 实现登出逻辑
            self.take_screenshot("logout", "success")
            return True
        except Exception as e:
            self.logger.error(f"登出失败: {str(e)}")
            self.take_screenshot("logout", "error")
            return False 