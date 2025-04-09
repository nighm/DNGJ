import os
import configparser
from typing import Any, Dict, Optional

class ConfigReader:
    _instance = None
    
    def __new__(cls):
        """单例模式"""
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance
    
    def __init__(self):
        """初始化配置读取器"""
        if not hasattr(self, 'config'):
            self.config = configparser.ConfigParser()
            self._load_config()
    
    def _load_config(self) -> None:
        """加载配置文件"""
        try:
            config_file = os.path.join(
                os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                'resources',
                'config',
                'config.ini'
            )
            if os.path.exists(config_file):
                self.config.read(config_file, encoding='utf-8')
            else:
                # 创建默认配置
                self.config['Environment'] = {
                    'base_url': 'http://localhost:8080',
                    'browser': 'edge',
                    'headless': 'false',
                    'implicit_wait': '10',
                    'explicit_wait': '20',
                    'page_load_timeout': '30'
                }
                self.config['Credentials'] = {
                    'admin_username': 'admin',
                    'admin_password': 'admin123',
                    'normal_username': 'user',
                    'normal_password': 'user123'
                }
                self.config['Paths'] = {
                    'screenshots_dir': 'reports/screenshots',
                    'allure_results_dir': 'reports/allure-results',
                    'test_data_dir': 'resources/testdata'
                }
                self.config['Logging'] = {
                    'log_level': 'INFO',
                    'log_file': 'logs/test.log',
                    'log_format': '%(asctime)s - %(name)s - %(levelname)s - %(message)s',
                    'log_date_format': '%Y-%m-%d %H:%M:%S'
                }
                self.config['RetrySettings'] = {
                    'max_retries': '3',
                    'retry_delay': '2'
                }
                # 保存默认配置
                self._save_config()
        except Exception as e:
            print(f"Error loading config: {e}")
            self.config = configparser.ConfigParser()
    
    def _save_config(self) -> None:
        """保存配置到文件"""
        try:
            config_file = os.path.join(
                os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                'resources',
                'config',
                'config.ini'
            )
            os.makedirs(os.path.dirname(config_file), exist_ok=True)
            with open(config_file, 'w', encoding='utf-8') as f:
                self.config.write(f)
        except Exception as e:
            print(f"Error saving config: {e}")
    
    def get_value(self, section: str, key: str, default: Any = None) -> Any:
        """获取配置值
        
        Args:
            section: 配置节
            key: 配置键
            default: 默认值
            
        Returns:
            配置值
        """
        try:
            if self.config.has_section(section):
                return self.config.get(section, key, fallback=default)
            return default
        except Exception as e:
            print(f"Error getting config value: {e}")
            return default
    
    def set_value(self, section: str, key: str, value: Any) -> None:
        """设置配置值
        
        Args:
            section: 配置节
            key: 配置键
            value: 配置值
        """
        try:
            if not self.config.has_section(section):
                self.config.add_section(section)
            self.config.set(section, key, str(value))
            self._save_config()
        except Exception as e:
            print(f"Error setting config value: {e}")
    
    def get_section(self, section: str) -> Dict[str, str]:
        """获取配置节
        
        Args:
            section: 配置节名称
            
        Returns:
            配置节字典
        """
        try:
            if self.config.has_section(section):
                return dict(self.config.items(section))
            return {}
        except Exception as e:
            print(f"Error getting config section: {e}")
            return {}
    
    def set_section(self, section: str, values: Dict[str, Any]) -> None:
        """设置配置节
        
        Args:
            section: 配置节名称
            values: 配置节值
        """
        try:
            if not self.config.has_section(section):
                self.config.add_section(section)
            for key, value in values.items():
                self.config.set(section, key, str(value))
            self._save_config()
        except Exception as e:
            print(f"Error setting config section: {e}")

    @property
    def base_url(self) -> str:
        """获取基础URL"""
        return self.get_value('Environment', 'base_url')

    @property
    def browser(self) -> str:
        """获取浏览器类型"""
        return self.get_value('Environment', 'browser')

    @property
    def headless(self) -> bool:
        """获取是否使用无头模式"""
        return self.get_value('Environment', 'headless').lower() == 'true'

    @property
    def implicit_wait(self) -> int:
        """获取隐式等待时间"""
        return int(self.get_value('Environment', 'implicit_wait', '10'))

    @property
    def explicit_wait(self) -> int:
        """获取显式等待时间"""
        return int(self.get_value('Environment', 'explicit_wait', '20'))

    @property
    def page_load_timeout(self) -> int:
        """获取页面加载超时时间"""
        return int(self.get_value('Environment', 'page_load_timeout', '30')) 