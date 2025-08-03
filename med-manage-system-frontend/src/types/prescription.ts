// 处方管理相关类型定义

export interface Prescription {
  id: number;
  medicalRecordId: number;
  doctorId: number;
  prescriptionNumber: string;
  prescriptionType: PrescriptionType;
  status: PrescriptionStatus;
  prescribedAt: string;
  reviewDoctorId?: number;
  reviewedAt?: string;
  pharmacistId?: number;
  dispensedAt?: string;
  deliveredAt?: string;
  totalAmount: number;
  dosageInstructions?: string;
  precautions?: string;
  clinicalDiagnosis?: string;
  validityDays: number;
  isEmergency: boolean;
  isChildPrescription: boolean;
  isChronicDiseasePrescription: boolean;
  repeatTimes: number;
  reviewComments?: string;
  cancelReason?: string;
  cancelledAt?: string;
  remarks?: string;
  // 关联数据
  prescriptionItems?: PrescriptionItem[];
  patientName?: string;
  doctorName?: string;
  reviewDoctorName?: string;
  pharmacistName?: string;
}

export type PrescriptionType = 
  | '普通处方' 
  | '急诊处方' 
  | '儿科处方' 
  | '麻醉处方' 
  | '精神药品处方' 
  | '毒性药品处方';

export type PrescriptionStatus = 
  | '草稿' 
  | '已开具' 
  | '已审核' 
  | '已调配' 
  | '已发药' 
  | '已取消';

export interface PrescriptionItem {
  id: number;
  prescriptionId: number;
  medicineId: number;
  medicineName: string;
  specification?: string;
  quantity: number;
  unit: string;
  unitPrice: number;
  subtotal: number;
  usage?: string;
  dosage?: string;
  frequency?: string;
  duration?: number;
  specialInstructions?: string;
  isSubstitute: boolean;
  originalMedicineId?: number;
  substituteReason?: string;
  sortOrder: number;
  remarks?: string;
}

export interface CreatePrescriptionRequest {
  medicalRecordId: number;
  prescriptionType: PrescriptionType;
  dosageInstructions?: string;
  precautions?: string;
  clinicalDiagnosis?: string;
  validityDays: number;
  isEmergency: boolean;
  isChildPrescription: boolean;
  isChronicDiseasePrescription: boolean;
  repeatTimes: number;
  remarks?: string;
  prescriptionItems: CreatePrescriptionItemRequest[];
}

export interface CreatePrescriptionItemRequest {
  medicineId: number;
  quantity: number;
  unit: string;
  usage?: string;
  dosage?: string;
  frequency?: string;
  duration?: number;
  specialInstructions?: string;
  sortOrder: number;
  remarks?: string;
}

export interface Medicine {
  id: number;
  name: string;
  specification: string;
  manufacturer: string;
  category: string;
  unit: string;
  unitPrice: number;
  stockQuantity: number;
  minStockLevel: number;
  maxStockLevel: number;
  isActive: boolean;
  isPrescriptionDrug: boolean;
  isControlledDrug: boolean;
  description?: string;
}

// 处方状态配置
export const PRESCRIPTION_STATUS_CONFIG = {
  '草稿': {
    text: '草稿',
    color: 'default',
    badge: 'default'
  },
  '已开具': {
    text: '已开具',
    color: 'processing',
    badge: 'processing'
  },
  '已审核': {
    text: '已审核',
    color: 'success',
    badge: 'success'
  },
  '已调配': {
    text: '已调配',
    color: 'warning',
    badge: 'warning'
  },
  '已发药': {
    text: '已发药',
    color: 'success',
    badge: 'success'
  },
  '已取消': {
    text: '已取消',
    color: 'error',
    badge: 'error'
  }
} as const;

// 处方类型配置
export const PRESCRIPTION_TYPE_CONFIG = {
  '普通处方': {
    text: '普通处方',
    color: 'blue'
  },
  '急诊处方': {
    text: '急诊处方',
    color: 'red'
  },
  '儿科处方': {
    text: '儿科处方',
    color: 'green'
  },
  '麻醉处方': {
    text: '麻醉处方',
    color: 'orange'
  },
  '精神药品处方': {
    text: '精神药品处方',
    color: 'purple'
  },
  '毒性药品处方': {
    text: '毒性药品处方',
    color: 'magenta'
  }
} as const;

// 用法选项
export const USAGE_OPTIONS = [
  { value: '口服', label: '口服' },
  { value: '静脉注射', label: '静脉注射' },
  { value: '肌肉注射', label: '肌肉注射' },
  { value: '皮下注射', label: '皮下注射' },
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

// 疗程选项
export const DURATION_OPTIONS = [
  { value: 1, label: '1天' },
  { value: 3, label: '3天' },
  { value: 5, label: '5天' },
  { value: 7, label: '7天' },
  { value: 10, label: '10天' },
  { value: 14, label: '14天' },
  { value: 21, label: '21天' },
  { value: 30, label: '30天' }
] as const;

// 处方有效期选项
export const VALIDITY_DAYS_OPTIONS = [
  { value: 1, label: '1天' },
  { value: 3, label: '3天' },
  { value: 7, label: '7天' },
  { value: 15, label: '15天' },
  { value: 30, label: '30天' }
] as const;

// 药品单位选项
export const MEDICINE_UNITS = [
  { value: '片', label: '片' },
  { value: '粒', label: '粒' },
  { value: '袋', label: '袋' },
  { value: '瓶', label: '瓶' },
  { value: '支', label: '支' },
  { value: '盒', label: '盒' },
  { value: 'ml', label: 'ml' },
  { value: 'g', label: 'g' },
  { value: 'mg', label: 'mg' },
  { value: '贴', label: '贴' }
] as const;

// 处方打印模板
export interface PrescriptionPrintData {
  prescription: Prescription;
  patient: {
    name: string;
    gender: string;
    age: number;
    phone: string;
  };
  doctor: {
    name: string;
    title: string;
    department: string;
  };
  clinic: {
    name: string;
    address: string;
    phone: string;
    license: string;
  };
}