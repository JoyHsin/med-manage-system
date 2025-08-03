// 医嘱开具相关类型定义

export interface MedicalOrder {
  id: number;
  patientId: number;
  prescribedBy: number;
  executedBy?: number;
  orderType: OrderType;
  content: string;
  dosage?: string;
  frequency?: string;
  status: OrderStatus;
  priority: OrderPriority;
  prescribedAt: string;
  executedAt?: string;
  route?: string;
  notes?: string;
  price?: number;
  quantity?: number;
  unit?: string;
  executionNotes?: string;
  postponeReason?: string;
  postponedBy?: number;
  postponedAt?: string;
  cancelReason?: string;
  cancelledBy?: number;
  cancelledAt?: string;
  // 关联数据
  patientName?: string;
  prescribedByName?: string;
  executedByName?: string;
}

export type OrderType = 
  | 'INJECTION' 
  | 'ORAL_MEDICATION' 
  | 'EXAMINATION' 
  | 'NURSING_CARE' 
  | 'TREATMENT' 
  | 'OBSERVATION';

export type OrderStatus = 
  | 'PENDING' 
  | 'EXECUTED' 
  | 'POSTPONED' 
  | 'CANCELLED';

export type OrderPriority = 
  | 'URGENT' 
  | 'NORMAL' 
  | 'LOW';

export interface CreateMedicalOrderRequest {
  patientId: number;
  orderType: OrderType;
  content: string;
  dosage?: string;
  frequency?: string;
  priority: OrderPriority;
  route?: string;
  notes?: string;
  price?: number;
  quantity?: number;
  unit?: string;
}

export interface ExecuteMedicalOrderRequest {
  executionNotes?: string;
}

