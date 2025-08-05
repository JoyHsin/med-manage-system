// 路由预加载工具
interface RoutePreloader {
  [key: string]: () => Promise<any>;
}

interface PreloadConfig {
  priority: 'high' | 'medium' | 'low';
  condition?: () => boolean;
  delay?: number;
  networkAware?: boolean;
}

// 预加载配置
const preloadConfigs: { [key: string]: PreloadConfig } = {
  // 高优先级 - 核心功能
  patients: { priority: 'high', networkAware: true },
  appointments: { priority: 'high', networkAware: true },
  
  // 中优先级 - 常用功能
  users: { priority: 'medium', delay: 1000 },
  roles: { priority: 'medium', delay: 1000 },
  staff: { priority: 'medium', delay: 1500 },
  triage: { priority: 'medium', networkAware: true },
  vitalSigns: { priority: 'medium', networkAware: true },
  medicalRecords: { priority: 'medium', networkAware: true },
  
  // 低优先级 - 辅助功能
  statistics: { priority: 'low', delay: 3000 },
  reports: { priority: 'low', delay: 3000 },
  tokenDebug: { priority: 'low', delay: 5000, condition: () => process.env.NODE_ENV === 'development' },
  
  // 按需加载 - 专业功能
  medicalOrders: { priority: 'medium', networkAware: true },
  prescriptions: { priority: 'medium', networkAware: true },
  medicalOrderCreation: { priority: 'medium', networkAware: true },
  billing: { priority: 'medium', networkAware: true },
  inventory: { priority: 'medium', networkAware: true },
  pharmacyDispensing: { priority: 'medium', networkAware: true },
  prescriptionQueue: { priority: 'medium', networkAware: true },
  registrations: { priority: 'medium', networkAware: true },
};

// 定义所有懒加载的路由组件
export const routePreloaders: RoutePreloader = {
  // 用户管理模块
  users: () => import('../pages/SimpleUserManagement'),
  roles: () => import('../pages/RolePermissionManagement'),
  
  // 患者管理模块
  patients: () => import('../pages/SimplePatientManagement'),
  staff: () => import('../pages/SimpleStaffManagement'),
  
  // 预约和挂号模块
  appointments: () => import('../pages/AppointmentManagement'),
  registrations: () => import('../pages/RegistrationManagement'),
  
  // 护士工作台模块
  triage: () => import('../pages/TriageManagement'),
  vitalSigns: () => import('../pages/SimpleVitalSignsManagement'),
  
  // 医生工作台模块
  medicalOrders: () => import('../pages/MedicalOrderManagement'),
  medicalRecords: () => import('../pages/MedicalRecordManagement'),
  prescriptions: () => import('../pages/PrescriptionManagement'),
  medicalOrderCreation: () => import('../pages/MedicalOrderCreation'),
  
  // 收费管理模块
  billing: () => import('../pages/BillingManagement'),
  
  // 药房管理模块
  inventory: () => import('../pages/InventoryManagement'),
  pharmacyDispensing: () => import('../pages/PharmacyDispensing'),
  prescriptionQueue: () => import('../pages/PrescriptionQueueManagement'),
  
  // 统计和报表模块
  statistics: () => import('../pages/StatisticsDashboard'),
  reports: () => import('../pages/ReportManagement'),
  
  // 调试页面
  tokenDebug: () => import('../pages/TokenDebugPage'),
};

// 网络状态检测
const getNetworkInfo = () => {
  const connection = (navigator as any).connection;
  if (!connection) return { effectiveType: '4g', saveData: false };
  
  return {
    effectiveType: connection.effectiveType,
    saveData: connection.saveData,
    downlink: connection.downlink || 1.5
  };
};

// 检查是否应该预加载
const shouldPreload = (routeName: string): boolean => {
  const config = preloadConfigs[routeName];
  if (!config) return true;
  
  // 检查条件
  if (config.condition && !config.condition()) {
    return false;
  }
  
  // 网络感知预加载
  if (config.networkAware) {
    const networkInfo = getNetworkInfo();
    
    // 如果用户开启了数据节省模式，只预加载高优先级
    if (networkInfo.saveData && config.priority !== 'high') {
      return false;
    }
    
    // 慢网络下只预加载高优先级
    if ((networkInfo.effectiveType === '2g' || networkInfo.effectiveType === 'slow-2g') && 
        config.priority === 'low') {
      return false;
    }
  }
  
  return true;
};

// 预加载缓存
const preloadCache = new Set<string>();
const preloadPromises = new Map<string, Promise<any>>();

