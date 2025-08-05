import React, { useState, useRef } from 'react';
import {
  Modal,
  Button,
  Space,
  Select,
  Form,
  Input,
  Checkbox,
  Radio,
  Divider,
  Typography,
  Alert,
  message,
  Tooltip,
  Progress,
  Card,
  Row,
  Col,
  Tag,
  Upload,
  QRCode
} from 'antd';
import {
  FileExcelOutlined,
  FilePdfOutlined,
  DownloadOutlined,
  PrinterOutlined,
  ShareAltOutlined,
  MailOutlined,
  LinkOutlined,
  QrcodeOutlined,
  CloudUploadOutlined,
  CopyOutlined,
  CheckOutlined,
  LoadingOutlined
} from '@ant-design/icons';
import type { GeneratedReport } from './ReportGenerator';
import reportService from '../services/reportService';
import exportUtils from '../utils/exportUtils';

const { Title, Text, Paragraph } = Typography;
const { Option } = Select;
const { TextArea } = Input;

export interface ExportOptions {
  format: 'excel' | 'pdf';
  includeCharts: boolean;
  includeRawData: boolean;
  pageOrientation?: 'portrait' | 'landscape';
  pageSize?: 'A4' | 'A3' | 'Letter';
  compression?: 'none' | 'low' | 'medium' | 'high';
  password?: string;
  watermark?: string;
}

export interface ShareOptions {
  method: 'email' | 'link' | 'qr' | 'cloud';
  recipients?: string[];
  message?: string;
  expiryDays?: number;
  requirePassword?: boolean;
  allowDownload?: boolean;
  trackViews?: boolean;
}

export interface PrintOptions {
  copies: number;
  pageRange?: 'all' | 'current' | 'custom';
  customPages?: string;
  duplex?: 'none' | 'long' | 'short';
  colorMode?: 'color' | 'grayscale';
  quality?: 'draft' | 'normal' | 'high';
  paperSize?: 'A4' | 'A3' | 'Letter';
  margins?: 'normal' | 'narrow' | 'wide' | 'custom';
}

interface ReportExporterProps {
  visible: boolean;
  report: GeneratedReport | null;
  onCancel: () => void;
  onExportComplete?: (report: GeneratedReport, options: ExportOptions) => void;
  onShareComplete?: (report: GeneratedReport, options: ShareOptions) => void;
  onPrintComplete?: (report: GeneratedReport, options: PrintOptions) => void;
}

