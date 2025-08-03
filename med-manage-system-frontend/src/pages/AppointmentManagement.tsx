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
  Badge
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  UserOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  TableOutlined,
  UnorderedListOutlined
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { appointmentService } from '../services/appointmentService';
import { patientService } from '../services/patientService';
import AppointmentForm from '../components/AppointmentForm';
import AppointmentCalendar from '../components/AppointmentCalendar';
import type { 
  Appointment, 
  CreateAppointmentRequest, 
  UpdateAppointmentRequest 
} from '../types/appointment';
import {
  APPOINTMENT_TYPES,
  APPOINTMENT_STATUSES,
  APPOINTMENT_SOURCES,
  DEPARTMENTS,
  PRIORITY_OPTIONS
} from '../types/appointment';

const { Search } = Input;
const { Option } = Select;
const { RangePicker } = DatePicker;

const AppointmentManagement: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState<Appointment | null>(null);
  const [form] = Form.useForm();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [statusFilter, setStatusFilter] = useState<string | undefined>();
  const [departmentFilter, setDepartmentFilter] = useState<string | undefined>();
  const [typeFilter, setTypeFilter] = useState<string | undefined>();
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs] | null>(null);
  const [viewMode, setViewMode] = useState<'table' | 'calendar'>('table');
  useEffect(() => {
    fetchAppointments();
  }, []);

  useEffect(() => {
    fetchAppointments();
  }, [searchKeyword, statusFilter, departmentFilter, typeFilter, dateRange]);

  const fetchAppointments = async () => {
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
      if (typeFilter) {
        params.appointmentType = typeFilter;
      }
      if (dateRange) {
        params.startDate = dateRange[0].format('YYYY-MM-DD');
        params.endDate = dateRange[1].format('YYYY-MM-DD');
      }

      const response = await appointmentService.getAllAppointments(params);
      if (response.success && response.data) {
        setAppointments(response.data);
      }
    } catch (error) {
      message.error('获取预约列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateAppointment = () => {
    setEditingAppointment(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEditAppointment = (appointment: Appointment) => {
    setEditingAppointment(appointment);
    const appointmentDateTime = dayjs(appointment.appointmentTime);
    form.setFieldsValue({
      ...appointment,
      appointmentDate: appointmentDateTime,
      appointmentTime: appointmentDateTime
    });
    setIsModalVisible(true);
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      
      // 合并日期和时间
      const appointmentDateTime = values.appointmentDate
        .hour(values.appointmentTime.hour())
        .minute(values.appointmentTime.minute())
        .second(0);
      
      if (editingAppointment) {
        // 更新预约
        const updateRequest: UpdateAppointmentRequest = {
          appointmentTime: appointmentDateTime.format('YYYY-MM-DD HH:mm:ss'),
          appointmentType: values.appointmentType,
          department: values.department,
          chiefComplaint: values.chiefComplaint,
          notes: values.notes,
          priority: values.priority,
          appointmentFee: values.appointmentFee,
          needReminder: values.needReminder,
          reminderMinutes: values.reminderMinutes
        };
        
        const response = await appointmentService.updateAppointment(editingAppointment.id, updateRequest);
        if (response.success) {
          message.success('预约更新成功');
          fetchAppointments();
          setIsModalVisible(false);
        } else {
          message.error(response.message || '预约更新失败');
        }
      } else {
        // 创建预约
        const createRequest: CreateAppointmentRequest = {
          patientId: values.patientId,
          doctorId: values.doctorId,
          appointmentTime: appointmentDateTime.format('YYYY-MM-DD HH:mm:ss'),
          appointmentType: values.appointmentType,
          department: values.department,
          chiefComplaint: values.chiefComplaint,
          notes: values.notes,
          source: values.source,
          priority: values.priority,
          appointmentFee: values.appointmentFee,
          needReminder: values.needReminder,
          reminderMinutes: values.reminderMinutes
        };
        
        const response = await appointmentService.createAppointment(createRequest);
        if (response.success) {
          message.success('预约创建成功');
          fetchAppointments();
          setIsModalVisible(false);
        } else {
          message.error(response.message || '预约创建失败');
        }
      }
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const handleConfirmAppointment = async (appointment: Appointment) => {
    try {
      const response = await appointmentService.confirmAppointment(appointment.id);
      if (response.success) {
        message.success('预约确认成功');
        fetchAppointments();
      } else {
        message.error(response.message || '预约确认失败');
      }
    } catch (error) {
      message.error('预约确认失败');
    }
  };

  const handleCancelAppointment = (appointment: Appointment) => {
    Modal.confirm({
      title: '取消预约',
      content: '请输入取消原因：',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await appointmentService.cancelAppointment(appointment.id, '用户取消');
          if (response.success) {
            message.success('预约取消成功');
            fetchAppointments();
          } else {
            message.error(response.message || '预约取消失败');
          }
        } catch (error) {
          message.error('预约取消失败');
        }
      }
    });
  };

  const handleMarkArrived = async (appointment: Appointment) => {
    try {
      const response = await appointmentService.markPatientArrived(appointment.id);
      if (response.success) {
        message.success('患者到达标记成功');
        fetchAppointments();
      } else {
        message.error(response.message || '标记失败');
      }
    } catch (error) {
      message.error('标记失败');
    }
  };

  const handleStartConsultation = async (appointment: Appointment) => {
    try {
      const response = await appointmentService.startConsultation(appointment.id);
      if (response.success) {
        message.success('就诊开始');
        fetchAppointments();
      } else {
        message.error(response.message || '操作失败');
      }
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleCompleteConsultation = async (appointment: Appointment) => {
    try {
      const response = await appointmentService.completeConsultation(appointment.id);
      if (response.success) {
        message.success('就诊完成');
        fetchAppointments();
      } else {
        message.error(response.message || '操作失败');
      }
    } catch (error) {
      message.error('操作失败');
    }
  };

  const getStatusColor = (status: string) => {
    const statusConfig = APPOINTMENT_STATUSES.find(s => s.value === status);
    return statusConfig?.color || 'default';
  };

  const getPriorityColor = (priority: number) => {
    const priorityConfig = PRIORITY_OPTIONS.find(p => p.value === priority);
    return priorityConfig?.color || 'default';
  };

  const getPriorityLabel = (priority: number) => {
    const priorityConfig = PRIORITY_OPTIONS.find(p => p.value === priority);
    return priorityConfig?.label || priority.toString();
  };

  const handleCalendarAppointmentClick = (appointment: Appointment) => {
    handleEditAppointment(appointment);
  };

  const handleCalendarDateSelect = (date: dayjs.Dayjs) => {
    // 可以在这里处理日期选择逻辑，比如创建新预约时预设日期
    console.log('Selected date:', date.format('YYYY-MM-DD'));
  };

  const columns: ColumnsType<Appointment> = [
    {
      title: '预约时间',
      dataIndex: 'appointmentTime',
      key: 'appointmentTime',
      width: 150,
      render: (time: string) => (
        <div>
          <div>{dayjs(time).format('MM-DD')}</div>
          <div style={{ fontSize: '12px', color: '#666' }}>
            {dayjs(time).format('HH:mm')}
          </div>
        </div>
      ),
      sorter: (a, b) => dayjs(a.appointmentTime).unix() - dayjs(b.appointmentTime).unix()
    },
    {
      title: '患者信息',
      key: 'patient',
      width: 120,
      render: (_, record) => (
        <div>
          <div>{record.patientName || `患者${record.patientId}`}</div>
          <div style={{ fontSize: '12px', color: '#666' }}>
            ID: {record.patientId}
          </div>
        </div>
      )
    },
    {
      title: '科室',
      dataIndex: 'department',
      key: 'department',
      width: 100
    },
    {
      title: '医生',
      key: 'doctor',
      width: 100,
      render: (_, record) => record.doctorName || `医生${record.doctorId}`
    },
    {
      title: '预约类型',
      dataIndex: 'appointmentType',
      key: 'appointmentType',
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
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      width: 80,
      render: (priority: number) => (
        <Tag color={getPriorityColor(priority)}>
          {getPriorityLabel(priority)}
        </Tag>
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
      title: '预约费用',
      dataIndex: 'appointmentFee',
      key: 'appointmentFee',
      width: 100,
      render: (fee: number) => fee ? `¥${fee.toFixed(2)}` : '-'
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
              onClick={() => handleEditAppointment(record)}
              size="small"
            />
          </Tooltip>
          
          {record.status === '已预约' && (
            <Tooltip title="确认预约">
              <Button
                type="link"
                icon={<CheckCircleOutlined />}
                onClick={() => handleConfirmAppointment(record)}
                size="small"
                style={{ color: '#52c41a' }}
              />
            </Tooltip>
          )}
          
          {record.status === '已确认' && (
            <Tooltip title="标记到达">
              <Button
                type="link"
                icon={<UserOutlined />}
                onClick={() => handleMarkArrived(record)}
                size="small"
                style={{ color: '#1890ff' }}
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
          
          {record.status === '进行中' && (
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
          
          {(record.status === '已预约' || record.status === '已确认') && (
            <Tooltip title="取消预约">
              <Popconfirm
                title="确定取消这个预约吗？"
                onConfirm={() => handleCancelAppointment(record)}
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
      <Card>
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={6}>
            <Search
              placeholder="搜索患者姓名或预约信息"
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
              {APPOINTMENT_STATUSES.map(status => (
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
              placeholder="预约类型"
              allowClear
              style={{ width: '100%' }}
              onChange={setTypeFilter}
            >
              {APPOINTMENT_TYPES.map(type => (
                <Option key={type.value} value={type.value}>
                  {type.label}
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
          <Col span={4}>
            <Space>
              <Button.Group>
                <Button
                  type={viewMode === 'table' ? 'primary' : 'default'}
                  icon={<UnorderedListOutlined />}
                  onClick={() => setViewMode('table')}
                >
                  列表
                </Button>
                <Button
                  type={viewMode === 'calendar' ? 'primary' : 'default'}
                  icon={<CalendarOutlined />}
                  onClick={() => setViewMode('calendar')}
                >
                  日历
                </Button>
              </Button.Group>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={handleCreateAppointment}
              >
                新增预约
              </Button>
              <Button
                icon={<ReloadOutlined />}
                onClick={fetchAppointments}
              >
                刷新
              </Button>
            </Space>
          </Col>
        </Row>

        {viewMode === 'table' ? (
          <Table
            columns={columns}
            dataSource={appointments}
            loading={loading}
            rowKey="id"
            scroll={{ x: 1400 }}
            pagination={{
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total) => `共 ${total} 条记录`
            }}
          />
        ) : (
          <AppointmentCalendar
            onAppointmentClick={handleCalendarAppointmentClick}
            onDateSelect={handleCalendarDateSelect}
          />
        )}
      </Card>

      <Modal
        title={editingAppointment ? '编辑预约' : '新增预约'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        width={900}
        destroyOnClose
      >
        <AppointmentForm
          form={form}
          editingAppointment={editingAppointment}
        />
      </Modal>
    </div>
  );
};

export default AppointmentManagement;