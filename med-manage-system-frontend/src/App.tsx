import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import Login from './pages/Login';
import SimpleUserManagement from './pages/SimpleUserManagement';
import DebugUserManagement from './pages/DebugUserManagement';
import './App.css';

// 简化的Dashboard组件
const SimpleDashboard: React.FC = () => {
  return (
    <div style={{ padding: '24px' }}>
      <h1>仪表盘</h1>
      <p>登录成功！前后端对接正常。</p>
      <p>可以点击左侧菜单测试其他页面。</p>
      <p><strong>注意：</strong>用户管理功能需要相应权限，当前使用调试模式。</p>
    </div>
  );
};

const App: React.FC = () => {
  return (
    <ConfigProvider locale={zhCN}>
      <AuthProvider>
        <Router>
          <div className="App">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route
                path="/*"
                element={
                  <ProtectedRoute>
                    <Layout>
                      <Routes>
                        <Route path="/dashboard" element={<SimpleDashboard />} />
                        <Route path="/users" element={<SimpleUserManagement />} />
                      </Routes>
                    </Layout>
                  </ProtectedRoute>
                }
              />
            </Routes>
          </div>
        </Router>
      </AuthProvider>
    </ConfigProvider>
  );
};

export default App;
