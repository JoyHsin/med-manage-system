import React, { useState, useEffect } from 'react';
import {
  Card,
  Row,
  Col,
  Statistic,
  Progress,
  Typography,
  Badge,
  Tag,
  List,
  Alert,
  Space,
  Divider
} from 'antd';
import {
  ClockCircleOutlined,
  UserOutlined,
  FireOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  SoundOutlined
} from '@ant-design/icons';
import dayjs from 'dayjs';
import { PatientQueue, QUEUE_STATUS_CONFIG, PRIORITY_CONFIG } from '../types/triage';

const { Title, Text } = Typography;

interface QueueMonitorBoardProps {
  patientQueue: PatientQueue[];
  nextPatient: PatientQueue | null;
}

interface QueueStats {
  totalWaiting: number;
  totalCalled: number;
  totalArrived: number;
  totalAbsent: number;
  totalCompleted: number;
  urgentCount: number;
  averageWaitTime: number;
  longestWaitTime: number;
  completionRate: number;
}

const QueueMonitorBoard: React.FC<QueueMonitorBoardProps> = ({ patientQueue, nextPatient }) => {
  const [currentTime, setCurrentTime] = useState(dayjs());

  // 更新当前时间
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(dayjs());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  // 计算统计数据
  const stats: QueueStats = React.useMemo(() => {
    const now = dayjs();
    let totalWaitTime = 0;
    let waitingCount = 0;
    let longestWait = 0;
    
    const result = {
      totalWaiting: 0,
      totalCalled: 0,
      totalArrived: 0,
      totalAbsent: 0,
      totalCompleted: 0,
      urgentCount: 0,
      averageWaitTime: 0,
      longestWaitTime: 0,
      completionRate: 0
    };

    patientQueue.forEach(patient => {
      // 统计各状态数量
      switch (patient.status) {
        case 'WAITING':
          result.totalWaiting++;
          break;
        case 'CALLED':
          result.totalCalled++;
          break;
        case 'ARRIVED':
          result.totalArrived++;
          break;
        case 'ABSENT':
          result.totalAbsent++;
          break;
        case 'COMPLETED':
          result.totalCompleted++;
          break;
      }

      // 统计紧急患者
      if (patient.priority === 1) {
        result.urgentCount++;
      }

      // 计算等待时间（对于未完成的患者）
      if (patient.status !== 'COMPLETED') {
        const startTime = patient.calledAt ? dayjs(patient.calledAt) : dayjs(patient.createdAt);
        const waitTime = now.diff(startTime, 'minute');
        totalWaitTime += waitTime;
        waitingCount++;
        longestWait = Math.max(longestWait, waitTime);
      }
    });

    // 计算平均等待时间
    result.averageWaitTime = waitingCount > 0 ? Math.round(totalWaitTime / waitingCount) : 0;
    result.longestWaitTime = longestWait;

    // 计算完成率
    const totalPatients = patientQueue.length;
    result.completionRate = totalPatients > 0 ? Math.round((result.totalCompleted / totalPatients) * 100) : 0;

    return result;
  }, [patientQueue]);

  // 获取等待时间过长的患者
  const longWaitPatients = React.useMemo(() => {
    const now = dayjs();
    return patientQueue
      .filter(patient => patient.status !== 'COMPLETED')
      .map(patient => {
        const startTime = patient.calledAt ? dayjs(patient.calledAt) : dayjs(patient.createdAt);
        const waitTime = now.diff(startTime, 'minute');
        return { ...patient, waitTime };
      })
      .filter(patient => patient.waitTime > 30) // 等待超过30分钟
      .sort((a, b) => b.waitTime - a.waitTime);
  }, [patientQueue]);

  // 获取紧急患者列表
  const urgentPatients = React.useMemo(() => {
    return patientQueue
      .filter(patient => patient.priority === 1 && patient.status !== 'COMPLETED')
      .sort((a, b) => a.queueNumber - b.queueNumber);
  }, [patientQueue]);

  return (
    <div style={{ padding: '16px', backgroundColor: '#f0f2f5', minHeight: '100vh' }}>
      {/* 标题和时间 */}
      <Row justify="space-between" align="middle" style={{ marginBottom: 24 }}>
        <Col>
          <Title level={2} style={{ margin: 0, color: '#1890ff' }}>
            队列监控大屏
          </Title>
        </Col>
        <Col>
          <Title level={3} style={{ margin: 0, color: '#666' }}>
            <ClockCircleOutlined style={{ marginRight: 8 }} />
            {currentTime.format('YYYY-MM-DD HH:mm:ss')}
          </Title>
        </Col>
      </Row>

      {/* 核心统计数据 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={4}>
          <Card>
            <Statistic
              title="等待中"
              value={stats.totalWaiting}
              valueStyle={{ color: '#1890ff', fontSize: '36px' }}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="已叫号"
              value={stats.totalCalled}
              valueStyle={{ color: '#52c41a', fontSize: '36px' }}
              prefix={<SoundOutlined />}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="已到达"
              value={stats.totalArrived}
              valueStyle={{ color: '#13c2c2', fontSize: '36px' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="紧急患者"
              value={stats.urgentCount}
              valueStyle={{ color: '#ff4d4f', fontSize: '36px' }}
              prefix={<FireOutlined />}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="平均等待"
              value={stats.averageWaitTime}
              suffix="分钟"
              valueStyle={{ color: '#722ed1', fontSize: '36px' }}
              prefix={<ClockCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card>
            <Statistic
              title="完成率"
              value={stats.completionRate}
              suffix="%"
              valueStyle={{ color: '#fa8c16', fontSize: '36px' }}
            />
            <Progress
              percent={stats.completionRate}
              showInfo={false}
              strokeColor="#fa8c16"
              style={{ marginTop: 8 }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16}>
        {/* 下一个患者 */}
        <Col span={8}>
          <Card title="下一个待叫号患者" style={{ height: '400px' }}>
            {nextPatient ? (
              <div style={{ textAlign: 'center' }}>
                <div
                  style={{
                    fontSize: '72px',
                    fontWeight: 'bold',
                    color: nextPatient.priority === 1 ? '#ff4d4f' : '#1890ff',
                    lineHeight: 1,
                    marginBottom: 16
                  }}
                >
                  {nextPatient.queueNumber}
                </div>
                <Title level={3} style={{ marginBottom: 8 }}>
                  {nextPatient.patientName}
                </Title>
                <Space direction="vertical" size="small">
                  <Tag color={PRIORITY_CONFIG[nextPatient.priority as keyof typeof PRIORITY_CONFIG].color}>
                    {PRIORITY_CONFIG[nextPatient.priority as keyof typeof PRIORITY_CONFIG].text}
                  </Tag>
                  <Text type="secondary">{nextPatient.department}</Text>
                  <Text type="secondary">挂号号: {nextPatient.registrationNumber}</Text>
                </Space>
                {nextPatient.priority === 1 && (
                  <Alert
                    message="紧急患者"
                    type="error"
                    showIcon
                    icon={<FireOutlined />}
                    style={{ marginTop: 16 }}
                  />
                )}
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '60px 20px', color: '#999' }}>
                <SoundOutlined style={{ fontSize: '48px', marginBottom: 16, display: 'block' }} />
                <Text type="secondary" style={{ fontSize: '16px' }}>
                  暂无待叫号患者
                </Text>
              </div>
            )}
          </Card>
        </Col>

        {/* 紧急患者列表 */}
        <Col span={8}>
          <Card title="紧急患者" style={{ height: '400px' }}>
            {urgentPatients.length > 0 ? (
              <List
                dataSource={urgentPatients}
                renderItem={(patient) => (
                  <List.Item>
                    <List.Item.Meta
                      avatar={
                        <Badge count={<FireOutlined style={{ color: '#ff4d4f' }} />}>
                          <div
                            style={{
                              width: 40,
                              height: 40,
                              borderRadius: '50%',
                              backgroundColor: '#ff4d4f',
                              color: '#fff',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              fontWeight: 'bold',
                              fontSize: '16px'
                            }}
                          >
                            {patient.queueNumber}
                          </div>
                        </Badge>
                      }
                      title={patient.patientName}
                      description={
                        <div>
                          <div>{patient.department}</div>
                          <Badge
                            status={QUEUE_STATUS_CONFIG[patient.status].badge as any}
                            text={QUEUE_STATUS_CONFIG[patient.status].text}
                          />
                        </div>
                      }
                    />
                  </List.Item>
                )}
              />
            ) : (
              <div style={{ textAlign: 'center', padding: '60px 20px', color: '#999' }}>
                <CheckCircleOutlined style={{ fontSize: '48px', marginBottom: 16, display: 'block' }} />
                <Text type="secondary">暂无紧急患者</Text>
              </div>
            )}
          </Card>
        </Col>

        {/* 等待时间过长的患者 */}
        <Col span={8}>
          <Card title="等待时间过长" style={{ height: '400px' }}>
            {longWaitPatients.length > 0 ? (
              <List
                dataSource={longWaitPatients}
                renderItem={(patient) => (
                  <List.Item>
                    <List.Item.Meta
                      avatar={
                        <div
                          style={{
                            width: 40,
                            height: 40,
                            borderRadius: '50%',
                            backgroundColor: '#faad14',
                            color: '#fff',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontWeight: 'bold',
                            fontSize: '16px'
                          }}
                        >
                          {patient.queueNumber}
                        </div>
                      }
                      title={patient.patientName}
                      description={
                        <div>
                          <div>{patient.department}</div>
                          <Text type="warning">
                            <ExclamationCircleOutlined style={{ marginRight: 4 }} />
                            等待 {patient.waitTime} 分钟
                          </Text>
                        </div>
                      }
                    />
                  </List.Item>
                )}
              />
            ) : (
              <div style={{ textAlign: 'center', padding: '60px 20px', color: '#999' }}>
                <CheckCircleOutlined style={{ fontSize: '48px', marginBottom: 16, display: 'block' }} />
                <Text type="secondary">等待时间正常</Text>
              </div>
            )}
          </Card>
        </Col>
      </Row>

      {/* 底部状态栏 */}
      <Row style={{ marginTop: 24 }}>
        <Col span={24}>
          <Card>
            <Row gutter={32} align="middle">
              <Col>
                <Statistic
                  title="总患者数"
                  value={patientQueue.length}
                  valueStyle={{ fontSize: '24px' }}
                />
              </Col>
              <Col>
                <Statistic
                  title="已完成"
                  value={stats.totalCompleted}
                  valueStyle={{ fontSize: '24px', color: '#52c41a' }}
                />
              </Col>
              <Col>
                <Statistic
                  title="未到患者"
                  value={stats.totalAbsent}
                  valueStyle={{ fontSize: '24px', color: '#ff4d4f' }}
                />
              </Col>
              <Col>
                <Statistic
                  title="最长等待"
                  value={stats.longestWaitTime}
                  suffix="分钟"
                  valueStyle={{ fontSize: '24px', color: '#722ed1' }}
                />
              </Col>
              <Col flex="auto">
                <div style={{ textAlign: 'right' }}>
                  <Text type="secondary">
                    数据更新时间: {currentTime.format('HH:mm:ss')}
                  </Text>
                </div>
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default QueueMonitorBoard;