import React, { useState, useEffect } from 'react';
import {
  Form,
  Select,
  DatePicker,
  TimePicker,
  Input,
  InputNumber,
  Switch,
  Row,
  Col,
  Alert,
  Spin,
  message
} from 'antd';
import { ClockCircleOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { appointmentService } from '../services/appointmentService';
import { patientService } from '../services/patientService';
import type { 
  CreateAppointmentRequest, 
  UpdateAppointmentRequest,
  AvailableSlot,
  Appointment 
} from '../types/appointment';
import {
  APPOINTMENT_TYPES,
  APPOINTMENT_SOURCES,
  DEPARTMENTS,
  PRIORITY_OPTIONS
} from '../types/appointment';

const { Option } = Select;
const { TextArea } = Input;

interface AppointmentFormProps {
  form: any;
  editingAppointment?: Appointment | null;
  onValuesChange?: (changedValues: any, allValues: any) => void;
}

const AppointmentForm: React.FC<AppointmentFormProps> = ({
  form,
  editingAppointment,
  onValuesChange
}) => {
  const [patients, setPatients] = useState<any[]>([]);
  const [doctors, setDoctors] = useState<any[]>([]);
  const [availableSlots, setAvailableSlots] = useState<AvailableSlot[]>([]);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [timeConflict, setTimeConflict] = useState(false);
  const [checkingConflict, setCheckingConflict] = useState(false);
  const [selectedDate, setSelectedDate] = useState<dayjs.Dayjs | null>(null);
  const [selectedDoctor, setSelectedDoctor] = useState<number | null>(null);

  useEffect(() => {
    fetchPatients();
    fetchDoctors();
  }, []);

  useEffect(() => {
    if (selectedDoctor && selectedDate) {
      fetchAvailableSlots();
    }
  }, [selectedDoctor, selectedDate]);

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

  const fetchDoctors = async () => {
    // 临时模拟医生数据，实际应该从后端获取
    const mockDoctors = [
      { id: 1, name: '张医生', department: '内科', title: '主任医师' },
      { id: 2, name: '李医生', department: '外科', title: '副主任医师' },
      { id: 3, name: '王医生', department: '儿科', title: '主治医师' },
      { id: 4, name: '赵医生', department: '妇科', title: '主治医师' },
      { id: 5, name: '陈医生', department: '眼科', title: '副主任医师' }
    ];
    setDoctors(mockDoctors);
  };

  const fetchAvailableSlots = async () => {
    if (!selectedDoctor || !selectedDate) return;

    setLoadingSlots(true);
    try {
      const response = await appointmentService.getAvailableSlots(
        selectedDoctor,
        selectedDate.format('YYYY-MM-DD')
      );
      if (response.success && response.data) {
        setAvailableSlots(response.data);
      }
    } catch (error) {
      // 如果后端接口不可用，使用模拟数据
      const mockSlots: AvailableSlot[] = [
        {
          startTime: '08:00',
          endTime: '08:30',
          isAvailable: true,
          doctorId: selectedDoctor,
          doctorName: doctors.find(d => d.id === selectedDoctor)?.name || '',
          department: doctors.find(d => d.id === selectedDoctor)?.department || ''
        },
        {
          startTime: '08:30',
          endTime: '09:00',
          isAvailable: true,
          doctorId: selectedDoctor,
          doctorName: doctors.find(d => d.id === selectedDoctor)?.name || '',
          department: doctors.find(d => d.id === selectedDoctor)?.department || ''
        },
        {
          startTime: '09:00',
          endTime: '09:30',
          isAvailable: false,
          doctorId: selectedDoctor,
          doctorName: doctors.find(d => d.id === selectedDoctor)?.name || '',
          department: doctors.find(d => d.id === selectedDoctor)?.department || ''
        },
        {
          startTime: '09:30',
          endTime: '10:00',
          isAvailable: true,
          doctorId: selectedDoctor,
          doctorName: doctors.find(d => d.id === selectedDoctor)?.name || '',
          department: doctors.find(d => d.id === selectedDoctor)?.department || ''
        },
        {
          startTime: '10:00',
          endTime: '10:30',
          isAvailable: true,
          doctorId: selectedDoctor,
          doctorName: doctors.find(d => d.id === selectedDoctor)?.name || '',
          department: doctors.find(d => d.id === selectedDoctor)?.department || ''
        },
        {
          startTime: '14:00',
          endTime: '14:30',
          isAvailable: true,
          doctorId: selectedDoctor,
          doctorName: doctors.find(d => d.id === selectedDoctor)?.name || '',
          department: doctors.find(d => d.id === selectedDoctor)?.department || ''
        },
        {
          startTime: '14:30',
          endTime: '15:00',
          isAvailable: true,
          doctorId: selectedDoctor,
          doctorName: doctors.find(d => d.id === selectedDoctor)?.name || '',
          department: doctors.find(d => d.id === selectedDoctor)?.department || ''
        },
        {
          startTime: '15:00',
          endTime: '15:30',
          isAvailable: false,
          doctorId: selectedDoctor,
          doctorName: doctors.find(d => d.id === selectedDoctor)?.name || '',
          department: doctors.find(d => d.id === selectedDoctor)?.department || ''
        }
      ];
      setAvailableSlots(mockSlots);
    } finally {
      setLoadingSlots(false);
    }
  };

  const checkTimeConflict = async (doctorId: number, appointmentTime: string) => {
    if (!doctorId || !appointmentTime) return;

    setCheckingConflict(true);
    try {
      const response = await appointmentService.checkTimeConflict(doctorId, appointmentTime);
      if (response.success && response.data) {
        setTimeConflict(response.data.hasConflict);
      }
    } catch (error) {
      // 如果后端接口不可用，进行简单的时间冲突检查
      const selectedTime = dayjs(appointmentTime);
      const hasConflict = availableSlots.some(slot => {
        const slotStart = dayjs(`${selectedDate?.format('YYYY-MM-DD')} ${slot.startTime}`);
        const slotEnd = dayjs(`${selectedDate?.format('YYYY-MM-DD')} ${slot.endTime}`);
        return !slot.isAvailable && selectedTime.isBetween(slotStart, slotEnd, null, '[)');
      });
      setTimeConflict(hasConflict);
    } finally {
      setCheckingConflict(false);
    }
  };

  const handleDoctorChange = (doctorId: number) => {
    setSelectedDoctor(doctorId);
    const doctor = doctors.find(d => d.id === doctorId);
    if (doctor) {
      form.setFieldsValue({ department: doctor.department });
    }
    // 清空时间选择
    form.setFieldsValue({ appointmentTime: undefined });
    setTimeConflict(false);
  };

  const handleDateChange = (date: dayjs.Dayjs | null) => {
    setSelectedDate(date);
    // 清空时间选择
    form.setFieldsValue({ appointmentTime: undefined });
    setTimeConflict(false);
  };

  const handleTimeChange = (time: dayjs.Dayjs | null) => {
    if (time && selectedDoctor && selectedDate) {
      const appointmentTime = selectedDate
        .hour(time.hour())
        .minute(time.minute())
        .second(0);
      
      checkTimeConflict(selectedDoctor, appointmentTime.format('YYYY-MM-DD HH:mm:ss'));
    }
  };

  const handleValuesChange = (changedValues: any, allValues: any) => {
    if (changedValues.doctorId) {
      handleDoctorChange(changedValues.doctorId);
    }
    
    if (onValuesChange) {
      onValuesChange(changedValues, allValues);
    }
  };

  const getAvailableTimeSlots = () => {
    return availableSlots
      .filter(slot => slot.isAvailable)
      .map(slot => ({
        label: `${slot.startTime} - ${slot.endTime}`,
        value: slot.startTime
      }));
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onValuesChange={handleValuesChange}
      initialValues={{
        appointmentType: '初诊',
        source: '现场',
        priority: 3,
        needReminder: true,
        reminderMinutes: 30
      }}
    >
      {!editingAppointment && (
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
      )}

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="doctorId"
            label="选择医生"
            rules={[{ required: true, message: '请选择医生' }]}
          >
            <Select
              placeholder="请选择医生"
              onChange={handleDoctorChange}
            >
              {doctors.map(doctor => (
                <Option key={doctor.id} value={doctor.id}>
                  {doctor.name} - {doctor.title} ({doctor.department})
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Col>
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
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="appointmentDate"
            label="预约日期"
            rules={[{ required: true, message: '请选择预约日期' }]}
          >
            <DatePicker
              style={{ width: '100%' }}
              placeholder="请选择预约日期"
              disabledDate={(current) => current && current < dayjs().startOf('day')}
              onChange={handleDateChange}
            />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="appointmentTime"
            label="预约时间"
            rules={[{ required: true, message: '请选择预约时间' }]}
          >
            <TimePicker
              style={{ width: '100%' }}
              placeholder="请选择预约时间"
              format="HH:mm"
              minuteStep={30}
              onChange={handleTimeChange}
              disabled={!selectedDate || !selectedDoctor}
            />
          </Form.Item>
        </Col>
      </Row>

      {/* 可用时间段提示 */}
      {selectedDoctor && selectedDate && (
        <Row gutter={16}>
          <Col span={24}>
            <div style={{ marginBottom: 16 }}>
              <div style={{ marginBottom: 8, fontWeight: 'bold' }}>
                <ClockCircleOutlined /> 可用时间段：
              </div>
              {loadingSlots ? (
                <Spin size="small" />
              ) : (
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
                  {availableSlots.map((slot, index) => (
                    <div
                      key={index}
                      style={{
                        padding: '4px 8px',
                        border: '1px solid #d9d9d9',
                        borderRadius: 4,
                        fontSize: '12px',
                        backgroundColor: slot.isAvailable ? '#f6ffed' : '#fff2f0',
                        color: slot.isAvailable ? '#52c41a' : '#ff4d4f'
                      }}
                    >
                      {slot.startTime} - {slot.endTime}
                      {!slot.isAvailable && ' (已占用)'}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </Col>
        </Row>
      )}

      {/* 时间冲突警告 */}
      {timeConflict && (
        <Alert
          message="时间冲突"
          description="所选时间段已被预约，请选择其他时间。"
          type="error"
          icon={<ExclamationCircleOutlined />}
          style={{ marginBottom: 16 }}
        />
      )}

      {checkingConflict && (
        <Alert
          message="正在检查时间冲突..."
          type="info"
          style={{ marginBottom: 16 }}
        />
      )}

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="appointmentType"
            label="预约类型"
            rules={[{ required: true, message: '请选择预约类型' }]}
          >
            <Select placeholder="请选择预约类型">
              {APPOINTMENT_TYPES.map(type => (
                <Option key={type.value} value={type.value}>
                  {type.label}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="source"
            label="预约来源"
          >
            <Select placeholder="请选择预约来源">
              {APPOINTMENT_SOURCES.map(source => (
                <Option key={source.value} value={source.value}>
                  {source.label}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="priority"
            label="优先级"
          >
            <Select placeholder="请选择优先级">
              {PRIORITY_OPTIONS.map(priority => (
                <Option key={priority.value} value={priority.value}>
                  {priority.label}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="appointmentFee"
            label="预约费用"
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="请输入预约费用"
              min={0}
              precision={2}
              addonAfter="元"
            />
          </Form.Item>
        </Col>
      </Row>

      <Form.Item
        name="chiefComplaint"
        label="主诉"
      >
        <TextArea
          rows={3}
          placeholder="请输入主诉"
          maxLength={500}
          showCount
        />
      </Form.Item>

      <Form.Item
        name="notes"
        label="备注"
      >
        <TextArea
          rows={3}
          placeholder="请输入备注信息"
          maxLength={1000}
          showCount
        />
      </Form.Item>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="needReminder"
            label="是否需要提醒"
            valuePropName="checked"
          >
            <Switch checkedChildren="是" unCheckedChildren="否" />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="reminderMinutes"
            label="提醒时间（分钟）"
            dependencies={['needReminder']}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="预约前多少分钟提醒"
              min={0}
              max={1440}
              disabled={!form.getFieldValue('needReminder')}
            />
          </Form.Item>
        </Col>
      </Row>
    </Form>
  );
};

export default AppointmentForm;