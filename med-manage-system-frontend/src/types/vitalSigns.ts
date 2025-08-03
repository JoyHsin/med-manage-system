// 生命体征相关类型定义

export interface VitalSigns {
  id: number;
  patientId: number;
  recordedBy: number;
  systolicBp?: number;
  diastolicBp?: number;
  temperature?: number;
  heartRate?: number;
  respiratoryRate?: number;
  oxygenSaturation?: number;
  weight?: number;
  height?: number;
  bmi?: number;
  painScore?: number;
  consciousnessLevel?: string;
  remarks?: string;
  isAbnormal: boolean;
  abnormalIndicators?: string;
  recordedAt: string;
  createdAt: string;
  updatedAt: string;
}

export interface VitalSignsRequest {
  patientId: number;
  systolicBp?: number;
  diastolicBp?: number;
  temperature?: number;
  heartRate?: number;
  respiratoryRate?: number;
  oxygenSaturation?: number;
  weight?: number;
  height?: number;
  painScore?: number;
  consciousnessLevel?: string;
  remarks?: string;
}

export interface VitalSignsValidationResult {
  valid: boolean;
  hasWarnings: boolean;
  warnings?: string[];
  errors?: string[];
}

export interface VitalSignsResponse {
  success: boolean;
  message?: string;
  data: VitalSigns;
  warnings?: string[];
}

// 生命体征正常范围配置
export const VITAL_SIGNS_RANGES = {
  systolicBp: { min: 90, max: 140, unit: 'mmHg', name: '收缩压' },
  diastolicBp: { min: 60, max: 90, unit: 'mmHg', name: '舒张压' },
  temperature: { min: 36.0, max: 37.5, unit: '°C', name: '体温' },
  heartRate: { min: 60, max: 100, unit: '次/分', name: '心率' },
  respiratoryRate: { min: 12, max: 20, unit: '次/分', name: '呼吸频率' },
  oxygenSaturation: { min: 95, max: 100, unit: '%', name: '血氧饱和度' },
  weight: { min: 0.5, max: 500, unit: 'kg', name: '体重' },
  height: { min: 30, max: 250, unit: 'cm', name: '身高' },
  bmi: { min: 18.5, max: 24.0, unit: '', name: 'BMI' },
  painScore: { min: 0, max: 10, unit: '分', name: '疼痛评分' }
} as const;

// 意识状态选项
export const CONSCIOUSNESS_LEVELS = [
  { value: '清醒', label: '清醒' },
  { value: '嗜睡', label: '嗜睡' },
  { value: '昏睡', label: '昏睡' },
  { value: '昏迷', label: '昏迷' },
  { value: '其他', label: '其他' }
] as const;

// BMI分类
export const BMI_CATEGORIES = {
  underweight: { min: 0, max: 18.5, label: '偏瘦', color: '#1890ff' },
  normal: { min: 18.5, max: 24.0, label: '正常', color: '#52c41a' },
  overweight: { min: 24.0, max: 28.0, label: '超重', color: '#faad14' },
  obese: { min: 28.0, max: 100, label: '肥胖', color: '#ff4d4f' }
} as const;

// 疼痛评分描述
export const PAIN_SCORE_DESCRIPTIONS = {
  0: '无痛',
  1: '轻微疼痛',
  2: '轻微疼痛',
  3: '轻度疼痛',
  4: '轻度疼痛',
  5: '中度疼痛',
  6: '中度疼痛',
  7: '重度疼痛',
  8: '重度疼痛',
  9: '剧烈疼痛',
  10: '剧烈疼痛'
} as const;