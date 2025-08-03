import { apiClient } from './apiClient';
import {
  DispenseRecord,
  DispenseItem,
  DispenseEligibilityResult,
  StartDispensingRequest,
  DispenseMedicineRequest,
  DeliverMedicineRequest,
  SubstituteMedicineRequest,
  ReturnPrescriptionRequest,
  CancelDispensingRequest,
  ReviewDispensingRequest,
  PharmacyStats
} from '../types/pharmacy';

export class PharmacyService {
  private baseUrl = '/pharmacy';

  /**
   * 获取待调剂的处方列表
   */
  async getPendingPrescriptions(): Promise<any[]> {
    const response = await apiClient.get(`${this.baseUrl}/prescriptions/pending`);
    return response.data;
  }

  /**
   * 检查处方调剂资格
   */
  async checkDispenseEligibility(prescriptionId: number): Promise<DispenseEligibilityResult> {
    const response = await apiClient.get(`${this.baseUrl}/prescriptions/${prescriptionId}/eligibility`);
    return response.data;
  }

  /**
   * 开始处方调剂
   */
  async startDispensing(request: StartDispensingRequest): Promise<DispenseRecord> {
    const response = await apiClient.post(`${this.baseUrl}/dispense/start`, request);
    return response.data;
  }

  /**
   * 调剂单个药品项目
   */
  async dispenseMedicineItem(request: DispenseMedicineRequest): Promise<DispenseItem> {
    const response = await apiClient.post(`${this.baseUrl}/dispense/medicine`, request);
    return response.data;
  }

  /**
   * 完成处方调剂
   */
  async completeDispensing(dispenseRecordId: number): Promise<DispenseRecord> {
    const response = await apiClient.post(`${this.baseUrl}/dispense/${dispenseRecordId}/complete`);
    return response.data;
  }

  /**
   * 发药
   */
  async deliverMedicine(
    dispenseRecordId: number,
    request: DeliverMedicineRequest
  ): Promise<DispenseRecord> {
    const response = await apiClient.post(`${this.baseUrl}/dispense/${dispenseRecordId}/deliver`, request);
    return response.data;
  }

  /**
   * 替代药品
   */
  async substituteMedicine(
    dispenseItemId: number,
    request: SubstituteMedicineRequest
  ): Promise<DispenseItem> {
    const response = await apiClient.post(`${this.baseUrl}/dispense/items/${dispenseItemId}/substitute`, request);
    return response.data;
  }

  /**
   * 退回处方
   */
  async returnPrescription(
    dispenseRecordId: number,
    request: ReturnPrescriptionRequest
  ): Promise<DispenseRecord> {
    const response = await apiClient.post(`${this.baseUrl}/dispense/${dispenseRecordId}/return`, request);
    return response.data;
  }

  /**
   * 取消调剂
   */
  async cancelDispensing(
    dispenseRecordId: number,
    request: CancelDispensingRequest
  ): Promise<DispenseRecord> {
    const response = await apiClient.post(`${this.baseUrl}/dispense/${dispenseRecordId}/cancel`, request);
    return response.data;
  }

  /**
   * 复核调剂
   */
  async reviewDispensing(
    dispenseRecordId: number,
    request: ReviewDispensingRequest
  ): Promise<DispenseRecord> {
    const response = await apiClient.post(`${this.baseUrl}/dispense/${dispenseRecordId}/review`, request);
    return response.data;
  }

  /**
   * 获取调剂中的记录列表
   */
  async getInProgressDispenses(): Promise<DispenseRecord[]> {
    const response = await apiClient.get(`${this.baseUrl}/dispense/in-progress`);
    return response.data;
  }

  /**
   * 获取待发药的记录列表
   */
  async getReadyForDelivery(): Promise<DispenseRecord[]> {
    const response = await apiClient.get(`${this.baseUrl}/dispense/ready-for-delivery`);
    return response.data;
  }

  /**
   * 获取需要复核的记录列表
   */
  async getNeedingReview(): Promise<DispenseRecord[]> {
    const response = await apiClient.get(`${this.baseUrl}/dispense/needing-review`);
    return response.data;
  }

  /**
   * 根据处方ID获取调剂记录
   */
  async getDispenseRecordByPrescriptionId(prescriptionId: number): Promise<DispenseRecord | null> {
    try {
      const response = await apiClient.get(`${this.baseUrl}/dispense/prescription/${prescriptionId}`);
      return response.data;
    } catch (error) {
      return null;
    }
  }

