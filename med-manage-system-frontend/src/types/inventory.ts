// 药品库存管理相关类型定义

export interface Medicine {
  id: number;
  medicineCode: string;
  name: string;
  genericName?: string;
  brandName?: string;
  category: MedicineCategory;
  type?: string;
  dosageForm?: string;
  specification?: string;
  unit: string;
  manufacturer?: string;
  approvalNumber?: string;
  drugLicenseNumber?: string;
  purchasePrice?: number;
  sellingPrice?: number;
  storageConditions?: string;
  indications?: string;
  contraindications?: string;
  adverseReactions?: string;
  dosageAndUsage?: string;
  precautions?: string;
  drugInteractions?: string;
  shelfLifeMonths?: number;
  minStockLevel?: number;
  maxStockLevel?: number;
  safetyStockLevel?: number;
  requiresPrescription: boolean;
  isControlledSubstance: boolean;
  enabled: boolean;
  remarks?: string;
  supplierId?: number;
  createdBy?: number;
  lastUpdatedBy?: number;
  createdAt?: string;
  updatedAt?: string;
  // 关联数据
  currentStock?: number;
  availableStock?: number;
  reservedStock?: number;
  inventoryLevels?: InventoryLevel[];
  stockTransactions?: StockTransaction[];
  profitMargin?: number;
}

export type MedicineCategory = 
  | '处方药' 
  | '非处方药' 
  | '中药' 
  | '西药' 
  | '生物制品' 
  | '疫苗' 
  | '其他';

export interface InventoryLevel {
  id: number;
  medicineId: number;
  batchNumber: string;
  currentStock: number;
  availableStock: number;
  reservedStock: number;
  lockedStock: number;
  purchasePrice?: number;
  productionDate?: string;
  expiryDate?: string;
  supplierId?: number;
  supplierName?: string;
  storageLocation?: string;
  status: InventoryStatus;
  lastInventoryDate?: string;
  lastOutboundDate?: string;
  lastInboundDate?: string;
  inventoryCost?: number;
  remarks?: string;
  createdBy?: number;
  lastUpdatedBy?: number;
  // 计算属性
  remainingShelfLifeDays?: number;
  isExpiringSoon?: boolean;
  isExpired?: boolean;
}

export type InventoryStatus = 
  | '正常' 
  | '预警' 
  | '过期' 
  | '损坏' 
  | '冻结';

export interface StockTransaction {
  id: number;
  transactionNumber: string;
  medicineId: number;
  transactionType: TransactionType;
  quantity: number;
  unitPrice?: number;
  totalAmount?: number;
  batchNumber?: string;
  productionDate?: string;
  expiryDate?: string;
  supplierId?: number;
  supplierName?: string;
  stockBefore?: number;
  stockAfter?: number;
  reason?: string;
  relatedDocumentNumber?: string;
  status: TransactionStatus;
  transactionDate: string;
  operatorId?: number;
  operatorName?: string;
  reviewerId?: number;
  reviewerName?: string;
  reviewTime?: string;
  remarks?: string;
  // 关联数据
  medicineName?: string;
}

export type TransactionType = 
  | '入库' 
  | '出库' 
  | '调拨' 
  | '盘点' 
  | '报损' 
  | '退货' 
  | '过期处理';

export type TransactionStatus = 
  | '待审核' 
  | '已确认' 
  | '已取消';

export interface CreateMedicineRequest {
  medicineCode: string;
  name: string;
  genericName?: string;
  brandName?: string;
  category: MedicineCategory;
  type?: string;
  dosageForm?: string;
  specification?: string;
  unit: string;
  manufacturer?: string;
  approvalNumber?: string;
  drugLicenseNumber?: string;
  purchasePrice?: number;
  sellingPrice?: number;
  storageConditions?: string;
  indications?: string;
  contraindications?: string;
  adverseReactions?: string;
  dosageAndUsage?: string;
  precautions?: string;
  drugInteractions?: string;
  shelfLifeMonths?: number;
  minStockLevel?: number;
  maxStockLevel?: number;
  safetyStockLevel?: number;
  requiresPrescription?: boolean;
  isControlledSubstance?: boolean;
  remarks?: string;
  supplierId?: number;
}

export interface UpdateMedicineRequest extends CreateMedicineRequest {}

export interface StockUpdateRequest {
  transactionType: TransactionType;
  quantity: number;
  unitPrice?: number;
  batchNumber?: string;
  productionDate?: string;
  expiryDate?: string;
  supplierId?: number;
  supplierName?: string;
  reason?: string;
  relatedDocumentNumber?: string;
  remarks?: string;
}

