import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Input,
  Space,
  Modal,
  Form,
  message,
  Popconfirm,
  Select,
  DatePicker,
  Tag,
  Card,
  Row,
  Col,
  Tooltip,
  Badge,
  InputNumber,
  Switch,
  Statistic
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
  PhoneOutlined,
  UserOutlined,
  DollarOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  SoundOutlined
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { registrationService } from '../services/registrationService';
import { patientService } from '../services/patientService';
import type { 
  Registration, 
  CreateRegistrationRequest 
} from '../types/appointment';
import {
  REGISTRATION_TYPES,
  REGISTRATION_STATUSES,
  PAYMENT_STATUSES,
  PAYMENT_METHODS,
  DEPARTMENTS
} from '../types/appointment';

const { Search } = Input;
const { Option } = Select;
const { RangePicker } = DatePicker;

const RegistrationManagement: React.FC = () => {
  const [registrations, setRegistrations] = useState<Registration[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRegistration, setEditingRegistration] = useState<Registration | null>(null);
  const [form] = Form.useForm();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [statusFilter, setStatusFilter] = useState<string | undefined>();
  const [departmentFilter, setDepartmentFilter] = useState<string | undefined>();
  const [paymentStatusFilter, setPaymentStatusFilter] = useState<string | undefined>();
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs] | null>(null);
  const [patients, setPatients] = useState<any[]>([]);
  const [statistics, setStatistics] = useState<any>({});

  useEffect(() => {
    fetchRegistrations();
    fetchPatients();
    fetchStatistics();
  }, []);

  useEffect(() => {
    fetchRegistrations();
  }, [searchKeyword, statusFilter, departmentFilter, paymentStatusFilter, dateRange]);

  const fetchRegistrations = async () => {
    setLoading(true);
    try {
      const params: any = {};
      
      if (searchKeyword) {
        params.keyword = searchKeyword;
      }
      if (statusFilter) {
        params.status = statusFilter;
      }
      if (departmentFilter) {
        params.department = departmentFilter;
      }
      if (paymentStatusFilter) {
        params.paymentStatus = paymentStatusFilter;
      }
      if (dateRange) {
        params.startDate = dateRange[0].format('YYYY-MM-DD');
        params.endDate = dateRange[1].format('YYYY-MM-DD');
      }

      const response = await registrationService.getAllRegistrations(params);
      if (response.success && response.data) {
        setRegistrations(response.data);
      }
    } catch (error) {
      message.error('获取挂号列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchPatients = async () => {
    try {
      const response = await patientService.getAllPatients();
      if (response.success && response.data) {
        setPatients(response.data);
      }
    } catch (error) {
      console.error('获取患者列表失败:', error);
    }
  };

  const fetchStatistics = async () => {
    try {
      const response = await registrationService.getRegistrationStatistics();
      if (response.success && response.data) {
        setStatistics(response.data);
      }
    } catch (error) {
      console.error('获取统计信息失败:', error);
    }
  };

  const handleCreateRegistration = () => {
    setEditingRegistration(null);
    form.resetFields();
    form.setFieldsValue({
      registrationDate: dayjs(),
      registrationType: '普通门诊',
      priority: 3,
      registrationFee: 10,
      isFirstVisit: true,
      isEmergency: false
    });
    setIsModalVisible(true);
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      
      const createRequest: CreateRegistrationRequest = {
        patientId: values.patientId,
        registrationDate: values.registrationDate.format('YYYY-MM-DD'),
        department: values.department,
        doctorName: values.doctorName,
        registrationType: values.registrationType,
        priority: values.priority,
        registrationFee: values.registrationFee,
        consultationFee: values.consultationFee,
        chiefComplaint: values.chiefComplaint,
        presentIllness: values.presentIllness,
        isFirstVisit: values.isFirstVisit,
        isEmergency: values.isEmergency,
        remarks: values.remarks
      };
      
      const response = await registrationService.createRegistration(createRequest);
      if (response.success) {
        message.success('挂号创建成功');
        fetchRegistrations();
        fetchStatistics();
        setIsModalVisible(false);
      } else {
        message.error(response.message || '挂号创建失败');
      }
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const handleCallPatient = async (registration: Registration) => {
    try {
      const response = await registrationService.callPatient(registration.id);
      if (response.success) {
        message.success('叫号成功');
        fetchRegistrations();
        fetchStatistics();
      } else {
        message.error(response.message || '叫号失败');
      }
    } catch (error) {
      message.error('叫号失败');
    }
  };

  const handleMarkArrived = async (registration: Registration) => {
    try {
      const response = await registrationService.markPatientArrived(registration.id);
      if (response.success) {
        message.success('患者到达标记成功');
        fetchRegistrations();
        fetchStatistics();
      } else {
        message.error(response.message || '标记失败');
      }
    } catch (error) {
      message.error('标记失败');
    }
  };

  const handleStartConsultation = async (registration: Registration) => {
    try {
      const response = await registrationService.startConsultation(registration.id);
      if (response.success) {
        message.success('就诊开始');
        fetchRegistrations();
        fetchStatistics();
      } else {
        message.error(response.message || '操作失败');
      }
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleCompleteConsultation = async (registration: Registration) => {
    try {
      const response = await registrationService.completeConsultation(registration.id);
      if (response.success) {
        message.success('就诊完成');
        fetchRegistrations();
        fetchStatistics();
      } else {
        message.error(response.message || '操作失败');
      }
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleMarkPaid = (registration: Registration) => {
    Modal.confirm({
      title: '标记支付',
      content: (
        <div>
          <p>确定标记挂号 "{registration.registrationNumber}" 为已支付吗？</p>
          <p>费用总计：¥{registration.totalFee?.toFixed(2)}</p>
        </div>
      ),
      onOk: async () => {
        try {
          const response = await registrationService.markAsPaid(registration.id, '现金');
          if (response.success) {
            message.success('支付标记成功');
            fetchRegistrations();
            fetchStatistics();
          } else {
            message.error(response.message || '标记失败');
          }
        } catch (error) {
          message.error('标记失败');
        }
      }
    });
  };

  const handleCancelRegistration = (registration: Registration) => {
    Modal.confirm({
      title: '取消挂号',
      content: '请输入取消原因：',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await registrationService.cancelRegistration(registration.id, '用户取消');
          if (response.success) {
            message.success('挂号取消成功');
            fetchRegistrations();
            fetchStatistics();
          } else {
            message.error(response.message || '取消失败');
          }
        } catch (error) {
          message.error('取消失败');
        }
      }
    });
  };

  const getStatusColor = (status: string) => {
    const statusConfig = REGISTRATION_STATUSES.find(s => s.value === status);
    return statusConfig?.color || 'default';
  };

  const getPaymentStatusColor = (status: string) => {
    const statusConfig = PAYMENT_STATUSES.find(s => s.value === status);
    return statusConfig?.color || 'default';
  };

  const columns: ColumnsType<Registration> = [
    {
      title: '挂号编号',
      dataIndex: 'registrationNumber',
      key: 'registrationNumber',
      width: 120,
      fixed: 'left'
    },
    {
      title: '患者信息',
      key: 'patient',
      width: 120,
      render: (_, record) => (
        <div>
          <div>{record.patientName || `患者${record.patientId}`}</div>
          <div style={{ fontSize: '12px', color: '#666' }}>
            队列: {record.queueNumber || '-'}
          </div>
        </div>
      )
    },
    {
      title: '挂号日期',
      dataIndex: 'registrationDate',
      key: 'registrationDate',
      width: 100,
      render: (date: string) => dayjs(date).format('MM-DD')
    },
    {
      title: '科室',
      dataIndex: 'department',
      key: 'department',
      width: 100
    },
    {
      title: '医生',
      dataIndex: 'doctorName',
      key: 'doctorName',
      width: 100,
      render: (name: string) => name || '-'
    },
    {
      title: '挂号类型',
      dataIndex: 'registrationType',
      key: 'registrationType',
      width: 100
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => (
        <Tag color={getStatusColor(status)}>{status}</Tag>
      )
    },
    {
      title: '费用',
      key: 'fee',
      width: 100,
      render: (_, record) => (
        <div>
          <div>¥{record.totalFee?.toFixed(2) || '0.00'}</div>
          <Tag color={getPaymentStatusColor(record.paymentStatus)} size="small">
            {record.paymentStatus}
          </Tag>
        </div>
      )
    },
    {
      title: '主诉',
      dataIndex: 'chiefComplaint',
      key: 'chiefComplaint',
      width: 150,
      ellipsis: true
    },
    {
      title: '标识',
      key: 'flags',
      width: 80,
      render: (_, record) => (
        <Space direction="vertical" size="small">
          {record.isFirstVisit && <Tag color="blue" size="small">初诊</Tag>}
          {record.isEmergency && <Tag color="red" size="small">急诊</Tag>}
        </Space>
      )
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          {record.status === '已挂号' && (
            <Tooltip title="叫号">
              <Button
                type="link"
                icon={<SoundOutlined />}
                onClick={() => handleCallPatient(record)}
                size="small"
                style={{ color: '#1890ff' }}
              />
            </Tooltip>
          )}
          
          {record.status === '已叫号' && (
            <Tooltip title="标记到达">
              <Button
                type="link"
                icon={<UserOutlined />}
                onClick={() => handleMarkArrived(record)}
                size="small"
                style={{ color: '#52c41a' }}
              />
            </Tooltip>
          )}
          
          {record.status === '已到达' && (
            <Tooltip title="开始就诊">
              <Button
                type="link"
                icon={<ClockCircleOutlined />}
                onClick={() => handleStartConsultation(record)}
                size="small"
                style={{ color: '#722ed1' }}
              />
            </Tooltip>
          )}
          
          {record.status === '就诊中' && (
            <Tooltip title="完成就诊">
              <Button
                type="link"
                icon={<CheckCircleOutlined />}
                onClick={() => handleCompleteConsultation(record)}
                size="small"
                style={{ color: '#52c41a' }}
              />
            </Tooltip>
          )}
          
          {record.paymentStatus === '未支付' && (
            <Tooltip title="标记支付">
              <Button
                type="link"
                icon={<DollarOutlined />}
                onClick={() => handleMarkPaid(record)}
                size="small"
                style={{ color: '#faad14' }}
              />
            </Tooltip>
          )}
          
          {(record.status === '已挂号' || record.status === '已叫号') && (
            <Tooltip title="取消挂号">
              <Popconfirm
                title="确定取消这个挂号吗？"
                onConfirm={() => handleCancelRegistration(record)}
                okText="确定"
                cancelText="取消"
              >
                <Button
                  type="link"
                  danger
                  icon={<CloseCircleOutlined />}
                  size="small"
                />
              </Popconfirm>
            </Tooltip>
          )}
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="今日挂号"
              value={statistics.todayCount || 0}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="待叫号"
              value={statistics.pendingCount || 0}
              prefix={<ClockCircleOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="正在就诊"
              value={statistics.activeConsultationCount || 0}
              prefix={<SoundOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="未支付"
              value={statistics.unpaidCount || 0}
              prefix={<DollarOutlined />}
              valueStyle={{ color: '#ff4d4f' }}
            />
          </Card>
        </Col>
      </Row>

      <Card>
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={5}>
            <Search
              placeholder="搜索挂号编号或患者信息"
              onSearch={setSearchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              allowClear
            />
          </Col>
          <Col span={3}>
            <Select
              placeholder="选择状态"
              allowClear
              style={{ width: '100%' }}
              onChange={setStatusFilter}
            >
              {REGISTRATION_STATUSES.map(status => (
                <Option key={status.value} value={status.value}>
                  {status.label}
                </Option>
              ))}
            </Select>
          </Col>
          <Col span={3}>
            <Select
              placeholder="选择科室"
              allowClear
              style={{ width: '100%' }}
              onChange={setDepartmentFilter}
            >
              {DEPARTMENTS.map(dept => (
                <Option key={dept.value} value={dept.value}>
                  {dept.label}
                </Option>
              ))}
            </Select>
          </Col>
          <Col span={3}>
            <Select
              placeholder="支付状态"
              allowClear
              style={{ width: '100%' }}
              onChange={setPaymentStatusFilter}
            >
              {PAYMENT_STATUSES.map(status => (
                <Option key={status.value} value={status.value}>
                  {status.label}
                </Option>
              ))}
            </Select>
          </Col>
          <Col span={5}>
            <RangePicker
              style={{ width: '100%' }}
              onChange={(dates) => setDateRange(dates as [dayjs.Dayjs, dayjs.Dayjs] | null)}
              placeholder={['开始日期', '结束日期']}
            />
          </Col>
          <Col span={5}>
            <Space>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={handleCreateRegistration}
              >
                新增挂号
              </Button>
              <Button
                icon={<ReloadOutlined />}
                onClick={fetchRegistrations}
              >
                刷新
              </Button>
            </Space>
          </Col>
        </Row>

        <Table
          columns={columns}
          dataSource={registrations}
          loading={loading}
          rowKey="id"
          scroll={{ x: 1500 }}
          pagination={{
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`
          }}
        />
      </Card>

      <Modal
        title="新增挂号"
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        width={800}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="patientId"
            label="选择患者"
            rules={[{ required: true, message: '请选择患者' }]}
          >
            <Select
              placeholder="请选择患者"
              showSearch
              filterOption={(input, option) =>
                (option?.children as unknown as string)?.toLowerCase().includes(input.toLowerCase())
              }
            >
              {patients.map(patient => (
                <Option key={patient.id} value={patient.id}>
                  {patient.name} - {patient.phone || '无手机号'}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="registrationDate"
                label="挂号日期"
                rules={[{ required: true, message: '请选择挂号日期' }]}
              >
                <DatePicker
                  style={{ width: '100%' }}
                  placeholder="请选择挂号日期"
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="registrationType"
                label="挂号类型"
                rules={[{ required: true, message: '请选择挂号类型' }]}
              >
                <Select placeholder="请选择挂号类型">
                  {REGISTRATION_TYPES.map(type => (
                    <Option key={type.value} value={type.value}>
                      {type.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="department"
                label="科室"
                rules={[{ required: true, message: '请选择科室' }]}
              >
                <Select placeholder="请选择科室">
                  {DEPARTMENTS.map(dept => (
                    <Option key={dept.value} value={dept.value}>
                      {dept.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="doctorName"
                label="医生姓名"
              >
                <Input placeholder="请输入医生姓名" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item
                name="registrationFee"
                label="挂号费"
                rules={[{ required: true, message: '请输入挂号费' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  placeholder="请输入挂号费"
                  min={0}
                  precision={2}
                  addonAfter="元"
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="consultationFee"
                label="诊疗费"
              >
                <InputNumber
                  style={{ width: '100%' }}
                  placeholder="请输入诊疗费"
                  min={0}
                  precision={2}
                  addonAfter="元"
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="priority"
                label="优先级"
              >
                <Select placeholder="请选择优先级">
                  <Option value={1}>最低</Option>
                  <Option value={2}>较低</Option>
                  <Option value={3}>普通</Option>
                  <Option value={4}>较高</Option>
                  <Option value={5}>最高</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            name="chiefComplaint"
            label="主诉"
          >
            <Input.TextArea
              rows={3}
              placeholder="请输入主诉"
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Form.Item
            name="presentIllness"
            label="现病史"
          >
            <Input.TextArea
              rows={3}
              placeholder="请输入现病史"
              maxLength={1000}
              showCount
            />
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="isFirstVisit"
                label="是否初诊"
                valuePropName="checked"
              >
                <Switch checkedChildren="是" unCheckedChildren="否" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="isEmergency"
                label="是否急诊"
                valuePropName="checked"
              >
                <Switch checkedChildren="是" unCheckedChildren="否" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            name="remarks"
            label="备注"
          >
            <Input.TextArea
              rows={2}
              placeholder="请输入备注信息"
              maxLength={1000}
              showCount
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default RegistrationManagement;