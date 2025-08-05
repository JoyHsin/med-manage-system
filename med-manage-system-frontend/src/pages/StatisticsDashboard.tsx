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
  Divider,
  Button,
  Tooltip,
  Alert,
  Tabs
} from 'antd';
import {
  DollarOutlined,
  UserOutlined,
  TeamOutlined,
  CalendarOutlined,
  TrendingUpOutlined,
  TrendingDownOutlined,
  ReloadOutlined,
  BarChartOutlined,
  PieChartOutlined,
  LineChartOutlined,
  InfoCircleOutlined
} from '@ant-design/icons';
import dayjs, { Dayjs } from 'dayjs';
import analyticsService from '../services/analyticsService';
import {
  DashboardStats,
  DailyFinancialReport,
  MonthlyFinancialReport,
  PatientDemographics,
  DateRange,
  PatientVisitAnalytics,
  WaitTimeAnalytics
} from '../types/analytics';
import RevenueChart from '../components/charts/RevenueChart';
import PatientChart from '../components/charts/PatientChart';
import ServiceChart from '../components/charts/ServiceChart';
import PaymentMethodChart from '../components/charts/PaymentMethodChart';
import BusinessAnalytics from '../components/BusinessAnalytics';
import OperationalAnalytics from '../components/OperationalAnalytics';

const { Title } = Typography;
const { RangePicker } = DatePicker;
const { Option } = Select;
const { TabPane } = Tabs;

