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
import RolePermissionManagement from './pages/RolePermissionManagement';
import PatientManagement from './pages/PatientManagement';
import AppointmentManagement from './pages/AppointmentManagement';
import RegistrationManagement from './pages/RegistrationManagement';
import TriageManagement from './pages/TriageManagement';
import VitalSignsManagement from './pages/VitalSignsManagement';
import MedicalOrderManagement from './pages/MedicalOrderManagement';
import MedicalRecordManagement from './pages/MedicalRecordManagement';
import PrescriptionManagement from './pages/PrescriptionManagement';
import MedicalOrderCreation from './pages/MedicalOrderCreation';
import BillingManagement from './pages/BillingManagement';
import InventoryManagement from './pages/InventoryManagement';
import PharmacyDispensing from './pages/PharmacyDispensing';
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
                        <Route path="/roles" element={<RolePermissionManagement />} />
                        <Route path="/patients" element={<PatientManagement />} />
                        <Route path="/appointments" element={<AppointmentManagement />} />
                        <Route path="/registrations" element={<RegistrationManagement />} />
                        <Route path="/triage" element={<TriageManagement />} />
                        <Route path="/vital-signs" element={<VitalSignsManagement />} />
                        <Route path="/medical-orders" element={<MedicalOrderManagement />} />
                        <Route path="/medical-records" element={<MedicalRecordManagement />} />
                        <Route path="/prescriptions" element={<PrescriptionManagement />} />
                        <Route path="/medical-order-creation" element={<MedicalOrderCreation />} />
                        <Route path="/billing" element={<BillingManagement />} />
                        <Route path="/inventory" element={<InventoryManagement />} />
                        <Route path="/pharmacy-dispensing" element={<PharmacyDispensing />} />
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
