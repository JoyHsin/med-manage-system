import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { resolve } from 'path'
import { visualizer } from 'rollup-plugin-visualizer'
import { createHtmlPlugin } from 'vite-plugin-html'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react({
      // 启用React优化
      babel: {
        plugins: [
          // 生产环境优化
          ...(process.env.NODE_ENV === 'production' ? [
            // 移除开发时的代码
            ['babel-plugin-transform-remove-console', { exclude: ['error', 'warn'] }],
          ] : []),
        ],
      },
    }),
    
    // HTML优化插件
    createHtmlPlugin({
      minify: process.env.NODE_ENV === 'production' ? {
        collapseWhitespace: true,
        removeComments: true,
        removeRedundantAttributes: true,
        removeScriptTypeAttributes: true,
        removeStyleLinkTypeAttributes: true,
        useShortDoctype: true,
        minifyCSS: true,
        minifyJS: true,
      } : false,
    }),
    
    // 打包分析插件
    process.env.ANALYZE && visualizer({
      filename: 'dist/stats.html',
      open: true,
      gzipSize: true,
      brotliSize: true,
      template: 'treemap', // 'treemap' | 'sunburst' | 'network'
    }),
  ].filter(Boolean),
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    open: true,
    cors: true
  },
  build: {
    rollupOptions: {
      output: {
        // 增强的手动分包策略 - 更细粒度的代码分割
        manualChunks: (id) => {
          // 第三方库分包 - 更精细的分割
          if (id.includes('node_modules')) {
            // React 核心库
            if (id.includes('react/') && !id.includes('react-dom') && !id.includes('react-router')) {
              return 'react-core';
            }
            if (id.includes('react-dom')) {
              return 'react-dom';
            }
            if (id.includes('react-router')) {
              return 'react-router';
            }
            
            // Ant Design 核心和图标分离
            if (id.includes('@ant-design/icons')) {
              return 'antd-icons';
            }
            if (id.includes('antd/') && !id.includes('@ant-design/icons')) {
              return 'antd-core';
            }
            if (id.includes('@ant-design/plots') || id.includes('@ant-design/charts')) {
              return 'antd-charts';
            }
            
            // 图表和可视化库
            if (id.includes('chart') || id.includes('d3') || id.includes('echarts')) {
              return 'charts-vendor';
            }
            
            // 日期和时间库
            if (id.includes('dayjs') || id.includes('moment')) {
              return 'date-vendor';
            }
            
            // HTTP客户端
            if (id.includes('axios')) {
              return 'http-vendor';
            }
            
            // 工具库
            if (id.includes('lodash') || id.includes('ramda') || id.includes('classnames')) {
              return 'utils-vendor';
            }
            
            // 其他第三方库
            return 'vendor';
          }
          
          // 按功能模块和页面级别分割应用代码
          if (id.includes('/pages/')) {
            // 用户管理模块 - 按页面分割
            if (id.includes('SimpleUserManagement')) return 'page-user-management';
            if (id.includes('DebugUserManagement')) return 'page-user-debug';
            if (id.includes('RolePermissionManagement')) return 'page-role-management';
            if (id.includes('StaffManagement')) return 'page-staff-management';
            
            // 患者管理模块 - 按页面分割
            if (id.includes('SimplePatientManagement')) return 'page-patient-management';
            if (id.includes('AppointmentManagement')) return 'page-appointment-management';
            if (id.includes('RegistrationManagement')) return 'page-registration-management';
            
            // 护士工作台模块 - 按页面分割
            if (id.includes('TriageManagement')) return 'page-triage-management';
            if (id.includes('VitalSignsManagement')) return 'page-vital-signs';
            
            // 医生工作台模块 - 按页面分割
            if (id.includes('MedicalOrderManagement')) return 'page-medical-order-management';
            if (id.includes('MedicalOrderCreation')) return 'page-medical-order-creation';
            if (id.includes('MedicalRecordManagement')) return 'page-medical-record';
            if (id.includes('PrescriptionManagement')) return 'page-prescription-management';
            
            // 收费管理模块
            if (id.includes('BillingManagement')) return 'page-billing-management';
            
            // 药房管理模块 - 按页面分割
            if (id.includes('InventoryManagement')) return 'page-inventory-management';
            if (id.includes('PharmacyDispensing')) return 'page-pharmacy-dispensing';
            if (id.includes('PrescriptionQueueManagement')) return 'page-prescription-queue';
            
            // 统计和报表模块 - 按页面分割
            if (id.includes('StatisticsDashboard')) return 'page-statistics-dashboard';
            if (id.includes('ReportManagement')) return 'page-report-management';
            
            // 调试页面
            if (id.includes('TokenDebugPage')) return 'page-debug';
            
            // 其他页面
            return 'pages-misc';
          }
          
          // 组件按功能分组 - 更细粒度
          if (id.includes('/components/')) {
            // 图表组件
            if (id.includes('/charts/')) return 'components-charts';
            
            // 患者管理相关组件
            if (id.includes('PatientManagement/') || id.includes('Patient')) return 'components-patient';
            
            // 表单和输入组件
            if (id.includes('Form') || id.includes('Input') || id.includes('Modal')) return 'components-forms';
            
            // 布局和导航组件
            if (id.includes('Layout') || id.includes('Menu') || id.includes('Navigation')) return 'components-layout';
            
            // 数据展示组件
            if (id.includes('Table') || id.includes('List') || id.includes('Card')) return 'components-display';
            
            // 业务逻辑组件
            if (id.includes('Management') || id.includes('Editor') || id.includes('Generator')) return 'components-business';
            
            // 其他通用组件
            return 'components-common';
          }
          
          // 服务层按模块分组
          if (id.includes('/services/')) {
            if (id.includes('authService') || id.includes('userService')) return 'services-auth';
            if (id.includes('patientService') || id.includes('appointmentService')) return 'services-patient';
            if (id.includes('medicalService') || id.includes('prescriptionService')) return 'services-medical';
            if (id.includes('billingService') || id.includes('pharmacyService')) return 'services-business';
            if (id.includes('analyticsService') || id.includes('reportService')) return 'services-analytics';
            return 'services-common';
          }
          
          // 工具函数按功能分组
          if (id.includes('/utils/')) {
            if (id.includes('lazy') || id.includes('preload') || id.includes('bundle')) return 'utils-performance';
            if (id.includes('export') || id.includes('format') || id.includes('validate')) return 'utils-data';
            return 'utils-common';
          }
          
          // 类型定义
          if (id.includes('/types/')) {
            return 'types';
          }
          
          // 上下文和钩子
          if (id.includes('/contexts/') || id.includes('/hooks/')) {
            return 'react-context-hooks';
          }
        },
        
        // 优化文件命名
        chunkFileNames: (chunkInfo) => {
          const facadeModuleId = chunkInfo.facadeModuleId ? chunkInfo.facadeModuleId.split('/').pop() : 'chunk';
          return `assets/js/[name]-[hash].js`;
        },
        entryFileNames: 'assets/js/[name]-[hash].js',
        assetFileNames: 'assets/[ext]/[name]-[hash].[ext]'
      }
    },
    
    // 增强的代码压缩和优化
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: process.env.NODE_ENV === 'production',
        drop_debugger: true,
        pure_funcs: ['console.log', 'console.info', 'console.debug'],
        // 移除未使用的代码
        dead_code: true,
        // 移除未使用的变量
        unused: true,
        // 压缩条件表达式
        conditionals: true,
        // 压缩比较操作
        comparisons: true,
        // 压缩序列
        sequences: true,
        // 内联函数
        inline: 2,
        // 移除重复代码
        reduce_vars: true,
        // 压缩对象属性访问
        properties: true,
      },
      mangle: {
        safari10: true,
        // 保留类名（用于调试）
        keep_classnames: process.env.NODE_ENV !== 'production',
        // 保留函数名（用于调试）
        keep_fnames: process.env.NODE_ENV !== 'production',
      },
      format: {
        // 移除注释
        comments: false,
        // 压缩输出
        beautify: false,
      },
    },
    
    // 增强的构建配置
    target: ['es2020', 'chrome80', 'firefox78', 'safari14'], // 更精确的目标浏览器
    cssCodeSplit: true,
    sourcemap: process.env.NODE_ENV !== 'production',
    
    // 更严格的chunk大小限制
    chunkSizeWarningLimit: 500, // 进一步降低警告阈值
    
    // 启用详细的构建分析
    reportCompressedSize: true,
    
    // 优化资源内联
    assetsInlineLimit: 4096, // 4KB以下的资源内联为base64
    
    // 优化CommonJS处理
    commonjsOptions: {
      include: [/node_modules/],
      transformMixedEsModules: true,
      // 优化动态require
      dynamicRequireTargets: [],
    },
    
    // 启用实验性优化
    experimentalMinChunkSize: 1024, // 最小chunk大小1KB
    
    // CSS优化
    cssMinify: true,
  },
  
  // 增强的依赖预构建优化
  optimizeDeps: {
    include: [
      'react',
      'react-dom',
      'react-router-dom',
      'antd',
      '@ant-design/icons',
      'axios',
      'dayjs',
      // 预构建常用的工具库
      'classnames',
      'lodash-es/debounce',
      'lodash-es/throttle',
    ],
    exclude: [
      '@ant-design/plots', // 大型图表库按需加载
      '@ant-design/charts', // 图表组件按需加载
    ],
    // 强制预构建某些依赖
    force: process.env.NODE_ENV === 'development',
    // 优化预构建缓存
    esbuildOptions: {
      target: 'es2020',
      // 启用tree shaking
      treeShaking: true,
      // 压缩预构建的依赖
      minify: process.env.NODE_ENV === 'production',
    },
  },
  
  // 启用实验性功能
  experimental: {
    renderBuiltUrl(filename, { hostType }) {
      if (hostType === 'js') {
        return { js: `/${filename}` };
      } else {
        return { relative: true };
      }
    }
  }
})