const StatisticsDashboard: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [dashboardStats, setDashboardStats] = useState<DashboardStats | null>(null);
  const [todayReport, setTodayReport] = useState<DailyFinancialReport | null>(null);
  const [monthlyReport, setMonthlyReport] = useState<MonthlyFinancialReport | null>(null);
  const [patientDemographics, setPatientDemographics] = useState<PatientDemographics | null>(null);
  const [patientVisitAnalytics, setPatientVisitAnalytics] = useState<PatientVisitAnalytics | null>(null);
  const [waitTimeAnalytics, setWaitTimeAnalytics] = useState<WaitTimeAnalytics | null>(null);
  const [dateRange, setDateRange] = useState<DateRange>({
    startDate: dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
    endDate: dayjs().format('YYYY-MM-DD')
  });
  const [timeRange, setTimeRange] = useState<string>('30days');
  const [lastUpdated, setLastUpdated] = useState<string>('');

  // 加载仪表盘数据
  const loadDashboardData = async (showRefreshing = false) => {
    if (showRefreshing) {
      setRefreshing(true);
    } else {
      setLoading(true);
    }
    
    try {
      const [stats, today, monthly, demographics, visitAnalytics, waitTime] = await Promise.all([
        analyticsService.getDashboardStats(),
        analyticsService.getTodayFinancialReport(),
        analyticsService.getCurrentMonthFinancialReport(),
        analyticsService.getPatientDemographics(),
        analyticsService.getPatientVisitAnalytics(dateRange.startDate, dateRange.endDate),
        analyticsService.getWaitTimeAnalytics(dateRange.startDate, dateRange.endDate)
      ]);

      setDashboardStats(stats);
      setTodayReport(today);
      setMonthlyReport(monthly);
      setPatientDemographics(demographics);
      setPatientVisitAnalytics(visitAnalytics);
      setWaitTimeAnalytics(waitTime);
      setLastUpdated(dayjs().format('YYYY-MM-DD HH:mm:ss'));
    } catch (error) {
      console.error('加载仪表盘数据失败:', error);
      message.error('加载数据失败，请稍后重试');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  // 手动刷新数据
  const handleRefresh = () => {
    loadDashboardData(true);
  };

  // 处理时间范围变化
  const handleTimeRangeChange = (value: string) => {
    setTimeRange(value);
    const today = dayjs();
    let startDate: Dayjs;

    switch (value) {
      case '7days':
        startDate = today.subtract(7, 'day');
        break;
      case '30days':
        startDate = today.subtract(30, 'day');
        break;
      case '90days':
        startDate = today.subtract(90, 'day');
        break;
      case '1year':
        startDate = today.subtract(1, 'year');
        break;
      default:
        startDate = today.subtract(30, 'day');
    }

    setDateRange({
      startDate: startDate.format('YYYY-MM-DD'),
      endDate: today.format('YYYY-MM-DD')
    });
  };

  // 处理自定义日期范围变化
  const handleDateRangeChange = (dates: [Dayjs | null, Dayjs | null] | null) => {
    if (dates && dates[0] && dates[1]) {
      setDateRange({
        startDate: dates[0].format('YYYY-MM-DD'),
        endDate: dates[1].format('YYYY-MM-DD')
      });
      setTimeRange('custom');
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  // 当日期范围变化时重新加载数据
  useEffect(() => {
    if (dateRange.startDate && dateRange.endDate) {
      loadDashboardData();
    }
  }, [dateRange]);

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
        <p style={{ marginTop: '16px' }}>正在加载统计数据...</p>
      </div>
    );
  }

  return (
    <div style={{ padding: '24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <Title level={2} style={{ margin: 0 }}>
            <BarChartOutlined style={{ marginRight: '8px' }} />
            数据统计仪表盘
          </Title>
          <Space>
            <Button 
              icon={<ReloadOutlined />} 
              onClick={handleRefresh}
              loading={refreshing}
              type="default"
            >
              刷新数据
            </Button>
          </Space>
        </div>
        
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Space>
            <Select
              value={timeRange}
              onChange={handleTimeRangeChange}
              style={{ width: 120 }}
            >
              <Option value="7days">近7天</Option>
              <Option value="30days">近30天</Option>
              <Option value="90days">近90天</Option>
              <Option value="1year">近1年</Option>
              <Option value="custom">自定义</Option>
            </Select>
            {timeRange === 'custom' && (
              <RangePicker
                value={[dayjs(dateRange.startDate), dayjs(dateRange.endDate)]}
                onChange={handleDateRangeChange}
                format="YYYY-MM-DD"
              />
            )}
          </Space>
          
          {lastUpdated && (
            <Typography.Text type="secondary" style={{ fontSize: '12px' }}>
              最后更新: {lastUpdated}
            </Typography.Text>
          )}
        </div>
      </div>

      {/* 关键指标卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title={
                <Space>
                  今日收入
                  <Tooltip title="当日所有收费项目的总收入">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              value={todayReport?.totalRevenue || 0}
              precision={2}
              valueStyle={{ color: '#3f8600', fontSize: '24px' }}
              prefix={<DollarOutlined />}
              suffix="元"
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              净收入: ¥{(todayReport?.netRevenue || 0).toLocaleString()}
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title={
                <Space>
                  今日患者
                  <Tooltip title="当日就诊的患者总数">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              value={todayReport?.totalPatients || 0}
              valueStyle={{ color: '#1890ff', fontSize: '24px' }}
              prefix={<UserOutlined />}
              suffix="人"
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              新患者: {patientVisitAnalytics?.newPatients || 0}人
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title={
                <Space>
                  本月收入
                  <Tooltip title="当月累计收入总额">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              value={monthlyReport?.totalRevenue || 0}
              precision={2}
              valueStyle={{ color: '#722ed1', fontSize: '24px' }}
              prefix={<TrendingUpOutlined />}
              suffix="元"
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              日均: ¥{(monthlyReport?.averageDailyRevenue || 0).toLocaleString()}
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title={
                <Space>
                  总患者数
                  <Tooltip title="系统中注册的患者总数">
                    <InfoCircleOutlined style={{ color: '#999' }} />
                  </Tooltip>
                </Space>
              }
              value={patientDemographics?.totalPatients || 0}
              valueStyle={{ color: '#fa8c16', fontSize: '24px' }}
              prefix={<TeamOutlined />}
              suffix="人"
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              平均年龄: {patientDemographics?.averageAge || 0}岁
            </div>
          </Card>
        </Col>
      </Row>

      {/* 详细统计卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="今日账单数"
              value={todayReport?.totalBills || 0}
              prefix={<CalendarOutlined />}
              suffix="笔"
              valueStyle={{ fontSize: '20px' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              平均金额: ¥{((todayReport?.totalRevenue || 0) / Math.max(todayReport?.totalBills || 1, 1)).toFixed(2)}
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="平均等待时间"
              value={waitTimeAnalytics?.averageWaitTime || 0}
              precision={1}
              prefix={<CalendarOutlined />}
              suffix="分钟"
              valueStyle={{ fontSize: '20px', color: waitTimeAnalytics && waitTimeAnalytics.averageWaitTime > 30 ? '#ff4d4f' : '#52c41a' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              最长: {waitTimeAnalytics?.maxWaitTime || 0}分钟
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="本月平均账单"
              value={monthlyReport?.averageBillAmount || 0}
              precision={2}
              prefix={<DollarOutlined />}
              suffix="元"
              valueStyle={{ fontSize: '20px' }}
            />
            <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
              总账单: {monthlyReport?.totalBills || 0}笔
            </div>
          </Card>
        </Col>
      </Row>

      <Divider />

      {/* 数据质量提示 */}
      {(!todayReport || !monthlyReport) && (
        <Alert
          message="数据加载提示"
          description="部分统计数据可能需要时间加载，请稍后刷新查看完整数据。"
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />
      )}

      {/* 标签页内容 */}
      <Tabs defaultActiveKey="overview" size="large">
        <TabPane tab="概览仪表盘" key="overview">
          {/* 图表展示区域 */}
          <Row gutter={[16, 16]}>
            <Col xs={24} lg={12}>
              <Card 
                title={
                  <Space>
                    <LineChartOutlined />
                    收入趋势分析
                    <Tooltip title="显示指定时间范围内的收入变化趋势">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                style={{ height: '420px' }}
                extra={
                  <Typography.Text type="secondary" style={{ fontSize: '12px' }}>
                    {dateRange.startDate} 至 {dateRange.endDate}
                  </Typography.Text>
                }
              >
                <RevenueChart 
                  dateRange={dateRange}
                  monthlyReport={monthlyReport}
                />
              </Card>
            </Col>
            <Col xs={24} lg={12}>
              <Card 
                title={
                  <Space>
                    <PieChartOutlined />
                    患者分布统计
                    <Tooltip title="按性别、年龄、地区统计患者分布情况">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                style={{ height: '420px' }}
                extra={
                  <Typography.Text type="secondary" style={{ fontSize: '12px' }}>
                    总计 {patientDemographics?.totalPatients || 0} 人
                  </Typography.Text>
                }
              >
                <PatientChart 
                  patientDemographics={patientDemographics}
                />
              </Card>
            </Col>
          </Row>

          <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
            <Col xs={24} lg={12}>
              <Card 
                title={
                  <Space>
                    <BarChartOutlined />
                    服务收入构成
                    <Tooltip title="各类医疗服务的收入占比分析">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                style={{ height: '420px' }}
                extra={
                  <Typography.Text type="secondary" style={{ fontSize: '12px' }}>
                    今日数据
                  </Typography.Text>
                }
              >
                <ServiceChart 
                  dateRange={dateRange}
                  todayReport={todayReport}
                />
              </Card>
            </Col>
            <Col xs={24} lg={12}>
              <Card 
                title={
                  <Space>
                    <PieChartOutlined />
                    支付方式统计
                    <Tooltip title="不同支付方式的使用情况统计">
                      <InfoCircleOutlined style={{ color: '#999' }} />
                    </Tooltip>
                  </Space>
                }
                style={{ height: '420px' }}
                extra={
                  <Typography.Text type="secondary" style={{ fontSize: '12px' }}>
                    今日数据
                  </Typography.Text>
                }
              >
                <PaymentMethodChart 
                  dateRange={dateRange}
                  todayReport={todayReport}
                />
              </Card>
            </Col>
          </Row>
        </TabPane>

        <TabPane tab="业务分析" key="business">
          <BusinessAnalytics 
            dateRange={dateRange}
            onDateRangeChange={setDateRange}
          />
        </TabPane>

        <TabPane tab="运营分析" key="operational">
          <OperationalAnalytics 
            dateRange={dateRange}
          />
        </TabPane>
      </Tabs>
    </div>
  );
};

export default StatisticsDashboard;