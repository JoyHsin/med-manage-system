import React from 'react';
import { Pie } from '@ant-design/plots';
import { Empty, Tabs } from 'antd';
import { PatientDemographics } from '../../types/analytics';

interface PatientChartProps {
  patientDemographics: PatientDemographics | null;
}

const PatientChart: React.FC<PatientChartProps> = ({ patientDemographics }) => {
  if (!patientDemographics) {
    return <Empty description="暂无数据" />;
  }

  // 性别分布数据
  const genderData = Object.entries(patientDemographics.genderDistribution || {}).map(([key, value]) => ({
    type: key === 'MALE' ? '男性' : key === 'FEMALE' ? '女性' : '其他',
    value: value,
  }));

  // 年龄分布数据
  const ageData = Object.entries(patientDemographics.ageGroupDistribution || {}).map(([key, value]) => ({
    type: key,
    value: value,
  }));

  // 地区分布数据
  const locationData = Object.entries(patientDemographics.locationDistribution || {}).map(([key, value]) => ({
    type: key || '未知',
    value: value,
  }));

  const pieConfig = {
    angleField: 'value',
    colorField: 'type',
    radius: 0.8,
    label: {
      type: 'outer',
      content: '{name} {percentage}',
    },
    interactions: [
      {
        type: 'element-active',
      },
    ],
    legend: {
      position: 'bottom' as const,
    },
  };

  const tabItems = [
    {
      key: 'gender',
      label: '性别分布',
      children: genderData.length > 0 ? (
        <Pie {...pieConfig} data={genderData} />
      ) : (
        <Empty description="暂无性别分布数据" />
      ),
    },
    {
      key: 'age',
      label: '年龄分布',
      children: ageData.length > 0 ? (
        <Pie {...pieConfig} data={ageData} />
      ) : (
        <Empty description="暂无年龄分布数据" />
      ),
    },
    {
      key: 'location',
      label: '地区分布',
      children: locationData.length > 0 ? (
        <Pie {...pieConfig} data={locationData} />
      ) : (
        <Empty description="暂无地区分布数据" />
      ),
    },
  ];

  return (
    <div style={{ height: '320px' }}>
      <Tabs items={tabItems} size="small" />
    </div>
  );
};

export default PatientChart;