const ReportExporter: React.FC<ReportExporterProps> = ({
  visible,
  report,
  onCancel,
  onExportComplete,
  onShareComplete,
  onPrintComplete
}) => {
  const [form] = Form.useForm();
  const [activeTab, setActiveTab] = useState<'export' | 'share' | 'print'>('export');
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [shareUrl, setShareUrl] = useState<string>('');
  const [copied, setCopied] = useState(false);
  const printRef = useRef<HTMLDivElement>(null);

  // 导出报表
  const handleExport = async () => {
    if (!report) return;

    try {
      const values = await form.validateFields();
      setLoading(true);
      setProgress(0);

      const exportOptions: ExportOptions = {
        format: values.format || 'excel',
        includeCharts: values.includeCharts !== false,
        includeRawData: values.includeRawData !== false,
        pageOrientation: values.pageOrientation || 'portrait',
        pageSize: values.pageSize || 'A4',
        compression: values.compression || 'medium',
        password: values.password,
        watermark: values.watermark
      };

      // 模拟导出进度
      const progressInterval = setInterval(() => {
        setProgress(prev => {
          if (prev >= 90) {
            clearInterval(progressInterval);
            return prev;
          }
          return prev + 10;
        });
      }, 200);

      // 模拟导出过程
      setTimeout(async () => {
        try {
          // 调用导出服务
          const blob = await reportService.exportReport(report.id, exportOptions);
          
          // 下载文件
          const filename = exportUtils.generateFileName(
            report.templateName,
            exportOptions.format,
            report.createdAt.replace(/[:\s]/g, '_')
          );
          exportUtils.downloadFile(blob, filename);

          setProgress(100);
          message.success('报表导出成功');
          onExportComplete?.(report, exportOptions);
          
          setTimeout(() => {
            onCancel();
            setProgress(0);
          }, 1000);

        } catch (error) {
          console.error('导出失败:', error);
          message.error('导出失败，请稍后重试');
        } finally {
          clearInterval(progressInterval);
          setLoading(false);
        }
      }, 2000);

    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  // 分享报表
  const handleShare = async () => {
    if (!report) return;

    try {
      const values = await form.validateFields();
      setLoading(true);

      const shareOptions: ShareOptions = {
        method: values.shareMethod || 'link',
        recipients: values.recipients?.split(',').map((email: string) => email.trim()),
        message: values.shareMessage,
        expiryDays: values.expiryDays || 7,
        requirePassword: values.requirePassword || false,
        allowDownload: values.allowDownload !== false,
        trackViews: values.trackViews !== false
      };

      // 调用分享服务
      try {
        const shareResult = await reportService.shareReport(report.id, shareOptions);
        
        if (shareResult.success) {
          if (shareResult.shareUrl) {
            setShareUrl(shareResult.shareUrl);
          }
          
          if (shareOptions.method === 'email' && shareResult.emailsSent) {
            message.success(`报表已发送到 ${shareResult.emailsSent} 个邮箱`);
          } else {
            message.success('分享链接已生成');
          }
          
          onShareComplete?.(report, shareOptions);
        } else {
          message.error('分享失败，请稍后重试');
        }
      } catch (error) {
        console.error('分享失败:', error);
        message.error('分享失败，请稍后重试');
      } finally {
        setLoading(false);
      }

    } catch (error) {
      console.error('分享失败:', error);
      message.error('分享失败，请稍后重试');
      setLoading(false);
    }
  };

  // 打印报表
  const handlePrint = async () => {
    if (!report) return;

    try {
      const values = await form.validateFields();
      setLoading(true);

      const printOptions: PrintOptions = {
        copies: values.copies || 1,
        pageRange: values.pageRange || 'all',
        customPages: values.customPages,
        duplex: values.duplex || 'none',
        colorMode: values.colorMode || 'color',
        quality: values.quality || 'normal',
        paperSize: values.paperSize || 'A4',
        margins: values.margins || 'normal'
      };

      // 调用打印服务
      try {
        const printResult = await reportService.printReport(report.id, printOptions);
        
        if (printResult.status === 'queued' || printResult.status === 'printing') {
          // 执行浏览器打印预览
          const printContent = `
            <div class="print-content">
              <p>报表内容将在这里显示...</p>
              <p>格式: ${report.params.format.toUpperCase()}</p>
              <p>包含图表: ${report.params.includeCharts ? '是' : '否'}</p>
              <p>打印任务ID: ${printResult.printJobId}</p>
              <p>打印份数: ${printOptions.copies}</p>
              <p>页面范围: ${printOptions.pageRange}</p>
              <p>颜色模式: ${printOptions.colorMode}</p>
              <p>打印质量: ${printOptions.quality}</p>
            </div>
          `;
          
          const printHTML = exportUtils.generatePrintHTML(
            `打印报表 - ${report.templateName}`,
            printContent,
            {
              timestamp: report.createdAt
            }
          );
          
          exportUtils.executePrint(printHTML);

          message.success(printResult.message || '打印任务已发送');
          onPrintComplete?.(report, printOptions);
          onCancel();
        } else {
          message.error('打印失败，请稍后重试');
        }
      } catch (error) {
        console.error('打印失败:', error);
        message.error('打印失败，请稍后重试');
      } finally {
        setLoading(false);
      }

    } catch (error) {
      console.error('打印失败:', error);
      message.error('打印失败，请稍后重试');
      setLoading(false);
    }
  };

  // 复制分享链接
  const handleCopyLink = async () => {
    if (!shareUrl) return;

    const success = await exportUtils.copyToClipboard(shareUrl);
    if (success) {
      setCopied(true);
      message.success('链接已复制到剪贴板');
      setTimeout(() => setCopied(false), 2000);
    } else {
      message.error('复制失败，请手动复制');
    }
  };

  // 重置表单
  const handleReset = () => {
    form.resetFields();
    setShareUrl('');
    setCopied(false);
    setProgress(0);
  };

  // 关闭弹窗
  const handleCancel = () => {
    handleReset();
    onCancel();
  };

  if (!report) return null;

  return (
    <Modal
      title={
        <Space>
          <ShareAltOutlined />
          报表导出与分享 - {report.templateName}
        </Space>
      }
      open={visible}
      onCancel={handleCancel}
      width={800}
      footer={null}
      destroyOnClose
    >
      <div style={{ marginBottom: '16px' }}>
        <Space>
          <Button
            type={activeTab === 'export' ? 'primary' : 'default'}
            icon={<DownloadOutlined />}
            onClick={() => setActiveTab('export')}
          >
            导出
          </Button>
          <Button
            type={activeTab === 'share' ? 'primary' : 'default'}
            icon={<ShareAltOutlined />}
            onClick={() => setActiveTab('share')}
          >
            分享
          </Button>
          <Button
            type={activeTab === 'print' ? 'primary' : 'default'}
            icon={<PrinterOutlined />}
            onClick={() => setActiveTab('print')}
          >
            打印
          </Button>
        </Space>
      </div>

      <Form form={form} layout="vertical">
        {/* 导出选项 */}
        {activeTab === 'export' && (
          <div>
            <Card title="导出设置" size="small" style={{ marginBottom: '16px' }}>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="导出格式"
                    name="format"
                    initialValue="excel"
                  >
                    <Select>
                      <Option value="excel">
                        <FileExcelOutlined /> Excel (.xlsx)
                      </Option>
                      <Option value="pdf">
                        <FilePdfOutlined /> PDF (.pdf)
                      </Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="页面方向"
                    name="pageOrientation"
                    initialValue="portrait"
                  >
                    <Select>
                      <Option value="portrait">纵向</Option>
                      <Option value="landscape">横向</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="页面大小"
                    name="pageSize"
                    initialValue="A4"
                  >
                    <Select>
                      <Option value="A4">A4</Option>
                      <Option value="A3">A3</Option>
                      <Option value="Letter">Letter</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="压缩级别"
                    name="compression"
                    initialValue="medium"
                  >
                    <Select>
                      <Option value="none">无压缩</Option>
                      <Option value="low">低压缩</Option>
                      <Option value="medium">中等压缩</Option>
                      <Option value="high">高压缩</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="includeCharts"
                    valuePropName="checked"
                    initialValue={true}
                  >
                    <Checkbox>包含图表</Checkbox>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="includeRawData"
                    valuePropName="checked"
                    initialValue={true}
                  >
                    <Checkbox>包含原始数据</Checkbox>
                  </Form.Item>
                </Col>
              </Row>
            </Card>

            <Card title="安全设置" size="small" style={{ marginBottom: '16px' }}>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="文件密码"
                    name="password"
                  >
                    <Input.Password placeholder="可选，为文件设置密码" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="水印文字"
                    name="watermark"
                  >
                    <Input placeholder="可选，添加水印文字" />
                  </Form.Item>
                </Col>
              </Row>
            </Card>

            {loading && (
              <div style={{ marginBottom: '16px' }}>
                <Progress percent={progress} status="active" />
                <Text type="secondary">正在导出报表...</Text>
              </div>
            )}

            <div style={{ textAlign: 'right' }}>
              <Space>
                <Button onClick={handleCancel}>取消</Button>
                <Button
                  type="primary"
                  icon={<DownloadOutlined />}
                  onClick={handleExport}
                  loading={loading}
                >
                  开始导出
                </Button>
              </Space>
            </div>
          </div>
        )}

        {/* 分享选项 */}
        {activeTab === 'share' && (
          <div>
            <Card title="分享方式" size="small" style={{ marginBottom: '16px' }}>
              <Form.Item
                label="分享方式"
                name="shareMethod"
                initialValue="link"
              >
                <Radio.Group>
                  <Radio value="link">
                    <LinkOutlined /> 生成链接
                  </Radio>
                  <Radio value="email">
                    <MailOutlined /> 邮件发送
                  </Radio>
                  <Radio value="qr">
                    <QrcodeOutlined /> 二维码
                  </Radio>
                  <Radio value="cloud">
                    <CloudUploadOutlined /> 云端分享
                  </Radio>
                </Radio.Group>
              </Form.Item>

              <Form.Item
                noStyle
                shouldUpdate={(prevValues, currentValues) =>
                  prevValues.shareMethod !== currentValues.shareMethod
                }
              >
                {({ getFieldValue }) => {
                  const shareMethod = getFieldValue('shareMethod');
                  
                  if (shareMethod === 'email') {
                    return (
                      <Form.Item
                        label="收件人邮箱"
                        name="recipients"
                        rules={[{ required: true, message: '请输入收件人邮箱' }]}
                      >
                        <TextArea
                          placeholder="输入邮箱地址，多个邮箱用逗号分隔"
                          rows={3}
                        />
                      </Form.Item>
                    );
                  }
                  return null;
                }}
              </Form.Item>

              <Form.Item
                label="分享消息"
                name="shareMessage"
              >
                <TextArea
                  placeholder="可选，添加分享消息"
                  rows={3}
                />
              </Form.Item>
            </Card>

            <Card title="权限设置" size="small" style={{ marginBottom: '16px' }}>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="有效期（天）"
                    name="expiryDays"
                    initialValue={7}
                  >
                    <Select>
                      <Option value={1}>1天</Option>
                      <Option value={3}>3天</Option>
                      <Option value={7}>7天</Option>
                      <Option value={30}>30天</Option>
                      <Option value={0}>永久</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="requirePassword"
                    valuePropName="checked"
                  >
                    <Checkbox>需要密码访问</Checkbox>
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="allowDownload"
                    valuePropName="checked"
                    initialValue={true}
                  >
                    <Checkbox>允许下载</Checkbox>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="trackViews"
                    valuePropName="checked"
                    initialValue={true}
                  >
                    <Checkbox>跟踪查看次数</Checkbox>
                  </Form.Item>
                </Col>
              </Row>
            </Card>

            {shareUrl && (
              <Card title="分享链接" size="small" style={{ marginBottom: '16px' }}>
                <div style={{ marginBottom: '12px' }}>
                  <Input
                    value={shareUrl}
                    readOnly
                    addonAfter={
                      <Tooltip title={copied ? '已复制' : '复制链接'}>
                        <Button
                          type="text"
                          icon={copied ? <CheckOutlined /> : <CopyOutlined />}
                          onClick={handleCopyLink}
                        />
                      </Tooltip>
                    }
                  />
                </div>
                <div style={{ textAlign: 'center' }}>
                  <QRCode value={shareUrl} size={120} />
                  <div style={{ marginTop: '8px' }}>
                    <Text type="secondary">扫描二维码访问</Text>
                  </div>
                </div>
              </Card>
            )}

            <div style={{ textAlign: 'right' }}>
              <Space>
                <Button onClick={handleCancel}>取消</Button>
                <Button
                  type="primary"
                  icon={<ShareAltOutlined />}
                  onClick={handleShare}
                  loading={loading}
                >
                  开始分享
                </Button>
              </Space>
            </div>
          </div>
        )}

        {/* 打印选项 */}
        {activeTab === 'print' && (
          <div>
            <Card title="打印设置" size="small" style={{ marginBottom: '16px' }}>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="打印份数"
                    name="copies"
                    initialValue={1}
                  >
                    <Input type="number" min={1} max={99} />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="页面范围"
                    name="pageRange"
                    initialValue="all"
                  >
                    <Select>
                      <Option value="all">全部页面</Option>
                      <Option value="current">当前页面</Option>
                      <Option value="custom">自定义</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item
                noStyle
                shouldUpdate={(prevValues, currentValues) =>
                  prevValues.pageRange !== currentValues.pageRange
                }
              >
                {({ getFieldValue }) => {
                  const pageRange = getFieldValue('pageRange');
                  
                  if (pageRange === 'custom') {
                    return (
                      <Form.Item
                        label="页面范围"
                        name="customPages"
                        rules={[{ required: true, message: '请输入页面范围' }]}
                      >
                        <Input placeholder="例如: 1-5, 8, 10-12" />
                      </Form.Item>
                    );
                  }
                  return null;
                }}
              </Form.Item>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="双面打印"
                    name="duplex"
                    initialValue="none"
                  >
                    <Select>
                      <Option value="none">单面</Option>
                      <Option value="long">双面（长边翻页）</Option>
                      <Option value="short">双面（短边翻页）</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="颜色模式"
                    name="colorMode"
                    initialValue="color"
                  >
                    <Select>
                      <Option value="color">彩色</Option>
                      <Option value="grayscale">灰度</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="打印质量"
                    name="quality"
                    initialValue="normal"
                  >
                    <Select>
                      <Option value="draft">草稿</Option>
                      <Option value="normal">普通</Option>
                      <Option value="high">高质量</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="纸张大小"
                    name="paperSize"
                    initialValue="A4"
                  >
                    <Select>
                      <Option value="A4">A4</Option>
                      <Option value="A3">A3</Option>
                      <Option value="Letter">Letter</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item
                label="页边距"
                name="margins"
                initialValue="normal"
              >
                <Radio.Group>
                  <Radio value="normal">普通</Radio>
                  <Radio value="narrow">窄</Radio>
                  <Radio value="wide">宽</Radio>
                  <Radio value="custom">自定义</Radio>
                </Radio.Group>
              </Form.Item>
            </Card>

            <Card title="打印预览" size="small" style={{ marginBottom: '16px' }}>
              <div
                ref={printRef}
                style={{
                  border: '1px solid #d9d9d9',
                  padding: '20px',
                  backgroundColor: '#fff',
                  minHeight: '200px'
                }}
              >
                <div style={{ textAlign: 'center', marginBottom: '20px' }}>
                  <Title level={4}>{report.templateName}</Title>
                  <Text type="secondary">生成时间: {report.createdAt}</Text>
                </div>
                <div>
                  <p>报表内容预览...</p>
                  <p>格式: {report.params.format.toUpperCase()}</p>
                  <p>包含图表: {report.params.includeCharts ? '是' : '否'}</p>
                  <p>文件大小: {report.fileSize ? reportService.formatFileSize(report.fileSize) : '未知'}</p>
                </div>
              </div>
            </Card>

            <div style={{ textAlign: 'right' }}>
              <Space>
                <Button onClick={handleCancel}>取消</Button>
                <Button
                  type="primary"
                  icon={<PrinterOutlined />}
                  onClick={handlePrint}
                  loading={loading}
                >
                  开始打印
                </Button>
              </Space>
            </div>
          </div>
        )}
      </Form>

      {/* 报表信息 */}
      <div style={{ marginTop: '16px', padding: '12px', backgroundColor: '#f5f5f5', borderRadius: '6px' }}>
        <Row gutter={16}>
          <Col span={8}>
            <Text strong>报表名称:</Text>
            <br />
            <Text>{report.templateName}</Text>
          </Col>
          <Col span={8}>
            <Text strong>生成时间:</Text>
            <br />
            <Text>{report.createdAt}</Text>
          </Col>
          <Col span={8}>
            <Text strong>文件大小:</Text>
            <br />
            <Text>{report.fileSize ? reportService.formatFileSize(report.fileSize) : '未知'}</Text>
          </Col>
        </Row>
      </div>
    </Modal>
  );
};

export default ReportExporter;