export interface MedicalOrderTemplate {
  id: number;
  name: string;
  orderType: OrderType;
  content: string;
  dosage?: string;
  frequency?: string;
  route?: string;
  notes?: string;
  price?: number;
  quantity?: number;
  unit?: string;
  doctorId: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrderTemplateRequest {
  name: string;
  orderType: OrderType;
  content: string;
  dosage?: string;
  frequency?: string;
  route?: string;
  notes?: string;
  price?: number;
  quantity?: number;
  unit?: string;
}

// 医嘱状态配置
export const ORDER_STATUS_CONFIG = {
  PENDING: {
    text: '待执行',
    color: 'processing',
    badge: 'processing'
  },
  EXECUTED: {
    text: '已执行',
    color: 'success',
    badge: 'success'
  },
  POSTPONED: {
    text: '暂缓执行',
    color: 'warning',
    badge: 'warning'
  },
  CANCELLED: {
    text: '已取消',
    color: 'error',
    badge: 'error'
  }
} as const;

// 医嘱优先级配置
export const ORDER_PRIORITY_CONFIG = {
  URGENT: {
    text: '紧急',
    color: 'red'
  },
  NORMAL: {
    text: '普通',
    color: 'blue'
  },
  LOW: {
    text: '低',
    color: 'gray'
  }
} as const;

// 医嘱类型配置
export const ORDER_TYPE_CONFIG = {
  INJECTION: {
    text: '注射',
    color: 'red',
    icon: '💉'
  },
  ORAL_MEDICATION: {
    text: '口服药物',
    color: 'blue',
    icon: '💊'
  },
  EXAMINATION: {
    text: '检查',
    color: 'green',
    icon: '🔍'
  },
  NURSING_CARE: {
    text: '护理',
    color: 'purple',
    icon: '🩺'
  },
  TREATMENT: {
    text: '治疗',
    color: 'orange',
    icon: '⚕️'
  },
  OBSERVATION: {
    text: '观察',
    color: 'cyan',
    icon: '👁️'
  }
} as const;

// 给药途径选项
export const ROUTE_OPTIONS = [
  { value: '静脉注射', label: '静脉注射' },
  { value: '肌肉注射', label: '肌肉注射' },
  { value: '皮下注射', label: '皮下注射' },
  { value: '口服', label: '口服' },
  { value: '外用', label: '外用' },
  { value: '滴眼', label: '滴眼' },
  { value: '滴鼻', label: '滴鼻' },
  { value: '雾化吸入', label: '雾化吸入' },
  { value: '舌下含服', label: '舌下含服' },
  { value: '直肠给药', label: '直肠给药' },
  { value: '其他', label: '其他' }
] as const;

// 频次选项
export const FREQUENCY_OPTIONS = [
  { value: 'qd', label: '每日一次 (qd)' },
  { value: 'bid', label: '每日两次 (bid)' },
  { value: 'tid', label: '每日三次 (tid)' },
  { value: 'qid', label: '每日四次 (qid)' },
  { value: 'q4h', label: '每4小时一次 (q4h)' },
  { value: 'q6h', label: '每6小时一次 (q6h)' },
  { value: 'q8h', label: '每8小时一次 (q8h)' },
  { value: 'q12h', label: '每12小时一次 (q12h)' },
  { value: 'qn', label: '每晚一次 (qn)' },
  { value: 'prn', label: '必要时 (prn)' },
  { value: 'stat', label: '立即 (stat)' },
  { value: 'sos', label: '需要时 (sos)' }
] as const;

// 单位选项
export const UNIT_OPTIONS = [
  { value: '片', label: '片' },
  { value: '粒', label: '粒' },
  { value: '袋', label: '袋' },
  { value: '瓶', label: '瓶' },
  { value: '支', label: '支' },
  { value: '盒', label: '盒' },
  { value: 'ml', label: 'ml' },
  { value: 'g', label: 'g' },
  { value: 'mg', label: 'mg' },
  { value: '次', label: '次' },
  { value: '项', label: '项' }
] as const;

// 注射部位选项
export const INJECTION_SITE_OPTIONS = [
  { value: '左上臂', label: '左上臂' },
  { value: '右上臂', label: '右上臂' },
  { value: '左臀部', label: '左臀部' },
  { value: '右臀部', label: '右臀部' },
  { value: '左大腿', label: '左大腿' },
  { value: '右大腿', label: '右大腿' },
  { value: '腹部', label: '腹部' },
  { value: '背部', label: '背部' },
  { value: '其他', label: '其他' }
] as const;

// 常用医嘱内容模板
export const COMMON_ORDER_TEMPLATES = {
  INJECTION: [
    '青霉素G钠 240万单位 静脉滴注',
    '头孢曲松钠 2.0g 静脉滴注',
    '左氧氟沙星 0.4g 静脉滴注',
    '甲硝唑 0.5g 静脉滴注',
    '地塞米松 5mg 静脉注射'
  ],
  ORAL_MEDICATION: [
    '阿莫西林胶囊 0.5g 口服',
    '布洛芬缓释胶囊 0.3g 口服',
    '奥美拉唑肠溶胶囊 20mg 口服',
    '复方甘草片 3片 口服',
    '维生素C片 0.1g 口服'
  ],
  EXAMINATION: [
    '血常规检查',
    '尿常规检查',
    '胸部X线检查',
    '心电图检查',
    'B超检查',
    'CT检查',
    'MRI检查'
  ],
  NURSING_CARE: [
    '测量体温、脉搏、呼吸、血压',
    '观察病情变化',
    '协助患者翻身',
    '口腔护理',
    '皮肤护理',
    '导尿管护理',
    '伤口换药'
  ],
  TREATMENT: [
    '物理治疗',
    '康复训练',
    '雾化治疗',
    '氧气吸入',
    '胃肠减压',
    '导尿',
    '灌肠'
  ],
  OBSERVATION: [
    '观察生命体征',
    '观察意识状态',
    '观察伤口愈合情况',
    '观察用药反应',
    '观察排便情况',
    '观察睡眠情况',
    '观察食欲情况'
  ]
} as const;