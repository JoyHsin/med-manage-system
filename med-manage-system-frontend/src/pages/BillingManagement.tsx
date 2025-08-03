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
  Popconfirm,
  Drawer,
  Descriptions,
  Statistic,
  Progress
} from 'antd';
import {
  DollarOutlined,
  PlusOutlined,
  SaveOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  PrinterOutlined,
  ExportOutlined,
  SearchOutlined,
  PayCircleOutlined,
  CloseCircleOutlined,
  CalculatorOutlined,
  FileTextOutlined,
  CreditCardOutlined,
  UndoOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { billingService } from '../services/billingService';
import { patientService } from '../services/patientService';
import {
  Bill,
  BillItem,
  CreateBillRequest,
  CreateBillItemRequest,
  AddBillItemRequest,
  PaymentRequest,
  PaymentRecord,
  RefundRequest,
  BillingStats,
  BILL_STATUS_CONFIG,
  BILL_ITEM_TYPE_CONFIG,
  PAYMENT_METHOD_CONFIG,
  COMMON_BILL_ITEMS
} from '../types/billing';

const { Title, Text, Paragraph } = Typography;
const { Option } = Select;
const { TextArea } = Input;
const { TabPane } = Tabs;
const { confirm } = Modal;
const { RangePicker } = DatePicker;

interface Patient {
  id: number;
  name: string;
  phone: string;
  patientNumber: string;
  gender: string;
  age: number;
}

const BillingManagement: React.FC = () => {
  const [form] = Form.useForm();
  const [itemForm] = Form.useForm();
  const [paymentForm] = Form.useForm();
  const [refundForm] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [bills, setBills] = useState<Bill[]>([]);
  const [currentBill, setCurrentBill] = useState<Bill | null>(null);
  const [billItems, setBillItems] = useState<BillItem[]>([]);
  const [isEditing, setIsEditing] = useState(false);
  const [activeTab, setActiveTab] = useState('list');
  const [itemModalVisible, setItemModalVisible] = useState(false);
  const [paymentModalVisible, setPaymentModalVisible] = useState(false);
  const [refundModalVisible, setRefundModalVisible] = useState(false);
  const [detailDrawerVisible, setDetailDrawerVisible] = useState(false);
  const [editingItemIndex, setEditingItemIndex] = useState<number | null>(null);
  const [paymentRecords, setPaymentRecords] = useState<PaymentRecord[]>([]);
  const [stats, setStats] = useState<BillingStats | null>(null);

  // 加载初始数据
  useEffect(() => {
    loadPatients();
    loadBills();
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

  const loadBills = async () => {
    try {
      setLoading(true);
      // 这里应该调用获取所有账单的API，暂时使用搜索API
      const data = await billingService.searchBills();
      setBills(data);
    } catch (error) {
      console.error('加载账单列表失败:', error);
      message.error('加载账单列表失败');
    } finally {
      setLoading(false);
    }
  };

  const loadStats = async () => {
    try {
      const data = await billingService.getBillingStats();
      setStats(data);
    } catch (error) {
      console.error('加载统计数据失败:', error);
    }
  };

  // 新建账单
  const handleNewBill = () => {
    setCurrentBill(null);
    setIsEditing(true);
    setActiveTab('form');
    form.resetFields();
    setBillItems([]);
  };

  // 编辑账单
  const handleEditBill = (bill: Bill) => {
    if (bill.status !== 'PENDING') {
      message.warning('只能编辑待付款状态的账单');
      return;
    }
    setCurrentBill(bill);
    setIsEditing(true);
    setActiveTab('form');
    form.setFieldsValue(bill);
    setBillItems(bill.billItems || []);
  };

  // 查看账单详情
  const handleViewBill = async (bill: Bill) => {
    try {
      const [billData, items, payments] = await Promise.all([
        billingService.getBillById(bill.id),
        billingService.getBillItems(bill.id),
        billingService.getPaymentRecords(bill.id)
      ]);
      setCurrentBill({ ...billData, billItems: items });
      setBillItems(items);
      setPaymentRecords(payments);
      setDetailDrawerVisible(true);
    } catch (error) {
      console.error('加载账单详情失败:', error);
      message.error('加载账单详情失败');
    }
  };

  // 保存账单
  const handleSaveBill = async (values: any) => {
    if (billItems.length === 0) {
      message.error('请至少添加一个收费项目');
      return;
    }

    try {
      setLoading(true);
      const requestData: CreateBillRequest = {
        ...values,
        billItems: billItems.map(item => ({
          itemType: item.itemType,
          itemName: item.itemName,
          itemCode: item.itemCode,
          unitPrice: item.unitPrice,
          quantity: item.quantity,
          discount: item.discount || 0,
          specification: item.specification,
          notes: item.notes,
          prescriptionItemId: item.prescriptionItemId,
          medicalRecordId: item.medicalRecordId
        }))
      };

      // 验证账单数据
      const validation = billingService.validateBill(requestData);
      if (!validation.valid) {
        message.error(validation.errors[0]);
        return;
      }

      if (currentBill) {
        // 更新账单逻辑（需要后端支持）
        message.info('账单更新功能开发中');
      } else {
        await billingService.createBill(requestData);
        message.success('账单创建成功');
      }

      setIsEditing(false);
      setActiveTab('list');
      loadBills();
      loadStats();
    } catch (error) {
      console.error('保存账单失败:', error);
      message.error('保存账单失败');
    } finally {
      setLoading(false);
    }
  };

  // 添加收费项目
  const handleAddItem = async (values: any) => {
    try {
      const subtotal = billingService.calculateItemSubtotal(values.unitPrice, values.quantity);
      const discount = values.discount || 0;
      const actualAmount = billingService.calculateItemActualAmount(subtotal, discount);

      const newItem: BillItem = {
        id: Date.now(), // 临时ID
        billId: currentBill?.id || 0,
        ...values,
        subtotal,
        actualAmount
      };

      if (editingItemIndex !== null) {
        const updatedItems = [...billItems];
        updatedItems[editingItemIndex] = newItem;
        setBillItems(updatedItems);
        message.success('收费项目更新成功');
      } else {
        setBillItems([...billItems, newItem]);
        message.success('收费项目添加成功');
      }

      setItemModalVisible(false);
      setEditingItemIndex(null);
      itemForm.resetFields();
    } catch (error) {
      console.error('操作收费项目失败:', error);
      message.error('操作收费项目失败');
    }
  };

  // 编辑收费项目
  const handleEditItem = (index: number) => {
    const item = billItems[index];
    setEditingItemIndex(index);
    itemForm.setFieldsValue(item);
    setItemModalVisible(true);
  };

  // 删除收费项目
  const handleDeleteItem = (index: number) => {
    const updatedItems = billItems.filter((_, i) => i !== index);
    setBillItems(updatedItems);
    message.success('收费项目删除成功');
  };

  // 处理支付
  const handlePayment = async (values: any) => {
    try {
      const paymentRequest: PaymentRequest = {
        billId: currentBill!.id,
        paymentAmount: values.paymentAmount,
        paymentMethod: values.paymentMethod,
        paymentReference: values.paymentReference,
        notes: values.notes
      };

      // 验证支付金额
      const validation = billingService.validatePaymentAmount(
        currentBill!.totalAmount,
        currentBill!.paidAmount,
        values.paymentAmount
      );
      if (!validation.valid) {
        message.error(validation.error);
        return;
      }

      await billingService.processPayment(paymentRequest);
      message.success('支付处理成功');
      setPaymentModalVisible(false);
      paymentForm.resetFields();
      loadBills();
      loadStats();
      
      // 刷新当前账单详情
      if (detailDrawerVisible) {
        handleViewBill(currentBill!);
      }
    } catch (error) {
      console.error('支付处理失败:', error);
      message.error('支付处理失败');
    }
  };

  // 申请退费
  const handleRefund = async (values: any) => {
    try {
      const refundRequest: RefundRequest = {
        billId: currentBill!.id,
        refundAmount: values.refundAmount,
        refundReason: values.refundReason,
        refundMethod: values.refundMethod,
        notes: values.notes
      };

      // 验证退费金额
      const validation = billingService.validateRefundAmount(
        currentBill!.paidAmount,
        values.refundAmount
      );
      if (!validation.valid) {
        message.error(validation.error);
        return;
      }

      await billingService.requestRefund(refundRequest);
      message.success('退费申请提交成功');
      setRefundModalVisible(false);
      refundForm.resetFields();
      loadBills();
      loadStats();
    } catch (error) {
      console.error('退费申请失败:', error);
      message.error('退费申请失败');
    }
  };

  // 取消账单
  const handleCancelBill = async (bill: Bill) => {
    Modal.confirm({
      title: '取消账单',
      content: (
        <div>
          <p>确认取消此账单吗？</p>
          <TextArea
            placeholder="取消原因"
            rows={3}
            id="cancelReason"
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
          await billingService.cancelBill(bill.id, reason);
          message.success('账单取消成功');
          loadBills();
          loadStats();
        } catch (error) {
          console.error('取消账单失败:', error);
          message.error('取消账单失败');
        }
      }
    });
  };

  // 搜索账单
  const handleSearch = async (values: any) => {
    try {
      setLoading(true);
      const data = await billingService.searchBills(
        values.keyword,
        values.patientId,
        values.status,
        values.dateRange?.[0]?.format('YYYY-MM-DD'),
        values.dateRange?.[1]?.format('YYYY-MM-DD')
      );
      setBills(data);
    } catch (error) {
      console.error('搜索账单失败:', error);
      message.error('搜索账单失败');
    } finally {
      setLoading(false);
    }
  };

  // 打印账单
  const handlePrintBill = async (bill: Bill) => {
    try {
      const printData = await billingService.printBill(bill.id);
      // 这里可以集成打印功能
      console.log('打印数据:', printData);
      message.success('账单打印成功');
    } catch (error) {
      console.error('打印账单失败:', error);
      message.error('打印账单失败');
    }
  };

  // 从模板添加项目
  const handleAddFromTemplate = (itemType: string, template: any) => {
    const newItem: BillItem = {
      id: Date.now(),
      billId: currentBill?.id || 0,
      itemType: itemType as any,
      itemName: template.name,
      itemCode: template.code,
      unitPrice: template.unitPrice,
      quantity: 1,
      subtotal: template.unitPrice,
      discount: 0,
      actualAmount: template.unitPrice,
      specification: template.specification
    };
    setBillItems([...billItems, newItem]);
    message.success('收费项目添加成功');
  };

  // 账单列表表格列定义
  const billColumns: ColumnsType<Bill> = [
    {
      title: '账单编号',
      dataIndex: 'billNumber',
      key: 'billNumber',
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
      render: (name: string, record) => name || `患者${record.patientId}`
    },
    {
      title: '总金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 100,
      render: (amount: number) => `¥${amount?.toFixed(2) || '0.00'}`
    },
    {
      title: '已付金额',
      dataIndex: 'paidAmount',
      key: 'paidAmount',
      width: 100,
      render: (amount: number) => `¥${amount?.toFixed(2) || '0.00'}`
    },
    {
      title: '剩余金额',
      key: 'remainingAmount',
      width: 100,
      render: (_, record) => {
        const remaining = record.totalAmount - record.paidAmount;
        return (
          <Text type={remaining > 0 ? 'danger' : 'success'}>
            ¥{remaining.toFixed(2)}
          </Text>
        );
      }
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => {
        const config = BILL_STATUS_CONFIG[status as keyof typeof BILL_STATUS_CONFIG];
        return (
          <Badge status={config.badge as any} text={config.text} />
        );
      }
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 120,
      render: (date: string) => dayjs(date).format('MM-DD HH:mm')
    },
    {
      title: '操作',
      key: 'actions',
      width: 250,
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewBill(record)}
          >
            查看
          </Button>
          {record.status === 'PENDING' && (
            <>
              <Button
                type="link"
                size="small"
                icon={<EditOutlined />}
                onClick={() => handleEditBill(record)}
              >
                编辑
              </Button>
              <Button
                type="link"
                size="small"
                icon={<PayCircleOutlined />}
                onClick={() => {
                  setCurrentBill(record);
                  paymentForm.resetFields();
                  setPaymentModalVisible(true);
                }}
              >
                收费
              </Button>
            </>
          )}
          {(record.status === 'PAID' || record.status === 'PARTIALLY_PAID') && (
            <Button
              type="link"
              size="small"
              icon={<UndoOutlined />}
              onClick={() => {
                setCurrentBill(record);
                refundForm.resetFields();
                setRefundModalVisible(true);
              }}
            >
              退费
            </Button>
          )}
          <Button
            type="link"
            size="small"
            icon={<PrinterOutlined />}
            onClick={() => handlePrintBill(record)}
          >
            打印
          </Button>
          {record.status === 'PENDING' && (
            <Button
              type="link"
              size="small"
              danger
              icon={<CloseCircleOutlined />}
              onClick={() => handleCancelBill(record)}
            >
              取消
            </Button>
          )}
        </Space>
      )
    }
  ];

  // 收费项目表格列定义
  const itemColumns: ColumnsType<BillItem> = [
    {
      title: '项目类型',
      dataIndex: 'itemType',
      key: 'itemType',
      width: 100,
      render: (type: string) => {
        const config = BILL_ITEM_TYPE_CONFIG[type as keyof typeof BILL_ITEM_TYPE_CONFIG];
        return (
          <Tag color={config.color}>
            {config.icon} {config.text}
          </Tag>
        );
      }
    },
    {
      title: '项目名称',
      dataIndex: 'itemName',
      key: 'itemName',
      width: 150
    },
    {
      title: '单价',
      dataIndex: 'unitPrice',
      key: 'unitPrice',
      width: 80,
      render: (price: number) => `¥${price?.toFixed(2) || '0.00'}`
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 60
    },
    {
      title: '小计',
      dataIndex: 'subtotal',
      key: 'subtotal',
      width: 80,
      render: (amount: number) => `¥${amount?.toFixed(2) || '0.00'}`
    },
    {
      title: '折扣',
      dataIndex: 'discount',
      key: 'discount',
      width: 80,
      render: (amount: number) => `¥${amount?.toFixed(2) || '0.00'}`
    },
    {
      title: '实际金额',
      dataIndex: 'actualAmount',
      key: 'actualAmount',
      width: 100,
      render: (amount: number) => (
        <Text strong>¥{amount?.toFixed(2) || '0.00'}</Text>
      )
    },
    ...(isEditing ? [{
      title: '操作',
      key: 'actions',
      width: 120,
      render: (_: any, record: BillItem, index: number) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            onClick={() => handleEditItem(index)}
          >
            编辑
          </Button>
          <Button
            type="link"
            size="small"
            danger
            onClick={() => handleDeleteItem(index)}
          >
            删除
          </Button>
        </Space>
      )
    }] : [])
  ];

  const totalAmount = billItems.reduce((sum, item) => sum + item.actualAmount, 0);

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <DollarOutlined style={{ marginRight: 8 }} />
        收费管理
      </Title>

      {/* 统计卡片 */}
      {stats && (
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={4}>
            <Card>
              <Statistic title="总账单数" value={stats.totalBills} />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="待付款" 
                value={stats.pendingBills}
                valueStyle={{ color: '#faad14' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="已付款" 
                value={stats.paidBills}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="部分付款" 
                value={stats.partiallyPaidBills}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="今日收入" 
                value={stats.todayRevenue} 
                precision={2}
                prefix="¥"
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="本月收入" 
                value={stats.monthRevenue} 
                precision={2}
                prefix="¥"
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
        </Row>
      )}

      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        {/* 账单列表 */}
        <TabPane tab="账单列表" key="list">
          <Card
            title="账单列表"
            extra={
              <Space>
                <Button
                  type="primary"
                  icon={<PlusOutlined />}
                  onClick={handleNewBill}
                >
                  新建账单
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
              layout="inline"
              onFinish={handleSearch}
              style={{ marginBottom: 16 }}
            >
              <Form.Item name="keyword">
                <Input
                  placeholder="搜索账单编号、患者姓名"
                  prefix={<SearchOutlined />}
                  style={{ width: 200 }}
                />
              </Form.Item>
              <Form.Item name="patientId">
                <Select
                  placeholder="选择患者"
                  style={{ width: 150 }}
                  showSearch
                  optionFilterProp="children"
                  allowClear
                >
                  {patients.map(patient => (
                    <Option key={patient.id} value={patient.id}>
                      {patient.name}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item name="status">
                <Select placeholder="账单状态" style={{ width: 120 }} allowClear>
                  <Option value="PENDING">待付款</Option>
                  <Option value="PAID">已付款</Option>
                  <Option value="PARTIALLY_PAID">部分付款</Option>
                  <Option value="CANCELLED">已取消</Option>
                </Select>
              </Form.Item>
              <Form.Item name="dateRange">
                <RangePicker placeholder={['开始日期', '结束日期']} />
              </Form.Item>
              <Form.Item>
                <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                  搜索
                </Button>
              </Form.Item>
              <Form.Item>
                <Button
                  onClick={() => {
                    searchForm.resetFields();
                    loadBills();
                  }}
                >
                  重置
                </Button>
              </Form.Item>
            </Form>

            <Table
              columns={billColumns}
              dataSource={bills}
              rowKey="id"
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

        {/* 账单表单 */}
        <TabPane tab={currentBill ? '编辑账单' : '新建账单'} key="form" disabled={!isEditing}>
          <Card
            title={currentBill ? '编辑账单' : '新建账单'}
            extra={
              <Space>
                <Button
                  type="primary"
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
              layout="vertical"
              onFinish={handleSaveBill}
            >
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="患者"
                    name="patientId"
                    rules={[{ required: true, message: '请选择患者' }]}
                  >
                    <Select
                      placeholder="选择患者"
                      showSearch
                      optionFilterProp="children"
                    >
                      {patients.map(patient => (
                        <Option key={patient.id} value={patient.id}>
                          {patient.name} - {patient.patientNumber}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item label="挂号ID" name="registrationId">
                    <InputNumber
                      style={{ width: '100%' }}
                      placeholder="关联的挂号ID（可选）"
                    />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item label="备注" name="notes">
                <TextArea
                  rows={2}
                  placeholder="账单备注信息..."
                  maxLength={500}
                  showCount
                />
              </Form.Item>

              {/* 收费项目 */}
              <Divider>收费项目</Divider>
              <div style={{ marginBottom: 16 }}>
                <Space>
                  <Button
                    type="dashed"
                    icon={<PlusOutlined />}
                    onClick={() => {
                      setEditingItemIndex(null);
                      itemForm.resetFields();
                      setItemModalVisible(true);
                    }}
                  >
                    添加项目
                  </Button>
                  <Button
                    icon={<CalculatorOutlined />}
                    onClick={() => {
                      // 显示常用项目模板
                      Modal.info({
                        title: '常用收费项目',
                        width: 800,
                        content: (
                          <div>
                            {Object.entries(COMMON_BILL_ITEMS).map(([type, items]) => (
                              <div key={type} style={{ marginBottom: 16 }}>
                                <Title level={5}>
                                  {BILL_ITEM_TYPE_CONFIG[type as keyof typeof BILL_ITEM_TYPE_CONFIG].text}
                                </Title>
                                <List
                                  size="small"
                                  dataSource={items}
                                  renderItem={(item) => (
                                    <List.Item
                                      actions={[
                                        <Button
                                          type="link"
                                          size="small"
                                          onClick={() => handleAddFromTemplate(type, item)}
                                        >
                                          添加
                                        </Button>
                                      ]}
                                    >
                                      <List.Item.Meta
                                        title={item.name}
                                        description={`单价: ¥${item.unitPrice} | 规格: ${item.specification}`}
                                      />
                                    </List.Item>
                                  )}
                                />
                              </div>
                            ))}
                          </div>
                        )
                      });
                    }}
                  >
                    常用项目
                  </Button>
                </Space>
              </div>

              <Table
                columns={itemColumns}
                dataSource={billItems}
                rowKey="id"
                pagination={false}
                summary={() => (
                  <Table.Summary.Row>
                    <Table.Summary.Cell index={0} colSpan={6}>
                      <Text strong>合计</Text>
                    </Table.Summary.Cell>
                    <Table.Summary.Cell index={1}>
                      <Text strong style={{ color: '#f5222d' }}>
                        ¥{totalAmount.toFixed(2)}
                      </Text>
                    </Table.Summary.Cell>
                    {isEditing && <Table.Summary.Cell index={2} />}
                  </Table.Summary.Row>
                )}
              />
            </Form>
          </Card>
        </TabPane>
      </Tabs>

      {/* 添加/编辑收费项目模态框 */}
      <Modal
        title={editingItemIndex !== null ? '编辑收费项目' : '添加收费项目'}
        open={itemModalVisible}
        onCancel={() => {
          setItemModalVisible(false);
          setEditingItemIndex(null);
          itemForm.resetFields();
        }}
        footer={null}
        width={600}
      >
        <Form
          form={itemForm}
          layout="vertical"
          onFinish={handleAddItem}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="项目类型"
                name="itemType"
                rules={[{ required: true, message: '请选择项目类型' }]}
              >
                <Select placeholder="选择项目类型">
                  <Option value="REGISTRATION">📋 挂号费</Option>
                  <Option value="CONSULTATION">👨‍⚕️ 诊疗费</Option>
                  <Option value="MEDICINE">💊 药品费</Option>
                  <Option value="EXAMINATION">🔍 检查费</Option>
                  <Option value="TREATMENT">⚕️ 治疗费</Option>
                  <Option value="OTHER">📄 其他费用</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="项目名称"
                name="itemName"
                rules={[{ required: true, message: '请输入项目名称' }]}
              >
                <Input placeholder="请输入项目名称" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item label="项目编码" name="itemCode">
                <Input placeholder="项目编码" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="单价"
                name="unitPrice"
                rules={[{ required: true, message: '请输入单价' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={2}
                  placeholder="单价"
                  prefix="¥"
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="数量"
                name="quantity"
                rules={[{ required: true, message: '请输入数量' }]}
                initialValue={1}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="数量"
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item label="折扣金额" name="discount" initialValue={0}>
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={2}
                  placeholder="折扣金额"
                  prefix="¥"
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="规格/单位" name="specification">
                <Input placeholder="如：次、盒、支" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item label="备注" name="notes">
            <TextArea
              rows={2}
              placeholder="项目备注..."
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
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

      {/* 收费模态框 */}
      <Modal
        title="收费"
        open={paymentModalVisible}
        onCancel={() => {
          setPaymentModalVisible(false);
          paymentForm.resetFields();
        }}
        footer={null}
        width={500}
      >
        {currentBill && (
          <div>
            <Alert
              message={`账单编号: ${currentBill.billNumber}`}
              description={
                <div>
                  <div>总金额: ¥{currentBill.totalAmount.toFixed(2)}</div>
                  <div>已付金额: ¥{currentBill.paidAmount.toFixed(2)}</div>
                  <div>剩余金额: ¥{(currentBill.totalAmount - currentBill.paidAmount).toFixed(2)}</div>
                </div>
              }
              type="info"
              style={{ marginBottom: 16 }}
            />

            <Form
              form={paymentForm}
              layout="vertical"
              onFinish={handlePayment}
            >
              <Form.Item
                label="支付金额"
                name="paymentAmount"
                rules={[{ required: true, message: '请输入支付金额' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0.01}
                  max={currentBill.totalAmount - currentBill.paidAmount}
                  precision={2}
                  placeholder="支付金额"
                  prefix="¥"
                />
              </Form.Item>

              <Form.Item
                label="支付方式"
                name="paymentMethod"
                rules={[{ required: true, message: '请选择支付方式' }]}
              >
                <Select placeholder="选择支付方式">
                  <Option value="CASH">💵 现金</Option>
                  <Option value="CARD">💳 银行卡</Option>
                  <Option value="ALIPAY">📱 支付宝</Option>
                  <Option value="WECHAT">💬 微信支付</Option>
                  <Option value="INSURANCE">🏥 医保</Option>
                  <Option value="OTHER">💰 其他</Option>
                </Select>
              </Form.Item>

              <Form.Item label="支付凭证号" name="paymentReference">
                <Input placeholder="支付凭证号或交易号" />
              </Form.Item>

              <Form.Item label="备注" name="notes">
                <TextArea rows={2} placeholder="支付备注..." />
              </Form.Item>

              <Form.Item>
                <Space>
                  <Button type="primary" htmlType="submit">
                    确认收费
                  </Button>
                  <Button
                    onClick={() => {
                      setPaymentModalVisible(false);
                      paymentForm.resetFields();
                    }}
                  >
                    取消
                  </Button>
                </Space>
              </Form.Item>
            </Form>
          </div>
        )}
      </Modal>

      {/* 退费模态框 */}
      <Modal
        title="申请退费"
        open={refundModalVisible}
        onCancel={() => {
          setRefundModalVisible(false);
          refundForm.resetFields();
        }}
        footer={null}
        width={500}
      >
        {currentBill && (
          <div>
            <Alert
              message={`账单编号: ${currentBill.billNumber}`}
              description={
                <div>
                  <div>总金额: ¥{currentBill.totalAmount.toFixed(2)}</div>
                  <div>已付金额: ¥{currentBill.paidAmount.toFixed(2)}</div>
                </div>
              }
              type="info"
              style={{ marginBottom: 16 }}
            />

            <Form
              form={refundForm}
              layout="vertical"
              onFinish={handleRefund}
            >
              <Form.Item
                label="退费金额"
                name="refundAmount"
                rules={[{ required: true, message: '请输入退费金额' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0.01}
                  max={currentBill.paidAmount}
                  precision={2}
                  placeholder="退费金额"
                  prefix="¥"
                />
              </Form.Item>

              <Form.Item
                label="退费原因"
                name="refundReason"
                rules={[{ required: true, message: '请输入退费原因' }]}
              >
                <TextArea rows={3} placeholder="请详细说明退费原因..." />
              </Form.Item>

              <Form.Item
                label="退费方式"
                name="refundMethod"
                rules={[{ required: true, message: '请选择退费方式' }]}
              >
                <Select placeholder="选择退费方式">
                  <Option value="CASH">💵 现金</Option>
                  <Option value="CARD">💳 银行卡</Option>
                  <Option value="ALIPAY">📱 支付宝</Option>
                  <Option value="WECHAT">💬 微信支付</Option>
                  <Option value="OTHER">💰 其他</Option>
                </Select>
              </Form.Item>

              <Form.Item label="备注" name="notes">
                <TextArea rows={2} placeholder="退费备注..." />
              </Form.Item>

              <Form.Item>
                <Space>
                  <Button type="primary" htmlType="submit">
                    提交申请
                  </Button>
                  <Button
                    onClick={() => {
                      setRefundModalVisible(false);
                      refundForm.resetFields();
                    }}
                  >
                    取消
                  </Button>
                </Space>
              </Form.Item>
            </Form>
          </div>
        )}
      </Modal>

      {/* 账单详情抽屉 */}
      <Drawer
        title="账单详情"
        placement="right"
        width={800}
        open={detailDrawerVisible}
        onClose={() => setDetailDrawerVisible(false)}
      >
        {currentBill && (
          <div>
            <Descriptions column={2} bordered>
              <Descriptions.Item label="账单编号">
                <Text code>{currentBill.billNumber}</Text>
              </Descriptions.Item>
              <Descriptions.Item label="状态">
                <Badge 
                  status={BILL_STATUS_CONFIG[currentBill.status].badge as any} 
                  text={BILL_STATUS_CONFIG[currentBill.status].text} 
                />
              </Descriptions.Item>
              <Descriptions.Item label="患者">
                {currentBill.patientName || `患者${currentBill.patientId}`}
              </Descriptions.Item>
              <Descriptions.Item label="创建时间">
                {dayjs(currentBill.createdAt).format('YYYY-MM-DD HH:mm:ss')}
              </Descriptions.Item>
              <Descriptions.Item label="总金额">
                <Text strong>¥{currentBill.totalAmount.toFixed(2)}</Text>
              </Descriptions.Item>
              <Descriptions.Item label="已付金额">
                <Text strong style={{ color: '#52c41a' }}>
                  ¥{currentBill.paidAmount.toFixed(2)}
                </Text>
              </Descriptions.Item>
              <Descriptions.Item label="剩余金额" span={2}>
                <Text strong style={{ color: '#f5222d' }}>
                  ¥{(currentBill.totalAmount - currentBill.paidAmount).toFixed(2)}
                </Text>
              </Descriptions.Item>
            </Descriptions>

            {currentBill.notes && (
              <div style={{ marginTop: 16 }}>
                <Title level={5}>备注</Title>
                <Paragraph>{currentBill.notes}</Paragraph>
              </div>
            )}

            <div style={{ marginTop: 16 }}>
              <Title level={5}>收费项目</Title>
              <Table
                columns={itemColumns.filter(col => col.key !== 'actions')}
                dataSource={billItems}
                rowKey="id"
                pagination={false}
                size="small"
                summary={() => (
                  <Table.Summary.Row>
                    <Table.Summary.Cell index={0} colSpan={6}>
                      <Text strong>合计</Text>
                    </Table.Summary.Cell>
                    <Table.Summary.Cell index={1}>
                      <Text strong style={{ color: '#f5222d' }}>
                        ¥{billItems.reduce((sum, item) => sum + item.actualAmount, 0).toFixed(2)}
                      </Text>
                    </Table.Summary.Cell>
                  </Table.Summary.Row>
                )}
              />
            </div>

            {paymentRecords.length > 0 && (
              <div style={{ marginTop: 16 }}>
                <Title level={5}>支付记录</Title>
                <List
                  dataSource={paymentRecords}
                  renderItem={(payment) => (
                    <List.Item>
                      <List.Item.Meta
                        title={
                          <Space>
                            <Text strong>¥{payment.paymentAmount.toFixed(2)}</Text>
                            <Tag color={PAYMENT_METHOD_CONFIG[payment.paymentMethod].color}>
                              {PAYMENT_METHOD_CONFIG[payment.paymentMethod].icon} {PAYMENT_METHOD_CONFIG[payment.paymentMethod].text}
                            </Tag>
                          </Space>
                        }
                        description={
                          <div>
                            <div>支付时间: {dayjs(payment.paymentTime).format('YYYY-MM-DD HH:mm:ss')}</div>
                            {payment.paymentReference && (
                              <div>凭证号: {payment.paymentReference}</div>
                            )}
                            {payment.notes && (
                              <div>备注: {payment.notes}</div>
                            )}
                          </div>
                        }
                      />
                    </List.Item>
                  )}
                />
              </div>
            )}
          </div>
        )}
      </Drawer>
    </div>
  );
};

export default BillingManagement;