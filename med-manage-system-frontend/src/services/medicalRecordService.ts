import { apiClient } from './apiClient';
import {
  MedicalRecord,
  Diagnosis,
  Prescription,
  CreateMedicalRecordRequest,
  UpdateMedicalRecordRequest
} from '../types/medicalRecord';

export class MedicalRecordService {
  private baseUrl = '/medical-records';

  /**
   * 创建病历
   */
  async createMedicalRecord(request: CreateMedicalRecordRequest): Promise<MedicalRecord> {
    const response = await apiClient.post(this.baseUrl, request);
    return response.data;
  }

  /**
   * 更新病历
   */
  async updateMedicalRecord(recordId: number, request: UpdateMedicalRecordRequest): Promise<MedicalRecord> {
    const response = await apiClient.put(`${this.baseUrl}/${recordId}`, request);
    return response.data;
  }

  /**
   * 获取病历详情
   */
  async getMedicalRecord(recordId: number): Promise<MedicalRecord> {
    const response = await apiClient.get(`${this.baseUrl}/${recordId}`);
    return response.data;
  }

  /**
   * 获取患者病历列表
   */
  async getPatientMedicalRecords(patientId: number): Promise<MedicalRecord[]> {
    const response = await apiClient.get(`${this.baseUrl}/patient/${patientId}`);
    return response.data;
  }

  /**
   * 添加诊断
   */
  async addDiagnosis(recordId: number, diagnosis: Partial<Diagnosis>): Promise<Diagnosis> {
    const response = await apiClient.post(`${this.baseUrl}/${recordId}/diagnoses`, diagnosis);
    return response.data;
  }

  /**
   * 添加处方
   */
  async addPrescription(recordId: number, prescription: Partial<Prescription>): Promise<Prescription> {
    const response = await apiClient.post(`${this.baseUrl}/${recordId}/prescriptions`, prescription);
    return response.data;
  }

  /**
   * 提交审核
   */
  async submitForReview(recordId: number): Promise<void> {
    await apiClient.post(`${this.baseUrl}/${recordId}/submit-review`);
  }

  /**
   * 审核病历
   */
  async reviewMedicalRecord(
    recordId: number,
    reviewDoctorId: number,
    approved: boolean,
    comments?: string
  ): Promise<void> {
    await apiClient.post(`${this.baseUrl}/${recordId}/review`, {
      reviewDoctorId,
      approved,
      comments
    });
  }

  /**
   * 删除病历
   */
  async deleteMedicalRecord(recordId: number): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/${recordId}`);
  }

  /**
   * 生成病历编号
   */
  generateRecordNumber(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const timestamp = now.getTime().toString().slice(-6);
    return `MR${year}${month}${day}${timestamp}`;
  }

  /**
   * 验证病历完整性
   */
  validateMedicalRecord(record: Partial<MedicalRecord>): {
    isValid: boolean;
    errors: string[];
    warnings: string[];
  } {
    const errors: string[] = [];
    const warnings: string[] = [];

    // 必填字段检查
    if (!record.chiefComplaint?.trim()) {
      errors.push('主诉不能为空');
    }
    if (!record.presentIllness?.trim()) {
      errors.push('现病史不能为空');
    }
    if (!record.physicalExamination?.trim()) {
      errors.push('体格检查不能为空');
    }
    if (!record.preliminaryDiagnosis?.trim()) {
      errors.push('初步诊断不能为空');
    }
    if (!record.treatmentPlan?.trim()) {
      errors.push('治疗方案不能为空');
    }

    // 警告检查
    if (!record.pastHistory?.trim()) {
      warnings.push('建议填写既往史');
    }
    if (!record.finalDiagnosis?.trim() && record.preliminaryDiagnosis?.trim()) {
      warnings.push('建议确认最终诊断');
    }
    if (!record.followUpAdvice?.trim()) {
      warnings.push('建议填写随访建议');
    }

    return {
      isValid: errors.length === 0,
      errors,
      warnings
    };
  }

  /**
   * 搜索ICD诊断编码
   */
  async searchICDCodes(keyword: string): Promise<Array<{ code: string; name: string }>> {
    // 这里应该调用ICD编码搜索API，暂时返回模拟数据
    const mockData = [
      { code: 'J00', name: '急性鼻咽炎[感冒]' },
      { code: 'J06.9', name: '急性上呼吸道感染，未特指' },
      { code: 'K59.1', name: '功能性腹泻' },
      { code: 'M79.3', name: '脂膜炎，未特指' },
      { code: 'R50.9', name: '发热，未特指' },
      { code: 'R51', name: '头痛' },
      { code: 'R06.02', name: '气短' },
      { code: 'R05', name: '咳嗽' }
    ];

    return mockData.filter(item =>
      item.name.toLowerCase().includes(keyword.toLowerCase()) ||
      item.code.toLowerCase().includes(keyword.toLowerCase())
    );
  }

  /**
   * 获取病历模板
   */
  async getMedicalRecordTemplates(): Promise<Array<{
    id: string;
    name: string;
    template: Partial<MedicalRecord>;
  }>> {
    // 返回常用病历模板
    return [
      {
        id: 'common_cold',
        name: '感冒模板',
        template: {
          chiefComplaint: '发热、咳嗽、流涕',
          presentIllness: '患者于X天前无明显诱因出现发热，体温最高达XX℃，伴有咳嗽、流涕等症状...',
          physicalExamination: '体温：XX℃，血压：XX/XX mmHg，心率：XX次/分，呼吸：XX次/分...',
          preliminaryDiagnosis: '急性上呼吸道感染',
          treatmentPlan: '1. 对症治疗；2. 多饮水，注意休息；3. 必要时给予退热药物...'
        }
      },
      {
        id: 'hypertension',
        name: '高血压模板',
        template: {
          chiefComplaint: '血压升高',
          presentIllness: '患者既往有高血压病史，平时血压控制情况...',
          physicalExamination: '血压：XX/XX mmHg，心率：XX次/分...',
          preliminaryDiagnosis: '原发性高血压',
          treatmentPlan: '1. 降压药物治疗；2. 低盐低脂饮食；3. 适量运动...'
        }
      }
    ];
  }

  /**
   * 导出病历为PDF
   */
  async exportToPDF(recordId: number): Promise<Blob> {
    const response = await apiClient.get(`${this.baseUrl}/${recordId}/export/pdf`, {
      responseType: 'blob'
    });
    return response.data;
  }

  /**
   * 打印病历
   */
  async printMedicalRecord(recordId: number): Promise<void> {
    const pdfBlob = await this.exportToPDF(recordId);
    const url = URL.createObjectURL(pdfBlob);
    const printWindow = window.open(url, '_blank');
    if (printWindow) {
      printWindow.onload = () => {
        printWindow.print();
      };
    }
  }
}

export const medicalRecordService = new MedicalRecordService();