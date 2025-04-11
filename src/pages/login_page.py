from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

class LoginPage:
    def __init__(self, url, username, password):
        self.url = url
        self.username = username
        self.password = password
        self.driver = None

    def start_browser(self):
        """启动浏览器并打开指定URL"""
        self.driver = webdriver.Edge()
        self.driver.get(self.url)
        self.driver.maximize_window()

    def login(self):
        """执行登录操作"""
        try:
            # 等待页面加载
            WebDriverWait(self.driver, 10).until(
                EC.presence_of_element_located((By.ID, "username"))
            )

            # 输入用户名
            username_input = self.driver.find_element(By.ID, "username")
            username_input.clear()
            username_input.send_keys(self.username)

            # 输入密码
            password_input = self.driver.find_element(By.ID, "password")
            password_input.clear()
            password_input.send_keys(self.password)

            # 检查是否有验证码
            try:
                captcha_input = self.driver.find_element(By.ID, "captcha")
                print("请手动输入验证码...")
                # 等待用户手动输入验证码
                while True:
                    captcha_value = captcha_input.get_attribute("value")
                    if captcha_value:
                        break
                    time.sleep(1)
            except:
                print("未检测到验证码输入框")

            # 点击登录按钮
            login_button = self.driver.find_element(By.ID, "login-button")
            login_button.click()

            # 等待登录成功
            WebDriverWait(self.driver, 10).until(
                EC.url_changes(self.url)
            )

            print("登录成功！")
            return True

        except Exception as e:
            print(f"登录失败: {str(e)}")
            return False

    def close(self):
        """关闭浏览器"""
        if self.driver:
            self.driver.quit()
