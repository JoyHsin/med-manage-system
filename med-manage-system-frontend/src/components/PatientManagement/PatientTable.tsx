import React from 'react';
import { Table, Space, Button, Tooltip, Popconfirm, Tag, Badge } from 'antd';
import {
  EditOutlined,
  DeleteOutlined,
  StarOutlined,
  StarFilled,
  HistoryOutlined,
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import type { Patient } from '../../services/patientService';

interface PatientTableProps {
  patients: Patient[];
  loading: boolean;
  onEdit: (patient: Patient) => void;
  onDelete: (patientId: number) => void;
  onToggleVip: (patient: Patient) => void;
  onViewHistory?: (patient: Patient) => void;
}

const PatientTable: React.FC<PatientTableProps> = ({
  patients,
  loading,
  onEdit,
  onDelete,
  onToggleVip,
  onViewHistory,
}) => {
  const columns: ColumnsType<Patient> = [
    {
      title: '患者编号',
      dataIndex: 'patientNumber',
      key: 'patientNumber',
      width: 120,
      fixed: 'left'
    },
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 100,
      render: (text: string, record: Patient) => (
        <Space>
          {text}
          {record.isVip && <StarFilled style={{ color: '#faad14' }} />}
        </Space>
      )
    },
    {
      title: '性别',
      dataIndex: 'gender',
      key: 'gender',
      width: 60
    },
    {
      title: '年龄',
      dataIndex: 'age',
      key: 'age',
      width: 60
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
      width: 130
    },
    {
      title: '血型',
      dataIndex: 'bloodType',
      key: 'bloodType',
      width: 70
    },
    {
      title: '医保类型',
      dataIndex: 'insuranceType',
      key: 'insuranceType',
      width: 100
    },
    {
      title: '就诊次数',
      dataIndex: 'visitCount',
      key: 'visitCount',
      width: 80,
      render: (count: number) => <Badge count={count} />
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => {
        const color = status === '正常' ? 'green' : status === '黑名单' ? 'red' : 'orange';
        return <Tag color={color}>{status}</Tag>;
      }
    },
    {
      title: '最后就诊',
      dataIndex: 'lastVisitTime',
      key: 'lastVisitTime',
      width: 120,
      render: (date: string) => date ? dayjs(date).format('YYYY-MM-DD') : '-'
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Tooltip title="编辑">
            <Button
              type="link"
              icon={<EditOutlined />}
              onClick={() => onEdit(record)}
              size="small"
            />
          </Tooltip>
          <Tooltip title={record.isVip ? '取消VIP' : '设为VIP'}>
            <Button
              type="link"
              icon={record.isVip ? <StarFilled /> : <StarOutlined />}
              onClick={() => onToggleVip(record)}
              size="small"
              style={{ color: record.isVip ? '#faad14' : undefined }}
            />
          </Tooltip>
          {onViewHistory && (
            <Tooltip title="病史">
              <Button
                type="link"
                icon={<HistoryOutlined />}
                onClick={() => onViewHistory(record)}
                size="small"
              />
            </Tooltip>
          )}
          <Tooltip title="删除">
            <Popconfirm
              title="确定删除这个患者吗？"
              onConfirm={() => onDelete(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <Button
                type="link"
                danger
                icon={<DeleteOutlined />}
                size="small"
              />
            </Popconfirm>
          </Tooltip>
        </Space>
      )
    }
  ];

  return (
    <Table
      columns={columns}
      dataSource={patients}
      loading={loading}
      rowKey="id"
      scroll={{ x: 1500 }}
      pagination={{
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total) => `共 ${total} 条记录`
      }}
    />
  );
};

export default PatientTable;