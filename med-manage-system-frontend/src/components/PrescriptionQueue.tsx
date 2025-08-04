import React, { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Tag,
  Badge,
  Typography,
  message,
  Row,
  Col,
  Statistic,
  Select,
  Input,
  DatePicker,
  Tooltip,
  Modal,
  Descriptions,
  List,
  Alert,
  Progress,
  Divider
} from 'antd';
import {
  ClockCircleOutlined,
  UserOutlined,
  MedicineBoxOutlined,
  SearchOutlined,
  ReloadOutlined,
  ExclamationCircleOutlined,
  CheckCircleOutlined,
  EyeOutlined,
  FilterOutlined,
  SortAscendingOutlined,
  WarningOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { pharmacyService } from '../services/pharmacyService';

const { Title, Text } = Typography;
const { Option } = Select;
const { RangePicker } = DatePicker;

interface PrescriptionQueueProps {
  onStartDispensing?: (prescription: any) => void;
  onViewDetails?: (prescription: any) => void;
}

const PrescriptionQueue: React.FC<PrescriptionQueueProps> = ({
  onStartDispensing,
  onViewDetails
}) => {
  const [loading, setLoading] = useState(false);
  const [prescriptions, setPrescriptions] = useState<any[]>([]);
  const [filteredPrescriptions, setFilteredPrescriptions] = useState<any[]>([]);
  const [stats, setStats] = useState<any>(null);
  const [selectedPrescription, setSelectedPrescription] = useState<any>(null);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  
  // 筛选和排序状态
  const [filters, setFilters] = useState({
    priority: '',
    doctor: '',
    patient: '',
    dateRange: null as any,
    status: 'pending'
  });
  const [sortConfig, setSortConfig] = useState({
    field: 'prescribedAt',
    order: 'asc' as 'asc' | 'desc'
  });

  // 当前药师信息（实际应用中从认证状态获取）
  const currentPharmacistId = 1;
  const currentPharmacistName = '药师张三';

  // 加载数据
  useEffect(() => {
    loadPrescriptions();
    loadStats();
    
    // 设置自动刷新
    const interval = setInterval(loadPrescriptions, 30000); // 30秒刷新一次
    return () => clearInterval(interval);
  }, []);

  // 应用筛选和排序
  useEffect(() => {
    applyFiltersAndSort();
  }, [prescriptions, filters, sortConfig]);

  const loadPrescriptions = async () => {
    try {
      setLoading(true);
      const data = await pharmacyService.getPendingPrescriptions();
      setPrescriptions(data);
    } catch (error) {
      console.error('加载处方队列失败:', error);
      message.error('加载处方队列失败');
    } finally {
      setLoading(false);
    }
  };

  const loadStats = async () => {
    try {
      const data = await pharmacyService.getPharmacyStats();
      setStats(data);
    } catch (error) {
      console.error('加载统计数据失败:', error);
    }
  };

  const applyFiltersAndSort = () => {
    let filtered = [...prescriptions];

    // 应用筛选
    if (filters.priority) {
      filtered = filtered.filter(p => p.priority === filters.priority);
    }
    if (filters.doctor) {
      filtered = filtered.filter(p => 
        p.doctorName?.toLowerCase().includes(filters.doctor.toLowerCase())
      );
    }
    if (filters.patient) {
      filtered = filtered.filter(p => 
        p.patientName?.toLowerCase().includes(filters.patient.toLowerCase())
      );
    }
    if (filters.dateRange && filters.dateRange.length === 2) {
      const [start, end] = filters.dateRange;
      filtered = filtered.filter(p => {
        const prescribedDate = dayjs(p.prescribedAt);
        return prescribedDate.isAfter(start.startOf('day')) && 
               prescribedDate.isBefore(end.endOf('day'));
      });
    }

    // 应用排序
    filtered.sort((a, b) => {
      let aValue, bValue;
      
      switch (sortConfig.field) {
        case 'prescribedAt':
          aValue = new Date(a.prescribedAt).getTime();
          bValue = new Date(b.prescribedAt).getTime();
          break;
        case 'priority':
          const priorityOrder = { 'urgent': 3, 'high': 2, 'normal': 1, 'low': 0 };
          aValue = priorityOrder[a.priority as keyof typeof priorityOrder] || 0;
          bValue = priorityOrder[b.priority as keyof typeof priorityOrder] || 0;
          break;
        case 'waitingTime':
          aValue = calculateWaitingTime(a.prescribedAt);
          bValue = calculateWaitingTime(b.prescribedAt);
          break;
        case 'itemCount':
          aValue = a.prescriptionItems?.length || 0;
          bValue = b.prescriptionItems?.length || 0;
          break;
        default:
          aValue = a[sortConfig.field];
          bValue = b[sortConfig.field];
      }

      if (sortConfig.order === 'asc') {
        return aValue > bValue ? 1 : -1;
      } else {
        return aValue < bValue ? 1 : -1;
      }
    });

    setFilteredPrescriptions(filtered);
  };

  const calculateWaitingTime = (prescribedAt: string): number => {
    const now = new Date();
    const prescribed = new Date(prescribedAt);
    return Math.floor((now.getTime() - prescribed.getTime()) / (1000 * 60)); // 分钟
  };

  const getPriorityConfig = (priority: string) => {
    const configs = {
      urgent: { color: 'red', text: '紧急', icon: <ExclamationCircleOutlined /> },
      high: { color: 'orange', text: '高', icon: <WarningOutlined /> },
      normal: { color: 'blue', text: '普通', icon: <ClockCircleOutlined /> },
      low: { color: 'default', text: '低', icon: <ClockCircleOutlined /> }
    };
    return configs[priority as keyof typeof configs] || configs.normal;
  };

  const handleStartDispensing = async (prescription: any) => {
    try {
      // 检查调剂资格
      const eligibility = await pharmacyService.checkDispenseEligibility(prescription.id);
      if (!eligibility.eligible) {
        message.error(`无法调剂: ${eligibility.reason}`);
        return;
      }

      if (eligibility.warnings.length > 0) {
        Modal.warning({
          title: '调剂警告',
          content: (
            <div>
              {eligibility.warnings.map((warning, index) => (
                <div key={index} style={{ marginBottom: 8 }}>
                  <WarningOutlined style={{ color: '#faad14', marginRight: 8 }} />
                  {warning}
                </div>
              ))}
              <p style={{ marginTop: 16 }}>是否继续调剂？</p>
            </div>
          ),
          onOk: () => startDispensing(prescription)
        });
      } else {
        await startDispensing(prescription);
      }
    } catch (error) {
      console.error('开始调剂失败:', error);
      message.error('开始调剂失败');
    }
  };

  const startDispensing = async (prescription: any) => {
    try {
      await pharmacyService.startDispensing({
        prescriptionId: prescription.id,
        pharmacistId: currentPharmacistId,
        pharmacistName: currentPharmacistName
      });
      message.success('开始调剂成功');
      loadPrescriptions();
      
      // 调用外部回调
      if (onStartDispensing) {
        onStartDispensing(prescription);
      }
    } catch (error) {
      console.error('开始调剂失败:', error);
      message.error('开始调剂失败');
    }
  };

  const handleViewDetails = (prescription: any) => {
    setSelectedPrescription(prescription);
    setDetailModalVisible(true);
    
    if (onViewDetails) {
      onViewDetails(prescription);
    }
  };

  const handleSort = (field: string) => {
    setSortConfig(prev => ({
      field,
      order: prev.field === field && prev.order === 'asc' ? 'desc' : 'asc'
    }));
  };

  const resetFilters = () => {
    setFilters({
      priority: '',
      doctor: '',
      patient: '',
      dateRange: null,
      status: 'pending'
    });
  };

  // 表格列定义
  const columns: ColumnsType<any> = [
    {
      title: (
        <Space>
          优先级
          <Button 
            type="text" 
            size="small" 
            icon={<SortAscendingOutlined />}
            onClick={() => handleSort('priority')}
          />
        </Space>
      ),
      dataIndex: 'priority',
      key: 'priority',
      width: 100,
      render: (priority: string) => {
        const config = getPriorityConfig(priority || 'normal');
        return (
          <Tag color={config.color} icon={config.icon}>
            {config.text}
          </Tag>
        );
      }
    },
    {
      title: '处方编号',
      dataIndex: 'prescriptionNumber',
      key: 'prescriptionNumber',
      width: 150,
      render: (text: string) => <Text code>{text}</Text>
    },
    {
      title: '患者',
      dataIndex: 'patientName',
      key: 'patientName',
      width: 120,
      render: (name: string, record: any) => (
        <Space>
          <UserOutlined />
          <span>{name || `患者${record.id}`}</span>
          {record.isVip && <Tag color="gold">VIP</Tag>}
        </Space>
      )
    },
    {
      title: '医生',
      dataIndex: 'doctorName',
      key: 'doctorName',
      width: 100,
      render: (name: string, record: any) => name || `医生${record.doctorId}`
    },
    {
      title: (
        <Space>
          开具时间
          <Button 
            type="text" 
            size="small" 
            icon={<SortAscendingOutlined />}
            onClick={() => handleSort('prescribedAt')}
          />
        </Space>
      ),
      dataIndex: 'prescribedAt',
      key: 'prescribedAt',
      width: 140,
      render: (date: string) => (
        <Tooltip title={dayjs(date).format('YYYY-MM-DD HH:mm:ss')}>
          {dayjs(date).format('MM-DD HH:mm')}
        </Tooltip>
      )
    },
    {
      title: (
        <Space>
          等待时间
          <Button 
            type="text" 
            size="small" 
            icon={<SortAscendingOutlined />}
            onClick={() => handleSort('waitingTime')}
          />
        </Space>
      ),
      key: 'waitingTime',
      width: 100,
      render: (_, record) => {
        const waitingMinutes = calculateWaitingTime(record.prescribedAt);
        const isOverdue = waitingMinutes > 30; // 30分钟为超时
        return (
          <Text type={isOverdue ? 'danger' : 'default'}>
            {waitingMinutes}分钟
          </Text>
        );
      }
    },
    {
      title: (
        <Space>
          药品数量
          <Button 
            type="text" 
            size="small" 
            icon={<SortAscendingOutlined />}
            onClick={() => handleSort('itemCount')}
          />
        </Space>
      ),
      key: 'itemCount',
      width: 100,
      render: (_, record) => (
        <Badge count={record.prescriptionItems?.length || 0} showZero>
          <MedicineBoxOutlined />
        </Badge>
      )
    },
    {
      title: '总金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 100,
      render: (amount: number) => `¥${amount?.toFixed(2) || '0.00'}`
    },
    {
      title: '操作',
      key: 'actions',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="primary"
            size="small"
            icon={<CheckCircleOutlined />}
            onClick={() => handleStartDispensing(record)}
          >
            开始调剂
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewDetails(record)}
          >
            详情
          </Button>
        </Space>
      )
    }
  ];

  return (
    <div>
      {/* 统计卡片 */}
      {stats && (
        <Row gutter={16} style={{ marginBottom: 16 }}>
          <Col span={6}>
            <Card>
              <Statistic 
                title="待调剂处方" 
                value={stats.pendingPrescriptions}
                prefix={<ClockCircleOutlined />}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic 
                title="调剂中" 
                value={stats.inProgressPrescriptions}
                valueStyle={{ color: '#1890ff' }}
                prefix={<MedicineBoxOutlined />}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic 
                title="今日完成" 
                value={stats.todayDispensed}
                valueStyle={{ color: '#52c41a' }}
                prefix={<CheckCircleOutlined />}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic 
                title="平均等待时间" 
                value={Math.round(filteredPrescriptions.reduce((sum, p) => 
                  sum + calculateWaitingTime(p.prescribedAt), 0) / (filteredPrescriptions.length || 1))}
                suffix="分钟"
                valueStyle={{ color: '#faad14' }}
                prefix={<ClockCircleOutlined />}
              />
            </Card>
          </Col>
        </Row>
      )}

      {/* 筛选和搜索 */}
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16} align="middle">
          <Col span={4}>
            <Select
              placeholder="优先级"
              value={filters.priority}
              onChange={(value) => setFilters(prev => ({ ...prev, priority: value }))}
              allowClear
              style={{ width: '100%' }}
            >
              <Option value="urgent">紧急</Option>
              <Option value="high">高</Option>
              <Option value="normal">普通</Option>
              <Option value="low">低</Option>
            </Select>
          </Col>
          <Col span={4}>
            <Input
              placeholder="医生姓名"
              value={filters.doctor}
              onChange={(e) => setFilters(prev => ({ ...prev, doctor: e.target.value }))}
              prefix={<SearchOutlined />}
              allowClear
            />
          </Col>
          <Col span={4}>
            <Input
              placeholder="患者姓名"
              value={filters.patient}
              onChange={(e) => setFilters(prev => ({ ...prev, patient: e.target.value }))}
              prefix={<UserOutlined />}
              allowClear
            />
          </Col>
          <Col span={6}>
            <RangePicker
              value={filters.dateRange}
              onChange={(dates) => setFilters(prev => ({ ...prev, dateRange: dates }))}
              placeholder={['开始日期', '结束日期']}
              style={{ width: '100%' }}
            />
          </Col>
          <Col span={6}>
            <Space>
              <Button 
                icon={<FilterOutlined />} 
                onClick={resetFilters}
              >
                重置筛选
              </Button>
              <Button 
                type="primary" 
                icon={<ReloadOutlined />} 
                onClick={loadPrescriptions}
                loading={loading}
              >
                刷新
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 处方队列表格 */}
      <Card 
        title={
          <Space>
            <MedicineBoxOutlined />
            处方调剂队列
            <Badge count={filteredPrescriptions.length} showZero />
          </Space>
        }
      >
        {/* 队列状态提示 */}
        {filteredPrescriptions.some(p => calculateWaitingTime(p.prescribedAt) > 30) && (
          <Alert
            message="注意"
            description={`有 ${filteredPrescriptions.filter(p => calculateWaitingTime(p.prescribedAt) > 30).length} 个处方等待时间超过30分钟，请优先处理`}
            type="warning"
            showIcon
            style={{ marginBottom: 16 }}
          />
        )}

        <Table
          columns={columns}
          dataSource={filteredPrescriptions}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1200 }}
          pagination={{
            pageSize: 20,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => 
              `第 ${range[0]}-${range[1]} 条，共 ${total} 条记录`
          }}
          rowClassName={(record) => {
            const waitingTime = calculateWaitingTime(record.prescribedAt);
            if (record.priority === 'urgent') return 'urgent-row';
            if (waitingTime > 30) return 'overdue-row';
            return '';
          }}
        />
      </Card>

      {/* 处方详情模态框 */}
      <Modal
        title="处方详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailModalVisible(false)}>
            关闭
          </Button>,
          <Button 
            key="start" 
            type="primary" 
            onClick={() => {
              handleStartDispensing(selectedPrescription);
              setDetailModalVisible(false);
            }}
          >
            开始调剂
          </Button>
        ]}
        width={800}
      >
        {selectedPrescription && (
          <div>
            <Descriptions column={2} bordered>
              <Descriptions.Item label="处方编号">
                {selectedPrescription.prescriptionNumber}
              </Descriptions.Item>
              <Descriptions.Item label="优先级">
                {getPriorityConfig(selectedPrescription.priority || 'normal').text}
              </Descriptions.Item>
              <Descriptions.Item label="患者姓名">
                {selectedPrescription.patientName}
              </Descriptions.Item>
              <Descriptions.Item label="医生姓名">
                {selectedPrescription.doctorName}
              </Descriptions.Item>
              <Descriptions.Item label="开具时间">
                {dayjs(selectedPrescription.prescribedAt).format('YYYY-MM-DD HH:mm:ss')}
              </Descriptions.Item>
              <Descriptions.Item label="等待时间">
                {calculateWaitingTime(selectedPrescription.prescribedAt)}分钟
              </Descriptions.Item>
              <Descriptions.Item label="总金额" span={2}>
                ¥{selectedPrescription.totalAmount?.toFixed(2) || '0.00'}
              </Descriptions.Item>
            </Descriptions>

            <Divider>处方药品</Divider>
            <List
              dataSource={selectedPrescription.prescriptionItems || []}
              renderItem={(item: any, index: number) => (
                <List.Item>
                  <List.Item.Meta
                    title={`${index + 1}. ${item.medicineName || `药品${item.medicineId}`}`}
                    description={
                      <Space direction="vertical" size="small">
                        <Text>数量: {item.quantity} {item.unit || '盒'}</Text>
                        <Text>用法: {item.dosage || '按医嘱使用'}</Text>
                        <Text>单价: ¥{item.unitPrice?.toFixed(2) || '0.00'}</Text>
                      </Space>
                    }
                  />
                  <div>¥{(item.quantity * (item.unitPrice || 0)).toFixed(2)}</div>
                </List.Item>
              )}
            />
          </div>
        )}
      </Modal>

      <style jsx>{`
        .urgent-row {
          background-color: #fff2f0;
          border-left: 4px solid #ff4d4f;
        }
        .overdue-row {
          background-color: #fffbe6;
          border-left: 4px solid #faad14;
        }
      `}</style>
    </div>
  );
};

export default PrescriptionQueue;