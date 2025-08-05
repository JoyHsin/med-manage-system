import React, { Suspense } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider, Spin } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import ErrorBoundary from './components/ErrorBoundary';
import LazyLoadingSpinner from './components/LazyLoadingSpinner';
import { createLazyComponent } from './utils/lazyComponentLoader';
import { useRoutePreloader } from './hooks/useRoutePreloader';
import { executeAutoOptimization, startOptimizationMonitoring } from './utils/bundleOptimizer';
import './App.css';

// 立即加载的关键组件
import Login from './pages/Login';

// 使用增强的懒加载组件创建器 - 按功能模块和优先级分组
// 用户管理模块 - 高优先级
const SimpleUserManagement = createLazyComponent(
  () => import('./pages/SimpleUserManagement'),
  { 
    loadingTip: '正在加载用户管理...', 
    retryCount: 3, 
    priority: 'high',
    networkAware: true,
    cacheKey: 'user-management'
  }
);
const DebugUserManagement = createLazyComponent(
  () => import('./pages/DebugUserManagement'),
  { 
    loadingTip: '正在加载调试用户管理...', 
    retryCount: 2, 
    priority: 'low',
    cacheKey: 'debug-user-management'
  }
);
const TokenDebugPage = createLazyComponent(
  () => import('./pages/TokenDebugPage'),
  { 
    loadingTip: '正在加载调试页面...', 
    retryCount: 2, 
    priority: 'low',
    cacheKey: 'token-debug'
  }
);
const RolePermissionManagement = createLazyComponent(
  () => import('./pages/RolePermissionManagement'),
  { 
    loadingTip: '正在加载角色管理...', 
    retryCount: 3, 
    priority: 'high',
    networkAware: true,
    cacheKey: 'role-management'
  }
);

// 患者管理模块 - 高优先级
const SimplePatientManagement = createLazyComponent(
  () => import('./pages/SimplePatientManagement'),
  { 
    loadingTip: '正在加载患者管理...', 
    retryCount: 3, 
    priority: 'high',
    networkAware: true,
    preload: true,
    cacheKey: 'patient-management'
  }
);
const SimpleStaffManagement = createLazyComponent(
  () => import('./pages/SimpleStaffManagement'),
  { 
    loadingTip: '正在加载员工管理...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'staff-management'
  }
);

// 预约和挂号模块 - 高优先级
const AppointmentManagement = createLazyComponent(
  () => import('./pages/AppointmentManagement'),
  { 
    loadingTip: '正在加载预约管理...', 
    retryCount: 3, 
    priority: 'high',
    networkAware: true,
    preload: true,
    cacheKey: 'appointment-management'
  }
);
const RegistrationManagement = createLazyComponent(
  () => import('./pages/RegistrationManagement'),
  { 
    loadingTip: '正在加载挂号管理...', 
    retryCount: 3, 
    priority: 'high',
    networkAware: true,
    cacheKey: 'registration-management'
  }
);

// 护士工作台模块 - 中优先级
const TriageManagement = createLazyComponent(
  () => import('./pages/TriageManagement'),
  { 
    loadingTip: '正在加载分诊管理...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'triage-management'
  }
);
const SimpleVitalSignsManagement = createLazyComponent(
  () => import('./pages/SimpleVitalSignsManagement'),
  { 
    loadingTip: '正在加载生命体征...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'vital-signs-management'
  }
);

// 医生工作台模块 - 中优先级
const MedicalOrderManagement = createLazyComponent(
  () => import('./pages/MedicalOrderManagement'),
  { 
    loadingTip: '正在加载医嘱管理...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'medical-order-management'
  }
);
const MedicalRecordManagement = createLazyComponent(
  () => import('./pages/MedicalRecordManagement'),
  { 
    loadingTip: '正在加载病历管理...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'medical-record-management'
  }
);
const PrescriptionManagement = createLazyComponent(
  () => import('./pages/PrescriptionManagement'),
  { 
    loadingTip: '正在加载处方管理...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'prescription-management'
  }
);
const MedicalOrderCreation = createLazyComponent(
  () => import('./pages/MedicalOrderCreation'),
  { 
    loadingTip: '正在加载医嘱开具...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'medical-order-creation'
  }
);

// 收费管理模块 - 中优先级
const BillingManagement = createLazyComponent(
  () => import('./pages/BillingManagement'),
  { 
    loadingTip: '正在加载费用管理...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'billing-management'
  }
);

// 药房管理模块 - 中优先级
const InventoryManagement = createLazyComponent(
  () => import('./pages/InventoryManagement'),
  { 
    loadingTip: '正在加载库存管理...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'inventory-management'
  }
);
const PharmacyDispensing = createLazyComponent(
  () => import('./pages/PharmacyDispensing'),
  { 
    loadingTip: '正在加载药房发药...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'pharmacy-dispensing'
  }
);
const PrescriptionQueueManagement = createLazyComponent(
  () => import('./pages/PrescriptionQueueManagement'),
  { 
    loadingTip: '正在加载处方队列...', 
    retryCount: 3, 
    priority: 'medium',
    networkAware: true,
    cacheKey: 'prescription-queue-management'
  }
);

// 统计和报表模块 - 低优先级
const StatisticsDashboard = createLazyComponent(
  () => import('./pages/StatisticsDashboard'),
  { 
    loadingTip: '正在加载统计数据...', 
    retryCount: 3, 
    priority: 'low',
    networkAware: true,
    cacheKey: 'statistics-dashboard'
  }
);
const ReportManagement = createLazyComponent(
  () => import('./pages/ReportManagement'),
  { 
    loadingTip: '正在加载报表管理...', 
    retryCount: 3, 
    priority: 'low',
    networkAware: true,
    cacheKey: 'report-management'
  }
);

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

// 主应用组件，集成路由预加载
const AppWithPreloader: React.FC = () => {
  useRoutePreloader(); // 启用智能路由预加载
  
  return (
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
        <Route path="/statistics" element={<StatisticsDashboard />} />
        <Route path="/reports" element={<ReportManagement />} />
        <Route path="/token-debug" element={<TokenDebugPage />} />
      </Routes>
    </ErrorBoundary>
  );
};

const App: React.FC = () => {
  // 初始化优化策略
  React.useEffect(() => {
    // 延迟执行优化，避免影响初始加载
    const initOptimization = async () => {
      try {
        await executeAutoOptimization({
          enableTreeShaking: true,
          enableCodeSplitting: true,
          enablePreloading: true,
          enableCompression: true,
          targetBrowsers: ['chrome >= 80', 'firefox >= 78', 'safari >= 14'],
          chunkSizeLimit: 500 * 1024,
        });
        
        // 启动实时监控
        const stopMonitoring = startOptimizationMonitoring();
        
        // 页面卸载时清理
        return () => {
          stopMonitoring();
        };
      } catch (error) {
        console.warn('Failed to initialize optimization:', error);
      }
    };
    
    // 在空闲时间初始化
    if ('requestIdleCallback' in window) {
      window.requestIdleCallback(() => {
        initOptimization();
      }, { timeout: 5000 });
    } else {
      setTimeout(initOptimization, 2000);
    }
  }, []);

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
                        <AppWithPreloader />
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
