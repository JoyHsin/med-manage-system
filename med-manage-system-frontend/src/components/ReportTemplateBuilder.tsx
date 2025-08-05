import React, { useState } from 'react';
import {
  Modal,
  Form,
  Input,
  Select,
  Button,
  Space,
  Card,
  Row,
  Col,
  Checkbox,
  InputNumber,
  Divider,
  Typography,
  Alert,
  List,
  Tag,
  Tooltip
} from 'antd';
import {
  PlusOutlined,
  DeleteOutlined,
  EditOutlined,
  SaveOutlined,
  EyeOutlined
} from '@ant-design/icons';
import { ReportField, ReportTemplate } from './ReportGenerator';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { Option } = Select;

interface ReportTemplateBuilderProps {
  visible: boolean;
  onCancel: () => void;
  onSave: (template: Omit<ReportTemplate, 'id' | 'createdAt' | 'updatedAt'>) => void;
  initialTemplate?: ReportTemplate;
}

const ReportTemplateBuilder: React.FC<ReportTemplateBuilderProps> = ({
  visible,
  onCancel,
  onSave,
  initialTemplate
}) => {
  const [form] = Form.useForm();
  const [fields, setFields] = useState<ReportField[]>(initialTemplate?.fields || []);
  const [fieldModalVisible, setFieldModalVisible] = useState(false);
  const [editingField, setEditingField] = useState<ReportField | null>(null);
  const [editingIndex, setEditingIndex] = useState<number>(-1);
  const [fieldForm] = Form.useForm();

  // 字段类型选项
  const fieldTypeOptions = [
    { label: '文本', value: 'string' },
    { label: '数字', value: 'number' },
    { label: '日期', value: 'date' },
    { label: '布尔值', value: 'boolean' },
    { label: '选择器', value: 'select' }
  ];

  // 预定义的字段选项
  const predefinedOptions = {
    department: [
      { label: '内科', value: 'internal' },
      { label: '外科', value: 'surgery' },
      { label: '儿科', value: 'pediatrics' },
      { label: '妇科', value: 'gynecology' }
    ],
    paymentMethod: [
      { label: '现金', value: 'cash' },
      { label: '银行卡', value: 'card' },
      { label: '医保', value: 'insurance' },
      { label: '支付宝', value: 'alipay' },
      { label: '微信', value: 'wechat' }
    ],
    status: [
      { label: '待处理', value: 'pending' },
      { label: '进行中', value: 'processing' },
      { label: '已完成', value: 'completed' },
      { label: '已取消', value: 'cancelled' }
    ]
  };

  // 添加字段
  const handleAddField = () => {
    setEditingField(null);
    setEditingIndex(-1);
    fieldForm.resetFields();
    setFieldModalVisible(true);
  };

  // 编辑字段
  const handleEditField = (field: ReportField, index: number) => {
    setEditingField(field);
    setEditingIndex(index);
    fieldForm.setFieldsValue(field);
    setFieldModalVisible(true);
  };

  // 删除字段
  const handleDeleteField = (index: number) => {
    const newFields = fields.filter((_, i) => i !== index);
    setFields(newFields);
  };

  // 保存字段
  const handleSaveField = async () => {
    try {
      const values = await fieldForm.validateFields();
      const newField: ReportField = {
        key: values.key,
        label: values.label,
        type: values.type,
        required: values.required || false,
        options: values.options || undefined,
        defaultValue: values.defaultValue
      };

      if (editingIndex >= 0) {
        // 编辑现有字段
        const newFields = [...fields];
        newFields[editingIndex] = newField;
        setFields(newFields);
      } else {
        // 添加新字段
        setFields([...fields, newField]);
      }

      setFieldModalVisible(false);
      fieldForm.resetFields();
    } catch (error) {
      console.error('保存字段失败:', error);
    }
  };

  // 保存模板
  const handleSaveTemplate = async () => {
    try {
      const values = await form.validateFields();
      
      if (fields.length === 0) {
        Modal.warning({
          title: '提示',
          content: '请至少添加一个字段'
        });
        return;
      }

      const template: Omit<ReportTemplate, 'id' | 'createdAt' | 'updatedAt'> = {
        name: values.name,
        description: values.description,
        category: values.category || '自定义报表',
        fields: fields,
        defaultParams: {
          format: 'excel',
          includeCharts: values.includeCharts !== false
        },
        isCustom: true
      };

      onSave(template);
    } catch (error) {
      console.error('保存模板失败:', error);
    }
  };

  // 预览模板
  const handlePreviewTemplate = () => {
    Modal.info({
      title: '模板预览',
      width: 600,
      content: (
        <div>
          <div style={{ marginBottom: '16px' }}>
            <Text strong>字段列表:</Text>
          </div>
          <List
            size="small"
            dataSource={fields}
            renderItem={(field) => (
              <List.Item>
                <Space>
                  <Tag color="blue">{field.type}</Tag>
                  <Text strong>{field.label}</Text>
                  <Text type="secondary">({field.key})</Text>
                  {field.required && <Tag color="red">必填</Tag>}
                </Space>
              </List.Item>
            )}
          />
        </div>
      )
    });
  };

  return (
    <>
      <Modal
        title={initialTemplate ? '编辑报表模板' : '创建报表模板'}
        open={visible}
        onCancel={onCancel}
        width={800}
        footer={[
          <Button key="cancel" onClick={onCancel}>
            取消
          </Button>,
          <Button key="preview" onClick={handlePreviewTemplate}>
            <EyeOutlined /> 预览
          </Button>,
          <Button key="save" type="primary" onClick={handleSaveTemplate}>
            <SaveOutlined /> 保存模板
          </Button>
        ]}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={initialTemplate}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="模板名称"
                name="name"
                rules={[{ required: true, message: '请输入模板名称' }]}
              >
                <Input placeholder="输入模板名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="模板分类"
                name="category"
              >
                <Select placeholder="选择或输入分类">
                  <Option value="财务报表">财务报表</Option>
                  <Option value="患者分析">患者分析</Option>
                  <Option value="运营分析">运营分析</Option>
                  <Option value="库存管理">库存管理</Option>
                  <Option value="自定义报表">自定义报表</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="模板描述"
            name="description"
            rules={[{ required: true, message: '请输入模板描述' }]}
          >
            <TextArea rows={3} placeholder="输入模板描述" />
          </Form.Item>

          <Form.Item
            label="包含图表"
            name="includeCharts"
            valuePropName="checked"
            initialValue={true}
          >
            <Checkbox>默认生成图表</Checkbox>
          </Form.Item>
        </Form>

        <Divider orientation="left">报表字段配置</Divider>

        <div style={{ marginBottom: '16px' }}>
          <Button
            type="dashed"
            onClick={handleAddField}
            icon={<PlusOutlined />}
            block
          >
            添加字段
          </Button>
        </div>

        {fields.length > 0 ? (
          <List
            dataSource={fields}
            renderItem={(field, index) => (
              <List.Item
                actions={[
                  <Tooltip title="编辑">
                    <Button
                      type="text"
                      icon={<EditOutlined />}
                      onClick={() => handleEditField(field, index)}
                    />
                  </Tooltip>,
                  <Tooltip title="删除">
                    <Button
                      type="text"
                      danger
                      icon={<DeleteOutlined />}
                      onClick={() => handleDeleteField(index)}
                    />
                  </Tooltip>
                ]}
              >
                <List.Item.Meta
                  title={
                    <Space>
                      <Tag color="blue">{field.type}</Tag>
                      {field.label}
                      {field.required && <Tag color="red" size="small">必填</Tag>}
                    </Space>
                  }
                  description={
                    <Space direction="vertical" size={4}>
                      <Text type="secondary">字段键: {field.key}</Text>
                      {field.options && (
                        <Text type="secondary">
                          选项: {field.options.map(opt => opt.label).join(', ')}
                        </Text>
                      )}
                      {field.defaultValue && (
                        <Text type="secondary">默认值: {String(field.defaultValue)}</Text>
                      )}
                    </Space>
                  }
                />
              </List.Item>
            )}
          />
        ) : (
          <Alert
            message="暂无字段"
            description="请添加至少一个字段来定义报表参数"
            type="info"
            showIcon
          />
        )}
      </Modal>

      {/* 字段编辑弹窗 */}
      <Modal
        title={editingField ? '编辑字段' : '添加字段'}
        open={fieldModalVisible}
        onCancel={() => setFieldModalVisible(false)}
        onOk={handleSaveField}
        width={600}
      >
        <Form
          form={fieldForm}
          layout="vertical"
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="字段键"
                name="key"
                rules={[
                  { required: true, message: '请输入字段键' },
                  { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '字段键必须以字母开头，只能包含字母、数字和下划线' }
                ]}
              >
                <Input placeholder="例如: startDate" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="字段标签"
                name="label"
                rules={[{ required: true, message: '请输入字段标签' }]}
              >
                <Input placeholder="例如: 开始日期" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="字段类型"
                name="type"
                rules={[{ required: true, message: '请选择字段类型' }]}
              >
                <Select placeholder="选择字段类型">
                  {fieldTypeOptions.map(option => (
                    <Option key={option.value} value={option.value}>
                      {option.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="是否必填"
                name="required"
                valuePropName="checked"
              >
                <Checkbox>必填字段</Checkbox>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="默认值"
            name="defaultValue"
          >
            <Input placeholder="输入默认值（可选）" />
          </Form.Item>

          <Form.Item
            noStyle
            shouldUpdate={(prevValues, currentValues) => 
              prevValues.type !== currentValues.type
            }
          >
            {({ getFieldValue }) => {
              const fieldType = getFieldValue('type');
              if (fieldType === 'select') {
                return (
                  <Form.Item
                    label="选项配置"
                    name="options"
                    rules={[{ required: true, message: '请配置选项' }]}
                  >
                    <Select
                      mode="tags"
                      placeholder="输入选项，按回车添加"
                      style={{ width: '100%' }}
                      tokenSeparators={[',']}
                    />
                  </Form.Item>
                );
              }
              return null;
            }}
          </Form.Item>
        </Form>

        <Alert
          message="字段配置说明"
          description={
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li>字段键用于程序识别，必须唯一且符合命名规范</li>
              <li>字段标签是用户看到的显示名称</li>
              <li>选择器类型需要配置可选项</li>
              <li>日期类型会自动显示日期选择器</li>
            </ul>
          }
          type="info"
          showIcon
          style={{ marginTop: '16px' }}
        />
      </Modal>
    </>
  );
};

export default ReportTemplateBuilder;