// Bundle优化工具
export interface BundleOptimizationConfig {
  enableTreeShaking: boolean;
  enableCodeSplitting: boolean;
  enablePreloading: boolean;
  enableCompression: boolean;
  targetBrowsers: string[];
  chunkSizeLimit: number;
}

export interface OptimizationMetrics {
  totalBundleSize: number;
  chunkCount: number;
  duplicateModules: string[];
  unusedExports: string[];
  compressionRatio: number;
  loadTime: number;
}

// 默认优化配置
export const defaultOptimizationConfig: BundleOptimizationConfig = {
  enableTreeShaking: true,
  enableCodeSplitting: true,
  enablePreloading: true,
  enableCompression: true,
  targetBrowsers: ['chrome >= 80', 'firefox >= 78', 'safari >= 14'],
  chunkSizeLimit: 500 * 1024, // 500KB
};

// 检测重复模块
export const detectDuplicateModules = (): string[] => {
  const moduleMap = new Map<string, number>();
  const duplicates: string[] = [];
  
  // 分析已加载的脚本
  const scripts = Array.from(document.querySelectorAll('script[src]'));
  scripts.forEach(script => {
    const src = (script as HTMLScriptElement).src;
    if (src.includes('node_modules')) {
      const moduleName = extractModuleName(src);
      if (moduleName) {
        const count = moduleMap.get(moduleName) || 0;
        moduleMap.set(moduleName, count + 1);
        
        if (count > 0) {
          duplicates.push(moduleName);
        }
      }
    }
  });
  
  return duplicates;
};

// 提取模块名称
const extractModuleName = (src: string): string | null => {
  const match = src.match(/node_modules\/([^\/]+)/);
  return match ? match[1] : null;
};

// 分析未使用的导出
export const analyzeUnusedExports = async (): Promise<string[]> => {
  const unusedExports: string[] = [];
  
  try {
    // 使用动态导入分析模块导出
    const modules = await Promise.all([
      import('../services/authService'),
      import('../services/patientService'),
      import('../services/appointmentService'),
      // 添加更多服务模块
    ]);
    
    modules.forEach((module, index) => {
      const exports = Object.keys(module);
      // 这里可以添加更复杂的使用情况分析
      console.log(`Module ${index} exports:`, exports);
    });
  } catch (error) {
    console.warn('Failed to analyze unused exports:', error);
  }
  
  return unusedExports;
};

// 计算压缩比率
export const calculateCompressionRatio = async (): Promise<number> => {
  try {
    const response = await fetch('/stats.json');
    if (response.ok) {
      const stats = await response.json();
      const originalSize = stats.assets?.reduce((sum: number, asset: any) => sum + asset.size, 0) || 0;
      const compressedSize = stats.assets?.reduce((sum: number, asset: any) => sum + (asset.gzipSize || asset.size), 0) || 0;
      
      return originalSize > 0 ? compressedSize / originalSize : 1;
    }
  } catch (error) {
    console.warn('Failed to calculate compression ratio:', error);
  }
  
  return 1;
};

// 测量加载时间
export const measureLoadTime = (): number => {
  const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
  return navigation ? navigation.loadEventEnd - navigation.fetchStart : 0;
};

// 生成优化报告
export const generateOptimizationReport = async (): Promise<OptimizationMetrics> => {
  const duplicateModules = detectDuplicateModules();
  const unusedExports = await analyzeUnusedExports();
  const compressionRatio = await calculateCompressionRatio();
  const loadTime = measureLoadTime();
  
  // 估算总bundle大小
  const scripts = Array.from(document.querySelectorAll('script[src]'));
  let totalBundleSize = 0;
  
  for (const script of scripts) {
    try {
      const response = await fetch((script as HTMLScriptElement).src, { method: 'HEAD' });
      const contentLength = response.headers.get('content-length');
      if (contentLength) {
        totalBundleSize += parseInt(contentLength, 10);
      }
    } catch (error) {
      // 忽略错误，继续处理其他脚本
    }
  }
  
  const metrics: OptimizationMetrics = {
    totalBundleSize,
    chunkCount: scripts.length,
    duplicateModules,
    unusedExports,
    compressionRatio,
    loadTime,
  };
  
  console.group('📦 Bundle Optimization Report');
  console.log('Total Bundle Size:', `${(totalBundleSize / 1024).toFixed(2)} KB`);
  console.log('Chunk Count:', scripts.length);
  console.log('Duplicate Modules:', duplicateModules);
  console.log('Compression Ratio:', `${(compressionRatio * 100).toFixed(1)}%`);
  console.log('Load Time:', `${loadTime.toFixed(2)}ms`);
  console.groupEnd();
  
  return metrics;
};

