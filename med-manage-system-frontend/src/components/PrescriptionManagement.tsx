import React, { useState, useEffect } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Tag,
  Modal,
  Form,
  Input,
  Select,
  InputNumber,
  Switch,
  message,
  Typography,
  Divider,
  Alert,
  AutoComplete,
  Row,
  Col
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  PrinterOutlined,
  ExclamationCircleOutlined,
  MedicineBoxOutlined,
  WarningOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import {
  Prescription,
  PrescriptionItem,
  PrescriptionType,
  PrescriptionStatus,
  PRESCRIPTION_STATUS_CONFIG
} from '../types/medicalRecord';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { Option } = Select;
const { confirm } = Modal;

interface PrescriptionManagementProps {
  medicalRecordId: number;
  prescriptions: Prescription[];
  onPrescriptionChange: (prescriptions: Prescription[]) => void;
  readonly?: boolean;
}

interface Medicine {
  id: number;
  name: string;
  specification: string;
  unit: string;
  unitPrice: number;
  stockQuantity: number;
}

const PrescriptionManagement: React.FC<PrescriptionManagementProps> = ({
  medicalRecordId,
  prescriptions,
  onPrescriptionChange,
  readonly = false
}) => {
  const [form] = Form.useForm();
  const [itemForm] = Form.useForm();
  const [modalVisible, setModalVisible] = useState(false);
  const [itemModalVisible, setItemModalVisible] = useState(false);
  const [editingPrescription, setEditingPrescription] = useState<Prescription | null>(null);
  const [editingItem, setEditingItem] = useState<PrescriptionItem | null>(null);
  const [currentPrescriptionItems, setCurrentPrescriptionItems] = useState<PrescriptionItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [medicines, setMedicines] = useState<Medicine[]>([]);
  const [medicineOptions, setMedicineOptions] = useState<Medicine[]>([]);

  const isEditMode = !!editingPrescription;

  // 模拟药品数据
  useEffect(() => {
    const mockMedicines: Medicine[] = [
      { id: 1, name: '阿莫西林胶囊', specification: '0.25g', unit: '粒', unitPrice: 0.5, stockQuantity: 1000 },
      { id: 2, name: '布洛芬片', specification: '0.2g', unit: '片', unitPrice: 0.8, stockQuantity: 500 },
      { id: 3, name: '头孢克肟胶囊', specification: '0.1g', unit: '粒', unitPrice: 2.5, stockQuantity: 200 },
      { id: 4, name: '复方甘草片', specification: '/', unit: '片', unitPrice: 0.3, stockQuantity: 800 },
      { id: 5, name: '维生素C片', specification: '0.1g', unit: '片', unitPrice: 0.2, stockQuantity: 1500 }
    ];
    setMedicines(mockMedicines);
    setMedicineOptions(mockMedicines);
  }, []);

  // 生成处方编号
  const generatePrescriptionNumber = (): string => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const timestamp = now.getTime().toString().slice(-6);
    return `RX${year}${month}${day}${timestamp}`;
  };

  // 搜索药品
  const handleMedicineSearch = (value: string) => {
    if (value) {
      const filtered = medicines.filter(medicine =>
        medicine.name.toLowerCase().includes(value.toLowerCase())
      );
      setMedicineOptions(filtered);
    } else {
      setMedicineOptions(medicines);
    }
  };

  // 添加处方
  const handleAddPrescription = () => {
    setEditingPrescription(null);
    setCurrentPrescriptionItems([]);
    form.resetFields();
    form.setFieldsValue({
      prescriptionNumber: generatePrescriptionNumber(),
      prescriptionType: '普通处方',
      status: '草稿',
      validityDays: 3,
      isEmergency: false,
      isChildPrescription: false,
      isChronicDiseasePrescription: false,
      repeatTimes: 1
    });
    setModalVisible(true);
  };

  // 编辑处方
  const handleEditPrescription = (prescription: Prescription) => {
    setEditingPrescription(prescription);
    setCurrentPrescriptionItems(prescription.prescriptionItems || []);
    form.setFieldsValue(prescription);
    setModalVisible(true);
  };

  // 删除处方
  const handleDeletePrescription = (prescription: Prescription) => {
    confirm({
      title: '确认删除',
      content: `确定要删除处方"${prescription.prescriptionNumber}"吗？`,
      icon: <ExclamationCircleOutlined />,
      onOk: () => {
        const newPrescriptions = prescriptions.filter(p => p.id !== prescription.id);
        onPrescriptionChange(newPrescriptions);
        message.success('处方删除成功');
      }
    });
  };

  // 打印处方
  const handlePrintPrescription = (prescription: Prescription) => {
    message.info('处方打印功能开发中...');
  };

  // 保存处方
  const handleSavePrescription = async (values: any) => {
    try {
      setLoading(true);

      // 计算总金额
      const totalAmount = currentPrescriptionItems.reduce((sum, item) => {
        return sum + (item.unitPrice * item.quantity);
      }, 0);

      if (isEditMode && editingPrescription) {
        // 编辑模式
        const updatedPrescription: Prescription = {
          ...editingPrescription,
          ...values,
          totalAmount,
          prescriptionItems: currentPrescriptionItems
        };
        const newPrescriptions = prescriptions.map(p =>
          p.id === editingPrescription.id ? updatedPrescription : p
        );
        onPrescriptionChange(newPrescriptions);
        message.success('处方更新成功');
      } else {
        // 新增模式
        const newPrescription: Prescription = {
          id: Date.now(), // 临时ID，实际应该由后端生成
          medicalRecordId,
          doctorId: 1, // TODO: 从用户上下文获取
          prescribedAt: new Date().toISOString(),
          totalAmount,
          prescriptionItems: currentPrescriptionItems,
          ...values
        };
        const newPrescriptions = [...prescriptions, newPrescription];
        onPrescriptionChange(newPrescriptions);
        message.success('处方添加成功');
      }

      setModalVisible(false);
      form.resetFields();
      setEditingPrescription(null);
      setCurrentPrescriptionItems([]);
    } catch (error) {
      console.error('保存处方失败:', error);
      message.error('保存处方失败');
    } finally {
      setLoading(false);
    }
  };

  // 添加处方项目
  const handleAddPrescriptionItem = () => {
    setEditingItem(null);
    itemForm.resetFields();
    setItemModalVisible(true);
  };

  // 编辑处方项目
  const handleEditPrescriptionItem = (item: PrescriptionItem) => {
    setEditingItem(item);
    itemForm.setFieldsValue(item);
    setItemModalVisible(true);
  };

  // 删除处方项目
  const handleDeletePrescriptionItem = (item: PrescriptionItem) => {
    const newItems = currentPrescriptionItems.filter(i => i.id !== item.id);
    setCurrentPrescriptionItems(newItems);
    message.success('药品删除成功');
  };

  // 保存处方项目
  const handleSavePrescriptionItem = (values: any) => {
    const selectedMedicine = medicines.find(m => m.id === values.medicineId);
    if (!selectedMedicine) {
      message.error('请选择药品');
      return;
    }

    if (editingItem) {
      // 编辑模式
      const updatedItem: PrescriptionItem = {
        ...editingItem,
        ...values,
        medicineName: selectedMedicine.name,
        unit: selectedMedicine.unit,
        unitPrice: selectedMedicine.unitPrice
      };
      const newItems = currentPrescriptionItems.map(item =>
        item.id === editingItem.id ? updatedItem : item
      );
      setCurrentPrescriptionItems(newItems);
      message.success('药品更新成功');
    } else {
      // 新增模式
      const newItem: PrescriptionItem = {
        id: Date.now(), // 临时ID
        prescriptionId: 0, // 临时值
        medicineId: selectedMedicine.id,
        medicineName: selectedMedicine.name,
        specification: selectedMedicine.specification,
        unit: selectedMedicine.unit,
        unitPrice: selectedMedicine.unitPrice,
        ...values
      };
      setCurrentPrescriptionItems([...currentPrescriptionItems, newItem]);
      message.success('药品添加成功');
    }

    setItemModalVisible(false);
    itemForm.resetFields();
    setEditingItem(null);
  };

  // 处方表格列定义
  const prescriptionColumns: ColumnsType<Prescription> = [
    {
      title: '处方编号',
      dataIndex: 'prescriptionNumber',
      key: 'prescriptionNumber',
      width: 150
    },
    {
      title: '处方类型',
      dataIndex: 'prescriptionType',
      key: 'prescriptionType',
      width: 120,
      render: (type: PrescriptionType) => (
        <Tag color={type === '急诊处方' ? 'red' : 'blue'}>{type}</Tag>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: PrescriptionStatus) => {
        const config = PRESCRIPTION_STATUS_CONFIG[status];
        return (
          <Tag color={config.color}>{status}</Tag>
        );
      }
    },
    {
      title: '总金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 100,
      render: (amount: number) => `¥${amount?.toFixed(2) || '0.00'}`
    },
    {
      title: '开具时间',
      dataIndex: 'prescribedAt',
      key: 'prescribedAt',
      width: 120,
      render: (time: string) => dayjs(time).format('MM-DD HH:mm')
    },
    {
      title: '有效期',
      dataIndex: 'validityDays',
      key: 'validityDays',
      width: 80,
      render: (days: number) => `${days}天`
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      render: (_, record) => (
        <Space size="small">
          {!readonly && (
            <>
              <Button
                type="link"
                size="small"
                icon={<EditOutlined />}
                onClick={() => handleEditPrescription(record)}
              >
                编辑
              </Button>
              <Button
                type="link"
                size="small"
                danger
                icon={<DeleteOutlined />}
                onClick={() => handleDeletePrescription(record)}
              >
                删除
              </Button>
            </>
          )}
          <Button
            type="link"
            size="small"
            icon={<PrinterOutlined />}
            onClick={() => handlePrintPrescription(record)}
          >
            打印
          </Button>
        </Space>
      )
    }
  ];

  // 处方项目表格列定义
  const itemColumns: ColumnsType<PrescriptionItem> = [
    {
      title: '药品名称',
      dataIndex: 'medicineName',
      key: 'medicineName',
      width: 150
    },
    {
      title: '规格',
      dataIndex: 'specification',
      key: 'specification',
      width: 100
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 80,
      render: (quantity: number, record) => `${quantity} ${record.unit}`
    },
    {
      title: '单价',
      dataIndex: 'unitPrice',
      key: 'unitPrice',
      width: 80,
      render: (price: number) => `¥${price.toFixed(2)}`
    },
    {
      title: '小计',
      key: 'subtotal',
      width: 80,
      render: (_, record) => `¥${(record.unitPrice * record.quantity).toFixed(2)}`
    },
    {
      title: '用法用量',
      dataIndex: 'dosage',
      key: 'dosage',
      width: 120,
      ellipsis: true
    },
    {
      title: '频次',
      dataIndex: 'frequency',
      key: 'frequency',
      width: 100
    },
    {
      title: '疗程',
      dataIndex: 'duration',
      key: 'duration',
      width: 80
    },
    {
      title: '操作',
      key: 'actions',
      width: 120,
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEditPrescriptionItem(record)}
          >
            编辑
          </Button>
          <Button
            type="link"
            size="small"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDeletePrescriptionItem(record)}
          >
            删除
          </Button>
        </Space>
      )
    }
  ];

  return (
    <div>
      <Card
        title="处方信息"
        extra={
          !readonly && (
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={handleAddPrescription}
            >
              开具处方
            </Button>
          )
        }
      >
        {prescriptions.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
            <MedicineBoxOutlined style={{ fontSize: '48px', marginBottom: '16px', display: 'block' }} />
            暂无处方信息
          </div>
        ) : (
          <Table
            columns={prescriptionColumns}
            dataSource={prescriptions}
            rowKey="id"
            pagination={false}
            size="small"
          />
        )}
      </Card>

      {/* 处方编辑模态框 */}
      <Modal
        title={isEditMode ? '编辑处方' : '开具处方'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
          setEditingPrescription(null);
          setCurrentPrescriptionItems([]);
        }}
        footer={null}
        width={1200}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSavePrescription}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="处方编号"
                name="prescriptionNumber"
                rules={[{ required: true, message: '请输入处方编号' }]}
              >
                <Input disabled />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="处方类型"
                name="prescriptionType"
                rules={[{ required: true, message: '请选择处方类型' }]}
              >
                <Select>
                  <Option value="普通处方">普通处方</Option>
                  <Option value="急诊处方">急诊处方</Option>
                  <Option value="儿科处方">儿科处方</Option>
                  <Option value="麻醉处方">麻醉处方</Option>
                  <Option value="精神药品处方">精神药品处方</Option>
                  <Option value="毒性药品处方">毒性药品处方</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item
                label="有效期（天）"
                name="validityDays"
                rules={[{ required: true, message: '请输入有效期' }]}
              >
                <InputNumber min={1} max={30} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="重复次数"
                name="repeatTimes"
                rules={[{ required: true, message: '请输入重复次数' }]}
              >
                <InputNumber min={1} max={10} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Space>
                <Form.Item
                  label="急诊处方"
                  name="isEmergency"
                  valuePropName="checked"
                >
                  <Switch />
                </Form.Item>
                <Form.Item
                  label="儿童处方"
                  name="isChildPrescription"
                  valuePropName="checked"
                >
                  <Switch />
                </Form.Item>
                <Form.Item
                  label="慢性病处方"
                  name="isChronicDiseasePrescription"
                  valuePropName="checked"
                >
                  <Switch />
                </Form.Item>
              </Space>
            </Col>
          </Row>

          <Form.Item
            label="临床诊断"
            name="clinicalDiagnosis"
          >
            <Input placeholder="请输入临床诊断" />
          </Form.Item>

          <Form.Item
            label="用法用量说明"
            name="dosageInstructions"
          >
            <TextArea
              rows={2}
              placeholder="整体用法用量说明..."
              maxLength={1000}
              showCount
            />
          </Form.Item>

          <Form.Item
            label="注意事项"
            name="precautions"
          >
            <TextArea
              rows={2}
              placeholder="用药注意事项..."
              maxLength={1000}
              showCount
            />
          </Form.Item>

          <Divider />

          {/* 处方项目管理 */}
          <div style={{ marginBottom: 16 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <Title level={4} style={{ margin: 0 }}>处方药品</Title>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={handleAddPrescriptionItem}
              >
                添加药品
              </Button>
            </div>

            {currentPrescriptionItems.length === 0 ? (
              <Alert
                message="请添加处方药品"
                type="warning"
                showIcon
              />
            ) : (
              <>
                <Table
                  columns={itemColumns}
                  dataSource={currentPrescriptionItems}
                  rowKey="id"
                  pagination={false}
                  size="small"
                  summary={() => (
                    <Table.Summary.Row>
                      <Table.Summary.Cell index={0} colSpan={4}>
                        <strong>总计</strong>
                      </Table.Summary.Cell>
                      <Table.Summary.Cell index={4}>
                        <strong>
                          ¥{currentPrescriptionItems.reduce((sum, item) => 
                            sum + (item.unitPrice * item.quantity), 0
                          ).toFixed(2)}
                        </strong>
                      </Table.Summary.Cell>
                      <Table.Summary.Cell index={5} colSpan={4} />
                    </Table.Summary.Row>
                  )}
                />
              </>
            )}
          </div>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" loading={loading}>
                保存处方
              </Button>
              <Button onClick={() => {
                setModalVisible(false);
                form.resetFields();
                setEditingPrescription(null);
                setCurrentPrescriptionItems([]);
              }}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {/* 处方项目编辑模态框 */}
      <Modal
        title={editingItem ? '编辑药品' : '添加药品'}
        open={itemModalVisible}
        onCancel={() => {
          setItemModalVisible(false);
          itemForm.resetFields();
          setEditingItem(null);
        }}
        footer={null}
        width={600}
      >
        <Form
          form={itemForm}
          layout="vertical"
          onFinish={handleSavePrescriptionItem}
        >
          <Form.Item
            label="药品"
            name="medicineId"
            rules={[{ required: true, message: '请选择药品' }]}
          >
            <AutoComplete
              options={medicineOptions.map(medicine => ({
                value: medicine.id,
                label: `${medicine.name} (${medicine.specification}) - 库存: ${medicine.stockQuantity}${medicine.unit}`
              }))}
              onSearch={handleMedicineSearch}
              placeholder="搜索药品名称..."
              filterOption={false}
            />
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="数量"
                name="quantity"
                rules={[{ required: true, message: '请输入数量' }]}
              >
                <InputNumber min={1} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="用法用量"
                name="dosage"
                rules={[{ required: true, message: '请输入用法用量' }]}
              >
                <Input placeholder="如：每次1片" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="频次"
                name="frequency"
                rules={[{ required: true, message: '请输入频次' }]}
              >
                <Select>
                  <Option value="每日1次">每日1次</Option>
                  <Option value="每日2次">每日2次</Option>
                  <Option value="每日3次">每日3次</Option>
                  <Option value="每日4次">每日4次</Option>
                  <Option value="每8小时1次">每8小时1次</Option>
                  <Option value="每12小时1次">每12小时1次</Option>
                  <Option value="必要时服用">必要时服用</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="疗程"
                name="duration"
                rules={[{ required: true, message: '请输入疗程' }]}
              >
                <Select>
                  <Option value="3天">3天</Option>
                  <Option value="5天">5天</Option>
                  <Option value="7天">7天</Option>
                  <Option value="10天">10天</Option>
                  <Option value="14天">14天</Option>
                  <Option value="1个月">1个月</Option>
                  <Option value="长期">长期</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="特殊说明"
            name="instructions"
          >
            <TextArea
              rows={2}
              placeholder="特殊用药说明..."
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                保存
              </Button>
              <Button onClick={() => {
                setItemModalVisible(false);
                itemForm.resetFields();
                setEditingItem(null);
              }}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default PrescriptionManagement;