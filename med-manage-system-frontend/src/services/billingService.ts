import { apiClient } from './apiClient';
import {
  Bill,
  BillItem,
  CreateBillRequest,
  AddBillItemRequest,
  PaymentRequest,
  PaymentRecord,
  RefundRequest,
  RefundRecord,
  BillingStats,
  InsuranceClaim,
  InsuranceProvider
} from '../types/billing';

export class BillingService {
  private baseUrl = '/api/billing';

  /**
   * 创建账单
   */
  async createBill(request: CreateBillRequest): Promise<Bill> {
    const response = await apiClient.post(`${this.baseUrl}/bills`, request);
    return response.data;
  }

  /**
   * 根据挂号ID计算账单
   */
  async calculateBill(registrationId: number): Promise<Bill> {
    const response = await apiClient.post(`${this.baseUrl}/bills/calculate/${registrationId}`);
    return response.data;
  }

  /**
   * 获取账单详情
   */
  async getBillById(billId: number): Promise<Bill> {
    const response = await apiClient.get(`${this.baseUrl}/bills/${billId}`);
    return response.data;
  }

  /**
   * 根据账单编号获取账单
   */
  async getBillByNumber(billNumber: string): Promise<Bill> {
    const response = await apiClient.get(`${this.baseUrl}/bills/number/${billNumber}`);
    return response.data;
  }

  /**
   * 获取患者的所有账单
   */
  async getPatientBills(patientId: number): Promise<Bill[]> {
    const response = await apiClient.get(`${this.baseUrl}/bills/patient/${patientId}`);
    return response.data;
  }

  /**
   * 获取账单项目
   */
  async getBillItems(billId: number): Promise<BillItem[]> {
    const response = await apiClient.get(`${this.baseUrl}/bills/${billId}/items`);
    return response.data;
  }

  /**
   * 添加账单项目
   */
  async addBillItem(billId: number, request: AddBillItemRequest): Promise<BillItem> {
    const response = await apiClient.post(`${this.baseUrl}/bills/${billId}/items`, request);
    return response.data;
  }

  /**
   * 更新账单项目
   */
  async updateBillItem(billItemId: number, request: AddBillItemRequest): Promise<BillItem> {
    const response = await apiClient.put(`${this.baseUrl}/bills/items/${billItemId}`, request);
    return response.data;
  }

