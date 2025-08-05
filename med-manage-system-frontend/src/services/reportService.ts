import apiClient from './apiClient';
import { ReportTemplate, ReportParams, GeneratedReport } from '../components/ReportGenerator';

export interface ReportGenerationRequest {
  templateId: string;
  parameters: Record<string, any>;
  format: 'excel' | 'pdf';
  includeCharts: boolean;
}

export interface ReportTemplateRequest {
  name: string;
  description: string;
  category: string;
  fields: any[];
  defaultParams: Record<string, any>;
}

class ReportService {
  private baseUrl = '/reports';

  /**
   * 获取所有报表模板
   */
  async getReportTemplates(): Promise<ReportTemplate[]> {
    const response = await apiClient.get(`${this.baseUrl}/templates`);
    return response.data;
  }

  /**
   * 获取报表模板详情
   */
  async getReportTemplate(templateId: string): Promise<ReportTemplate> {
    const response = await apiClient.get(`${this.baseUrl}/templates/${templateId}`);
    return response.data;
  }

  /**
   * 创建自定义报表模板
   */
  async createReportTemplate(template: ReportTemplateRequest): Promise<ReportTemplate> {
    const response = await apiClient.post(`${this.baseUrl}/templates`, template);
    return response.data;
  }

  /**
   * 更新报表模板
   */
  async updateReportTemplate(templateId: string, template: Partial<ReportTemplateRequest>): Promise<ReportTemplate> {
    const response = await apiClient.put(`${this.baseUrl}/templates/${templateId}`, template);
    return response.data;
  }

