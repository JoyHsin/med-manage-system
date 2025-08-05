// Bundle分析工具
export interface BundleInfo {
  name: string;
  size: number;
  gzipSize?: number;
  modules: string[];
  loadTime?: number;
  cacheHit?: boolean;
}

export interface ChunkLoadMetrics {
  name: string;
  size: number;
  loadTime: number;
  cacheStatus: 'hit' | 'miss' | 'unknown';
  priority: 'high' | 'medium' | 'low';
}

// 获取当前加载的chunks信息
export const getLoadedChunks = (): string[] => {
  const scripts = Array.from(document.querySelectorAll('script[src]'));
  return scripts
    .map(script => (script as HTMLScriptElement).src)
    .filter(src => src.includes('assets/'))
    .map(src => {
      const match = src.match(/assets\/js\/(.+?)\.js/);
      return match ? match[1] : '';
    })
    .filter(Boolean);
};

// 增强的chunk加载性能监控
export const monitorChunkLoading = () => {
  const chunkMetrics: ChunkLoadMetrics[] = [];
  
  if ('performance' in window && 'getEntriesByType' in performance) {
    const observer = new PerformanceObserver((list) => {
      list.getEntries().forEach((entry) => {
        if (entry.name.includes('assets/js/') && entry.name.endsWith('.js')) {
          const chunkName = entry.name.match(/assets\/js\/(.+?)\.js/)?.[1] || 'unknown';
          const transferSize = (entry as any).transferSize || 0;
          const loadTime = entry.duration;
          
          // 判断缓存状态
          let cacheStatus: 'hit' | 'miss' | 'unknown' = 'unknown';
          if (transferSize === 0) {
            cacheStatus = 'hit';
          } else if ((entry as any).decodedBodySize > 0) {
            cacheStatus = 'miss';
          }
          
          // 判断优先级
          let priority: 'high' | 'medium' | 'low' = 'medium';
          if (chunkName.includes('vendor') || chunkName.includes('react')) {
            priority = 'high';
          } else if (chunkName.includes('components') || chunkName.includes('utils')) {
            priority = 'low';
          }
          
          const metric: ChunkLoadMetrics = {
            name: chunkName,
            size: transferSize,
            loadTime,
            cacheStatus,
            priority
          };
          
          chunkMetrics.push(metric);
          
          // 性能警告
          if (loadTime > 1000) {
            console.warn(`Slow chunk loading detected: ${chunkName} took ${loadTime.toFixed(2)}ms`);
          }
          
          if (transferSize > 500 * 1024) { // 500KB
            console.warn(`Large chunk detected: ${chunkName} is ${(transferSize / 1024).toFixed(2)}KB`);
          }
          
          console.log(`Chunk loaded: ${chunkName}`, {
            duration: `${loadTime.toFixed(2)}ms`,
            size: `${(transferSize / 1024).toFixed(2)}KB`,
            cacheStatus,
            priority,
            startTime: entry.startTime
          });
        }
      });
    });
    
    observer.observe({ entryTypes: ['resource'] });
    
    return () => {
      observer.disconnect();
      return chunkMetrics;
    };
  }
  
  return () => chunkMetrics;
};

// 预估bundle大小
export const estimateBundleSize = async (modulePath: string): Promise<number> => {
  try {
    const response = await fetch(modulePath, { method: 'HEAD' });
    const contentLength = response.headers.get('content-length');
    return contentLength ? parseInt(contentLength, 10) : 0;
  } catch (error) {
    console.warn('Failed to estimate bundle size:', error);
    return 0;
  }
};

