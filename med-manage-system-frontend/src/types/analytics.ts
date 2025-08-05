// 数据统计相关类型定义

export interface DailyFinancialReport {
  reportDate: string;
  totalRevenue: number;
  totalRegistrationFees: number;
  totalMedicalFees: number;
  totalMedicineFees: number;
  totalRefunds: number;
  netRevenue: number;
  totalPatients: number;
  totalBills: number;
  revenueByServices: RevenueByService[];
  paymentMethodSummaries: PaymentMethodSummary[];
}

export interface MonthlyFinancialReport {
  reportMonth: string;
  totalRevenue: number;
  totalRegistrationFees: number;
  totalMedicalFees: number;
  totalMedicineFees: number;
  totalRefunds: number;
  netRevenue: number;
  totalPatients: number;
  totalBills: number;
  averageDailyRevenue: number;
  averageBillAmount: number;
  dailySummaries: DailyFinancialSummary[];
  revenueByServices: RevenueByService[];
  paymentMethodSummaries: PaymentMethodSummary[];
}

export interface DailyFinancialSummary {
  date: string;
  revenue: number;
  patients: number;
  bills: number;
}

export interface RevenueByService {
  serviceType: string;
  serviceName: string;
  revenue: number;
  count: number;
  averageAmount: number;
  percentage: number;
}

export interface PaymentMethodSummary {
  paymentMethod: string;
  totalAmount: number;
  transactionCount: number;
  averageAmount: number;
  percentage: number;
}

export interface PatientDemographics {
  totalPatients: number;
  genderDistribution: Record<string, number>;
  ageGroupDistribution: Record<string, number>;
  averageAge: number;
  locationDistribution: Record<string, number>;
}

export interface PatientVisitAnalytics {
  date: string;
  totalVisits: number;
  newPatients: number;
  returningPatients: number;
  averageVisitsPerDay: number;
  peakHourVisits: number;
  peakHour: string;
}

export interface DoctorPerformanceReport {
  doctorId: number;
  doctorName: string;
  department: string;
  reportDate: string;
  totalPatients: number;
  completedConsultations: number;
  averageConsultationTime: number;
  patientSatisfactionScore: number;
  prescriptionsIssued: number;
  revenueGenerated: number;
}

export interface PopularService {
  serviceName: string;
  serviceCode: string;
  visitCount: number;
  percentage: number;
  department: string;
}

export interface WaitTimeAnalytics {
  averageWaitTime: number;
  medianWaitTime: number;
  maxWaitTime: number;
  minWaitTime: number;
  waitTimeByHour: Record<string, number>;
}

export interface CommonDiagnosis {
  diagnosisCode: string;
  diagnosisName: string;
  count: number;
  percentage: number;
}

export interface PatientRetentionReport {
  totalPatients: number;
  newPatients: number;
  returningPatients: number;
  retentionRate: number;
  averageVisitsPerPatient: number;
}

export interface PatientSatisfactionScore {
  department: string;
  averageScore: number;
  totalResponses: number;
  scoreDistribution: Record<string, number>;
}

// 统计仪表盘数据接口
export interface DashboardStats {
  todayRevenue: number;
  todayPatients: number;
  monthlyRevenue: number;
  monthlyPatients: number;
  totalPatients: number;
  activeStaff: number;
  pendingAppointments: number;
  completedAppointments: number;
}

// 图表数据接口
export interface ChartData {
  name: string;
  value: number;
  [key: string]: any;
}

// 时间范围选择器
export interface DateRange {
  startDate: string;
  endDate: string;
}

// API响应包装器
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}