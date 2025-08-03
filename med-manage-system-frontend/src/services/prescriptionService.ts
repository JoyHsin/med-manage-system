import { apiClient } from './apiClient';
import {
  Prescription,
  CreatePrescriptionRequest,
  Medicine,
  PrescriptionPrintData
} from '../types/prescription';

export class PrescriptionService {
  private baseUrl = '/prescriptions';
  private pharmacyUrl = '/pharmacy';

  /**
   * 创建处方
   */
  async createPrescription(request: CreatePrescriptionRequest): Promise<Prescription> {
    const response = await apiClient.post(this.baseUrl, request);
    return response.data;
  }

  /**
   * 获取处方详情
   */
  async getPrescription(id: number): Promise<Prescription> {
    const response = await apiClient.get(`${this.baseUrl}/${id}`);
    return response.data;
  }

  /**
   * 更新处方
   */
  async updatePrescription(id: number, request: CreatePrescriptionRequest): Promise<Prescription> {
    const response = await apiClient.put(`${this.baseUrl}/${id}`, request);
    return response.data;
  }

  /**
   * 删除处方
   */
  async deletePrescription(id: number): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/${id}`);
  }

  /**
   * 获取医生的处方列表
   */
  async getDoctorPrescriptions(
    doctorId: number,
    startDate?: string,
    endDate?: string,
    status?: string
  ): Promise<Prescription[]> {
    const params: any = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    if (status) params.status = status;

    const response = await apiClient.get(`${this.baseUrl}/doctor/${doctorId}`, { params });
    return response.data;
  }

  /**
   * 获取患者的处方列表
   */
  async getPatientPrescriptions(patientId: number): Promise<Prescription[]> {
    const response = await apiClient.get(`${this.baseUrl}/patient/${patientId}`);
    return response.data;
  }

  /**
   * 搜索处方
   */
  async searchPrescriptions(
    keyword?: string,
    doctorId?: number,
    patientId?: number,
    status?: string,
    prescriptionType?: string,
    startDate?: string,
    endDate?: string
  ): Promise<Prescription[]> {
    const params: any = {};
    if (keyword) params.keyword = keyword;
    if (doctorId) params.doctorId = doctorId;
    if (patientId) params.patientId = patientId;
    if (status) params.status = status;
    if (prescriptionType) params.prescriptionType = prescriptionType;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/search`, { params });
    return response.data;
  }

  /**
   * 开具处方（提交）
   */
  async issuePrescription(id: number): Promise<Prescription> {
    const response = await apiClient.post(`${this.baseUrl}/${id}/issue`);
    return response.data;
  }

  /**
   * 审核处方
   */
  async reviewPrescription(id: number, comments?: string): Promise<Prescription> {
    const response = await apiClient.post(`${this.baseUrl}/${id}/review`, { comments });
    return response.data;
  }

  /**
   * 取消处方
   */
  async cancelPrescription(id: number, reason: string): Promise<Prescription> {
    const response = await apiClient.post(`${this.baseUrl}/${id}/cancel`, { reason });
    return response.data;
  }

  /**
   * 复制处方
   */
  async copyPrescription(id: number): Promise<Prescription> {
    const response = await apiClient.post(`${this.baseUrl}/${id}/copy`);
    return response.data;
  }

  /**
   * 获取处方模板
   */
  async getPrescriptionTemplates(doctorId: number): Promise<Prescription[]> {
    const response = await apiClient.get(`${this.baseUrl}/templates/doctor/${doctorId}`);
    return response.data;
  }

  /**
   * 保存为模板
   */
  async saveAsTemplate(id: number, templateName: string): Promise<void> {
    await apiClient.post(`${this.baseUrl}/${id}/save-template`, { templateName });
  }

  /**
   * 从模板创建处方
   */
  async createFromTemplate(templateId: number, medicalRecordId: number): Promise<Prescription> {
    const response = await apiClient.post(`${this.baseUrl}/create-from-template`, {
      templateId,
      medicalRecordId
    });
    return response.data;
  }

  /**
   * 获取药品列表
   */
  async getMedicines(
    keyword?: string,
    category?: string,
    isPrescriptionDrug?: boolean
  ): Promise<Medicine[]> {
    const params: any = {};
    if (keyword) params.keyword = keyword;
    if (category) params.category = category;
    if (isPrescriptionDrug !== undefined) params.isPrescriptionDrug = isPrescriptionDrug;

    const response = await apiClient.get('/medicines', { params });
    return response.data;
  }

  /**
   * 获取药品详情
   */
  async getMedicine(id: number): Promise<Medicine> {
    const response = await apiClient.get(`/medicines/${id}`);
    return response.data;
  }

  /**
   * 检查药品库存
   */
  async checkMedicineStock(medicineId: number, quantity: number): Promise<{
    available: boolean;
    currentStock: number;
    message: string;
  }> {
    const response = await apiClient.get(`/medicines/${medicineId}/stock-check`, {
      params: { quantity }
    });
    return response.data;
  }

  /**
   * 检查药品相互作用
   */
  async checkDrugInteractions(medicineIds: number[]): Promise<{
    hasInteractions: boolean;
    interactions: Array<{
      medicine1: string;
      medicine2: string;
      severity: string;
      description: string;
    }>;
  }> {
    const response = await apiClient.post('/medicines/interaction-check', { medicineIds });
    return response.data;
  }

  /**
   * 检查用药禁忌
   */
  async checkContraindications(
    patientId: number,
    medicineIds: number[]
  ): Promise<{
    hasContraindications: boolean;
    contraindications: Array<{
      medicine: string;
      type: string;
      description: string;
    }>;
  }> {
    const response = await apiClient.post('/medicines/contraindication-check', {
      patientId,
      medicineIds
    });
    return response.data;
  }

  /**
   * 获取常用药品
   */
  async getCommonMedicines(doctorId: number): Promise<Medicine[]> {
    const response = await apiClient.get(`/medicines/common/doctor/${doctorId}`);
    return response.data;
  }

  /**
   * 添加常用药品
   */
  async addCommonMedicine(doctorId: number, medicineId: number): Promise<void> {
    await apiClient.post('/medicines/common', { doctorId, medicineId });
  }

  /**
   * 移除常用药品
   */
  async removeCommonMedicine(doctorId: number, medicineId: number): Promise<void> {
    await apiClient.delete(`/medicines/common/${doctorId}/${medicineId}`);
  }

  /**
   * 获取处方统计
   */
  async getPrescriptionStats(
    doctorId?: number,
    startDate?: string,
    endDate?: string
  ): Promise<{
    totalPrescriptions: number;
    issuedPrescriptions: number;
    reviewedPrescriptions: number;
    dispensedPrescriptions: number;
    cancelledPrescriptions: number;
    averageItemsPerPrescription: number;
    totalAmount: number;
  }> {
    const params: any = {};
    if (doctorId) params.doctorId = doctorId;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/stats`, { params });
    return response.data;
  }

  /**
   * 打印处方
   */
  async printPrescription(id: number): Promise<PrescriptionPrintData> {
    const response = await apiClient.get(`${this.baseUrl}/${id}/print-data`);
    return response.data;
  }

  /**
   * 导出处方
   */
  async exportPrescriptions(
    doctorId?: number,
    startDate?: string,
    endDate?: string,
    format: 'excel' | 'pdf' = 'excel'
  ): Promise<Blob> {
    const params: any = { format };
    if (doctorId) params.doctorId = doctorId;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/export`, {
      params,
      responseType: 'blob'
    });
    return response.data;
  }

  /**
   * 获取待调剂处方列表（药房用）
   */
  async getPendingPrescriptions(): Promise<Prescription[]> {
    const response = await apiClient.get(`${this.pharmacyUrl}/prescriptions/pending`);
    return response.data;
  }

  /**
   * 检查处方调剂资格
   */
  async checkDispenseEligibility(prescriptionId: number): Promise<{
    eligible: boolean;
    reason?: string;
    warnings: string[];
  }> {
    const response = await apiClient.get(`${this.pharmacyUrl}/prescriptions/${prescriptionId}/eligibility`);
    return response.data;
  }

  /**
   * 生成处方编号
   */
  generatePrescriptionNumber(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const timestamp = now.getTime().toString().slice(-6);
    return `RX${year}${month}${day}${timestamp}`;
  }

  /**
   * 验证处方数据
   */
  validatePrescription(prescription: CreatePrescriptionRequest): {
    valid: boolean;
    errors: string[];
  } {
    const errors: string[] = [];

    if (!prescription.medicalRecordId) {
      errors.push('病历ID不能为空');
    }

    if (!prescription.prescriptionItems || prescription.prescriptionItems.length === 0) {
      errors.push('处方项目不能为空');
    }

    prescription.prescriptionItems?.forEach((item, index) => {
      if (!item.medicineId) {
        errors.push(`第${index + 1}项药品不能为空`);
      }
      if (!item.quantity || item.quantity <= 0) {
        errors.push(`第${index + 1}项药品数量必须大于0`);
      }
      if (!item.unit) {
        errors.push(`第${index + 1}项药品单位不能为空`);
      }
    });

    if (prescription.validityDays <= 0) {
      errors.push('处方有效期必须大于0');
    }

    if (prescription.repeatTimes <= 0) {
      errors.push('重复次数必须大于0');
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }
}

export const prescriptionService = new PrescriptionService();