// 检查浏览器功能支持
export const getBrowserCapabilities = () => {
  return {
    dynamicImport: (() => {
      try {
        new Function('import("")');
        return true;
      } catch {
        return false;
      }
    })(),
    
    modulePreload: (() => {
      const link = document.createElement('link');
      return 'relList' in link && link.relList.supports('modulepreload');
    })(),
    
    intersectionObserver: 'IntersectionObserver' in window,
    
    webWorkers: 'Worker' in window,
    
    serviceWorker: 'serviceWorker' in navigator,
    
    http2: (() => {
      // 检查是否支持HTTP/2 (简单检测)
      return window.location.protocol === 'https:' || window.location.hostname === 'localhost';
    })(),
    
    connectionType: (() => {
      const connection = (navigator as any).connection;
      return connection ? {
        effectiveType: connection.effectiveType,
        downlink: connection.downlink,
        rtt: connection.rtt,
        saveData: connection.saveData
      } : null;
    })()
  };
};

// 网络状态检测
export const getNetworkInfo = () => {
  const connection = (navigator as any).connection;
  if (!connection) return null;
  
  return {
    effectiveType: connection.effectiveType, // '4g', '3g', '2g', 'slow-2g'
    downlink: connection.downlink, // Mbps
    rtt: connection.rtt, // ms
    saveData: connection.saveData, // boolean
    isSlowConnection: connection.effectiveType === 'slow-2g' || connection.effectiveType === '2g',
    isFastConnection: connection.effectiveType === '4g' && connection.downlink > 1.5
  };
};

// 内存使用情况监控
export const getMemoryInfo = () => {
  const memory = (performance as any).memory;
  if (!memory) return null;
  
  return {
    usedJSHeapSize: memory.usedJSHeapSize,
    totalJSHeapSize: memory.totalJSHeapSize,
    jsHeapSizeLimit: memory.jsHeapSizeLimit,
    usagePercentage: (memory.usedJSHeapSize / memory.jsHeapSizeLimit) * 100
  };
};

// 综合性能监控报告
export const generatePerformanceReport = () => {
  const report = {
    timestamp: new Date().toISOString(),
    loadedChunks: getLoadedChunks(),
    browserCapabilities: getBrowserCapabilities(),
    networkInfo: getNetworkInfo(),
    memoryInfo: getMemoryInfo(),
    performanceMetrics: {
      navigationStart: performance.timing?.navigationStart,
      domContentLoaded: performance.timing?.domContentLoadedEventEnd - performance.timing?.navigationStart,
      loadComplete: performance.timing?.loadEventEnd - performance.timing?.navigationStart,
      firstPaint: (() => {
        const paintEntries = performance.getEntriesByType('paint');
        const firstPaint = paintEntries.find(entry => entry.name === 'first-paint');
        return firstPaint ? firstPaint.startTime : null;
      })(),
      firstContentfulPaint: (() => {
        const paintEntries = performance.getEntriesByType('paint');
        const fcp = paintEntries.find(entry => entry.name === 'first-contentful-paint');
        return fcp ? fcp.startTime : null;
      })()
    }
  };
  
  console.group('📊 Performance Report');
  console.table(report.loadedChunks);
  console.log('🌐 Network Info:', report.networkInfo);
  console.log('💾 Memory Info:', report.memoryInfo);
  console.log('⚡ Performance Metrics:', report.performanceMetrics);
  console.groupEnd();
  
  return report;
};

// 自动性能监控
export const startPerformanceMonitoring = () => {
  const stopChunkMonitoring = monitorChunkLoading();
  
  // 定期生成性能报告
  const reportInterval = setInterval(() => {
    const memoryInfo = getMemoryInfo();
    if (memoryInfo && memoryInfo.usagePercentage > 80) {
      console.warn('⚠️ High memory usage detected:', memoryInfo);
    }
  }, 30000); // 每30秒检查一次
  
  // 页面卸载时生成最终报告
  const handleBeforeUnload = () => {
    generatePerformanceReport();
  };
  
  window.addEventListener('beforeunload', handleBeforeUnload);
  
  return () => {
    const chunkMetrics = stopChunkMonitoring();
    clearInterval(reportInterval);
    window.removeEventListener('beforeunload', handleBeforeUnload);
    return chunkMetrics;
  };
};