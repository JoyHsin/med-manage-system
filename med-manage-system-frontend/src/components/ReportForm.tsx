import React from 'react';
import {
  Form,
  Select,
  DatePicker,
  Input,
  Checkbox,
  Button,
  Space,
  Card,
  Typography
} from 'antd';
import { FileExcelOutlined, FilePdfOutlined, SettingOutlined } from '@ant-design/icons';
import type { ReportTemplate, ReportParams } from './ReportGenerator';
import dayjs from 'dayjs';

const { Title } = Typography;
const { RangePicker } = DatePicker;
const { Option } = Select;

interface ReportFormProps {
  templates: ReportTemplate[];
  selectedTemplate: ReportTemplate | null;
  loading: boolean;
  onTemplateChange: (templateId: string) => void;
  onGenerate: (params: any) => Promise<void>;
  onOpenTemplateBuilder: () => void;
}

const ReportForm: React.FC<ReportFormProps> = ({
  templates,
  selectedTemplate,
  loading,
  onTemplateChange,
  onGenerate,
  onOpenTemplateBuilder
}) => {
  const [form] = Form.useForm();

  const handleGenerate = async () => {
    try {
      const values = await form.validateFields();
      await onGenerate(values);
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const renderFormField = (field: any) => {
    switch (field.type) {
      case 'date':
        if (field.label.includes('范围')) {
          return (
            <RangePicker
              format="YYYY-MM-DD"
              placeholder={['开始日期', '结束日期']}
            />
          );
        }
        return (
          <DatePicker
            format={field.label.includes('月') ? 'YYYY-MM' : 'YYYY-MM-DD'}
            picker={field.label.includes('月') ? 'month' : 'date'}
            placeholder={`请选择${field.label}`}
          />
        );
      case 'select':
        return (
          <Select placeholder={`请选择${field.label}`}>
            {field.options?.map((option: any) => (
              <Option key={option.value} value={option.value}>
                {option.label}
              </Option>
            ))}
          </Select>
        );
      case 'boolean':
        return <Checkbox>{field.label}</Checkbox>;
      case 'number':
        return (
          <Input
            type="number"
            placeholder={`请输入${field.label}`}
          />
        );
      default:
        return (
          <Input placeholder={`请输入${field.label}`} />
        );
    }
  };

  return (
    <Card
      title={
        <Space>
          <SettingOutlined />
          <Title level={4} style={{ margin: 0 }}>报表生成器</Title>
        </Space>
      }
      extra={
        <Button 
          type="link" 
          onClick={onOpenTemplateBuilder}
        >
          自定义模板
        </Button>
      }
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleGenerate}
      >
        <Form.Item
          label="报表模板"
          name="templateId"
          rules={[{ required: true, message: '请选择报表模板' }]}
        >
          <Select
            placeholder="请选择报表模板"
            onChange={onTemplateChange}
          >
            {templates.map(template => (
              <Option key={template.id} value={template.id}>
                <div>
                  <div style={{ fontWeight: 'bold' }}>{template.name}</div>
                  <div style={{ fontSize: '12px', color: '#666' }}>
                    {template.description}
                  </div>
                </div>
              </Option>
            ))}
          </Select>
        </Form.Item>

        {selectedTemplate?.fields.map(field => (
          <Form.Item
            key={field.key}
            label={field.label}
            name={field.key}
            rules={[
              { required: field.required, message: `请填写${field.label}` }
            ]}
            valuePropName={field.type === 'boolean' ? 'checked' : 'value'}
            initialValue={field.defaultValue}
          >
            {renderFormField(field)}
          </Form.Item>
        ))}

        <Form.Item label="导出格式" name="format" initialValue="excel">
          <Select>
            <Option value="excel">
              <Space>
                <FileExcelOutlined style={{ color: '#52c41a' }} />
                Excel格式
              </Space>
            </Option>
            <Option value="pdf">
              <Space>
                <FilePdfOutlined style={{ color: '#ff4d4f' }} />
                PDF格式
              </Space>
            </Option>
          </Select>
        </Form.Item>

        <Form.Item name="includeCharts" valuePropName="checked" initialValue={true}>
          <Checkbox>包含图表</Checkbox>
        </Form.Item>

        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
            disabled={!selectedTemplate}
            block
          >
            生成报表
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default ReportForm;