import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Button, 
  Typography, 
  Table, 
  message, 
  Space, 
  Alert
} from 'antd';
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';

const { Title } = Typography;

const DebugUserManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);

  // 模拟用户数据
  const mockUsers = [
    {
      id: 1,
      username: 'admin',
      fullName: '系统管理员',
      email: 'admin@clinic.com',
      department: '行政部',
      position: '系统管理员',
      enabled: true,
    },
    {
      id: 2,
      username: 'doctor01',
      fullName: '张医生',
      email: 'zhang@clinic.com',
      department: '内科',
      position: '主治医师',
      enabled: true,
    }
  ];

  const testBackendConnection = async () => {
    setLoading(true);
    try {
      // 测试获取用户列表API（只读，不需要特殊权限）
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/users', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });
      
      console.log('API Response Status:', response.status);
      const data = await response.text();
      console.log('API Response Data:', data);
      
      if (response.ok) {
        message.success('后端API连接正常');
      } else {
        message.error(`API调用失败: ${response.status} - ${data}`);
      }
    } catch (error: any) {
      console.error('API调用错误:', error);
      message.error('网络连接失败: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  // 表格列定义
  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '姓名',
      dataIndex: 'fullName',
      key: 'fullName',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '部门',
      dataIndex: 'department',
      key: 'department',
    },
    {
      title: '职位',
      dataIndex: 'position',
      key: 'position',
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      key: 'enabled',
      render: (enabled: boolean) => (
        <span style={{ color: enabled ? 'green' : 'red' }}>
          {enabled ? '启用' : '禁用'}
        </span>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>用户管理 - 调试模式</Title>
      
      <Alert
        message="权限问题诊断"
        description="当前admin用户可能缺少USER_MANAGEMENT的CREATE权限。这里是调试页面，用于测试API连接。"
        type="warning"
        showIcon
        style={{ marginBottom: 16 }}
      />
      
      <Card>
        <div style={{ marginBottom: 16 }}>
          <Space>
            <Button 
              type="primary" 
              icon={<PlusOutlined />}
              onClick={() => message.info('创建功能需要权限配置')}
            >
              新增用户 (需要权限)
            </Button>
            <Button 
              icon={<ReloadOutlined />}
              onClick={testBackendConnection}
              loading={loading}
            >
              测试API连接
            </Button>
          </Space>
        </div>

        <Table
          columns={columns}
          dataSource={mockUsers}
          loading={loading}
          rowKey="id"
          pagination={false}
          title={() => '模拟用户数据 (需要权限才能获取真实数据)'}
        />
      </Card>
    </div>
  );
};

export default DebugUserManagement;