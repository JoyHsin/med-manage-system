import { apiClient } from './apiClient';
import {
  Medicine,
  InventoryLevel,
  StockTransaction,
  CreateMedicineRequest,
  UpdateMedicineRequest,
  StockUpdateRequest,
  MedicineSearchCriteria,
  InventoryStats,
  Supplier
} from '../types/inventory';

export class InventoryService {
  private baseUrl = '/inventory';

  /**
   * 创建药品
   */
  async createMedicine(request: CreateMedicineRequest): Promise<Medicine> {
    const response = await apiClient.post(`${this.baseUrl}/medicines`, request);
    return response.data;
  }

  /**
   * 更新药品信息
   */
  async updateMedicine(id: number, request: UpdateMedicineRequest): Promise<Medicine> {
    const response = await apiClient.put(`${this.baseUrl}/medicines/${id}`, request);
    return response.data;
  }

  /**
   * 获取药品详情
   */
  async getMedicineById(id: number): Promise<Medicine> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/${id}`);
    return response.data;
  }

  /**
   * 根据药品编码获取药品
   */
  async getMedicineByCode(code: string): Promise<Medicine> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/code/${code}`);
    return response.data;
  }

  /**
   * 搜索药品
   */
  async searchMedicines(criteria: MedicineSearchCriteria): Promise<Medicine[]> {
    const response = await apiClient.post(`${this.baseUrl}/medicines/search`, criteria);
    return response.data;
  }

  /**
   * 获取所有启用的药品
   */
  async getAllActiveMedicines(): Promise<Medicine[]> {
    const response = await apiClient.get(`${this.baseUrl}/medicines`);
    return response.data;
  }

  /**
   * 根据分类获取药品
   */
  async getMedicinesByCategory(category: string): Promise<Medicine[]> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/category/${category}`);
    return response.data;
  }

  /**
   * 获取处方药列表
   */
  async getPrescriptionMedicines(): Promise<Medicine[]> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/prescription`);
    return response.data;
  }

  /**
   * 获取非处方药列表
   */
  async getOverTheCounterMedicines(): Promise<Medicine[]> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/otc`);
    return response.data;
  }

  /**
   * 获取特殊管制药品列表
   */
  async getControlledSubstances(): Promise<Medicine[]> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/controlled`);
    return response.data;
  }

  /**
   * 更新库存
   */
  async updateStock(id: number, request: StockUpdateRequest): Promise<void> {
    await apiClient.post(`${this.baseUrl}/medicines/${id}/stock`, request);
  }

  /**
   * 获取需要补货的药品列表
   */
  async getLowStockMedicines(): Promise<Medicine[]> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/low-stock`);
    return response.data;
  }

  /**
   * 获取库存过多的药品列表
   */
  async getOverstockedMedicines(): Promise<Medicine[]> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/overstocked`);
    return response.data;
  }

  /**
   * 获取即将过期的药品列表
   */
  async getExpiringMedicines(days: number = 30): Promise<Medicine[]> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/expiring`, {
      params: { days }
    });
    return response.data;
  }

  /**
   * 获取已过期的药品列表
   */
  async getExpiredMedicines(): Promise<Medicine[]> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/expired`);
    return response.data;
  }

  /**
   * 获取药品的当前库存总量
   */
  async getCurrentStock(id: number): Promise<number> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/${id}/stock/current`);
    return response.data.currentStock;
  }

  /**
   * 获取药品的可用库存总量
   */
  async getAvailableStock(id: number): Promise<number> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/${id}/stock/available`);
    return response.data.availableStock;
  }

  /**
   * 预留库存
   */
  async reserveStock(id: number, quantity: number): Promise<{ success: boolean; message: string }> {
    const response = await apiClient.post(`${this.baseUrl}/medicines/${id}/stock/reserve`, null, {
      params: { quantity }
    });
    return response.data;
  }

  /**
   * 释放预留库存
   */
  async releaseReservedStock(id: number, quantity: number): Promise<void> {
    await apiClient.post(`${this.baseUrl}/medicines/${id}/stock/release`, null, {
      params: { quantity }
    });
  }

  /**
   * 启用药品
   */
  async enableMedicine(id: number): Promise<void> {
    await apiClient.post(`${this.baseUrl}/medicines/${id}/enable`);
  }

  /**
   * 禁用药品
   */
  async disableMedicine(id: number): Promise<void> {
    await apiClient.post(`${this.baseUrl}/medicines/${id}/disable`);
  }

  /**
   * 删除药品（软删除）
   */
  async deleteMedicine(id: number): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/medicines/${id}`);
  }

  /**
   * 获取库存统计信息
   */
  async getInventoryStatistics(): Promise<InventoryStats> {
    const response = await apiClient.get(`${this.baseUrl}/statistics`);
    return response.data;
  }

  /**
   * 获取指定药品的库存价值
   */
  async getInventoryValueByMedicine(id: number): Promise<number> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/${id}/value`);
    return response.data.inventoryValue;
  }

  /**
   * 获取药品的库存水平列表
   */
  async getInventoryLevels(medicineId: number): Promise<InventoryLevel[]> {
    const response = await apiClient.get(`${this.baseUrl}/medicines/${medicineId}/inventory-levels`);
    return response.data;
  }

  /**
   * 获取药品的库存交易记录
   */
  async getStockTransactions(
    medicineId?: number,
    transactionType?: string,
    startDate?: string,
    endDate?: string
  ): Promise<StockTransaction[]> {
    const params: any = {};
    if (medicineId) params.medicineId = medicineId;
    if (transactionType) params.transactionType = transactionType;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/stock-transactions`, { params });
    return response.data;
  }

  /**
   * 获取供应商列表
   */
  async getSuppliers(): Promise<Supplier[]> {
    const response = await apiClient.get(`${this.baseUrl}/suppliers`);
    return response.data;
  }

  /**
   * 导出药品库存
   */
  async exportInventory(
    category?: string,
    status?: string,
    format: 'excel' | 'pdf' = 'excel'
  ): Promise<Blob> {
    const params: any = { format };
    if (category) params.category = category;
    if (status) params.status = status;

    const response = await apiClient.get(`${this.baseUrl}/medicines/export`, {
      params,
      responseType: 'blob'
    });
    return response.data;
  }

  /**
   * 生成药品编码
   */
  generateMedicineCode(category: string): string {
    const categoryCode = this.getCategoryCode(category);
    const timestamp = Date.now().toString().slice(-6);
    return `${categoryCode}${timestamp}`;
  }

  /**
   * 获取分类编码
   */
  private getCategoryCode(category: string): string {
    const codes: Record<string, string> = {
      '处方药': 'RX',
      '非处方药': 'OTC',
      '中药': 'TCM',
      '西药': 'WM',
      '生物制品': 'BP',
      '疫苗': 'VAC',
      '其他': 'OTH'
    };
    return codes[category] || 'MED';
  }

  /**
   * 验证药品数据
   */
  validateMedicine(medicine: CreateMedicineRequest): {
    valid: boolean;
    errors: string[];
  } {
    const errors: string[] = [];

    if (!medicine.medicineCode) {
      errors.push('药品编码不能为空');
    }

    if (!medicine.name) {
      errors.push('药品名称不能为空');
    }

    if (!medicine.category) {
      errors.push('药品分类不能为空');
    }

    if (!medicine.unit) {
      errors.push('单位不能为空');
    }

    if (medicine.purchasePrice && medicine.purchasePrice < 0) {
      errors.push('进价不能为负数');
    }

    if (medicine.sellingPrice && medicine.sellingPrice < 0) {
      errors.push('售价不能为负数');
    }

    if (medicine.minStockLevel && medicine.minStockLevel < 0) {
      errors.push('最小库存量不能为负数');
    }

    if (medicine.maxStockLevel && medicine.maxStockLevel < 0) {
      errors.push('最大库存量不能为负数');
    }

    if (medicine.minStockLevel && medicine.maxStockLevel && 
        medicine.minStockLevel > medicine.maxStockLevel) {
      errors.push('最小库存量不能大于最大库存量');
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }

  /**
   * 验证库存更新数据
   */
  validateStockUpdate(request: StockUpdateRequest): {
    valid: boolean;
    errors: string[];
  } {
    const errors: string[] = [];

    if (!request.transactionType) {
      errors.push('交易类型不能为空');
    }

    if (!request.quantity || request.quantity === 0) {
      errors.push('交易数量不能为空或零');
    }

    if (request.unitPrice && request.unitPrice < 0) {
      errors.push('单价不能为负数');
    }

    if (request.productionDate && request.expiryDate) {
      const prodDate = new Date(request.productionDate);
      const expDate = new Date(request.expiryDate);
      if (prodDate >= expDate) {
        errors.push('生产日期不能晚于或等于过期日期');
      }
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }

  /**
   * 计算利润率
   */
  calculateProfitMargin(purchasePrice: number, sellingPrice: number): number {
    if (!purchasePrice || purchasePrice === 0) {
      return 0;
    }
    return ((sellingPrice - purchasePrice) / purchasePrice) * 100;
  }

  /**
   * 检查是否需要补货
   */
  needsRestock(currentStock: number, minStockLevel: number): boolean {
    return currentStock <= minStockLevel;
  }

  /**
   * 检查是否库存过多
   */
  isOverstocked(currentStock: number, maxStockLevel: number): boolean {
    return currentStock > maxStockLevel;
  }

  /**
   * 计算剩余保质期天数
   */
  calculateRemainingShelfLife(expiryDate: string): number {
    const expiry = new Date(expiryDate);
    const now = new Date();
    const diffTime = expiry.getTime() - now.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  /**
   * 检查是否即将过期
   */
  isExpiringSoon(expiryDate: string, days: number = 30): boolean {
    const remainingDays = this.calculateRemainingShelfLife(expiryDate);
    return remainingDays <= days && remainingDays > 0;
  }

  /**
   * 检查是否已过期
   */
  isExpired(expiryDate: string): boolean {
    const remainingDays = this.calculateRemainingShelfLife(expiryDate);
    return remainingDays <= 0;
  }

  /**
   * 格式化库存状态
   */
  formatStockStatus(medicine: Medicine): {
    status: string;
    color: string;
    message: string;
  } {
    const currentStock = medicine.currentStock || 0;
    const minStock = medicine.minStockLevel || 0;
    const maxStock = medicine.maxStockLevel || 999999;

    if (currentStock === 0) {
      return { status: 'out-of-stock', color: 'red', message: '缺货' };
    } else if (currentStock <= minStock) {
      return { status: 'low-stock', color: 'orange', message: '库存不足' };
    } else if (currentStock > maxStock) {
      return { status: 'overstock', color: 'purple', message: '库存过多' };
    } else {
      return { status: 'normal', color: 'green', message: '库存正常' };
    }
  }

  /**
   * 生成交易编号
   */
  generateTransactionNumber(transactionType: string): string {
    const typeCode = this.getTransactionTypeCode(transactionType);
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const timestamp = now.getTime().toString().slice(-6);
    return `${typeCode}${year}${month}${day}${timestamp}`;
  }

  /**
   * 获取交易类型编码
   */
  private getTransactionTypeCode(transactionType: string): string {
    const codes: Record<string, string> = {
      '入库': 'IN',
      '出库': 'OUT',
      '调拨': 'TRF',
      '盘点': 'INV',
      '报损': 'DMG',
      '退货': 'RTN',
      '过期处理': 'EXP'
    };
    return codes[transactionType] || 'TXN';
  }
}

export const inventoryService = new InventoryService();