import React, { useState, useEffect } from 'react';
import {
  Card,
  Row,
  Col,
  Statistic,
  Typography,
  Space,
  Button,
  Tabs,
  Alert,
  List,
  Tag,
  Tooltip,
  message
} from 'antd';
import {
  FileExcelOutlined,
  FilePdfOutlined,
  BarChartOutlined,
  TrophyOutlined,
  ClockCircleOutlined,
  DownloadOutlined,
  ReloadOutlined,
  ShareAltOutlined,
  PrinterOutlined
} from '@ant-design/icons';
import ReportGenerator, { GeneratedReport } from '../components/ReportGenerator';
import ReportExporter from '../components/ReportExporter';
import reportService from '../services/reportService';

const { Title, Text } = Typography;
const { TabPane } = Tabs;

interface ReportStatistics {
  totalReports: number;
  todayReports: number;
  popularTemplates: Array<{
    templateId: string;
    templateName: string;
    count: number;
  }>;
  averageGenerationTime: number;
}

const ReportManagement: React.FC = () => {
  const [statistics, setStatistics] = useState<ReportStatistics | null>(null);
  const [recentReports, setRecentReports] = useState<GeneratedReport[]>([]);
  const [loading, setLoading] = useState(false);
  const [exporterVisible, setExporterVisible] = useState(false);
  const [selectedReport, setSelectedReport] = useState<GeneratedReport | null>(null);

  // 加载统计数据
  const loadStatistics = async () => {
    try {
      setLoading(true);
      
      // 模拟统计数据，实际应该调用 reportService.getReportStatistics()
      const mockStats: ReportStatistics = {
        totalReports: 156,
        todayReports: 8,
        popularTemplates: [
          { templateId: 'daily-financial', templateName: '日财务报表', count: 45 },
          { templateId: 'monthly-financial', templateName: '月财务报表', count: 32 },
          { templateId: 'patient-demographics', templateName: '患者统计报表', count: 28 },
          { templateId: 'doctor-performance', templateName: '医生绩效报表', count: 21 },
          { templateId: 'inventory-report', templateName: '库存统计报表', count: 18 }
        ],
        averageGenerationTime: 2.3
      };
      
      setStatistics(mockStats);
      
      // 加载最近的报表
      const mockRecentReports: GeneratedReport[] = [
        {
          id: 'report_1',
          templateId: 'daily-financial',
          templateName: '日财务报表',
          params: {
            templateId: 'daily-financial',
            filters: { date: '2024-01-15' },
            format: 'excel',
            includeCharts: true
          },
          status: 'completed',
          progress: 100,
          downloadUrl: '/api/reports/report_1/download',
          createdAt: '2024-01-15 14:30:25',
          fileSize: 245760
        },
        {
          id: 'report_2',
          templateId: 'patient-demographics',
          templateName: '患者统计报表',
          params: {
            templateId: 'patient-demographics',
            filters: { dateRange: ['2024-01-01', '2024-01-15'] },
            format: 'pdf',
            includeCharts: true
          },
          status: 'completed',
          progress: 100,
          downloadUrl: '/api/reports/report_2/download',
          createdAt: '2024-01-15 13:45:12',
          fileSize: 512000
        },
        {
          id: 'report_3',
          templateId: 'monthly-financial',
          templateName: '月财务报表',
          params: {
            templateId: 'monthly-financial',
            filters: { month: '2024-01' },
            format: 'excel',
            includeCharts: true
          },
          status: 'generating',
          progress: 65,
          createdAt: '2024-01-15 15:20:08'
        }
      ];
      
      setRecentReports(mockRecentReports);
      
    } catch (error) {
      console.error('加载统计数据失败:', error);
      message.error('加载统计数据失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStatistics();
  }, []);

  // 处理报表生成完成
  const handleReportGenerated = (report: GeneratedReport) => {
    setRecentReports(prev => [report, ...prev.slice(0, 9)]);
    
    // 更新统计数据
    if (statistics) {
      setStatistics({
        ...statistics,
        totalReports: statistics.totalReports + 1,
        todayReports: statistics.todayReports + 1
      });
    }
  };

  // 处理报表下载
  const handleReportDownload = (reportId: string) => {
    console.log('下载报表:', reportId);
  };

  // 下载最近报表
  const handleDownloadRecentReport = async (report: GeneratedReport) => {
    if (report.status !== 'completed' || !report.downloadUrl) {
      message.error('报表尚未生成完成');
      return;
    }

    try {
      // 模拟下载
      const link = document.createElement('a');
      link.href = report.downloadUrl;
      link.download = reportService.generateFileName(
        report.templateName,
        report.params.format,
        report.createdAt.replace(/[:\s]/g, '_')
      );
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      message.success('报表下载开始');
    } catch (error) {
      console.error('下载报表失败:', error);
      message.error('下载报表失败');
    }
  };

  return (
    <div style={{ padding: '24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <Title level={2}>
          <BarChartOutlined style={{ marginRight: '8px' }} />
          报表管理
        </Title>
        <Text type="secondary">
          生成和管理各类业务报表，支持多种格式导出和自定义模板
        </Text>
      </div>

      {/* 统计卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="总报表数"
              value={statistics?.totalReports || 0}
              prefix={<FileExcelOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="今日生成"
              value={statistics?.todayReports || 0}
              prefix={<ClockCircleOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="平均生成时间"
              value={statistics?.averageGenerationTime || 0}
              precision={1}
              suffix="秒"
              prefix={<ClockCircleOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="热门模板"
              value={statistics?.popularTemplates?.[0]?.count || 0}
              prefix={<TrophyOutlined />}
              valueStyle={{ color: '#fa8c16' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              {statistics?.popularTemplates?.[0]?.templateName || '暂无数据'}
            </div>
          </Card>
        </Col>
      </Row>

      <Tabs defaultActiveKey="generator" size="large">
        {/* 报表生成器 */}
        <TabPane tab="报表生成器" key="generator">
          <ReportGenerator
            onReportGenerated={handleReportGenerated}
            onReportDownload={handleReportDownload}
          />
        </TabPane>

        {/* 报表概览 */}
        <TabPane tab="报表概览" key="overview">
          <Row gutter={[24, 24]}>
            {/* 最近生成的报表 */}
            <Col xs={24} lg={14}>
              <Card 
                title="最近生成的报表" 
                extra={
                  <Button
                    icon={<ReloadOutlined />}
                    onClick={loadStatistics}
                    loading={loading}
                  >
                    刷新
                  </Button>
                }
              >
                <List
                  dataSource={recentReports}
                  renderItem={(report) => (
                    <List.Item
                      actions={[
                        <Tooltip title="下载报表">
                          <Button
                            type="text"
                            icon={<DownloadOutlined />}
                            onClick={() => handleDownloadRecentReport(report)}
                            disabled={report.status !== 'completed'}
                          />
                        </Tooltip>,
                        <Tooltip title="导出分享">
                          <Button
                            type="text"
                            icon={<ShareAltOutlined />}
                            onClick={() => {
                              setSelectedReport(report);
                              setExporterVisible(true);
                            }}
                            disabled={report.status !== 'completed'}
                          />
                        </Tooltip>,
                        <Tooltip title="打印">
                          <Button
                            type="text"
                            icon={<PrinterOutlined />}
                            onClick={() => {
                              setSelectedReport(report);
                              setExporterVisible(true);
                            }}
                            disabled={report.status !== 'completed'}
                          />
                        </Tooltip>
                      ]}
                    >
                      <List.Item.Meta
                        avatar={
                          report.params.format === 'excel' ? (
                            <FileExcelOutlined style={{ fontSize: '24px', color: '#52c41a' }} />
                          ) : (
                            <FilePdfOutlined style={{ fontSize: '24px', color: '#f5222d' }} />
                          )
                        }
                        title={
                          <Space>
                            {report.templateName}
                            <Tag color={
                              report.status === 'completed' ? 'success' :
                              report.status === 'generating' ? 'processing' : 'error'
                            }>
                              {report.status === 'completed' ? '已完成' :
                               report.status === 'generating' ? '生成中' : '失败'}
                            </Tag>
                          </Space>
                        }
                        description={
                          <Space direction="vertical" size={4}>
                            <Text type="secondary">
                              生成时间: {report.createdAt}
                            </Text>
                            {report.fileSize && (
                              <Text type="secondary">
                                文件大小: {reportService.formatFileSize(report.fileSize)}
                              </Text>
                            )}
                            {report.status === 'generating' && (
                              <div style={{ width: '200px' }}>
                                <div style={{ 
                                  height: '4px', 
                                  backgroundColor: '#f0f0f0', 
                                  borderRadius: '2px',
                                  overflow: 'hidden'
                                }}>
                                  <div style={{
                                    height: '100%',
                                    width: `${report.progress}%`,
                                    backgroundColor: '#1890ff',
                                    transition: 'width 0.3s ease'
                                  }} />
                                </div>
                                <Text type="secondary" style={{ fontSize: '12px' }}>
                                  {report.progress}%
                                </Text>
                              </div>
                            )}
                          </Space>
                        }
                      />
                    </List.Item>
                  )}
                />
              </Card>
            </Col>

            {/* 热门报表模板 */}
            <Col xs={24} lg={10}>
              <Card title="热门报表模板">
                <List
                  dataSource={statistics?.popularTemplates || []}
                  renderItem={(template, index) => (
                    <List.Item>
                      <List.Item.Meta
                        avatar={
                          <div style={{
                            width: '32px',
                            height: '32px',
                            borderRadius: '50%',
                            backgroundColor: '#1890ff',
                            color: 'white',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: '14px',
                            fontWeight: 'bold'
                          }}>
                            {index + 1}
                          </div>
                        }
                        title={template.templateName}
                        description={
                          <Space>
                            <Text type="secondary">使用次数: {template.count}</Text>
                            <Tag color="blue">{template.templateId}</Tag>
                          </Space>
                        }
                      />
                    </List.Item>
                  )}
                />
              </Card>

              {/* 使用提示 */}
              <Card title="使用提示" style={{ marginTop: '16px' }}>
                <Alert
                  message="报表生成提示"
                  description={
                    <ul style={{ margin: 0, paddingLeft: '20px' }}>
                      <li>Excel格式适合数据分析和二次处理</li>
                      <li>PDF格式适合打印和正式文档</li>
                      <li>包含图表会增加文件大小但提升可读性</li>
                      <li>大数据量报表生成可能需要较长时间</li>
                      <li>可以创建自定义模板满足特殊需求</li>
                    </ul>
                  }
                  type="info"
                  showIcon
                />
              </Card>
            </Col>
          </Row>
        </TabPane>
      </Tabs>

      {/* 报表导出分享弹窗 */}
      <ReportExporter
        visible={exporterVisible}
        report={selectedReport}
        onCancel={() => {
          setExporterVisible(false);
          setSelectedReport(null);
        }}
        onExportComplete={(report, options) => {
          console.log('导出完成:', report, options);
        }}
        onShareComplete={(report, options) => {
          console.log('分享完成:', report, options);
        }}
        onPrintComplete={(report, options) => {
          console.log('打印完成:', report, options);
        }}
      />
    </div>
  );
};

export default ReportManagement;