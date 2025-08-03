// 收费管理相关类型定义

export interface Bill {
  id: number;
  patientId: number;
  registrationId?: number;
  billNumber: string;
  totalAmount: number;
  paidAmount: number;
  status: BillStatus;
  createdAt: string;
  updatedAt: string;
  notes?: string;
  createdBy?: number;
  updatedBy?: number;
  // 关联数据
  patientName?: string;
  patientNumber?: string;
  billItems?: BillItem[];
  remainingAmount?: number;
}

export type BillStatus = 
  | 'PENDING'        // 待付款
  | 'PAID'           // 已付款
  | 'PARTIALLY_PAID' // 部分付款
  | 'CANCELLED';     // 已取消

export interface BillItem {
  id: number;
  billId: number;
  itemType: BillItemType;
  itemName: string;
  itemCode?: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
  discount: number;
  actualAmount: number;
  specification?: string;
  notes?: string;
  prescriptionItemId?: number;
  medicalRecordId?: number;
}

export type BillItemType = 
  | 'REGISTRATION'  // 挂号费
  | 'CONSULTATION'  // 诊疗费
  | 'MEDICINE'      // 药品费
  | 'EXAMINATION'   // 检查费
  | 'TREATMENT'     // 治疗费
  | 'OTHER';        // 其他费用

export interface CreateBillRequest {
  patientId: number;
  registrationId?: number;
  notes?: string;
  billItems: CreateBillItemRequest[];
}

export interface CreateBillItemRequest {
  itemType: BillItemType;
  itemName: string;
  itemCode?: string;
  unitPrice: number;
  quantity: number;
  discount?: number;
  specification?: string;
  notes?: string;
  prescriptionItemId?: number;
  medicalRecordId?: number;
}

export interface AddBillItemRequest {
  itemType: BillItemType;
  itemName: string;
  itemCode?: string;
  unitPrice: number;
  quantity: number;
  discount?: number;
  specification?: string;
  notes?: string;
  prescriptionItemId?: number;
  medicalRecordId?: number;
}

export interface PaymentRequest {
  billId: number;
  paymentAmount: number;
  paymentMethod: PaymentMethod;
  paymentReference?: string;
  notes?: string;
}

export type PaymentMethod = 
  | 'CASH'          // 现金
  | 'CARD'          // 银行卡
  | 'ALIPAY'        // 支付宝
  | 'WECHAT'        // 微信支付
  | 'INSURANCE'     // 医保
  | 'OTHER';        // 其他

export interface PaymentRecord {
  id: number;
  billId: number;
  paymentAmount: number;
  paymentMethod: PaymentMethod;
  paymentReference?: string;
  paymentTime: string;
  operatorId: number;
  operatorName?: string;
  notes?: string;
}

export interface RefundRequest {
  billId: number;
  refundAmount: number;
  refundReason: string;
  refundMethod: PaymentMethod;
  notes?: string;
}

export interface RefundRecord {
  id: number;
  billId: number;
  refundAmount: number;
  refundReason: string;
  refundMethod: PaymentMethod;
  refundTime: string;
  operatorId: number;
  operatorName?: string;
  notes?: string;
  status: RefundStatus;
}

export type RefundStatus = 
  | 'PENDING'   // 待处理
  | 'APPROVED'  // 已批准
  | 'REJECTED'  // 已拒绝
  | 'COMPLETED' // 已完成
  | 'CANCELLED'; // 已取消

export interface BillingStats {
  totalBills: number;
  pendingBills: number;
  paidBills: number;
  partiallyPaidBills: number;
  cancelledBills: number;
  totalRevenue: number;
  todayRevenue: number;
  monthRevenue: number;
  averageBillAmount: number;
  revenueByItemType: Record<string, number>;
  revenueByPaymentMethod: Record<string, number>;
}

export interface InsuranceClaim {
  id: number;
  billId: number;
  patientId: number;
  insuranceProviderId: number;
  claimNumber: string;
  claimAmount: number;
  approvedAmount?: number;
  status: InsuranceClaimStatus;
  submittedAt: string;
  processedAt?: string;
  notes?: string;
  // 关联数据
  insuranceProviderName?: string;
  patientName?: string;
}

export type InsuranceClaimStatus = 
  | 'DRAFT'      // 草稿
  | 'SUBMITTED'  // 已提交
  | 'PROCESSING' // 处理中
  | 'APPROVED'   // 已批准
  | 'REJECTED'   // 已拒绝
  | 'PAID';      // 已支付

export interface InsuranceProvider {
  id: number;
  name: string;
  code: string;
  contactPerson?: string;
  contactPhone?: string;
  contactEmail?: string;
  address?: string;
  isActive: boolean;
  notes?: string;
}

