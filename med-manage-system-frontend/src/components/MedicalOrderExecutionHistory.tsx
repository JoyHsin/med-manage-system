import React, { useState, useEffect } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Tag,
  Typography,
  Modal,
  Descriptions,
  Timeline,
  Statistic,
  Row,
  Col,
  DatePicker,
  Select,
  Input
} from 'antd';
import {
  HistoryOutlined,
  EyeOutlined,
  FileTextOutlined,
  ClockCircleOutlined,
  UserOutlined,
  CheckCircleOutlined,
  WarningOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import {
  MedicalOrder,
  ORDER_STATUS_CONFIG,
  ORDER_PRIORITY_CONFIG,
  ORDER_TYPE_CONFIG
} from '../types/medicalOrder';

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;
const { Option } = Select;

interface MedicalOrderExecutionHistoryProps {
  patientId?: number;
  orders: MedicalOrder[];
  onRefresh: () => void;
}

const MedicalOrderExecutionHistory: React.FC<MedicalOrderExecutionHistoryProps> = ({
  patientId,
  orders,
  onRefresh
}) => {
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState<MedicalOrder | null>(null);
  const [filteredOrders, setFilteredOrders] = useState<MedicalOrder[]>(orders);
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs] | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [typeFilter, setTypeFilter] = useState<string>('ALL');
  const [searchText, setSearchText] = useState('');

  // 更新过滤后的医嘱列表
  useEffect(() => {
    let filtered = [...orders];

    // 日期范围过滤
    if (dateRange) {
      filtered = filtered.filter(order => {
        const orderDate = dayjs(order.executedAt || order.prescribedAt);
        return orderDate.isAfter(dateRange[0]) && orderDate.isBefore(dateRange[1]);
      });
    }

    // 状态过滤
    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(order => order.status === statusFilter);
    }

    // 类型过滤
    if (typeFilter !== 'ALL') {
      filtered = filtered.filter(order => order.orderType === typeFilter);
    }

    // 文本搜索
    if (searchText) {
      const searchLower = searchText.toLowerCase();
      filtered = filtered.filter(order =>
        order.content.toLowerCase().includes(searchLower) ||
        (order.executionNotes && order.executionNotes.toLowerCase().includes(searchLower))
      );
    }

    setFilteredOrders(filtered);
  }, [orders, dateRange, statusFilter, typeFilter, searchText]);

  // 统计数据
  const statistics = React.useMemo(() => {
    const executed = filteredOrders.filter(order => order.status === 'EXECUTED');
    const postponed = filteredOrders.filter(order => order.status === 'POSTPONED');
    const cancelled = filteredOrders.filter(order => order.status === 'CANCELLED');
    
    return {
      total: filteredOrders.length,
      executed: executed.length,
      postponed: postponed.length,
      cancelled: cancelled.length,
      executionRate: filteredOrders.length > 0 ? Math.round((executed.length / filteredOrders.length) * 100) : 0
    };
  }, [filteredOrders]);

  // 查看医嘱详情
  const handleViewDetail = (order: MedicalOrder) => {
    setSelectedOrder(order);
    setDetailModalVisible(true);
  };

  // 表格列定义
  const columns: ColumnsType<MedicalOrder> = [
    {
      title: '医嘱类型',
      dataIndex: 'orderType',
      key: 'orderType',
      width: 100,
      render: (orderType: string) => {
        const config = ORDER_TYPE_CONFIG[orderType as keyof typeof ORDER_TYPE_CONFIG];
        return config ? (
          <Tag color={config.color}>
            {config.icon} {config.text}
          </Tag>
        ) : orderType;
      }
    },
    {
      title: '医嘱内容',
      dataIndex: 'content',
      key: 'content',
      width: 200,
      ellipsis: true
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      width: 80,
      render: (priority: string) => {
        const config = ORDER_PRIORITY_CONFIG[priority as keyof typeof ORDER_PRIORITY_CONFIG];
        return (
          <Tag color={config.color}>{config.text}</Tag>
        );
      }
    },
    {
      title: '开具时间',
      dataIndex: 'prescribedAt',
      key: 'prescribedAt',
      width: 120,
      render: (time: string) => dayjs(time).format('MM-DD HH:mm')
    },
    {
      title: '执行时间',
      dataIndex: 'executedAt',
      key: 'executedAt',
      width: 120,
      render: (time: string) => time ? dayjs(time).format('MM-DD HH:mm') : '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => {
        const config = ORDER_STATUS_CONFIG[status as keyof typeof ORDER_STATUS_CONFIG];
        return (
          <Tag color={config.color}>{config.text}</Tag>
        );
      }
    },
    {
      title: '执行人员',
      dataIndex: 'executedBy',
      key: 'executedBy',
      width: 100,
      render: (executedBy: number) => executedBy ? `护士${executedBy}` : '-'
    },
    {
      title: '操作',
      key: 'actions',
      width: 100,
      render: (_, record) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => handleViewDetail(record)}
        >
          详情
        </Button>
      )
    }
  ];

  // 渲染医嘱执行时间线
  const renderExecutionTimeline = (order: MedicalOrder) => {
    const timelineItems = [];

    // 开具医嘱
    timelineItems.push({
      color: 'blue',
      dot: <FileTextOutlined />,
      children: (
        <div>
          <div><strong>医嘱开具</strong></div>
          <div>时间: {dayjs(order.prescribedAt).format('YYYY-MM-DD HH:mm:ss')}</div>
          <div>医生: 医生{order.prescribedBy}</div>
        </div>
      )
    });

    // 暂缓执行
    if (order.status === 'POSTPONED' && order.postponedAt) {
      timelineItems.push({
        color: 'orange',
        dot: <WarningOutlined />,
        children: (
          <div>
            <div><strong>暂缓执行</strong></div>
            <div>时间: {dayjs(order.postponedAt).format('YYYY-MM-DD HH:mm:ss')}</div>
            <div>执行人: 护士{order.postponedBy}</div>
            <div>原因: {order.postponeReason}</div>
          </div>
        )
      });
    }

    // 执行医嘱
    if (order.status === 'EXECUTED' && order.executedAt) {
      timelineItems.push({
        color: 'green',
        dot: <CheckCircleOutlined />,
        children: (
          <div>
            <div><strong>执行完成</strong></div>
            <div>时间: {dayjs(order.executedAt).format('YYYY-MM-DD HH:mm:ss')}</div>
            <div>执行人: 护士{order.executedBy}</div>
            {order.executionNotes && <div>备注: {order.executionNotes}</div>}
          </div>
        )
      });
    }

    // 取消医嘱
    if (order.status === 'CANCELLED' && order.cancelledAt) {
      timelineItems.push({
        color: 'red',
        dot: <ClockCircleOutlined />,
        children: (
          <div>
            <div><strong>医嘱取消</strong></div>
            <div>时间: {dayjs(order.cancelledAt).format('YYYY-MM-DD HH:mm:ss')}</div>
            <div>取消人: 护士{order.cancelledBy}</div>
            <div>原因: {order.cancelReason}</div>
          </div>
        )
      });
    }

    return <Timeline items={timelineItems} />;
  };

  return (
    <div>
      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="总医嘱数"
              value={statistics.total}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="已执行"
              value={statistics.executed}
              valueStyle={{ color: '#52c41a' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="执行率"
              value={statistics.executionRate}
              suffix="%"
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="异常医嘱"
              value={statistics.postponed + statistics.cancelled}
              valueStyle={{ color: '#ff4d4f' }}
              prefix={<WarningOutlined />}
            />
          </Card>
        </Col>
      </Row>

      {/* 筛选条件 */}
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16} align="middle">
          <Col span={6}>
            <Text strong>时间范围：</Text>
            <RangePicker
              value={dateRange}
              onChange={setDateRange}
              style={{ width: '100%', marginLeft: 8 }}
            />
          </Col>
          <Col span={4}>
            <Text strong>状态：</Text>
            <Select
              value={statusFilter}
              onChange={setStatusFilter}
              style={{ width: '100%', marginLeft: 8 }}
            >
              <Option value="ALL">全部</Option>
              <Option value="PENDING">待执行</Option>
              <Option value="EXECUTED">已执行</Option>
              <Option value="POSTPONED">暂缓执行</Option>
              <Option value="CANCELLED">已取消</Option>
            </Select>
          </Col>
          <Col span={4}>
            <Text strong>类型：</Text>
            <Select
              value={typeFilter}
              onChange={setTypeFilter}
              style={{ width: '100%', marginLeft: 8 }}
            >
              <Option value="ALL">全部</Option>
              <Option value="INJECTION">注射</Option>
              <Option value="ORAL_MEDICATION">口服药物</Option>
              <Option value="EXAMINATION">检查</Option>
              <Option value="NURSING_CARE">护理</Option>
              <Option value="TREATMENT">治疗</Option>
              <Option value="OBSERVATION">观察</Option>
            </Select>
          </Col>
          <Col span={6}>
            <Text strong>搜索：</Text>
            <Input.Search
              placeholder="医嘱内容或执行备注"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              style={{ marginLeft: 8 }}
              allowClear
            />
          </Col>
          <Col span={4}>
            <Button onClick={onRefresh}>
              刷新数据
            </Button>
          </Col>
        </Row>
      </Card>

      {/* 执行记录表格 */}
      <Card title={<><HistoryOutlined style={{ marginRight: 8 }} />执行记录</>}>
        <Table
          columns={columns}
          dataSource={filteredOrders}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`
          }}
          scroll={{ x: 1000 }}
        />
      </Card>

      {/* 医嘱详情模态框 */}
      <Modal
        title="医嘱执行详情"
        open={detailModalVisible}
        onCancel={() => {
          setDetailModalVisible(false);
          setSelectedOrder(null);
        }}
        footer={null}
        width={800}
      >
        {selectedOrder && (
          <div>
            {/* 基本信息 */}
            <Descriptions title="基本信息" bordered column={2}>
              <Descriptions.Item label="医嘱类型">
                <Tag color={ORDER_TYPE_CONFIG[selectedOrder.orderType as keyof typeof ORDER_TYPE_CONFIG]?.color}>
                  {ORDER_TYPE_CONFIG[selectedOrder.orderType as keyof typeof ORDER_TYPE_CONFIG]?.text || selectedOrder.orderType}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="优先级">
                <Tag color={ORDER_PRIORITY_CONFIG[selectedOrder.priority as keyof typeof ORDER_PRIORITY_CONFIG].color}>
                  {ORDER_PRIORITY_CONFIG[selectedOrder.priority as keyof typeof ORDER_PRIORITY_CONFIG].text}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="医嘱内容" span={2}>
                {selectedOrder.content}
              </Descriptions.Item>
              {selectedOrder.dosage && (
                <Descriptions.Item label="剂量">
                  {selectedOrder.dosage}
                </Descriptions.Item>
              )}
              {selectedOrder.frequency && (
                <Descriptions.Item label="频次">
                  {selectedOrder.frequency}
                </Descriptions.Item>
              )}
              {selectedOrder.route && (
                <Descriptions.Item label="给药途径">
                  {selectedOrder.route}
                </Descriptions.Item>
              )}
              <Descriptions.Item label="状态">
                <Tag color={ORDER_STATUS_CONFIG[selectedOrder.status as keyof typeof ORDER_STATUS_CONFIG].color}>
                  {ORDER_STATUS_CONFIG[selectedOrder.status as keyof typeof ORDER_STATUS_CONFIG].text}
                </Tag>
              </Descriptions.Item>
              {selectedOrder.notes && (
                <Descriptions.Item label="医嘱备注" span={2}>
                  {selectedOrder.notes}
                </Descriptions.Item>
              )}
              {selectedOrder.executionNotes && (
                <Descriptions.Item label="执行备注" span={2}>
                  {selectedOrder.executionNotes}
                </Descriptions.Item>
              )}
            </Descriptions>

            {/* 执行时间线 */}
            <div style={{ marginTop: 24 }}>
              <Title level={4}>执行时间线</Title>
              {renderExecutionTimeline(selectedOrder)}
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default MedicalOrderExecutionHistory;