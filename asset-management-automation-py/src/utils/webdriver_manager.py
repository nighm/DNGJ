from selenium import webdriver
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.edge.service import Service as EdgeService
from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager
from webdriver_manager.microsoft import EdgeChromiumDriverManager
from typing import Optional
from .config_reader import ConfigReader

class WebDriverManager:
    _instance = None
    _driver = None

    def __new__(cls) -> 'WebDriverManager':
        if cls._instance is None:
            cls._instance = super(WebDriverManager, cls).__new__(cls)
        return cls._instance

    def __init__(self, config_reader):
        """初始化 WebDriver 管理器
        
        Args:
            config_reader: 配置读取器实例
        """
        self.config_reader = config_reader
        self.driver: Optional[webdriver.Remote] = None

    def create_driver(self) -> webdriver.Remote:
        """创建 WebDriver 实例
        
        Returns:
            WebDriver 实例
        """
        browser = self.config_reader.get_value('Environment', 'browser', 'chrome')
        headless = self.config_reader.get_value('Environment', 'headless', False)
        
        if browser.lower() == 'chrome':
            options = webdriver.ChromeOptions()
            if headless:
                options.add_argument('--headless')
            options.add_argument('--no-sandbox')
            options.add_argument('--disable-dev-shm-usage')
            self.driver = webdriver.Chrome(
                service=ChromeService(ChromeDriverManager().install()),
                options=options
            )
        elif browser.lower() == 'firefox':
            options = webdriver.FirefoxOptions()
            if headless:
                options.add_argument('--headless')
            self.driver = webdriver.Firefox(
                service=FirefoxService(GeckoDriverManager().install()),
                options=options
            )
        elif browser.lower() == 'edge':
            options = webdriver.EdgeOptions()
            if headless:
                options.add_argument('--headless')
            self.driver = webdriver.Edge(
                service=EdgeService(EdgeChromiumDriverManager().install()),
                options=options
            )
        else:
            raise ValueError(f"Unsupported browser: {browser}")
        
        # 设置超时时间
        self.driver.implicitly_wait(
            self.config_reader.get_value('Environment', 'implicit_wait', 10)
        )
        self.driver.set_page_load_timeout(
            self.config_reader.get_value('Environment', 'page_load_timeout', 30)
        )
        
        return self.driver

    @property
    def driver(self) -> webdriver.Edge:
        """获取WebDriver实例"""
        return self._driver

    def quit(self) -> None:
        """关闭WebDriver"""
        if self._driver:
            self._driver.quit()
            self._driver = None

    def get(self, url: str) -> None:
        """导航到指定URL"""
        self._driver.get(url)

    def refresh(self) -> None:
        """刷新当前页面"""
        self._driver.refresh()

    def get_current_url(self) -> str:
        """获取当前URL"""
        return self._driver.current_url

    def get_title(self) -> str:
        """获取页面标题"""
        return self._driver.title

    def take_screenshot(self, filename: str) -> bool:
        """保存截图
        
        Args:
            filename: 截图文件名
            
        Returns:
            bool: 截图是否成功
        """
        try:
            self._driver.save_screenshot(filename)
            return True
        except Exception as e:
            print(f"截图失败: {str(e)}")
            return False

    def quit_driver(self) -> None:
        """退出 WebDriver 实例"""
        if self.driver:
            self.driver.quit()
            self.driver = None 