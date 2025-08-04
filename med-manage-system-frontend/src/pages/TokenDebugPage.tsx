import React, { useState } from 'react';
import { Card, Button, Typography, Space, Alert, Divider, Input } from 'antd';
import { apiClient } from '../services/apiClient';
import { authService } from '../services/authService';
import { validateTokenFormat, ensureBearerToken, debugToken } from '../utils/tokenUtils';

const { Title, Text, Paragraph } = Typography;

const TokenDebugPage: React.FC = () => {
  const [debugInfo, setDebugInfo] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [testToken, setTestToken] = useState<string>('');

  const testTokenFormat = () => {
    const token = localStorage.getItem('token');
    const validation = validateTokenFormat(token);
    
    const info = [
      `原始令牌: ${token ? token.substring(0, 30) + '...' : 'null'}`,
      `验证结果: ${validation.message}`,
      `是否有效: ${validation.isValid ? '是' : '否'}`,
      `包含Bearer: ${validation.hasBearer ? '是' : '否'}`,
      `令牌长度: ${validation.length}`,
    ].join('\n');
    
    setDebugInfo(info);
    debugToken(); // 在控制台输出详细信息
  };

  const testApiCall = async () => {
    setLoading(true);
    try {
      console.log('开始测试API调用...');
      
      // 先检查token是否存在
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('localStorage中没有找到token');
      }
      
      console.log('找到token:', token.substring(0, 20) + '...');
      
      // 使用apiClient调用/auth/me接口
      const response = await authService.getCurrentUser();
      console.log('API调用成功:', response);
      
      setDebugInfo(prev => prev + '\n\nAPI调用成功: ' + JSON.stringify(response, null, 2));
    } catch (error: any) {
      console.error('API调用失败:', error);
      const errorMsg = error.response?.data?.message || error.message || '未知错误';
      setDebugInfo(prev => prev + '\n\nAPI调用失败: ' + errorMsg);
      
      // 如果是401错误，说明token无效
      if (error.response?.status === 401) {
        setDebugInfo(prev => prev + '\n提示: 401错误通常表示token无效或已过期');
      }
    } finally {
      setLoading(false);
    }
  };

  const testDirectFetch = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      const authHeader = ensureBearerToken(token);
      
      console.log('使用fetch直接调用API...');
      console.log('Authorization头:', authHeader?.substring(0, 30) + '...');
      
      if (!authHeader) {
        throw new Error('无法生成有效的Authorization头');
      }
      
      const response = await fetch('http://localhost:8080/api/auth/me', {
        method: 'GET',
        headers: {
          'Authorization': authHeader,
          'Content-Type': 'application/json',
        },
      });
      
      const data = await response.text();
      console.log('Fetch响应状态:', response.status);
      console.log('Fetch响应数据:', data);
      
      setDebugInfo(prev => prev + `\n\nFetch调用结果:\n状态: ${response.status}\n数据: ${data}`);
    } catch (error: any) {
      console.error('Fetch调用失败:', error);
      setDebugInfo(prev => prev + '\n\nFetch调用失败: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const clearDebugInfo = () => {
    setDebugInfo('');
    console.clear();
  };

  const setManualToken = () => {
    if (testToken.trim()) {
      localStorage.setItem('token', testToken.trim());
      setDebugInfo(prev => prev + '\n\n手动设置token: ' + testToken.substring(0, 30) + '...');
      console.log('手动设置token:', testToken.substring(0, 30) + '...');
    }
  };

  const clearToken = () => {
    localStorage.removeItem('token');
    setDebugInfo(prev => prev + '\n\n已清除localStorage中的token');
    console.log('已清除token');
  };

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>JWT令牌调试页面</Title>
      
      <Alert
        message="调试说明"
        description="这个页面用于调试JWT令牌格式问题。请按顺序点击按钮进行测试。"
        type="info"
        showIcon
        style={{ marginBottom: 16 }}
      />
      
      <Card>
        <Space direction="vertical" style={{ width: '100%' }}>
          <Space direction="vertical" style={{ width: '100%' }}>
            <Space wrap>
              <Button onClick={testTokenFormat}>
                1. 检查令牌格式
              </Button>
              <Button onClick={testApiCall} loading={loading}>
                2. 测试apiClient调用
              </Button>
              <Button onClick={testDirectFetch} loading={loading}>
                3. 测试直接fetch调用
              </Button>
              <Button onClick={clearDebugInfo}>
                清除调试信息
              </Button>
            </Space>
            
            <Divider orientation="left">手动测试</Divider>
            <Space.Compact style={{ width: '100%' }}>
              <Input
                placeholder="输入测试token（用于调试）"
                value={testToken}
                onChange={(e) => setTestToken(e.target.value)}
                style={{ width: '70%' }}
              />
              <Button onClick={setManualToken}>设置Token</Button>
              <Button onClick={clearToken} danger>清除Token</Button>
            </Space.Compact>
          </Space>
          
          <Divider />
          
          {debugInfo && (
            <div>
              <Title level={4}>调试信息:</Title>
              <pre style={{ 
                background: '#f5f5f5', 
                padding: '12px', 
                borderRadius: '4px',
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-all'
              }}>
                {debugInfo}
              </pre>
            </div>
          )}
          
          <Divider />
          
          <div>
            <Title level={4}>使用说明:</Title>
            <Paragraph>
              <ol>
                <li>首先点击"检查令牌格式"查看当前存储的令牌格式</li>
                <li>然后点击"测试apiClient调用"使用我们的API客户端调用接口</li>
                <li>最后点击"测试直接fetch调用"使用原生fetch调用接口</li>
                <li>查看浏览器控制台和下方的调试信息</li>
              </ol>
            </Paragraph>
          </div>
        </Space>
      </Card>
    </div>
  );
};

export default TokenDebugPage;