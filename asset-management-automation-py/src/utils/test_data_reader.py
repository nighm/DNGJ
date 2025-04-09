import csv
import os
from typing import List, Dict, Optional
from dataclasses import dataclass
from ..utils.config_reader import ConfigReader
from ..utils.logger import Logger

@dataclass
class UserData:
    username: str
    password: str
    role: str
    expected_result: str

class TestDataReader:
    _instance = None

    def __new__(cls) -> 'TestDataReader':
        if cls._instance is None:
            cls._instance = super(TestDataReader, cls).__new__(cls)
            cls._instance._initialize()
        return cls._instance

    def _initialize(self) -> None:
        """初始化测试数据读取器"""
        self.config = ConfigReader()
        self.logger = Logger()
        self.test_data_dir = self.config.get_value('Paths', 'test_data_dir')

    def _get_file_path(self, filename: str) -> str:
        """获取测试数据文件路径
        
        Args:
            filename: 文件名
            
        Returns:
            文件完整路径
        """
        return os.path.join(self.test_data_dir, filename)

    def read_user_data(self) -> List[UserData]:
        """读取用户测试数据
        
        Returns:
            用户数据列表
        """
        try:
            file_path = self._get_file_path('users.csv')
            self.logger.info(f"读取用户测试数据: {file_path}")
            
            users = []
            with open(file_path, 'r', encoding='utf-8') as file:
                reader = csv.DictReader(file)
                for row in reader:
                    user = UserData(
                        username=row['username'],
                        password=row['password'],
                        role=row['role'],
                        expected_result=row['expected_result']
                    )
                    users.append(user)
            
            self.logger.info(f"成功读取 {len(users)} 条用户数据")
            return users
            
        except FileNotFoundError:
            self.logger.error(f"用户数据文件未找到: {file_path}")
            return []
        except Exception as e:
            self.logger.error(f"读取用户数据失败: {str(e)}")
            return []

    def get_admin_user(self) -> Optional[UserData]:
        """获取管理员用户数据
        
        Returns:
            管理员用户数据或None
        """
        users = self.read_user_data()
        for user in users:
            if user.role == 'admin':
                return user
        return None

    def get_normal_user(self) -> Optional[UserData]:
        """获取普通用户数据
        
        Returns:
            普通用户数据或None
        """
        users = self.read_user_data()
        for user in users:
            if user.role == 'user':
                return user
        return None

    def get_test_users(self) -> List[UserData]:
        """获取测试用户数据
        
        Returns:
            测试用户数据列表
        """
        users = self.read_user_data()
        return [user for user in users if user.expected_result == 'failure'] 