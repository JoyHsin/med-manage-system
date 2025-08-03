import React, { useState, useEffect } from 'react';
import {
  Card,
  Button,
  Space,
  Typography,
  Badge,
  Tag,
  Modal,
  message,
  Row,
  Col,
  Divider,
  Alert,
  Tooltip
} from 'antd';
import {
  SoundOutlined,
  CheckOutlined,
  CloseOutlined,
  RedoOutlined,
  UserOutlined,
  PhoneOutlined,
  ClockCircleOutlined,
  ExclamationCircleOutlined,
  FireOutlined
} from '@ant-design/icons';
import dayjs from 'dayjs';
import { PatientQueue, QUEUE_STATUS_CONFIG, PRIORITY_CONFIG } from '../types/triage';
import { triageService } from '../services/triageService';

const { Title, Text } = Typography;
const { confirm } = Modal;

interface TriageCallPanelProps {
  nextPatient: PatientQueue | null;
  onRefresh: () => void;
}

const TriageCallPanel: React.FC<TriageCallPanelProps> = ({ nextPatient, onRefresh }) => {
  const [loading, setLoading] = useState(false);
  const [currentTime, setCurrentTime] = useState(dayjs());

  // 更新当前时间
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(dayjs());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  // 叫号操作
  const handleCallPatient = async (patient: PatientQueue) => {
    try {
      setLoading(true);
      await triageService.callPatient({
        patientQueueId: patient.id,
        calledBy: 1 // TODO: 从用户上下文获取当前用户ID
      });
      message.success(`已叫号：${patient.patientName}`);
      onRefresh();
    } catch (error) {
      console.error('叫号失败:', error);
      message.error('叫号失败');
    } finally {
      setLoading(false);
    }
  };

  // 确认到达
  const handleConfirmArrival = async (patient: PatientQueue) => {
    try {
      setLoading(true);
      await triageService.confirmPatientArrival({
        patientQueueId: patient.id,
        calledBy: 1 // TODO: 从用户上下文获取当前用户ID
      });
      message.success(`${patient.patientName} 已确认到达`);
      onRefresh();
    } catch (error) {
      console.error('确认到达失败:', error);
      message.error('确认到达失败');
    } finally {
      setLoading(false);
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
          setLoading(true);
          await triageService.markPatientAbsent({
            patientQueueId: patient.id,
            calledBy: 1 // TODO: 从用户上下文获取当前用户ID
          });
          message.success(`${patient.patientName} 已标记为未到`);
          onRefresh();
        } catch (error) {
          console.error('标记未到失败:', error);
          message.error('标记未到失败');
        } finally {
          setLoading(false);
        }
      }
    });
  };

  // 重新叫号
  const handleRecallPatient = async (patient: PatientQueue) => {
    try {
      setLoading(true);
      await triageService.recallPatient({
        patientQueueId: patient.id,
        calledBy: 1 // TODO: 从用户上下文获取当前用户ID
      });
      message.success(`已重新叫号：${patient.patientName}`);
      onRefresh();
    } catch (error) {
      console.error('重新叫号失败:', error);
      message.error('重新叫号失败');
    } finally {
      setLoading(false);
    }
  };

  // 完成就诊
  const handleCompletePatient = async (patient: PatientQueue) => {
    confirm({
      title: '确认完成就诊',
      content: `确定 ${patient.patientName} 已完成就诊吗？`,
      onOk: async () => {
        try {
          setLoading(true);
          await triageService.completePatient(patient.id);
          message.success(`${patient.patientName} 已完成就诊`);
          onRefresh();
        } catch (error) {
          console.error('完成就诊失败:', error);
          message.error('完成就诊失败');
        } finally {
          setLoading(false);
        }
      }
    });
  };

  // 渲染患者信息卡片
  const renderPatientCard = (patient: PatientQueue) => {
    const statusConfig = QUEUE_STATUS_CONFIG[patient.status];
    const priorityConfig = PRIORITY_CONFIG[patient.priority as keyof typeof PRIORITY_CONFIG];

    return (
      <Card
        style={{
          marginBottom: 16,
          border: patient.priority === 1 ? '2px solid #ff4d4f' : '1px solid #d9d9d9',
          backgroundColor: patient.priority === 1 ? '#fff2f0' : '#fff'
        }}
      >
        <Row gutter={16}>
          <Col span={12}>
            <div style={{ textAlign: 'center' }}>
              <Badge
                count={patient.priority === 1 ? <FireOutlined style={{ color: '#ff4d4f' }} /> : 0}
              >
                <div
                  style={{
                    fontSize: '48px',
                    fontWeight: 'bold',
                    color: patient.priority <= 2 ? '#ff4d4f' : '#1890ff',
                    lineHeight: 1
                  }}
                >
                  {patient.queueNumber}
                </div>
              </Badge>
              <div style={{ marginTop: 8 }}>
                <Tag color={priorityConfig.color}>{priorityConfig.text}</Tag>
                <Badge status={statusConfig.badge as any} text={statusConfig.text} />
              </div>
            </div>
          </Col>
          <Col span={12}>
            <div>
              <Title level={4} style={{ margin: 0, marginBottom: 8 }}>
                <UserOutlined style={{ marginRight: 8 }} />
                {patient.patientName}
              </Title>
              <div style={{ marginBottom: 4 }}>
                <PhoneOutlined style={{ marginRight: 8, color: '#666' }} />
                <Text type="secondary">{patient.patientPhone}</Text>
              </div>
              <div style={{ marginBottom: 4 }}>
                <Text type="secondary">挂号号: {patient.registrationNumber}</Text>
              </div>
              <div style={{ marginBottom: 4 }}>
                <Text type="secondary">科室: {patient.department}</Text>
              </div>
              {patient.callCount > 0 && (
                <div>
                  <Text type="secondary">叫号次数: {patient.callCount}</Text>
                </div>
              )}
            </div>
          </Col>
        </Row>

        {/* 时间信息 */}
        {(patient.calledAt || patient.arrivedAt) && (
          <>
            <Divider />
            <Row gutter={16}>
              {patient.calledAt && (
                <Col span={12}>
                  <div style={{ textAlign: 'center' }}>
                    <ClockCircleOutlined style={{ color: '#1890ff', marginRight: 4 }} />
                    <Text type="secondary">叫号时间</Text>
                    <div style={{ fontWeight: 'bold' }}>
                      {dayjs(patient.calledAt).format('HH:mm:ss')}
                    </div>
                  </div>
                </Col>
              )}
              {patient.arrivedAt && (
                <Col span={12}>
                  <div style={{ textAlign: 'center' }}>
                    <CheckOutlined style={{ color: '#52c41a', marginRight: 4 }} />
                    <Text type="secondary">到达时间</Text>
                    <div style={{ fontWeight: 'bold' }}>
                      {dayjs(patient.arrivedAt).format('HH:mm:ss')}
                    </div>
                  </div>
                </Col>
              )}
            </Row>
          </>
        )}

        {/* 操作按钮 */}
        <Divider />
        <div style={{ textAlign: 'center' }}>
          {patient.status === 'WAITING' && (
            <Button
              type="primary"
              size="large"
              icon={<SoundOutlined />}
              onClick={() => handleCallPatient(patient)}
              loading={loading}
              style={{ minWidth: 120 }}
            >
              叫号
            </Button>
          )}

          {patient.status === 'CALLED' && (
            <Space size="middle">
              <Button
                type="primary"
                size="large"
                icon={<CheckOutlined />}
                onClick={() => handleConfirmArrival(patient)}
                loading={loading}
              >
                确认到达
              </Button>
              <Button
                danger
                size="large"
                icon={<CloseOutlined />}
                onClick={() => handleMarkAbsent(patient)}
                loading={loading}
              >
                标记未到
              </Button>
            </Space>
          )}

          {patient.status === 'ABSENT' && (
            <Button
              type="default"
              size="large"
              icon={<RedoOutlined />}
              onClick={() => handleRecallPatient(patient)}
              loading={loading}
            >
              重新叫号
            </Button>
          )}

          {patient.status === 'ARRIVED' && (
            <Button
              type="primary"
              size="large"
              onClick={() => handleCompletePatient(patient)}
              loading={loading}
            >
              完成就诊
            </Button>
          )}
        </div>
      </Card>
    );
  };

  return (
    <div>
      {/* 当前时间显示 */}
      <Card style={{ marginBottom: 16, textAlign: 'center' }}>
        <Title level={3} style={{ margin: 0, color: '#1890ff' }}>
          <ClockCircleOutlined style={{ marginRight: 8 }} />
          {currentTime.format('YYYY-MM-DD HH:mm:ss')}
        </Title>
      </Card>

      {/* 下一个患者信息 */}
      {nextPatient ? (
        <>
          <Alert
            message="下一个待叫号患者"
            type="info"
            showIcon
            style={{ marginBottom: 16 }}
          />
          {renderPatientCard(nextPatient)}
        </>
      ) : (
        <Card style={{ textAlign: 'center', padding: '40px 20px' }}>
          <div style={{ color: '#999', fontSize: '16px' }}>
            <SoundOutlined style={{ fontSize: '48px', marginBottom: 16, display: 'block' }} />
            暂无待叫号患者
          </div>
        </Card>
      )}

      {/* 快捷操作提示 */}
      <Card title="操作提示" size="small" style={{ marginTop: 16 }}>
        <div style={{ fontSize: '12px', color: '#666' }}>
          <div>• 点击"叫号"按钮呼叫患者</div>
          <div>• 患者到达后点击"确认到达"</div>
          <div>• 如患者未响应可标记"未到"或"重新叫号"</div>
          <div>• 就诊完成后点击"完成就诊"</div>
          <div>• 紧急患者会有特殊标识和颜色提示</div>
        </div>
      </Card>
    </div>
  );
};

export default TriageCallPanel;