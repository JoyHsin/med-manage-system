import React, { useState, useEffect } from 'react';
import {
  Card,
  Form,
  Input,
  InputNumber,
  Button,
  Select,
  Row,
  Col,
  Typography,
  Alert,
  message,
  Space,
  Divider,
  Tag,
  Statistic,
  Modal,
  Table,
  DatePicker
} from 'antd';
import {
  HeartOutlined,
  ThermometerOutlined,
  DashboardOutlined,
  UserOutlined,
  HistoryOutlined,
  ExclamationCircleOutlined,
  CheckCircleOutlined,
  WarningOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { vitalSignsService } from '../services/vitalSignsService';
import { patientService } from '../services/patientService';
import {
  VitalSigns,
  VitalSignsRequest,
  VITAL_SIGNS_RANGES,
  CONSCIOUSNESS_LEVELS,
  BMI_CATEGORIES,
  PAIN_SCORE_DESCRIPTIONS
} from '../types/vitalSigns';

const { Title, Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;
const { RangePicker } = DatePicker;

interface Patient {
  id: number;
  name: string;
  phone: string;
  patientNumber: string;
}

const VitalSignsManagement: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
  const [warnings, setWarnings] = useState<string[]>([]);
  const [bmi, setBmi] = useState<number | null>(null);
  const [bmiCategory, setBmiCategory] = useState<string>('');
  const [historyVisible, setHistoryVisible] = useState(false);
  const [vitalSignsHistory, setVitalSignsHistory] = useState<VitalSigns[]>([]);
  const [historyLoading, setHistoryLoading] = useState(false);

  // 加载患者列表
  useEffect(() => {
    loadPatients();
  }, []);

  const loadPatients = async () => {
    try {
      const response = await patientService.getAllPatients();
      setPatients(response.data || []);
    } catch (error) {
      console.error('加载患者列表失败:', error);
      message.error('加载患者列表失败');
    }
  };

  // 监听体重和身高变化，自动计算BMI
  const handleWeightHeightChange = () => {
    const weight = form.getFieldValue('weight');
    const height = form.getFieldValue('height');
    
    if (weight && height) {
      const calculatedBmi = vitalSignsService.calculateBMI(weight, height);
      setBmi(calculatedBmi);
      setBmiCategory(vitalSignsService.getBMICategory(calculatedBmi));
      form.setFieldsValue({ bmi: calculatedBmi });
    } else {
      setBmi(null);
      setBmiCategory('');
      form.setFieldsValue({ bmi: undefined });
    }
  };

  // 实时验证生命体征数据
  const handleFieldChange = async () => {
    try {
      const values = await form.validateFields();
      if (values.patientId) {
        const validationResult = await vitalSignsService.validateVitalSigns(values);
        setWarnings(validationResult.warnings || []);
      }
    } catch (error) {
      // 表单验证失败时清空警告
      setWarnings([]);
    }
  };

  // 提交生命体征数据
  const handleSubmit = async (values: VitalSignsRequest) => {
    try {
      setLoading(true);
      const response = await vitalSignsService.recordVitalSigns(values);
      
      if (response.warnings && response.warnings.length > 0) {
        Modal.warning({
          title: '数据异常提醒',
          content: (
            <div>
              <p>生命体征已成功录入，但检测到以下异常指标：</p>
              <ul>
                {response.warnings.map((warning, index) => (
                  <li key={index}>{warning}</li>
                ))}
              </ul>
            </div>
          ),
          onOk: () => {
            message.success('生命体征录入成功');
            form.resetFields();
            setSelectedPatient(null);
            setWarnings([]);
            setBmi(null);
            setBmiCategory('');
          }
        });
      } else {
        message.success('生命体征录入成功');
        form.resetFields();
        setSelectedPatient(null);
        setWarnings([]);
        setBmi(null);
        setBmiCategory('');
      }
    } catch (error) {
      console.error('录入生命体征失败:', error);
      message.error('录入生命体征失败');
    } finally {
      setLoading(false);
    }
  };

  // 选择患者
  const handlePatientSelect = (patientId: number) => {
    const patient = patients.find(p => p.id === patientId);
    setSelectedPatient(patient || null);
    form.setFieldsValue({ patientId });
  };

  // 查看患者历史记录
  const handleViewHistory = async () => {
    if (!selectedPatient) {
      message.warning('请先选择患者');
      return;
    }

    try {
      setHistoryLoading(true);
      setHistoryVisible(true);
      const history = await vitalSignsService.getPatientVitalSigns(selectedPatient.id);
      setVitalSignsHistory(history);
    } catch (error) {
      console.error('加载历史记录失败:', error);
      message.error('加载历史记录失败');
    } finally {
      setHistoryLoading(false);
    }
  };

  // 历史记录表格列定义
  const historyColumns: ColumnsType<VitalSigns> = [
    {
      title: '记录时间',
      dataIndex: 'recordedAt',
      key: 'recordedAt',
      render: (time: string) => dayjs(time).format('YYYY-MM-DD HH:mm')
    },
    {
      title: '血压',
      key: 'bloodPressure',
      render: (_, record) => {
        if (record.systolicBp && record.diastolicBp) {
          const isNormal = vitalSignsService.isValueNormal(record.systolicBp, 90, 140) &&
                           vitalSignsService.isValueNormal(record.diastolicBp, 60, 90);
          return (
            <span style={{ color: isNormal ? '#52c41a' : '#ff4d4f' }}>
              {record.systolicBp}/{record.diastolicBp} mmHg
            </span>
          );
        }
        return '-';
      }
    },
    {
      title: '体温',
      dataIndex: 'temperature',
      key: 'temperature',
      render: (temp: number) => {
        if (temp) {
          const isNormal = vitalSignsService.isValueNormal(temp, 36.0, 37.5);
          return (
            <span style={{ color: isNormal ? '#52c41a' : '#ff4d4f' }}>
              {temp}°C
            </span>
          );
        }
        return '-';
      }
    },
    {
      title: '心率',
      dataIndex: 'heartRate',
      key: 'heartRate',
      render: (rate: number) => {
        if (rate) {
          const isNormal = vitalSignsService.isValueNormal(rate, 60, 100);
          return (
            <span style={{ color: isNormal ? '#52c41a' : '#ff4d4f' }}>
              {rate} 次/分
            </span>
          );
        }
        return '-';
      }
    },
    {
      title: '异常指标',
      dataIndex: 'isAbnormal',
      key: 'isAbnormal',
      render: (isAbnormal: boolean, record) => (
        isAbnormal ? (
          <Tag color="red" icon={<WarningOutlined />}>
            异常
          </Tag>
        ) : (
          <Tag color="green" icon={<CheckCircleOutlined />}>
            正常
          </Tag>
        )
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <HeartOutlined style={{ marginRight: 8 }} />
        生命体征录入
      </Title>

      <Row gutter={24}>
        <Col span={16}>
          <Card title="生命体征录入表单">
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
              onValuesChange={handleFieldChange}
            >
              {/* 患者选择 */}
              <Form.Item
                label="选择患者"
                name="patientId"
                rules={[{ required: true, message: '请选择患者' }]}
              >
                <Select
                  showSearch
                  placeholder="搜索患者姓名或编号"
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
              </Form.Item>

              {selectedPatient && (
                <Alert
                  message={`当前患者：${selectedPatient.name} (${selectedPatient.patientNumber})`}
                  type="info"
                  showIcon
                  style={{ marginBottom: 16 }}
                  action={
                    <Button size="small" onClick={handleViewHistory}>
                      查看历史
                    </Button>
                  }
                />
              )}

              {/* 基础生命体征 */}
              <Divider orientation="left">基础生命体征</Divider>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item label="收缩压 (mmHg)" name="systolicBp">
                    <InputNumber
                      min={60}
                      max={300}
                      placeholder="90-140"
                      style={{ width: '100%' }}
                      addonAfter="mmHg"
                    />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="舒张压 (mmHg)" name="diastolicBp">
                    <InputNumber
                      min={40}
                      max={200}
                      placeholder="60-90"
                      style={{ width: '100%' }}
                      addonAfter="mmHg"
                    />
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item label="体温 (°C)" name="temperature">
                    <InputNumber
                      min={35.0}
                      max={42.0}
                      step={0.1}
                      precision={1}
                      placeholder="36.0-37.5"
                      style={{ width: '100%' }}
                      addonAfter="°C"
                    />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="心率 (次/分)" name="heartRate">
                    <InputNumber
                      min={40}
                      max={200}
                      placeholder="60-100"
                      style={{ width: '100%' }}
                      addonAfter="次/分"
                    />
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item label="呼吸频率 (次/分)" name="respiratoryRate">
                    <InputNumber
                      min={8}
                      max={40}
                      placeholder="12-20"
                      style={{ width: '100%' }}
                      addonAfter="次/分"
                    />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="血氧饱和度 (%)" name="oxygenSaturation">
                    <InputNumber
                      min={70}
                      max={100}
                      placeholder="95-100"
                      style={{ width: '100%' }}
                      addonAfter="%"
                    />
                  </Form.Item>
                </Col>
              </Row>

              {/* 体格测量 */}
              <Divider orientation="left">体格测量</Divider>
              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item label="体重 (kg)" name="weight">
                    <InputNumber
                      min={0.5}
                      max={500}
                      step={0.1}
                      precision={1}
                      placeholder="体重"
                      style={{ width: '100%' }}
                      addonAfter="kg"
                      onChange={handleWeightHeightChange}
                    />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="身高 (cm)" name="height">
                    <InputNumber
                      min={30}
                      max={250}
                      placeholder="身高"
                      style={{ width: '100%' }}
                      addonAfter="cm"
                      onChange={handleWeightHeightChange}
                    />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="BMI" name="bmi">
                    <InputNumber
                      disabled
                      precision={1}
                      style={{ width: '100%' }}
                      addonAfter={
                        bmiCategory && BMI_CATEGORIES[bmiCategory as keyof typeof BMI_CATEGORIES] ? (
                          <Tag color={BMI_CATEGORIES[bmiCategory as keyof typeof BMI_CATEGORIES].color}>
                            {BMI_CATEGORIES[bmiCategory as keyof typeof BMI_CATEGORIES].label}
                          </Tag>
                        ) : null
                      }
                    />
                  </Form.Item>
                </Col>
              </Row>

              {/* 其他指标 */}
              <Divider orientation="left">其他指标</Divider>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item label="疼痛评分 (0-10分)" name="painScore">
                    <InputNumber
                      min={0}
                      max={10}
                      placeholder="0-无痛, 10-剧痛"
                      style={{ width: '100%' }}
                      addonAfter="分"
                    />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="意识状态" name="consciousnessLevel">
                    <Select placeholder="选择意识状态">
                      {CONSCIOUSNESS_LEVELS.map(level => (
                        <Option key={level.value} value={level.value}>
                          {level.label}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item label="备注" name="remarks">
                <TextArea
                  rows={3}
                  placeholder="记录其他相关信息..."
                  maxLength={1000}
                  showCount
                />
              </Form.Item>

              <Form.Item>
                <Space>
                  <Button type="primary" htmlType="submit" loading={loading}>
                    录入生命体征
                  </Button>
                  <Button onClick={() => form.resetFields()}>
                    重置表单
                  </Button>
                </Space>
              </Form.Item>
            </Form>
          </Card>
        </Col>

        <Col span={8}>
          {/* 正常范围参考 */}
          <Card title="正常范围参考" style={{ marginBottom: 16 }}>
            <div style={{ fontSize: '12px' }}>
              <div><strong>血压:</strong> 收缩压 90-140 mmHg, 舒张压 60-90 mmHg</div>
              <div><strong>体温:</strong> 36.0-37.5°C</div>
              <div><strong>心率:</strong> 60-100 次/分</div>
              <div><strong>呼吸:</strong> 12-20 次/分</div>
              <div><strong>血氧:</strong> ≥95%</div>
              <div><strong>BMI:</strong> 18.5-24.0 (正常)</div>
            </div>
          </Card>

          {/* 异常提醒 */}
          {warnings.length > 0 && (
            <Card title="异常提醒" style={{ marginBottom: 16 }}>
              <Alert
                message="检测到异常指标"
                description={
                  <ul style={{ margin: 0, paddingLeft: 20 }}>
                    {warnings.map((warning, index) => (
                      <li key={index}>{warning}</li>
                    ))}
                  </ul>
                }
                type="warning"
                showIcon
              />
            </Card>
          )}

          {/* BMI计算结果 */}
          {bmi && (
            <Card title="BMI计算结果">
              <Statistic
                title="BMI指数"
                value={bmi}
                precision={1}
                valueStyle={{
                  color: BMI_CATEGORIES[bmiCategory as keyof typeof BMI_CATEGORIES]?.color
                }}
                suffix={
                  <Tag color={BMI_CATEGORIES[bmiCategory as keyof typeof BMI_CATEGORIES]?.color}>
                    {BMI_CATEGORIES[bmiCategory as keyof typeof BMI_CATEGORIES]?.label}
                  </Tag>
                }
              />
            </Card>
          )}
        </Col>
      </Row>

      {/* 历史记录模态框 */}
      <Modal
        title={`${selectedPatient?.name} 的生命体征历史记录`}
        open={historyVisible}
        onCancel={() => setHistoryVisible(false)}
        width={1000}
        footer={null}
      >
        <Table
          columns={historyColumns}
          dataSource={vitalSignsHistory}
          rowKey="id"
          loading={historyLoading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`
          }}
        />
      </Modal>
    </div>
  );
};

export default VitalSignsManagement;