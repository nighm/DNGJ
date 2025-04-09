from selenium.webdriver import Edge
from selenium.webdriver.edge.service import Service
from selenium.webdriver.edge.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.microsoft import EdgeChromiumDriverManager
import time

def main():
    # 配置Edge浏览器
    service = Service(EdgeChromiumDriverManager().install())
    options = Options()
    options.set_capability('acceptInsecureCerts', True)
    options.add_argument('--ignore-certificate-errors')
    options.add_argument('--ignore-ssl-errors')
    options.add_argument('--start-maximized')
    
    # 初始化浏览器
    print("正在初始化浏览器...")
    driver = Edge(service=service, options=options)
    wait = WebDriverWait(driver, 20)
    
    try:
        # 访问登录页面
        print("正在访问登录页面...")
        driver.get("https://192.168.30.240")
        
        # 等待页面加载完成
        wait.until(lambda d: d.execute_script('return document.readyState') == 'complete')
        
        # 等待用户名输入框出现
        username_input = wait.until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "input[type='text'][placeholder='请输入用户名']"))
        )
        
        # 输入登录信息
        print("正在输入登录信息...")
        username_input.clear()
        username_input.send_keys("super")
        
        password_input = driver.find_element(By.CSS_SELECTOR, "input[type='password'][placeholder='请输入密码']")
        password_input.clear()
        password_input.send_keys("admin123")
        
        # 等待用户手动输入验证码（如果有）
        print("\n如果有验证码，请在浏览器中手动输入，然后按回车继续...")
        input()
        
        # 点击登录按钮
        print("点击登录按钮...")
        login_button = driver.find_element(By.CSS_SELECTOR, ".el-button--primary")
        login_button.click()
        
        # 等待登录结果
        print("等待登录结果...")
        time.sleep(2)
        
        # 检查是否登录成功
        current_url = driver.current_url
        print(f"当前页面URL: {current_url}")
        
        if "/login" not in current_url:
            print("登录成功！")
            # 检查用户信息元素
            try:
                wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, ".user-info, .user-profile, .avatar")))
                print("已检测到用户信息元素，确认登录成功！")
            except:
                print("警告：未检测到用户信息元素，请手动确认登录状态。")
        else:
            error_message = None
            try:
                error_element = driver.find_element(By.CSS_SELECTOR, ".el-message.el-message--error")
                if error_element.is_displayed():
                    error_message = error_element.text
            except:
                pass
            
            if error_message:
                print(f"登录失败！错误信息：{error_message}")
            else:
                print("登录可能失败，请检查页面状态。")
        
        # 保持浏览器窗口打开
        print("\n浏览器窗口将保持打开状态，按回车键关闭...")
        input()
        
    except Exception as e:
        print(f"发生错误: {str(e)}")
    finally:
        driver.quit()

if __name__ == "__main__":
    main() 