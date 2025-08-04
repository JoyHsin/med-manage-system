import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Input,
  Space,
  Modal,
  Form,
  message,
  Popconfirm,
  Select,
  Tag,
  Card,
  Row,
  Col,
  Tooltip,
  DatePicker
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { staffService } from '../services/staffService';
import type { Staff, CreateStaffRequest, UpdateStaffRequest } from '../types/staff';

const { Search } = Input;
const { Option } = Select;

const StaffManagement: React.FC = () => {
  const [staff, setStaff] = useState<Staff[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingStaff, setEditingStaff] = useState<Staff | null>(null);
  const [form] = Form.useForm();
  const [searchKeyword, setSearchKeyword] = useState('');

  useEffect(() => {
    fetchStaff();
  }, []);

  const fetchStaff = async () => {
    setLoading(true);
    try {
      const response = await staffService.getAllStaff();
      setStaff(response.data || []);
    } catch (error) {
      message.error('获取员工列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateStaff = () => {
    setEditingStaff(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEditStaff = (staffMember: Staff) => {
    setEditingStaff(staffMember);
    form.setFieldsValue({
      name: staffMember.name,
      gender: staffMember.gender,
      department: staffMember.department,
      position: staffMember.position,
      phone: staffMember.phone,
      email: staffMember.email,
      hireDate: staffMember.hireDate ? new Date(staffMember.hireDate) : null,
    });
    setIsModalVisible(true);
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingStaff) {
        // 更新员工
        const updateRequest: UpdateStaffRequest = {
          name: values.name,
          gender: values.gender,
          hireDate: values.hireDate?.format('YYYY-MM-DD'),
          position: values.position,
          department: values.department,
          phone: values.phone,
          email: values.email,
        };
        
        await staffService.updateStaff(editingStaff.id, updateRequest);
        message.success('员工信息更新成功');
        fetchStaff();
        setIsModalVisible(false);
      } else {
        // 创建员工
        const createRequest: CreateStaffRequest = {
          name: values.name,
          gender: values.gender,
          hireDate: values.hireDate?.format('YYYY-MM-DD'),
          position: values.position,
          department: values.department,
          phone: values.phone,
          email: values.email,
        };
        
        await staffService.createStaff(createRequest);
        message.success('员工创建成功');
        fetchStaff();
        setIsModalVisible(false);
      }
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const handleDeleteStaff = async (staffId: number) => {
    try {
      await staffService.deleteStaff(staffId);
      message.success('员工删除成功');
      fetchStaff();
    } catch (error) {
      message.error('员工删除失败');
    }
  };

  const columns: ColumnsType<Staff> = [
    {
      title: '员工姓名',
      dataIndex: 'name',
      key: 'name',
      sorter: (a, b) => a.name.localeCompare(b.name),
    },
    {
      title: '员工工号',
      dataIndex: 'staffNumber',
      key: 'staffNumber',
      sorter: (a, b) => a.staffNumber.localeCompare(b.staffNumber),
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
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
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
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (text: string) => new Date(text).toLocaleString(),
      sorter: (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
    },
    {
      title: '操作',
      key: 'actions',
      render: (_, record) => (
        <Space size="middle">
          <Tooltip title="编辑">
            <Button icon={<EditOutlined />} onClick={() => handleEditStaff(record)} />
          </Tooltip>
          <Popconfirm
            title="确定删除此员工吗？"
            onConfirm={() => handleDeleteStaff(record.id)}
            okText="是"
            cancelText="否"
          >
            <Tooltip title="删除">
              <Button icon={<DeleteOutlined />} danger />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card title="医护人员管理" extra={
      <Space>
        <Search
          placeholder="搜索员工姓名或工号"
          onSearch={setSearchKeyword}
          style={{ width: 200 }}
          allowClear
        />
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreateStaff}>
          新增员工
        </Button>
        <Button icon={<ReloadOutlined />} onClick={fetchStaff}>
          刷新
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

      <Modal
        title={editingStaff ? '编辑员工' : '新增员工'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        destroyOnClose
        width={600}
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="name"
                label="员工姓名"
                rules={[
                  { required: true, message: '请输入员工姓名!' },
                  { max: 100, message: '员工姓名长度不能超过100个字符' }
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="gender"
                label="性别"
                rules={[{ required: true, message: '请选择性别!' }]}
              >
                <Select placeholder="请选择性别">
                  <Option value="男">男</Option>
                  <Option value="女">女</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="department"
                label="部门"
                rules={[{ required: true, message: '请输入部门!' }]}
              >
                <Select placeholder="请选择部门">
                  <Option value="内科">内科</Option>
                  <Option value="外科">外科</Option>
                  <Option value="儿科">儿科</Option>
                  <Option value="妇产科">妇产科</Option>
                  <Option value="急诊科">急诊科</Option>
                  <Option value="药房">药房</Option>
                  <Option value="护理部">护理部</Option>
                  <Option value="检验科">检验科</Option>
                  <Option value="放射科">放射科</Option>
                  <Option value="行政部">行政部</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="position"
                label="职位"
                rules={[{ required: true, message: '请输入职位!' }]}
              >
                <Select placeholder="请选择职位">
                  <Option value="医生">医生</Option>
                  <Option value="护士">护士</Option>
                  <Option value="药剂师">药剂师</Option>
                  <Option value="前台">前台</Option>
                  <Option value="管理员">管理员</Option>
                  <Option value="其他">其他</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="hireDate"
                label="入职日期"
                rules={[{ required: true, message: '请选择入职日期!' }]}
              >
                <DatePicker style={{ width: '100%' }} placeholder="请选择入职日期" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="phone"
                label="电话"
                rules={[
                  { pattern: /^1[3-9]\d{9}$/, message: '手机号码格式不正确' }
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="email"
                label="邮箱"
                rules={[
                  { type: 'email', message: '邮箱格式不正确' }
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              {/* 预留空位 */}
            </Col>
          </Row>
        </Form>
      </Modal>
    </Card>
  );
};

export default StaffManagement;