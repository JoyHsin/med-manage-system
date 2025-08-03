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
  DatePicker,
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
  Statistic
} from 'antd';
import {
  MedicineBoxOutlined,
  PlusOutlined,
  SaveOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  PrinterOutlined,
  CopyOutlined,
  ExportOutlined,
  SearchOutlined,
  WarningOutlined,
  FileTextOutlined,
  UserOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { prescriptionService } from '../services/prescriptionService';
import { patientService } from '../services/patientService';
import {
  Prescription,
  CreatePrescriptionRequest,
  CreatePrescriptionItemRequest,
  Medicine,
  PrescriptionItem,
  PRESCRIPTION_STATUS_CONFIG,
  PRESCRIPTION_TYPE_CONFIG,
  USAGE_OPTIONS,
  FREQUENCY_OPTIONS,
  DURATION_OPTIONS,
  VALIDITY_DAYS_OPTIONS,
  MEDICINE_UNITS
} from '../types/prescription';

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

const PrescriptionManagement: React.FC = () => {
  const [form] = Form.useForm();
  const [itemForm] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [medicines, setMedicines] = useState<Medicine[]>([]);
  const [commonMedicines, setCommonMedicines] = useState<Medicine[]>([]);
  const [prescriptions, setPrescriptions] = useState<Prescription[]>([]);
  const [currentPrescription, setCurrentPrescription] = useState<Prescription | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [activeTab, setActiveTab] = useState('list');
  const [prescriptionItems, setPrescriptionItems] = useState<CreatePrescriptionItemRequest[]>([]);
  const [itemModalVisible, setItemModalVisible] = useState(false);
  const [editingItemIndex, setEditingItemIndex] = useState<number | null>(null);
  const [detailDrawerVisible, setDetailDrawerVisible] = useState(false);
  const [templateModalVisible, setTemplateModalVisible] = useState(false);
  const [templates, setTemplates] = useState<Prescription[]>([]);
  const [stats, setStats] = useState<any>(null);

  // 当前医生ID（实际应用中从认证状态获取）
  const currentDoctorId = 1;

  // 加载初始数据
  useEffect(() => {
    loadPatients();
    loadMedicines();
    loadCommonMedicines();
    loadPrescriptions();
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

  const loadMedicines = async () => {
    try {
      const data = await prescriptionService.getMedicines();
      setMedicines(data);
    } catch (error) {
      console.error('加载药品列表失败:', error);
      message.error('加载药品列表失败');
    }
  };

  const loadCommonMedicines = async () => {
    try {
      const data = await prescriptionService.getCommonMedicines(currentDoctorId);
      setCommonMedicines(data);
    } catch (error) {
      console.error('加载常用药品失败:', error);
    }
  };

  const loadPrescriptions = async () => {
    try {
      setLoading(true);
      const data = await prescriptionService.getDoctorPrescriptions(currentDoctorId);
      setPrescriptions(data);
    } catch (error) {
      console.error('加载处方列表失败:', error);
      message.error('加载处方列表失败');
    } finally {
      setLoading(false);
    }
  };

  const loadStats = async () => {
    try {
      const data = await prescriptionService.getPrescriptionStats(currentDoctorId);
      setStats(data);
    } catch (error) {
      console.error('加载统计数据失败:', error);
    }
  };

  // 新建处方
  const handleNewPrescription = () => {
    setCurrentPrescription(null);
    setIsEditing(true);
    setActiveTab('form');
    form.resetFields();
    form.setFieldsValue({
      prescriptionType: '普通处方',
      validityDays: 3,
      repeatTimes: 1,
      isEmergency: false,
      isChildPrescription: false,
      isChronicDiseasePrescription: false
    });
    setPrescriptionItems([]);
  };

  // 编辑处方
  const handleEditPrescription = (prescription: Prescription) => {
    if (prescription.status !== '草稿') {
      message.warning('只能编辑草稿状态的处方');
      return;
    }
    setCurrentPrescription(prescription);
    setIsEditing(true);
    setActiveTab('form');
    form.setFieldsValue({
      ...prescription,
      medicalRecordId: prescription.medicalRecordId
    });
    setPrescriptionItems(prescription.prescriptionItems?.map(item => ({
      medicineId: item.medicineId,
      quantity: item.quantity,
      unit: item.unit,
      usage: item.usage,
      dosage: item.dosage,
      frequency: item.frequency,
      duration: item.duration,
      specialInstructions: item.specialInstructions,
      sortOrder: item.sortOrder,
      remarks: item.remarks
    })) || []);
  };

  // 查看处方详情
  const handleViewPrescription = (prescription: Prescription) => {
    setCurrentPrescription(prescription);
    setDetailDrawerVisible(true);
  };

  // 保存处方
  const handleSavePrescription = async (values: any) => {
    if (prescriptionItems.length === 0) {
      message.error('请至少添加一个处方项目');
      return;
    }

    try {
      setLoading(true);
      const requestData: CreatePrescriptionRequest = {
        ...values,
        prescriptionItems
      };

      // 验证处方数据
      const validation = prescriptionService.validatePrescription(requestData);
      if (!validation.valid) {
        message.error(validation.errors[0]);
        return;
      }

      if (currentPrescription) {
        await prescriptionService.updatePrescription(currentPrescription.id, requestData);
        message.success('处方更新成功');
      } else {
        await prescriptionService.createPrescription(requestData);
        message.success('处方创建成功');
      }

      setIsEditing(false);
      setActiveTab('list');
      loadPrescriptions();
      loadStats();
    } catch (error) {
      console.error('保存处方失败:', error);
      message.error('保存处方失败');
    } finally {
      setLoading(false);
    }
  };

  // 开具处方
  const handleIssuePrescription = async (prescription: Prescription) => {
    try {
      await prescriptionService.issuePrescription(prescription.id);
      message.success('处方开具成功');
      loadPrescriptions();
      loadStats();
    } catch (error) {
      console.error('开具处方失败:', error);
      message.error('开具处方失败');
    }
  };

  // 审核处方
  const handleReviewPrescription = async (prescription: Prescription) => {
    Modal.confirm({
      title: '审核处方',
      content: (
        <div>
          <p>确认审核通过此处方吗？</p>
          <TextArea
            placeholder=\"审核意见（可选）\"
            rows={3}
            id=\"reviewComments\"
          />
        </div>
      ),
      onOk: async () => {
        try {
          const comments = (document.getElementById('reviewComments') as HTMLTextAreaElement)?.value;
          await prescriptionService.reviewPrescription(prescription.id, comments);
          message.success('处方审核成功');
          loadPrescriptions();
          loadStats();
        } catch (error) {
          console.error('审核处方失败:', error);
          message.error('审核处方失败');
        }
      }
    });
  };

  // 取消处方
  const handleCancelPrescription = async (prescription: Prescription) => {
    Modal.confirm({
      title: '取消处方',
      content: (
        <div>
          <p>确认取消此处方吗？</p>
          <TextArea
            placeholder=\"取消原因\"
            rows={3}
            id=\"cancelReason\"
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
          await prescriptionService.cancelPrescription(prescription.id, reason);
          message.success('处方取消成功');
          loadPrescriptions();
          loadStats();
        } catch (error) {
          console.error('取消处方失败:', error);
          message.error('取消处方失败');
        }
      }
    });
  };

  // 复制处方
  const handleCopyPrescription = async (prescription: Prescription) => {
    try {
      const newPrescription = await prescriptionService.copyPrescription(prescription.id);
      message.success('处方复制成功');
      handleEditPrescription(newPrescription);
    } catch (error) {
      console.error('复制处方失败:', error);
      message.error('复制处方失败');
    }
  };

  // 删除处方
  const handleDeletePrescription = async (prescription: Prescription) => {
    if (prescription.status !== '草稿') {
      message.warning('只能删除草稿状态的处方');
      return;
    }
    try {
      await prescriptionService.deletePrescription(prescription.id);
      message.success('处方删除成功');
      loadPrescriptions();
      loadStats();
    } catch (error) {
      console.error('删除处方失败:', error);
      message.error('删除处方失败');
    }
  };

  // 添加处方项目
  const handleAddItem = async (values: any) => {
    try {
      // 检查库存
      const stockCheck = await prescriptionService.checkMedicineStock(values.medicineId, values.quantity);
      if (!stockCheck.available) {
        message.warning(stockCheck.message);
      }

      const medicine = medicines.find(m => m.id === values.medicineId);
      const newItem: CreatePrescriptionItemRequest = {
        ...values,
        sortOrder: prescriptionItems.length + 1
      };

      if (editingItemIndex !== null) {
        const updatedItems = [...prescriptionItems];
        updatedItems[editingItemIndex] = newItem;
        setPrescriptionItems(updatedItems);
        message.success('处方项目更新成功');
      } else {
        setPrescriptionItems([...prescriptionItems, newItem]);
        message.success('处方项目添加成功');
      }

      setItemModalVisible(false);
      setEditingItemIndex(null);
      itemForm.resetFields();
    } catch (error) {
      console.error('操作处方项目失败:', error);
      message.error('操作处方项目失败');
    }
  };

  // 编辑处方项目
  const handleEditItem = (index: number) => {
    const item = prescriptionItems[index];
    setEditingItemIndex(index);
    itemForm.setFieldsValue(item);
    setItemModalVisible(true);
  };

  // 删除处方项目
  const handleDeleteItem = (index: number) => {
    const updatedItems = prescriptionItems.filter((_, i) => i !== index);
    setPrescriptionItems(updatedItems);
    message.success('处方项目删除成功');
  };

  // 搜索处方
  const handleSearch = async (values: any) => {
    try {
      setLoading(true);
      const data = await prescriptionService.searchPrescriptions(
        values.keyword,
        currentDoctorId,
        values.patientId,
        values.status,
        values.prescriptionType,
        values.startDate?.format('YYYY-MM-DD'),
        values.endDate?.format('YYYY-MM-DD')
      );
      setPrescriptions(data);
    } catch (error) {
      console.error('搜索处方失败:', error);
      message.error('搜索处方失败');
    } finally {
      setLoading(false);
    }
  };

  // 打印处方
  const handlePrintPrescription = async (prescription: Prescription) => {
    try {
      const printData = await prescriptionService.printPrescription(prescription.id);
      // 这里可以集成打印功能
      console.log('打印数据:', printData);
      message.success('处方打印成功');
    } catch (error) {
      console.error('打印处方失败:', error);
      message.error('打印处方失败');
    }
  };

  // 处方列表表格列定义
  const prescriptionColumns: ColumnsType<Prescription> = [
    {
      title: '处方编号',
      dataIndex: 'prescriptionNumber',
      key: 'prescriptionNumber',
      width: 150,
      render: (text: string) => (
        <Text code>{text}</Text>
      )
    },
    {
      title: '患者',
      dataIndex: 'patientName',
      key: 'patientName',
      width: 100,
      render: (name: string) => name || '患者' + (Math.floor(Math.random() * 100) + 1)
    },
    {
      title: '处方类型',
      dataIndex: 'prescriptionType',
      key: 'prescriptionType',
      width: 120,
      render: (type: string) => {
        const config = PRESCRIPTION_TYPE_CONFIG[type as keyof typeof PRESCRIPTION_TYPE_CONFIG];
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
        const config = PRESCRIPTION_STATUS_CONFIG[status as keyof typeof PRESCRIPTION_STATUS_CONFIG];
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
      render: (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')
    },
    {
      title: '总金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 100,
      render: (amount: number) => `¥${amount?.toFixed(2) || '0.00'}`
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      render: (_, record) => (
        <Space size=\"small\">
          <Button
            type=\"link\"
            size=\"small\"
            icon={<EyeOutlined />}
            onClick={() => handleViewPrescription(record)}
          >
            查看
          </Button>
          {record.status === '草稿' && (
            <>
              <Button
                type=\"link\"
                size=\"small\"
                icon={<EditOutlined />}
                onClick={() => handleEditPrescription(record)}
              >
                编辑
              </Button>
              <Button
                type=\"link\"
                size=\"small\"
                icon={<CheckCircleOutlined />}
                onClick={() => handleIssuePrescription(record)}
              >
                开具
              </Button>
            </>
          )}
          {record.status === '已开具' && (
            <Button
              type=\"link\"
              size=\"small\"
              icon={<CheckCircleOutlined />}
              onClick={() => handleReviewPrescription(record)}
            >
              审核
            </Button>
          )}
          <Button
            type=\"link\"
            size=\"small\"
            icon={<CopyOutlined />}
            onClick={() => handleCopyPrescription(record)}
          >
            复制
          </Button>
          {['草稿', '已开具', '已审核'].includes(record.status) && (
            <Button
              type=\"link\"
              size=\"small\"
              danger
              icon={<CloseCircleOutlined />}
              onClick={() => handleCancelPrescription(record)}
            >
              取消
            </Button>
          )}
          {record.status === '草稿' && (
            <Popconfirm
              title=\"确定删除此处方吗？\"
              onConfirm={() => handleDeletePrescription(record)}
            >
              <Button
                type=\"link\"
                size=\"small\"
                danger
                icon={<DeleteOutlined />}
              >
                删除
              </Button>
            </Popconfirm>
          )}
          <Button
            type=\"link\"
            size=\"small\"
            icon={<PrinterOutlined />}
            onClick={() => handlePrintPrescription(record)}
          >
            打印
          </Button>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <MedicineBoxOutlined style={{ marginRight: 8 }} />
        处方管理
      </Title>

      {/* 统计卡片 */}
      {stats && (
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={4}>
            <Card>
              <Statistic title=\"总处方数\" value={stats.totalPrescriptions} />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic title=\"已开具\" value={stats.issuedPrescriptions} />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic title=\"已审核\" value={stats.reviewedPrescriptions} />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic title=\"已调配\" value={stats.dispensedPrescriptions} />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic title=\"已取消\" value={stats.cancelledPrescriptions} />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title=\"总金额\" 
                value={stats.totalAmount} 
                precision={2}
                prefix=\"¥\"
              />
            </Card>
          </Col>
        </Row>
      )}

      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        {/* 处方列表 */}
        <TabPane tab=\"处方列表\" key=\"list\">
          <Card
            title=\"处方列表\"
            extra={
              <Space>
                <Button
                  type=\"primary\"
                  icon={<PlusOutlined />}
                  onClick={handleNewPrescription}
                >
                  新建处方
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
              layout=\"inline\"
              onFinish={handleSearch}
              style={{ marginBottom: 16 }}
            >
              <Form.Item name=\"keyword\">
                <Input
                  placeholder=\"搜索处方编号、患者姓名\"
                  prefix={<SearchOutlined />}
                  style={{ width: 200 }}
                />
              </Form.Item>
              <Form.Item name=\"patientId\">
                <Select
                  placeholder=\"选择患者\"
                  style={{ width: 150 }}
                  showSearch
                  optionFilterProp=\"children\"
                  allowClear
                >
                  {patients.map(patient => (
                    <Option key={patient.id} value={patient.id}>
                      {patient.name}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item name=\"status\">
                <Select placeholder=\"处方状态\" style={{ width: 120 }} allowClear>
                  <Option value=\"草稿\">草稿</Option>
                  <Option value=\"已开具\">已开具</Option>
                  <Option value=\"已审核\">已审核</Option>
                  <Option value=\"已调配\">已调配</Option>
                  <Option value=\"已发药\">已发药</Option>
                  <Option value=\"已取消\">已取消</Option>
                </Select>
              </Form.Item>
              <Form.Item name=\"prescriptionType\">
                <Select placeholder=\"处方类型\" style={{ width: 120 }} allowClear>
                  <Option value=\"普通处方\">普通处方</Option>
                  <Option value=\"急诊处方\">急诊处方</Option>
                  <Option value=\"儿科处方\">儿科处方</Option>
                  <Option value=\"麻醉处方\">麻醉处方</Option>
                  <Option value=\"精神药品处方\">精神药品处方</Option>
                  <Option value=\"毒性药品处方\">毒性药品处方</Option>
                </Select>
              </Form.Item>
              <Form.Item name=\"startDate\">
                <DatePicker placeholder=\"开始日期\" />
              </Form.Item>
              <Form.Item name=\"endDate\">
                <DatePicker placeholder=\"结束日期\" />
              </Form.Item>
              <Form.Item>
                <Button type=\"primary\" htmlType=\"submit\" icon={<SearchOutlined />}>
                  搜索
                </Button>
              </Form.Item>
              <Form.Item>
                <Button
                  onClick={() => {
                    searchForm.resetFields();
                    loadPrescriptions();
                  }}
                >
                  重置
                </Button>
              </Form.Item>
            </Form>

            <Table
              columns={prescriptionColumns}
              dataSource={prescriptions}
              rowKey=\"id\"
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

        {/* 处方表单 */}
        <TabPane tab={currentPrescription ? '编辑处方' : '新建处方'} key=\"form\" disabled={!isEditing}>
          <Card
            title={currentPrescription ? '编辑处方' : '新建处方'}
            extra={
              <Space>
                <Button
                  type=\"primary\"
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
              layout=\"vertical\"
              onFinish={handleSavePrescription}
            >
              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item
                    label=\"病历ID\"
                    name=\"medicalRecordId\"
                    rules={[{ required: true, message: '请输入病历ID' }]}
                  >
                    <InputNumber
                      style={{ width: '100%' }}
                      placeholder=\"请输入病历ID\"
                      min={1}
                    />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    label=\"处方类型\"
                    name=\"prescriptionType\"
                    rules={[{ required: true, message: '请选择处方类型' }]}
                  >
                    <Select>
                      <Option value=\"普通处方\">普通处方</Option>
                      <Option value=\"急诊处方\">急诊处方</Option>
                      <Option value=\"儿科处方\">儿科处方</Option>
                      <Option value=\"麻醉处方\">麻醉处方</Option>
                      <Option value=\"精神药品处方\">精神药品处方</Option>
                      <Option value=\"毒性药品处方\">毒性药品处方</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    label=\"有效期（天）\"
                    name=\"validityDays\"
                    rules={[{ required: true, message: '请选择有效期' }]}
                  >
                    <Select>
                      {VALIDITY_DAYS_OPTIONS.map(option => (
                        <Option key={option.value} value={option.value}>
                          {option.label}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item
                    label=\"重复次数\"
                    name=\"repeatTimes\"
                    rules={[{ required: true, message: '请输入重复次数' }]}
                  >
                    <InputNumber
                      style={{ width: '100%' }}
                      min={1}
                      max={10}
                    />
                  </Form.Item>
                </Col>
                <Col span={16}>
                  <Form.Item label=\"处方标识\">
                    <Space>
                      <Form.Item name=\"isEmergency\" valuePropName=\"checked\" noStyle>
                        <Checkbox>急诊处方</Checkbox>
                      </Form.Item>
                      <Form.Item name=\"isChildPrescription\" valuePropName=\"checked\" noStyle>
                        <Checkbox>儿童处方</Checkbox>
                      </Form.Item>
                      <Form.Item name=\"isChronicDiseasePrescription\" valuePropName=\"checked\" noStyle>
                        <Checkbox>慢性病处方</Checkbox>
                      </Form.Item>
                    </Space>
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item label=\"临床诊断\" name=\"clinicalDiagnosis\">
                <TextArea
                  rows={2}
                  placeholder=\"请输入临床诊断...\"
                  maxLength={500}
                  showCount
                />
              </Form.Item>

              <Form.Item label=\"用法用量说明\" name=\"dosageInstructions\">
                <TextArea
                  rows={3}
                  placeholder=\"请输入用法用量说明...\"
                  maxLength={1000}
                  showCount
                />
              </Form.Item>

              <Form.Item label=\"注意事项\" name=\"precautions\">
                <TextArea
                  rows={3}
                  placeholder=\"请输入注意事项...\"
                  maxLength={1000}
                  showCount
                />
              </Form.Item>

              <Form.Item label=\"备注\" name=\"remarks\">
                <TextArea
                  rows={2}
                  placeholder=\"请输入备注信息...\"
                  maxLength={1000}
                  showCount
                />
              </Form.Item>

              {/* 处方项目 */}
              <Divider>处方项目</Divider>
              <div style={{ marginBottom: 16 }}>
                <Button
                  type=\"dashed\"
                  icon={<PlusOutlined />}
                  onClick={() => {
                    setEditingItemIndex(null);
                    itemForm.resetFields();
                    setItemModalVisible(true);
                  }}
                  block
                >
                  添加药品
                </Button>
              </div>

              <List
                dataSource={prescriptionItems}
                renderItem={(item, index) => {
                  const medicine = medicines.find(m => m.id === item.medicineId);
                  return (
                    <List.Item
                      actions={[
                        <Button
                          type=\"link\"
                          size=\"small\"
                          onClick={() => handleEditItem(index)}
                        >
                          编辑
                        </Button>,
                        <Button
                          type=\"link\"
                          size=\"small\"
                          danger
                          onClick={() => handleDeleteItem(index)}
                        >
                          删除
                        </Button>
                      ]}
                    >
                      <List.Item.Meta
                        title={
                          <Space>
                            <Text strong>{medicine?.name || '未知药品'}</Text>
                            <Tag>{medicine?.specification}</Tag>
                          </Space>
                        }
                        description={
                          <div>
                            <div>
                              数量: {item.quantity} {item.unit} | 
                              用法: {item.usage} | 
                              用量: {item.dosage} | 
                              频次: {item.frequency}
                              {item.duration && ` | 疗程: ${item.duration}天`}
                            </div>
                            {item.specialInstructions && (
                              <div style={{ color: '#ff4d4f' }}>
                                特殊说明: {item.specialInstructions}
                              </div>
                            )}
                          </div>
                        }
                      />
                    </List.Item>
                  );
                }}
              />
            </Form>
          </Card>
        </TabPane>
      </Tabs>

      {/* 添加/编辑药品项目模态框 */}
      <Modal
        title={editingItemIndex !== null ? '编辑药品项目' : '添加药品项目'}
        open={itemModalVisible}
        onCancel={() => {
          setItemModalVisible(false);
          setEditingItemIndex(null);
          itemForm.resetFields();
        }}
        footer={null}
        width={800}
      >
        <Form
          form={itemForm}
          layout=\"vertical\"
          onFinish={handleAddItem}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label=\"药品\"
                name=\"medicineId\"
                rules={[{ required: true, message: '请选择药品' }]}
              >
                <Select
                  showSearch
                  placeholder=\"搜索并选择药品\"
                  optionFilterProp=\"children\"
                  filterOption={(input, option) =>
                    (option?.children as unknown as string)
                      ?.toLowerCase()
                      ?.includes(input.toLowerCase())
                  }
                >
                  {medicines.map(medicine => (
                    <Option key={medicine.id} value={medicine.id}>
                      {medicine.name} ({medicine.specification}) - ¥{medicine.unitPrice}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label=\"数量\"
                name=\"quantity\"
                rules={[{ required: true, message: '请输入数量' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder=\"数量\"
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label=\"单位\"
                name=\"unit\"
                rules={[{ required: true, message: '请选择单位' }]}
              >
                <Select placeholder=\"单位\">
                  {MEDICINE_UNITS.map(unit => (
                    <Option key={unit.value} value={unit.value}>
                      {unit.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item label=\"用法\" name=\"usage\">
                <Select placeholder=\"选择用法\" allowClear>
                  {USAGE_OPTIONS.map(usage => (
                    <Option key={usage.value} value={usage.value}>
                      {usage.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item label=\"用量\" name=\"dosage\">
                <Input placeholder=\"如：1片、5ml\" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item label=\"频次\" name=\"frequency\">
                <Select placeholder=\"选择频次\" allowClear>
                  {FREQUENCY_OPTIONS.map(freq => (
                    <Option key={freq.value} value={freq.value}>
                      {freq.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item label=\"疗程（天）\" name=\"duration\">
                <Select placeholder=\"选择疗程\" allowClear>
                  {DURATION_OPTIONS.map(duration => (
                    <Option key={duration.value} value={duration.value}>
                      {duration.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label=\"排序\" name=\"sortOrder\">
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder=\"排序序号\"
                />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item label=\"特殊说明\" name=\"specialInstructions\">
            <TextArea
              rows={2}
              placeholder=\"特殊用药说明...\"
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Form.Item label=\"备注\" name=\"remarks\">
            <TextArea
              rows={2}
              placeholder=\"备注信息...\"
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type=\"primary\" htmlType=\"submit\">
                {editingItemIndex !== null ? '更新' : '添加'}
              </Button>
              <Button
                onClick={() => {
                  setItemModalVisible(false);
                  setEditingItemIndex(null);
                  itemForm.resetFields();
                }}
              >
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {/* 处方详情抽屉 */}
      <Drawer
        title=\"处方详情\"
        placement=\"right\"
        width={600}
        open={detailDrawerVisible}
        onClose={() => setDetailDrawerVisible(false)}
      >
        {currentPrescription && (
          <div>
            <Descriptions column={2} bordered>
              <Descriptions.Item label=\"处方编号\">
                <Text code>{currentPrescription.prescriptionNumber}</Text>
              </Descriptions.Item>
              <Descriptions.Item label=\"处方类型\">
                <Tag color={PRESCRIPTION_TYPE_CONFIG[currentPrescription.prescriptionType].color}>
                  {PRESCRIPTION_TYPE_CONFIG[currentPrescription.prescriptionType].text}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label=\"状态\">
                <Badge 
                  status={PRESCRIPTION_STATUS_CONFIG[currentPrescription.status].badge as any} 
                  text={PRESCRIPTION_STATUS_CONFIG[currentPrescription.status].text} 
                />
              </Descriptions.Item>
              <Descriptions.Item label=\"开具时间\">
                {dayjs(currentPrescription.prescribedAt).format('YYYY-MM-DD HH:mm')}
              </Descriptions.Item>
              <Descriptions.Item label=\"有效期\">
                {currentPrescription.validityDays}天
              </Descriptions.Item>
              <Descriptions.Item label=\"总金额\">
                ¥{currentPrescription.totalAmount?.toFixed(2) || '0.00'}
              </Descriptions.Item>
            </Descriptions>

            {currentPrescription.clinicalDiagnosis && (
              <div style={{ marginTop: 16 }}>
                <Title level={5}>临床诊断</Title>
                <Paragraph>{currentPrescription.clinicalDiagnosis}</Paragraph>
              </div>
            )}

            {currentPrescription.dosageInstructions && (
              <div style={{ marginTop: 16 }}>
                <Title level={5}>用法用量说明</Title>
                <Paragraph>{currentPrescription.dosageInstructions}</Paragraph>
              </div>
            )}

            {currentPrescription.precautions && (
              <div style={{ marginTop: 16 }}>
                <Title level={5}>注意事项</Title>
                <Paragraph>{currentPrescription.precautions}</Paragraph>
              </div>
            )}

            <div style={{ marginTop: 16 }}>
              <Title level={5}>处方项目</Title>
              <List
                dataSource={currentPrescription.prescriptionItems || []}
                renderItem={(item) => (
                  <List.Item>
                    <List.Item.Meta
                      title={
                        <Space>
                          <Text strong>{item.medicineName}</Text>
                          {item.specification && <Tag>{item.specification}</Tag>}
                        </Space>
                      }
                      description={
                        <div>
                          <div>
                            数量: {item.quantity} {item.unit} | 
                            单价: ¥{item.unitPrice} | 
                            小计: ¥{item.subtotal}
                          </div>
                          {(item.usage || item.dosage || item.frequency) && (
                            <div>
                              {item.usage && `用法: ${item.usage}`}
                              {item.dosage && ` | 用量: ${item.dosage}`}
                              {item.frequency && ` | 频次: ${item.frequency}`}
                              {item.duration && ` | 疗程: ${item.duration}天`}
                            </div>
                          )}
                          {item.specialInstructions && (
                            <div style={{ color: '#ff4d4f' }}>
                              特殊说明: {item.specialInstructions}
                            </div>
                          )}
                        </div>
                      }
                    />
                  </List.Item>
                )}
              />
            </div>
          </div>
        )}
      </Drawer>
    </div>
  );
};

export default PrescriptionManagement;