export interface MedicineSearchCriteria {
  keyword?: string;
  category?: MedicineCategory;
  requiresPrescription?: boolean;
  isControlledSubstance?: boolean;
  enabled?: boolean;
  minStockLevel?: number;
  maxStockLevel?: number;
  supplierId?: number;
}

export interface InventoryStats {
  totalMedicines: number;
  activeMedicines: number;
  inactiveMedicines: number;
  prescriptionMedicines: number;
  otcMedicines: number;
  controlledSubstances: number;
  lowStockCount: number;
  overstockedCount: number;
  expiringCount: number;
  expiredCount: number;
  totalInventoryValue: number;
  medicinesByCategory: Record<string, number>;
  stockLevelDistribution: {
    normal: number;
    warning: number;
    critical: number;
  };
}

export interface Supplier {
  id: number;
  name: string;
  code: string;
  contactPerson?: string;
  contactPhone?: string;
  contactEmail?: string;
  address?: string;
  isActive: boolean;
  remarks?: string;
}

// 药品分类配置
export const MEDICINE_CATEGORY_CONFIG = {
  '处方药': {
    text: '处方药',
    color: 'red',
    icon: '🔴'
  },
  '非处方药': {
    text: '非处方药',
    color: 'green',
    icon: '🟢'
  },
  '中药': {
    text: '中药',
    color: 'orange',
    icon: '🟠'
  },
  '西药': {
    text: '西药',
    color: 'blue',
    icon: '🔵'
  },
  '生物制品': {
    text: '生物制品',
    color: 'purple',
    icon: '🟣'
  },
  '疫苗': {
    text: '疫苗',
    color: 'cyan',
    icon: '🔷'
  },
  '其他': {
    text: '其他',
    color: 'gray',
    icon: '⚪'
  }
} as const;

// 库存状态配置
export const INVENTORY_STATUS_CONFIG = {
  '正常': {
    text: '正常',
    color: 'success',
    badge: 'success'
  },
  '预警': {
    text: '预警',
    color: 'warning',
    badge: 'warning'
  },
  '过期': {
    text: '过期',
    color: 'error',
    badge: 'error'
  },
  '损坏': {
    text: '损坏',
    color: 'error',
    badge: 'error'
  },
  '冻结': {
    text: '冻结',
    color: 'default',
    badge: 'default'
  }
} as const;

// 交易类型配置
export const TRANSACTION_TYPE_CONFIG = {
  '入库': {
    text: '入库',
    color: 'green',
    icon: '📥'
  },
  '出库': {
    text: '出库',
    color: 'red',
    icon: '📤'
  },
  '调拨': {
    text: '调拨',
    color: 'blue',
    icon: '🔄'
  },
  '盘点': {
    text: '盘点',
    color: 'orange',
    icon: '📊'
  },
  '报损': {
    text: '报损',
    color: 'red',
    icon: '❌'
  },
  '退货': {
    text: '退货',
    color: 'purple',
    icon: '↩️'
  },
  '过期处理': {
    text: '过期处理',
    color: 'gray',
    icon: '⏰'
  }
} as const;

// 交易状态配置
export const TRANSACTION_STATUS_CONFIG = {
  '待审核': {
    text: '待审核',
    color: 'warning',
    badge: 'warning'
  },
  '已确认': {
    text: '已确认',
    color: 'success',
    badge: 'success'
  },
  '已取消': {
    text: '已取消',
    color: 'error',
    badge: 'error'
  }
} as const;

// 剂型选项
export const DOSAGE_FORM_OPTIONS = [
  { value: '片剂', label: '片剂' },
  { value: '胶囊', label: '胶囊' },
  { value: '注射液', label: '注射液' },
  { value: '口服液', label: '口服液' },
  { value: '颗粒剂', label: '颗粒剂' },
  { value: '软膏', label: '软膏' },
  { value: '滴眼液', label: '滴眼液' },
  { value: '喷雾剂', label: '喷雾剂' },
  { value: '贴剂', label: '贴剂' },
  { value: '栓剂', label: '栓剂' },
  { value: '其他', label: '其他' }
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
  { value: '贴', label: '贴' },
  { value: '枚', label: '枚' }
] as const;

// 存储条件选项
export const STORAGE_CONDITIONS = [
  { value: '常温保存', label: '常温保存' },
  { value: '阴凉干燥处保存', label: '阴凉干燥处保存' },
  { value: '冷藏保存(2-8℃)', label: '冷藏保存(2-8℃)' },
  { value: '冷冻保存(-18℃以下)', label: '冷冻保存(-18℃以下)' },
  { value: '避光保存', label: '避光保存' },
  { value: '密封保存', label: '密封保存' },
  { value: '防潮保存', label: '防潮保存' }
] as const;