// 预加载指定路由
export const preloadRoute = (routeName: string): Promise<any> => {
  // 检查是否已经预加载过
  if (preloadCache.has(routeName)) {
    return Promise.resolve();
  }
  
  // 检查是否正在预加载
  if (preloadPromises.has(routeName)) {
    return preloadPromises.get(routeName)!;
  }
  
  // 检查是否应该预加载
  if (!shouldPreload(routeName)) {
    return Promise.resolve();
  }
  
  const preloader = routePreloaders[routeName];
  if (!preloader) {
    return Promise.resolve();
  }
  
  const config = preloadConfigs[routeName];
  const delay = config?.delay || 0;
  
  const preloadPromise = new Promise<any>((resolve) => {
    setTimeout(async () => {
      try {
        console.log(`🚀 Preloading route: ${routeName}`);
        const result = await preloader();
        preloadCache.add(routeName);
        console.log(`✅ Route preloaded: ${routeName}`);
        resolve(result);
      } catch (error) {
        console.warn(`❌ Failed to preload route: ${routeName}`, error);
        resolve(null);
      } finally {
        preloadPromises.delete(routeName);
      }
    }, delay);
  });
  
  preloadPromises.set(routeName, preloadPromise);
  return preloadPromise;
};

// 智能批量预加载路由
export const preloadRoutes = (routeNames: string[]): Promise<any[]> => {
  // 按优先级排序
  const sortedRoutes = routeNames.sort((a, b) => {
    const priorityOrder = { high: 0, medium: 1, low: 2 };
    const aPriority = preloadConfigs[a]?.priority || 'medium';
    const bPriority = preloadConfigs[b]?.priority || 'medium';
    return priorityOrder[aPriority] - priorityOrder[bPriority];
  });
  
  return Promise.allSettled(sortedRoutes.map(preloadRoute));
};

// 根据用户角色智能预加载相关路由
export const preloadRoutesByRole = (userRole: string): Promise<any[]> => {
  const roleRouteMap: { [key: string]: string[] } = {
    admin: ['users', 'roles', 'patients', 'staff', 'statistics', 'reports'],
    doctor: ['patients', 'appointments', 'medicalRecords', 'prescriptions', 'medicalOrders'],
    nurse: ['patients', 'triage', 'vitalSigns', 'medicalOrders'],
    receptionist: ['patients', 'appointments', 'registrations', 'billing'],
    pharmacist: ['prescriptions', 'inventory', 'pharmacyDispensing', 'prescriptionQueue'],
  };
  
  const routes = roleRouteMap[userRole.toLowerCase()] || [];
  console.log(`👤 Preloading routes for role: ${userRole}`, routes);
  return preloadRoutes(routes);
};

// 智能预加载：根据当前路由预加载相关路由
export const preloadRelatedRoutes = (currentRoute: string): Promise<any[]> => {
  const relatedRoutesMap: { [key: string]: string[] } = {
    '/dashboard': ['patients', 'appointments', 'statistics'],
    '/patients': ['appointments', 'medicalRecords', 'billing'],
    '/appointments': ['patients', 'registrations'],
    '/triage': ['vitalSigns', 'medicalOrders'],
    '/medical-records': ['prescriptions', 'medicalOrders'],
    '/prescriptions': ['pharmacyDispensing', 'prescriptionQueue'],
    '/billing': ['patients', 'reports'],
    '/inventory': ['pharmacyDispensing'],
    '/statistics': ['reports'],
  };
  
  const relatedRoutes = relatedRoutesMap[currentRoute] || [];
  if (relatedRoutes.length > 0) {
    console.log(`🔗 Preloading related routes for ${currentRoute}:`, relatedRoutes);
  }
  return preloadRoutes(relatedRoutes);
};

// 在空闲时间预加载
export const preloadOnIdle = (routeNames: string[]): void => {
  const idleCallback = () => {
    console.log('⏰ Idle preloading:', routeNames);
    preloadRoutes(routeNames);
  };
  
  if ('requestIdleCallback' in window) {
    window.requestIdleCallback(idleCallback, { timeout: 5000 });
  } else {
    // 降级方案：使用setTimeout
    setTimeout(idleCallback, 100);
  }
};

// 鼠标悬停时预加载
export const preloadOnHover = (routeName: string): void => {
  console.log(`🖱️ Hover preloading: ${routeName}`);
  preloadRoute(routeName);
};

