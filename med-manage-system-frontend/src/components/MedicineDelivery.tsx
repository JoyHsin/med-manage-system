import React, { useState, useEffect } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Typography,
  message,
  Modal,
  Form,
  Input,
  Descriptions,
  List,
  Tag,
  Alert,
  Divider,
  Row,
  Col,
  Badge,
  Tooltip,
  Steps,
  Checkbox,
  Radio,
  Select
} from 'antd';
import {
  MedicineBoxOutlined,
  CheckCircleOutlined,
  UserOutlined,
  InfoCircleOutlined,
  WarningOutlined,
  PrinterOutlined,
  ClockCircleOutlined,
  FileTextOutlined,
  HeartOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { pharmacyService } from '../services/pharmacyService';

const { Title, Text, Paragraph } = Typography;
const { TextArea } = Input;
const { Option } = Select;
const { Step } = Steps;

interface MedicineDeliveryProps {
  dispenseRecord: any;
  onDelivered?: (record: any) => void;
  onCancel?: () => void;
}

const MedicineDelivery: React.FC<MedicineDeliveryProps> = ({
  dispenseRecord,
  onDelivered,
  onCancel
}) => {
  const [loading, setLoading] = useState(false);
  const [dispenseItems, setDispenseItems] = useState<any[]>([]);
  const [deliveryForm] = Form.useForm();
  const [guidanceModalVisible, setGuidanceModalVisible] = useState(false);
  const [selectedMedicine, setSelectedMedicine] = useState<any>(null);
  const [deliveryConfirmed, setDeliveryConfirmed] = useState(false);
  const [patientConfirmation, setPatientConfirmation] = useState({
    identityVerified: false,
    medicinesExplained: false,
    instructionsUnderstood: false,
    allergiesChecked: false
  });

  // 当前药师信息
  const currentPharmacistId = 1;
  const currentPharmacistName = '药师张三';

  useEffect(() => {
    if (dispenseRecord) {
      loadDispenseItems();
    }
  }, [dispenseRecord]);

  const loadDispenseItems = async () => {
    try {
      const record = await pharmacyService.getDispenseRecordWithItems(dispenseRecord.id);
      if (record && record.dispenseItems) {
        setDispenseItems(record.dispenseItems);
      }
    } catch (error) {
      console.error('加载调剂项目失败:', error);
      message.error('加载调剂项目失败');
    }
  };

  const handleDeliverMedicine = async () => {
    try {
      setLoading(true);
      
      // 验证所有确认项
      const allConfirmed = Object.values(patientConfirmation).every(Boolean);
      if (!allConfirmed) {
        message.error('请完成所有确认项');
        return;
      }

      const values = await deliveryForm.validateFields();
      
      await pharmacyService.deliverMedicine(dispenseRecord.id, {
        dispensingPharmacistId: currentPharmacistId,
        dispensingPharmacistName: currentPharmacistName,
        deliveryNotes: values.deliveryNotes || '药品已发放给患者'
      });

      message.success('发药成功');
      setDeliveryConfirmed(true);
      
      if (onDelivered) {
        onDelivered(dispenseRecord);
      }
    } catch (error) {
      console.error('发药失败:', error);
      message.error('发药失败');
    } finally {
      setLoading(false);
    }
  };

  const showMedicineGuidance = (medicine: any) => {
    setSelectedMedicine(medicine);
    setGuidanceModalVisible(true);
  };

  const printDeliveryRecord = () => {
    // 实际应用中这里会调用打印功能
    message.success('发药记录已发送到打印机');
  };

  const getMedicineGuidance = (medicine: any) => {
    // 模拟用药指导信息
    return {
      dosage: medicine.dosage || '按医嘱使用',
      frequency: '每日3次，饭后服用',
      duration: '连续服用7天',
      precautions: [
        '服药期间避免饮酒',
        '如出现不良反应请及时就医',
        '请按时按量服用，不可随意增减剂量',
        '药品应存放在阴凉干燥处'
      ],
      sideEffects: [
        '可能出现轻微胃肠道反应',
        '个别患者可能出现头晕、乏力',
        '如症状严重请立即停药并就医'
      ],
      contraindications: [
        '孕妇及哺乳期妇女慎用',
        '肝肾功能不全者慎用',
        '对本品过敏者禁用'
      ]
    };
  };

  // 发药项目表格列定义
  const columns: ColumnsType<any> = [
    {
      title: '药品名称',
      dataIndex: 'medicineName',
      key: 'medicineName',
      width: 200,
      render: (name: string, record: any) => (
        <Space direction="vertical" size="small">
          <Text strong>{name}</Text>
          <Text type="secondary" style={{ fontSize: '12px' }}>
            批号: {record.batchNumber || '未设置'}
          </Text>
          {record.expiryDate && (
            <Text type="secondary" style={{ fontSize: '12px' }}>
              有效期: {dayjs(record.expiryDate).format('YYYY-MM-DD')}
            </Text>
          )}
        </Space>
      )
    },
    {
      title: '发药数量',
      dataIndex: 'dispensedQuantity',
      key: 'dispensedQuantity',
      width: 100,
      render: (quantity: number) => (
        <Badge count={quantity} showZero style={{ backgroundColor: '#52c41a' }} />
      )
    },
    {
      title: '用法用量',
      key: 'dosage',
      width: 200,
      render: (_, record) => {
        const guidance = getMedicineGuidance(record);
        return (
          <Space direction="vertical" size="small">
            <Text>{guidance.dosage}</Text>
            <Text type="secondary" style={{ fontSize: '12px' }}>
              {guidance.frequency}
            </Text>
          </Space>
        );
      }
    },
    {
      title: '用药指导',
      key: 'guidance',
      width: 120,
      render: (_, record) => (
        <Button
          type="link"
          size="small"
          icon={<InfoCircleOutlined />}
          onClick={() => showMedicineGuidance(record)}
        >
          查看详情
        </Button>
      )
    },
    {
      title: '状态',
      key: 'status',
      width: 100,
      render: () => (
        <Tag color="success" icon={<CheckCircleOutlined />}>
          待发药
        </Tag>
      )
    }
  ];

  const deliverySteps = [
    {
      title: '核对信息',
      description: '核对患者身份和处方信息',
      icon: <UserOutlined />
    },
    {
      title: '用药指导',
      description: '向患者说明用药方法和注意事项',
      icon: <InfoCircleOutlined />
    },
    {
      title: '确认发药',
      description: '患者确认并签收药品',
      icon: <CheckCircleOutlined />
    }
  ];

  if (!dispenseRecord) {
    return (
      <Card>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <MedicineBoxOutlined style={{ fontSize: '48px', color: '#1890ff' }} />
          <Title level={4} style={{ marginTop: '16px' }}>
            请选择要发药的调剂记录
          </Title>
        </div>
      </Card>
    );
  }

  return (
    <div>
      {/* 发药流程步骤 */}
      <Card style={{ marginBottom: 16 }}>
        <Steps current={deliveryConfirmed ? 2 : 1} items={deliverySteps} />
      </Card>

      {/* 患者和处方信息 */}
      <Card title="患者信息确认" style={{ marginBottom: 16 }}>
        <Alert
          message="请核对患者身份信息"
          description="发药前必须核对患者身份，确保药品发放给正确的患者"
          type="warning"
          showIcon
          style={{ marginBottom: 16 }}
        />
        
        <Descriptions column={3} bordered>
          <Descriptions.Item label="患者姓名">
            <Space>
              <UserOutlined />
              <Text strong>{dispenseRecord.patientName}</Text>
            </Space>
          </Descriptions.Item>
          <Descriptions.Item label="处方编号">
            <Text code>{dispenseRecord.prescription?.prescriptionNumber}</Text>
          </Descriptions.Item>
          <Descriptions.Item label="开具医生">
            {dispenseRecord.doctorName}
          </Descriptions.Item>
          <Descriptions.Item label="调剂药师">
            {dispenseRecord.pharmacistName}
          </Descriptions.Item>
          <Descriptions.Item label="调剂完成时间">
            {dispenseRecord.completedTime ? 
              dayjs(dispenseRecord.completedTime).format('YYYY-MM-DD HH:mm:ss') : 
              '未完成'
            }
          </Descriptions.Item>
          <Descriptions.Item label="药品总数">
            <Badge count={dispenseItems.length} showZero />
          </Descriptions.Item>
        </Descriptions>
      </Card>

      {/* 发药项目列表 */}
      <Card 
        title={
          <Space>
            <MedicineBoxOutlined />
            发药清单
            <Badge count={dispenseItems.length} showZero />
          </Space>
        }
        style={{ marginBottom: 16 }}
      >
        <Table
          columns={columns}
          dataSource={dispenseItems}
          rowKey="id"
          pagination={false}
          size="middle"
        />
      </Card>

      {/* 发药确认 */}
      <Card title="发药确认" style={{ marginBottom: 16 }}>
        <Form form={deliveryForm} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <div style={{ marginBottom: 16 }}>
                <Title level={5}>患者确认事项</Title>
                <Space direction="vertical" style={{ width: '100%' }}>
                  <Checkbox
                    checked={patientConfirmation.identityVerified}
                    onChange={(e) => setPatientConfirmation(prev => ({
                      ...prev,
                      identityVerified: e.target.checked
                    }))}
                  >
                    已核对患者身份信息
                  </Checkbox>
                  <Checkbox
                    checked={patientConfirmation.medicinesExplained}
                    onChange={(e) => setPatientConfirmation(prev => ({
                      ...prev,
                      medicinesExplained: e.target.checked
                    }))}
                  >
                    已向患者说明药品用法用量
                  </Checkbox>
                  <Checkbox
                    checked={patientConfirmation.instructionsUnderstood}
                    onChange={(e) => setPatientConfirmation(prev => ({
                      ...prev,
                      instructionsUnderstood: e.target.checked
                    }))}
                  >
                    患者已理解用药注意事项
                  </Checkbox>
                  <Checkbox
                    checked={patientConfirmation.allergiesChecked}
                    onChange={(e) => setPatientConfirmation(prev => ({
                      ...prev,
                      allergiesChecked: e.target.checked
                    }))}
                  >
                    已确认患者无药物过敏史
                  </Checkbox>
                </Space>
              </div>
            </Col>
            <Col span={12}>
              <Form.Item label="发药备注" name="deliveryNotes">
                <TextArea
                  rows={4}
                  placeholder="请输入发药备注信息..."
                  defaultValue="药品已发放给患者，已告知用法用量和注意事项"
                />
              </Form.Item>
            </Col>
          </Row>
        </Form>

        <Divider />

        <Space>
          <Button
            type="primary"
            size="large"
            icon={<CheckCircleOutlined />}
            onClick={handleDeliverMedicine}
            loading={loading}
            disabled={deliveryConfirmed}
          >
            确认发药
          </Button>
          <Button
            icon={<PrinterOutlined />}
            onClick={printDeliveryRecord}
            disabled={!deliveryConfirmed}
          >
            打印发药记录
          </Button>
          <Button onClick={onCancel}>
            取消
          </Button>
        </Space>
      </Card>

      {/* 发药成功提示 */}
      {deliveryConfirmed && (
        <Card>
          <Alert
            message="发药完成"
            description={
              <Space direction="vertical">
                <Text>药品已成功发放给患者</Text>
                <Text type="secondary">
                  发药时间: {dayjs().format('YYYY-MM-DD HH:mm:ss')}
                </Text>
                <Text type="secondary">
                  发药药师: {currentPharmacistName}
                </Text>
              </Space>
            }
            type="success"
            showIcon
          />
        </Card>
      )}

      {/* 用药指导模态框 */}
      <Modal
        title={
          <Space>
            <HeartOutlined />
            用药指导 - {selectedMedicine?.medicineName}
          </Space>
        }
        open={guidanceModalVisible}
        onCancel={() => setGuidanceModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setGuidanceModalVisible(false)}>
            关闭
          </Button>,
          <Button key="print" type="primary" icon={<PrinterOutlined />}>
            打印指导单
          </Button>
        ]}
        width={800}
      >
        {selectedMedicine && (
          <div>
            <Descriptions column={2} bordered style={{ marginBottom: 16 }}>
              <Descriptions.Item label="药品名称">
                {selectedMedicine.medicineName}
              </Descriptions.Item>
              <Descriptions.Item label="发药数量">
                {selectedMedicine.dispensedQuantity}
              </Descriptions.Item>
              <Descriptions.Item label="批号">
                {selectedMedicine.batchNumber || '未设置'}
              </Descriptions.Item>
              <Descriptions.Item label="有效期">
                {selectedMedicine.expiryDate ? 
                  dayjs(selectedMedicine.expiryDate).format('YYYY-MM-DD') : 
                  '未设置'
                }
              </Descriptions.Item>
            </Descriptions>

            {(() => {
              const guidance = getMedicineGuidance(selectedMedicine);
              return (
                <div>
                  <Divider orientation="left">用法用量</Divider>
                  <Paragraph>
                    <Text strong>用法：</Text>{guidance.dosage}
                  </Paragraph>
                  <Paragraph>
                    <Text strong>频次：</Text>{guidance.frequency}
                  </Paragraph>
                  <Paragraph>
                    <Text strong>疗程：</Text>{guidance.duration}
                  </Paragraph>

                  <Divider orientation="left">注意事项</Divider>
                  <List
                    size="small"
                    dataSource={guidance.precautions}
                    renderItem={(item, index) => (
                      <List.Item>
                        <Text>{index + 1}. {item}</Text>
                      </List.Item>
                    )}
                  />

                  <Divider orientation="left">不良反应</Divider>
                  <List
                    size="small"
                    dataSource={guidance.sideEffects}
                    renderItem={(item, index) => (
                      <List.Item>
                        <Text type="warning">{index + 1}. {item}</Text>
                      </List.Item>
                    )}
                  />

                  <Divider orientation="left">禁忌症</Divider>
                  <List
                    size="small"
                    dataSource={guidance.contraindications}
                    renderItem={(item, index) => (
                      <List.Item>
                        <Text type="danger">{index + 1}. {item}</Text>
                      </List.Item>
                    )}
                  />
                </div>
              );
            })()}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default MedicineDelivery;