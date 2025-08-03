import React, { useState } from 'react';
import { Layout as AntLayout, Button, Space, Menu } from 'antd';
import type { MenuProps } from 'antd';
import {
  LogoutOutlined,
  UserOutlined,
  TeamOutlined,
  SafetyOutlined,
  MedicineBoxOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  SoundOutlined,
  HeartOutlined,
  FileTextOutlined,
  DollarOutlined,
  ExperimentOutlined
} from '@ant-design/icons';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';

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

  const menuItems: MenuProps['items'] = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: '仪表盘',
    },
    {
      key: 'system',
      icon: <SafetyOutlined />,
      label: '系统管理',
      children: [
        {
          key: '/users',
          icon: <UserOutlined />,
          label: '用户管理',
        },
        {
          key: '/roles',
          icon: <TeamOutlined />,
          label: '角色权限',
        },
      ],
    },
    {
      key: 'basic',
      icon: <TeamOutlined />,
      label: '基础信息',
      children: [
        {
          key: '/patients',
          icon: <UserOutlined />,
          label: '患者管理',
        },
        {
          key: '/staff',
          icon: <TeamOutlined />,
          label: '医护人员',
        },
        {
          key: '/inventory',
          icon: <MedicineBoxOutlined />,
          label: '药品库存',
        },
      ],
    },
    {
      key: 'clinic',
      icon: <CalendarOutlined />,
      label: '门诊管理',
      children: [
        {
          key: '/appointments',
          icon: <CalendarOutlined />,
          label: '预约管理',
        },
        {
          key: '/registrations',
          icon: <ClockCircleOutlined />,
          label: '挂号管理',
        },
      ],
    },
    {
      key: 'nurse',
      icon: <HeartOutlined />,
      label: '护士工作台',
      children: [
        {
          key: '/triage',
          icon: <SoundOutlined />,
          label: '分诊叫号',
        },
        {
          key: '/vital-signs',
          icon: <HeartOutlined />,
          label: '生命体征',
        },
        {
          key: '/medical-orders',
          icon: <FileTextOutlined />,
          label: '医嘱执行',
        },
      ],
    },
    {
      key: 'doctor',
      icon: <UserOutlined />,
      label: '医生工作台',
      children: [
        {
          key: '/medical-records',
          icon: <FileTextOutlined />,
          label: '电子病历',
        },
        {
          key: '/prescriptions',
          icon: <MedicineBoxOutlined />,
          label: '处方管理',
        },
        {
          key: '/medical-order-creation',
          icon: <FileTextOutlined />,
          label: '医嘱开具',
        },
      ],
    },
    {
      key: 'billing',
      icon: <DollarOutlined />,
      label: '收费管理',
      children: [
        {
          key: '/billing',
          icon: <DollarOutlined />,
          label: '费用管理',
        },
      ],
    },
    {
      key: 'pharmacy',
      icon: <MedicineBoxOutlined />,
      label: '药房管理',
      children: [
        {
          key: '/inventory',
          icon: <MedicineBoxOutlined />,
          label: '药品库存',
        },
        {
          key: '/pharmacy-dispensing',
          icon: <ExperimentOutlined />,
          label: '处方调剂',
        },
      ],
    },
  ];

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
          items={menuItems}
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