// 增强的预测性预加载策略
export const preloadPredictive = (visitHistory: string[]): void => {
  if (visitHistory.length < 2) return;
  
  // 分析用户访问模式
  const patterns: { [key: string]: string[] } = {};
  const timePatterns: { [key: string]: { route: string; timestamp: number }[] } = {};
  
  for (let i = 0; i < visitHistory.length - 1; i++) {
    const current = visitHistory[i];
    const next = visitHistory[i + 1];
    
    if (!patterns[current]) {
      patterns[current] = [];
    }
    patterns[current].push(next);
    
    // 记录时间模式
    if (!timePatterns[current]) {
      timePatterns[current] = [];
    }
    timePatterns[current].push({
      route: next,
      timestamp: Date.now() - (visitHistory.length - i) * 60000 // 模拟时间戳
    });
  }
  
  // 预加载最可能访问的路由
  const currentRoute = visitHistory[visitHistory.length - 1];
  const likelyNext = patterns[currentRoute];
  
  if (likelyNext && likelyNext.length > 0) {
    // 计算路由权重（频率 + 时间衰减）
    const routeWeights: { [key: string]: number } = {};
    const now = Date.now();
    
    likelyNext.forEach(route => {
      const frequency = (routeWeights[route] || 0) + 1;
      
      // 时间衰减因子：最近访问的权重更高
      const timeEntries = timePatterns[currentRoute]?.filter(entry => entry.route === route) || [];
      const avgTimeDiff = timeEntries.reduce((sum, entry) => sum + (now - entry.timestamp), 0) / timeEntries.length;
      const timeDecay = Math.exp(-avgTimeDiff / (24 * 60 * 60 * 1000)); // 24小时衰减
      
      routeWeights[route] = frequency * (1 + timeDecay);
    });
    
    const mostLikely = Object.entries(routeWeights)
      .sort(([, a], [, b]) => b - a)
      .slice(0, 3) // 预加载前3个最可能的路由
      .map(([route]) => route);
    
    console.log(`🔮 Predictive preloading based on weighted history:`, mostLikely);
    preloadRoutes(mostLikely);
  }
};

// 基于时间的智能预加载
export const preloadByTimePattern = (): void => {
  const now = new Date();
  const hour = now.getHours();
  const dayOfWeek = now.getDay();
  
  // 工作时间模式预加载
  const workHourRoutes = ['patients', 'appointments', 'triage', 'medicalRecords'];
  const lunchTimeRoutes = ['statistics', 'reports'];
  const eveningRoutes = ['billing', 'inventory'];
  
  let routesToPreload: string[] = [];
  
  if (hour >= 8 && hour <= 12) {
    // 上午：主要业务功能
    routesToPreload = workHourRoutes;
  } else if (hour >= 12 && hour <= 14) {
    // 午休：统计和报表
    routesToPreload = lunchTimeRoutes;
  } else if (hour >= 14 && hour <= 18) {
    // 下午：继续业务功能
    routesToPreload = workHourRoutes;
  } else if (hour >= 18 && hour <= 22) {
    // 晚上：结算和库存
    routesToPreload = eveningRoutes;
  }
  
  // 周末模式：减少预加载
  if (dayOfWeek === 0 || dayOfWeek === 6) {
    routesToPreload = routesToPreload.slice(0, 2);
  }
  
  if (routesToPreload.length > 0) {
    console.log(`⏰ Time-based preloading (${hour}:00):`, routesToPreload);
    preloadRoutes(routesToPreload);
  }
};

// 基于设备性能的自适应预加载
export const preloadAdaptive = (): void => {
  const getDeviceCapabilities = () => {
    const memory = (navigator as any).deviceMemory || 4; // GB
    const cores = navigator.hardwareConcurrency || 4;
    const connection = (navigator as any).connection;
    
    return {
      memory,
      cores,
      networkType: connection?.effectiveType || '4g',
      saveData: connection?.saveData || false
    };
  };
  
  const capabilities = getDeviceCapabilities();
  let preloadLevel = 'medium';
  
  // 根据设备能力调整预加载策略
  if (capabilities.memory >= 8 && capabilities.cores >= 8 && capabilities.networkType === '4g') {
    preloadLevel = 'aggressive';
  } else if (capabilities.memory <= 2 || capabilities.saveData || capabilities.networkType === '2g') {
    preloadLevel = 'conservative';
  }
  
  const preloadStrategies = {
    conservative: ['patients', 'appointments'], // 只预加载核心功能
    medium: ['patients', 'appointments', 'triage', 'medicalRecords'], // 常用功能
    aggressive: Object.keys(routePreloaders).slice(0, 8) // 预加载更多功能
  };
  
  const routesToPreload = preloadStrategies[preloadLevel as keyof typeof preloadStrategies];
  console.log(`🎯 Adaptive preloading (${preloadLevel}):`, routesToPreload);
  preloadRoutes(routesToPreload);
};

