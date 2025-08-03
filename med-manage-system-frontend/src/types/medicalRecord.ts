// 电子病历相关类型定义

export interface MedicalRecord {
  id: number;
  patientId: number;
  doctorId: number;
  registrationId?: number;
  recordNumber: string;
  chiefComplaint?: string;
  presentIllness?: string;
  pastHistory?: string;
  personalHistory?: string;
  familyHistory?: string;
  physicalExamination?: string;
  auxiliaryExamination?: string;
  preliminaryDiagnosis?: string;
  finalDiagnosis?: string;
  treatmentPlan?: string;
  medicalOrders?: string;
  conditionAssessment?: string;
  prognosis?: string;
  followUpAdvice?: string;
  status: RecordStatus;
  recordDate: string;
  reviewDoctorId?: number;
  reviewTime?: string;
  reviewComments?: string;
  department?: string;
  recordType: RecordType;
  isInfectious: boolean;
  isChronicDisease: boolean;
  summary?: string;
  remarks?: string;
  createdBy?: number;
  lastUpdatedBy?: number;
  diagnoses?: Diagnosis[];
  prescriptions?: Prescription[];
  createdAt: string;
  updatedAt: string;
}

export interface Diagnosis {
  id: number;
  medicalRecordId: number;
  diagnosisCode?: string;
  diagnosisName: string;
  diagnosisType: DiagnosisType;
  description?: string;
  severity?: SeverityLevel;
  status: DiagnosisStatus;
  evidence?: string;
  doctorId: number;
  diagnosisTime: string;
  isPrimary: boolean;
  isChronicDisease: boolean;
  isInfectious: boolean;
  isHereditary: boolean;
  prognosis?: string;
  treatmentAdvice?: string;
  followUpRequirement?: string;
  remarks?: string;
  sortOrder: number;
}

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
  totalAmount?: number;
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
  prescriptionItems?: PrescriptionItem[];
}

export interface PrescriptionItem {
  id: number;
  prescriptionId: number;
  medicineId: number;
  medicineName: string;
  specification?: string;
  quantity: number;
  unit: string;
  unitPrice: number;
  dosage: string;
  frequency: string;
  duration: string;
  instructions?: string;
}

export interface CreateMedicalRecordRequest {
  patientId: number;
  registrationId?: number;
  chiefComplaint?: string;
  presentIllness?: string;
  pastHistory?: string;
  personalHistory?: string;
  familyHistory?: string;
  physicalExamination?: string;
  auxiliaryExamination?: string;
  preliminaryDiagnosis?: string;
  finalDiagnosis?: string;
  treatmentPlan?: string;
  medicalOrders?: string;
  conditionAssessment?: string;
  prognosis?: string;
  followUpAdvice?: string;
  department?: string;
  recordType?: RecordType;
  isInfectious?: boolean;
  isChronicDisease?: boolean;
  remarks?: string;
}

export interface UpdateMedicalRecordRequest extends CreateMedicalRecordRequest {
  id: number;
}

// 病历状态
export type RecordStatus = '草稿' | '待审核' | '已审核' | '已归档';

// 病历类型
export type RecordType = '门诊病历' | '急诊病历' | '住院病历' | '体检报告' | '其他';

// 诊断类型
export type DiagnosisType = '主要诊断' | '次要诊断' | '疑似诊断' | '排除诊断' | '并发症' | '合并症';

// 严重程度
export type SeverityLevel = '轻度' | '中度' | '重度' | '危重';

// 诊断状态
export type DiagnosisStatus = '初步诊断' | '确定诊断' | '修正诊断' | '排除诊断';

// 处方类型
export type PrescriptionType = '普通处方' | '急诊处方' | '儿科处方' | '麻醉处方' | '精神药品处方' | '毒性药品处方';

// 处方状态
export type PrescriptionStatus = '草稿' | '已开具' | '已审核' | '已调配' | '已发药' | '已取消';

