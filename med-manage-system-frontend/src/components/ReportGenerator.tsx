import React, { useState, useEffect } from 'react';
import {
  Row,
  Col,
  message,
  Typography,
  Space
} from 'antd';
import { FileTextOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';

// Import the separated components
import ReportForm from './ReportForm';
import ReportHistoryTable from './ReportHistoryTable';
import ReportPreview from './ReportPreview';
import ReportTemplateBuilder from './ReportTemplateBuilder';
import ReportExporter from './ReportExporter';

const { Title } = Typography;

// Re-export types that were originally in the large component
export interface ReportTemplate {
  id: string;
  name: string;
  description: string;
  category: string;
  fields: ReportField[];
  defaultParams: Record<string, any>;
  isCustom: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface ReportField {
  key: string;
  label: string;
  type: 'string' | 'number' | 'date' | 'boolean' | 'select';
  required: boolean;
  options?: { label: string; value: any }[];
  defaultValue?: any;
}

export interface ReportParams {
  templateId: string;
  dateRange?: [string, string];
  filters: Record<string, any>;
  format: 'excel' | 'pdf';
  includeCharts: boolean;
  groupBy?: string[];
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export interface GeneratedReport {
  id: string;
  templateId: string;
  templateName: string;
  params: ReportParams;
  status: 'generating' | 'completed' | 'failed';
  progress: number;
  downloadUrl?: string;
  createdAt: string;
  fileSize?: number;
  error?: string;
}

interface ReportGeneratorProps {
  onReportGenerated?: (report: GeneratedReport) => void;
  onReportDownload?: (reportId: string) => void;
}

const ReportGenerator: React.FC<ReportGeneratorProps> = ({
  onReportGenerated,
  onReportDownload
}) => {
  const [loading, setLoading] = useState(false);
  const [templates, setTemplates] = useState<ReportTemplate[]>([]);
  const [selectedTemplate, setSelectedTemplate] = useState<ReportTemplate | null>(null);
  const [templateBuilderVisible, setTemplateBuilderVisible] = useState(false);
  const [previewModalVisible, setPreviewModalVisible] = useState(false);
  const [exporterVisible, setExporterVisible] = useState(false);
  const [selectedReport, setSelectedReport] = useState<GeneratedReport | null>(null);
  const [generatedReports, setGeneratedReports] = useState<GeneratedReport[]>([]);

  // Predefined report templates (moved from original component)
  const predefinedTemplates: ReportTemplate[] = [
    {
      id: 'daily-financial',
      name: '日财务报表',
      description: '生成指定日期的财务收入统计报表',
      category: '财务报表',
      fields: [
        {
          key: 'date',
          label: '报表日期',
          type: 'date',
          required: true,
          defaultValue: dayjs().format('YYYY-MM-DD')
        },
        {
          key: 'includeRefunds',
          label: '包含退费',
          type: 'boolean',
          required: false,
          defaultValue: true
        },
        {
          key: 'groupByService',
          label: '按服务分组',
          type: 'boolean',
          required: false,
          defaultValue: false
        }
      ],
      defaultParams: {
        format: 'excel',
        includeCharts: true
      },
      isCustom: false
    },
    {
      id: 'monthly-financial',
      name: '月财务报表',
      description: '生成指定月份的财务收入统计报表',
      category: '财务报表',
      fields: [
        {
          key: 'month',
          label: '报表月份',
          type: 'date',
          required: true,
          defaultValue: dayjs().format('YYYY-MM')
        },
        {
          key: 'includeComparison',
          label: '包含同比分析',
          type: 'boolean',
          required: false,
          defaultValue: true
        }
      ],
      defaultParams: {
        format: 'excel',
        includeCharts: true
      },
      isCustom: false
    },
    {
      id: 'patient-demographics',
      name: '患者统计报表',
      description: '生成患者人口统计分析报表',
      category: '患者分析',
      fields: [
        {
          key: 'dateRange',
          label: '统计时间范围',
          type: 'date',
          required: true
        },
        {
          key: 'groupBy',
          label: '分组方式',
          type: 'select',
          required: true,
          options: [
            { label: '按性别', value: 'gender' },
            { label: '按年龄段', value: 'ageGroup' },
            { label: '按地区', value: 'location' }
          ],
          defaultValue: 'gender'
        }
      ],
      defaultParams: {
        format: 'excel',
        includeCharts: true
      },
      isCustom: false
    }
  ];

  // Load initial data
  useEffect(() => {
    loadTemplates();
    loadGeneratedReports();
  }, []);

  const loadTemplates = async () => {
    try {
      const customTemplates = JSON.parse(localStorage.getItem('customReportTemplates') || '[]');
      setTemplates([...predefinedTemplates, ...customTemplates]);
    } catch (error) {
      console.error('加载报表模板失败:', error);
      setTemplates(predefinedTemplates);
    }
  };

  const loadGeneratedReports = async () => {
    try {
      const reports = JSON.parse(localStorage.getItem('generatedReports') || '[]');
      setGeneratedReports(reports);
    } catch (error) {
      console.error('加载生成的报表失败:', error);
    }
  };

  const handleTemplateChange = (templateId: string) => {
    const template = templates.find(t => t.id === templateId);
    setSelectedTemplate(template || null);
  };

  const handleGenerateReport = async (formValues: any) => {
    if (!selectedTemplate) return;

    setLoading(true);
    try {
      const params: ReportParams = {
        templateId: selectedTemplate.id,
        filters: formValues,
        format: formValues.format || 'excel',
        includeCharts: formValues.includeCharts || false
      };

      const report: GeneratedReport = {
        id: `report_${Date.now()}`,
        templateId: selectedTemplate.id,
        templateName: selectedTemplate.name,
        params,
        status: 'generating',
        progress: 0,
        createdAt: new Date().toISOString()
      };

      const updatedReports = [report, ...generatedReports];
      setGeneratedReports(updatedReports);
      localStorage.setItem('generatedReports', JSON.stringify(updatedReports));

      // Simulate report generation
      const progressInterval = setInterval(() => {
        setGeneratedReports(prevReports => {
          const updated = prevReports.map(r => {
            if (r.id === report.id && r.status === 'generating') {
              return { ...r, progress: Math.min(r.progress + 10, 90) };
            }
            return r;
          });
          return updated;
        });
      }, 200);

      setTimeout(() => {
        clearInterval(progressInterval);
        const completedReport: GeneratedReport = {
          ...report,
          status: 'completed',
          progress: 100,
          downloadUrl: `/api/reports/download/${report.id}`,
          fileSize: Math.floor(Math.random() * 1024 * 1024) + 50 * 1024
        };

        const finalReports = generatedReports.map(r => 
          r.id === report.id ? completedReport : r
        );
        setGeneratedReports(finalReports);
        localStorage.setItem('generatedReports', JSON.stringify(finalReports));

        message.success(`报表"${selectedTemplate.name}"生成成功！`);
        onReportGenerated?.(completedReport);
      }, 2000);

    } catch (error) {
      console.error('生成报表失败:', error);
      message.error('生成报表失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadReport = (report: GeneratedReport) => {
    if (report.downloadUrl) {
      window.open(report.downloadUrl, '_blank');
      onReportDownload?.(report.id);
    } else {
      message.error('报表下载链接不可用');
    }
  };

  const handlePreviewReport = (report: GeneratedReport) => {
    setSelectedReport(report);
    setPreviewModalVisible(true);
  };

  const handleDeleteReport = (reportId: string) => {
    const updatedReports = generatedReports.filter(r => r.id !== reportId);
    setGeneratedReports(updatedReports);
    localStorage.setItem('generatedReports', JSON.stringify(updatedReports));
    message.success('报表已删除');
  };

  const handleCreateCustomTemplate = (templateData: Omit<ReportTemplate, 'id' | 'createdAt' | 'updatedAt'>) => {
    const customTemplate: ReportTemplate = {
      ...templateData,
      id: `custom_${Date.now()}`,
      isCustom: true,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    const customTemplates = JSON.parse(localStorage.getItem('customReportTemplates') || '[]');
    const updatedCustomTemplates = [...customTemplates, customTemplate];
    localStorage.setItem('customReportTemplates', JSON.stringify(updatedCustomTemplates));
    
    setTemplates([...predefinedTemplates, ...updatedCustomTemplates]);
    setTemplateBuilderVisible(false);
    message.success('自定义模板创建成功！');
  };

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <Space>
          <FileTextOutlined style={{ fontSize: '24px', color: '#1890ff' }} />
          <Title level={2} style={{ margin: 0 }}>
            报表管理中心
          </Title>
        </Space>
      </div>

      <Row gutter={[24, 24]}>
        <Col xs={24} lg={8}>
          <ReportForm
            templates={templates}
            selectedTemplate={selectedTemplate}
            loading={loading}
            onTemplateChange={handleTemplateChange}
            onGenerate={handleGenerateReport}
            onOpenTemplateBuilder={() => setTemplateBuilderVisible(true)}
          />
        </Col>
        
        <Col xs={24} lg={16}>
          <ReportHistoryTable
            reports={generatedReports}
            loading={false}
            onDownload={handleDownloadReport}
            onPreview={handlePreviewReport}
            onDelete={handleDeleteReport}
            onRefresh={loadGeneratedReports}
          />
        </Col>
      </Row>

      <ReportPreview
        visible={previewModalVisible}
        report={selectedReport}
        onClose={() => setPreviewModalVisible(false)}
        onDownload={handleDownloadReport}
      />

      <ReportTemplateBuilder
        visible={templateBuilderVisible}
        onClose={() => setTemplateBuilderVisible(false)}
        onSave={handleCreateCustomTemplate}
      />

      <ReportExporter
        visible={exporterVisible}
        report={selectedReport}
        onClose={() => setExporterVisible(false)}
      />
    </div>
  );
};

export default ReportGenerator;