// 基于用户交互的预加载
export const preloadOnInteraction = (): void => {
  let interactionTimeout: NodeJS.Timeout;
  
  const handleUserInteraction = () => {
    clearTimeout(interactionTimeout);
    
    // 用户活跃时延迟预加载，避免影响交互性能
    interactionTimeout = setTimeout(() => {
      console.log('👆 User interaction-based preloading');
      preloadAdaptive();
    }, 2000);
  };
  
  // 监听用户交互事件
  ['mousedown', 'keydown', 'touchstart', 'scroll'].forEach(event => {
    document.addEventListener(event, handleUserInteraction, { passive: true });
  });
  
  // 清理函数
  return () => {
    clearTimeout(interactionTimeout);
    ['mousedown', 'keydown', 'touchstart', 'scroll'].forEach(event => {
      document.removeEventListener(event, handleUserInteraction);
    });
  };
};

// 清理预加载缓存
export const clearPreloadCache = (): void => {
  preloadCache.clear();
  preloadPromises.clear();
  console.log('🧹 Preload cache cleared');
};

// 预加载策略协调器
export const initializePreloadingStrategies = () => {
  console.log('🚀 Initializing advanced preloading strategies');
  
  // 立即执行基于时间的预加载
  preloadByTimePattern();
  
  // 启动自适应预加载
  setTimeout(() => {
    preloadAdaptive();
  }, 1000);
  
  // 启动基于交互的预加载
  const cleanupInteraction = preloadOnInteraction();
  
  // 定期执行时间模式预加载
  const timePatternInterval = setInterval(() => {
    preloadByTimePattern();
  }, 60 * 60 * 1000); // 每小时检查一次
  
  // 监听网络状态变化
  const handleNetworkChange = () => {
    console.log('🌐 Network status changed, adjusting preload strategy');
    setTimeout(() => {
      preloadAdaptive();
    }, 1000);
  };
  
  window.addEventListener('online', handleNetworkChange);
  window.addEventListener('offline', handleNetworkChange);
  
  // 监听内存压力
  if ('memory' in performance) {
    const checkMemoryPressure = () => {
      const memory = (performance as any).memory;
      if (memory && memory.usedJSHeapSize / memory.jsHeapSizeLimit > 0.8) {
        console.warn('⚠️ High memory usage detected, reducing preload activity');
        // 清理一些缓存
        const cacheArray = Array.from(preloadCache);
        const toRemove = cacheArray.slice(Math.floor(cacheArray.length * 0.3));
        toRemove.forEach(key => preloadCache.delete(key));
      }
    };
    
    const memoryCheckInterval = setInterval(checkMemoryPressure, 30000);
    
    return () => {
      cleanupInteraction();
      clearInterval(timePatternInterval);
      clearInterval(memoryCheckInterval);
      window.removeEventListener('online', handleNetworkChange);
      window.removeEventListener('offline', handleNetworkChange);
    };
  }
  
  return () => {
    cleanupInteraction();
    clearInterval(timePatternInterval);
    window.removeEventListener('online', handleNetworkChange);
    window.removeEventListener('offline', handleNetworkChange);
  };
};

// 获取增强的预加载状态
export const getPreloadStatus = () => {
  const networkInfo = getNetworkInfo();
  const memoryInfo = (performance as any).memory;
  
  return {
    cached: Array.from(preloadCache),
    loading: Array.from(preloadPromises.keys()),
    total: Object.keys(routePreloaders).length,
    cacheHitRate: preloadCache.size / Object.keys(routePreloaders).length,
    networkInfo: {
      type: networkInfo.effectiveType,
      saveData: networkInfo.saveData,
      downlink: networkInfo.downlink
    },
    memoryInfo: memoryInfo ? {
      used: Math.round(memoryInfo.usedJSHeapSize / 1024 / 1024),
      total: Math.round(memoryInfo.totalJSHeapSize / 1024 / 1024),
      limit: Math.round(memoryInfo.jsHeapSizeLimit / 1024 / 1024),
      usage: Math.round((memoryInfo.usedJSHeapSize / memoryInfo.jsHeapSizeLimit) * 100)
    } : null,
    timestamp: new Date().toISOString()
  };
};