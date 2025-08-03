import React, { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Tag,
  Badge,
  message,
  Modal,
  Select,
  Input,
  Row,
  Col,
  Typography,
  Divider,
  Alert,
  Tooltip,
  Statistic,
  Form,
  DatePicker
} from 'antd';
import {
  FileTextOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  StopOutlined,
  ClockCircleOutlined,
  UserOutlined,
  ExclamationCircleOutlined,
  CheckCircleOutlined,
  WarningOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { medicalOrderService } from '../services/medicalOrderService';
import { patientService } from '../services/patientService';
import {
  MedicalOrder,
  ExecuteMedicalOrderRequest,
  ORDER_STATUS_CONFIG,
  ORDER_PRIORITY_CONFIG,
  ORDER_TYPE_CONFIG,
  ROUTE_OPTIONS,
  FREQUENCY_OPTIONS,
  INJECTION_SITE_OPTIONS
} from '../types/medicalOrder';

const { Title, Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;
const { confirm } = Modal;

interface Patient {
  id: number;
  name: string;
  phone: string;
  patientNumber: string;
}

const MedicalOrderManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
  const [pendingOrders, setPendingOrders] = useState<MedicalOrder[]>([]);
  const [executedOrders, setExecutedOrders] = useState<MedicalOrder[]>([]);
  const [activeTab, setActiveTab] = useState<'pending' | 'executed'>('pending');
  const [executeModalVisible, setExecuteModalVisible] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState<MedicalOrder | null>(null);
  const [executeForm] = Form.useForm();
  const [postponeModalVisible, setPostponeModalVisible] = useState(false);
  const [postponeReason, setPostponeReason] = useState('');

  // 统计数据
  const statistics = React.useMemo(() => {
    const stats = {
      totalPending: pendingOrders.length,
      urgentPending: pendingOrders.filter(order => order.priority === 'URGENT').length,
      totalExecuted: executedOrders.length,
      todayExecuted: executedOrders.filter(order => 
        dayjs(order.executedAt).isSame(dayjs(), 'day')
      ).length
    };
    return stats;
  }, [pendingOrders, executedOrders]);

  // 加载患者列表
  useEffect(() => {
    loadPatients();
  }, []);

  const loadPatients = async () => {
    try {
      const data = await patientService.getAllPatients();
      setPatients(data);
    } catch (error) {
      console.error('加载患者列表失败:', error);
      message.error('加载患者列表失败');
    }
  };

  // 加载医嘱数据
  const loadMedicalOrders = useCallback(async () => {
    if (!selectedPatient) return;

    try {
      setLoading(true);
      const [pending, executed] = await Promise.all([
        medicalOrderService.getPendingOrders(selectedPatient.id),
        medicalOrderService.getExecutedOrders(selectedPatient.id)
      ]);
      setPendingOrders(pending);
      setExecutedOrders(executed);
    } catch (error) {
      console.error('加载医嘱数据失败:', error);
      message.error('加载医嘱数据失败');
    } finally {
      setLoading(false);
    }
  }, [selectedPatient]);

  // 选择患者时加载医嘱
  useEffect(() => {
    if (selectedPatient) {
      loadMedicalOrders();
    }
  }, [selectedPatient, loadMedicalOrders]);

  // 选择患者
  const handlePatientSelect = (patientId: number) => {
    const patient = patients.find(p => p.id === patientId);
    setSelectedPatient(patient || null);
  };

  // 执行医嘱
  const handleExecuteOrder = (order: MedicalOrder) => {
    setSelectedOrder(order);
    setExecuteModalVisible(true);
    executeForm.resetFields();
    
    // 根据医嘱类型预设表单字段
    if (order.orderType === 'INJECTION') {
      executeForm.setFieldsValue({
        actualRoute: order.route || '静脉注射',
        actualDosage: order.dosage
      });
    }
  };

  // 确认执行医嘱
  const handleConfirmExecute = async (values: ExecuteMedicalOrderRequest) => {
    if (!selectedOrder) return;

    try {
      await medicalOrderService.executeOrder(selectedOrder.id, values);
      message.success('医嘱执行成功');
      setExecuteModalVisible(false);
      setSelectedOrder(null);
      executeForm.resetFields();
      loadMedicalOrders();
    } catch (error) {
      console.error('执行医嘱失败:', error);
      message.error('执行医嘱失败');
    }
  };

  // 暂缓医嘱
  const handlePostponeOrder = (order: MedicalOrder) => {
    setSelectedOrder(order);
    setPostponeModalVisible(true);
    setPostponeReason('');
  };

  // 确认暂缓医嘱
  const handleConfirmPostpone = async () => {
    if (!selectedOrder || !postponeReason.trim()) {
      message.warning('请填写暂缓原因');
      return;
    }

    try {
      await medicalOrderService.postponeOrder(selectedOrder.id, postponeReason);
      message.success('医嘱已暂缓');
      setPostponeModalVisible(false);
      setSelectedOrder(null);
      setPostponeReason('');
      loadMedicalOrders();
    } catch (error) {
      console.error('暂缓医嘱失败:', error);
      message.error('暂缓医嘱失败');
    }
  };

  // 取消医嘱
  const handleCancelOrder = (order: MedicalOrder) => {
    confirm({
      title: '确认取消医嘱',
      content: `确定要取消医嘱"${order.content}"吗？`,
      icon: <ExclamationCircleOutlined />,
      onOk: async () => {
        try {
          await medicalOrderService.cancelOrder(order.id, '护士取消');
          message.success('医嘱已取消');
          loadMedicalOrders();
        } catch (error) {
          console.error('取消医嘱失败:', error);
          message.error('取消医嘱失败');
        }
      }
    });
  };

  // 待执行医嘱表格列定义
  const pendingColumns: ColumnsType<MedicalOrder> = [
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
      title: '剂量/频次',
      key: 'dosageFrequency',
      width: 120,
      render: (_, record) => (
        <div>
          {record.dosage && <div>{record.dosage}</div>}
          {record.frequency && <div style={{ color: '#666', fontSize: '12px' }}>{record.frequency}</div>}
        </div>
      )
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
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => {
        const config = ORDER_STATUS_CONFIG[status as keyof typeof ORDER_STATUS_CONFIG];
        return (
          <Badge status={config.badge as any} text={config.text} />
        );
      }
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      render: (_, record) => (
        <Space size="small">
          {record.status === 'PENDING' && (
            <>
              <Button
                type="primary"
                size="small"
                icon={<PlayCircleOutlined />}
                onClick={() => handleExecuteOrder(record)}
              >
                执行
              </Button>
              <Button
                size="small"
                icon={<PauseCircleOutlined />}
                onClick={() => handlePostponeOrder(record)}
              >
                暂缓
              </Button>
              <Button
                danger
                size="small"
                icon={<StopOutlined />}
                onClick={() => handleCancelOrder(record)}
              >
                取消
              </Button>
            </>
          )}
          {record.status === 'POSTPONED' && (
            <Button
              type="primary"
              size="small"
              icon={<PlayCircleOutlined />}
              onClick={() => handleExecuteOrder(record)}
            >
              继续执行
            </Button>
          )}
        </Space>
      )
    }
  ];

  // 已执行医嘱表格列定义
  const executedColumns: ColumnsType<MedicalOrder> = [
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
      title: '执行时间',
      dataIndex: 'executedAt',
      key: 'executedAt',
      width: 120,
      render: (time: string) => time ? dayjs(time).format('MM-DD HH:mm') : '-'
    },
    {
      title: '执行备注',
      dataIndex: 'executionNotes',
      key: 'executionNotes',
      width: 150,
      ellipsis: true,
      render: (notes: string) => notes || '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => {
        const config = ORDER_STATUS_CONFIG[status as keyof typeof ORDER_STATUS_CONFIG];
        return (
          <Badge status={config.badge as any} text={config.text} />
        );
      }
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <FileTextOutlined style={{ marginRight: 8 }} />
        医嘱执行管理
      </Title>

      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="待执行医嘱"
              value={statistics.totalPending}
              valueStyle={{ color: '#1890ff' }}
              prefix={<ClockCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="紧急医嘱"
              value={statistics.urgentPending}
              valueStyle={{ color: '#ff4d4f' }}
              prefix={<WarningOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="今日已执行"
              value={statistics.todayExecuted}
              valueStyle={{ color: '#52c41a' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="总已执行"
              value={statistics.totalExecuted}
              valueStyle={{ color: '#722ed1' }}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>
      </Row>

      {/* 患者选择 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16} align="middle">
          <Col span={8}>
            <Text strong>选择患者：</Text>
            <Select
              showSearch
              placeholder="搜索患者姓名或编号"
              style={{ width: '100%', marginLeft: 8 }}
              optionFilterProp="children"
              onChange={handlePatientSelect}
              filterOption={(input, option) =>
                (option?.children as unknown as string)
                  ?.toLowerCase()
                  ?.includes(input.toLowerCase())
              }
            >
              {patients.map(patient => (
                <Option key={patient.id} value={patient.id}>
                  {patient.name} - {patient.patientNumber} - {patient.phone}
                </Option>
              ))}
            </Select>
          </Col>
          <Col>
            <Button
              icon={<ReloadOutlined />}
              onClick={loadMedicalOrders}
              loading={loading}
              disabled={!selectedPatient}
            >
              刷新
            </Button>
          </Col>
        </Row>

        {selectedPatient && (
          <Alert
            message={`当前患者：${selectedPatient.name} (${selectedPatient.patientNumber})`}
            type="info"
            showIcon
            style={{ marginTop: 16 }}
          />
        )}
      </Card>

      {/* 医嘱列表 */}
      {selectedPatient && (
        <Card>
          <div style={{ marginBottom: 16 }}>
            <Space>
              <Button
                type={activeTab === 'pending' ? 'primary' : 'default'}
                onClick={() => setActiveTab('pending')}
              >
                待执行医嘱 ({statistics.totalPending})
              </Button>
              <Button
                type={activeTab === 'executed' ? 'primary' : 'default'}
                onClick={() => setActiveTab('executed')}
              >
                已执行医嘱 ({statistics.totalExecuted})
              </Button>
            </Space>
          </div>

          <Table
            columns={activeTab === 'pending' ? pendingColumns : executedColumns}
            dataSource={activeTab === 'pending' ? pendingOrders : executedOrders}
            rowKey="id"
            loading={loading}
            pagination={{
              pageSize: 10,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total) => `共 ${total} 条记录`
            }}
            scroll={{ x: 1000 }}
            rowClassName={(record) => {
              if (record.priority === 'URGENT') return 'urgent-row';
              return '';
            }}
          />
        </Card>
      )}

      {/* 执行医嘱模态框 */}
      <Modal
        title={`执行医嘱：${selectedOrder?.content}`}
        open={executeModalVisible}
        onCancel={() => {
          setExecuteModalVisible(false);
          setSelectedOrder(null);
          executeForm.resetFields();
        }}
        footer={null}
        width={600}
      >
        {selectedOrder && (
          <Form
            form={executeForm}
            layout="vertical"
            onFinish={handleConfirmExecute}
          >
            {/* 医嘱基本信息 */}
            <Alert
              message="医嘱信息"
              description={
                <div>
                  <div><strong>类型：</strong>{ORDER_TYPE_CONFIG[selectedOrder.orderType as keyof typeof ORDER_TYPE_CONFIG]?.text || selectedOrder.orderType}</div>
                  <div><strong>内容：</strong>{selectedOrder.content}</div>
                  {selectedOrder.dosage && <div><strong>剂量：</strong>{selectedOrder.dosage}</div>}
                  {selectedOrder.frequency && <div><strong>频次：</strong>{selectedOrder.frequency}</div>}
                  {selectedOrder.route && <div><strong>途径：</strong>{selectedOrder.route}</div>}
                </div>
              }
              type="info"
              style={{ marginBottom: 16 }}
            />

            {/* 注射类医嘱的特殊字段 */}
            {selectedOrder.orderType === 'INJECTION' && (
              <>
                <Form.Item label="实际给药途径" name="actualRoute">
                  <Select placeholder="选择给药途径">
                    {ROUTE_OPTIONS.map(route => (
                      <Option key={route.value} value={route.value}>
                        {route.label}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>

                <Form.Item label="注射部位" name="injectionSite">
                  <Select placeholder="选择注射部位">
                    {INJECTION_SITE_OPTIONS.map(site => (
                      <Option key={site.value} value={site.value}>
                        {site.label}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>

                <Form.Item label="实际剂量" name="actualDosage">
                  <Input placeholder="输入实际使用剂量" />
                </Form.Item>
              </>
            )}

            {/* 测量类医嘱的特殊字段 */}
            {selectedOrder.orderType === 'EXAMINATION' && (
              <Form.Item label="测量结果" name="measurementResult">
                <TextArea
                  rows={3}
                  placeholder="记录测量结果..."
                />
              </Form.Item>
            )}

            {/* 通用字段 */}
            <Form.Item label="异常情况" name="abnormalSituation">
              <TextArea
                rows={2}
                placeholder="如有异常情况请记录..."
              />
            </Form.Item>

            <Form.Item label="执行备注" name="executionNotes">
              <TextArea
                rows={3}
                placeholder="记录执行过程中的注意事项..."
              />
            </Form.Item>

            <Form.Item>
              <Space>
                <Button type="primary" htmlType="submit">
                  确认执行
                </Button>
                <Button onClick={() => {
                  setExecuteModalVisible(false);
                  setSelectedOrder(null);
                  executeForm.resetFields();
                }}>
                  取消
                </Button>
              </Space>
            </Form.Item>
          </Form>
        )}
      </Modal>

      {/* 暂缓医嘱模态框 */}
      <Modal
        title="暂缓医嘱"
        open={postponeModalVisible}
        onOk={handleConfirmPostpone}
        onCancel={() => {
          setPostponeModalVisible(false);
          setSelectedOrder(null);
          setPostponeReason('');
        }}
      >
        <div style={{ marginBottom: 16 }}>
          <Text strong>医嘱内容：</Text>
          <Text>{selectedOrder?.content}</Text>
        </div>
        <div>
          <Text strong>暂缓原因：</Text>
          <TextArea
            rows={3}
            value={postponeReason}
            onChange={(e) => setPostponeReason(e.target.value)}
            placeholder="请说明暂缓执行的原因..."
            style={{ marginTop: 8 }}
          />
        </div>
      </Modal>

      <style jsx>{`
        .urgent-row {
          background-color: #fff2f0 !important;
        }
      `}</style>
    </div>
  );
};

export default MedicalOrderManagement;