  /**
   * 获取调剂记录详情（包含调剂项目）
   */
  async getDispenseRecordWithItems(dispenseRecordId: number): Promise<DispenseRecord | null> {
    try {
      const response = await apiClient.get(`${this.baseUrl}/dispense/${dispenseRecordId}`);
      return response.data;
    } catch (error) {
      return null;
    }
  }

  /**
   * 获取药师的调剂记录列表
   */
  async getDispenseRecordsByPharmacist(pharmacistId: number): Promise<DispenseRecord[]> {
    const response = await apiClient.get(`${this.baseUrl}/dispense/pharmacist/${pharmacistId}`);
    return response.data;
  }

  /**
   * 获取患者的调剂记录列表
   */
  async getDispenseRecordsByPatient(patientId: number): Promise<DispenseRecord[]> {
    const response = await apiClient.get(`${this.baseUrl}/dispense/patient/${patientId}`);
    return response.data;
  }

  /**
   * 获取药房统计数据
   */
  async getPharmacyStats(
    pharmacistId?: number,
    startDate?: string,
    endDate?: string
  ): Promise<PharmacyStats> {
    const params: any = {};
    if (pharmacistId) params.pharmacistId = pharmacistId;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/stats`, { params });
    return response.data;
  }

  /**
   * 搜索调剂记录
   */
  async searchDispenseRecords(
    keyword?: string,
    status?: string,
    pharmacistId?: number,
    startDate?: string,
    endDate?: string
  ): Promise<DispenseRecord[]> {
    const params: any = {};
    if (keyword) params.keyword = keyword;
    if (status) params.status = status;
    if (pharmacistId) params.pharmacistId = pharmacistId;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/dispense/search`, { params });
    return response.data;
  }

  /**
   * 导出调剂记录
   */
  async exportDispenseRecords(
    startDate?: string,
    endDate?: string,
    status?: string,
    format: 'excel' | 'pdf' = 'excel'
  ): Promise<Blob> {
    const params: any = { format };
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    if (status) params.status = status;

    const response = await apiClient.get(`${this.baseUrl}/dispense/export`, {
      params,
      responseType: 'blob'
    });
    return response.data;
  }

  /**
   * 验证调剂数据
   */
  validateDispenseRequest(request: DispenseMedicineRequest): {
    valid: boolean;
    errors: string[];
  } {
    const errors: string[] = [];

    if (!request.dispenseRecordId) {
      errors.push('调剂记录ID不能为空');
    }

    if (!request.prescriptionItemId) {
      errors.push('处方项目ID不能为空');
    }

    if (!request.medicineId) {
      errors.push('药品ID不能为空');
    }

    if (!request.dispensedQuantity || request.dispensedQuantity <= 0) {
      errors.push('调剂数量必须大于0');
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }

  /**
   * 计算调剂时间
   */
  calculateDispensingTime(startTime: string, endTime: string): number {
    const start = new Date(startTime);
    const end = new Date(endTime);
    return Math.round((end.getTime() - start.getTime()) / (1000 * 60)); // 返回分钟数
  }

  /**
   * 检查是否超时
   */
  isOverdue(startTime: string, timeoutMinutes: number = 30): boolean {
    const start = new Date(startTime);
    const now = new Date();
    const elapsedMinutes = (now.getTime() - start.getTime()) / (1000 * 60);
    return elapsedMinutes > timeoutMinutes;
  }

  /**
   * 格式化调剂状态
   */
  formatDispenseStatus(status: string): {
    text: string;
    color: string;
    badge: string;
  } {
    const statusMap: Record<string, any> = {
      PENDING: { text: '待调剂', color: 'warning', badge: 'warning' },
      IN_PROGRESS: { text: '调剂中', color: 'processing', badge: 'processing' },
      COMPLETED: { text: '已完成', color: 'success', badge: 'success' },
      DELIVERED: { text: '已发药', color: 'success', badge: 'success' },
      REVIEWED: { text: '已复核', color: 'success', badge: 'success' },
      RETURNED: { text: '已退回', color: 'error', badge: 'error' },
      CANCELLED: { text: '已取消', color: 'default', badge: 'default' }
    };
    return statusMap[status] || { text: status, color: 'default', badge: 'default' };
  }

  /**
   * 生成调剂编号
   */
  generateDispenseNumber(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const timestamp = now.getTime().toString().slice(-6);
    return `DISP${year}${month}${day}${timestamp}`;
  }
}

export const pharmacyService = new PharmacyService();