// 账单状态配置
export const BILL_STATUS_CONFIG = {
  PENDING: {
    text: '待付款',
    color: 'warning',
    badge: 'warning'
  },
  PAID: {
    text: '已付款',
    color: 'success',
    badge: 'success'
  },
  PARTIALLY_PAID: {
    text: '部分付款',
    color: 'processing',
    badge: 'processing'
  },
  CANCELLED: {
    text: '已取消',
    color: 'error',
    badge: 'error'
  }
} as const;

// 账单项目类型配置
export const BILL_ITEM_TYPE_CONFIG = {
  REGISTRATION: {
    text: '挂号费',
    color: 'blue',
    icon: '📋'
  },
  CONSULTATION: {
    text: '诊疗费',
    color: 'green',
    icon: '👨‍⚕️'
  },
  MEDICINE: {
    text: '药品费',
    color: 'purple',
    icon: '💊'
  },
  EXAMINATION: {
    text: '检查费',
    color: 'orange',
    icon: '🔍'
  },
  TREATMENT: {
    text: '治疗费',
    color: 'red',
    icon: '⚕️'
  },
  OTHER: {
    text: '其他费用',
    color: 'gray',
    icon: '📄'
  }
} as const;

// 支付方式配置
export const PAYMENT_METHOD_CONFIG = {
  CASH: {
    text: '现金',
    color: 'green',
    icon: '💵'
  },
  CARD: {
    text: '银行卡',
    color: 'blue',
    icon: '💳'
  },
  ALIPAY: {
    text: '支付宝',
    color: 'cyan',
    icon: '📱'
  },
  WECHAT: {
    text: '微信支付',
    color: 'green',
    icon: '💬'
  },
  INSURANCE: {
    text: '医保',
    color: 'orange',
    icon: '🏥'
  },
  OTHER: {
    text: '其他',
    color: 'gray',
    icon: '💰'
  }
} as const;

// 退费状态配置
export const REFUND_STATUS_CONFIG = {
  PENDING: {
    text: '待处理',
    color: 'warning',
    badge: 'warning'
  },
  APPROVED: {
    text: '已批准',
    color: 'processing',
    badge: 'processing'
  },
  REJECTED: {
    text: '已拒绝',
    color: 'error',
    badge: 'error'
  },
  COMPLETED: {
    text: '已完成',
    color: 'success',
    badge: 'success'
  },
  CANCELLED: {
    text: '已取消',
    color: 'default',
    badge: 'default'
  }
} as const;

// 医保理赔状态配置
export const INSURANCE_CLAIM_STATUS_CONFIG = {
  DRAFT: {
    text: '草稿',
    color: 'default',
    badge: 'default'
  },
  SUBMITTED: {
    text: '已提交',
    color: 'processing',
    badge: 'processing'
  },
  PROCESSING: {
    text: '处理中',
    color: 'warning',
    badge: 'warning'
  },
  APPROVED: {
    text: '已批准',
    color: 'success',
    badge: 'success'
  },
  REJECTED: {
    text: '已拒绝',
    color: 'error',
    badge: 'error'
  },
  PAID: {
    text: '已支付',
    color: 'success',
    badge: 'success'
  }
} as const;

// 常用收费项目模板
export const COMMON_BILL_ITEMS = {
  REGISTRATION: [
    { name: '普通门诊挂号费', code: 'REG001', unitPrice: 5, specification: '次' },
    { name: '专家门诊挂号费', code: 'REG002', unitPrice: 15, specification: '次' },
    { name: '急诊挂号费', code: 'REG003', unitPrice: 10, specification: '次' }
  ],
  CONSULTATION: [
    { name: '普通门诊诊疗费', code: 'CONS001', unitPrice: 20, specification: '次' },
    { name: '专家门诊诊疗费', code: 'CONS002', unitPrice: 50, specification: '次' },
    { name: '急诊诊疗费', code: 'CONS003', unitPrice: 30, specification: '次' }
  ],
  EXAMINATION: [
    { name: '血常规检查', code: 'EXAM001', unitPrice: 25, specification: '次' },
    { name: '尿常规检查', code: 'EXAM002', unitPrice: 15, specification: '次' },
    { name: '心电图检查', code: 'EXAM003', unitPrice: 30, specification: '次' },
    { name: 'B超检查', code: 'EXAM004', unitPrice: 80, specification: '次' },
    { name: 'X线检查', code: 'EXAM005', unitPrice: 60, specification: '次' }
  ],
  TREATMENT: [
    { name: '静脉输液', code: 'TREAT001', unitPrice: 10, specification: '次' },
    { name: '肌肉注射', code: 'TREAT002', unitPrice: 5, specification: '次' },
    { name: '换药', code: 'TREAT003', unitPrice: 15, specification: '次' },
    { name: '雾化治疗', code: 'TREAT004', unitPrice: 20, specification: '次' }
  ]
} as const;