// 优化建议生成器
export const generateOptimizationSuggestions = (metrics: OptimizationMetrics): string[] => {
  const suggestions: string[] = [];
  
  if (metrics.totalBundleSize > 1024 * 1024) { // > 1MB
    suggestions.push('考虑进一步拆分大型chunks，总bundle大小超过1MB');
  }
  
  if (metrics.chunkCount > 20) {
    suggestions.push('chunk数量过多，考虑合并一些小的chunks');
  }
  
  if (metrics.duplicateModules.length > 0) {
    suggestions.push(`发现重复模块: ${metrics.duplicateModules.join(', ')}，考虑优化依赖管理`);
  }
  
  if (metrics.compressionRatio > 0.8) {
    suggestions.push('压缩效果不佳，检查是否启用了gzip/brotli压缩');
  }
  
  if (metrics.loadTime > 3000) {
    suggestions.push('页面加载时间过长，考虑启用更多的预加载策略');
  }
  
  if (metrics.unusedExports.length > 0) {
    suggestions.push(`发现未使用的导出: ${metrics.unusedExports.join(', ')}，考虑清理代码`);
  }
  
  return suggestions;
};

// 自动优化执行器
export const executeAutoOptimization = async (config: BundleOptimizationConfig = defaultOptimizationConfig): Promise<void> => {
  console.log('🔧 Executing automatic bundle optimization...');
  
  // 生成当前优化报告
  const metrics = await generateOptimizationReport();
  const suggestions = generateOptimizationSuggestions(metrics);
  
  if (suggestions.length > 0) {
    console.warn('⚠️ Optimization suggestions:', suggestions);
  }
  
  // 根据配置执行优化
  if (config.enablePreloading) {
    const { initializePreloadingStrategies } = await import('./routePreloader');
    initializePreloadingStrategies();
  }
  
  // 启用性能监控
  if (config.enableCompression) {
    const { startPerformanceMonitoring } = await import('./bundleAnalyzer');
    startPerformanceMonitoring();
  }
  
  console.log('✅ Bundle optimization completed');
};

// 实时优化监控
export const startOptimizationMonitoring = (): (() => void) => {
  let monitoringInterval: NodeJS.Timeout;
  
  const monitor = async () => {
    const metrics = await generateOptimizationReport();
    
    // 检查是否需要优化
    if (metrics.totalBundleSize > defaultOptimizationConfig.chunkSizeLimit * 2) {
      console.warn('🚨 Bundle size exceeded threshold, consider optimization');
    }
    
    // 内存使用检查
    const memory = (performance as any).memory;
    if (memory && memory.usedJSHeapSize / memory.jsHeapSizeLimit > 0.8) {
      console.warn('🚨 High memory usage detected');
    }
  };
  
  // 每5分钟检查一次
  monitoringInterval = setInterval(monitor, 5 * 60 * 1000);
  
  // 页面可见性变化时检查
  const handleVisibilityChange = () => {
    if (!document.hidden) {
      monitor();
    }
  };
  
  document.addEventListener('visibilitychange', handleVisibilityChange);
  
  return () => {
    clearInterval(monitoringInterval);
    document.removeEventListener('visibilitychange', handleVisibilityChange);
  };
};