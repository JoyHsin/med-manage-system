export interface Staff {
  id: number;
  staffNumber: string;
  name: string;
  gender: string;
  phone?: string;
  email?: string;
  idCard?: string;
  birthDate?: string;
  address?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelation?: string;
  hireDate: string;
  position: string;
  department?: string;
  licenseNumber?: string;
  licenseExpiryDate?: string;
  workStatus: string;
  isSchedulable: boolean;
  userId?: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateStaffRequest {
  name: string;
  gender: string;
  phone?: string;
  email?: string;
  idCard?: string;
  birthDate?: string;
  address?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelation?: string;
  hireDate: string;
  position: string;
  department?: string;
  licenseNumber?: string;
  licenseExpiryDate?: string;
  workStatus?: string;
  isSchedulable?: boolean;
}

export interface UpdateStaffRequest extends CreateStaffRequest {}

export interface Schedule {
  id: number;
  staffId: number;
  scheduleDate: string;
  shiftType: string;
  startTime: string;
  endTime: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateScheduleRequest {
  scheduleDate: string;
  shiftType: string;
  startTime: string;
  endTime: string;
  status?: string;
}

export interface StaffStatistics {
  totalStaff: number;
  activeStaff: number;
  doctorCount: number;
  nurseCount: number;
}