import React, { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Form,
  Input,
  Button,
  Select,
  Row,
  Col,
  Typography,
  message,
  Space,
  Divider,
  Table,
  Tag,
  Modal,
  DatePicker,
  Alert,
  Tabs,
  List,
  Tooltip,
  Badge,
  InputNumber,
  Checkbox,
  Popconfirm,
  Drawer,
  Descriptions,
  Statistic,
  Progress
} from 'antd';
import {
  MedicineBoxOutlined,
  PlusOutlined,
  SaveOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  WarningOutlined,
  ExportOutlined,
  SearchOutlined,
  StockOutlined,
  BarChartOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { inventoryService } from '../services/inventoryService';
import {
  Medicine,
  InventoryLevel,
  StockTransaction,
  CreateMedicineRequest,
  StockUpdateRequest,
  InventoryStats,
  MEDICINE_CATEGORY_CONFIG,
  INVENTORY_STATUS_CONFIG,
  TRANSACTION_TYPE_CONFIG,
  TRANSACTION_STATUS_CONFIG,
  DOSAGE_FORM_OPTIONS,
  UNIT_OPTIONS,
  STORAGE_CONDITIONS
} from '../types/inventory';

const { Title, Text, Paragraph } = Typography;
const { Option } = Select;
const { TextArea } = Input;
const { TabPane } = Tabs;
const { confirm } = Modal;
const { RangePicker } = DatePicker;

const InventoryManagement: React.FC = () => {
  const [form] = Form.useForm();
  const [stockForm] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [medicines, setMedicines] = useState<Medicine[]>([]);
  const [currentMedicine, setCurrentMedicine] = useState<Medicine | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [activeTab, setActiveTab] = useState('list');
  const [stockModalVisible, setStockModalVisible] = useState(false);
  const [detailDrawerVisible, setDetailDrawerVisible] = useState(false);
  const [inventoryLevels, setInventoryLevels] = useState<InventoryLevel[]>([]);
  const [stockTransactions, setStockTransactions] = useState<StockTransaction[]>([]);
  const [stats, setStats] = useState<InventoryStats | null>(null);

  // 加载初始数据
  useEffect(() => {
    loadMedicines();
    loadStats();
  }, []);

  const loadMedicines = async () => {
    try {
      setLoading(true);
      const data = await inventoryService.getAllActiveMedicines();
      setMedicines(data);
    } catch (error) {
      console.error('加载药品列表失败:', error);
      message.error('加载药品列表失败');
    } finally {
      setLoading(false);
    }
  };

  const loadStats = async () => {
    try {
      const data = await inventoryService.getInventoryStatistics();
      setStats(data);
    } catch (error) {
      console.error('加载统计数据失败:', error);
    }
  };

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <MedicineBoxOutlined style={{ marginRight: 8 }} />
        药品库存管理
      </Title>

      {/* 统计卡片 */}
      {stats && (
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={4}>
            <Card>
              <Statistic title="药品总数" value={stats.totalMedicines} />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="库存不足" 
                value={stats.lowStockCount}
                valueStyle={{ color: '#f5222d' }}
                prefix={<WarningOutlined />}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="即将过期" 
                value={stats.expiringCount}
                valueStyle={{ color: '#faad14' }}
                prefix={<ClockCircleOutlined />}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="已过期" 
                value={stats.expiredCount}
                valueStyle={{ color: '#f5222d' }}
                prefix={<CloseCircleOutlined />}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="处方药" 
                value={stats.prescriptionMedicines}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col span={4}>
            <Card>
              <Statistic 
                title="库存总价值" 
                value={stats.totalInventoryValue} 
                precision={2}
                prefix="¥"
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
        </Row>
      )}

      <Card>
        <p>药品库存管理功能开发中...</p>
      </Card>
    </div>
  );
};

export default InventoryManagement;