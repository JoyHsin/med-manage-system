import React, { Suspense, ComponentType } from 'react';
import { Result, Button } from 'antd';
import LazyLoadingSpinner from '../components/LazyLoadingSpinner';

// 懒加载组件的错误边界
class LazyLoadErrorBoundary extends React.Component<
  { children: React.ReactNode; fallback?: React.ComponentType },
  { hasError: boolean; error?: Error }
> {
  constructor(props: { children: React.ReactNode; fallback?: React.ComponentType }) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('Lazy component loading error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        const FallbackComponent = this.props.fallback;
        return <FallbackComponent />;
      }

      return (
        <Result
          status="error"
          title="页面加载失败"
          subTitle="抱歉，页面加载时出现了错误。请刷新页面重试。"
          extra={[
            <Button type="primary" key="refresh" onClick={() => window.location.reload()}>
              刷新页面
            </Button>,
            <Button key="back" onClick={() => window.history.back()}>
              返回上页
            </Button>
          ]}
        />
      );
    }

    return this.props.children;
  }
}

// 增强的懒加载组件包装器
export const createLazyComponent = <T extends ComponentType<any>>(
  importFunc: () => Promise<{ default: T }>,
  options: {
    fallback?: React.ComponentType;
    loadingTip?: string;
    retryCount?: number;
    preload?: boolean;
    priority?: 'high' | 'medium' | 'low';
    networkAware?: boolean;
    cacheKey?: string;
  } = {}
) => {
  const { 
    fallback, 
    loadingTip = '正在加载...', 
    retryCount = 3,
    preload = false,
    priority = 'medium',
    networkAware = false,
    cacheKey
  } = options;

  // 组件缓存
  const componentCache = new Map<string, Promise<{ default: T }>>();
  
  // 网络状态检测
  const getNetworkInfo = () => {
    const connection = (navigator as any).connection;
    return connection ? {
      effectiveType: connection.effectiveType,
      saveData: connection.saveData,
      downlink: connection.downlink || 1.5
    } : { effectiveType: '4g', saveData: false, downlink: 1.5 };
  };

  // 智能重试策略
  const getRetryDelay = (attempt: number, networkType: string) => {
    const baseDelay = networkType === 'slow-2g' || networkType === '2g' ? 2000 : 1000;
    return baseDelay * Math.pow(1.5, attempt); // 指数退避
  };

  // 带智能重试机制的导入函数
  const importWithRetry = async (retries = retryCount): Promise<{ default: T }> => {
    const cacheKeyToUse = cacheKey || importFunc.toString();
    
    // 检查缓存
    if (componentCache.has(cacheKeyToUse)) {
      return componentCache.get(cacheKeyToUse)!;
    }

    const importPromise = (async () => {
      let lastError: Error | null = null;
      
      for (let attempt = 0; attempt <= retries; attempt++) {
        try {
          // 网络感知加载
          if (networkAware) {
            const networkInfo = getNetworkInfo();
            
            // 在慢网络或数据节省模式下，降低优先级组件的加载
            if ((networkInfo.saveData || networkInfo.effectiveType === 'slow-2g') && priority === 'low') {
              throw new Error('Skipping low priority component on slow network');
            }
          }

          const startTime = performance.now();
          const result = await importFunc();
          const loadTime = performance.now() - startTime;
          
          // 性能监控
          if (loadTime > 2000) {
            console.warn(`Slow component loading: ${cacheKeyToUse} took ${loadTime.toFixed(2)}ms`);
          } else {
            console.log(`Component loaded: ${cacheKeyToUse} in ${loadTime.toFixed(2)}ms`);
          }
          
          return result;
        } catch (error) {
          lastError = error as Error;
          
          if (attempt < retries) {
            const networkInfo = getNetworkInfo();
            const delay = getRetryDelay(attempt, networkInfo.effectiveType);
            
            console.warn(`Component import failed (attempt ${attempt + 1}/${retries + 1}), retrying in ${delay}ms...`, error);
            await new Promise(resolve => setTimeout(resolve, delay));
          }
        }
      }
      
      throw lastError || new Error('Component import failed after all retries');
    })();

    // 缓存Promise
    componentCache.set(cacheKeyToUse, importPromise);
    
    try {
      return await importPromise;
    } catch (error) {
      // 失败时从缓存中移除
      componentCache.delete(cacheKeyToUse);
      throw error;
    }
  };

  const LazyComponent = React.lazy(importWithRetry);

  // 预加载功能
  if (preload) {
    // 使用requestIdleCallback进行预加载
    if ('requestIdleCallback' in window) {
      window.requestIdleCallback(() => {
        importWithRetry().catch(() => {
          // 预加载失败不影响正常使用
        });
      }, { timeout: 5000 });
    } else {
      setTimeout(() => {
        importWithRetry().catch(() => {
          // 预加载失败不影响正常使用
        });
      }, 100);
    }
  }

  return React.forwardRef<any, any>((props, ref) => (
    <LazyLoadErrorBoundary fallback={fallback}>
      <Suspense fallback={<LazyLoadingSpinner tip={loadingTip} priority={priority} />}>
        <LazyComponent {...props} ref={ref} />
      </Suspense>
    </LazyLoadErrorBoundary>
  ));
};

