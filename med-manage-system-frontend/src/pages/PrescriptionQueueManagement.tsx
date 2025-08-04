import React, { useState } from 'react';
import { Typography, Card, Tabs, message } from 'antd';
import {
  MedicineBoxOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';
import PrescriptionQueue from '../components/PrescriptionQueue';

const { Title } = Typography;
const { TabPane } = Tabs;

const PrescriptionQueueManagement: React.FC = () => {
  const [activeTab, setActiveTab] = useState('queue');

  const handleStartDispensing = (prescription: any) => {
    message.success(`开始调剂处方: ${prescription.prescriptionNumber}`);
    // 可以在这里添加额外的逻辑，比如跳转到调剂页面
  };

  const handleViewDetails = (prescription: any) => {
    console.log('查看处方详情:', prescription);
    // 可以在这里添加额外的逻辑，比如记录查看日志
  };

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <MedicineBoxOutlined style={{ marginRight: 8 }} />
        处方调剂队列管理
      </Title>

      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        <TabPane 
          tab={
            <span>
              <ClockCircleOutlined />
              处方队列
            </span>
          } 
          key="queue"
        >
          <PrescriptionQueue
            onStartDispensing={handleStartDispensing}
            onViewDetails={handleViewDetails}
          />
        </TabPane>

        <TabPane 
          tab={
            <span>
              <CheckCircleOutlined />
              队列统计
            </span>
          } 
          key="statistics"
        >
          <Card>
            <div style={{ textAlign: 'center', padding: '40px' }}>
              <ExclamationCircleOutlined style={{ fontSize: '48px', color: '#faad14' }} />
              <Title level={4} style={{ marginTop: '16px' }}>
                队列统计功能开发中...
              </Title>
              <p>此功能将在后续版本中提供</p>
            </div>
          </Card>
        </TabPane>
      </Tabs>
    </div>
  );
};

export default PrescriptionQueueManagement;