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
  DatePicker,
  Switch,
  Tag,
  Card,
  Row,
  Col,
  Tooltip
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
  KeyOutlined,
  UserAddOutlined,
  StopOutlined,
  CheckCircleOutlined
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { userService } from '../services/userService';
import type { User, CreateUserRequest, UpdateUserRequest } from '../types/auth';

const { Search } = Input;
const { Option } = Select;

const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [form] = Form.useForm();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [departmentFilter, setDepartmentFilter] = useState<string | undefined>();
  const [statusFilter, setStatusFilter] = useState<boolean | undefined>();

  useEffect(() => {
    fetchUsers();
  }, [searchKeyword, departmentFilter, statusFilter]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await userService.getAllUsers({
        keyword: searchKeyword || undefined,
        department: departmentFilter,
        enabled: statusFilter
      });
      if (response.success && response.data) {
        setUsers(response.data);
      }
    } catch (error) {
      message.error('获取用户列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateUser = () => {
    setEditingUser(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEditUser = (user: User) => {
    setEditingUser(user);
    form.setFieldsValue({
      ...user,
      hireDate: user.hireDate ? dayjs(user.hireDate) : undefined
    });
    setIsModalVisible(true);
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingUser) {
        // 更新用户
        const updateRequest: UpdateUserRequest = {
          fullName: values.fullName,
          email: values.email,
          phone: values.phone,
          hireDate: values.hireDate ? values.hireDate.format('YYYY-MM-DD') : undefined,
          employeeId: values.employeeId,
          department: values.department,
          position: values.position,
          enabled: values.enabled
        };
        
        const response = await userService.updateUser(editingUser.id, updateRequest);
        if (response.success) {
          message.success('用户更新成功');
          fetchUsers();
          setIsModalVisible(false);
        } else {
          message.error(response.message || '用户更新失败');
        }
      } else {
        // 创建用户
        const createRequest: CreateUserRequest = {
          username: values.username,
          password: values.password,
          fullName: values.fullName,
          email: values.email,
          phone: values.phone,
          hireDate: values.hireDate ? values.hireDate.format('YYYY-MM-DD') : undefined,
          employeeId: values.employeeId,
          department: values.department,
          position: values.position,
          enabled: values.enabled ?? true
        };
        
        const response = await userService.createUser(createRequest);
        if (response.success) {
          message.success('用户创建成功');
          fetchUsers();
          setIsModalVisible(false);
        } else {
          message.error(response.message || '用户创建失败');
        }
      }
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const handleToggleUserStatus = async (user: User) => {
    try {
      if (user.enabled) {
        const response = await userService.disableUser(user.id);
        if (response.success) {
          message.success('用户已禁用');
          fetchUsers();
        }
      } else {
        const response = await userService.enableUser(user.id);
        if (response.success) {
          message.success('用户已启用');
          fetchUsers();
        }
      }
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleDeleteUser = async (userId: number) => {
    try {
      const response = await userService.deleteUser(userId);
      if (response.success) {
        message.success('用户删除成功');
        fetchUsers();
      } else {
        message.error(response.message || '用户删除失败');
      }
    } catch (error) {
      message.error('用户删除失败');
    }
  };

  const handleResetPassword = (user: User) => {
    Modal.confirm({
      title: '重置密码',
      content: `确定要重置用户 "${user.fullName}" 的密码吗？`,
      onOk: async () => {
        try {
          // 生成一个临时密码
          const tempPassword = 'TempPass123!';
          const response = await userService.resetUserPassword(user.id, tempPassword);
          if (response.success) {
            message.success(`密码重置成功，临时密码：${tempPassword}`);
          } else {
            message.error(response.message || '密码重置失败');
          }
        } catch (error) {
          message.error('密码重置失败');
        }
      }
    });
  };

  const validateUsername = async (_: any, value: string) => {
    if (!value || editingUser) return;
    
    try {
      const response = await userService.checkUsernameExists(value);
      if (response.success && response.data?.exists) {
        throw new Error('用户名已存在');
      }
    } catch (error: any) {
      throw new Error(error.message || '用户名验证失败');
    }
  };

  const validateEmail = async (_: any, value: string) => {
    if (!value) return;
    
    try {
      const response = await userService.checkEmailExists(value);
      if (response.success && response.data?.exists) {
        // 如果是编辑模式且邮箱未变更，则不报错
        if (editingUser && editingUser.email === value) {
          return;
        }
        throw new Error('邮箱已被使用');
      }
    } catch (error: any) {
      throw new Error(error.message || '邮箱验证失败');
    }
  };

  const columns: ColumnsType<User> = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 120,
      fixed: 'left'
    },
    {
      title: '姓名',
      dataIndex: 'fullName',
      key: 'fullName',
      width: 120
    },
    {
      title: '员工工号',
      dataIndex: 'employeeId',
      key: 'employeeId',
      width: 120
    },
    {
      title: '部门',
      dataIndex: 'department',
      key: 'department',
      width: 120
    },
    {
      title: '职位',
      dataIndex: 'position',
      key: 'position',
      width: 120
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      width: 180
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
      width: 130
    },
    {
      title: '入职日期',
      dataIndex: 'hireDate',
      key: 'hireDate',
      width: 120,
      render: (date: string) => date ? dayjs(date).format('YYYY-MM-DD') : '-'
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      key: 'enabled',
      width: 80,
      render: (enabled: boolean) => (
        <Tag color={enabled ? 'success' : 'default'}>
          {enabled ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 150,
      render: (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')
    },
    {
      title: '操作',
      key: 'actions',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Tooltip title="编辑">
            <Button
              type="link"
              icon={<EditOutlined />}
              onClick={() => handleEditUser(record)}
              size="small"
            />
          </Tooltip>
          <Tooltip title={record.enabled ? '禁用' : '启用'}>
            <Button
              type="link"
              icon={record.enabled ? <StopOutlined /> : <CheckCircleOutlined />}
              onClick={() => handleToggleUserStatus(record)}
              size="small"
            />
          </Tooltip>
          <Tooltip title="重置密码">
            <Button
              type="link"
              icon={<KeyOutlined />}
              onClick={() => handleResetPassword(record)}
              size="small"
            />
          </Tooltip>
          <Tooltip title="删除">
            <Popconfirm
              title="确定删除这个用户吗？"
              onConfirm={() => handleDeleteUser(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <Button
                type="link"
                danger
                icon={<DeleteOutlined />}
                size="small"
              />
            </Popconfirm>
          </Tooltip>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={6}>
            <Search
              placeholder="搜索用户名、姓名或员工工号"
              onSearch={setSearchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              allowClear
            />
          </Col>
          <Col span={4}>
            <Select
              placeholder="选择部门"
              allowClear
              style={{ width: '100%' }}
              onChange={setDepartmentFilter}
            >
              <Option value="内科">内科</Option>
              <Option value="外科">外科</Option>
              <Option value="儿科">儿科</Option>
              <Option value="妇科">妇科</Option>
              <Option value="药房">药房</Option>
              <Option value="护理部">护理部</Option>
              <Option value="行政部">行政部</Option>
            </Select>
          </Col>
          <Col span={4}>
            <Select
              placeholder="选择状态"
              allowClear
              style={{ width: '100%' }}
              onChange={setStatusFilter}
            >
              <Option value={true}>启用</Option>
              <Option value={false}>禁用</Option>
            </Select>
          </Col>
          <Col span={10}>
            <Space>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={handleCreateUser}
              >
                新增用户
              </Button>
              <Button
                icon={<ReloadOutlined />}
                onClick={fetchUsers}
              >
                刷新
              </Button>
            </Space>
          </Col>
        </Row>

        <Table
          columns={columns}
          dataSource={users}
          loading={loading}
          rowKey="id"
          scroll={{ x: 1500 }}
          pagination={{
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`
          }}
        />
      </Card>

      <Modal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{ enabled: true }}
        >
          {!editingUser && (
            <>
              <Form.Item
                name="username"
                label="用户名"
                rules={[
                  { required: true, message: '请输入用户名' },
                  { min: 3, max: 50, message: '用户名长度必须在3-50个字符之间' },
                  { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线' },
                  { validator: validateUsername }
                ]}
              >
                <Input placeholder="请输入用户名" />
              </Form.Item>
              <Form.Item
                name="password"
                label="密码"
                rules={[
                  { required: true, message: '请输入密码' },
                  { min: 8, max: 50, message: '密码长度必须在8-50个字符之间' },
                  {
                    pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
                    message: '密码必须包含至少一个大写字母、一个小写字母、一个数字和一个特殊字符(@$!%*?&)'
                  }
                ]}
              >
                <Input.Password placeholder="请输入密码" />
              </Form.Item>
            </>
          )}
          
          <Form.Item
            name="fullName"
            label="员工姓名"
            rules={[
              { required: true, message: '请输入员工姓名' },
              { max: 100, message: '员工姓名长度不能超过100个字符' }
            ]}
          >
            <Input placeholder="请输入员工姓名" />
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="email"
                label="邮箱地址"
                rules={[
                  { type: 'email', message: '邮箱格式不正确' },
                  { max: 100, message: '邮箱长度不能超过100个字符' },
                  { validator: validateEmail }
                ]}
              >
                <Input placeholder="请输入邮箱地址" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="phone"
                label="手机号码"
                rules={[
                  { pattern: /^1[3-9]\d{9}$/, message: '手机号码格式不正确' }
                ]}
              >
                <Input placeholder="请输入手机号码" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="employeeId"
                label="员工工号"
                rules={[
                  { max: 50, message: '员工工号长度不能超过50个字符' }
                ]}
              >
                <Input placeholder="请输入员工工号" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="hireDate"
                label="入职日期"
              >
                <DatePicker 
                  style={{ width: '100%' }}
                  placeholder="请选择入职日期"
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="department"
                label="部门"
                rules={[
                  { max: 100, message: '部门名称长度不能超过100个字符' }
                ]}
              >
                <Select placeholder="请选择部门">
                  <Option value="内科">内科</Option>
                  <Option value="外科">外科</Option>
                  <Option value="儿科">儿科</Option>
                  <Option value="妇科">妇科</Option>
                  <Option value="药房">药房</Option>
                  <Option value="护理部">护理部</Option>
                  <Option value="行政部">行政部</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="position"
                label="职位"
                rules={[
                  { max: 100, message: '职位名称长度不能超过100个字符' }
                ]}
              >
                <Input placeholder="请输入职位" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            name="enabled"
            label="账号状态"
            valuePropName="checked"
          >
            <Switch checkedChildren="启用" unCheckedChildren="禁用" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserManagement;