import { apiClient } from './apiClient';
import type { 
  Appointment, 
  CreateAppointmentRequest, 
  UpdateAppointmentRequest,
  AvailableSlot,
  ApiResponse 
} from '../types/appointment';

class AppointmentService {
  // 创建预约
  async createAppointment(appointmentData: CreateAppointmentRequest): Promise<ApiResponse<Appointment>> {
    const response = await apiClient.post<ApiResponse<Appointment>>('/appointments', appointmentData);
    return response.data;
  }

  // 更新预约
  async updateAppointment(id: number, appointmentData: UpdateAppointmentRequest): Promise<ApiResponse<Appointment>> {
    const response = await apiClient.put<ApiResponse<Appointment>>(`/appointments/${id}`, appointmentData);
    return response.data;
  }

  // 根据ID获取预约
  async getAppointmentById(id: number): Promise<ApiResponse<Appointment>> {
    const response = await apiClient.get<ApiResponse<Appointment>>(`/appointments/${id}`);
    return response.data;
  }

  // 根据患者ID获取预约列表
  async getAppointmentsByPatientId(patientId: number): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>(`/appointments/patient/${patientId}`);
    return response.data;
  }

  // 根据医生ID获取预约列表
  async getAppointmentsByDoctorId(doctorId: number): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>(`/appointments/doctor/${doctorId}`);
    return response.data;
  }

  // 根据日期获取预约列表
  async getAppointmentsByDate(date: string): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>(`/appointments/date/${date}`);
    return response.data;
  }

  // 根据医生ID和日期获取预约列表
  async getAppointmentsByDoctorAndDate(doctorId: number, date: string): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>(`/appointments/doctor/${doctorId}/date/${date}`);
    return response.data;
  }

  // 获取医生的可用时间段
  async getAvailableSlots(doctorId: number, date: string): Promise<ApiResponse<AvailableSlot[]>> {
    const response = await apiClient.get<ApiResponse<AvailableSlot[]>>(`/appointments/doctor/${doctorId}/available-slots`, {
      params: { date }
    });
    return response.data;
  }

  // 检查时间冲突
  async checkTimeConflict(doctorId: number, appointmentTime: string): Promise<ApiResponse<{ hasConflict: boolean }>> {
    const response = await apiClient.get<ApiResponse<{ hasConflict: boolean }>>(`/appointments/doctor/${doctorId}/time-conflict`, {
      params: { appointmentTime }
    });
    return response.data;
  }

  // 确认预约
  async confirmAppointment(id: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/appointments/${id}/confirm`);
    return response.data;
  }

  // 取消预约
  async cancelAppointment(id: number, reason: string): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/appointments/${id}/cancel`, null, {
      params: { reason }
    });
    return response.data;
  }

  // 标记患者到达
  async markPatientArrived(id: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/appointments/${id}/arrive`);
    return response.data;
  }

  // 开始就诊
  async startConsultation(id: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/appointments/${id}/start`);
    return response.data;
  }

  // 完成就诊
  async completeConsultation(id: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/appointments/${id}/complete`);
    return response.data;
  }

  // 标记未到
  async markNoShow(id: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/appointments/${id}/no-show`);
    return response.data;
  }

  // 获取今日预约
  async getTodayAppointments(): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>('/appointments/today');
    return response.data;
  }

  // 获取明日预约
  async getTomorrowAppointments(): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>('/appointments/tomorrow');
    return response.data;
  }

  // 根据状态获取预约
  async getAppointmentsByStatus(status: string): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>(`/appointments/status/${status}`);
    return response.data;
  }

  // 根据预约类型获取预约
  async getAppointmentsByType(type: string): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>(`/appointments/type/${type}`);
    return response.data;
  }

  // 根据科室获取预约
  async getAppointmentsByDepartment(department: string): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>(`/appointments/department/${department}`);
    return response.data;
  }

  // 获取高优先级预约
  async getHighPriorityAppointments(): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>('/appointments/high-priority');
    return response.data;
  }

  // 获取需要提醒的预约
  async getAppointmentsNeedingReminder(): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>('/appointments/reminders');
    return response.data;
  }

  // 获取过期预约
  async getExpiredAppointments(): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>('/appointments/expired');
    return response.data;
  }

  // 获取预约统计信息
  async getAppointmentStatistics(): Promise<ApiResponse<any>> {
    const response = await apiClient.get<ApiResponse<any>>('/appointments/statistics');
    return response.data;
  }

  // 获取患者最近的预约
  async getLatestAppointmentByPatient(patientId: number): Promise<ApiResponse<Appointment>> {
    const response = await apiClient.get<ApiResponse<Appointment>>(`/appointments/patient/${patientId}/latest`);
    return response.data;
  }

  // 获取医生的下一个预约
  async getNextAppointmentByDoctor(doctorId: number): Promise<ApiResponse<Appointment>> {
    const response = await apiClient.get<ApiResponse<Appointment>>(`/appointments/doctor/${doctorId}/next`);
    return response.data;
  }

  // 批量处理过期预约
  async processExpiredAppointments(): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>('/appointments/process-expired');
    return response.data;
  }

  // 发送预约提醒
  async sendAppointmentReminders(): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>('/appointments/send-reminders');
    return response.data;
  }

  // 获取所有预约（带分页和筛选）
  async getAllAppointments(params?: {
    page?: number;
    size?: number;
    status?: string;
    department?: string;
    appointmentType?: string;
    startDate?: string;
    endDate?: string;
    keyword?: string;
  }): Promise<ApiResponse<Appointment[]>> {
    const response = await apiClient.get<ApiResponse<Appointment[]>>('/appointments', { params });
    return response.data;
  }
}

export const appointmentService = new AppointmentService();