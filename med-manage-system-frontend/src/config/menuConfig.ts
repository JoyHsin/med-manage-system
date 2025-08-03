import React from 'react';
import type { MenuProps } from 'antd';

export interface MenuItem {
  key: string;
  iconType?: string; // 改为字符串类型，在组件中动态创建图标
  label: string;
  children?: MenuItem[];
  permissions?: string[]; // 添加权限控制
}

export const menuItems: MenuItem[] = [
  {
    key: '/dashboard',
    iconType: 'DashboardOutlined',
    label: '仪表盘',
  },
  {
    key: 'system',
    iconType: 'SafetyOutlined',
    label: '系统管理',
    permissions: ['SYSTEM_MANAGE'],
    children: [
      {
        key: '/users',
        iconType: 'UserOutlined',
        label: '用户管理',
        permissions: ['USER_MANAGE'],
      },
      {
        key: '/roles',
        iconType: 'TeamOutlined',
        label: '角色权限',
        permissions: ['ROLE_MANAGE'],
      },
    ],
  },
  {
    key: 'basic',
    iconType: 'TeamOutlined',
    label: '基础信息',
    children: [
      {
        key: '/patients',
        iconType: 'UserOutlined',
        label: '患者管理',
        permissions: ['PATIENT_MANAGE'],
      },
      {
        key: '/staff',
        iconType: 'TeamOutlined',
        label: '医护人员',
        permissions: ['STAFF_MANAGE'],
      },
      {
        key: '/inventory',
        iconType: 'MedicineBoxOutlined',
        label: '药品库存',
        permissions: ['INVENTORY_MANAGE'],
      },
    ],
  },
  {
    key: 'clinic',
    iconType: 'CalendarOutlined',
    label: '门诊管理',
    children: [
      {
        key: '/appointments',
        iconType: 'CalendarOutlined',
        label: '预约管理',
        permissions: ['APPOINTMENT_MANAGE'],
      },
      {
        key: '/registrations',
        iconType: 'ClockCircleOutlined',
        label: '挂号管理',
        permissions: ['REGISTRATION_MANAGE'],
      },
    ],
  },
  {
    key: 'nurse',
    iconType: 'HeartOutlined',
    label: '护士工作台',
    children: [
      {
        key: '/triage',
        iconType: 'SoundOutlined',
        label: '分诊叫号',
        permissions: ['TRIAGE_MANAGE'],
      },
      {
        key: '/vital-signs',
        iconType: 'HeartOutlined',
        label: '生命体征',
        permissions: ['VITAL_SIGNS_MANAGE'],
      },
      {
        key: '/medical-orders',
        iconType: 'FileTextOutlined',
        label: '医嘱执行',
        permissions: ['MEDICAL_ORDER_EXECUTE'],
      },
    ],
  },
  {
    key: 'doctor',
    iconType: 'UserOutlined',
    label: '医生工作台',
    children: [
      {
        key: '/medical-records',
        iconType: 'FileTextOutlined',
        label: '电子病历',
        permissions: ['MEDICAL_RECORD_MANAGE'],
      },
      {
        key: '/prescriptions',
        iconType: 'MedicineBoxOutlined',
        label: '处方管理',
        permissions: ['PRESCRIPTION_MANAGE'],
      },
      {
        key: '/medical-order-creation',
        iconType: 'FileTextOutlined',
        label: '医嘱开具',
        permissions: ['MEDICAL_ORDER_CREATE'],
      },
    ],
  },
  {
    key: 'billing',
    iconType: 'DollarOutlined',
    label: '收费管理',
    children: [
      {
        key: '/billing',
        iconType: 'DollarOutlined',
        label: '费用管理',
        permissions: ['BILLING_MANAGE'],
      },
    ],
  },
  {
    key: 'pharmacy',
    iconType: 'MedicineBoxOutlined',
    label: '药房管理',
    children: [
      {
        key: '/pharmacy-dispensing',
        iconType: 'ExperimentOutlined',
        label: '处方调剂',
        permissions: ['PHARMACY_DISPENSE'],
      },
    ],
  },
];

// 权限过滤函数
export const filterMenuByPermissions = (
  items: MenuItem[], 
  userPermissions: string[] = []
): MenuItem[] => {
  return items.filter(item => {
    // 如果没有定义权限要求，则显示该菜单项
    if (!item.permissions || item.permissions.length === 0) {
      if (item.children) {
        const filteredChildren = filterMenuByPermissions(item.children, userPermissions);
        return filteredChildren.length > 0;
      }
      return true;
    }
    
    // 检查用户是否有相应权限
    const hasPermission = item.permissions.some(permission => 
      userPermissions.includes(permission)
    );
    
    if (hasPermission && item.children) {
      const filteredChildren = filterMenuByPermissions(item.children, userPermissions);
      return filteredChildren.length > 0;
    }
    
    return hasPermission;
  }).map(item => ({
    ...item,
    children: item.children ? filterMenuByPermissions(item.children, userPermissions) : undefined
  }));
};

// 图标映射
import * as Icons from '@ant-design/icons';

// 转换为 Antd MenuProps 格式
export const convertToAntdMenu = (items: MenuItem[]): MenuProps['items'] => {
  return items.map(item => {
    // 动态创建图标
    const IconComponent = item.iconType ? (Icons as any)[item.iconType] : undefined;
    
    return {
      key: item.key,
      icon: IconComponent ? React.createElement(IconComponent) : undefined,
      label: item.label,
      children: item.children ? convertToAntdMenu(item.children) : undefined,
    };
  });
};