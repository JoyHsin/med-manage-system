import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { 
  preloadRoutesByRole, 
  preloadRelatedRoutes, 
  preloadOnIdle,
  preloadPredictive,
  getPreloadStatus,
  clearPreloadCache,
  initializePreloadingStrategies
} from '../utils/routePreloader';
import { startPerformanceMonitoring } from '../utils/bundleAnalyzer';

// 使用路由预加载的Hook
export const useRoutePreloader = () => {
  const location = useLocation();
  const { user } = useAuth();
  const visitHistoryRef = useRef<string[]>([]);
  const [preloadStatus, setPreloadStatus] = useState(getPreloadStatus());
  const performanceMonitorRef = useRef<(() => any) | null>(null);

  // 启动性能监控
  useEffect(() => {
    if (!performanceMonitorRef.current) {
      performanceMonitorRef.current = startPerformanceMonitoring();
    }
    
    return () => {
      if (performanceMonitorRef.current) {
        performanceMonitorRef.current();
        performanceMonitorRef.current = null;
      }
    };
  }, []);

  // 根据用户角色预加载相关路由
  useEffect(() => {
    if (user?.roles && user.roles.length > 0) {
      const primaryRole = user.roles[0].code.toLowerCase();
      preloadRoutesByRole(primaryRole).then(() => {
        setPreloadStatus(getPreloadStatus());
      });
    }
  }, [user]);

  // 初始化高级预加载策略
  useEffect(() => {
    const cleanup = initializePreloadingStrategies();
    return cleanup;
  }, []);

  // 记录访问历史并进行预测性预加载
  useEffect(() => {
    const currentPath = location.pathname;
    
    // 更新访问历史
    visitHistoryRef.current = [...visitHistoryRef.current, currentPath].slice(-10); // 保留最近10次访问
    
    // 根据当前路由预加载相关路由
    preloadRelatedRoutes(currentPath).then(() => {
      setPreloadStatus(getPreloadStatus());
    });
    
    // 基于历史的预测性预加载
    if (visitHistoryRef.current.length >= 3) {
      preloadPredictive(visitHistoryRef.current);
    }
  }, [location.pathname]);

  // 初始化时的空闲预加载
  useEffect(() => {
    // 在空闲时间预加载常用路由
    preloadOnIdle(['patients', 'appointments', 'statistics']);
    
    // 定期更新预加载状态
    const statusInterval = setInterval(() => {
      setPreloadStatus(getPreloadStatus());
    }, 5000);
    
    return () => {
      clearInterval(statusInterval);
    };
  }, []);

  // 页面卸载时清理
  useEffect(() => {
    const handleBeforeUnload = () => {
      // 在开发环境下保留缓存，生产环境清理
      if (process.env.NODE_ENV === 'production') {
        clearPreloadCache();
      }
    };
    
    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, []);

  return {
    preloadStatus,
    visitHistory: visitHistoryRef.current
  };
};

// 菜单项预加载Hook
export const useMenuPreloader = () => {
  const hoverTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  
  const handleMenuHover = (routeName: string) => {
    // 防抖处理，避免频繁预加载
    if (hoverTimeoutRef.current) {
      clearTimeout(hoverTimeoutRef.current);
    }
    
    hoverTimeoutRef.current = setTimeout(() => {
      // 鼠标悬停时预加载对应路由
      import('../utils/routePreloader').then(({ preloadOnHover }) => {
        preloadOnHover(routeName);
      });
    }, 200); // 200ms延迟，避免快速划过时的无效预加载
  };
  
  const handleMenuLeave = () => {
    // 鼠标离开时取消预加载
    if (hoverTimeoutRef.current) {
      clearTimeout(hoverTimeoutRef.current);
      hoverTimeoutRef.current = null;
    }
  };

  useEffect(() => {
    return () => {
      if (hoverTimeoutRef.current) {
        clearTimeout(hoverTimeoutRef.current);
      }
    };
  }, []);

  return { 
    handleMenuHover, 
    handleMenuLeave 
  };
};

// 智能预加载Hook - 基于用户交互
export const useSmartPreloader = () => {
  const [isVisible, setIsVisible] = useState(true);
  const [isOnline, setIsOnline] = useState(navigator.onLine);
  
  // 监听页面可见性
  useEffect(() => {
    const handleVisibilityChange = () => {
      setIsVisible(!document.hidden);
    };
    
    document.addEventListener('visibilitychange', handleVisibilityChange);
    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange);
    };
  }, []);
  
  // 监听网络状态
  useEffect(() => {
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);
    
    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);
    
    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);
  
  return {
    shouldPreload: isVisible && isOnline,
    isVisible,
    isOnline
  };
};