// 病历状态配置
export const RECORD_STATUS_CONFIG = {
  '草稿': {
    color: 'default',
    badge: 'default'
  },
  '待审核': {
    color: 'processing',
    badge: 'processing'
  },
  '已审核': {
    color: 'success',
    badge: 'success'
  },
  '已归档': {
    color: 'default',
    badge: 'default'
  }
} as const;

// 病历类型配置
export const RECORD_TYPE_CONFIG = {
  '门诊病历': {
    color: 'blue',
    icon: '📋'
  },
  '急诊病历': {
    color: 'red',
    icon: '🚨'
  },
  '住院病历': {
    color: 'purple',
    icon: '🏥'
  },
  '体检报告': {
    color: 'green',
    icon: '📊'
  },
  '其他': {
    color: 'gray',
    icon: '📄'
  }
} as const;

// 诊断类型配置
export const DIAGNOSIS_TYPE_CONFIG = {
  '主要诊断': {
    color: 'red',
    priority: 1
  },
  '次要诊断': {
    color: 'orange',
    priority: 2
  },
  '疑似诊断': {
    color: 'yellow',
    priority: 3
  },
  '排除诊断': {
    color: 'gray',
    priority: 4
  },
  '并发症': {
    color: 'purple',
    priority: 5
  },
  '合并症': {
    color: 'cyan',
    priority: 6
  }
} as const;

// 严重程度配置
export const SEVERITY_CONFIG = {
  '轻度': {
    color: 'green'
  },
  '中度': {
    color: 'orange'
  },
  '重度': {
    color: 'red'
  },
  '危重': {
    color: 'red'
  }
} as const;

// 诊断状态配置
export const DIAGNOSIS_STATUS_CONFIG = {
  '初步诊断': {
    color: 'processing',
    badge: 'processing'
  },
  '确定诊断': {
    color: 'success',
    badge: 'success'
  },
  '修正诊断': {
    color: 'warning',
    badge: 'warning'
  },
  '排除诊断': {
    color: 'default',
    badge: 'default'
  }
} as const;

// 处方状态配置
export const PRESCRIPTION_STATUS_CONFIG = {
  '草稿': {
    color: 'default',
    badge: 'default'
  },
  '已开具': {
    color: 'processing',
    badge: 'processing'
  },
  '已审核': {
    color: 'success',
    badge: 'success'
  },
  '已调配': {
    color: 'warning',
    badge: 'warning'
  },
  '已发药': {
    color: 'success',
    badge: 'success'
  },
  '已取消': {
    color: 'error',
    badge: 'error'
  }
} as const;

// 常用诊断模板
export const COMMON_DIAGNOSES = [
  { code: 'J00', name: '急性鼻咽炎[感冒]' },
  { code: 'J06.9', name: '急性上呼吸道感染，未特指' },
  { code: 'K59.1', name: '功能性腹泻' },
  { code: 'M79.3', name: '脂膜炎，未特指' },
  { code: 'R50.9', name: '发热，未特指' },
  { code: 'R51', name: '头痛' },
  { code: 'R06.02', name: '气短' },
  { code: 'R05', name: '咳嗽' }
] as const;

// 病历模板字段
export const MEDICAL_RECORD_TEMPLATE_FIELDS = [
  { key: 'chiefComplaint', label: '主诉', required: true },
  { key: 'presentIllness', label: '现病史', required: true },
  { key: 'pastHistory', label: '既往史', required: false },
  { key: 'personalHistory', label: '个人史', required: false },
  { key: 'familyHistory', label: '家族史', required: false },
  { key: 'physicalExamination', label: '体格检查', required: true },
  { key: 'auxiliaryExamination', label: '辅助检查', required: false },
  { key: 'preliminaryDiagnosis', label: '初步诊断', required: true },
  { key: 'finalDiagnosis', label: '最终诊断', required: false },
  { key: 'treatmentPlan', label: '治疗方案', required: true },
  { key: 'medicalOrders', label: '医嘱', required: false },
  { key: 'conditionAssessment', label: '病情评估', required: false },
  { key: 'prognosis', label: '预后', required: false },
  { key: 'followUpAdvice', label: '随访建议', required: false }
] as const;