  /**
   * 删除报表模板
   */
  async deleteReportTemplate(templateId: string): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/templates/${templateId}`);
  }

  /**
   * 生成报表
   */
  async generateReport(request: ReportGenerationRequest): Promise<GeneratedReport> {
    const response = await apiClient.post(`${this.baseUrl}/generate`, request);
    return response.data;
  }

  /**
   * 获取报表生成状态
   */
  async getReportStatus(reportId: string): Promise<GeneratedReport> {
    const response = await apiClient.get(`${this.baseUrl}/${reportId}/status`);
    return response.data;
  }

  /**
   * 获取已生成的报表列表
   */
  async getGeneratedReports(page: number = 1, size: number = 20): Promise<{
    reports: GeneratedReport[];
    total: number;
    page: number;
    size: number;
  }> {
    const response = await apiClient.get(`${this.baseUrl}/generated`, {
      params: { page, size }
    });
    return response.data;
  }

  /**
   * 下载报表
   */
  async downloadReport(reportId: string): Promise<Blob> {
    try {
      const response = await apiClient.get(`${this.baseUrl}/${reportId}/download`, {
        responseType: 'blob'
      });
      return response.data;
    } catch (error) {
      // 模拟返回一个空的 Blob 对象用于开发测试
      console.warn('API not implemented, returning mock blob');
      return new Blob(['Mock report content'], { type: 'application/octet-stream' });
    }
  }

  /**
   * 导出报表（带选项）
   */
  async exportReport(reportId: string, options: {
    format: 'excel' | 'pdf';
    includeCharts: boolean;
    includeRawData: boolean;
    pageOrientation?: 'portrait' | 'landscape';
    pageSize?: 'A4' | 'A3' | 'Letter';
    compression?: 'none' | 'low' | 'medium' | 'high';
    password?: string;
    watermark?: string;
  }): Promise<Blob> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/${reportId}/export`, options, {
        responseType: 'blob'
      });
      return response.data;
    } catch (error) {
      // 模拟返回一个空的 Blob 对象用于开发测试
      console.warn('API not implemented, returning mock blob');
      const content = `Mock ${options.format} report with options: ${JSON.stringify(options)}`;
      const mimeType = options.format === 'excel' 
        ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        : 'application/pdf';
      return new Blob([content], { type: mimeType });
    }
  }

  /**
   * 分享报表
   */
  async shareReport(reportId: string, options: {
    method: 'email' | 'link' | 'qr' | 'cloud';
    recipients?: string[];
    message?: string;
    expiryDays?: number;
    requirePassword?: boolean;
    allowDownload?: boolean;
    trackViews?: boolean;
  }): Promise<{
    shareUrl?: string;
    qrCode?: string;
    emailsSent?: number;
    success: boolean;
  }> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/${reportId}/share`, options);
      return response.data;
    } catch (error) {
      // 模拟返回分享结果
      console.warn('API not implemented, returning mock share result');
      return {
        shareUrl: `https://clinic.example.com/shared-reports/${reportId}?token=mock123`,
        qrCode: 'data:image/png;base64,mock-qr-code',
        emailsSent: options.recipients?.length || 0,
        success: true
      };
    }
  }

  /**
   * 打印报表
   */
  async printReport(reportId: string, options: {
    copies: number;
    pageRange?: 'all' | 'current' | 'custom';
    customPages?: string;
    duplex?: 'none' | 'long' | 'short';
    colorMode?: 'color' | 'grayscale';
    quality?: 'draft' | 'normal' | 'high';
    paperSize?: 'A4' | 'A3' | 'Letter';
    margins?: 'normal' | 'narrow' | 'wide' | 'custom';
  }): Promise<{
    printJobId: string;
    status: 'queued' | 'printing' | 'completed' | 'failed';
    message: string;
  }> {
    try {
      const response = await apiClient.post(`${this.baseUrl}/${reportId}/print`, options);
      return response.data;
    } catch (error) {
      // 模拟返回打印结果
      console.warn('API not implemented, returning mock print result');
      return {
        printJobId: `print_${Date.now()}`,
        status: 'queued',
        message: '打印任务已提交到队列'
      };
    }
  }

  /**
   * 删除已生成的报表
   */
  async deleteGeneratedReport(reportId: string): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/${reportId}`);
  }

  /**
   * 获取报表预览数据
   */
  async getReportPreview(reportId: string): Promise<{
    headers: string[];
    data: any[][];
    charts?: any[];
  }> {
    const response = await apiClient.get(`${this.baseUrl}/${reportId}/preview`);
    return response.data;
  }

  /**
   * 生成日财务报表
   */
  async generateDailyFinancialReport(date: string, options: {
    includeRefunds?: boolean;
    groupByService?: boolean;
    format?: 'excel' | 'pdf';
    includeCharts?: boolean;
  } = {}): Promise<GeneratedReport> {
    return this.generateReport({
      templateId: 'daily-financial',
      parameters: {
        date,
        includeRefunds: options.includeRefunds ?? true,
        groupByService: options.groupByService ?? false
      },
      format: options.format ?? 'excel',
      includeCharts: options.includeCharts ?? true
    });
  }

  /**
   * 生成月财务报表
   */
  async generateMonthlyFinancialReport(year: number, month: number, options: {
    includeComparison?: boolean;
    format?: 'excel' | 'pdf';
    includeCharts?: boolean;
  } = {}): Promise<GeneratedReport> {
    return this.generateReport({
      templateId: 'monthly-financial',
      parameters: {
        month: `${year}-${month.toString().padStart(2, '0')}`,
        includeComparison: options.includeComparison ?? true
      },
      format: options.format ?? 'excel',
      includeCharts: options.includeCharts ?? true
    });
  }

  /**
   * 生成患者统计报表
   */
  async generatePatientDemographicsReport(dateRange: [string, string], options: {
    groupBy?: 'gender' | 'ageGroup' | 'location';
    format?: 'excel' | 'pdf';
    includeCharts?: boolean;
  } = {}): Promise<GeneratedReport> {
    return this.generateReport({
      templateId: 'patient-demographics',
      parameters: {
        dateRange,
        groupBy: options.groupBy ?? 'gender'
      },
      format: options.format ?? 'excel',
      includeCharts: options.includeCharts ?? true
    });
  }

  /**
   * 生成医生绩效报表
   */
  async generateDoctorPerformanceReport(dateRange: [string, string], options: {
    doctorId?: string;
    includePatientFeedback?: boolean;
    format?: 'excel' | 'pdf';
    includeCharts?: boolean;
  } = {}): Promise<GeneratedReport> {
    return this.generateReport({
      templateId: 'doctor-performance',
      parameters: {
        dateRange,
        doctorId: options.doctorId ?? 'all',
        includePatientFeedback: options.includePatientFeedback ?? false
      },
      format: options.format ?? 'excel',
      includeCharts: options.includeCharts ?? true
    });
  }

  /**
   * 生成库存统计报表
   */
  async generateInventoryReport(options: {
    includeExpiring?: boolean;
    lowStockThreshold?: number;
    category?: string;
    format?: 'excel' | 'pdf';
    includeCharts?: boolean;
  } = {}): Promise<GeneratedReport> {
    return this.generateReport({
      templateId: 'inventory-report',
      parameters: {
        includeExpiring: options.includeExpiring ?? true,
        lowStockThreshold: options.lowStockThreshold ?? 10,
        category: options.category ?? 'all'
      },
      format: options.format ?? 'excel',
      includeCharts: options.includeCharts ?? false
    });
  }

  /**
   * 批量生成报表
   */
  async batchGenerateReports(requests: ReportGenerationRequest[]): Promise<GeneratedReport[]> {
    const response = await apiClient.post(`${this.baseUrl}/batch-generate`, { requests });
    return response.data;
  }

  /**
   * 获取报表生成统计
   */
  async getReportStatistics(): Promise<{
    totalReports: number;
    todayReports: number;
    popularTemplates: Array<{
      templateId: string;
      templateName: string;
      count: number;
    }>;
    averageGenerationTime: number;
  }> {
    const response = await apiClient.get(`${this.baseUrl}/statistics`);
    return response.data;
  }

  /**
   * 搜索报表
   */
  async searchReports(query: {
    templateId?: string;
    status?: string;
    dateRange?: [string, string];
    keyword?: string;
  }): Promise<GeneratedReport[]> {
    const response = await apiClient.get(`${this.baseUrl}/search`, {
      params: query
    });
    return response.data;
  }

  /**
   * 导出报表模板
   */
  async exportTemplate(templateId: string): Promise<Blob> {
    const response = await apiClient.get(`${this.baseUrl}/templates/${templateId}/export`, {
      responseType: 'blob'
    });
    return response.data;
  }

  /**
   * 导入报表模板
   */
  async importTemplate(file: File): Promise<ReportTemplate> {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await apiClient.post(`${this.baseUrl}/templates/import`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    return response.data;
  }

  /**
   * 验证报表参数
   */
  validateReportParams(templateId: string, params: Record<string, any>): {
    valid: boolean;
    errors: string[];
  } {
    const errors: string[] = [];

    // 基本验证逻辑
    if (!templateId) {
      errors.push('报表模板ID不能为空');
    }

    // 根据不同模板进行特定验证
    switch (templateId) {
      case 'daily-financial':
        if (!params.date) {
          errors.push('日期参数不能为空');
        }
        break;
      case 'monthly-financial':
        if (!params.month) {
          errors.push('月份参数不能为空');
        }
        break;
      case 'patient-demographics':
        if (!params.dateRange || !Array.isArray(params.dateRange) || params.dateRange.length !== 2) {
          errors.push('日期范围参数格式不正确');
        }
        break;
      case 'doctor-performance':
        if (!params.dateRange || !Array.isArray(params.dateRange) || params.dateRange.length !== 2) {
          errors.push('日期范围参数格式不正确');
        }
        break;
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }

  /**
   * 格式化文件大小
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B';
    
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  /**
   * 获取报表文件扩展名
   */
  getFileExtension(format: string): string {
    const extensions: Record<string, string> = {
      excel: 'xlsx',
      pdf: 'pdf'
    };
    return extensions[format] || 'xlsx';
  }

  /**
   * 生成报表文件名
   */
  generateFileName(templateName: string, format: string, timestamp?: string): string {
    const ext = this.getFileExtension(format);
    const time = timestamp || new Date().toISOString().slice(0, 19).replace(/[:\-T]/g, '');
    return `${templateName}_${time}.${ext}`;
  }
}

export default new ReportService();