import React, { useState, useEffect } from 'react';
import { Card, Form, Input, InputNumber, Button, Select, Row, Col, Typography, message, Space } from 'antd';
import { HeartOutlined } from '@ant-design/icons';

const { Title } = Typography;
const { Option } = Select;

interface SimplePatient {
  id: number;
  name: string;
  phone: string;
  patientNumber: string;
}

const SimpleVitalSignsManagement: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [patients] = useState<SimplePatient[]>([
    { id: 1, name: '张三', phone: '13800138001', patientNumber: 'P001' },
    { id: 2, name: '李四', phone: '13800138002', patientNumber: 'P002' },
    { id: 3, name: '王五', phone: '13800138003', patientNumber: 'P003' },
  ]);

  const handleSubmit = async (values: any) => {
    try {
      setLoading(true);
      console.log('提交生命体征数据:', values);
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      message.success('生命体征录入成功');
      form.resetFields();
    } catch (error) {
      console.error('录入生命体征失败:', error);
      message.error('录入生命体征失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <HeartOutlined style={{ marginRight: 8 }} />
        生命体征录入 (简化版)
      </Title>

      <Row gutter={24}>
        <Col span={16}>
          <Card title="生命体征录入表单">
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
            >
              <Form.Item
                label="选择患者"
                name="patientId"
                rules={[{ required: true, message: '请选择患者' }]}
              >
                <Select placeholder="选择患者">
                  {patients.map(patient => (
                    <Option key={patient.id} value={patient.id}>
                      {patient.name} - {patient.patientNumber} - {patient.phone}
                    </Option>
                  ))}
                </Select>
              </Form.Item>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item label="收缩压 (mmHg)" name="systolicBp">
                    <InputNumber
                      min={60}
                      max={300}
                      placeholder="90-140"
                      style={{ width: '100%' }}
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
                    />
                  </Form.Item>
                </Col>
              </Row>

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
          <Card title="正常范围参考">
            <div style={{ fontSize: '12px' }}>
              <div><strong>血压:</strong> 收缩压 90-140 mmHg, 舒张压 60-90 mmHg</div>
              <div><strong>体温:</strong> 36.0-37.5°C</div>
              <div><strong>心率:</strong> 60-100 次/分</div>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default SimpleVitalSignsManagement;