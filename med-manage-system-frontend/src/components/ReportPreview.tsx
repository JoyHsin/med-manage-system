import React from 'react';
import {
  Modal,
  Spin,
  Alert,
  Typography,
  Descriptions,
  Button,
  Space
} from 'antd';
import {
  DownloadOutlined,
  ShareAltOutlined,
  FileExcelOutlined,
  FilePdfOutlined
} from '@ant-design/icons';
import type { GeneratedReport } from './ReportGenerator';
import dayjs from 'dayjs';

const { Title, Text } = Typography;

interface ReportPreviewProps {
  visible: boolean;
  report: GeneratedReport | null;
  loading?: boolean;
  onClose: () => void;
  onDownload: (report: GeneratedReport) => void;
}

const ReportPreview: React.FC<ReportPreviewProps> = ({
  visible,
  report,
  loading = false,
  onClose,
  onDownload
}) => {
  if (!report) return null;

  const formatIcon = report.params.format === 'excel' 
    ? <FileExcelOutlined style={{ color: '#52c41a' }} />
    : <FilePdfOutlined style={{ color: '#ff4d4f' }} />;

  return (
    <Modal
      title={
        <Space>
          {formatIcon}
          <Title level={4} style={{ margin: 0 }}>
            报表预览 - {report.templateName}
          </Title>
        </Space>
      }
      open={visible}
      onCancel={onClose}
      width={800}
      footer={[
        <Button key="close" onClick={onClose}>
          关闭
        </Button>,
        <Button
          key="share"
          icon={<ShareAltOutlined />}
          onClick={() => {
            // TODO: 实现分享功能
            console.log('分享报表:', report.id);
          }}
        >
          分享
        </Button>,
        <Button
          key="download"
          type="primary"
          icon={<DownloadOutlined />}
          onClick={() => onDownload(report)}
        >
          下载
        </Button>
      ]}
    >
      {loading ? (
        <div style={{ textAlign: 'center', padding: '50px' }}>
          <Spin size="large" />
          <p style={{ marginTop: '16px' }}>正在加载报表预览...</p>
        </div>
      ) : (
        <div>
          <Alert
            message="报表信息"
            description={
              <Descriptions column={1} size="small">
                <Descriptions.Item label="报表模板">
                  {report.templateName}
                </Descriptions.Item>
                <Descriptions.Item label="生成状态">
                  <Text type={report.status === 'completed' ? 'success' : 'danger'}>
                    {report.status === 'completed' ? '已完成' : '生成失败'}
                  </Text>
                </Descriptions.Item>
                <Descriptions.Item label="文件格式">
                  {report.params.format.toUpperCase()}
                </Descriptions.Item>
                <Descriptions.Item label="文件大小">
                  {report.fileSize 
                    ? (report.fileSize > 1024 * 1024 
                        ? `${(report.fileSize / (1024 * 1024)).toFixed(1)} MB`
                        : `${(report.fileSize / 1024).toFixed(1)} KB`)
                    : '未知'
                  }
                </Descriptions.Item>
                <Descriptions.Item label="创建时间">
                  {dayjs(report.createdAt).format('YYYY-MM-DD HH:mm:ss')}
                </Descriptions.Item>
                <Descriptions.Item label="包含图表">
                  {report.params.includeCharts ? '是' : '否'}
                </Descriptions.Item>
              </Descriptions>
            }
            type="info"
            style={{ marginBottom: '16px' }}
          />

          {report.params.filters && Object.keys(report.params.filters).length > 0 && (
            <Alert
              message="筛选条件"
              description={
                <Descriptions column={2} size="small">
                  {Object.entries(report.params.filters).map(([key, value]) => (
                    <Descriptions.Item key={key} label={key}>
                      {Array.isArray(value) ? value.join(', ') : String(value)}
                    </Descriptions.Item>
                  ))}
                </Descriptions>
              }
              type="success"
              style={{ marginBottom: '16px' }}
            />
          )}

          {report.error && (
            <Alert
              message="错误信息"
              description={report.error}
              type="error"
              style={{ marginBottom: '16px' }}
            />
          )}

          <div style={{ 
            border: '1px solid #d9d9d9', 
            borderRadius: '6px',
            padding: '16px',
            backgroundColor: '#fafafa',
            textAlign: 'center',
            minHeight: '200px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            <div>
              {formatIcon}
              <div style={{ marginTop: '8px', fontSize: '16px', fontWeight: 'bold' }}>
                {report.templateName}
              </div>
              <div style={{ marginTop: '4px', color: '#666' }}>
                {report.params.format.toUpperCase()} 格式报表
              </div>
              {report.status === 'completed' && report.downloadUrl && (
                <div style={{ marginTop: '16px' }}>
                  <Text type="success">报表已生成完成，点击下载按钮获取文件</Text>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </Modal>
  );
};

export default ReportPreview;