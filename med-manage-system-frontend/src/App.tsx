import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider, Spin } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import ErrorBoundary from './components/ErrorBoundary';
import Login from './pages/Login';
import SimpleUserManagement from './pages/SimpleUserManagement';
import DebugUserManagement from './pages/DebugUserManagement';
import TokenDebugPage from './pages/TokenDebugPage';
import RolePermissionManagement from './pages/RolePermissionManagement';
import SimplePatientManagement from './pages/SimplePatientManagement';
import AppointmentManagement from './pages/AppointmentManagement';
import RegistrationManagement from './pages/RegistrationManagement';
import TriageManagement from './pages/TriageManagement';
import SimpleVitalSignsManagement from './pages/SimpleVitalSignsManagement';
import MedicalOrderManagement from './pages/MedicalOrderManagement';
import MedicalRecordManagement from './pages/MedicalRecordManagement';
import PrescriptionManagement from './pages/PrescriptionManagement';
import MedicalOrderCreation from './pages/MedicalOrderCreation';
import BillingManagement from './pages/BillingManagement';
import InventoryManagement from './pages/InventoryManagement';
import PharmacyDispensing from './pages/PharmacyDispensing';
import PrescriptionQueueManagement from './pages/PrescriptionQueueManagement';
import SimpleStaffManagement from './pages/SimpleStaffManagement';
import './App.css';

// 简化的Dashboard组件
const SimpleDashboard: React.FC = () => {
  const { user, fetchUserInfo, token } = useAuth();
  const [loading, setLoading] = React.useState(false);

  React.useEffect(() => {
    // 如果有token但没有用户信息，则获取用户信息
    if (token && !user) {
      setLoading(true);
      fetchUserInfo().finally(() => setLoading(false));
    }
  }, [token, user, fetchUserInfo]);

  if (loading) {
    return (
      <div style={{ padding: '24px', textAlign: 'center' }}>
        <Spin size="large" />
        <p>正在获取用户信息...</p>
      </div>
    );
  }

  return (
    <div style={{ padding: '24px' }}>
      <h1>仪表盘</h1>
      <p>登录成功！前后端对接正常。</p>
      {user && (
        <div>
          <p><strong>欢迎，{user.fullName}！</strong></p>
          <p>用户名: {user.username}</p>
          <p>部门: {user.department}</p>
          <p>职位: {user.position}</p>
        </div>
      )}
      <p>可以点击左侧菜单测试其他页面。</p>
      <p><strong>注意：</strong>用户管理功能需要相应权限，当前使用调试模式。</p>
    </div>
  );
};

const App: React.FC = () => {
  return (
    <ErrorBoundary>
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
                        <ErrorBoundary>
                          <Routes>
                            <Route path="/dashboard" element={<SimpleDashboard />} />
                            <Route path="/users" element={<SimpleUserManagement />} />
                            <Route path="/roles" element={<RolePermissionManagement />} />
                            <Route path="/patients" element={<SimplePatientManagement />} />
                            <Route path="/staff" element={<SimpleStaffManagement />} />
                            <Route path="/appointments" element={<AppointmentManagement />} />
                            <Route path="/registrations" element={<RegistrationManagement />} />
                            <Route path="/triage" element={<TriageManagement />} />
                            <Route path="/vital-signs" element={<SimpleVitalSignsManagement />} />
                            <Route path="/medical-orders" element={<MedicalOrderManagement />} />
                            <Route path="/medical-records" element={<MedicalRecordManagement />} />
                            <Route path="/prescriptions" element={<PrescriptionManagement />} />
                            <Route path="/medical-order-creation" element={<MedicalOrderCreation />} />
                            <Route path="/billing" element={<BillingManagement />} />
                            <Route path="/inventory" element={<InventoryManagement />} />
                            <Route path="/pharmacy-dispensing" element={<PharmacyDispensing />} />
                            <Route path="/prescription-queue" element={<PrescriptionQueueManagement />} />
                            <Route path="/token-debug" element={<TokenDebugPage />} />
                          </Routes>
                        </ErrorBoundary>
                      </Layout>
                    </ProtectedRoute>
                  }
                />
              </Routes>
            </div>
          </Router>
        </AuthProvider>
      </ConfigProvider>
    </ErrorBoundary>
  );
};

export default App;
