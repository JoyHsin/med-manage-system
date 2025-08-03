import React, { useState, useMemo } from 'react';
import { Layout as AntLayout, Button, Space, Menu } from 'antd';
import {
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';
import { menuItems, filterMenuByPermissions, convertToAntdMenu } from '../config/menuConfig';

const { Header, Content, Sider } = AntLayout;

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // 根据用户权限过滤菜单项
  const filteredMenuItems = useMemo(() => {
    // 如果没有用户信息，只显示仪表盘
    if (!user) {
      return convertToAntdMenu(menuItems.filter(item => item.key === '/dashboard'));
    }

    // 获取用户权限列表
    const userPermissions = user?.roles?.flatMap(role => 
      role.permissions?.map(permission => permission.code) || []
    ) || [];
    
    // 对于admin用户或超级管理员，显示所有菜单
    if (user.username === 'admin' || userPermissions.includes('SYSTEM_CONFIG')) {
      return convertToAntdMenu(menuItems);
    }
    
    // 过滤菜单项
    const filtered = filterMenuByPermissions(menuItems, userPermissions);
    return convertToAntdMenu(filtered);
  }, [user]);

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
  };

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Sider 
        trigger={null} 
        collapsible 
        collapsed={collapsed}
        style={{
          overflow: 'auto',
          height: '100vh',
          position: 'fixed',
          left: 0,
          top: 0,
          bottom: 0,
        }}
      >
        <div style={{ 
          height: 32, 
          margin: 16, 
          background: 'rgba(255, 255, 255, 0.3)',
          borderRadius: 6,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#fff',
          fontWeight: 'bold'
        }}>
          {collapsed ? '诊所' : '社区诊所管理系统'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={filteredMenuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      
      <AntLayout style={{ marginLeft: collapsed ? 80 : 200, transition: 'margin-left 0.2s' }}>
        <Header style={{ 
          padding: '0 24px',
          background: '#fff',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        }}>
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            style={{
              fontSize: '16px',
              width: 64,
              height: 64,
            }}
          />
          
          <Space>
            <span>欢迎，{user?.fullName || user?.username}</span>
            <Button 
              type="primary" 
              icon={<LogoutOutlined />}
              onClick={handleLogout}
            >
              退出登录
            </Button>
          </Space>
        </Header>
        
        <Content style={{
          margin: '24px',
          padding: 0,
          minHeight: 280,
        }}>
          {children}
        </Content>
      </AntLayout>
    </AntLayout>
  );
};

export default Layout;