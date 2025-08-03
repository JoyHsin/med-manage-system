import { apiClient } from './apiClient';
import { PatientQueue, CallPatientRequest, QueueFilters } from '../types/triage';

export class TriageService {
  private baseUrl = '/triage';

  /**
   * 获取当日患者队列
   */
  async getTodayPatientQueue(): Promise<PatientQueue[]> {
    const response = await apiClient.get(`${this.baseUrl}/queue/today`);
    return response.data;
  }

  /**
   * 获取指定日期的患者队列
   */
  async getPatientQueueByDate(queueDate: string): Promise<PatientQueue[]> {
    const response = await apiClient.get(`${this.baseUrl}/queue`, {
      params: { queueDate }
    });
    return response.data;
  }

  /**
   * 获取指定状态的患者队列
   */
  async getPatientQueueByStatus(status: string, queueDate?: string): Promise<PatientQueue[]> {
    const params: any = {};
    if (queueDate) {
      params.queueDate = queueDate;
    }
    
    const response = await apiClient.get(`${this.baseUrl}/queue/status/${status}`, {
      params
    });
    return response.data;
  }

  /**
   * 获取下一个待叫号的患者
   */
  async getNextPatient(queueDate?: string): Promise<PatientQueue | null> {
    const params: any = {};
    if (queueDate) {
      params.queueDate = queueDate;
    }
    
    try {
      const response = await apiClient.get(`${this.baseUrl}/queue/next`, { params });
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 204) {
        return null; // 没有待叫号的患者
      }
      throw error;
    }
  }

  /**
   * 叫号
   */
  async callPatient(request: CallPatientRequest): Promise<void> {
    await apiClient.post(`${this.baseUrl}/call`, request);
  }

  /**
   * 确认患者到达
   */
  async confirmPatientArrival(request: CallPatientRequest): Promise<void> {
    await apiClient.post(`${this.baseUrl}/confirm-arrival`, request);
  }

  /**
   * 标记患者未到
   */
  async markPatientAbsent(request: CallPatientRequest): Promise<void> {
    await apiClient.post(`${this.baseUrl}/mark-absent`, request);
  }

  /**
   * 重新叫号
   */
  async recallPatient(request: CallPatientRequest): Promise<void> {
    await apiClient.post(`${this.baseUrl}/recall`, request);
  }

  /**
   * 完成患者就诊
   */
  async completePatient(patientQueueId: number): Promise<void> {
    await apiClient.post(`${this.baseUrl}/complete/${patientQueueId}`);
  }

  /**
   * 创建患者队列记录
   */
  async createPatientQueue(registrationId: number, priority?: number): Promise<PatientQueue> {
    const params: any = { registrationId };
    if (priority !== undefined) {
      params.priority = priority;
    }
    
    const response = await apiClient.post(`${this.baseUrl}/queue`, null, { params });
    return response.data;
  }
}

export const triageService = new TriageService();