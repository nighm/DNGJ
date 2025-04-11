import os
from dotenv import load_dotenv
from pages.login_page import LoginPage

def main():
    # 加载环境变量
    load_dotenv()
    
    # 获取登录信息
    url = os.getenv('LOGIN_URL')
    username = os.getenv('USERNAME')
    password = os.getenv('PASSWORD')
    
    # 创建登录页面实例
    login_page = LoginPage(url, username, password)
    
    try:
        # 启动浏览器并登录
        login_page.start_browser()
        login_page.login()
        
        # 保持浏览器打开，等待用户查看
        input("按回车键关闭浏览器...")
        
    finally:
        # 关闭浏览器
        login_page.close()

if __name__ == "__main__":
    main()
