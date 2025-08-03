import React, { useState } from 'react';
import {
  Card,
  Button,
  Space,
  List,
  Tag,
  Badge,
  message,
  Modal,
  Checkbox,
  Typography,
  Alert,
  Divider
} from 'antd';
import {
  PlayCircleOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  WarningOutlined,
  FireOutlined
} from '@ant-design/icons';
import dayjs from 'dayjs';
import { medicalOrderService } from '../services/medicalOrderService';
import {
  MedicalOrder,
  ORDER_STATUS_CONFIG,
  ORDER_PRIORITY_CONFIG,
  ORDER_TYPE_CONFIG
} from '../types/medicalOrder';

const { Title, Text } = Typography;
const { confirm } = Modal;

interface MedicalOrderQuickActionsProps {
  orders: MedicalOrder[];
  onRefresh: () => void;
}

const MedicalOrderQuickActions: React.FC<MedicalOrderQuickActionsProps> = ({
  orders,
  onRefresh
}) => {
  const [selectedOrders, setSelectedOrders] = useState<number[]>([]);
  const [batchExecuteVisible, setBatchExecuteVisible] = useState(false);
  const [executing, setExecuting] = useState(false);

  // 筛选不同类型的医嘱
  const urgentOrders = orders.filter(order => 
    order.priority === 'URGENT' && order.status === 'PENDING'
  );
  
  const injectionOrders = orders.filter(order => 
    order.orderType === 'INJECTION' && order.status === 'PENDING'
  );
  
  const medicationOrders = orders.filter(order => 
    order.orderType === 'ORAL_MEDICATION' && order.status === 'PENDING'
  );

  // 快速执行单个医嘱（适用于简单医嘱）
  const handleQuickExecute = async (order: MedicalOrder) => {
    // 只对简单的医嘱类型支持快速执行
    if (!['ORAL_MEDICATION', 'OBSERVATION'].includes(order.orderType)) {
      message.warning('该类型医嘱需要详细执行记录，请使用完整执行流程');
      return;
    }

    confirm({
      title: '快速执行医嘱',
      content: `确定要执行医嘱"${order.content}"吗？`,
      onOk: async () => {
        try {
          await medicalOrderService.executeOrder(order.id, {
            executionNotes: '快速执行'
          });
          message.success('医嘱执行成功');
          onRefresh();
        } catch (error) {
          console.error('执行医嘱失败:', error);
          message.error('执行医嘱失败');
        }
      }
    });
  };

  // 批量执行医嘱
  const handleBatchExecute = () => {
    if (selectedOrders.length === 0) {
      message.warning('请选择要执行的医嘱');
      return;
    }
    setBatchExecuteVisible(true);
  };

  // 确认批量执行
  const handleConfirmBatchExecute = async () => {
    try {
      setExecuting(true);
      const promises = selectedOrders.map(orderId =>
        medicalOrderService.executeOrder(orderId, {
          executionNotes: '批量执行'
        })
      );
      await Promise.all(promises);
      message.success(`成功执行 ${selectedOrders.length} 个医嘱`);
      setSelectedOrders([]);
      setBatchExecuteVisible(false);
      onRefresh();
    } catch (error) {
      console.error('批量执行失败:', error);
      message.error('批量执行失败');
    } finally {
      setExecuting(false);
    }
  };

  // 渲染医嘱列表项
  const renderOrderItem = (order: MedicalOrder, showCheckbox = false) => {
    const typeConfig = ORDER_TYPE_CONFIG[order.orderType as keyof typeof ORDER_TYPE_CONFIG];
    const priorityConfig = ORDER_PRIORITY_CONFIG[order.priority as keyof typeof ORDER_PRIORITY_CONFIG];
    const statusConfig = ORDER_STATUS_CONFIG[order.status as keyof typeof ORDER_STATUS_CONFIG];

    return (
      <List.Item
        key={order.id}
        actions={[
          showCheckbox ? (
            <Checkbox
              checked={selectedOrders.includes(order.id)}
              onChange={(e) => {
                if (e.target.checked) {
                  setSelectedOrders([...selectedOrders, order.id]);
                } else {
                  setSelectedOrders(selectedOrders.filter(id => id !== order.id));
                }
              }}
            />
          ) : (
            <Button
              type="link"
              size="small"
              icon={<PlayCircleOutlined />}
              onClick={() => handleQuickExecute(order)}
              disabled={!['ORAL_MEDICATION', 'OBSERVATION'].includes(order.orderType)}
            >
              快速执行
            </Button>
          )
        ]}
      >
        <List.Item.Meta
          avatar={
            <Badge
              count={order.priority === 'URGENT' ? <FireOutlined style={{ color: '#ff4d4f' }} /> : 0}
            >
              <div
                style={{
                  width: 40,
                  height: 40,
                  borderRadius: '50%',
                  backgroundColor: typeConfig?.color || '#1890ff',
                  color: '#fff',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '16px'
                }}
              >
                {typeConfig?.icon || '📋'}
              </div>
            </Badge>
          }
          title={
            <div>
              <Text strong>{order.content}</Text>
              <div style={{ marginTop: 4 }}>
                <Tag color={typeConfig?.color}>{typeConfig?.text}</Tag>
                <Tag color={priorityConfig.color}>{priorityConfig.text}</Tag>
                <Badge status={statusConfig.badge as any} text={statusConfig.text} />
              </div>
            </div>
          }
          description={
            <div>
              {order.dosage && <div>剂量: {order.dosage}</div>}
              {order.frequency && <div>频次: {order.frequency}</div>}
              <div style={{ color: '#999', fontSize: '12px' }}>
                开具时间: {dayjs(order.prescribedAt).format('MM-DD HH:mm')}
              </div>
            </div>
          }
        />
      </List.Item>
    );
  };

  return (
    <div>
      {/* 紧急医嘱 */}
      {urgentOrders.length > 0 && (
        <Card
          title={
            <span style={{ color: '#ff4d4f' }}>
              <WarningOutlined style={{ marginRight: 8 }} />
              紧急医嘱 ({urgentOrders.length})
            </span>
          }
          style={{ marginBottom: 16 }}
          size="small"
        >
          <List
            dataSource={urgentOrders}
            renderItem={(order) => renderOrderItem(order)}
            size="small"
          />
        </Card>
      )}

      {/* 注射医嘱 */}
      {injectionOrders.length > 0 && (
        <Card
          title={
            <span>
              💉 注射医嘱 ({injectionOrders.length})
            </span>
          }
          style={{ marginBottom: 16 }}
          size="small"
        >
          <List
            dataSource={injectionOrders}
            renderItem={(order) => renderOrderItem(order)}
            size="small"
          />
        </Card>
      )}

      {/* 口服药物医嘱 */}
      {medicationOrders.length > 0 && (
        <Card
          title={
            <span>
              💊 口服药物 ({medicationOrders.length})
            </span>
          }
          extra={
            <Space>
              <Button
                type="primary"
                size="small"
                onClick={handleBatchExecute}
                disabled={selectedOrders.length === 0}
              >
                批量执行 ({selectedOrders.length})
              </Button>
            </Space>
          }
          style={{ marginBottom: 16 }}
          size="small"
        >
          <List
            dataSource={medicationOrders}
            renderItem={(order) => renderOrderItem(order, true)}
            size="small"
          />
        </Card>
      )}

      {/* 批量执行确认模态框 */}
      <Modal
        title="批量执行医嘱"
        open={batchExecuteVisible}
        onOk={handleConfirmBatchExecute}
        onCancel={() => {
          setBatchExecuteVisible(false);
          setSelectedOrders([]);
        }}
        confirmLoading={executing}
      >
        <Alert
          message={`即将执行 ${selectedOrders.length} 个医嘱`}
          type="info"
          style={{ marginBottom: 16 }}
        />
        <div>
          <Text strong>选中的医嘱：</Text>
          <ul style={{ marginTop: 8 }}>
            {selectedOrders.map(orderId => {
              const order = orders.find(o => o.id === orderId);
              return order ? (
                <li key={orderId}>{order.content}</li>
              ) : null;
            })}
          </ul>
        </div>
        <Alert
          message="注意"
          description="批量执行将为所有选中的医嘱添加统一的执行记录，如需详细记录请单独执行。"
          type="warning"
          style={{ marginTop: 16 }}
        />
      </Modal>
    </div>
  );
};

export default MedicalOrderQuickActions;