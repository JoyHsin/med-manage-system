import { apiClient } from './apiClient';
import type { 
  Staff, 
  CreateStaffRequest, 
  UpdateStaffRequest, 
  Schedule, 
  CreateScheduleRequest,
  StaffStatistics 
} from '../types/staff';

export const staffService = {
  // 员工管理
  getAllStaff: () => apiClient.get<Staff[]>('/staff'),
  
  getStaffById: (staffId: number) => apiClient.get<Staff>(`/staff/${staffId}`),
  
  getStaffByNumber: (staffNumber: string) => apiClient.get<Staff>(`/staff/number/${staffNumber}`),
  
  getStaffByIdCard: (idCard: string) => apiClient.get<Staff>(`/staff/idcard/${idCard}`),
  
  getStaffByUserId: (userId: number) => apiClient.get<Staff>(`/staff/user/${userId}`),
  
  getStaffByPosition: (position: string) => apiClient.get<Staff[]>(`/staff/position/${position}`),
  
  getStaffByDepartment: (department: string) => apiClient.get<Staff[]>(`/staff/department/${department}`),
  
  getStaffByWorkStatus: (workStatus: string) => apiClient.get<Staff[]>(`/staff/status/${workStatus}`),
  
  getActiveStaff: () => apiClient.get<Staff[]>('/staff/active'),
  
  getSchedulableStaff: () => apiClient.get<Staff[]>('/staff/schedulable'),
  
  searchStaff: (keyword: string) => apiClient.get<Staff[]>(`/staff/search?keyword=${keyword}`),
  
  getStaffWithExpiringLicense: (days: number = 30) => 
    apiClient.get<Staff[]>(`/staff/license-expiring?days=${days}`),
  
  createStaff: (data: CreateStaffRequest) => apiClient.post<Staff>('/staff', data),
  
  updateStaff: (staffId: number, data: UpdateStaffRequest) => 
    apiClient.put<Staff>(`/staff/${staffId}`, data),
  
  deleteStaff: (staffId: number) => apiClient.delete(`/staff/${staffId}`),
  
  updateStaffWorkStatus: (staffId: number, workStatus: string) => 
    apiClient.put(`/staff/${staffId}/status`, { workStatus }),
  
  resignStaff: (staffId: number, resignationDate?: string) => 
    apiClient.post(`/staff/${staffId}/resign`, resignationDate ? { resignationDate } : {}),
  
  reinstateStaff: (staffId: number) => apiClient.post(`/staff/${staffId}/reinstate`),
  
  updateStaffSchedulableStatus: (staffId: number, isSchedulable: boolean) => 
    apiClient.put(`/staff/${staffId}/schedulable`, { isSchedulable }),

  // 排班管理
  createSchedule: (staffId: number, data: CreateScheduleRequest) => 
    apiClient.post<Schedule>(`/staff/${staffId}/schedules`, data),
  
  updateSchedule: (scheduleId: number, data: CreateScheduleRequest) => 
    apiClient.put<Schedule>(`/staff/schedules/${scheduleId}`, data),
  
  deleteSchedule: (scheduleId: number) => apiClient.delete(`/staff/schedules/${scheduleId}`),
  
  getStaffSchedules: (staffId: number) => apiClient.get<Schedule[]>(`/staff/${staffId}/schedules`),
  
  getStaffSchedulesByDateRange: (staffId: number, startDate: string, endDate: string) => 
    apiClient.get<Schedule[]>(`/staff/${staffId}/schedules/range?startDate=${startDate}&endDate=${endDate}`),
  
  getSchedulesByDate: (date: string) => apiClient.get<Schedule[]>(`/staff/schedules/date/${date}`),
  
  getTodaySchedules: () => apiClient.get<Schedule[]>('/staff/schedules/today'),
  
  getWeeklySchedules: () => apiClient.get<Schedule[]>('/staff/schedules/weekly'),
  
  getMonthlySchedules: () => apiClient.get<Schedule[]>('/staff/schedules/monthly'),
  
  batchCreateSchedules: (schedules: CreateScheduleRequest[]) => 
    apiClient.post<Schedule[]>('/staff/schedules/batch', schedules),

  // 统计信息
  getStaffStatistics: () => apiClient.get<StaffStatistics>('/staff/statistics'),
};