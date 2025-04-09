from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time

def setup_driver():
    """设置并返回Chrome浏览器驱动"""
    options = webdriver.ChromeOptions()
    options.add_argument('--ignore-certificate-errors')  # 忽略SSL证书错误
    options.add_argument('--start-maximized')  # 最大化窗口
    
    # 设置Chrome WebDriver
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    return driver

def auto_login(username="super", password="admin123"):
    """自动化登录过程，只需手动输入验证码"""
    driver = setup_driver()
    
    try:
        # 访问登录页面
        print("正在打开登录页面...")
        driver.get("https://192.168.30.240/asset/assetOver/login")
        
        # 等待页面加载完成
        wait = WebDriverWait(driver, 10)
        
        # 等待并找到用户名输入框
        username_input = wait.until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "input[placeholder*='用户名']"))
        )
        
        # 输入用户名
        username_input.clear()
        username_input.send_keys(username)
        
        # 输入密码
        password_input = driver.find_element(By.CSS_SELECTOR, "input[placeholder*='密码']")
        password_input.clear()
        password_input.send_keys(password)
        
        # 等待用户手动输入验证码
        print("\n请在浏览器中手动输入验证码，然后按回车继续...")
        input()
        
        # 点击登录按钮
        login_button = driver.find_element(By.CSS_SELECTOR, "button[type='button']")
        login_button.click()
        
        # 等待登录结果
        time.sleep(2)
        
        # 获取当前URL，检查是否登录成功
        current_url = driver.current_url
        if "/login" not in current_url:
            print("登录成功！")
        else:
            print("登录可能失败，请检查页面状态。")
        
        # 等待用户确认
        input("按回车键关闭浏览器...")
        
    except Exception as e:
        print(f"发生错误: {str(e)}")
    finally:
        driver.quit()

if __name__ == "__main__":
    while True:
        auto_login()
        retry = input("\n是否要重试？(y/n): ")
        if retry.lower() != 'y':
            break 