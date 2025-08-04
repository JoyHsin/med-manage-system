import React, { useState } from 'react';
import { Table, Button, Input, Space, Modal, Form, message, Card, Row, Col, Tag } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';

const { Search } = Input;

interface SimpleStaff {
  id: number;
  staffNumber: string;
  name: string;
  gender: string;
  department: string;
  position: string;
  phone: string;
  workStatus: string;
}

const SimpleStaffManagement: React.FC = () => {
  const [staff, setStaff] = useState<SimpleStaff[]>([
    { 
      id: 1, 
      staffNumber: 'S001', 
      name: '医生张三', 
      gender: '男', 
      department: '内科', 
      position: '医生', 
      phone: '13800138001', 
      workStatus: '在职' 
    },
    { 
      id: 2, 
      staffNumber: 'S002', 
      name: '护士李四', 
      gender: '女', 
      department: '护理部', 
      position: '护士', 
      phone: '13800138002', 
      workStatus: '在职' 
    },
    { 
      id: 3, 
      staffNumber: 'S003', 
      name: '药师王五', 
      gender: '男', 
      department: '药房', 
      position: '药剂师', 
      phone: '13800138003', 
      workStatus: '在职' 
    },
  ]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingStaff, setEditingStaff] = useState<SimpleStaff | null>(null);
  const [form] = Form.useForm();

  const handleCreateStaff = () => {
    setEditingStaff(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEditStaff = (staffMember: SimpleStaff) => {
    setEditingStaff(staffMember);
    form.setFieldsValue(staffMember);
    setIsModalVisible(true);
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingStaff) {
        // 更新员工
        setStaff(prev => prev.map(s => 
          s.id === editingStaff.id ? { ...s, ...values } : s
        ));
        message.success('员工信息更新成功');
      } else {
        // 创建员工
        const newStaff: SimpleStaff = {
          id: Date.now(),
          staffNumber: `S${String(Date.now()).slice(-3)}`,
          workStatus: '在职',
          ...values,
        };
        setStaff(prev => [...prev, newStaff]);
        message.success('员工创建成功');
      }
      
      setIsModalVisible(false);
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const handleDeleteStaff = (staffId: number) => {
    setStaff(prev => prev.filter(s => s.id !== staffId));
    message.success('员工删除成功');
  };

  const columns: ColumnsType<SimpleStaff> = [
    {
      title: '员工姓名',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '员工工号',
      dataIndex: 'staffNumber',
      key: 'staffNumber',
    },
    {
      title: '部门',
      dataIndex: 'department',
      key: 'department',
    },
    {
      title: '职位',
      dataIndex: 'position',
      key: 'position',
    },
    {
      title: '电话',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '工作状态',
      dataIndex: 'workStatus',
      key: 'workStatus',
      render: (workStatus: string) => {
        const colorMap: { [key: string]: string } = {
          '在职': 'green',
          '离职': 'red',
          '休假': 'orange',
          '停职': 'gray',
        };
        return (
          <Tag color={colorMap[workStatus] || 'default'}>
            {workStatus}
          </Tag>
        );
      },
    },
    {
      title: '操作',
      key: 'actions',
      render: (_, record) => (
        <Space size="middle">
          <Button icon={<EditOutlined />} onClick={() => handleEditStaff(record)} size="small" />
          <Button 
            icon={<DeleteOutlined />} 
            onClick={() => handleDeleteStaff(record.id)} 
            size="small" 
            danger 
          />
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card title="医护人员管理 (简化版)" extra={
        <Space>
          <Search placeholder="搜索员工" style={{ width: 200 }} />
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreateStaff}>
            新增员工
          </Button>
        </Space>
      }>
        <Table
          columns={columns}
          dataSource={staff}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingStaff ? '编辑员工' : '新增员工'}
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
                label="员工姓名"
                rules={[{ required: true, message: '请输入员工姓名' }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="gender"
                label="性别"
                rules={[{ required: true, message: '请输入性别' }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="department"
                label="部门"
                rules={[{ required: true, message: '请输入部门' }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="position"
                label="职位"
                rules={[{ required: true, message: '请输入职位' }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            name="phone"
            label="电话"
            rules={[{ required: true, message: '请输入电话' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default SimpleStaffManagement;