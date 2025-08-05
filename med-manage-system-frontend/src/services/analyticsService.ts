import apiClient from './apiClient';
import {
  DailyFinancialReport,
  MonthlyFinancialReport,
  PatientDemographics,
  PatientVisitAnalytics,
  DoctorPerformanceReport,
  PopularService,
  WaitTimeAnalytics,
  CommonDiagnosis,
  PatientRetentionReport,
  PatientSatisfactionScore,
  RevenueByService,
  PaymentMethodSummary,
  DashboardStats,
  DateRange
} from '../types/analytics';

class AnalyticsService {
  // 财务分析相关API
  async getDailyFinancialReport(date: string): Promise<DailyFinancialReport> {
    const response = await apiClient.get(`/analytics/financial/daily?date=${date}`);
    return response.data;
  }

  async getMonthlyFinancialReport(year: number, month: number): Promise<MonthlyFinancialReport> {
    const response = await apiClient.get(`/analytics/financial/monthly?year=${year}&month=${month}`);
    return response.data;
  }

  async getTodayFinancialReport(): Promise<DailyFinancialReport> {
    const response = await apiClient.get('/analytics/financial/today');
    return response.data;
  }

  async getCurrentMonthFinancialReport(): Promise<MonthlyFinancialReport> {
    const response = await apiClient.get('/analytics/financial/current-month');
    return response.data;
  }

  async getRevenueByService(startDate: string, endDate: string): Promise<RevenueByService[]> {
    const response = await apiClient.get(`/analytics/financial/revenue-by-service?startDate=${startDate}&endDate=${endDate}`);
    return response.data;
  }

  async getPaymentMethodSummary(startDate: string, endDate: string): Promise<PaymentMethodSummary[]> {
    const response = await apiClient.get(`/analytics/financial/payment-method-summary?startDate=${startDate}&endDate=${endDate}`);
    return response.data;
  }

  // 患者分析相关API
  async getPatientDemographics(): Promise<PatientDemographics> {
    const response = await apiClient.get('/analytics/patient/demographics');
    return response.data;
  }

  async getCommonDiagnoses(startDate: string, endDate: string): Promise<CommonDiagnosis[]> {
    const response = await apiClient.get(`/analytics/patient/common-diagnoses?startDate=${startDate}&endDate=${endDate}`);
    return response.data;
  }

  async getPatientRetentionReport(): Promise<PatientRetentionReport> {
    const response = await apiClient.get('/analytics/patient/retention-report');
    return response.data;
  }

  async getPatientSatisfactionScores(): Promise<PatientSatisfactionScore[]> {
    const response = await apiClient.get('/analytics/patient/satisfaction-scores');
    return response.data;
  }

  // 运营分析相关API
  async getPatientVisitAnalytics(startDate: string, endDate: string): Promise<PatientVisitAnalytics> {
    const response = await apiClient.get(`/analytics/operational/patient-visits?startDate=${startDate}&endDate=${endDate}`);
    return response.data;
  }

  async getPopularServices(startDate: string, endDate: string): Promise<PopularService[]> {
    const response = await apiClient.get(`/analytics/operational/popular-services?startDate=${startDate}&endDate=${endDate}`);
    return response.data;
  }

  async getDoctorPerformanceReport(doctorId: number, startDate: string, endDate: string): Promise<DoctorPerformanceReport> {
    const response = await apiClient.get(`/analytics/operational/doctor-performance/${doctorId}?startDate=${startDate}&endDate=${endDate}`);
    return response.data;
  }

  async getAllDoctorsPerformanceReport(startDate: string, endDate: string): Promise<DoctorPerformanceReport[]> {
    const response = await apiClient.get(`/analytics/operational/doctor-performance?startDate=${startDate}&endDate=${endDate}`);
    return response.data;
  }

  async getWaitTimeAnalytics(startDate: string, endDate: string): Promise<WaitTimeAnalytics> {
    const response = await apiClient.get(`/analytics/operational/wait-time?startDate=${startDate}&endDate=${endDate}`);
    return response.data;
  }

  async getDepartmentVisitStatistics(startDate: string, endDate: string): Promise<PopularService[]> {
    const response = await apiClient.get(`/analytics/operational/department-visits?startDate=${startDate}&endDate=${endDate}`);
    return response.data;
  }

  // 仪表盘统计数据（组合多个API的结果）
  async getDashboardStats(): Promise<DashboardStats> {
    try {
      const [todayReport, monthlyReport, patientDemographics] = await Promise.all([
        this.getTodayFinancialReport().catch(() => ({
          totalRevenue: 0,
          totalPatients: 0,
          totalBills: 0,
          netRevenue: 0
        } as DailyFinancialReport)),
        this.getCurrentMonthFinancialReport().catch(() => ({
          totalRevenue: 0,
          totalPatients: 0,
          averageDailyRevenue: 0,
          averageBillAmount: 0,
          totalBills: 0
        } as MonthlyFinancialReport)),
        this.getPatientDemographics().catch(() => ({
          totalPatients: 0,
          averageAge: 0,
          genderDistribution: {},
          ageGroupDistribution: {},
          locationDistribution: {}
        } as PatientDemographics))
      ]);

      return {
        todayRevenue: todayReport.totalRevenue || 0,
        todayPatients: todayReport.totalPatients || 0,
        monthlyRevenue: monthlyReport.totalRevenue || 0,
        monthlyPatients: monthlyReport.totalPatients || 0,
        totalPatients: patientDemographics.totalPatients || 0,
        activeStaff: 0, // 需要从其他API获取
        pendingAppointments: 0, // 需要从预约API获取
        completedAppointments: 0 // 需要从预约API获取
      };
    } catch (error) {
      console.error('获取仪表盘统计数据失败:', error);
      // 返回默认值而不是抛出错误
      return {
        todayRevenue: 0,
        todayPatients: 0,
        monthlyRevenue: 0,
        monthlyPatients: 0,
        totalPatients: 0,
        activeStaff: 0,
        pendingAppointments: 0,
        completedAppointments: 0
      };
    }
  }

  // 获取指定时间范围的综合统计数据
  async getComprehensiveStats(dateRange: DateRange) {
    try {
      const [
        revenueByService,
        paymentMethodSummary,
        patientVisitAnalytics,
        popularServices,
        commonDiagnoses
      ] = await Promise.all([
        this.getRevenueByService(dateRange.startDate, dateRange.endDate),
        this.getPaymentMethodSummary(dateRange.startDate, dateRange.endDate),
        this.getPatientVisitAnalytics(dateRange.startDate, dateRange.endDate),
        this.getPopularServices(dateRange.startDate, dateRange.endDate),
        this.getCommonDiagnoses(dateRange.startDate, dateRange.endDate)
      ]);

      return {
        revenueByService,
        paymentMethodSummary,
        patientVisitAnalytics,
        popularServices,
        commonDiagnoses
      };
    } catch (error) {
      console.error('获取综合统计数据失败:', error);
      throw error;
    }
  }
}

export default new AnalyticsService();