import React, { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Tag,
  Badge,
  message,
  Modal,
  DatePicker,
  Select,
  Statistic,
  Row,
  Col,
  Typography,
  Tooltip,
  Input,
  Divider,
  Drawer
} from 'antd';
import {
  SoundOutlined,
  CheckOutlined,
  CloseOutlined,
  RedoOutlined,
  UserOutlined,
  PhoneOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  ExclamationCircleOutlined,
  ControlOutlined,
  DesktopOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { triageService } from '../services/triageService';
import { PatientQueue, QueueStatus, QUEUE_STATUS_CONFIG, PRIORITY_CONFIG } from '../types/triage';
import TriageCallPanel from '../components/TriageCallPanel';
import QueueMonitorBoard from '../components/QueueMonitorBoard';

const { Title, Text } = Typography;
const { Option } = Select;
const { confirm } = Modal;

const TriageManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [patientQueue, setPatientQueue] = useState<PatientQueue[]>([]);
  const [selectedDate, setSelectedDate] = useState<dayjs.Dayjs>(dayjs());
  const [statusFilter, setStatusFilter] = useState<QueueStatus | 'ALL'>('ALL');
  const [searchText, setSearchText] = useState('');
  const [nextPatient, setNextPatient] = useState<PatientQueue | null>(null);
  const [callPanelVisible, setCallPanelVisible] = useState(false);
  const [monitorBoardVisible, setMonitorBoardVisible] = useState(false);

  // 统计数据
  const statistics = React.useMemo(() => {
    const stats = {
      totalWaiting: 0,
      totalCalled: 0,
      totalArrived: 0,
      totalAbsent: 0,
      totalCompleted: 0,
      averageWaitTime: 0
    };

    patientQueue.forEach(patient => {
      switch (patient.status) {
        case 'WAITING':
          stats.totalWaiting++;
          break;
        case 'CALLED':
          stats.totalCalled++;
          break;
        case 'ARRIVED':
          stats.totalArrived++;
          break;
        case 'ABSENT':
          stats.totalAbsent++;
          break;
        case 'COMPLETED':
          stats.totalCompleted++;
          break;
      }
    });

    return stats;
  }, [patientQueue]);

  // 加载患者队列数据
  const loadPatientQueue = useCallback(async () => {
    try {
      setLoading(true);
      const dateStr = selectedDate.format('YYYY-MM-DD');
      
      let data: PatientQueue[];
      if (statusFilter === 'ALL') {
        data = await triageService.getPatientQueueByDate(dateStr);
      } else {
        data = await triageService.getPatientQueueByStatus(statusFilter, dateStr);
      }
      
      setPatientQueue(data);
      
      // 获取下一个待叫号患者
      const next = await triageService.getNextPatient(dateStr);
      setNextPatient(next);
    } catch (error) {
      console.error('加载患者队列失败:', error);
      message.error('加载患者队列失败');
    } finally {
      setLoading(false);
    }
  }, [selectedDate, statusFilter]);

  // 初始加载和定时刷新
  useEffect(() => {
    loadPatientQueue();
    
    // 每30秒自动刷新
    const interval = setInterval(loadPatientQueue, 30000);
    return () => clearInterval(interval);
  }, [loadPatientQueue]);

  // 叫号操作
  const handleCallPatient = async (patient: PatientQueue) => {
    try {
      await triageService.callPatient({
        patientQueueId: patient.id,
        calledBy: 1 // TODO: 从用户上下文获取当前用户ID
      });
      message.success(`已叫号：${patient.patientName}`);
      loadPatientQueue();
    } catch (error) {
      console.error('叫号失败:', error);
      message.error('叫号失败');
    }
  };

  // 确认到达
  const handleConfirmArrival = async (patient: PatientQueue) => {
    try {
      await triageService.confirmPatientArrival({
        patientQueueId: patient.id,
        calledBy: 1 // TODO: 从用户上下文获取当前用户ID
      });
      message.success(`${patient.patientName} 已确认到达`);
      loadPatientQueue();
    } catch (error) {
      console.error('确认到达失败:', error);
      message.error('确认到达失败');
    }
  };

  // 标记未到
  const handleMarkAbsent = async (patient: PatientQueue) => {
    confirm({
      title: '确认标记未到',
      content: `确定要将 ${patient.patientName} 标记为未到吗？`,
      icon: <ExclamationCircleOutlined />,
      onOk: async () => {
        try {
          await triageService.markPatientAbsent({
            patientQueueId: patient.id,
            calledBy: 1 // TODO: 从用户上下文获取当前用户ID
          });
          message.success(`${patient.patientName} 已标记为未到`);
          loadPatientQueue();
        } catch (error) {
          console.error('标记未到失败:', error);
          message.error('标记未到失败');
        }
      }
    });
  };

  // 重新叫号
  const handleRecallPatient = async (patient: PatientQueue) => {
    try {
      await triageService.recallPatient({
        patientQueueId: patient.id,
        calledBy: 1 // TODO: 从用户上下文获取当前用户ID
      });
      message.success(`已重新叫号：${patient.patientName}`);
      loadPatientQueue();
    } catch (error) {
      console.error('重新叫号失败:', error);
      message.error('重新叫号失败');
    }
  };

  // 完成就诊
  const handleCompletePatient = async (patient: PatientQueue) => {
    confirm({
      title: '确认完成就诊',
      content: `确定 ${patient.patientName} 已完成就诊吗？`,
      onOk: async () => {
        try {
          await triageService.completePatient(patient.id);
          message.success(`${patient.patientName} 已完成就诊`);
          loadPatientQueue();
        } catch (error) {
          console.error('完成就诊失败:', error);
          message.error('完成就诊失败');
        }
      }
    });
  };

  // 一键叫号下一个患者
  const handleCallNext = async () => {
    if (!nextPatient) {
      message.info('暂无待叫号患者');
      return;
    }
    await handleCallPatient(nextPatient);
  };

  // 过滤数据
  const filteredData = patientQueue.filter(patient => {
    if (searchText) {
      const searchLower = searchText.toLowerCase();
      return (
        patient.patientName.toLowerCase().includes(searchLower) ||
        patient.patientPhone.includes(searchText) ||
        patient.registrationNumber.toLowerCase().includes(searchLower)
      );
    }
    return true;
  });

  // 表格列定义
  const columns: ColumnsType<PatientQueue> = [
    {
      title: '队列号',
      dataIndex: 'queueNumber',
      key: 'queueNumber',
      width: 80,
      render: (queueNumber: number, record: PatientQueue) => (
        <div style={{ textAlign: 'center' }}>
          <Badge
            count={record.priority === 1 ? '急' : ''}
            color="red"
            size="small"
          >
            <Tag color={record.priority <= 2 ? 'red' : 'blue'} style={{ minWidth: 40, textAlign: 'center' }}>
              {queueNumber}
            </Tag>
          </Badge>
        </div>
      )
    },
    {
      title: '患者信息',
      key: 'patientInfo',
      width: 200,
      render: (_, record: PatientQueue) => (
        <div>
          <div style={{ fontWeight: 'bold', marginBottom: 4 }}>
            <UserOutlined style={{ marginRight: 4 }} />
            {record.patientName}
          </div>
          <div style={{ color: '#666', fontSize: '12px' }}>
            <PhoneOutlined style={{ marginRight: 4 }} />
            {record.patientPhone}
          </div>
          <div style={{ color: '#666', fontSize: '12px' }}>
            挂号号: {record.registrationNumber}
          </div>
        </div>
      )
    },
    {
      title: '科室',
      dataIndex: 'department',
      key: 'department',
      width: 100
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: QueueStatus) => {
        const config = QUEUE_STATUS_CONFIG[status];
        return (
          <Badge status={config.badge as any} text={config.text} />
        );
      }
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      width: 80,
      render: (priority: number) => {
        const config = PRIORITY_CONFIG[priority as keyof typeof PRIORITY_CONFIG];
        return (
          <Tag color={config.color}>{config.text}</Tag>
        );
      }
    },
    {
      title: '叫号次数',
      dataIndex: 'callCount',
      key: 'callCount',
      width: 80,
      align: 'center'
    },
    {
      title: '时间信息',
      key: 'timeInfo',
      width: 150,
      render: (_, record: PatientQueue) => (
        <div style={{ fontSize: '12px' }}>
          {record.calledAt && (
            <div>
              <ClockCircleOutlined style={{ marginRight: 4 }} />
              叫号: {dayjs(record.calledAt).format('HH:mm')}
            </div>
          )}
          {record.arrivedAt && (
            <div>
              <CheckOutlined style={{ marginRight: 4, color: 'green' }} />
              到达: {dayjs(record.arrivedAt).format('HH:mm')}
            </div>
          )}
          {record.completedAt && (
            <div>
              <CheckOutlined style={{ marginRight: 4, color: 'blue' }} />
              完成: {dayjs(record.completedAt).format('HH:mm')}
            </div>
          )}
        </div>
      )
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      render: (_, record: PatientQueue) => {
        const actions = [];

        if (record.status === 'WAITING') {
          actions.push(
            <Button
              key="call"
              type="primary"
              size="small"
              icon={<SoundOutlined />}
              onClick={() => handleCallPatient(record)}
            >
              叫号
            </Button>
          );
        }

        if (record.status === 'CALLED') {
          actions.push(
            <Button
              key="confirm"
              type="primary"
              size="small"
              icon={<CheckOutlined />}
              onClick={() => handleConfirmArrival(record)}
            >
              确认到达
            </Button>
          );
          actions.push(
            <Button
              key="absent"
              danger
              size="small"
              icon={<CloseOutlined />}
              onClick={() => handleMarkAbsent(record)}
            >
              未到
            </Button>
          );
        }

        if (record.status === 'ABSENT') {
          actions.push(
            <Button
              key="recall"
              type="default"
              size="small"
              icon={<RedoOutlined />}
              onClick={() => handleRecallPatient(record)}
            >
              重新叫号
            </Button>
          );
        }

        if (record.status === 'ARRIVED') {
          actions.push(
            <Button
              key="complete"
              type="primary"
              size="small"
              onClick={() => handleCompletePatient(record)}
            >
              完成就诊
            </Button>
          );
        }

        return <Space size="small">{actions}</Space>;
      }
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>分诊叫号管理</Title>

      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={4}>
          <Card>
            <Statistic
              title="等待中"
              value={statistics.totalWaiting}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="已叫号"
              value={statistics.totalCalled}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="已到达"
              value={statistics.totalArrived}
              valueStyle={{ color: '#13c2c2' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="未到"
              value={statistics.totalAbsent}
              valueStyle={{ color: '#ff4d4f' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="已完成"
              value={statistics.totalCompleted}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <div style={{ textAlign: 'center' }}>
              <Button
                type="primary"
                size="large"
                icon={<SoundOutlined />}
                onClick={handleCallNext}
                disabled={!nextPatient}
                style={{ width: '100%', height: '40px', marginBottom: '8px' }}
              >
                {nextPatient ? `叫号 ${nextPatient.queueNumber}` : '无待叫号'}
              </Button>
              <Button
                type="default"
                size="small"
                icon={<ControlOutlined />}
                onClick={() => setCallPanelVisible(true)}
                style={{ width: '100%' }}
              >
                操作面板
              </Button>
              {nextPatient && (
                <Text type="secondary" style={{ fontSize: '12px', display: 'block', marginTop: '4px' }}>
                  {nextPatient.patientName}
                </Text>
              )}
            </div>
          </Card>
        </Col>
      </Row>

      {/* 筛选和搜索 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16} align="middle">
          <Col>
            <Text strong>日期：</Text>
            <DatePicker
              value={selectedDate}
              onChange={(date) => date && setSelectedDate(date)}
              style={{ marginLeft: 8 }}
            />
          </Col>
          <Col>
            <Text strong>状态：</Text>
            <Select
              value={statusFilter}
              onChange={setStatusFilter}
              style={{ width: 120, marginLeft: 8 }}
            >
              <Option value="ALL">全部</Option>
              <Option value="WAITING">等待中</Option>
              <Option value="CALLED">已叫号</Option>
              <Option value="ARRIVED">已到达</Option>
              <Option value="ABSENT">未到</Option>
              <Option value="COMPLETED">已完成</Option>
            </Select>
          </Col>
          <Col>
            <Text strong>搜索：</Text>
            <Input.Search
              placeholder="患者姓名、电话或挂号号"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              style={{ width: 200, marginLeft: 8 }}
              allowClear
            />
          </Col>
          <Col>
            <Space>
              <Button onClick={loadPatientQueue} loading={loading}>
                刷新
              </Button>
              <Button
                type="default"
                icon={<DesktopOutlined />}
                onClick={() => setMonitorBoardVisible(true)}
              >
                监控大屏
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 患者队列表格 */}
      <Card>
        <Table
          columns={columns}
          dataSource={filteredData}
          rowKey="id"
          loading={loading}
          pagination={{
            total: filteredData.length,
            pageSize: 20,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`
          }}
          scroll={{ x: 1200 }}
          rowClassName={(record) => {
            if (record.priority === 1) return 'urgent-row';
            if (record.status === 'CALLED') return 'called-row';
            return '';
          }}
        />
      </Card>

      {/* 叫号操作面板抽屉 */}
      <Drawer
        title="叫号操作面板"
        placement="right"
        width={400}
        onClose={() => setCallPanelVisible(false)}
        open={callPanelVisible}
      >
        <TriageCallPanel
          nextPatient={nextPatient}
          onRefresh={loadPatientQueue}
        />
      </Drawer>

      {/* 监控大屏模态框 */}
      <Modal
        title="队列监控大屏"
        open={monitorBoardVisible}
        onCancel={() => setMonitorBoardVisible(false)}
        width="95vw"
        style={{ top: 20 }}
        footer={null}
        destroyOnClose
      >
        <QueueMonitorBoard
          patientQueue={patientQueue}
          nextPatient={nextPatient}
        />
      </Modal>

      <style jsx>{`
        .urgent-row {
          background-color: #fff2f0 !important;
        }
        .called-row {
          background-color: #f6ffed !important;
        }
      `}</style>
    </div>
  );
};

export default TriageManagement;