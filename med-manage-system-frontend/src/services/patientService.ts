import { apiClient } from './apiClient';

export interface Patient {
  id: number;
  patientNumber: string;
  name: string;
  phone?: string;
  idCard?: string;
  birthDate?: string;
  age?: number;
  gender: string;
  address?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelation?: string;
  bloodType?: string;
  maritalStatus?: string;
  occupation?: string;
  ethnicity?: string;
  insuranceType?: string;
  insuranceNumber?: string;
  remarks?: string;
  isVip: boolean;
  status: string;
  firstVisitTime?: string;
  lastVisitTime?: string;
  visitCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePatientRequest {
  name: string;
  phone?: string;
  idCard?: string;
  birthDate?: string;
  gender: string;
  address?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelation?: string;
  bloodType?: string;
  maritalStatus?: string;
  occupation?: string;
  ethnicity?: string;
  insuranceType?: string;
  insuranceNumber?: string;
  remarks?: string;
  isVip?: boolean;
}

export interface UpdatePatientRequest {
  name?: string;
  phone?: string;
  idCard?: string;
  birthDate?: string;
  gender?: string;
  address?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelation?: string;
  bloodType?: string;
  maritalStatus?: string;
  occupation?: string;
  ethnicity?: string;
  insuranceType?: string;
  insuranceNumber?: string;
  remarks?: string;
  isVip?: boolean;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message?: string;
  data?: T;
  total?: number;
  timestamp: number;
}

class PatientService {
  async getAllPatients(): Promise<ApiResponse<Patient[]>> {
    const response = await apiClient.get<Patient[]>('/patients');
    // 后端直接返回数组，需要包装成ApiResponse格式
    return {
      success: true,
      data: response.data,
      message: '获取患者列表成功'
    };
  }

  async getPatientById(patientId: number): Promise<ApiResponse<Patient>> {
    const response = await apiClient.get<ApiResponse<Patient>>(`/patients/${patientId}`);
    return response.data;
  }

  async getPatientByNumber(patientNumber: string): Promise<ApiResponse<Patient>> {
    const response = await apiClient.get<ApiResponse<Patient>>(`/patients/number/${patientNumber}`);
    return response.data;
  }

  async getPatientByIdCard(idCard: string): Promise<ApiResponse<Patient>> {
    const response = await apiClient.get<ApiResponse<Patient>>(`/patients/idcard/${idCard}`);
    return response.data;
  }

  async createPatient(patientData: CreatePatientRequest): Promise<ApiResponse<Patient>> {
    const response = await apiClient.post<Patient>('/patients', patientData);
    // 后端直接返回患者对象，需要包装成ApiResponse格式
    return {
      success: true,
      data: response.data,
      message: '患者创建成功'
    };
  }

  async updatePatient(patientId: number, patientData: UpdatePatientRequest): Promise<ApiResponse<Patient>> {
    const response = await apiClient.put<ApiResponse<Patient>>(`/patients/${patientId}`, patientData);
    return response.data;
  }

  async deletePatient(patientId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.delete<ApiResponse<any>>(`/patients/${patientId}`);
    return response.data;
  }

  async getPatientsByStatus(status: string): Promise<ApiResponse<Patient[]>> {
    const response = await apiClient.get<ApiResponse<Patient[]>>(`/patients/status/${status}`);
    return response.data;
  }

  async getVipPatients(): Promise<ApiResponse<Patient[]>> {
    const response = await apiClient.get<ApiResponse<Patient[]>>('/patients/vip');
    return response.data;
  }

  async getPatientsByGender(gender: string): Promise<ApiResponse<Patient[]>> {
    const response = await apiClient.get<ApiResponse<Patient[]>>(`/patients/gender/${gender}`);
    return response.data;
  }

  async getPatientsByAgeRange(minAge: number, maxAge: number): Promise<ApiResponse<Patient[]>> {
    const response = await apiClient.get<ApiResponse<Patient[]>>('/patients/age', {
      params: { minAge, maxAge }
    });
    return response.data;
  }

  async searchPatients(keyword: string): Promise<ApiResponse<Patient[]>> {
    const response = await apiClient.get<ApiResponse<Patient[]>>('/patients/search', {
      params: { keyword }
    });
    return response.data;
  }

  async setPatientAsVip(patientId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.put<ApiResponse<any>>(`/patients/${patientId}/vip`);
    return response.data;
  }

  async removePatientVipStatus(patientId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.delete<ApiResponse<any>>(`/patients/${patientId}/vip`);
    return response.data;
  }

  async updatePatientStatus(patientId: number, status: string): Promise<ApiResponse<any>> {
    const response = await apiClient.put<ApiResponse<any>>(`/patients/${patientId}/status`, { status });
    return response.data;
  }

  async recordPatientVisit(patientId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/patients/${patientId}/visit`);
    return response.data;
  }

  async getPatientStatistics(): Promise<ApiResponse<{
    totalPatients: number;
    normalPatients: number;
    vipPatients: number;
  }>> {
    const response = await apiClient.get<ApiResponse<{
      totalPatients: number;
      normalPatients: number;
      vipPatients: number;
    }>>('/patients/statistics');
    return response.data;
  }
}

export const patientService = new PatientService();