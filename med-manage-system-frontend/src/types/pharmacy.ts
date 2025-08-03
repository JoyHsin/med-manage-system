// 处方调剂相关类型定义

export interface DispenseRecord {
  id: number;
  prescriptionId: number;
  pharmacistId: number;
  pharmacistName: string;
  status: DispenseStatus;
  startTime: string;
  completedTime?: string;
  deliveredTime?: string;
  reviewedTime?: string;
  reviewPharmacistId?: number;
  reviewPharmacistName?: string;
  dispensingPharmacistId?: number;
  dispensingPharmacistName?: string;
  comments?: string;
  deliveryNotes?: string;
  returnReason?: string;
  cancelReason?: string;
  // 关联数据
  prescription?: any; // 处方信息
  dispenseItems?: DispenseItem[];
  patientName?: string;
  doctorName?: string;
}

export type DispenseStatus = 
  | 'PENDING'     // 待调剂
  | 'IN_PROGRESS' // 调剂中
  | 'COMPLETED'   // 已完成
  | 'DELIVERED'   // 已发药
  | 'REVIEWED'    // 已复核
  | 'RETURNED'    // 已退回
  | 'CANCELLED';  // 已取消

export interface DispenseItem {
  id: number;
  dispenseRecordId: number;
  prescriptionItemId: number;
  medicineId: number;
  medicineName: string;
  prescribedQuantity: number;
  dispensedQuantity: number;
  batchNumber?: string;
  expiryDate?: string;
  substituteMedicineId?: number;
  substituteReason?: string;
  notes?: string;
  // 关联数据
  medicine?: any;
  substituteMedicine?: any;
}

export interface DispenseEligibilityResult {
  eligible: boolean;
  reason?: string;
  warnings: string[];
}

export interface StartDispensingRequest {
  prescriptionId: number;
  pharmacistId: number;
  pharmacistName: string;
}

export interface DispenseMedicineRequest {
  dispenseRecordId: number;
  prescriptionItemId: number;
  medicineId: number;
  dispensedQuantity: number;
  batchNumber?: string;
  expiryDate?: string;
  notes?: string;
}

export interface DeliverMedicineRequest {
  dispensingPharmacistId: number;
  dispensingPharmacistName: string;
  deliveryNotes?: string;
}

export interface SubstituteMedicineRequest {
  newMedicineId: number;
  reason: string;
}

export interface ReturnPrescriptionRequest {
  reason: string;
}

export interface CancelDispensingRequest {
  reason: string;
}

export interface ReviewDispensingRequest {
  reviewPharmacistId: number;
  reviewPharmacistName: string;
  comments?: string;
}

export interface PharmacyStats {
  totalPrescriptions: number;
  pendingPrescriptions: number;
  inProgressPrescriptions: number;
  completedPrescriptions: number;
  deliveredPrescriptions: number;
  returnedPrescriptions: number;
  averageDispensingTime: number;
  todayDispensed: number;
  weekDispensed: number;
  monthDispensed: number;
}

// 调剂状态配置
export const DISPENSE_STATUS_CONFIG = {
  PENDING: {
    text: '待调剂',
    color: 'warning',
    badge: 'warning'
  },
  IN_PROGRESS: {
    text: '调剂中',
    color: 'processing',
    badge: 'processing'
  },
  COMPLETED: {
    text: '已完成',
    color: 'success',
    badge: 'success'
  },
  DELIVERED: {
    text: '已发药',
    color: 'success',
    badge: 'success'
  },
  REVIEWED: {
    text: '已复核',
    color: 'success',
    badge: 'success'
  },
  RETURNED: {
    text: '已退回',
    color: 'error',
    badge: 'error'
  },
  CANCELLED: {
    text: '已取消',
    color: 'default',
    badge: 'default'
  }
} as const;

// 常见退回原因
export const RETURN_REASONS = [
  '药品库存不足',
  '处方信息不完整',
  '患者信息有误',
  '药品相互作用',
  '用药禁忌',
  '剂量异常',
  '其他原因'
] as const;

// 常见替代原因
export const SUBSTITUTE_REASONS = [
  '原药品缺货',
  '原药品过期',
  '患者过敏',
  '医生建议替代',
  '价格考虑',
  '其他原因'
] as const;