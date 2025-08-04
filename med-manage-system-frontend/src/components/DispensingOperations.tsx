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
  InputNumber,
  Select,
  DatePicker,
  Descriptions,
  List,
  Tag,
  Alert,
  Steps,
  Progress,
  Divider,
  Row,
  Col,
  Tooltip,
  Badge,
  Popconfirm
} from 'antd';
import {
  ExperimentOutlined,
  CheckCircleOutlined,
  CloseOutlined,
  EditOutlined,
  SwapOutlined,
  SaveOutlined,
  WarningOutlined,
  InfoCircleOutlined,
  MedicineBoxOutlined,
  ClockCircleOutlined,
  UserOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { pharmacyService } from '../services/pharmacyService';
import { inventoryService } from '../services/inventoryService';

const { Title, Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;
const { Step } = Steps;

interface DispensingOperationsProps {
  dispenseRecord: any;
  onComplete?: (record: any) => void;
  onCancel?: (record: any) => void;
  onReturn?: (record: any) => void;
}

const DispensingOperations: React.FC<DispensingOperationsProps> = ({
  dispenseRecord,
  onComplete,
  onCancel,
  onReturn
}) => {
  const [loading, setLoading] = useState(false);
  const [dispenseItems, setDispenseItems] = useState<any[]>([]);
  const [availableMedicines, setAvailableMedicines] = useState<any[]>([]);
  const [currentStep, setCurrentStep] = useState(0);
  const [form] = Form.useForm();
  const [substituteModalVisible, setSubstituteModalVisible] = useState(false);
  const [selectedItem, setSelectedItem] = useState<any>(null);
  const [substituteForm] = Form.useForm();

  // 当前药师信息
  const currentPharmacistId = 1;
  const currentPharmacistName = '药师张三';

  useEffect(() => {
    if (dispenseRecord) {
      loadDispenseItems();
      loadAvailableMedicines();
    }
  }, [dispenseRecord]);

  const loadDispenseItems = async () => {
    try {
      const record = await pharmacyService.getDispenseRecordWithItems(dispenseRecord.id);
      if (record && record.dispenseItems) {
        setDispenseItems(record.dispenseItems);
        // 根据调剂进度设置当前步骤
        const completedItems = record.dispenseItems.filter((item: any) => item.dispensedQuantity > 0);
        const progress = completedItems.length / record.dispenseItems.length;
        setCurrentStep(progress === 1 ? 2 : progress > 0 ? 1 : 0);
      }
    } catch (error) {
      console.error('加载调剂项目失败:', error);
      message.error('加载调剂项目失败');
    }
  };

  const loadAvailableMedicines = async () => {
    try {
      const medicines = await inventoryService.getMedicines();
      setAvailableMedicines(medicines);
    } catch (error) {
      console.error('加载药品列表失败:', error);
    }
  };

  const handleDispenseItem = async (item: any, dispensedQuantity: number, batchNumber?: string, notes?: string) => {
    try {
      setLoading(true);
      
      // 验证库存
      const medicine = availableMedicines.find(m => m.id === item.medicineId);
      if (!medicine || medicine.currentStock < dispensedQuantity) {
        message.error('库存不足，无法调剂');
        return;
      }

      const request = {
        dispenseRecordId: dispenseRecord.id,
        prescriptionItemId: item.prescriptionItemId,
        medicineId: item.medicineId,
        dispensedQuantity,
        batchNumber,
        notes
      };

      // 验证调剂请求
      const validation = pharmacyService.validateDispenseRequest(request);
      if (!validation.valid) {
        message.error(validation.errors.join(', '));
        return;
      }

      await pharmacyService.dispenseMedicineItem(request);
      message.success('药品调剂成功');
      
      // 更新库存（实际应该由后端处理）
      await inventoryService.updateStock(item.medicineId, {
        quantity: -dispensedQuantity,
        type: 'OUT',
        reason: '处方调剂',
        batchNumber
      });

      loadDispenseItems();
      loadAvailableMedicines();
    } catch (error) {
      console.error('调剂药品失败:', error);
      message.error('调剂药品失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSubstituteMedicine = async (values: any) => {
    try {
      setLoading(true);
      
      await pharmacyService.substituteMedicine(selectedItem.id, {
        newMedicineId: values.newMedicineId,
        reason: values.reason
      });
      
      message.success('药品替代成功');
      setSubstituteModalVisible(false);
      loadDispenseItems();
    } catch (error) {
      console.error('药品替代失败:', error);
      message.error('药品替代失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCompleteDispensing = async () => {
    try {
      setLoading(true);
      
      // 检查是否所有项目都已调剂
      const incompleteItems = dispenseItems.filter(item => 
        item.dispensedQuantity < item.prescribedQuantity
      );
      
      if (incompleteItems.length > 0) {
        Modal.confirm({
          title: '确认完成调剂',
          content: `还有 ${incompleteItems.length} 个药品项目未完全调剂，确认完成吗？`,
          onOk: async () => {
            await completeDispensing();
          }
        });
      } else {
        await completeDispensing();
      }
    } catch (error) {
      console.error('完成调剂失败:', error);
      message.error('完成调剂失败');
    } finally {
      setLoading(false);
    }
  };

  const completeDispensing = async () => {
    await pharmacyService.completeDispensing(dispenseRecord.id);
    message.success('调剂完成');
    setCurrentStep(2);
    
    if (onComplete) {
      onComplete(dispenseRecord);
    }
  };

  const handleReturnPrescription = (reason: string) => {
    Modal.confirm({
      title: '确认退回处方',
      content: `退回原因: ${reason}`,
      onOk: async () => {
        try {
          await pharmacyService.returnPrescription(dispenseRecord.id, { reason });
          message.success('处方已退回');
          
          if (onReturn) {
            onReturn(dispenseRecord);
          }
        } catch (error) {
          console.error('退回处方失败:', error);
          message.error('退回处方失败');
        }
      }
    });
  };

  const getStockStatus = (medicineId: number, requiredQuantity: number) => {
    const medicine = availableMedicines.find(m => m.id === medicineId);
    if (!medicine) return { status: 'unknown', color: 'default', text: '未知' };
    
    const stock = medicine.currentStock || 0;
    if (stock >= requiredQuantity) {
      return { status: 'sufficient', color: 'success', text: '充足' };
    } else if (stock > 0) {
      return { status: 'insufficient', color: 'warning', text: '不足' };
    } else {
      return { status: 'out', color: 'error', text: '缺货' };
    }
  };

  // 调剂项目表格列定义
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
            ID: {record.medicineId}
          </Text>
        </Space>
      )
    },
    {
      title: '处方数量',
      dataIndex: 'prescribedQuantity',
      key: 'prescribedQuantity',
      width: 100,
      render: (quantity: number) => (
        <Badge count={quantity} showZero style={{ backgroundColor: '#108ee9' }} />
      )
    },
    {
      title: '已调剂',
      dataIndex: 'dispensedQuantity',
      key: 'dispensedQuantity',
      width: 100,
      render: (quantity: number, record: any) => {
        const progress = (quantity / record.prescribedQuantity) * 100;
        return (
          <Space direction="vertical" size="small">
            <Badge count={quantity} showZero style={{ backgroundColor: '#52c41a' }} />
            <Progress 
              percent={progress} 
              size="small" 
              status={progress === 100 ? 'success' : 'active'}
              showInfo={false}
            />
          </Space>
        );
      }
    },
    {
      title: '库存状态',
      key: 'stockStatus',
      width: 100,
      render: (_, record) => {
        const remainingQuantity = record.prescribedQuantity - record.dispensedQuantity;
        const stockStatus = getStockStatus(record.medicineId, remainingQuantity);
        return (
          <Tag color={stockStatus.color} icon={<MedicineBoxOutlined />}>
            {stockStatus.text}
          </Tag>
        );
      }
    },
    {
      title: '批号/有效期',
      key: 'batchInfo',
      width: 150,
      render: (_, record) => (
        <Space direction="vertical" size="small">
          {record.batchNumber && (
            <Text style={{ fontSize: '12px' }}>
              批号: {record.batchNumber}
            </Text>
          )}
          {record.expiryDate && (
            <Text style={{ fontSize: '12px' }}>
              有效期: {dayjs(record.expiryDate).format('YYYY-MM-DD')}
            </Text>
          )}
        </Space>
      )
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      render: (_, record) => {
        const remainingQuantity = record.prescribedQuantity - record.dispensedQuantity;
        const stockStatus = getStockStatus(record.medicineId, remainingQuantity);
        const isCompleted = record.dispensedQuantity >= record.prescribedQuantity;
        
        return (
          <Space size="small" direction="vertical">
            {!isCompleted && stockStatus.status === 'sufficient' && (
              <Button
                type="primary"
                size="small"
                icon={<CheckCircleOutlined />}
                onClick={() => {
                  Modal.confirm({
                    title: '调剂药品',
                    content: (
                      <Form
                        layout="vertical"
                        initialValues={{
                          quantity: remainingQuantity,
                          batchNumber: '',
                          notes: ''
                        }}
                      >
                        <Form.Item
                          label="调剂数量"
                          name="quantity"
                          rules={[
                            { required: true, message: '请输入调剂数量' },
                            { type: 'number', min: 1, max: remainingQuantity, message: `数量应在1-${remainingQuantity}之间` }
                          ]}
                        >
                          <InputNumber min={1} max={remainingQuantity} style={{ width: '100%' }} />
                        </Form.Item>
                        <Form.Item label="批号" name="batchNumber">
                          <Input placeholder="请输入批号" />
                        </Form.Item>
                        <Form.Item label="备注" name="notes">
                          <TextArea rows={2} placeholder="调剂备注" />
                        </Form.Item>
                      </Form>
                    ),
                    onOk: (close) => {
                      const formData = form.getFieldsValue();
                      handleDispenseItem(
                        record, 
                        formData.quantity, 
                        formData.batchNumber, 
                        formData.notes
                      );
                      close();
                    }
                  });
                }}
              >
                调剂
              </Button>
            )}
            
            {!isCompleted && stockStatus.status !== 'sufficient' && (
              <Button
                type="default"
                size="small"
                icon={<SwapOutlined />}
                onClick={() => {
                  setSelectedItem(record);
                  setSubstituteModalVisible(true);
                }}
              >
                替代
              </Button>
            )}
            
            {isCompleted && (
              <Tag color="success" icon={<CheckCircleOutlined />}>
                已完成
              </Tag>
            )}
          </Space>
        );
      }
    }
  ];

  const steps = [
    {
      title: '开始调剂',
      description: '接收处方，开始调剂流程',
      icon: <ClockCircleOutlined />
    },
    {
      title: '药品配发',
      description: '逐项调剂处方药品',
      icon: <ExperimentOutlined />
    },
    {
      title: '调剂完成',
      description: '所有药品调剂完成',
      icon: <CheckCircleOutlined />
    }
  ];

  if (!dispenseRecord) {
    return (
      <Card>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <InfoCircleOutlined style={{ fontSize: '48px', color: '#1890ff' }} />
          <Title level={4} style={{ marginTop: '16px' }}>
            请选择要调剂的处方
          </Title>
        </div>
      </Card>
    );
  }

  return (
    <div>
      {/* 调剂进度 */}
      <Card style={{ marginBottom: 16 }}>
        <Steps current={currentStep} items={steps} />
      </Card>

      {/* 处方信息 */}
      <Card title="处方信息" style={{ marginBottom: 16 }}>
        <Descriptions column={3} bordered>
          <Descriptions.Item label="处方编号">
            {dispenseRecord.prescription?.prescriptionNumber}
          </Descriptions.Item>
          <Descriptions.Item label="患者姓名">
            <Space>
              <UserOutlined />
              {dispenseRecord.patientName}
            </Space>
          </Descriptions.Item>
          <Descriptions.Item label="医生姓名">
            {dispenseRecord.doctorName}
          </Descriptions.Item>
          <Descriptions.Item label="调剂药师">
            {dispenseRecord.pharmacistName}
          </Descriptions.Item>
          <Descriptions.Item label="开始时间">
            {dayjs(dispenseRecord.startTime).format('YYYY-MM-DD HH:mm:ss')}
          </Descriptions.Item>
          <Descriptions.Item label="调剂状态">
            <Badge 
              status={pharmacyService.formatDispenseStatus(dispenseRecord.status).badge as any}
              text={pharmacyService.formatDispenseStatus(dispenseRecord.status).text}
            />
          </Descriptions.Item>
        </Descriptions>
      </Card>

      {/* 调剂项目 */}
      <Card 
        title={
          <Space>
            <MedicineBoxOutlined />
            调剂项目
            <Badge count={dispenseItems.length} showZero />
          </Space>
        }
        extra={
          <Space>
            <Button
              type="primary"
              icon={<CheckCircleOutlined />}
              onClick={handleCompleteDispensing}
              loading={loading}
              disabled={currentStep >= 2}
            >
              完成调剂
            </Button>
            <Button
              danger
              icon={<CloseOutlined />}
              onClick={() => {
                Modal.confirm({
                  title: '退回处方',
                  content: (
                    <Form layout="vertical">
                      <Form.Item
                        label="退回原因"
                        name="reason"
                        rules={[{ required: true, message: '请输入退回原因' }]}
                      >
                        <Select placeholder="请选择退回原因">
                          <Option value="药品库存不足">药品库存不足</Option>
                          <Option value="处方信息不完整">处方信息不完整</Option>
                          <Option value="患者信息有误">患者信息有误</Option>
                          <Option value="药品相互作用">药品相互作用</Option>
                          <Option value="用药禁忌">用药禁忌</Option>
                          <Option value="剂量异常">剂量异常</Option>
                          <Option value="其他原因">其他原因</Option>
                        </Select>
                      </Form.Item>
                    </Form>
                  ),
                  onOk: (close) => {
                    const reason = form.getFieldValue('reason');
                    if (reason) {
                      handleReturnPrescription(reason);
                      close();
                    } else {
                      message.error('请选择退回原因');
                    }
                  }
                });
              }}
            >
              退回处方
            </Button>
          </Space>
        }
      >
        {/* 库存警告 */}
        {dispenseItems.some(item => {
          const remainingQuantity = item.prescribedQuantity - item.dispensedQuantity;
          const stockStatus = getStockStatus(item.medicineId, remainingQuantity);
          return stockStatus.status !== 'sufficient' && remainingQuantity > 0;
        }) && (
          <Alert
            message="库存警告"
            description="部分药品库存不足，可能需要药品替代"
            type="warning"
            showIcon
            style={{ marginBottom: 16 }}
          />
        )}

        <Table
          columns={columns}
          dataSource={dispenseItems}
          rowKey="id"
          loading={loading}
          pagination={false}
          size="middle"
        />
      </Card>

      {/* 药品替代模态框 */}
      <Modal
        title="药品替代"
        open={substituteModalVisible}
        onCancel={() => setSubstituteModalVisible(false)}
        footer={null}
        width={600}
      >
        <Form
          form={substituteForm}
          layout="vertical"
          onFinish={handleSubstituteMedicine}
        >
          <Alert
            message="原药品库存不足"
            description={`${selectedItem?.medicineName} 库存不足，请选择替代药品`}
            type="warning"
            showIcon
            style={{ marginBottom: 16 }}
          />
          
          <Form.Item
            label="替代药品"
            name="newMedicineId"
            rules={[{ required: true, message: '请选择替代药品' }]}
          >
            <Select
              placeholder="请选择替代药品"
              showSearch
              filterOption={(input, option) =>
                option?.children?.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
            >
              {availableMedicines
                .filter(medicine => 
                  medicine.id !== selectedItem?.medicineId && 
                  medicine.currentStock > 0
                )
                .map(medicine => (
                  <Option key={medicine.id} value={medicine.id}>
                    {medicine.name} (库存: {medicine.currentStock})
                  </Option>
                ))
              }
            </Select>
          </Form.Item>
          
          <Form.Item
            label="替代原因"
            name="reason"
            rules={[{ required: true, message: '请输入替代原因' }]}
          >
            <Select placeholder="请选择替代原因">
              <Option value="原药品缺货">原药品缺货</Option>
              <Option value="原药品过期">原药品过期</Option>
              <Option value="患者过敏">患者过敏</Option>
              <Option value="医生建议替代">医生建议替代</Option>
              <Option value="价格考虑">价格考虑</Option>
              <Option value="其他原因">其他原因</Option>
            </Select>
          </Form.Item>
          
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" loading={loading}>
                确认替代
              </Button>
              <Button onClick={() => setSubstituteModalVisible(false)}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default DispensingOperations;