  /**
   * 删除账单项目
   */
  async removeBillItem(billItemId: number): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/bills/items/${billItemId}`);
  }

  /**
   * 更新支付状态
   */
  async updatePaymentStatus(billId: number, paidAmount: number): Promise<void> {
    await apiClient.put(`${this.baseUrl}/bills/${billId}/payment`, null, {
      params: { paidAmount }
    });
  }

  /**
   * 取消账单
   */
  async cancelBill(billId: number, reason: string): Promise<void> {
    await apiClient.put(`${this.baseUrl}/bills/${billId}/cancel`, null, {
      params: { reason }
    });
  }

  /**
   * 根据状态获取账单列表
   */
  async getBillsByStatus(status: string): Promise<Bill[]> {
    const response = await apiClient.get(`${this.baseUrl}/bills/status/${status}`);
    return response.data;
  }

  /**
   * 获取指定日期范围内的账单
   */
  async getBillsByDateRange(startDate: string, endDate: string): Promise<Bill[]> {
    const response = await apiClient.get(`${this.baseUrl}/bills/date-range`, {
      params: { startDate, endDate }
    });
    return response.data;
  }

  /**
   * 获取指定日期的总收入
   */
  async getTotalRevenueByDate(date: string): Promise<{ date: string; totalRevenue: number }> {
    const response = await apiClient.get(`${this.baseUrl}/revenue/${date}`);
    return response.data;
  }

  /**
   * 更新账单总金额
   */
  async updateBillTotalAmount(billId: number): Promise<void> {
    await apiClient.put(`${this.baseUrl}/bills/${billId}/total`);
  }

  /**
   * 搜索账单
   */
  async searchBills(
    keyword?: string,
    patientId?: number,
    status?: string,
    startDate?: string,
    endDate?: string
  ): Promise<Bill[]> {
    const params: any = {};
    if (keyword) params.keyword = keyword;
    if (patientId) params.patientId = patientId;
    if (status) params.status = status;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/bills/search`, { params });
    return response.data;
  }

  /**
   * 处理支付
   */
  async processPayment(request: PaymentRequest): Promise<PaymentRecord> {
    const response = await apiClient.post(`${this.baseUrl}/payments`, request);
    return response.data;
  }

  /**
   * 获取支付记录
   */
  async getPaymentRecords(billId: number): Promise<PaymentRecord[]> {
    const response = await apiClient.get(`${this.baseUrl}/payments/bill/${billId}`);
    return response.data;
  }

  /**
   * 申请退费
   */
  async requestRefund(request: RefundRequest): Promise<RefundRecord> {
    const response = await apiClient.post(`${this.baseUrl}/refunds`, request);
    return response.data;
  }

  /**
   * 获取退费记录
   */
  async getRefundRecords(billId?: number): Promise<RefundRecord[]> {
    const params = billId ? { billId } : {};
    const response = await apiClient.get(`${this.baseUrl}/refunds`, { params });
    return response.data;
  }

  /**
   * 处理退费申请
   */
  async processRefund(refundId: number, action: 'approve' | 'reject', notes?: string): Promise<RefundRecord> {
    const response = await apiClient.post(`${this.baseUrl}/refunds/${refundId}/${action}`, { notes });
    return response.data;
  }

  /**
   * 获取收费统计
   */
  async getBillingStats(
    startDate?: string,
    endDate?: string
  ): Promise<BillingStats> {
    const params: any = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/stats`, { params });
    return response.data;
  }

  /**
   * 获取医保理赔列表
   */
  async getInsuranceClaims(
    patientId?: number,
    status?: string
  ): Promise<InsuranceClaim[]> {
    const params: any = {};
    if (patientId) params.patientId = patientId;
    if (status) params.status = status;

    const response = await apiClient.get(`${this.baseUrl}/insurance-claims`, { params });
    return response.data;
  }

  /**
   * 创建医保理赔
   */
  async createInsuranceClaim(claim: Partial<InsuranceClaim>): Promise<InsuranceClaim> {
    const response = await apiClient.post(`${this.baseUrl}/insurance-claims`, claim);
    return response.data;
  }

  /**
   * 获取医保机构列表
   */
  async getInsuranceProviders(): Promise<InsuranceProvider[]> {
    const response = await apiClient.get(`${this.baseUrl}/insurance-providers`);
    return response.data;
  }

  /**
   * 导出账单
   */
  async exportBills(
    startDate?: string,
    endDate?: string,
    status?: string,
    format: 'excel' | 'pdf' = 'excel'
  ): Promise<Blob> {
    const params: any = { format };
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    if (status) params.status = status;

    const response = await apiClient.get(`${this.baseUrl}/bills/export`, {
      params,
      responseType: 'blob'
    });
    return response.data;
  }

  /**
   * 打印账单
   */
  async printBill(billId: number): Promise<{
    bill: Bill;
    billItems: BillItem[];
    clinic: {
      name: string;
      address: string;
      phone: string;
      license: string;
    };
  }> {
    const response = await apiClient.get(`${this.baseUrl}/bills/${billId}/print-data`);
    return response.data;
  }

  /**
   * 生成账单编号
   */
  generateBillNumber(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const timestamp = now.getTime().toString().slice(-6);
    return `BILL${year}${month}${day}${timestamp}`;
  }

  /**
   * 验证账单数据
   */
  validateBill(bill: CreateBillRequest): {
    valid: boolean;
    errors: string[];
  } {
    const errors: string[] = [];

    if (!bill.patientId) {
      errors.push('患者ID不能为空');
    }

    if (!bill.billItems || bill.billItems.length === 0) {
      errors.push('账单项目不能为空');
    }

    bill.billItems?.forEach((item, index) => {
      if (!item.itemType) {
        errors.push(`第${index + 1}项的项目类型不能为空`);
      }
      if (!item.itemName) {
        errors.push(`第${index + 1}项的项目名称不能为空`);
      }
      if (!item.unitPrice || item.unitPrice <= 0) {
        errors.push(`第${index + 1}项的单价必须大于0`);
      }
      if (!item.quantity || item.quantity <= 0) {
        errors.push(`第${index + 1}项的数量必须大于0`);
      }
    });

    return {
      valid: errors.length === 0,
      errors
    };
  }

  /**
   * 计算账单总金额
   */
  calculateBillTotal(billItems: BillItem[]): number {
    return billItems.reduce((total, item) => total + item.actualAmount, 0);
  }

  /**
   * 计算项目小计
   */
  calculateItemSubtotal(unitPrice: number, quantity: number): number {
    return unitPrice * quantity;
  }

  /**
   * 计算项目实际金额
   */
  calculateItemActualAmount(subtotal: number, discount: number): number {
    const actualAmount = subtotal - discount;
    return actualAmount < 0 ? 0 : actualAmount;
  }

  /**
   * 应用折扣率
   */
  applyDiscountRate(subtotal: number, discountRate: number): number {
    return subtotal * discountRate;
  }

  /**
   * 检查支付金额是否有效
   */
  validatePaymentAmount(billTotal: number, paidAmount: number, paymentAmount: number): {
    valid: boolean;
    error?: string;
  } {
    const remainingAmount = billTotal - paidAmount;
    
    if (paymentAmount <= 0) {
      return { valid: false, error: '支付金额必须大于0' };
    }
    
    if (paymentAmount > remainingAmount) {
      return { valid: false, error: '支付金额不能超过剩余未付金额' };
    }
    
    return { valid: true };
  }

  /**
   * 检查退费金额是否有效
   */
  validateRefundAmount(paidAmount: number, refundAmount: number): {
    valid: boolean;
    error?: string;
  } {
    if (refundAmount <= 0) {
      return { valid: false, error: '退费金额必须大于0' };
    }
    
    if (refundAmount > paidAmount) {
      return { valid: false, error: '退费金额不能超过已付金额' };
    }
    
    return { valid: true };
  }
}

export const billingService = new BillingService();