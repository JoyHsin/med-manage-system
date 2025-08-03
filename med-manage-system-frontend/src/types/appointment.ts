export interface Appointment {
  id: number;
  patientId: number;
  doctorId: number;
  appointmentTime: string;
  appointmentType: string;
  status: string;
  department?: string;
  chiefComplaint?: string;
  notes?: string;
  source: string;
  priority: number;
  appointmentFee?: number;
  needReminder: boolean;
  reminderMinutes: number;
  confirmedAt?: string;
  arrivedAt?: string;
  startedAt?: string;
  completedAt?: string;
  cancelledAt?: string;
  cancelReason?: string;
  createdBy?: number;
  lastUpdatedBy?: number;
  createdAt: string;
  updatedAt: string;
  // 关联数据
  patientName?: string;
  doctorName?: string;
}

export interface CreateAppointmentRequest {
  patientId: number;
  doctorId: number;
  appointmentTime: string;
  appointmentType: string;
  department?: string;
  chiefComplaint?: string;
  notes?: string;
  source?: string;
  priority?: number;
  appointmentFee?: number;
  needReminder?: boolean;
  reminderMinutes?: number;
}

export interface UpdateAppointmentRequest {
  appointmentTime?: string;
  appointmentType?: string;
  department?: string;
  chiefComplaint?: string;
  notes?: string;
  priority?: number;
  appointmentFee?: number;
  needReminder?: boolean;
  reminderMinutes?: number;
}

export interface AvailableSlot {
  startTime: string;
  endTime: string;
  isAvailable: boolean;
  doctorId: number;
  doctorName: string;
  department: string;
}

export interface Registration {
  id: number;
  patientId: number;
  appointmentId?: number;
  registrationNumber: string;
  registrationDate: string;
  registrationTime: string;
  department: string;
  doctorId?: number;
  doctorName?: string;
  registrationType: string;
  status: string;
  queueNumber?: number;
  priority: number;
  registrationFee: number;
  consultationFee?: number;
  totalFee: number;
  paymentStatus: string;
  paymentMethod?: string;
  chiefComplaint?: string;
  presentIllness?: string;
  source: string;
  isFirstVisit: boolean;
  isEmergency: boolean;
  calledAt?: string;
  arrivedAt?: string;
  startedAt?: string;
  completedAt?: string;
  cancelledAt?: string;
  cancelReason?: string;
  remarks?: string;
  createdBy?: number;
  lastUpdatedBy?: number;
  createdAt: string;
  updatedAt: string;
  // 关联数据
  patientName?: string;
}

export interface CreateRegistrationRequest {
  patientId: number;
  appointmentId?: number;
  registrationDate: string;
  department: string;
  doctorId?: number;
  doctorName?: string;
  registrationType: string;
  priority?: number;
  registrationFee: number;
  consultationFee?: number;
  chiefComplaint?: string;
  presentIllness?: string;
  source?: string;
  isFirstVisit?: boolean;
  isEmergency?: boolean;
  remarks?: string;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message?: string;
  data?: T;
  total?: number;
  timestamp: number;
}

// 预约类型选项
export const APPOINTMENT_TYPES = [
  { value: '初诊', label: '初诊' },
  { value: '复诊', label: '复诊' },
  { value: '专家门诊', label: '专家门诊' },
  { value: '急诊', label: '急诊' },
  { value: '体检', label: '体检' },
  { value: '疫苗接种', label: '疫苗接种' },
  { value: '其他', label: '其他' }
];

// 预约状态选项
export const APPOINTMENT_STATUSES = [
  { value: '已预约', label: '已预约', color: 'blue' },
  { value: '已确认', label: '已确认', color: 'green' },
  { value: '已到达', label: '已到达', color: 'orange' },
  { value: '进行中', label: '进行中', color: 'purple' },
  { value: '已完成', label: '已完成', color: 'success' },
  { value: '已取消', label: '已取消', color: 'default' },
  { value: '未到', label: '未到', color: 'error' }
];

// 预约来源选项
export const APPOINTMENT_SOURCES = [
  { value: '现场', label: '现场' },
  { value: '电话', label: '电话' },
  { value: '网络', label: '网络' },
  { value: '微信', label: '微信' },
  { value: '其他', label: '其他' }
];

// 挂号类型选项
export const REGISTRATION_TYPES = [
  { value: '普通门诊', label: '普通门诊' },
  { value: '专家门诊', label: '专家门诊' },
  { value: '急诊', label: '急诊' },
  { value: '专科门诊', label: '专科门诊' },
  { value: '体检', label: '体检' },
  { value: '疫苗接种', label: '疫苗接种' },
  { value: '其他', label: '其他' }
];

// 挂号状态选项
export const REGISTRATION_STATUSES = [
  { value: '已挂号', label: '已挂号', color: 'blue' },
  { value: '已叫号', label: '已叫号', color: 'orange' },
  { value: '已到达', label: '已到达', color: 'green' },
  { value: '就诊中', label: '就诊中', color: 'purple' },
  { value: '已完成', label: '已完成', color: 'success' },
  { value: '已取消', label: '已取消', color: 'default' },
  { value: '未到', label: '未到', color: 'error' }
];

// 支付状态选项
export const PAYMENT_STATUSES = [
  { value: '未支付', label: '未支付', color: 'error' },
  { value: '已支付', label: '已支付', color: 'success' },
  { value: '部分支付', label: '部分支付', color: 'warning' },
  { value: '已退费', label: '已退费', color: 'default' }
];

// 支付方式选项
export const PAYMENT_METHODS = [
  { value: '现金', label: '现金' },
  { value: '银行卡', label: '银行卡' },
  { value: '支付宝', label: '支付宝' },
  { value: '微信', label: '微信' },
  { value: '医保', label: '医保' },
  { value: '其他', label: '其他' }
];

// 科室选项
export const DEPARTMENTS = [
  { value: '内科', label: '内科' },
  { value: '外科', label: '外科' },
  { value: '儿科', label: '儿科' },
  { value: '妇科', label: '妇科' },
  { value: '眼科', label: '眼科' },
  { value: '耳鼻喉科', label: '耳鼻喉科' },
  { value: '皮肤科', label: '皮肤科' },
  { value: '中医科', label: '中医科' },
  { value: '康复科', label: '康复科' },
  { value: '体检科', label: '体检科' }
];

// 优先级选项
export const PRIORITY_OPTIONS = [
  { value: 1, label: '最低', color: 'default' },
  { value: 2, label: '较低', color: 'blue' },
  { value: 3, label: '普通', color: 'green' },
  { value: 4, label: '较高', color: 'orange' },
  { value: 5, label: '最高', color: 'red' }
];