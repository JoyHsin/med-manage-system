import React, { useState } from 'react';
import { Table, Button, Input, Space, Modal, Form, message, Card, Row, Col } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';

const { Search } = Input;

interface SimplePatient {
  id: number;
  patientNumber: string;
  name: string;
  phone: string;
  gender: string;
  age: number;
  status: string;
}

const SimplePatientManagement: React.FC = () => {
  const [patients, setPatients] = useState<SimplePatient[]>([
    { id: 1, patientNumber: 'P001', name: '张三', phone: '13800138001', gender: '男', age: 35, status: '正常' },
    { id: 2, patientNumber: 'P002', name: '李四', phone: '13800138002', gender: '女', age: 28, status: '正常' },
    { id: 3, patientNumber: 'P003', name: '王五', phone: '13800138003', gender: '男', age: 42, status: '正常' },
  ]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingPatient, setEditingPatient] = useState<SimplePatient | null>(null);
  const [form] = Form.useForm();

  const handleCreatePatient = () => {
    setEditingPatient(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEditPatient = (patient: SimplePatient) => {
    setEditingPatient(patient);
    form.setFieldsValue(patient);
    setIsModalVisible(true);
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingPatient) {
        // 更新患者
        setPatients(prev => prev.map(p => 
          p.id === editingPatient.id ? { ...p, ...values } : p
        ));
        message.success('患者信息更新成功');
      } else {
        // 创建患者
        const newPatient: SimplePatient = {
          id: Date.now(),
          patientNumber: `P${String(Date.now()).slice(-3)}`,
          ...values,
        };
        setPatients(prev => [...prev, newPatient]);
        message.success('患者创建成功');
      }
      
      setIsModalVisible(false);
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const handleDeletePatient = (patientId: number) => {
    setPatients(prev => prev.filter(p => p.id !== patientId));
    message.success('患者删除成功');
  };

  const columns: ColumnsType<SimplePatient> = [
    {
      title: '患者编号',
      dataIndex: 'patientNumber',
      key: 'patientNumber',
    },
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '性别',
      dataIndex: 'gender',
      key: 'gender',
    },
    {
      title: '年龄',
      dataIndex: 'age',
      key: 'age',
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
    },
    {
      title: '操作',
      key: 'actions',
      render: (_, record) => (
        <Space size="middle">
          <Button icon={<EditOutlined />} onClick={() => handleEditPatient(record)} size="small" />
          <Button 
            icon={<DeleteOutlined />} 
            onClick={() => handleDeletePatient(record.id)} 
            size="small" 
            danger 
          />
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card title="患者管理 (简化版)" extra={
        <Space>
          <Search placeholder="搜索患者" style={{ width: 200 }} />
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreatePatient}>
            新增患者
          </Button>
        </Space>
      }>
        <Table
          columns={columns}
          dataSource={patients}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingPatient ? '编辑患者' : '新增患者'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="name"
                label="患者姓名"
                rules={[{ required: true, message: '请输入患者姓名' }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="gender"
                label="性别"
                rules={[{ required: true, message: '请选择性别' }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="age"
                label="年龄"
                rules={[{ required: true, message: '请输入年龄' }]}
              >
                <Input type="number" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="phone"
                label="手机号"
                rules={[{ required: true, message: '请输入手机号' }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  );
};

export default SimplePatientManagement;