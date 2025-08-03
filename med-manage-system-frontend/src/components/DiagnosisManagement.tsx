import React, { useState, useEffect } from 'react';
import {
  Card,
  Table,
  Button,
  Space,
  Tag,
  Modal,
  Form,
  Input,
  Select,
  Switch,
  message,
  Typography,
  AutoComplete,
  Divider,
  Alert
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  SearchOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { medicalRecordService } from '../services/medicalRecordService';
import {
  Diagnosis,
  DiagnosisType,
  SeverityLevel,
  DiagnosisStatus,
  DIAGNOSIS_TYPE_CONFIG,
  SEVERITY_CONFIG,
  DIAGNOSIS_STATUS_CONFIG,
  COMMON_DIAGNOSES
} from '../types/medicalRecord';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { Option } = Select;
const { confirm } = Modal;

interface DiagnosisManagementProps {
  medicalRecordId: number;
  diagnoses: Diagnosis[];
  onDiagnosisChange: (diagnoses: Diagnosis[]) => void;
  readonly?: boolean;
}

const DiagnosisManagement: React.FC<DiagnosisManagementProps> = ({
  medicalRecordId,
  diagnoses,
  onDiagnosisChange,
  readonly = false
}) => {
  const [form] = Form.useForm();
  const [modalVisible, setModalVisible] = useState(false);
  const [editingDiagnosis, setEditingDiagnosis] = useState<Diagnosis | null>(null);
  const [loading, setLoading] = useState(false);
  const [icdSuggestions, setIcdSuggestions] = useState<Array<{ code: string; name: string }>>([]);

  const isEditMode = !!editingDiagnosis;

  // ICD编码搜索
  const handleICDSearch = async (value: string) => {
    if (value.length > 1) {
      try {
        const suggestions = await medicalRecordService.searchICDCodes(value);
        setIcdSuggestions(suggestions);
      } catch (error) {
        console.error('搜索ICD编码失败:', error);
      }
    }
  };

  // 添加诊断
  const handleAddDiagnosis = () => {
    setEditingDiagnosis(null);
    form.resetFields();
    form.setFieldsValue({
      diagnosisType: '主要诊断',
      status: '初步诊断',
      isPrimary: false,
      isChronicDisease: false,
      isInfectious: false,
      isHereditary: false,
      sortOrder: diagnoses.length + 1
    });
    setModalVisible(true);
  };

  // 编辑诊断
  const handleEditDiagnosis = (diagnosis: Diagnosis) => {
    setEditingDiagnosis(diagnosis);
    form.setFieldsValue(diagnosis);
    setModalVisible(true);
  };

  // 删除诊断
  const handleDeleteDiagnosis = (diagnosis: Diagnosis) => {
    confirm({
      title: '确认删除',
      content: `确定要删除诊断"${diagnosis.diagnosisName}"吗？`,
      icon: <ExclamationCircleOutlined />,
      onOk: () => {
        const newDiagnoses = diagnoses.filter(d => d.id !== diagnosis.id);
        onDiagnosisChange(newDiagnoses);
        message.success('诊断删除成功');
      }
    });
  };

  // 确认诊断
  const handleConfirmDiagnosis = (diagnosis: Diagnosis) => {
    const newDiagnoses = diagnoses.map(d =>
      d.id === diagnosis.id ? { ...d, status: '确定诊断' as DiagnosisStatus } : d
    );
    onDiagnosisChange(newDiagnoses);
    message.success('诊断已确认');
  };

  // 保存诊断
  const handleSaveDiagnosis = async (values: any) => {
    try {
      setLoading(true);

      if (isEditMode && editingDiagnosis) {
        // 编辑模式
        const updatedDiagnosis: Diagnosis = {
          ...editingDiagnosis,
          ...values,
          diagnosisTime: editingDiagnosis.diagnosisTime
        };
        const newDiagnoses = diagnoses.map(d =>
          d.id === editingDiagnosis.id ? updatedDiagnosis : d
        );
        onDiagnosisChange(newDiagnoses);
        message.success('诊断更新成功');
      } else {
        // 新增模式
        const newDiagnosis: Diagnosis = {
          id: Date.now(), // 临时ID，实际应该由后端生成
          medicalRecordId,
          doctorId: 1, // TODO: 从用户上下文获取
          diagnosisTime: new Date().toISOString(),
          ...values
        };
        const newDiagnoses = [...diagnoses, newDiagnosis];
        onDiagnosisChange(newDiagnoses);
        message.success('诊断添加成功');
      }

      setModalVisible(false);
      form.resetFields();
      setEditingDiagnosis(null);
    } catch (error) {
      console.error('保存诊断失败:', error);
      message.error('保存诊断失败');
    } finally {
      setLoading(false);
    }
  };

  // 应用常用诊断
  const handleApplyCommonDiagnosis = (diagnosis: { code: string; name: string }) => {
    form.setFieldsValue({
      diagnosisCode: diagnosis.code,
      diagnosisName: diagnosis.name
    });
  };

  // 表格列定义
  const columns: ColumnsType<Diagnosis> = [
    {
      title: '排序',
      dataIndex: 'sortOrder',
      key: 'sortOrder',
      width: 60,
      sorter: (a, b) => a.sortOrder - b.sortOrder
    },
    {
      title: '诊断编码',
      dataIndex: 'diagnosisCode',
      key: 'diagnosisCode',
      width: 100,
      render: (code: string) => code || '-'
    },
    {
      title: '诊断名称',
      dataIndex: 'diagnosisName',
      key: 'diagnosisName',
      width: 200,
      ellipsis: true
    },
    {
      title: '诊断类型',
      dataIndex: 'diagnosisType',
      key: 'diagnosisType',
      width: 100,
      render: (type: DiagnosisType) => {
        const config = DIAGNOSIS_TYPE_CONFIG[type];
        return (
          <Tag color={config.color}>{type}</Tag>
        );
      }
    },
    {
      title: '严重程度',
      dataIndex: 'severity',
      key: 'severity',
      width: 100,
      render: (severity: SeverityLevel) => {
        if (!severity) return '-';
        const config = SEVERITY_CONFIG[severity];
        return (
          <Tag color={config.color}>{severity}</Tag>
        );
      }
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: DiagnosisStatus) => {
        const config = DIAGNOSIS_STATUS_CONFIG[status];
        return (
          <Tag color={config.color}>{status}</Tag>
        );
      }
    },
    {
      title: '特殊标识',
      key: 'flags',
      width: 120,
      render: (_, record) => (
        <Space size="small">
          {record.isPrimary && <Tag color="red">主诊断</Tag>}
          {record.isChronicDisease && <Tag color="orange">慢性病</Tag>}
          {record.isInfectious && <Tag color="red">传染病</Tag>}
          {record.isHereditary && <Tag color="purple">遗传病</Tag>}
        </Space>
      )
    },
    {
      title: '诊断时间',
      dataIndex: 'diagnosisTime',
      key: 'diagnosisTime',
      width: 120,
      render: (time: string) => dayjs(time).format('MM-DD HH:mm')
    },
    {
      title: '操作',
      key: 'actions',
      width: 150,
      render: (_, record) => (
        <Space size="small">
          {!readonly && (
            <>
              <Button
                type="link"
                size="small"
                icon={<EditOutlined />}
                onClick={() => handleEditDiagnosis(record)}
              >
                编辑
              </Button>
              {record.status === '初步诊断' && (
                <Button
                  type="link"
                  size="small"
                  icon={<CheckCircleOutlined />}
                  onClick={() => handleConfirmDiagnosis(record)}
                >
                  确认
                </Button>
              )}
              <Button
                type="link"
                size="small"
                danger
                icon={<DeleteOutlined />}
                onClick={() => handleDeleteDiagnosis(record)}
              >
                删除
              </Button>
            </>
          )}
        </Space>
      )
    }
  ];

  return (
    <div>
      <Card
        title="诊断信息"
        extra={
          !readonly && (
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={handleAddDiagnosis}
            >
              添加诊断
            </Button>
          )
        }
      >
        {diagnoses.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>
            暂无诊断信息
          </div>
        ) : (
          <Table
            columns={columns}
            dataSource={diagnoses}
            rowKey="id"
            pagination={false}
            size="small"
          />
        )}
      </Card>

      {/* 诊断编辑模态框 */}
      <Modal
        title={isEditMode ? '编辑诊断' : '添加诊断'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
          setEditingDiagnosis(null);
        }}
        footer={null}
        width={800}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSaveDiagnosis}
        >
          {/* 常用诊断快捷选择 */}
          <Alert
            message="常用诊断"
            description={
              <div style={{ marginTop: 8 }}>
                <Space wrap>
                  {COMMON_DIAGNOSES.map(diagnosis => (
                    <Button
                      key={diagnosis.code}
                      size="small"
                      onClick={() => handleApplyCommonDiagnosis(diagnosis)}
                    >
                      {diagnosis.name}
                    </Button>
                  ))}
                </Space>
              </div>
            }
            type="info"
            style={{ marginBottom: 16 }}
          />

          <div style={{ display: 'flex', gap: '16px' }}>
            <div style={{ flex: 1 }}>
              <Form.Item
                label="诊断编码 (ICD-10)"
                name="diagnosisCode"
              >
                <Input placeholder="如：J00" />
              </Form.Item>

              <Form.Item
                label="诊断名称"
                name="diagnosisName"
                rules={[{ required: true, message: '请输入诊断名称' }]}
              >
                <AutoComplete
                  options={icdSuggestions.map(item => ({
                    value: item.name,
                    label: `${item.code} ${item.name}`
                  }))}
                  onSearch={handleICDSearch}
                  placeholder="输入诊断名称搜索..."
                  onSelect={(value, option) => {
                    const selectedItem = icdSuggestions.find(item => item.name === value);
                    if (selectedItem) {
                      form.setFieldsValue({
                        diagnosisCode: selectedItem.code,
                        diagnosisName: selectedItem.name
                      });
                    }
                  }}
                />
              </Form.Item>

              <Form.Item
                label="诊断描述"
                name="description"
              >
                <TextArea
                  rows={3}
                  placeholder="详细描述诊断情况..."
                  maxLength={1000}
                  showCount
                />
              </Form.Item>

              <Form.Item
                label="诊断依据"
                name="evidence"
              >
                <TextArea
                  rows={2}
                  placeholder="诊断的依据和理由..."
                  maxLength={1000}
                  showCount
                />
              </Form.Item>
            </div>

            <div style={{ width: '300px' }}>
              <Form.Item
                label="诊断类型"
                name="diagnosisType"
                rules={[{ required: true, message: '请选择诊断类型' }]}
              >
                <Select>
                  <Option value="主要诊断">主要诊断</Option>
                  <Option value="次要诊断">次要诊断</Option>
                  <Option value="疑似诊断">疑似诊断</Option>
                  <Option value="排除诊断">排除诊断</Option>
                  <Option value="并发症">并发症</Option>
                  <Option value="合并症">合并症</Option>
                </Select>
              </Form.Item>

              <Form.Item
                label="严重程度"
                name="severity"
              >
                <Select allowClear>
                  <Option value="轻度">轻度</Option>
                  <Option value="中度">中度</Option>
                  <Option value="重度">重度</Option>
                  <Option value="危重">危重</Option>
                </Select>
              </Form.Item>

              <Form.Item
                label="诊断状态"
                name="status"
                rules={[{ required: true, message: '请选择诊断状态' }]}
              >
                <Select>
                  <Option value="初步诊断">初步诊断</Option>
                  <Option value="确定诊断">确定诊断</Option>
                  <Option value="修正诊断">修正诊断</Option>
                  <Option value="排除诊断">排除诊断</Option>
                </Select>
              </Form.Item>

              <Form.Item
                label="排序序号"
                name="sortOrder"
                rules={[{ required: true, message: '请输入排序序号' }]}
              >
                <Input type="number" min={1} />
              </Form.Item>

              <Divider />

              <Form.Item
                label="主诊断"
                name="isPrimary"
                valuePropName="checked"
              >
                <Switch />
              </Form.Item>

              <Form.Item
                label="慢性病"
                name="isChronicDisease"
                valuePropName="checked"
              >
                <Switch />
              </Form.Item>

              <Form.Item
                label="传染病"
                name="isInfectious"
                valuePropName="checked"
              >
                <Switch />
              </Form.Item>

              <Form.Item
                label="遗传病"
                name="isHereditary"
                valuePropName="checked"
              >
                <Switch />
              </Form.Item>
            </div>
          </div>

          <Form.Item
            label="治疗建议"
            name="treatmentAdvice"
          >
            <TextArea
              rows={2}
              placeholder="针对此诊断的治疗建议..."
              maxLength={1000}
              showCount
            />
          </Form.Item>

          <Form.Item
            label="随访要求"
            name="followUpRequirement"
          >
            <TextArea
              rows={2}
              placeholder="随访的时间和要求..."
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Form.Item
            label="备注"
            name="remarks"
          >
            <TextArea
              rows={2}
              placeholder="其他需要说明的信息..."
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" loading={loading}>
                保存
              </Button>
              <Button onClick={() => {
                setModalVisible(false);
                form.resetFields();
                setEditingDiagnosis(null);
              }}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default DiagnosisManagement;