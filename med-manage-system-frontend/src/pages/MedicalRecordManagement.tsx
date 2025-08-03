import React, { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Tag,
  Badge,
  message,
  Modal,
  Select,
  Input,
  Row,
  Col,
  Typography,
  Divider,
  DatePicker,
  Tooltip,
  Statistic
} from 'antd';
import {
  FileTextOutlined,
  PlusOutlined,
  EditOutlined,
  EyeOutlined,
  PrinterOutlined,
  SearchOutlined,
  FilterOutlined,
  ReloadOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { medicalRecordService } from '../services/medicalRecordService';
import { patientService } from '../services/patientService';
import {
  MedicalRecord,
  RecordStatus,
  RecordType,
  RECORD_STATUS_CONFIG,
  RECORD_TYPE_CONFIG
} from '../types/medicalRecord';

const { Title, Text } = Typography;
const { Option } = Select;
const { RangePicker } = DatePicker;

interface Patient {
  id: number;
  name: string;
  phone: string;
  patientNumber: string;
}

const MedicalRecordManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
  const [medicalRecords, setMedicalRecords] = useState<MedicalRecord[]>([]);
  const [filteredRecords, setFilteredRecords] = useState<MedicalRecord[]>([]);
  const [statusFilter, setStatusFilter] = useState<RecordStatus | 'ALL'>('ALL');
  const [typeFilter, setTypeFilter] = useState<RecordType | 'ALL'>('ALL');
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs] | null>(null);
  const [searchText, setSearchText] = useState('');
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState<MedicalRecord | null>(null);

  // 统计数据
  const statistics = React.useMemo(() => {
    const stats = {
      total: filteredRecords.length,
      draft: filteredRecords.filter(r => r.status === '草稿').length,
      pending: filteredRecords.filter(r => r.status === '待审核').length,
      approved: filteredRecords.filter(r => r.status === '已审核').length,
      archived: filteredRecords.filter(r => r.status === '已归档').length
    };
    return stats;
  }, [filteredRecords]);

  // 加载患者列表
  useEffect(() => {
    loadPatients();
  }, []);

  const loadPatients = async () => {
    try {
      const data = await patientService.getAllPatients();
      setPatients(data);
    } catch (error) {
      console.error('加载患者列表失败:', error);
      message.error('加载患者列表失败');
    }
  };

  // 加载病历数据
  const loadMedicalRecords = useCallback(async () => {
    if (!selectedPatient) return;

    try {
      setLoading(true);
      const records = await medicalRecordService.getPatientMedicalRecords(selectedPatient.id);
      setMedicalRecords(records);
    } catch (error) {
      console.error('加载病历数据失败:', error);
      message.error('加载病历数据失败');
    } finally {
      setLoading(false);
    }
  }, [selectedPatient]);

  // 选择患者时加载病历
  useEffect(() => {
    if (selectedPatient) {
      loadMedicalRecords();
    }
  }, [selectedPatient, loadMedicalRecords]);

  // 过滤病历数据
  useEffect(() => {
    let filtered = [...medicalRecords];

    // 状态过滤
    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(record => record.status === statusFilter);
    }

    // 类型过滤
    if (typeFilter !== 'ALL') {
      filtered = filtered.filter(record => record.recordType === typeFilter);
    }

    // 日期范围过滤
    if (dateRange) {
      filtered = filtered.filter(record => {
        const recordDate = dayjs(record.recordDate);
        return recordDate.isAfter(dateRange[0]) && recordDate.isBefore(dateRange[1]);
      });
    }

    // 文本搜索
    if (searchText) {
      const searchLower = searchText.toLowerCase();
      filtered = filtered.filter(record =>
        record.recordNumber.toLowerCase().includes(searchLower) ||
        record.chiefComplaint?.toLowerCase().includes(searchLower) ||
        record.preliminaryDiagnosis?.toLowerCase().includes(searchLower) ||
        record.finalDiagnosis?.toLowerCase().includes(searchLower)
      );
    }

    setFilteredRecords(filtered);
  }, [medicalRecords, statusFilter, typeFilter, dateRange, searchText]);

  // 选择患者
  const handlePatientSelect = (patientId: number) => {
    const patient = patients.find(p => p.id === patientId);
    setSelectedPatient(patient || null);
  };

  // 查看病历详情
  const handleViewRecord = (record: MedicalRecord) => {
    setSelectedRecord(record);
    setDetailModalVisible(true);
  };

  // 编辑病历
  const handleEditRecord = (record: MedicalRecord) => {
    // TODO: 跳转到病历编辑页面
    message.info('跳转到病历编辑页面');
  };

  // 新建病历
  const handleCreateRecord = () => {
    if (!selectedPatient) {
      message.warning('请先选择患者');
      return;
    }
    // TODO: 跳转到病历创建页面
    message.info('跳转到病历创建页面');
  };

  // 打印病历
  const handlePrintRecord = async (record: MedicalRecord) => {
    try {
      await medicalRecordService.printMedicalRecord(record.id);
      message.success('病历打印成功');
    } catch (error) {
      console.error('打印病历失败:', error);
      message.error('打印病历失败');
    }
  };

  // 提交审核
  const handleSubmitReview = async (record: MedicalRecord) => {
    try {
      await medicalRecordService.submitForReview(record.id);
      message.success('病历已提交审核');
      loadMedicalRecords();
    } catch (error) {
      console.error('提交审核失败:', error);
      message.error('提交审核失败');
    }
  };

  // 表格列定义
  const columns: ColumnsType<MedicalRecord> = [
    {
      title: '病历编号',
      dataIndex: 'recordNumber',
      key: 'recordNumber',
      width: 120,
      fixed: 'left'
    },
    {
      title: '病历类型',
      dataIndex: 'recordType',
      key: 'recordType',
      width: 100,
      render: (type: RecordType) => {
        const config = RECORD_TYPE_CONFIG[type];
        return (
          <Tag color={config.color}>
            {config.icon} {type}
          </Tag>
        );
      }
    },
    {
      title: '主诉',
      dataIndex: 'chiefComplaint',
      key: 'chiefComplaint',
      width: 200,
      ellipsis: true,
      render: (text: string) => (
        <Tooltip title={text}>
          {text || '-'}
        </Tooltip>
      )
    },
    {
      title: '初步诊断',
      dataIndex: 'preliminaryDiagnosis',
      key: 'preliminaryDiagnosis',
      width: 150,
      ellipsis: true,
      render: (text: string) => (
        <Tooltip title={text}>
          {text || '-'}
        </Tooltip>
      )
    },
    {
      title: '最终诊断',
      dataIndex: 'finalDiagnosis',
      key: 'finalDiagnosis',
      width: 150,
      ellipsis: true,
      render: (text: string) => (
        <Tooltip title={text}>
          {text || '-'}
        </Tooltip>
      )
    },
    {
      title: '记录时间',
      dataIndex: 'recordDate',
      key: 'recordDate',
      width: 120,
      render: (date: string) => dayjs(date).format('MM-DD HH:mm')
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: RecordStatus) => {
        const config = RECORD_STATUS_CONFIG[status];
        return (
          <Badge status={config.badge as any} text={status} />
        );
      }
    },
    {
      title: '科室',
      dataIndex: 'department',
      key: 'department',
      width: 100,
      render: (dept: string) => dept || '-'
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewRecord(record)}
          >
            查看
          </Button>
          {record.status === '草稿' && (
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEditRecord(record)}
            >
              编辑
            </Button>
          )}
          {record.status === '草稿' && (
            <Button
              type="link"
              size="small"
              onClick={() => handleSubmitReview(record)}
            >
              提交审核
            </Button>
          )}
          <Button
            type="link"
            size="small"
            icon={<PrinterOutlined />}
            onClick={() => handlePrintRecord(record)}
          >
            打印
          </Button>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <FileTextOutlined style={{ marginRight: 8 }} />
        电子病历管理
      </Title>

      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="总病历数"
              value={statistics.total}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="草稿"
              value={statistics.draft}
              valueStyle={{ color: '#666' }}
              prefix={<EditOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="待审核"
              value={statistics.pending}
              valueStyle={{ color: '#1890ff' }}
              prefix={<ClockCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="已审核"
              value={statistics.approved}
              valueStyle={{ color: '#52c41a' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
      </Row>

      {/* 患者选择和操作栏 */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={16} align="middle">
          <Col span={8}>
            <Text strong>选择患者：</Text>
            <Select
              showSearch
              placeholder="搜索患者姓名或编号"
              style={{ width: '100%', marginLeft: 8 }}
              optionFilterProp="children"
              onChange={handlePatientSelect}
              filterOption={(input, option) =>
                (option?.children as unknown as string)
                  ?.toLowerCase()
                  ?.includes(input.toLowerCase())
              }
            >
              {patients.map(patient => (
                <Option key={patient.id} value={patient.id}>
                  {patient.name} - {patient.patientNumber} - {patient.phone}
                </Option>
              ))}
            </Select>
          </Col>
          <Col>
            <Space>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={handleCreateRecord}
                disabled={!selectedPatient}
              >
                新建病历
              </Button>
              <Button
                icon={<ReloadOutlined />}
                onClick={loadMedicalRecords}
                loading={loading}
                disabled={!selectedPatient}
              >
                刷新
              </Button>
            </Space>
          </Col>
        </Row>

        {selectedPatient && (
          <div style={{ marginTop: 16 }}>
            <Text type="secondary">
              当前患者：{selectedPatient.name} ({selectedPatient.patientNumber})
            </Text>
          </div>
        )}
      </Card>

      {/* 筛选条件 */}
      {selectedPatient && (
        <Card style={{ marginBottom: 24 }}>
          <Row gutter={16} align="middle">
            <Col span={4}>
              <Text strong>状态：</Text>
              <Select
                value={statusFilter}
                onChange={setStatusFilter}
                style={{ width: '100%', marginLeft: 8 }}
              >
                <Option value="ALL">全部</Option>
                <Option value="草稿">草稿</Option>
                <Option value="待审核">待审核</Option>
                <Option value="已审核">已审核</Option>
                <Option value="已归档">已归档</Option>
              </Select>
            </Col>
            <Col span={4}>
              <Text strong>类型：</Text>
              <Select
                value={typeFilter}
                onChange={setTypeFilter}
                style={{ width: '100%', marginLeft: 8 }}
              >
                <Option value="ALL">全部</Option>
                <Option value="门诊病历">门诊病历</Option>
                <Option value="急诊病历">急诊病历</Option>
                <Option value="住院病历">住院病历</Option>
                <Option value="体检报告">体检报告</Option>
                <Option value="其他">其他</Option>
              </Select>
            </Col>
            <Col span={6}>
              <Text strong>时间范围：</Text>
              <RangePicker
                value={dateRange}
                onChange={setDateRange}
                style={{ marginLeft: 8 }}
              />
            </Col>
            <Col span={6}>
              <Text strong>搜索：</Text>
              <Input.Search
                placeholder="病历编号、主诉或诊断"
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
                style={{ marginLeft: 8 }}
                allowClear
              />
            </Col>
          </Row>
        </Card>
      )}

      {/* 病历列表 */}
      {selectedPatient && (
        <Card title="病历列表">
          <Table
            columns={columns}
            dataSource={filteredRecords}
            rowKey="id"
            loading={loading}
            pagination={{
              pageSize: 10,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total) => `共 ${total} 条记录`
            }}
            scroll={{ x: 1400 }}
          />
        </Card>
      )}

      {/* 病历详情模态框 */}
      <Modal
        title="病历详情"
        open={detailModalVisible}
        onCancel={() => {
          setDetailModalVisible(false);
          setSelectedRecord(null);
        }}
        footer={null}
        width={1000}
      >
        {selectedRecord && (
          <div>
            {/* 基本信息 */}
            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col span={8}>
                <Text strong>病历编号：</Text>
                <Text>{selectedRecord.recordNumber}</Text>
              </Col>
              <Col span={8}>
                <Text strong>病历类型：</Text>
                <Tag color={RECORD_TYPE_CONFIG[selectedRecord.recordType].color}>
                  {selectedRecord.recordType}
                </Tag>
              </Col>
              <Col span={8}>
                <Text strong>状态：</Text>
                <Badge
                  status={RECORD_STATUS_CONFIG[selectedRecord.status].badge as any}
                  text={selectedRecord.status}
                />
              </Col>
            </Row>

            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col span={8}>
                <Text strong>记录时间：</Text>
                <Text>{dayjs(selectedRecord.recordDate).format('YYYY-MM-DD HH:mm')}</Text>
              </Col>
              <Col span={8}>
                <Text strong>科室：</Text>
                <Text>{selectedRecord.department || '-'}</Text>
              </Col>
              <Col span={8}>
                <Text strong>医生：</Text>
                <Text>医生{selectedRecord.doctorId}</Text>
              </Col>
            </Row>

            <Divider />

            {/* 病历内容 */}
            <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
              {selectedRecord.chiefComplaint && (
                <div style={{ marginBottom: 16 }}>
                  <Text strong>主诉：</Text>
                  <div style={{ marginTop: 4, padding: '8px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                    {selectedRecord.chiefComplaint}
                  </div>
                </div>
              )}

              {selectedRecord.presentIllness && (
                <div style={{ marginBottom: 16 }}>
                  <Text strong>现病史：</Text>
                  <div style={{ marginTop: 4, padding: '8px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                    {selectedRecord.presentIllness}
                  </div>
                </div>
              )}

              {selectedRecord.physicalExamination && (
                <div style={{ marginBottom: 16 }}>
                  <Text strong>体格检查：</Text>
                  <div style={{ marginTop: 4, padding: '8px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                    {selectedRecord.physicalExamination}
                  </div>
                </div>
              )}

              {selectedRecord.preliminaryDiagnosis && (
                <div style={{ marginBottom: 16 }}>
                  <Text strong>初步诊断：</Text>
                  <div style={{ marginTop: 4, padding: '8px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                    {selectedRecord.preliminaryDiagnosis}
                  </div>
                </div>
              )}

              {selectedRecord.finalDiagnosis && (
                <div style={{ marginBottom: 16 }}>
                  <Text strong>最终诊断：</Text>
                  <div style={{ marginTop: 4, padding: '8px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                    {selectedRecord.finalDiagnosis}
                  </div>
                </div>
              )}

              {selectedRecord.treatmentPlan && (
                <div style={{ marginBottom: 16 }}>
                  <Text strong>治疗方案：</Text>
                  <div style={{ marginTop: 4, padding: '8px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                    {selectedRecord.treatmentPlan}
                  </div>
                </div>
              )}

              {selectedRecord.followUpAdvice && (
                <div style={{ marginBottom: 16 }}>
                  <Text strong>随访建议：</Text>
                  <div style={{ marginTop: 4, padding: '8px', backgroundColor: '#f5f5f5', borderRadius: '4px' }}>
                    {selectedRecord.followUpAdvice}
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default MedicalRecordManagement;