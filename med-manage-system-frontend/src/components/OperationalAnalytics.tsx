import React, { useState, useEffect } from 'react';
import {
  Card,
  Row,
  Col,
  Statistic,
  Spin,
  message,
  Typography,
  Space,
  Table,
  Progress,
  Tag,
  Select,
  Button,
  Tooltip
} from 'antd';
import {
  TeamOutlined,
  ClockCircleOutlined,
  TrophyOutlined,
  BarChartOutlined,
  LineChartOutlined,
  UserOutlined,
  CalendarOutlined,
  InfoCircleOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import { Column, Line, Gauge } from '@ant-design/plots';
import analyticsService from '../services/analyticsService';
import {
  DateRange,
  DoctorPerformanceReport,
  WaitTimeAnalytics,
  PopularService
} from '../types/analytics';

const { Title, Text } = Typography;
const { Option } = Select;

interface OperationalAnalyticsProps {
  dateRange: DateRange;
}

const OperationalAnalytics: React.FC<OperationalAnalyticsProps> = ({
  dateRange
}) => {
  const [loading, setLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [doctorPerformance, setDoctorPerformance] = useState<DoctorPerformanceReport[]>([]);
  const [waitTimeAnalytics, setWaitTimeAnalytics] = useState<WaitTimeAnalytics | null>(null);
  const [departmentStats, setDepartmentStats] = useState<PopularService[]>([]);
  const [selectedDepartment, setSelectedDepartment] = useState<string>('all');

  // 加载运营分析数据
  const loadOperationalAnalytics = async (showRefreshing = false) => {
    if (showRefreshing) {
      setRefreshing(true);
    } else {
      setLoading(true);
    }
    
    try {
      const [doctorReports, waitTime, deptStats] = await Promise.all([
        analyticsService.getAllDoctorsPerformanceReport(dateRange.startDate, dateRange.endDate),
        analyticsService.getWaitTimeAnalytics(dateRange.startDate, dateRange.endDate),
        analyticsService.getDepartmentVisitStatistics(dateRange.startDate, dateRange.endDate)
      ]);

      setDoctorPerformance(doctorReports);
      setWaitTimeAnalytics(waitTime);
      setDepartmentStats(deptStats);
    } catch (error) {
      console.error('加载运营分析数据失败:', error);
      message.error('加载数据失败，请稍后重试');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const handleRefresh = () => {
    loadOperationalAnalytics(true);
  };

  useEffect(() => {
    loadOperationalAnalytics();
  }, [dateRange]);

  // 医生绩效表格列配置
  const doctorColumns = [
    {
      title: '医生姓名',
      dataIndex: 'doctorName',
      key: 'doctorName',
      render: (name: string, record: DoctorPerformanceReport) => (
        <div>
          <div style={{ fontWeight: 'bold' }}>{name}</div>
          <div style={{ fontSize: '12px', color: '#666' }}>{record.department}</div>
        </div>
      )
    },
    {
      title: '接诊患者',
      dataIndex: 'totalPatients',
      key: 'totalPatients',
      render: (count: number) => `${count}人`,
      sorter: (a: DoctorPerformanceReport, b: DoctorPerformanceReport) => a.totalPatients - b.totalPatients
    },
    {
      title: '完成诊疗',
      dataIndex: 'completedConsultations',
      key: 'completedConsultations',
      render: (count: number, record: DoctorPerformanceReport) => (
        <div>
          <div>{count}次</div>
          <Progress 
            percent={(count / record.totalPatients) * 100} 
            size="small" 
            showInfo={false}
          />
        </div>
      ),
      sorter: (a: DoctorPerformanceReport, b: DoctorPerformanceReport) => a.completedConsultations - b.completedConsultations
    },
    {
      title: '平均诊疗时间',
      dataIndex: 'averageConsultationTime',
      key: 'averageConsultationTime',
      render: (time: number) => `${time.toFixed(1)}分钟`,
      sorter: (a: DoctorPerformanceReport, b: DoctorPerformanceReport) => a.averageConsultationTime - b.averageConsultationTime
    },
    {
      title: '患者满意度',
      dataIndex: 'patientSatisfactionScore',
      key: 'patientSatisfactionScore',
      render: (score: number) => (
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <span style={{ marginRight: '8px' }}>{score.toFixed(1)}</span>
          <Progress 
            percent={(score / 5) * 100} 
            size="small" 
            showInfo={false}
            strokeColor={score >= 4.5 ? '#52c41a' : score >= 4.0 ? '#faad14' : '#ff4d4f'}
          />
        </div>
      ),
      sorter: (a: DoctorPerformanceReport, b: DoctorPerformanceReport) => a.patientSatisfactionScore - b.patientSatisfactionScore
    },
    {
      title: '开具处方',
      dataIndex: 'prescriptionsIssued',
      key: 'prescriptionsIssued',
      render: (count: number) => `${count}张`,
      sorter: (a: DoctorPerformanceReport, b: DoctorPerformanceReport) => a.prescriptionsIssued - b.prescriptionsIssued
    },
    {
      title: '创收金额',
      dataIndex: 'revenueGenerated',
      key: 'revenueGenerated',
      render: (revenue: number) => `¥${revenue.toLocaleString()}`,
      sorter: (a: DoctorPerformanceReport, b: DoctorPerformanceReport) => a.revenueGenerated - b.revenueGenerated
    }
  ];

  // 科室效率表格列配置
  const departmentColumns = [
    {
      title: '排名',
      dataIndex: 'rank',
      key: 'rank',
      width: 60,
      render: (_: any, __: any, index: number) => (
        <Tag color={index < 3 ? 'gold' : 'default'}>{index + 1}</Tag>
      )
    },
    {
      title: '科室名称',
      dataIndex: 'serviceName',
      key: 'serviceName'
    },
    {
      title: '就诊量',
      dataIndex: 'visitCount',
      key: 'visitCount',
      render: (count: number) => `${count}人次`
    },
    {
      title: '占比',
      dataIndex: 'percentage',
      key: 'percentage',
      render: (percentage: number) => (
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <Progress
            percent={percentage}
            size="small"
            style={{ width: '60px', marginRight: '8px' }}
          />
          <span>{percentage.toFixed(1)}%</span>
        </div>
      )
    },
    {
      title: '效率评级',
      key: 'efficiency',
      render: (record: PopularService) => {
        const efficiency = record.percentage > 20 ? 'A' : record.percentage > 15 ? 'B' : record.percentage > 10 ? 'C' : 'D';
        const color = efficiency === 'A' ? 'green' : efficiency === 'B' ? 'blue' : efficiency === 'C' ? 'orange' : 'red';
        return <Tag color={color}>{efficiency}级</Tag>;
      }
    }
  ];

  // 等待时间趋势图配置
  const waitTimeConfig = {
    data: Object.entries(waitTimeAnalytics?.waitTimeByHour || {}).map(([hour, time]) => ({
      hour,
      waitTime: time
    })),
    xField: 'hour',
    yField: 'waitTime',
    point: { size: 4 },
    smooth: true,
    tooltip: {
      formatter: (datum: any) => ({
        name: '等待时间',
        value: `${datum.waitTime}分钟`
      })
    },
    yAxis: {
      label: {
        formatter: (v: string) => `${v}分钟`
      }
    }
  };

  // 科室就诊量图配置
  const departmentVisitConfig = {
    data: departmentStats.map(dept => ({
      department: dept.serviceName,
      visits: dept.visitCount
    })),
    xField: 'department',
    yField: 'visits',
    label: {
      position: 'middle' as const,
      style: {
        fill: '#FFFFFF',
        opacity: 0.6,
      },
    },
    tooltip: {
      formatter: (datum: any) => ({
        name: '就诊量',
        value: `${datum.visits}人次`
      })
    }
  };

  // 等待时间仪表盘配置
  const waitTimeGaugeConfig = {
    percent: Math.min((waitTimeAnalytics?.averageWaitTime || 0) / 60, 1),
    range: {
      color: waitTimeAnalytics && waitTimeAnalytics.averageWaitTime > 30 ? '#ff4d4f' : '#52c41a'
    },
    indicator: {
      pointer: {
        style: {
          stroke: '#D0D0D0',
        },
      },
      pin: {
        style: {
          stroke: '#D0D0D0',
        },
      },
    },
    statistic: {
      content: {
        style: {
          fontSize: '36px',
          lineHeight: '36px',
        },
        formatter: () => `${(waitTimeAnalytics?.averageWaitTime || 0).toFixed(1)}分钟`,
      },
    },
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
        <p style={{ marginTop: '16px' }}>正在加载运营分析数据...</p>
      </div>
    );
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <Title level={3} style={{ margin: 0 }}>
          <TeamOutlined style={{ marginRight: '8px' }} />
          运营数据分析
        </Title>
        <Button 
          icon={<ReloadOutlined />} 
          onClick={handleRefresh}
          loading={refreshing}
        >
          刷新数据
        </Button>
      </div>

      {/* 运营关键指标 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="医生总数"
              value={doctorPerformance.length}
              prefix={<TeamOutlined />}
              suffix="人"
              valueStyle={{ color: '#1890ff' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              在线医生: {doctorPerformance.length}人
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="平均等待时间"
              value={waitTimeAnalytics?.averageWaitTime || 0}
              precision={1}
              prefix={<ClockCircleOutlined />}
              suffix="分钟"
              valueStyle={{ 
                color: waitTimeAnalytics && waitTimeAnalytics.averageWaitTime > 30 ? '#ff4d4f' : '#52c41a' 
              }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              中位数: {waitTimeAnalytics?.medianWaitTime || 0}分钟
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="最高满意度"
              value={Math.max(...doctorPerformance.map(d => d.patientSatisfactionScore), 0)}
              precision={1}
              prefix={<TrophyOutlined />}
              suffix="分"
              valueStyle={{ color: '#52c41a' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              平均: {(doctorPerformance.reduce((sum, d) => sum + d.patientSatisfactionScore, 0) / Math.max(doctorPerformance.length, 1)).toFixed(1)}分
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="总创收金额"
              value={doctorPerformance.reduce((sum, d) => sum + d.revenueGenerated, 0)}
              prefix={<UserOutlined />}
              suffix="元"
              valueStyle={{ color: '#fa8c16' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              人均: ¥{(doctorPerformance.reduce((sum, d) => sum + d.revenueGenerated, 0) / Math.max(doctorPerformance.length, 1)).toLocaleString()}
            </div>
          </Card>
        </Col>
      </Row>

      {/* 运营分析图表 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} lg={8}>
          <Card
            title={
              <Space>
                <ClockCircleOutlined />
                等待时间监控
                <Tooltip title="显示平均等待时间，绿色表示良好，红色表示需要改善">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            style={{ height: '300px' }}
          >
            <Gauge {...waitTimeGaugeConfig} />
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card
            title={
              <Space>
                <LineChartOutlined />
                时段等待时间
              </Space>
            }
            style={{ height: '300px' }}
          >
            <Line {...waitTimeConfig} />
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card
            title={
              <Space>
                <BarChartOutlined />
                科室就诊量
              </Space>
            }
            style={{ height: '300px' }}
          >
            <Column {...departmentVisitConfig} />
          </Card>
        </Col>
      </Row>

      {/* 详细数据表格 */}
      <Row gutter={[16, 16]}>
        <Col xs={24}>
          <Card
            title={
              <Space>
                <TeamOutlined />
                医生绩效统计
                <Tooltip title="显示各医生的详细绩效数据，包括接诊量、满意度、创收等">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
            extra={
              <Select
                value={selectedDepartment}
                onChange={setSelectedDepartment}
                style={{ width: 120 }}
              >
                <Option value="all">全部科室</Option>
                <Option value="内科">内科</Option>
                <Option value="外科">外科</Option>
                <Option value="儿科">儿科</Option>
                <Option value="妇科">妇科</Option>
              </Select>
            }
          >
            <Table
              dataSource={doctorPerformance.filter(doctor => 
                selectedDepartment === 'all' || doctor.department === selectedDepartment
              )}
              columns={doctorColumns}
              pagination={{ pageSize: 10 }}
              size="small"
              rowKey="doctorId"
              scroll={{ x: 800 }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
        <Col xs={24}>
          <Card
            title={
              <Space>
                <BarChartOutlined />
                科室效率分析
                <Tooltip title="按就诊量统计各科室的运营效率">
                  <InfoCircleOutlined style={{ color: '#999' }} />
                </Tooltip>
              </Space>
            }
          >
            <Table
              dataSource={departmentStats}
              columns={departmentColumns}
              pagination={false}
              size="small"
              rowKey="serviceCode"
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default OperationalAnalytics;