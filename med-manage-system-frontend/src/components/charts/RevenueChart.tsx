import React, { useState, useEffect } from 'react';
import { Line } from '@ant-design/plots';
import { Spin, Empty } from 'antd';
import analyticsService from '../../services/analyticsService';
import { DateRange, MonthlyFinancialReport, DailyFinancialSummary } from '../../types/analytics';

interface RevenueChartProps {
  dateRange: DateRange;
  monthlyReport: MonthlyFinancialReport | null;
}

const RevenueChart: React.FC<RevenueChartProps> = ({ dateRange, monthlyReport }) => {
  const [loading, setLoading] = useState(false);
  const [chartData, setChartData] = useState<any[]>([]);

  const loadChartData = async () => {
    setLoading(true);
    try {
      // 如果是当月数据且有月报告，使用月报告的每日汇总
      if (monthlyReport?.dailySummaries && monthlyReport.dailySummaries.length > 0) {
        const data = monthlyReport.dailySummaries.map((summary: DailyFinancialSummary) => ({
          date: summary.date,
          value: summary.revenue,
          type: '收入'
        }));
        setChartData(data);
      } else {
        // 否则根据日期范围生成模拟数据
        const startDate = new Date(dateRange.startDate);
        const endDate = new Date(dateRange.endDate);
        const data: any[] = [];
        
        // 限制日期范围，避免请求过多数据
        const daysDiff = Math.ceil((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        if (daysDiff > 90) {
          // 如果超过90天，只显示每周的数据
          for (let d = new Date(startDate); d <= endDate; d.setDate(d.getDate() + 7)) {
            const dateStr = d.toISOString().split('T')[0];
            data.push({
              date: dateStr,
              value: Math.floor(Math.random() * 10000) + 5000, // 模拟数据
              type: '收入'
            });
          }
        } else {
          // 90天以内显示每日数据
          for (let d = new Date(startDate); d <= endDate; d.setDate(d.getDate() + 1)) {
            const dateStr = d.toISOString().split('T')[0];
            data.push({
              date: dateStr,
              value: Math.floor(Math.random() * 5000) + 2000, // 模拟数据
              type: '收入'
            });
          }
        }
        setChartData(data);
      }
    } catch (error) {
      console.error('加载收入图表数据失败:', error);
      setChartData([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadChartData();
  }, [dateRange, monthlyReport]);

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin />
      </div>
    );
  }

  if (!chartData.length) {
    return <Empty description="暂无数据" />;
  }

  const config = {
    data: chartData,
    xField: 'date',
    yField: 'value',
    seriesField: 'type',
    smooth: true,
    animation: {
      appear: {
        animation: 'path-in',
        duration: 1000,
      },
    },
    point: {
      size: 3,
      shape: 'circle',
    },
    tooltip: {
      formatter: (datum: any) => {
        return {
          name: datum.type,
          value: `¥${datum.value.toLocaleString()}`
        };
      },
    },
    yAxis: {
      label: {
        formatter: (v: string) => `¥${Number(v).toLocaleString()}`,
      },
    },
    xAxis: {
      label: {
        formatter: (v: string) => {
          const date = new Date(v);
          return `${date.getMonth() + 1}/${date.getDate()}`;
        },
      },
    },
  };

  return <Line {...config} />;
};

export default RevenueChart;