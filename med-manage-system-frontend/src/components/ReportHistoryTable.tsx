import React from 'react';
import {
  Table,
  Button,
  Space,
  Tag,
  Tooltip,
  Progress,
  Card,
  Typography,
  Popconfirm
} from 'antd';
import {
  DownloadOutlined,
  EyeOutlined,
  DeleteOutlined,
  ReloadOutlined,
  ShareAltOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { GeneratedReport } from './ReportGenerator';
import dayjs from 'dayjs';

const { Title } = Typography;

interface ReportHistoryTableProps {
  reports: GeneratedReport[];
  loading: boolean;
  onDownload: (report: GeneratedReport) => void;
  onPreview: (report: GeneratedReport) => void;
  onDelete: (reportId: string) => void;
  onRefresh: () => void;
}

const ReportHistoryTable: React.FC<ReportHistoryTableProps> = ({
  reports,
  loading,
  onDownload,
  onPreview,
  onDelete,
  onRefresh
}) => {
  const columns: ColumnsType<GeneratedReport> = [
    {
      title: '报表名称',
      dataIndex: 'templateName',
      key: 'templateName',
      render: (name, record) => (
        <div>
          <div style={{ fontWeight: 'bold' }}>{name}</div>
          <div style={{ fontSize: '12px', color: '#666' }}>
            ID: {record.id}
          </div>
        </div>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status: string, record) => {
        const statusConfig = {
          generating: { color: 'processing', text: '生成中' },
          completed: { color: 'success', text: '已完成' },
          failed: { color: 'error', text: '生成失败' }
        };
        const config = statusConfig[status as keyof typeof statusConfig];
        
        return (
          <div>
            <Tag color={config.color}>{config.text}</Tag>
            {status === 'generating' && (
              <Progress 
                percent={record.progress} 
                size="small" 
                style={{ marginTop: 4 }}
              />
            )}
            {status === 'failed' && record.error && (
              <Tooltip title={record.error}>
                <div style={{ fontSize: '12px', color: '#ff4d4f', marginTop: 2 }}>
                  {record.error.length > 20 ? 
                    `${record.error.substring(0, 20)}...` : 
                    record.error
                  }
                </div>
              </Tooltip>
            )}
          </div>
        );
      }
    },
    {
      title: '文件大小',
      dataIndex: 'fileSize',
      key: 'fileSize',
      width: 100,
      render: (size?: number) => {
        if (!size) return '-';
        return size > 1024 * 1024 
          ? `${(size / (1024 * 1024)).toFixed(1)} MB`
          : `${(size / 1024).toFixed(1)} KB`;
      }
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 160,
      render: (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: '操作',
      key: 'actions',
      width: 180,
      render: (_, record) => (
        <Space size="small">
          {record.status === 'completed' && (
            <>
              <Tooltip title="预览">
                <Button
                  type="text"
                  icon={<EyeOutlined />}
                  onClick={() => onPreview(record)}
                />
              </Tooltip>
              <Tooltip title="下载">
                <Button
                  type="text"
                  icon={<DownloadOutlined />}
                  onClick={() => onDownload(record)}
                />
              </Tooltip>
              <Tooltip title="分享">
                <Button
                  type="text"
                  icon={<ShareAltOutlined />}
                  onClick={() => {
                    // TODO: 实现分享功能
                    console.log('分享报表:', record.id);
                  }}
                />
              </Tooltip>
            </>
          )}
          <Popconfirm
            title="确定要删除这个报表吗？"
            onConfirm={() => onDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Tooltip title="删除">
              <Button
                type="text"
                danger
                icon={<DeleteOutlined />}
              />
            </Tooltip>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <Card
      title={
        <Space>
          <Title level={4} style={{ margin: 0 }}>报表历史</Title>
        </Space>
      }
      extra={
        <Button 
          icon={<ReloadOutlined />}
          onClick={onRefresh}
          loading={loading}
        >
          刷新
        </Button>
      }
    >
      <Table
        dataSource={reports}
        columns={columns}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => 
            `第 ${range[0]}-${range[1]} 条/共 ${total} 条`
        }}
        size="small"
      />
    </Card>
  );
};

export default ReportHistoryTable;