import pytest
import allure
from typing import List
from datetime import datetime
from selenium.webdriver import Edge
from selenium.webdriver.edge.service import Service
from selenium.webdriver.edge.options import Options
from webdriver_manager.microsoft import EdgeChromiumDriverManager

from src.pages.login_page import LoginPage
from src.utils.test_data_reader import TestDataReader, UserData
from src.utils.logger import Logger

@allure.epic("资产管理系统")
@allure.feature("登录功能")
class TestLogin:
    @pytest.fixture(autouse=True)
    def setup(self):
        """测试前置设置"""
        self.logger = Logger()
        self.logger.log_test_start("登录功能测试")
        
        # 初始化Edge浏览器驱动
        service = Service(EdgeChromiumDriverManager().install())
        options = Options()
        
        # 处理证书错误
        options.set_capability('acceptInsecureCerts', True)
        options.add_argument('--ignore-certificate-errors')
        options.add_argument('--ignore-ssl-errors')
        options.add_argument('--start-maximized')
        
        self.driver = Edge(service=service, options=options)
        
        # 初始化页面对象
        self.login_page = LoginPage(self.driver)
        self.test_data = TestDataReader()
        
        yield
        
        # 测试后清理
        if hasattr(self, 'driver'):
            self.login_page.logout()
            self.driver.quit()
            self.logger.log_test_end("登录功能测试", "completed", 0)

    @allure.story("正常登录")
    @allure.title("管理员登录测试")
    def test_admin_login(self):
        """测试管理员登录"""
        admin_user = self.test_data.get_admin_user()
        assert admin_user is not None, "未找到管理员用户数据"
        
        with allure.step("执行管理员登录"):
            start_time = datetime.now()
            result = self.login_page.login(
                username=admin_user.username,
                password=admin_user.password
            )
            duration = (datetime.now() - start_time).total_seconds()
            self.logger.log_performance("管理员登录", duration)
            assert result, "管理员登录失败"

    @allure.story("正常登录")
    @allure.title("普通用户登录测试")
    def test_normal_user_login(self):
        """测试普通用户登录"""
        normal_user = self.test_data.get_normal_user()
        assert normal_user is not None, "未找到普通用户数据"
        
        with allure.step("执行普通用户登录"):
            start_time = datetime.now()
            result = self.login_page.login(
                username=normal_user.username,
                password=normal_user.password
            )
            duration = (datetime.now() - start_time).total_seconds()
            self.logger.log_performance("普通用户登录", duration)
            assert result, "普通用户登录失败"

    @allure.story("异常登录")
    @allure.title("错误密码登录测试")
    def test_wrong_password_login(self):
        """测试错误密码登录"""
        test_users = self.test_data.get_test_users()
        assert len(test_users) > 0, "未找到测试用户数据"
        
        test_user = test_users[0]  # 使用第一个测试用户
        with allure.step("执行错误密码登录"):
            result = self.login_page.login(
                username=test_user.username,
                password="wrong_password"
            )
            assert not result, "错误密码登录不应该成功"
            error_message = self.login_page.get_error_message()
            assert error_message is not None, "未显示错误信息"
            self.logger.log_validation("错误密码登录", "passed", error_message)

    @allure.story("异常登录")
    @allure.title("空用户名登录测试")
    def test_empty_username_login(self):
        """测试空用户名登录"""
        with allure.step("执行空用户名登录"):
            result = self.login_page.login(
                username="",
                password="any_password"
            )
            assert not result, "空用户名登录不应该成功"
            error_message = self.login_page.get_error_message()
            assert error_message is not None, "未显示错误信息"
            self.logger.log_validation("空用户名登录", "passed", error_message)

    @allure.story("异常登录")
    @allure.title("空密码登录测试")
    def test_empty_password_login(self):
        """测试空密码登录"""
        test_users = self.test_data.get_test_users()
        assert len(test_users) > 0, "未找到测试用户数据"
        
        test_user = test_users[0]
        with allure.step("执行空密码登录"):
            result = self.login_page.login(
                username=test_user.username,
                password=""
            )
            assert not result, "空密码登录不应该成功"
            error_message = self.login_page.get_error_message()
            assert error_message is not None, "未显示错误信息"
            self.logger.log_validation("空密码登录", "passed", error_message)

    @allure.story("会话管理")
    @allure.title("登录状态保持测试")
    def test_login_session(self):
        """测试登录状态保持"""
        admin_user = self.test_data.get_admin_user()
        assert admin_user is not None, "未找到管理员用户数据"
        
        with allure.step("执行登录"):
            result = self.login_page.login(
                username=admin_user.username,
                password=admin_user.password
            )
            assert result, "登录失败"
        
        with allure.step("刷新页面"):
            self.login_page.refresh_page()
            assert self.login_page.is_login_successful(), "登录状态未保持"
            self.logger.log_validation("登录状态保持", "passed")

    @allure.story("会话管理")
    @allure.title("登出功能测试")
    def test_logout(self):
        """测试登出功能"""
        admin_user = self.test_data.get_admin_user()
        assert admin_user is not None, "未找到管理员用户数据"
        
        with allure.step("执行登录"):
            result = self.login_page.login(
                username=admin_user.username,
                password=admin_user.password
            )
            assert result, "登录失败"
        
        with allure.step("执行登出"):
            result = self.login_page.logout()
            assert result, "登出失败"
            assert not self.login_page.is_login_successful(), "登出后仍保持登录状态"
            self.logger.log_validation("登出功能", "passed")

    @allure.story("验证码测试")
    @allure.title("错误验证码登录测试")
    def test_wrong_captcha_login(self):
        """测试错误验证码登录"""
        admin_user = self.test_data.get_admin_user()
        assert admin_user is not None, "未找到管理员用户数据"
        
        with allure.step("执行错误验证码登录"):
            result = self.login_page.login(
                username=admin_user.username,
                password=admin_user.password,
                captcha="0000"  # 错误的验证码
            )
            assert not result, "错误验证码登录不应该成功"
            error_message = self.login_page.get_error_message()
            assert error_message is not None, "未显示错误信息"
            self.logger.log_validation("错误验证码登录", "passed", error_message)

    @allure.story("验证码测试")
    @allure.title("空验证码登录测试")
    def test_empty_captcha_login(self):
        """测试空验证码登录"""
        admin_user = self.test_data.get_admin_user()
        assert admin_user is not None, "未找到管理员用户数据"
        
        with allure.step("执行空验证码登录"):
            result = self.login_page.login(
                username=admin_user.username,
                password=admin_user.password,
                captcha=""  # 空的验证码
            )
            assert not result, "空验证码登录不应该成功"
            error_message = self.login_page.get_error_message()
            assert error_message is not None, "未显示错误信息"
            self.logger.log_validation("空验证码登录", "passed", error_message) 