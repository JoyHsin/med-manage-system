import React, { useState, useEffect } from 'react';
import {
  Calendar,
  Badge,
  Card,
  Select,
  Row,
  Col,
  Button,
  Modal,
  List,
  Tag,
  Space,
  Tooltip,
  message
} from 'antd';
import {
  CalendarOutlined,
  ClockCircleOutlined,
  UserOutlined,
  LeftOutlined,
  RightOutlined
} from '@ant-design/icons';
import type { Dayjs } from 'dayjs';
import dayjs from 'dayjs';
import { appointmentService } from '../services/appointmentService';
import type { Appointment } from '../types/appointment';
import { APPOINTMENT_STATUSES, DEPARTMENTS } from '../types/appointment';

const { Option } = Select;

interface AppointmentCalendarProps {
  onAppointmentClick?: (appointment: Appointment) => void;
  onDateSelect?: (date: Dayjs) => void;
}

const AppointmentCalendar: React.FC<AppointmentCalendarProps> = ({
  onAppointmentClick,
  onDateSelect
}) => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedDate, setSelectedDate] = useState<Dayjs>(dayjs());
  const [viewMode, setViewMode] = useState<'month' | 'year'>('month');
  const [departmentFilter, setDepartmentFilter] = useState<string | undefined>();
  const [statusFilter, setStatusFilter] = useState<string | undefined>();
  const [dayAppointmentsVisible, setDayAppointmentsVisible] = useState(false);
  const [selectedDayAppointments, setSelectedDayAppointments] = useState<Appointment[]>([]);

  useEffect(() => {
    fetchAppointments();
  }, [selectedDate, departmentFilter, statusFilter]);

  const fetchAppointments = async () => {
    setLoading(true);
    try {
      const startDate = selectedDate.startOf('month').format('YYYY-MM-DD');
      const endDate = selectedDate.endOf('month').format('YYYY-MM-DD');
      
      const params: any = {
        startDate,
        endDate
      };
      
      if (departmentFilter) {
        params.department = departmentFilter;
      }
      if (statusFilter) {
        params.status = statusFilter;
      }

      const response = await appointmentService.getAllAppointments(params);
      if (response.success && response.data) {
        setAppointments(response.data);
      }
    } catch (error) {
      message.error('获取预约数据失败');
    } finally {
      setLoading(false);
    }
  };

  const getAppointmentsForDate = (date: Dayjs) => {
    const dateStr = date.format('YYYY-MM-DD');
    return appointments.filter(appointment => 
      dayjs(appointment.appointmentTime).format('YYYY-MM-DD') === dateStr
    );
  };

  const getStatusColor = (status: string) => {
    const statusConfig = APPOINTMENT_STATUSES.find(s => s.value === status);
    return statusConfig?.color || 'default';
  };

  const dateCellRender = (date: Dayjs) => {
    const dayAppointments = getAppointmentsForDate(date);
    
    if (dayAppointments.length === 0) {
      return null;
    }

    // 按状态分组统计
    const statusCounts = dayAppointments.reduce((acc, appointment) => {
      acc[appointment.status] = (acc[appointment.status] || 0) + 1;
      return acc;
    }, {} as Record<string, number>);

    return (
      <div style={{ fontSize: '12px' }}>
        {Object.entries(statusCounts).map(([status, count]) => (
          <div key={status} style={{ marginBottom: 2 }}>
            <Badge
              status={getStatusColor(status) as any}
              text={`${status} ${count}`}
              style={{ fontSize: '11px' }}
            />
          </div>
        ))}
      </div>
    );
  };

  const monthCellRender = (date: Dayjs) => {
    const monthStart = date.startOf('month');
    const monthEnd = date.endOf('month');
    
    const monthAppointments = appointments.filter(appointment => {
      const appointmentDate = dayjs(appointment.appointmentTime);
      return appointmentDate.isBetween(monthStart, monthEnd, null, '[]');
    });

    if (monthAppointments.length === 0) {
      return null;
    }

    return (
      <div style={{ fontSize: '12px', textAlign: 'center' }}>
        <Badge count={monthAppointments.length} style={{ backgroundColor: '#52c41a' }} />
      </div>
    );
  };

  const handleDateSelect = (date: Dayjs) => {
    setSelectedDate(date);
    const dayAppointments = getAppointmentsForDate(date);
    
    if (dayAppointments.length > 0) {
      setSelectedDayAppointments(dayAppointments);
      setDayAppointmentsVisible(true);
    }
    
    if (onDateSelect) {
      onDateSelect(date);
    }
  };

  const handlePanelChange = (date: Dayjs, mode: 'month' | 'year') => {
    setSelectedDate(date);
    setViewMode(mode);
  };

  const handleAppointmentItemClick = (appointment: Appointment) => {
    if (onAppointmentClick) {
      onAppointmentClick(appointment);
    }
    setDayAppointmentsVisible(false);
  };

  const renderAppointmentItem = (appointment: Appointment) => (
    <List.Item
      key={appointment.id}
      style={{ cursor: 'pointer', padding: '8px 12px' }}
      onClick={() => handleAppointmentItemClick(appointment)}
    >
      <List.Item.Meta
        avatar={<UserOutlined style={{ color: '#1890ff' }} />}
        title={
          <Space>
            <span>{appointment.patientName || `患者${appointment.patientId}`}</span>
            <Tag color={getStatusColor(appointment.status)}>
              {appointment.status}
            </Tag>
          </Space>
        }
        description={
          <div>
            <div>
              <ClockCircleOutlined /> {dayjs(appointment.appointmentTime).format('HH:mm')} - 
              {appointment.doctorName || `医生${appointment.doctorId}`} ({appointment.department})
            </div>
            <div style={{ marginTop: 4, color: '#666' }}>
              {appointment.appointmentType} | {appointment.chiefComplaint || '无主诉'}
            </div>
          </div>
        }
      />
    </List.Item>
  );

  return (
    <div>
      <Card>
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={6}>
            <Select
              placeholder="选择科室"
              allowClear
              style={{ width: '100%' }}
              onChange={setDepartmentFilter}
              value={departmentFilter}
            >
              {DEPARTMENTS.map(dept => (
                <Option key={dept.value} value={dept.value}>
                  {dept.label}
                </Option>
              ))}
            </Select>
          </Col>
          <Col span={6}>
            <Select
              placeholder="选择状态"
              allowClear
              style={{ width: '100%' }}
              onChange={setStatusFilter}
              value={statusFilter}
            >
              {APPOINTMENT_STATUSES.map(status => (
                <Option key={status.value} value={status.value}>
                  {status.label}
                </Option>
              ))}
            </Select>
          </Col>
          <Col span={12}>
            <Space>
              <Button
                icon={<LeftOutlined />}
                onClick={() => {
                  const newDate = selectedDate.subtract(1, viewMode);
                  setSelectedDate(newDate);
                }}
              >
                上{viewMode === 'month' ? '月' : '年'}
              </Button>
              <Button
                onClick={() => setSelectedDate(dayjs())}
              >
                今天
              </Button>
              <Button
                icon={<RightOutlined />}
                onClick={() => {
                  const newDate = selectedDate.add(1, viewMode);
                  setSelectedDate(newDate);
                }}
              >
                下{viewMode === 'month' ? '月' : '年'}
              </Button>
            </Space>
          </Col>
        </Row>

        <Calendar
          value={selectedDate}
          mode={viewMode}
          onSelect={handleDateSelect}
          onPanelChange={handlePanelChange}
          dateCellRender={viewMode === 'month' ? dateCellRender : undefined}
          monthCellRender={viewMode === 'year' ? monthCellRender : undefined}
          headerRender={({ value, type, onChange, onTypeChange }) => (
            <div style={{ padding: '10px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div style={{ fontSize: '16px', fontWeight: 'bold' }}>
                <CalendarOutlined style={{ marginRight: 8 }} />
                {value.format(type === 'month' ? 'YYYY年MM月' : 'YYYY年')}
              </div>
              <Space>
                <Select
                  size="small"
                  value={type}
                  onChange={onTypeChange}
                  style={{ width: 80 }}
                >
                  <Option value="month">月</Option>
                  <Option value="year">年</Option>
                </Select>
              </Space>
            </div>
          )}
        />

        {/* 统计信息 */}
        <div style={{ marginTop: 16, padding: '16px', backgroundColor: '#fafafa', borderRadius: 6 }}>
          <Row gutter={16}>
            <Col span={6}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff' }}>
                  {appointments.length}
                </div>
                <div style={{ color: '#666' }}>总预约数</div>
              </div>
            </Col>
            <Col span={6}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#52c41a' }}>
                  {appointments.filter(a => a.status === '已确认').length}
                </div>
                <div style={{ color: '#666' }}>已确认</div>
              </div>
            </Col>
            <Col span={6}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#faad14' }}>
                  {appointments.filter(a => a.status === '已预约').length}
                </div>
                <div style={{ color: '#666' }}>待确认</div>
              </div>
            </Col>
            <Col span={6}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#ff4d4f' }}>
                  {appointments.filter(a => a.status === '已取消').length}
                </div>
                <div style={{ color: '#666' }}>已取消</div>
              </div>
            </Col>
          </Row>
        </div>
      </Card>

      {/* 当日预约详情弹窗 */}
      <Modal
        title={
          <Space>
            <CalendarOutlined />
            {selectedDate.format('YYYY年MM月DD日')} 预约详情
            <Badge count={selectedDayAppointments.length} />
          </Space>
        }
        open={dayAppointmentsVisible}
        onCancel={() => setDayAppointmentsVisible(false)}
        footer={null}
        width={600}
      >
        <List
          dataSource={selectedDayAppointments.sort((a, b) => 
            dayjs(a.appointmentTime).unix() - dayjs(b.appointmentTime).unix()
          )}
          renderItem={renderAppointmentItem}
          locale={{ emptyText: '当日无预约' }}
        />
      </Modal>
    </div>
  );
};

export default AppointmentCalendar;