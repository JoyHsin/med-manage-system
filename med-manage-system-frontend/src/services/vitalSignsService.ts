import { apiClient } from './apiClient';
import { VitalSigns, VitalSignsRequest, VitalSignsValidationResult, VitalSignsResponse } from '../types/vitalSigns';

export class VitalSignsService {
  private baseUrl = '/vital-signs';

  /**
   * 录入生命体征
   */
  async recordVitalSigns(request: VitalSignsRequest): Promise<VitalSignsResponse> {
    const response = await apiClient.post(this.baseUrl, request);
    return response.data;
  }

  /**
   * 获取生命体征记录详情
   */
  async getVitalSigns(id: number): Promise<VitalSigns> {
    const response = await apiClient.get(`${this.baseUrl}/${id}`);
    return response.data.data;
  }

  /**
   * 获取患者的生命体征记录列表
   */
  async getPatientVitalSigns(patientId: number): Promise<VitalSigns[]> {
    const response = await apiClient.get(`${this.baseUrl}/patient/${patientId}`);
    return response.data.data;
  }

  /**
   * 获取患者指定时间范围内的生命体征记录
   */
  async getPatientVitalSignsByTimeRange(
    patientId: number,
    startTime?: string,
    endTime?: string
  ): Promise<VitalSigns[]> {
    const params: any = {};
    if (startTime) params.startTime = startTime;
    if (endTime) params.endTime = endTime;

    const response = await apiClient.get(`${this.baseUrl}/patient/${patientId}/range`, { params });
    return response.data.data;
  }

  /**
   * 获取患者最新的生命体征记录
   */
  async getLatestVitalSigns(patientId: number): Promise<VitalSigns | null> {
    try {
      const response = await apiClient.get(`${this.baseUrl}/patient/${patientId}/latest`);
      return response.data.data;
    } catch (error: any) {
      if (error.response?.status === 404) {
        return null;
      }
      throw error;
    }
  }

  /**
   * 获取异常生命体征记录
   */
  async getAbnormalVitalSigns(
    patientId?: number,
    startTime?: string,
    endTime?: string
  ): Promise<VitalSigns[]> {
    const params: any = {};
    if (patientId) params.patientId = patientId;
    if (startTime) params.startTime = startTime;
    if (endTime) params.endTime = endTime;

    const response = await apiClient.get(`${this.baseUrl}/abnormal`, { params });
    return response.data.data;
  }

  /**
   * 更新生命体征记录
   */
  async updateVitalSigns(id: number, request: VitalSignsRequest): Promise<VitalSignsResponse> {
    const response = await apiClient.put(`${this.baseUrl}/${id}`, request);
    return response.data;
  }

  /**
   * 删除生命体征记录
   */
  async deleteVitalSigns(id: number): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/${id}`);
  }

  /**
   * 验证生命体征数据
   */
  async validateVitalSigns(request: VitalSignsRequest): Promise<VitalSignsValidationResult> {
    const response = await apiClient.post(`${this.baseUrl}/validate`, request);
    return response.data;
  }

  /**
   * 获取记录人员的生命体征记录
   */
  async getVitalSignsByRecorder(
    recordedBy: number,
    startTime: string,
    endTime: string
  ): Promise<VitalSigns[]> {
    const response = await apiClient.get(`${this.baseUrl}/recorder/${recordedBy}`, {
      params: { startTime, endTime }
    });
    return response.data.data;
  }

  /**
   * 计算BMI
   */
  calculateBMI(weight: number, height: number): number {
    if (weight <= 0 || height <= 0) return 0;
    const heightInMeters = height / 100;
    return Math.round((weight / (heightInMeters * heightInMeters)) * 10) / 10;
  }

  /**
   * 检查数值是否在正常范围内
   */
  isValueNormal(value: number, min: number, max: number): boolean {
    return value >= min && value <= max;
  }

  /**
   * 获取BMI分类
   */
  getBMICategory(bmi: number): string {
    if (bmi < 18.5) return 'underweight';
    if (bmi < 24.0) return 'normal';
    if (bmi < 28.0) return 'overweight';
    return 'obese';
  }
}

export const vitalSignsService = new VitalSignsService();