// 增强的预加载组件
export const preloadComponent = async (
  importFunc: () => Promise<any>,
  options: {
    priority?: 'high' | 'medium' | 'low';
    timeout?: number;
    networkAware?: boolean;
  } = {}
): Promise<any> => {
  const { priority = 'medium', timeout = 10000, networkAware = false } = options;
  
  try {
    // 网络感知预加载
    if (networkAware) {
      const connection = (navigator as any).connection;
      if (connection) {
        // 在慢网络或数据节省模式下，只预加载高优先级组件
        if ((connection.saveData || connection.effectiveType === 'slow-2g') && priority !== 'high') {
          console.log(`Skipping ${priority} priority preload on slow network`);
          return null;
        }
      }
    }

    // 带超时的预加载
    const timeoutPromise = new Promise((_, reject) => {
      setTimeout(() => reject(new Error('Preload timeout')), timeout);
    });

    const result = await Promise.race([importFunc(), timeoutPromise]);
    console.log(`Component preloaded successfully (priority: ${priority})`);
    return result;
  } catch (error) {
    console.warn(`Component preload failed (priority: ${priority}):`, error);
    return null;
  }
};

// 智能批量预加载组件
export const preloadComponents = (
  importConfigs: Array<{
    importFunc: () => Promise<any>;
    priority?: 'high' | 'medium' | 'low';
    delay?: number;
    networkAware?: boolean;
  }>
): Promise<any[]> => {
  // 按优先级排序
  const sortedConfigs = importConfigs.sort((a, b) => {
    const priorityOrder = { high: 0, medium: 1, low: 2 };
    return priorityOrder[a.priority || 'medium'] - priorityOrder[b.priority || 'medium'];
  });

  // 串行预加载高优先级，并行预加载其他
  const highPriorityConfigs = sortedConfigs.filter(config => config.priority === 'high');
  const otherConfigs = sortedConfigs.filter(config => config.priority !== 'high');

  const preloadWithDelay = async (config: typeof importConfigs[0]) => {
    if (config.delay) {
      await new Promise(resolve => setTimeout(resolve, config.delay));
    }
    return preloadComponent(config.importFunc, {
      priority: config.priority,
      networkAware: config.networkAware
    });
  };

  return Promise.allSettled([
    // 串行预加载高优先级
    ...highPriorityConfigs.map(config => preloadWithDelay(config)),
    // 并行预加载其他优先级
    ...otherConfigs.map(config => preloadWithDelay(config))
  ]);
};

// 条件懒加载：只在满足条件时才懒加载
export const createConditionalLazyComponent = <T extends ComponentType<any>>(
  condition: () => boolean,
  lazyImport: () => Promise<{ default: T }>,
  fallbackComponent: T,
  options: {
    loadingTip?: string;
    retryCount?: number;
  } = {}
) => {
  if (!condition()) {
    return fallbackComponent;
  }

  return createLazyComponent(lazyImport, options);
};

// 基于网络状态的懒加载
export const createNetworkAwareLazyComponent = <T extends ComponentType<any>>(
  importFunc: () => Promise<{ default: T }>,
  fallbackComponent: T,
  options: {
    loadingTip?: string;
    retryCount?: number;
  } = {}
) => {
  const isOnline = () => navigator.onLine;
  const hasGoodConnection = () => {
    const connection = (navigator as any).connection;
    if (!connection) return true;
    return connection.effectiveType !== 'slow-2g' && connection.effectiveType !== '2g';
  };

  return createConditionalLazyComponent(
    () => isOnline() && hasGoodConnection(),
    importFunc,
    fallbackComponent,
    options
  );
};