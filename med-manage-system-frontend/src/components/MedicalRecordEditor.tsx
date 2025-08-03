import React, { useState, useEffect } from 'react';
import {
  Card,
  Form,
  Input,
  Select,
  Button,
  Space,
  Row,
  Col,
  Typography,
  Divider,
  Alert,
  message,
  Modal,
  Tabs,
  Switch,
  AutoComplete
} from 'antd';
import {
  SaveOutlined,
  SendOutlined,
  EyeOutlined,
  FileTextOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  BulbOutlined
} from '@ant-design/icons';
import { medicalRecordService } from '../services/medicalRecordService';
import {
  MedicalRecord,
  CreateMedicalRecordRequest,
  UpdateMedicalRecordRequest,
  RecordType,
  MEDICAL_RECORD_TEMPLATE_FIELDS,
  COMMON_DIAGNOSES
} from '../types/medicalRecord';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { Option } = Select;
const { TabPane } = Tabs;

interface MedicalRecordEditorProps {
  patientId: number;
  patientName: string;
  recordId?: number;
  onSave?: (record: MedicalRecord) => void;
  onCancel?: () => void;
}

const MedicalRecordEditor: React.FC<MedicalRecordEditorProps> = ({
  patientId,
  patientName,
  recordId,
  onSave,
  onCancel
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [record, setRecord] = useState<MedicalRecord | null>(null);
  const [validationResult, setValidationResult] = useState<{
    isValid: boolean;
    errors: string[];
    warnings: string[];
  }>({ isValid: true, errors: [], warnings: [] });
  const [previewVisible, setPreviewVisible] = useState(false);
  const [templateModalVisible, setTemplateModalVisible] = useState(false);
  const [templates, setTemplates] = useState<Array<{
    id: string;
    name: string;
    template: Partial<MedicalRecord>;
  }>>([]);
  const [icdSuggestions, setIcdSuggestions] = useState<Array<{ code: string; name: string }>>([]);

  const isEditMode = !!recordId;

  // 加载病历数据（编辑模式）
  useEffect(() => {
    if (recordId) {
      loadMedicalRecord();
    } else {
      // 新建模式，生成病历编号
      const recordNumber = medicalRecordService.generateRecordNumber();
      form.setFieldsValue({
        recordNumber,
        patientId,
        recordType: '门诊病历',
        isInfectious: false,
        isChronicDisease: false
      });
    }
  }, [recordId, patientId, form]);

  // 加载模板
  useEffect(() => {
    loadTemplates();
  }, []);

  const loadMedicalRecord = async () => {
    if (!recordId) return;

    try {
      setLoading(true);
      const data = await medicalRecordService.getMedicalRecord(recordId);
      setRecord(data);
      form.setFieldsValue(data);
    } catch (error) {
      console.error('加载病历失败:', error);
      message.error('加载病历失败');
    } finally {
      setLoading(false);
    }
  };

  const loadTemplates = async () => {
    try {
      const data = await medicalRecordService.getMedicalRecordTemplates();
      setTemplates(data);
    } catch (error) {
      console.error('加载模板失败:', error);
    }
  };

  // 实时验证
  const handleFieldChange = () => {
    const values = form.getFieldsValue();
    const result = medicalRecordService.validateMedicalRecord(values);
    setValidationResult(result);
  };

  // 保存病历
  const handleSave = async (values: any) => {
    try {
      setSaving(true);
      let savedRecord: MedicalRecord;

      if (isEditMode && recordId) {
        const updateRequest: UpdateMedicalRecordRequest = {
          id: recordId,
          ...values
        };
        savedRecord = await medicalRecordService.updateMedicalRecord(recordId, updateRequest);
        message.success('病历保存成功');
      } else {
        const createRequest: CreateMedicalRecordRequest = {
          patientId,
          ...values
        };
        savedRecord = await medicalRecordService.createMedicalRecord(createRequest);
        message.success('病历创建成功');
      }

      setRecord(savedRecord);
      onSave?.(savedRecord);
    } catch (error) {
      console.error('保存病历失败:', error);
      message.error('保存病历失败');
    } finally {
      setSaving(false);
    }
  };

  // 提交审核
  const handleSubmitReview = async () => {
    if (!record?.id) {
      message.warning('请先保存病历');
      return;
    }

    const validation = medicalRecordService.validateMedicalRecord(form.getFieldsValue());
    if (!validation.isValid) {
      message.error('病历信息不完整，无法提交审核');
      return;
    }

    try {
      await medicalRecordService.submitForReview(record.id);
      message.success('病历已提交审核');
      loadMedicalRecord();
    } catch (error) {
      console.error('提交审核失败:', error);
      message.error('提交审核失败');
    }
  };

  // 应用模板
  const handleApplyTemplate = (template: Partial<MedicalRecord>) => {
    form.setFieldsValue(template);
    setTemplateModalVisible(false);
    message.success('模板应用成功');
    handleFieldChange();
  };

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

  // 预览病历
  const handlePreview = () => {
    setPreviewVisible(true);
  };

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <FileTextOutlined style={{ marginRight: 8 }} />
        {isEditMode ? '编辑病历' : '新建病历'} - {patientName}
      </Title>

      {/* 验证提示 */}
      {validationResult.errors.length > 0 && (
        <Alert
          message="必填信息缺失"
          description={
            <ul style={{ margin: 0, paddingLeft: 20 }}>
              {validationResult.errors.map((error, index) => (
                <li key={index}>{error}</li>
              ))}
            </ul>
          }
          type="error"
          style={{ marginBottom: 16 }}
        />
      )}

      {validationResult.warnings.length > 0 && (
        <Alert
          message="建议完善信息"
          description={
            <ul style={{ margin: 0, paddingLeft: 20 }}>
              {validationResult.warnings.map((warning, index) => (
                <li key={index}>{warning}</li>
              ))}
            </ul>
          }
          type="warning"
          style={{ marginBottom: 16 }}
        />
      )}

      {/* 操作栏 */}
      <Card style={{ marginBottom: 16 }}>
        <Space>
          <Button
            type="primary"
            icon={<SaveOutlined />}
            onClick={() => form.submit()}
            loading={saving}
          >
            保存病历
          </Button>
          {record && record.status === '草稿' && (
            <Button
              type="default"
              icon={<SendOutlined />}
              onClick={handleSubmitReview}
              disabled={!validationResult.isValid}
            >
              提交审核
            </Button>
          )}
          <Button
            icon={<EyeOutlined />}
            onClick={handlePreview}
          >
            预览
          </Button>
          <Button
            icon={<BulbOutlined />}
            onClick={() => setTemplateModalVisible(true)}
          >
            使用模板
          </Button>
          {onCancel && (
            <Button onClick={onCancel}>
              取消
            </Button>
          )}
        </Space>
      </Card>

      {/* 病历表单 */}
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSave}
        onValuesChange={handleFieldChange}
        loading={loading}
      >
        <Tabs defaultActiveKey="basic">
          <TabPane tab="基本信息" key="basic">
            <Row gutter={16}>
              <Col span={8}>
                <Form.Item
                  label="病历编号"
                  name="recordNumber"
                  rules={[{ required: true, message: '请输入病历编号' }]}
                >
                  <Input disabled />
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  label="病历类型"
                  name="recordType"
                  rules={[{ required: true, message: '请选择病历类型' }]}
                >
                  <Select>
                    <Option value="门诊病历">门诊病历</Option>
                    <Option value="急诊病历">急诊病历</Option>
                    <Option value="住院病历">住院病历</Option>
                    <Option value="体检报告">体检报告</Option>
                    <Option value="其他">其他</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  label="科室"
                  name="department"
                >
                  <Input placeholder="请输入科室" />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  label="传染病"
                  name="isInfectious"
                  valuePropName="checked"
                >
                  <Switch />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  label="慢性病"
                  name="isChronicDisease"
                  valuePropName="checked"
                >
                  <Switch />
                </Form.Item>
              </Col>
            </Row>
          </TabPane>

          <TabPane tab="病史信息" key="history">
            <Form.Item
              label="主诉"
              name="chiefComplaint"
              rules={[{ required: true, message: '请输入主诉' }]}
            >
              <TextArea
                rows={3}
                placeholder="患者主要症状和就诊原因..."
                maxLength={1000}
                showCount
              />
            </Form.Item>

            <Form.Item
              label="现病史"
              name="presentIllness"
              rules={[{ required: true, message: '请输入现病史' }]}
            >
              <TextArea
                rows={4}
                placeholder="详细描述患者本次疾病的发生、发展过程..."
                maxLength={2000}
                showCount
              />
            </Form.Item>

            <Form.Item
              label="既往史"
              name="pastHistory"
            >
              <TextArea
                rows={3}
                placeholder="患者既往疾病史、手术史、外伤史等..."
                maxLength={2000}
                showCount
              />
            </Form.Item>

            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  label="个人史"
                  name="personalHistory"
                >
                  <TextArea
                    rows={3}
                    placeholder="生活习惯、职业史、婚育史等..."
                    maxLength={1000}
                    showCount
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  label="家族史"
                  name="familyHistory"
                >
                  <TextArea
                    rows={3}
                    placeholder="家族遗传病史、传染病史等..."
                    maxLength={1000}
                    showCount
                  />
                </Form.Item>
              </Col>
            </Row>
          </TabPane>

          <TabPane tab="检查诊断" key="examination">
            <Form.Item
              label="体格检查"
              name="physicalExamination"
              rules={[{ required: true, message: '请输入体格检查结果' }]}
            >
              <TextArea
                rows={4}
                placeholder="体格检查结果，包括生命体征、各系统检查等..."
                maxLength={2000}
                showCount
              />
            </Form.Item>

            <Form.Item
              label="辅助检查"
              name="auxiliaryExamination"
            >
              <TextArea
                rows={3}
                placeholder="实验室检查、影像学检查等结果..."
                maxLength={2000}
                showCount
              />
            </Form.Item>

            <Form.Item
              label="初步诊断"
              name="preliminaryDiagnosis"
              rules={[{ required: true, message: '请输入初步诊断' }]}
            >
              <AutoComplete
                options={icdSuggestions.map(item => ({
                  value: `${item.code} ${item.name}`,
                  label: `${item.code} ${item.name}`
                }))}
                onSearch={handleICDSearch}
                placeholder="输入诊断名称或ICD编码搜索..."
              >
                <TextArea
                  rows={2}
                  maxLength={1000}
                  showCount
                />
              </AutoComplete>
            </Form.Item>

            <Form.Item
              label="最终诊断"
              name="finalDiagnosis"
            >
              <AutoComplete
                options={icdSuggestions.map(item => ({
                  value: `${item.code} ${item.name}`,
                  label: `${item.code} ${item.name}`
                }))}
                onSearch={handleICDSearch}
                placeholder="输入诊断名称或ICD编码搜索..."
              >
                <TextArea
                  rows={2}
                  maxLength={1000}
                  showCount
                />
              </AutoComplete>
            </Form.Item>
          </TabPane>

          <TabPane tab="治疗方案" key="treatment">
            <Form.Item
              label="治疗方案"
              name="treatmentPlan"
              rules={[{ required: true, message: '请输入治疗方案' }]}
            >
              <TextArea
                rows={4}
                placeholder="详细的治疗计划和方案..."
                maxLength={2000}
                showCount
              />
            </Form.Item>

            <Form.Item
              label="医嘱"
              name="medicalOrders"
            >
              <TextArea
                rows={3}
                placeholder="具体的医疗指令和用药方案..."
                maxLength={2000}
                showCount
              />
            </Form.Item>

            <Form.Item
              label="病情评估"
              name="conditionAssessment"
            >
              <TextArea
                rows={2}
                placeholder="对患者病情的评估..."
                maxLength={1000}
                showCount
              />
            </Form.Item>

            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  label="预后"
                  name="prognosis"
                >
                  <TextArea
                    rows={2}
                    placeholder="疾病预后评估..."
                    maxLength={500}
                    showCount
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  label="随访建议"
                  name="followUpAdvice"
                >
                  <TextArea
                    rows={2}
                    placeholder="随访时间和注意事项..."
                    maxLength={1000}
                    showCount
                  />
                </Form.Item>
              </Col>
            </Row>

            <Form.Item
              label="备注"
              name="remarks"
            >
              <TextArea
                rows={2}
                placeholder="其他需要说明的信息..."
                maxLength={1000}
                showCount
              />
            </Form.Item>
          </TabPane>
        </Tabs>
      </Form>

      {/* 模板选择模态框 */}
      <Modal
        title="选择病历模板"
        open={templateModalVisible}
        onCancel={() => setTemplateModalVisible(false)}
        footer={null}
        width={600}
      >
        <div>
          {templates.map(template => (
            <Card
              key={template.id}
              style={{ marginBottom: 16, cursor: 'pointer' }}
              hoverable
              onClick={() => handleApplyTemplate(template.template)}
            >
              <Title level={4}>{template.name}</Title>
              <Text type="secondary">
                点击应用此模板到当前病历
              </Text>
            </Card>
          ))}
        </div>
      </Modal>

      {/* 预览模态框 */}
      <Modal
        title="病历预览"
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        footer={null}
        width={800}
      >
        <div style={{ maxHeight: '600px', overflowY: 'auto' }}>
          {/* 预览内容将在这里显示 */}
          <div style={{ padding: '16px', backgroundColor: '#f5f5f5' }}>
            <Text>病历预览功能开发中...</Text>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default MedicalRecordEditor;