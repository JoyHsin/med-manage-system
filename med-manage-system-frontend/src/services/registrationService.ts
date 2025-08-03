import { apiClient } from './apiClient';
import type { 
  Registration, 
  CreateRegistrationRequest,
  ApiResponse 
} from '../types/appointment';

class RegistrationService {
  // 创建挂号
  async createRegistration(registrationData: CreateRegistrationRequest): Promise<ApiResponse<Registration>> {
    const response = await apiClient.post<ApiResponse<Registration>>('/registrations', registrationData);
    return response.data;
  }

  // 根据ID获取挂号记录
  async getRegistrationById(id: number): Promise<ApiResponse<Registration>> {
    const response = await apiClient.get<ApiResponse<Registration>>(`/registrations/${id}`);
    return response.data;
  }

  // 根据挂号编号获取挂号记录
  async getRegistrationByNumber(number: string): Promise<ApiResponse<Registration>> {
    const response = await apiClient.get<ApiResponse<Registration>>(`/registrations/number/${number}`);
    return response.data;
  }

  // 根据患者ID获取挂号记录
  async getRegistrationsByPatientId(patientId: number): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>(`/registrations/patient/${patientId}`);
    return response.data;
  }

  // 根据日期获取挂号记录
  async getRegistrationsByDate(date: string): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>(`/registrations/date/${date}`);
    return response.data;
  }

  // 根据科室获取挂号记录
  async getRegistrationsByDepartment(department: string): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>(`/registrations/department/${department}`);
    return response.data;
  }

  // 根据医生ID获取挂号记录
  async getRegistrationsByDoctorId(doctorId: number): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>(`/registrations/doctor/${doctorId}`);
    return response.data;
  }

  // 获取今日挂号记录
  async getTodayRegistrations(): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>('/registrations/today');
    return response.data;
  }

  // 获取今日指定科室的挂号记录
  async getTodayRegistrationsByDepartment(department: string): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>(`/registrations/today/department/${department}`);
    return response.data;
  }

  // 获取今日指定医生的挂号记录
  async getTodayRegistrationsByDoctor(doctorId: number): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>(`/registrations/today/doctor/${doctorId}`);
    return response.data;
  }

  // 获取待叫号的挂号记录
  async getPendingRegistrations(): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>('/registrations/pending');
    return response.data;
  }

  // 获取指定科室待叫号的挂号记录
  async getPendingRegistrationsByDepartment(department: string): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>(`/registrations/pending/department/${department}`);
    return response.data;
  }

  // 获取指定医生待叫号的挂号记录
  async getPendingRegistrationsByDoctor(doctorId: number): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>(`/registrations/pending/doctor/${doctorId}`);
    return response.data;
  }

  // 获取已叫号但未到达的挂号记录
  async getCalledButNotArrivedRegistrations(): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>('/registrations/called-not-arrived');
    return response.data;
  }

  // 获取正在就诊的挂号记录
  async getActiveConsultations(): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>('/registrations/active-consultations');
    return response.data;
  }

  // 叫号
  async callPatient(id: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/registrations/${id}/call`);
    return response.data;
  }

  // 标记患者到达
  async markPatientArrived(id: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/registrations/${id}/arrive`);
    return response.data;
  }

  // 开始就诊
  async startConsultation(id: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/registrations/${id}/start`);
    return response.data;
  }

  // 完成就诊
  async completeConsultation(id: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/registrations/${id}/complete`);
    return response.data;
  }

  // 取消挂号
  async cancelRegistration(id: number, reason: string): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/registrations/${id}/cancel`, null, {
      params: { reason }
    });
    return response.data;
  }

  // 标记未到
  async markNoShow(id: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/registrations/${id}/no-show`);
    return response.data;
  }

  // 标记已支付
  async markAsPaid(id: number, paymentMethod: string): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/registrations/${id}/pay`, null, {
      params: { paymentMethod }
    });
    return response.data;
  }

  // 更新挂号状态
  async updateRegistrationStatus(id: number, status: string): Promise<ApiResponse<any>> {
    const response = await apiClient.put<ApiResponse<any>>(`/registrations/${id}/status`, null, {
      params: { status }
    });
    return response.data;
  }

  // 根据状态获取挂号记录
  async getRegistrationsByStatus(status: string): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>(`/registrations/status/${status}`);
    return response.data;
  }

  // 根据支付状态获取挂号记录
  async getRegistrationsByPaymentStatus(paymentStatus: string): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>(`/registrations/payment-status/${paymentStatus}`);
    return response.data;
  }

  // 获取急诊挂号记录
  async getEmergencyRegistrations(): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>('/registrations/emergency');
    return response.data;
  }

  // 获取初诊挂号记录
  async getFirstVisitRegistrations(): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>('/registrations/first-visit');
    return response.data;
  }

  // 获取未支付的挂号记录
  async getUnpaidRegistrations(): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>('/registrations/unpaid');
    return response.data;
  }

  // 获取已完成的挂号记录
  async getCompletedRegistrations(): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>('/registrations/completed');
    return response.data;
  }

  // 获取挂号统计信息
  async getRegistrationStatistics(): Promise<ApiResponse<any>> {
    const response = await apiClient.get<ApiResponse<any>>('/registrations/statistics');
    return response.data;
  }

  // 获取患者最近的挂号记录
  async getLatestRegistrationByPatient(patientId: number): Promise<ApiResponse<Registration>> {
    const response = await apiClient.get<ApiResponse<Registration>>(`/registrations/patient/${patientId}/latest`);
    return response.data;
  }

  // 从预约创建挂号
  async createRegistrationFromAppointment(appointmentId: number): Promise<ApiResponse<Registration>> {
    const response = await apiClient.post<ApiResponse<Registration>>(`/registrations/from-appointment/${appointmentId}`);
    return response.data;
  }

  // 生成挂号编号
  async generateRegistrationNumber(): Promise<ApiResponse<{ registrationNumber: string }>> {
    const response = await apiClient.get<ApiResponse<{ registrationNumber: string }>>('/registrations/generate-number');
    return response.data;
  }

  // 获取所有挂号记录（带分页和筛选）
  async getAllRegistrations(params?: {
    page?: number;
    size?: number;
    status?: string;
    department?: string;
    registrationType?: string;
    paymentStatus?: string;
    startDate?: string;
    endDate?: string;
    keyword?: string;
  }): Promise<ApiResponse<Registration[]>> {
    const response = await apiClient.get<ApiResponse<Registration[]>>('/registrations', { params });
    return response.data;
  }
}

export const registrationService = new RegistrationService();