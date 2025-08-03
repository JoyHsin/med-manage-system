import { apiClient } from './apiClient';
import {
  MedicalOrder,
  CreateMedicalOrderRequest,
  ExecuteMedicalOrderRequest,
  MedicalOrderTemplate,
  CreateOrderTemplateRequest
} from '../types/medicalOrder';

export class MedicalOrderService {
  private baseUrl = '/api/medical-orders';
  private templateUrl = '/api/medical-order-templates';

  /**
   * 创建医嘱
   */
  async createMedicalOrder(request: CreateMedicalOrderRequest): Promise<MedicalOrder> {
    const response = await apiClient.post(this.baseUrl, request);
    return response.data;
  }

  /**
   * 获取医嘱详情
   */
  async getMedicalOrder(id: number): Promise<MedicalOrder> {
    const response = await apiClient.get(`${this.baseUrl}/${id}`);
    return response.data;
  }

  /**
   * 更新医嘱
   */
  async updateMedicalOrder(id: number, request: CreateMedicalOrderRequest): Promise<MedicalOrder> {
    const response = await apiClient.put(`${this.baseUrl}/${id}`, request);
    return response.data;
  }

  /**
   * 删除医嘱
   */
  async deleteMedicalOrder(id: number): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/${id}`);
  }

  /**
   * 获取患者待执行医嘱
   */
  async getPendingOrders(patientId: number): Promise<MedicalOrder[]> {
    const response = await apiClient.get(`${this.baseUrl}/patient/${patientId}/pending`);
    return response.data;
  }

  /**
   * 获取患者已执行医嘱
   */
  async getExecutedOrders(patientId: number): Promise<MedicalOrder[]> {
    const response = await apiClient.get(`${this.baseUrl}/patient/${patientId}/executed`);
    return response.data;
  }

  /**
   * 获取医生开具的医嘱列表
   */
  async getDoctorOrders(
    doctorId: number,
    startDate?: string,
    endDate?: string,
    status?: string
  ): Promise<MedicalOrder[]> {
    const params: any = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    if (status) params.status = status;

    const response = await apiClient.get(`${this.baseUrl}/doctor/${doctorId}`, { params });
    return response.data;
  }

  /**
   * 搜索医嘱
   */
  async searchMedicalOrders(
    keyword?: string,
    patientId?: number,
    doctorId?: number,
    orderType?: string,
    status?: string,
    priority?: string,
    startDate?: string,
    endDate?: string
  ): Promise<MedicalOrder[]> {
    const params: any = {};
    if (keyword) params.keyword = keyword;
    if (patientId) params.patientId = patientId;
    if (doctorId) params.doctorId = doctorId;
    if (orderType) params.orderType = orderType;
    if (status) params.status = status;
    if (priority) params.priority = priority;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/search`, { params });
    return response.data;
  }

  /**
   * 执行医嘱
   */
  async executeOrder(id: number, request: ExecuteMedicalOrderRequest): Promise<MedicalOrder> {
    const response = await apiClient.post(`${this.baseUrl}/${id}/execute`, request);
    return response.data;
  }

  /**
   * 暂缓执行医嘱
   */
  async postponeOrder(id: number, reason: string): Promise<MedicalOrder> {
    const response = await apiClient.post(`${this.baseUrl}/${id}/postpone`, { reason });
    return response.data;
  }

  /**
   * 取消医嘱
   */
  async cancelOrder(id: number, reason: string): Promise<MedicalOrder> {
    const response = await apiClient.post(`${this.baseUrl}/${id}/cancel`, { reason });
    return response.data;
  }

  /**
   * 批量创建医嘱
   */
  async batchCreateOrders(requests: CreateMedicalOrderRequest[]): Promise<MedicalOrder[]> {
    const response = await apiClient.post(`${this.baseUrl}/batch`, { orders: requests });
    return response.data;
  }

  /**
   * 批量执行医嘱
   */
  async batchExecuteOrders(
    orderIds: number[],
    executionNotes?: string
  ): Promise<MedicalOrder[]> {
    const response = await apiClient.post(`${this.baseUrl}/batch-execute`, {
      orderIds,
      executionNotes
    });
    return response.data;
  }

  /**
   * 复制医嘱
   */
  async copyOrder(id: number, patientId?: number): Promise<MedicalOrder> {
    const response = await apiClient.post(`${this.baseUrl}/${id}/copy`, { patientId });
    return response.data;
  }

  /**
   * 获取医嘱统计
   */
  async getOrderStats(
    doctorId?: number,
    patientId?: number,
    startDate?: string,
    endDate?: string
  ): Promise<{
    totalOrders: number;
    pendingOrders: number;
    executedOrders: number;
    postponedOrders: number;
    cancelledOrders: number;
    urgentOrders: number;
    ordersByType: Record<string, number>;
  }> {
    const params: any = {};
    if (doctorId) params.doctorId = doctorId;
    if (patientId) params.patientId = patientId;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/stats`, { params });
    return response.data;
  }

  /**
   * 获取医嘱模板列表
   */
  async getOrderTemplates(doctorId: number): Promise<MedicalOrderTemplate[]> {
    const response = await apiClient.get(`${this.templateUrl}/doctor/${doctorId}`);
    return response.data;
  }

  /**
   * 创建医嘱模板
   */
  async createOrderTemplate(request: CreateOrderTemplateRequest): Promise<MedicalOrderTemplate> {
    const response = await apiClient.post(this.templateUrl, request);
    return response.data;
  }

  /**
   * 更新医嘱模板
   */
  async updateOrderTemplate(
    id: number,
    request: CreateOrderTemplateRequest
  ): Promise<MedicalOrderTemplate> {
    const response = await apiClient.put(`${this.templateUrl}/${id}`, request);
    return response.data;
  }

  /**
   * 删除医嘱模板
   */
  async deleteOrderTemplate(id: number): Promise<void> {
    await apiClient.delete(`${this.templateUrl}/${id}`);
  }

  /**
   * 从模板创建医嘱
   */
  async createFromTemplate(templateId: number, patientId: number): Promise<MedicalOrder> {
    const response = await apiClient.post(`${this.templateUrl}/${templateId}/create-order`, {
      patientId
    });
    return response.data;
  }

  /**
   * 获取常用医嘱模板
   */
  async getCommonTemplates(orderType?: string): Promise<MedicalOrderTemplate[]> {
    const params: any = {};
    if (orderType) params.orderType = orderType;

    const response = await apiClient.get(`${this.templateUrl}/common`, { params });
    return response.data;
  }

  /**
   * 导出医嘱
   */
  async exportOrders(
    doctorId?: number,
    patientId?: number,
    startDate?: string,
    endDate?: string,
    format: 'excel' | 'pdf' = 'excel'
  ): Promise<Blob> {
    const params: any = { format };
    if (doctorId) params.doctorId = doctorId;
    if (patientId) params.patientId = patientId;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await apiClient.get(`${this.baseUrl}/export`, {
      params,
      responseType: 'blob'
    });
    return response.data;
  }

  /**
   * 验证医嘱数据
   */
  validateOrder(order: CreateMedicalOrderRequest): {
    valid: boolean;
    errors: string[];
  } {
    const errors: string[] = [];

    if (!order.patientId) {
      errors.push('患者ID不能为空');
    }

    if (!order.orderType) {
      errors.push('医嘱类型不能为空');
    }

    if (!order.content || order.content.trim().length === 0) {
      errors.push('医嘱内容不能为空');
    }

    if (order.content && order.content.length > 500) {
      errors.push('医嘱内容长度不能超过500个字符');
    }

    if (order.dosage && order.dosage.length > 100) {
      errors.push('剂量长度不能超过100个字符');
    }

    if (order.frequency && order.frequency.length > 100) {
      errors.push('频次长度不能超过100个字符');
    }

    if (order.route && order.route.length > 100) {
      errors.push('给药途径长度不能超过100个字符');
    }

    if (order.notes && order.notes.length > 500) {
      errors.push('备注长度不能超过500个字符');
    }

    if (order.unit && order.unit.length > 50) {
      errors.push('单位长度不能超过50个字符');
    }

    if (order.price && order.price < 0) {
      errors.push('价格不能为负数');
    }

    if (order.quantity && order.quantity <= 0) {
      errors.push('数量必须大于0');
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }

  /**
   * 生成医嘱编号
   */
  generateOrderNumber(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const timestamp = now.getTime().toString().slice(-6);
    return `MO${year}${month}${day}${timestamp}`;
  }

  /**
   * 检查医嘱冲突
   */
  async checkOrderConflicts(
    patientId: number,
    orderType: string,
    content: string
  ): Promise<{
    hasConflicts: boolean;
    conflicts: Array<{
      orderId: number;
      content: string;
      reason: string;
    }>;
  }> {
    const response = await apiClient.post(`${this.baseUrl}/check-conflicts`, {
      patientId,
      orderType,
      content
    });
    return response.data;
  }

  /**
   * 获取医嘱执行提醒
   */
  async getExecutionReminders(nurseId: number): Promise<Array<{
    orderId: number;
    patientName: string;
    content: string;
    scheduledTime: string;
    priority: string;
    overdue: boolean;
  }>> {
    const response = await apiClient.get(`${this.baseUrl}/execution-reminders/${nurseId}`);
    return response.data;
  }
}

export const medicalOrderService = new MedicalOrderService();