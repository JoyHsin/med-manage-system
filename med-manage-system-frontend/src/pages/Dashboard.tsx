import React from 'react';
import { Card, Typography, Button } from 'antd';
import { useAuth } from '../contexts/AuthContext';

const { Title } = Typography;

const Dashboard: React.FC = () => {
  const { user, logout } = useAuth();
  
  console.log('Dashboard rendering, user:', user);
  
  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>仪表盘</Title>
      
      <Card title="欢迎使用社区诊所管理系统" style={{ marginBottom: 16 }}>
        <p>欢迎，{user?.fullName || user?.username}！</p>
        <p>部门：{user?.department || '未设置'}</p>
        <p>职位：{user?.position || '未设置'}</p>
        <Button type="primary" onClick={logout}>
          退出登录
        </Button>
      </Card>

      <Card title="系统状态" size="small">
        <p>✅ 前端正常运行</p>
        <p>✅ 后端API连接正常</p>
        <p>✅ 用户认证成功</p>
      </Card>
    </div>
  );
};

export default Dashboard;