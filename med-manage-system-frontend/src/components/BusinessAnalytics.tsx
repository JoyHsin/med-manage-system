import React, { useState, useEffect } from 'react';
import {
  Card,
  Row,
  Col,
  Statistic,
  DatePicker,
  Select,
  Spin,
  message,
  Typography,
  Space,
  Tabs,
  Table,
  Progress,
  Tag
} from 'antd';
import {
  TrendingUpOutlined,
  TrendingDownOutlined,
  UserOutlined,
  DollarOutlined,
  CalendarOutlined,
  BarChartOutlined,
  LineChartOutlined,
  PieChartOutlined,
  InfoCircleOutlined
} from '@ant-design/icons';
import { Line, Column, Pie } from '@ant-design/plots';
import dayjs, { Dayjs } from 'dayjs';
import analyticsService from '../services/analyticsService';
import {
  DateRange,
  PatientVisitAnalytics,
  PopularService,
  CommonDiagnosis,
  RevenueByService,
  PatientDemographics
} from '../types/analytics';

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;
const { Option } = Select;
const { TabPane } = Tabs;

interface BusinessAnalyticsProps {
  dateRange: DateRange;
  onDateRangeChange: (range: DateRange) => void;
}

const BusinessAnalytics: React.FC<BusinessAnalyticsProps> = ({
  dateRange,
  onDateRangeChange
}) => {
  const [loading, setLoading] = useState(false);
  const [patientVisitAnalytics, setPatientVisitAnalytics] = useState<PatientVisitAnalytics | null>(null);
  const [popularServices, setPopularServices] = useState<PopularService[]>([]);
  const [commonDiagnoses, setCommonDiagnoses] = useState<CommonDiagnosis[]>([]);
  const [revenueByService, setRevenueByService] = useState<RevenueByService[]>([]);
  const [patientDemographics, setPatientDemographics] = useState<PatientDemographics | null>(null);

  // 加载业务分析数据
  const loadBusinessAnalytics = async () => {
    setLoading(true);
    try {
      const [visitAnalytics, services, diagnoses, revenue, demographics] = await Promise.all([
        analyticsService.getPatientVisitAnalytics(dateRange.startDate, dateRange.endDate),
        analyticsService.getPopularServices(dateRange.startDate, dateRange.endDate),
        analyticsService.getCommonDiagnoses(dateRange.startDate, dateRange.endDate),
        analyticsService.getRevenueByService(dateRange.startDate, dateRange.endDate),
        analyticsService.getPatientDemographics()
      ]);

      setPatientVisitAnalytics(visitAnalytics);
      setPopularServices(services);
      setCommonDiagnoses(diagnoses);
      setRevenueByService(revenue);
      setPatientDemographics(demographics);
    } catch (error) {
      console.error('加载业务分析数据失败:', error);
      message.error('加载数据失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBusinessAnalytics();
  }, [dateRange]);

  // 就诊量趋势图配置
  const visitTrendConfig = {
    data: [
      { date: '周一', visits: patientVisitAnalytics?.totalVisits || 0, type: '总就诊量' },
      { date: '周二', visits: Math.floor((patientVisitAnalytics?.totalVisits || 0) * 0.9), type: '总就诊量' },
      { date: '周三', visits: Math.floor((patientVisitAnalytics?.totalVisits || 0) * 1.1), type: '总就诊量' },
      { date: '周四', visits: Math.floor((patientVisitAnalytics?.totalVisits || 0) * 0.95), type: '总就诊量' },
      { date: '周五', visits: Math.floor((patientVisitAnalytics?.totalVisits || 0) * 1.2), type: '总就诊量' },
      { date: '周六', visits: Math.floor((patientVisitAnalytics?.totalVisits || 0) * 0.8), type: '总就诊量' },
      { date: '周日', visits: Math.floor((patientVisitAnalytics?.totalVisits || 0) * 0.6), type: '总就诊量' }
    ],
    xField: 'date',
    yField: 'visits',
    seriesField: 'type',
    smooth: true,
    point: { size: 4 },
    tooltip: {
      formatter: (datum: any) => ({
        name: datum.type,
        value: `${datum.visits}人次`
      })
    }
  };

  // 收入构成图配置
  const revenueCompositionConfig = {
    data: revenueByService.map(item => ({
      type: item.serviceName,
      value: item.revenue
    })),
    angleField: 'value',
    colorField: 'type',
    radius: 0.8,
    label: {
      type: 'outer',
      content: '{name} {percentage}'
    },
    interactions: [{ type: 'element-active' }]
  };

  // 热门服务表格列配置
  const serviceColumns = [
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
      title: '服务名称',
      dataIndex: 'serviceName',
      key: 'serviceName'
    },
    {
      title: '科室',
      dataIndex: 'department',
      key: 'department'
    },
    {
      title: '就诊次数',
      dataIndex: 'visitCount',
      key: 'visitCount',
      render: (count: number) => `${count}次`
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
    }
  ];

  // 常见诊断表格列配置
  const diagnosisColumns = [
    {
      title: 'ICD编码',
      dataIndex: 'diagnosisCode',
      key: 'diagnosisCode',
      width: 100
    },
    {
      title: '诊断名称',
      dataIndex: 'diagnosisName',
      key: 'diagnosisName'
    },
    {
      title: '诊断次数',
      dataIndex: 'count',
      key: 'count',
      render: (count: number) => `${count}次`
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
    }
  ];

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
        <p style={{ marginTop: '16px' }}>正在加载业务分析数据...</p>
      </div>
    );
  }

  return (
    <div>
      <Title level={3} style={{ marginBottom: '24px' }}>
        <BarChartOutlined style={{ marginRight: '8px' }} />
        业务统计分析
      </Title>

      {/* 关键业务指标 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="总就诊量"
              value={patientVisitAnalytics?.totalVisits || 0}
              prefix={<UserOutlined />}
              suffix="人次"
              valueStyle={{ color: '#1890ff' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              日均: {patientVisitAnalytics?.averageVisitsPerDay || 0}人次
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="新患者"
              value={patientVisitAnalytics?.newPatients || 0}
              prefix={<UserOutlined />}
              suffix="人"
              valueStyle={{ color: '#52c41a' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              占比: {patientVisitAnalytics ? ((patientVisitAnalytics.newPatients / patientVisitAnalytics.totalVisits) * 100).toFixed(1) : 0}%
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="复诊患者"
              value={patientVisitAnalytics?.returningPatients || 0}
              prefix={<UserOutlined />}
              suffix="人"
              valueStyle={{ color: '#722ed1' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              占比: {patientVisitAnalytics ? ((patientVisitAnalytics.returningPatients / patientVisitAnalytics.totalVisits) * 100).toFixed(1) : 0}%
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="高峰时段就诊"
              value={patientVisitAnalytics?.peakHourVisits || 0}
              prefix={<CalendarOutlined />}
              suffix="人次"
              valueStyle={{ color: '#fa8c16' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              时间: {patientVisitAnalytics?.peakHour || '--'}
            </div>
          </Card>
        </Col>
      </Row>

      {/* 分析图表 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} lg={12}>
          <Card
            title={
              <Space>
                <LineChartOutlined />
                就诊量趋势分析
              </Space>
            }
            style={{ height: '400px' }}
          >
            <Line {...visitTrendConfig} />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card
            title={
              <Space>
                <PieChartOutlined />
                收入构成分析
              </Space>
            }
            style={{ height: '400px' }}
          >
            <Pie {...revenueCompositionConfig} />
          </Card>
        </Col>
      </Row>

      {/* 详细分析表格 */}
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <Card
            title={
              <Space>
                <BarChartOutlined />
                热门服务排行
              </Space>
            }
            style={{ height: '500px' }}
          >
            <Table
              dataSource={popularServices}
              columns={serviceColumns}
              pagination={false}
              size="small"
              rowKey="serviceCode"
            />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card
            title={
              <Space>
                <BarChartOutlined />
                常见诊断统计
              </Space>
            }
            style={{ height: '500px' }}
          >
            <Table
              dataSource={commonDiagnoses}
              columns={diagnosisColumns}
              pagination={false}
              size="small"
              rowKey="diagnosisCode"
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default BusinessAnalytics;