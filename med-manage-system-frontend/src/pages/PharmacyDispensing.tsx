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
  Progress,
  Steps
} from 'antd';
import {
  ExperimentOutlined,
  PlusOutlined,
  CheckOutlined,
  EyeOutlined,
  EditOutlined,
  CloseOutlined,
  SearchOutlined,
  ClockCircleOutlined,
  UserOutlined,
  MedicineBoxOutlined,
  WarningOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { pharmacyService } from '../services/pharmacyService';

const { Title, Text, Paragraph } = Typography;
const { Option } = Select;
const { TextArea } = Input;
const { TabPane } = Tabs;
const { confirm } = Modal;
const { Step } = Steps;

const PharmacyDispensing: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [pendingPrescriptions, setPendingPrescriptions] = useState<any[]>([]);
  const [inProgressDispenses, setInProgressDispenses] = useState<any[]>([]);
  const [readyForDelivery, setReadyForDelivery] = useState<any[]>([]);
  const [needingReview, setNeedingReview] = useState<any[]>([]);
  const [activeTab, setActiveTab] = useState('pending');
  const [stats, setStats] = useState<any>(null);

  // 当前药师ID（实际应用中从认证状态获取）
  const currentPharmacistId = 1;
  const currentPharmacistName = '药师张三';

  // 加载初始数据
  useEffect(() => {
    loadAllData();
    loadStats();
    // 设置定时刷新
    const interval = setInterval(loadAllData, 30000); // 30秒刷新一次
    return () => clearInterval(interval);
  }, []);

  const loadAllData = async () => {
    try {
      setLoading(true);
      const [pending, inProgress, ready, review] = await Promise.all([
        pharmacyService.getPendingPrescriptions(),
        pharmacyService.getInProgressDispenses(),
        pharmacyService.getReadyForDelivery(),
        pharmacyService.getNeedingReview()
      ]);
      setPendingPrescriptions(pending);
      setInProgressDispenses(inProgress);
      setReadyForDelivery(ready);
      setNeedingReview(review);
    } catch (error) {
      console.error('加载数据失败:', error);
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const loadStats = async () => {
    try {
      const data = await pharmacyService.getPharmacyStats(currentPharmacistId);
      setStats(data);
    } catch (error) {
      console.error('加载统计数据失败:', error);
    }
  };

  // 开始调剂
  const handleStartDispensing = async (prescription: any) => {
    try {
      // 检查调剂资格
      const eligibility = await pharmacyService.checkDispenseEligibility(prescription.id);
      if (!eligibility.eligible) {
        message.error(`无法调剂: ${eligibility.reason}`);
        return;
      }

      if (eligibility.warnings.length > 0) {
        Modal.warning({
          title: '调剂警告',
          content: (
            <div>
              {eligibility.warnings.map((warning, index) => (
                <div key={index} style={{ marginBottom: 8 }}>
                  <WarningOutlined style={{ color: '#faad14', marginRight: 8 }} />
                  {warning}
                </div>
              ))}
              <p style={{ marginTop: 16 }}>是否继续调剂？</p>
            </div>
          ),
          onOk: () => startDispensing(prescription)
        });
      } else {
        await startDispensing(prescription);
      }
    } catch (error) {
      console.error('开始调剂失败:', error);
      message.error('开始调剂失败');
    }
  };

  const startDispensing = async (prescription: any) => {
    try {
      await pharmacyService.startDispensing({
        prescriptionId: prescription.id,
        pharmacistId: currentPharmacistId,
        pharmacistName: currentPharmacistName
      });
      message.success('开始调剂成功');
      loadAllData();
    } catch (error) {
      console.error('开始调剂失败:', error);
      message.error('开始调剂失败');
    }
  };

  // 完成调剂
  const handleCompleteDispensing = async (dispenseRecord: any) => {
    try {
      await pharmacyService.completeDispensing(dispenseRecord.id);
      message.success('调剂完成');
      loadAllData();
    } catch (error) {
      console.error('完成调剂失败:', error);
      message.error('完成调剂失败');
    }
  };

  // 发药
  const handleDeliverMedicine = async (dispenseRecord: any) => {
    try {
      await pharmacyService.deliverMedicine(dispenseRecord.id, {
        dispensingPharmacistId: currentPharmacistId,
        dispensingPharmacistName: currentPharmacistName,
        deliveryNotes: '药品已发放给患者'
      });
      message.success('发药成功');
      loadAllData();
    } catch (error) {
      console.error('发药失败:', error);
      message.error('发药失败');
    }
  };

  // 退回处方
  const handleReturnPrescription = (dispenseRecord: any) => {
    Modal.confirm({
      title: '退回处方',
      content: (
        <div>
          <p>确认退回此处方吗？</p>
          <TextArea
            placeholder=\"退回原因\"
            rows={3}
            id=\"returnReason\"
            required
          />
        </div>
      ),
      onOk: async () => {
        try {
          const reason = (document.getElementById('returnReason') as HTMLTextAreaElement)?.value;
          if (!reason) {
            message.error('请输入退回原因');
            return;
          }
          await pharmacyService.returnPrescription(dispenseRecord.id, { reason });
          message.success('处方退回成功');
          loadAllData();
        } catch (error) {
          console.error('退回处方失败:', error);
          message.error('退回处方失败');
        }
      }
    });
  };

  // 待调剂处方表格列定义
  const pendingColumns: ColumnsType<any> = [
    {
      title: '处方编号',
      dataIndex: 'prescriptionNumber',
      key: 'prescriptionNumber',
      width: 150,
      render: (text: string) => <Text code>{text}</Text>
    },
    {
      title: '患者',
      dataIndex: 'patientName',
      key: 'patientName',
      width: 100,
      render: (name: string) => name || '患者' + (Math.floor(Math.random() * 100) + 1)
    },
    {
      title: '医生',
      dataIndex: 'doctorName',
      key: 'doctorName',
      width: 100,
      render: (name: string) => name || '医生' + (Math.floor(Math.random() * 10) + 1)
    },
    {
      title: '开具时间',
      dataIndex: 'prescribedAt',
      key: 'prescribedAt',
      width: 120,
      render: (date: string) => dayjs(date).format('MM-DD HH:mm')
    },
    {
      title: '药品数量',
      key: 'itemCount',
      width: 80,
      render: (_, record) => record.prescriptionItems?.length || 0
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
      width: 120,
      render: (_, record) => (
        <Space size=\"small\">
          <Button
            type=\"primary\"
            size=\"small\"
            icon={<CheckOutlined />}
            onClick={() => handleStartDispensing(record)}
          >
            开始调剂
          </Button>
          <Button
            type=\"link\"
            size=\"small\"
            icon={<EyeOutlined />}
          >
            查看
          </Button>
        </Space>
      )
    }
  ];

  // 调剂中记录表格列定义
  const inProgressColumns: ColumnsType<any> = [
    {
      title: '处方编号',
      dataIndex: ['prescription', 'prescriptionNumber'],
      key: 'prescriptionNumber',
      width: 150,
      render: (text: string) => <Text code>{text}</Text>
    },
    {
      title: '患者',
      dataIndex: 'patientName',
      key: 'patientName',
      width: 100
    },
    {
      title: '药师',
      dataIndex: 'pharmacistName',
      key: 'pharmacistName',
      width: 100
    },
    {
      title: '开始时间',
      dataIndex: 'startTime',
      key: 'startTime',
      width: 120,
      render: (date: string) => dayjs(date).format('MM-DD HH:mm')
    },
    {
      title: '耗时',
      key: 'duration',
      width: 80,
      render: (_, record) => {
        const duration = pharmacyService.calculateDispensingTime(record.startTime, new Date().toISOString());
        const isOverdue = pharmacyService.isOverdue(record.startTime);
        return (
          <Text type={isOverdue ? 'danger' : 'default'}>
            {duration}分钟
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
        const config = pharmacyService.formatDispenseStatus(status);
        return <Badge status={config.badge as any} text={config.text} />;
      }
    },
    {
      title: '操作',
      key: 'actions',
      width: 150,
      render: (_, record) => (
        <Space size=\"small\">
          <Button
            type=\"primary\"
            size=\"small\"
            icon={<CheckCircleOutlined />}
            onClick={() => handleCompleteDispensing(record)}
          >
            完成
          </Button>
          <Button
            type=\"link\"
            size=\"small\"
            danger
            icon={<CloseOutlined />}
            onClick={() => handleReturnPrescription(record)}
          >
            退回
          </Button>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <ExperimentOutlined style={{ marginRight: 8 }} />
        处方调剂
      </Title>

      {/* 统计卡片 */}
      {stats && (
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={4}>
            <Card>
              <Statistic title=\"待调剂\" value={stats.pendingPrescriptions} />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title=\"调剂中\" 
                value={stats.inProgressPrescriptions}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title=\"今日完成\" 
                value={stats.todayDispensed}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title=\"本周完成\" 
                value={stats.weekDispensed}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title=\"本月完成\" 
                value={stats.monthDispensed}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title=\"平均耗时\" 
                value={stats.averageDispensingTime} 
                suffix=\"分钟\"
                valueStyle={{ color: '#faad14' }}
              />
            </Card>
          </Col>
        </Row>
      )}

      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        {/* 待调剂处方 */}
        <TabPane 
          tab={
            <Badge count={pendingPrescriptions.length} offset={[10, 0]}>
              <span>待调剂处方</span>
            </Badge>
          } 
          key=\"pending\"
        >
          <Card title=\"待调剂处方列表\">
            <Table
              columns={pendingColumns}
              dataSource={pendingPrescriptions}
              rowKey=\"id\"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: false,
                showTotal: (total) => `共 ${total} 条记录`
              }}
            />
          </Card>
        </TabPane>

        {/* 调剂中 */}
        <TabPane 
          tab={
            <Badge count={inProgressDispenses.length} offset={[10, 0]}>
              <span>调剂中</span>
            </Badge>
          } 
          key=\"in-progress\"
        >
          <Card title=\"调剂中记录\">
            <Table
              columns={inProgressColumns}
              dataSource={inProgressDispenses}
              rowKey=\"id\"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: false,
                showTotal: (total) => `共 ${total} 条记录`
              }}
            />
          </Card>
        </TabPane>

        {/* 待发药 */}
        <TabPane 
          tab={
            <Badge count={readyForDelivery.length} offset={[10, 0]}>
              <span>待发药</span>
            </Badge>
          } 
          key=\"ready\"
        >
          <Card title=\"待发药列表\">
            <Table
              columns={[
                ...inProgressColumns.slice(0, -1),
                {
                  title: '操作',
                  key: 'actions',
                  width: 120,
                  render: (_, record) => (
                    <Space size=\"small\">
                      <Button
                        type=\"primary\"
                        size=\"small\"
                        icon={<MedicineBoxOutlined />}
                        onClick={() => handleDeliverMedicine(record)}
                      >
                        发药
                      </Button>
                    </Space>
                  )
                }
              ]}
              dataSource={readyForDelivery}
              rowKey=\"id\"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: false,
                showTotal: (total) => `共 ${total} 条记录`
              }}
            />
          </Card>
        </TabPane>

        {/* 待复核 */}
        <TabPane 
          tab={
            <Badge count={needingReview.length} offset={[10, 0]}>
              <span>待复核</span>
            </Badge>
          } 
          key=\"review\"
        >
          <Card title=\"待复核列表\">
            <Table
              columns={[
                ...inProgressColumns.slice(0, -1),
                {
                  title: '操作',
                  key: 'actions',
                  width: 120,
                  render: (_, record) => (
                    <Space size=\"small\">
                      <Button
                        type=\"primary\"
                        size=\"small\"
                        icon={<CheckCircleOutlined />}
                        onClick={async () => {
                          try {
                            await pharmacyService.reviewDispensing(record.id, {
                              reviewPharmacistId: currentPharmacistId,
                              reviewPharmacistName: currentPharmacistName,
                              comments: '复核通过'
                            });
                            message.success('复核完成');
                            loadAllData();
                          } catch (error) {
                            message.error('复核失败');
                          }
                        }}
                      >
                        复核
                      </Button>
                    </Space>
                  )
                }
              ]}
              dataSource={needingReview}
              rowKey=\"id\"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: false,
                showTotal: (total) => `共 ${total} 条记录`
              }}
            />
          </Card>
        </TabPane>
      </Tabs>
    </div>
  );
};

export default PharmacyDispensing;