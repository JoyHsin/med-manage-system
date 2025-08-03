import React, { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Form,
  Input,
  Button,
  Select,
  Row,
  Col,
  Typography,
  message,
  Space,
  Divider,
  Table,
  Tag,
  Modal,
  Alert,
  Tabs,
  List,
  Tooltip,
  Badge,
  InputNumber,
  Checkbox,
  Popconfirm,
  Drawer,
  Descriptions,
  Statistic,
  AutoComplete
} from 'antd';
import {
  FileTextOutlined,
  PlusOutlined,
  SaveOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  CopyOutlined,
  ExportOutlined,
  SearchOutlined,
  WarningOutlined,
  ClockCircleOutlined,
  UserOutlined,
  BookOutlined,
  ThunderboltOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { medicalOrderService } from '../services/medicalOrderService';
import { patientService } from '../services/patientService';
import {
  MedicalOrder,
  CreateMedicalOrderRequest,
  MedicalOrderTemplate,
  CreateOrderTemplateRequest,
  ORDER_STATUS_CONFIG,
  ORDER_PRIORITY_CONFIG,
  ORDER_TYPE_CONFIG,
  ROUTE_OPTIONS,
  FREQUENCY_OPTIONS,
  UNIT_OPTIONS,
  COMMON_ORDER_TEMPLATES
} from '../types/medicalOrder';

const { Title, Text, Paragraph } = Typography;
const { Option } = Select;
const { TextArea } = Input;
const { TabPane } = Tabs;
const { confirm } = Modal;

interface Patient {
  id: number;
  name: string;
  phone: string;
  patientNumber: string;
  gender: string;
  age: number;
}

const MedicalOrderCreation: React.FC = () => {
  const [form] = Form.useForm();
  const [templateForm] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [medicalOrders, setMedicalOrders] = useState<MedicalOrder[]>([]);
  const [templates, setTemplates] = useState<MedicalOrderTemplate[]>([]);
  const [currentOrder, setCurrentOrder] = useState<MedicalOrder | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [activeTab, setActiveTab] = useState('list');
  const [templateModalVisible, setTemplateModalVisible] = useState(false);
  const [detailDrawerVisible, setDetailDrawerVisible] = useState(false);
  const [batchCreateVisible, setBatchCreateVisible] = useState(false);
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
  const [stats, setStats] = useState<any>(null);
  const [contentSuggestions, setContentSuggestions] = useState<string[]>([]);

  // 当前医生ID（实际应用中从认证状态获取）
  const currentDoctorId = 1;

  // 加载初始数据
  useEffect(() => {
    loadPatients();
    loadMedicalOrders();
    loadTemplates();
    loadStats();
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

  const loadMedicalOrders = async () => {
    try {
      setLoading(true);
      const data = await medicalOrderService.getDoctorOrders(currentDoctorId);
      setMedicalOrders(data);
    } catch (error) {
      console.error('加载医嘱列表失败:', error);
      message.error('加载医嘱列表失败');
    } finally {
      setLoading(false);
    }
  };

  const loadTemplates = async () => {
    try {
      const data = await medicalOrderService.getOrderTemplates(currentDoctorId);
      setTemplates(data);
    } catch (error) {
      console.error('加载医嘱模板失败:', error);
    }
  };

  const loadStats = async () => {
    try {
      const data = await medicalOrderService.getOrderStats(currentDoctorId);
      setStats(data);
    } catch (error) {
      console.error('加载统计数据失败:', error);
    }
  };

  // 选择患者
  const handlePatientSelect = (patientId: number) => {
    const patient = patients.find(p => p.id === patientId);
    setSelectedPatient(patient || null);
  };

  // 新建医嘱
  const handleNewOrder = () => {
    setCurrentOrder(null);
    setIsEditing(true);
    setActiveTab('form');
    form.resetFields();
    form.setFieldsValue({
      priority: 'NORMAL'
    });
  };

  // 编辑医嘱
  const handleEditOrder = (order: MedicalOrder) => {
    if (order.status !== 'PENDING') {
      message.warning('只能编辑待执行状态的医嘱');
      return;
    }
    setCurrentOrder(order);
    setIsEditing(true);
    setActiveTab('form');
    form.setFieldsValue(order);
  };

  // 查看医嘱详情
  const handleViewOrder = (order: MedicalOrder) => {
    setCurrentOrder(order);
    setDetailDrawerVisible(true);
  };

  // 保存医嘱
  const handleSaveOrder = async (values: any) => {
    try {
      setLoading(true);
      const requestData: CreateMedicalOrderRequest = values;

      // 验证医嘱数据
      const validation = medicalOrderService.validateOrder(requestData);
      if (!validation.valid) {
        message.error(validation.errors[0]);
        return;
      }

      // 检查医嘱冲突
      const conflictCheck = await medicalOrderService.checkOrderConflicts(
        requestData.patientId,
        requestData.orderType,
        requestData.content
      );
      if (conflictCheck.hasConflicts) {
        const conflictMessages = conflictCheck.conflicts.map(c => c.reason).join(', ');
        message.warning(`检测到医嘱冲突: ${conflictMessages}`);
      }

      if (currentOrder) {
        await medicalOrderService.updateMedicalOrder(currentOrder.id, requestData);
        message.success('医嘱更新成功');
      } else {
        await medicalOrderService.createMedicalOrder(requestData);
        message.success('医嘱创建成功');
      }

      setIsEditing(false);
      setActiveTab('list');
      loadMedicalOrders();
      loadStats();
    } catch (error) {
      console.error('保存医嘱失败:', error);
      message.error('保存医嘱失败');
    } finally {
      setLoading(false);
    }
  };

  // 取消医嘱
  const handleCancelOrder = async (order: MedicalOrder) => {
    Modal.confirm({
      title: '取消医嘱',
      content: (
        <div>
          <p>确认取消此医嘱吗？</p>
          <TextArea
            placeholder="取消原因"
            rows={3}
            id="cancelReason"
            required
          />
        </div>
      ),
      onOk: async () => {
        try {
          const reason = (document.getElementById('cancelReason') as HTMLTextAreaElement)?.value;
          if (!reason) {
            message.error('请输入取消原因');
            return;
          }
          await medicalOrderService.cancelOrder(order.id, reason);
          message.success('医嘱取消成功');
          loadMedicalOrders();
          loadStats();
        } catch (error) {
          console.error('取消医嘱失败:', error);
          message.error('取消医嘱失败');
        }
      }
    });
  };

  // 复制医嘱
  const handleCopyOrder = async (order: MedicalOrder) => {
    try {
      const newOrder = await medicalOrderService.copyOrder(order.id);
      message.success('医嘱复制成功');
      handleEditOrder(newOrder);
    } catch (error) {
      console.error('复制医嘱失败:', error);
      message.error('复制医嘱失败');
    }
  };

  // 删除医嘱
  const handleDeleteOrder = async (order: MedicalOrder) => {
    if (order.status !== 'PENDING') {
      message.warning('只能删除待执行状态的医嘱');
      return;
    }
    try {
      await medicalOrderService.deleteMedicalOrder(order.id);
      message.success('医嘱删除成功');
      loadMedicalOrders();
      loadStats();
    } catch (error) {
      console.error('删除医嘱失败:', error);
      message.error('删除医嘱失败');
    }
  };

  // 搜索医嘱
  const handleSearch = async (values: any) => {
    try {
      setLoading(true);
      const data = await medicalOrderService.searchMedicalOrders(
        values.keyword,
        values.patientId,
        currentDoctorId,
        values.orderType,
        values.status,
        values.priority,
        values.startDate?.format('YYYY-MM-DD'),
        values.endDate?.format('YYYY-MM-DD')
      );
      setMedicalOrders(data);
    } catch (error) {
      console.error('搜索医嘱失败:', error);
      message.error('搜索医嘱失败');
    } finally {
      setLoading(false);
    }
  };

  // 医嘱类型变化时更新内容建议
  const handleOrderTypeChange = (orderType: string) => {
    const suggestions = COMMON_ORDER_TEMPLATES[orderType as keyof typeof COMMON_ORDER_TEMPLATES] || [];
    setContentSuggestions(suggestions);
  };

  // 从模板创建医嘱
  const handleCreateFromTemplate = async (template: MedicalOrderTemplate) => {
    if (!selectedPatient) {
      message.warning('请先选择患者');
      return;
    }
    try {
      const newOrder = await medicalOrderService.createFromTemplate(template.id, selectedPatient.id);
      message.success('从模板创建医嘱成功');
      handleEditOrder(newOrder);
    } catch (error) {
      console.error('从模板创建医嘱失败:', error);
      message.error('从模板创建医嘱失败');
    }
  };

  // 保存为模板
  const handleSaveAsTemplate = async (values: any) => {
    try {
      const requestData: CreateOrderTemplateRequest = values;
      await medicalOrderService.createOrderTemplate(requestData);
      message.success('模板保存成功');
      setTemplateModalVisible(false);
      templateForm.resetFields();
      loadTemplates();
    } catch (error) {
      console.error('保存模板失败:', error);
      message.error('保存模板失败');
    }
  };

  // 医嘱列表表格列定义
  const orderColumns: ColumnsType<MedicalOrder> = [
    {
      title: '患者',
      dataIndex: 'patientName',
      key: 'patientName',
      width: 100,
      render: (name: string) => name || '患者' + (Math.floor(Math.random() * 100) + 1)
    },
    {
      title: '医嘱类型',
      dataIndex: 'orderType',
      key: 'orderType',
      width: 120,
      render: (type: string) => {
        const config = ORDER_TYPE_CONFIG[type as keyof typeof ORDER_TYPE_CONFIG];
        return (
          <Tag color={config.color}>
            {config.icon} {config.text}
          </Tag>
        );
      }
    },
    {
      title: '医嘱内容',
      dataIndex: 'content',
      key: 'content',
      width: 200,
      ellipsis: true,
      render: (content: string) => (
        <Tooltip title={content}>
          <Text>{content}</Text>
        </Tooltip>
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
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => {
        const config = ORDER_STATUS_CONFIG[status as keyof typeof ORDER_STATUS_CONFIG];
        return (
          <Badge status={config.badge as any} text={config.text} />
        );
      }
    },
    {
      title: '开具时间',
      dataIndex: 'prescribedAt',
      key: 'prescribedAt',
      width: 120,
      render: (date: string) => dayjs(date).format('MM-DD HH:mm')
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewOrder(record)}
          >
            查看
          </Button>
          {record.status === 'PENDING' && (
            <>
              <Button
                type="link"
                size="small"
                icon={<EditOutlined />}
                onClick={() => handleEditOrder(record)}
              >
                编辑
              </Button>
              <Button
                type="link"
                size="small"
                danger
                icon={<CloseCircleOutlined />}
                onClick={() => handleCancelOrder(record)}
              >
                取消
              </Button>
            </>
          )}
          <Button
            type="link"
            size="small"
            icon={<CopyOutlined />}
            onClick={() => handleCopyOrder(record)}
          >
            复制
          </Button>
          {record.status === 'PENDING' && (
            <Popconfirm
              title="确定删除此医嘱吗？"
              onConfirm={() => handleDeleteOrder(record)}
            >
              <Button
                type="link"
                size="small"
                danger
                icon={<DeleteOutlined />}
              >
                删除
              </Button>
            </Popconfirm>
          )}
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <FileTextOutlined style={{ marginRight: 8 }} />
        医嘱开具
      </Title>

      {/* 统计卡片 */}
      {stats && (
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={4}>
            <Card>
              <Statistic title="总医嘱数" value={stats.totalOrders} />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="待执行" 
                value={stats.pendingOrders}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="已执行" 
                value={stats.executedOrders}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="暂缓执行" 
                value={stats.postponedOrders}
                valueStyle={{ color: '#faad14' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="已取消" 
                value={stats.cancelledOrders}
                valueStyle={{ color: '#f5222d' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="紧急医嘱" 
                value={stats.urgentOrders}
                valueStyle={{ color: '#f5222d' }}
                prefix={<ThunderboltOutlined />}
              />
            </Card>
          </Col>
        </Row>
      )}

      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        {/* 医嘱列表 */}
        <TabPane tab="医嘱列表" key="list">
          <Card
            title="医嘱列表"
            extra={
              <Space>
                <Button
                  type="primary"
                  icon={<PlusOutlined />}
                  onClick={handleNewOrder}
                >
                  新建医嘱
                </Button>
                <Button
                  icon={<BookOutlined />}
                  onClick={() => setActiveTab('templates')}
                >
                  医嘱模板
                </Button>
                <Button
                  icon={<ExportOutlined />}
                  onClick={() => {
                    // 导出功能
                    message.info('导出功能开发中');
                  }}
                >
                  导出
                </Button>
              </Space>
            }
          >
            {/* 搜索表单 */}
            <Form
              form={searchForm}
              layout="inline"
              onFinish={handleSearch}
              style={{ marginBottom: 16 }}
            >
              <Form.Item name="keyword">
                <Input
                  placeholder="搜索医嘱内容"
                  prefix={<SearchOutlined />}
                  style={{ width: 200 }}
                />
              </Form.Item>
              <Form.Item name="patientId">
                <Select
                  placeholder="选择患者"
                  style={{ width: 150 }}
                  showSearch
                  optionFilterProp="children"
                  allowClear
                >
                  {patients.map(patient => (
                    <Option key={patient.id} value={patient.id}>
                      {patient.name}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item name="orderType">
                <Select placeholder="医嘱类型" style={{ width: 120 }} allowClear>
                  <Option value="INJECTION">注射</Option>
                  <Option value="ORAL_MEDICATION">口服药物</Option>
                  <Option value="EXAMINATION">检查</Option>
                  <Option value="NURSING_CARE">护理</Option>
                  <Option value="TREATMENT">治疗</Option>
                  <Option value="OBSERVATION">观察</Option>
                </Select>
              </Form.Item>
              <Form.Item name="status">
                <Select placeholder="状态" style={{ width: 100 }} allowClear>
                  <Option value="PENDING">待执行</Option>
                  <Option value="EXECUTED">已执行</Option>
                  <Option value="POSTPONED">暂缓执行</Option>
                  <Option value="CANCELLED">已取消</Option>
                </Select>
              </Form.Item>
              <Form.Item name="priority">
                <Select placeholder="优先级" style={{ width: 100 }} allowClear>
                  <Option value="URGENT">紧急</Option>
                  <Option value="NORMAL">普通</Option>
                  <Option value="LOW">低</Option>
                </Select>
              </Form.Item>
              <Form.Item>
                <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                  搜索
                </Button>
              </Form.Item>
              <Form.Item>
                <Button
                  onClick={() => {
                    searchForm.resetFields();
                    loadMedicalOrders();
                  }}
                >
                  重置
                </Button>
              </Form.Item>
            </Form>

            <Table
              columns={orderColumns}
              dataSource={medicalOrders}
              rowKey="id"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total) => `共 ${total} 条记录`
              }}
            />
          </Card>
        </TabPane>

        {/* 医嘱表单 */}
        <TabPane tab={currentOrder ? '编辑医嘱' : '新建医嘱'} key="form" disabled={!isEditing}>
          <Card
            title={currentOrder ? '编辑医嘱' : '新建医嘱'}
            extra={
              <Space>
                <Button
                  type="primary"
                  icon={<SaveOutlined />}
                  loading={loading}
                  onClick={() => form.submit()}
                >
                  保存
                </Button>
                <Button
                  onClick={() => {
                    setIsEditing(false);
                    setActiveTab('list');
                  }}
                >
                  取消
                </Button>
              </Space>
            }
          >
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSaveOrder}
            >
              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item
                    label="患者"
                    name="patientId"
                    rules={[{ required: true, message: '请选择患者' }]}
                  >
                    <Select
                      placeholder="选择患者"
                      showSearch
                      optionFilterProp="children"
                      onChange={handlePatientSelect}
                    >
                      {patients.map(patient => (
                        <Option key={patient.id} value={patient.id}>
                          {patient.name} - {patient.patientNumber}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    label="医嘱类型"
                    name="orderType"
                    rules={[{ required: true, message: '请选择医嘱类型' }]}
                  >
                    <Select onChange={handleOrderTypeChange}>
                      <Option value="INJECTION">💉 注射</Option>
                      <Option value="ORAL_MEDICATION">💊 口服药物</Option>
                      <Option value="EXAMINATION">🔍 检查</Option>
                      <Option value="NURSING_CARE">🩺 护理</Option>
                      <Option value="TREATMENT">⚕️ 治疗</Option>
                      <Option value="OBSERVATION">👁️ 观察</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    label="优先级"
                    name="priority"
                    rules={[{ required: true, message: '请选择优先级' }]}
                  >
                    <Select>
                      <Option value="URGENT">🔴 紧急</Option>
                      <Option value="NORMAL">🔵 普通</Option>
                      <Option value="LOW">⚪ 低</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item
                label="医嘱内容"
                name="content"
                rules={[{ required: true, message: '请输入医嘱内容' }]}
              >
                <AutoComplete
                  options={contentSuggestions.map(suggestion => ({ value: suggestion }))}
                  placeholder="请输入医嘱内容..."
                  filterOption={(inputValue, option) =>
                    option!.value.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1
                  }
                >
                  <TextArea
                    rows={3}
                    maxLength={500}
                    showCount
                  />
                </AutoComplete>
              </Form.Item>

              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item label="剂量" name="dosage">
                    <Input placeholder="如：5mg、10ml" maxLength={100} />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="频次" name="frequency">
                    <Select placeholder="选择频次" allowClear>
                      {FREQUENCY_OPTIONS.map(freq => (
                        <Option key={freq.value} value={freq.value}>
                          {freq.label}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="给药途径" name="route">
                    <Select placeholder="选择给药途径" allowClear>
                      {ROUTE_OPTIONS.map(route => (
                        <Option key={route.value} value={route.value}>
                          {route.label}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item label="数量" name="quantity">
                    <InputNumber
                      style={{ width: '100%' }}
                      min={1}
                      placeholder="数量"
                    />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="单位" name="unit">
                    <Select placeholder="选择单位" allowClear>
                      {UNIT_OPTIONS.map(unit => (
                        <Option key={unit.value} value={unit.value}>
                          {unit.label}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="价格" name="price">
                    <InputNumber
                      style={{ width: '100%' }}
                      min={0}
                      precision={2}
                      placeholder="价格"
                      prefix="¥"
                    />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item label="备注" name="notes">
                <TextArea
                  rows={3}
                  placeholder="医嘱备注信息..."
                  maxLength={500}
                  showCount
                />
              </Form.Item>
            </Form>
          </Card>
        </TabPane>

        {/* 医嘱模板 */}
        <TabPane tab="医嘱模板" key="templates">
          <Card
            title="医嘱模板"
            extra={
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => {
                  setTemplateModalVisible(true);
                  templateForm.resetFields();
                }}
              >
                新建模板
              </Button>
            }
          >
            <List
              dataSource={templates}
              renderItem={(template) => (
                <List.Item
                  actions={[
                    <Button
                      type="link"
                      size="small"
                      onClick={() => handleCreateFromTemplate(template)}
                      disabled={!selectedPatient}
                    >
                      使用模板
                    </Button>,
                    <Button
                      type="link"
                      size="small"
                      danger
                      onClick={async () => {
                        try {
                          await medicalOrderService.deleteOrderTemplate(template.id);
                          message.success('模板删除成功');
                          loadTemplates();
                        } catch (error) {
                          message.error('模板删除失败');
                        }
                      }}
                    >
                      删除
                    </Button>
                  ]}
                >
                  <List.Item.Meta
                    title={
                      <Space>
                        <Text strong>{template.name}</Text>
                        <Tag color={ORDER_TYPE_CONFIG[template.orderType].color}>
                          {ORDER_TYPE_CONFIG[template.orderType].text}
                        </Tag>
                      </Space>
                    }
                    description={
                      <div>
                        <div>{template.content}</div>
                        {(template.dosage || template.frequency || template.route) && (
                          <div style={{ marginTop: 4, color: '#666' }}>
                            {template.dosage && `剂量: ${template.dosage}`}
                            {template.frequency && ` | 频次: ${template.frequency}`}
                            {template.route && ` | 途径: ${template.route}`}
                          </div>
                        )}
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        </TabPane>
      </Tabs>

      {/* 新建模板模态框 */}
      <Modal
        title="新建医嘱模板"
        open={templateModalVisible}
        onCancel={() => {
          setTemplateModalVisible(false);
          templateForm.resetFields();
        }}
        footer={null}
        width={600}
      >
        <Form
          form={templateForm}
          layout="vertical"
          onFinish={handleSaveAsTemplate}
        >
          <Form.Item
            label="模板名称"
            name="name"
            rules={[{ required: true, message: '请输入模板名称' }]}
          >
            <Input placeholder="请输入模板名称" />
          </Form.Item>

          <Form.Item
            label="医嘱类型"
            name="orderType"
            rules={[{ required: true, message: '请选择医嘱类型' }]}
          >
            <Select>
              <Option value="INJECTION">💉 注射</Option>
              <Option value="ORAL_MEDICATION">💊 口服药物</Option>
              <Option value="EXAMINATION">🔍 检查</Option>
              <Option value="NURSING_CARE">🩺 护理</Option>
              <Option value="TREATMENT">⚕️ 治疗</Option>
              <Option value="OBSERVATION">👁️ 观察</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="医嘱内容"
            name="content"
            rules={[{ required: true, message: '请输入医嘱内容' }]}
          >
            <TextArea
              rows={3}
              placeholder="请输入医嘱内容..."
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item label="剂量" name="dosage">
                <Input placeholder="如：5mg、10ml" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item label="频次" name="frequency">
                <Select placeholder="选择频次" allowClear>
                  {FREQUENCY_OPTIONS.map(freq => (
                    <Option key={freq.value} value={freq.value}>
                      {freq.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item label="给药途径" name="route">
                <Select placeholder="选择给药途径" allowClear>
                  {ROUTE_OPTIONS.map(route => (
                    <Option key={route.value} value={route.value}>
                      {route.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item label="数量" name="quantity">
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="数量"
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item label="单位" name="unit">
                <Select placeholder="选择单位" allowClear>
                  {UNIT_OPTIONS.map(unit => (
                    <Option key={unit.value} value={unit.value}>
                      {unit.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item label="价格" name="price">
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={2}
                  placeholder="价格"
                  prefix="¥"
                />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item label="备注" name="notes">
            <TextArea
              rows={2}
              placeholder="模板备注..."
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                保存模板
              </Button>
              <Button
                onClick={() => {
                  setTemplateModalVisible(false);
                  templateForm.resetFields();
                }}
              >
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {/* 医嘱详情抽屉 */}
      <Drawer
        title="医嘱详情"
        placement="right"
        width={600}
        open={detailDrawerVisible}
        onClose={() => setDetailDrawerVisible(false)}
      >
        {currentOrder && (
          <div>
            <Descriptions column={2} bordered>
              <Descriptions.Item label="患者">
                {currentOrder.patientName || '患者' + currentOrder.patientId}
              </Descriptions.Item>
              <Descriptions.Item label="医嘱类型">
                <Tag color={ORDER_TYPE_CONFIG[currentOrder.orderType].color}>
                  {ORDER_TYPE_CONFIG[currentOrder.orderType].icon} {ORDER_TYPE_CONFIG[currentOrder.orderType].text}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="优先级">
                <Tag color={ORDER_PRIORITY_CONFIG[currentOrder.priority].color}>
                  {ORDER_PRIORITY_CONFIG[currentOrder.priority].text}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="状态">
                <Badge 
                  status={ORDER_STATUS_CONFIG[currentOrder.status].badge as any} 
                  text={ORDER_STATUS_CONFIG[currentOrder.status].text} 
                />
              </Descriptions.Item>
              <Descriptions.Item label="开具时间" span={2}>
                {dayjs(currentOrder.prescribedAt).format('YYYY-MM-DD HH:mm:ss')}
              </Descriptions.Item>
              {currentOrder.executedAt && (
                <Descriptions.Item label="执行时间" span={2}>
                  {dayjs(currentOrder.executedAt).format('YYYY-MM-DD HH:mm:ss')}
                </Descriptions.Item>
              )}
            </Descriptions>

            <div style={{ marginTop: 16 }}>
              <Title level={5}>医嘱内容</Title>
              <Paragraph>{currentOrder.content}</Paragraph>
            </div>

            {(currentOrder.dosage || currentOrder.frequency || currentOrder.route) && (
              <div style={{ marginTop: 16 }}>
                <Title level={5}>用药信息</Title>
                <Descriptions column={1} size="small">
                  {currentOrder.dosage && (
                    <Descriptions.Item label="剂量">{currentOrder.dosage}</Descriptions.Item>
                  )}
                  {currentOrder.frequency && (
                    <Descriptions.Item label="频次">{currentOrder.frequency}</Descriptions.Item>
                  )}
                  {currentOrder.route && (
                    <Descriptions.Item label="给药途径">{currentOrder.route}</Descriptions.Item>
                  )}
                  {currentOrder.quantity && (
                    <Descriptions.Item label="数量">
                      {currentOrder.quantity} {currentOrder.unit}
                    </Descriptions.Item>
                  )}
                  {currentOrder.price && (
                    <Descriptions.Item label="价格">¥{currentOrder.price}</Descriptions.Item>
                  )}
                </Descriptions>
              </div>
            )}

            {currentOrder.notes && (
              <div style={{ marginTop: 16 }}>
                <Title level={5}>备注</Title>
                <Paragraph>{currentOrder.notes}</Paragraph>
              </div>
            )}

            {currentOrder.executionNotes && (
              <div style={{ marginTop: 16 }}>
                <Title level={5}>执行备注</Title>
                <Paragraph>{currentOrder.executionNotes}</Paragraph>
              </div>
            )}

            {currentOrder.cancelReason && (
              <div style={{ marginTop: 16 }}>
                <Title level={5}>取消原因</Title>
                <Paragraph type="danger">{currentOrder.cancelReason}</Paragraph>
              </div>
            )}
          </div>
        )}
      </Drawer>
    </div>
  );
};

export default MedicalOrderCreation;