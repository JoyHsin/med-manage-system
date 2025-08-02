import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Button, 
  Typography, 
  Table, 
  message, 
  Space, 
  Modal, 
  Form, 
  Input, 
  Select,
  Switch,
  Tag,
  Popconfirm,
  Dropdown,
  Transfer
} from 'antd';
import { PlusOutlined, ReloadOutlined, EditOutlined, DeleteOutlined, MoreOutlined, KeyOutlined } from '@ant-design/icons';
import { userService } from '../services/userService';
import type { User, CreateUserRequest, UpdateUserRequest, Role } from '../types/auth';

const { Title } = Typography;
const { Option } = Select;

const SimpleUserManagement: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [statusFilter, setStatusFilter] = useState<'all' | 'enabled' | 'disabled'>('all');
  const [departmentFilter, setDepartmentFilter] = useState<string>('all');
  const [isCreateModalVisible, setIsCreateModalVisible] = useState(false);
  const [isEditModalVisible, setIsEditModalVisible] = useState(false);
  const [isRoleModalVisible, setIsRoleModalVisible] = useState(false);
  const [isPasswordModalVisible, setIsPasswordModalVisible] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [roles, setRoles] = useState<Role[]>([]);
  const [userRoles, setUserRoles] = useState<Role[]>([]);
  const [createForm] = Form.useForm();
  const [editForm] = Form.useForm();
  const [passwordForm] = Form.useForm();

  // 获取角色列表
  const fetchRoles = async () => {
    try {
      const response = await userService.getEnabledRoles();
      if (response.success && response.data) {
        setRoles(response.data);
      }
    } catch (error: any) {
      console.error('获取角色列表失败:', error);
    }
  };

  // 获取用户角色
  const fetchUserRoles = async (userId: number) => {
    try {
      const response = await userService.getUserRoles(userId);
      if (response.success && response.data) {
        setUserRoles(response.data);
      }
    } catch (error: any) {
      console.error('获取用户角色失败:', error);
    }
  };

  // 搜索和过滤逻辑
  const filterUsers = () => {
    let filtered = [...users];

    // 文本搜索
    if (searchText) {
      filtered = filtered.filter(user => 
        user.username.toLowerCase().includes(searchText.toLowerCase()) ||
        user.fullName.toLowerCase().includes(searchText.toLowerCase()) ||
        user.email.toLowerCase().includes(searchText.toLowerCase()) ||
        user.department.toLowerCase().includes(searchText.toLowerCase()) ||
        user.position.toLowerCase().includes(searchText.toLowerCase())
      );
    }

    // 状态过滤
    if (statusFilter !== 'all') {
      filtered = filtered.filter(user => 
        statusFilter === 'enabled' ? user.enabled : !user.enabled
      );
    }

    // 部门过滤
    if (departmentFilter !== 'all') {
      filtered = filtered.filter(user => user.department === departmentFilter);
    }

    setFilteredUsers(filtered);
  };

  // 获取用户列表
  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await userService.getAllUsers();
      console.log('User list response:', response);
      if (response.success && response.data) {
        setUsers(response.data);
        setFilteredUsers(response.data); // 初始化过滤后的用户列表
        message.success(`成功加载 ${response.data.length} 个用户`);
      } else {
        message.error('获取用户列表失败');
      }
    } catch (error: any) {
      console.error('获取用户列表失败:', error);
      message.error('获取用户列表失败: ' + (error.message || '未知错误'));
    } finally {
      setLoading(false);
    }
  };

  // 创建用户
  const handleCreateUser = async () => {
    try {
      const values = await createForm.validateFields();
      console.log('Creating user with values:', values);
      
      const createRequest: CreateUserRequest = {
        username: values.username,
        password: values.password,
        fullName: values.fullName,
        email: values.email,
        phone: values.phone,
        department: values.department,
        position: values.position,
        enabled: values.enabled ?? true
      };
      
      const response = await userService.createUser(createRequest);
      if (response.success) {
        message.success('用户创建成功');
        setIsCreateModalVisible(false);
        createForm.resetFields();
        fetchUsers(); // 刷新用户列表
      } else {
        message.error(response.message || '用户创建失败');
      }
    } catch (error: any) {
      console.error('创建用户失败:', error);
      if (error.message) {
        message.error('表单验证失败');
      } else {
        message.error('创建用户失败: ' + (error.response?.data?.message || error.message || '未知错误'));
      }
    }
  };

  // 编辑用户
  const handleEditUser = async () => {
    try {
      const values = await editForm.validateFields();
      if (!selectedUser) return;

      const updateRequest: UpdateUserRequest = {
        fullName: values.fullName,
        email: values.email,
        phone: values.phone,
        department: values.department,
        position: values.position,
        enabled: values.enabled
      };

      const response = await userService.updateUser(selectedUser.id, updateRequest);
      if (response.success) {
        message.success('用户信息更新成功');
        setIsEditModalVisible(false);
        setSelectedUser(null);
        fetchUsers();
      } else {
        message.error(response.message || '用户信息更新失败');
      }
    } catch (error: any) {
      console.error('更新用户失败:', error);
      message.error('更新用户失败: ' + (error.response?.data?.message || error.message || '未知错误'));
    }
  };

  // 启用/禁用用户
  const handleToggleUserStatus = async (userId: number, enabled: boolean) => {
    try {
      const response = enabled 
        ? await userService.enableUser(userId)
        : await userService.disableUser(userId);
      
      if (response.success) {
        message.success(`用户${enabled ? '启用' : '禁用'}成功`);
        fetchUsers();
      } else {
        message.error(response.message || `用户${enabled ? '启用' : '禁用'}失败`);
      }
    } catch (error: any) {
      console.error(`${enabled ? '启用' : '禁用'}用户失败:`, error);
      message.error(`${enabled ? '启用' : '禁用'}用户失败: ` + (error.response?.data?.message || error.message || '未知错误'));
    }
  };

  // 删除用户
  const handleDeleteUser = async (userId: number) => {
    try {
      const response = await userService.deleteUser(userId);
      if (response.success) {
        message.success('用户删除成功');
        fetchUsers();
      } else {
        message.error(response.message || '用户删除失败');
      }
    } catch (error: any) {
      console.error('删除用户失败:', error);
      message.error('删除用户失败: ' + (error.response?.data?.message || error.message || '未知错误'));
    }
  };

  // 重置用户密码
  const handleResetPassword = async () => {
    try {
      const values = await passwordForm.validateFields();
      if (!selectedUser) return;

      const response = await userService.resetUserPassword(selectedUser.id, values.newPassword);
      if (response.success) {
        message.success('密码重置成功');
        setIsPasswordModalVisible(false);
        setSelectedUser(null);
        passwordForm.resetFields();
      } else {
        message.error(response.message || '密码重置失败');
      }
    } catch (error: any) {
      console.error('重置密码失败:', error);
      message.error('重置密码失败: ' + (error.response?.data?.message || error.message || '未知错误'));
    }
  };

  // 分配角色
  const handleAssignRoles = async (targetKeys: string[]) => {
    try {
      if (!selectedUser) return;

      // 获取当前用户角色
      const currentRoleIds = userRoles.map(role => role.id.toString());
      const newRoleIds = targetKeys;

      // 找出需要添加的角色
      const rolesToAdd = newRoleIds.filter(id => !currentRoleIds.includes(id));
      
      // 找出需要移除的角色
      const rolesToRemove = currentRoleIds.filter(id => !newRoleIds.includes(id));

      // 执行角色分配
      for (const roleId of rolesToAdd) {
        await userService.assignRoleToUser(selectedUser.id, parseInt(roleId));
      }

      // 执行角色移除
      for (const roleId of rolesToRemove) {
        await userService.removeRoleFromUser(selectedUser.id, parseInt(roleId));
      }

      message.success('角色分配成功');
      setIsRoleModalVisible(false);
      setSelectedUser(null);
      fetchUsers();
    } catch (error: any) {
      console.error('角色分配失败:', error);
      message.error('角色分配失败: ' + (error.response?.data?.message || error.message || '未知错误'));
    }
  };

  // 打开编辑模态框
  const openEditModal = (user: User) => {
    setSelectedUser(user);
    editForm.setFieldsValue({
      fullName: user.fullName,
      email: user.email,
      phone: user.phone,
      department: user.department,
      position: user.position,
      enabled: user.enabled
    });
    setIsEditModalVisible(true);
  };

  // 打开角色管理模态框
  const openRoleModal = async (user: User) => {
    setSelectedUser(user);
    await fetchUserRoles(user.id);
    setIsRoleModalVisible(true);
  };

  // 打开密码重置模态框
  const openPasswordModal = (user: User) => {
    setSelectedUser(user);
    setIsPasswordModalVisible(true);
  };

  // 组件加载时获取用户列表
  useEffect(() => {
    fetchUsers();
    fetchRoles();
  }, []);

  // 监听搜索和过滤条件变化
  useEffect(() => {
    filterUsers();
  }, [users, searchText, statusFilter, departmentFilter]);

  // 表格列定义
  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '姓名',
      dataIndex: 'fullName',
      key: 'fullName',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
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
      title: '角色',
      dataIndex: 'roles',
      key: 'roles',
      render: (roles: Role[]) => (
        <div>
          {roles.map(role => (
            <Tag key={role.id} color="blue" style={{ marginBottom: 4 }}>
              {role.roleName}
            </Tag>
          ))}
          {roles.length === 0 && <Tag color="default">无角色</Tag>}
        </div>
      ),
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      key: 'enabled',
      render: (enabled: boolean) => (
        <span style={{ color: enabled ? 'green' : 'red' }}>
          {enabled ? '启用' : '禁用'}
        </span>
      ),
    },
    {
      title: '操作',
      key: 'actions',
      render: (_, record: User) => {
        const menuItems = [
          {
            key: 'edit',
            icon: <EditOutlined />,
            label: '编辑',
            onClick: () => openEditModal(record),
          },
          {
            key: 'roles',
            icon: <MoreOutlined />,
            label: '管理角色',
            onClick: () => openRoleModal(record),
          },
          {
            key: 'password',
            icon: <KeyOutlined />,
            label: '重置密码',
            onClick: () => openPasswordModal(record),
          },
          {
            key: 'toggle',
            label: record.enabled ? '禁用' : '启用',
            onClick: () => handleToggleUserStatus(record.id, !record.enabled),
          },
        ];

        return (
          <Space>
            <Button
              type="primary"
              size="small"
              icon={<EditOutlined />}
              onClick={() => openEditModal(record)}
            >
              编辑
            </Button>
            <Popconfirm
              title={`确定${record.enabled ? '禁用' : '启用'}该用户吗？`}
              onConfirm={() => handleToggleUserStatus(record.id, !record.enabled)}
              okText="确定"
              cancelText="取消"
            >
              <Button size="small" type={record.enabled ? 'default' : 'primary'}>
                {record.enabled ? '禁用' : '启用'}
              </Button>
            </Popconfirm>
            <Popconfirm
              title={`确定删除用户 "${record.fullName}" 吗？`}
              description="删除后将无法恢复，请谨慎操作！"
              onConfirm={() => handleDeleteUser(record.id)}
              okText="确定删除"
              cancelText="取消"
              okType="danger"
            >
              <Button size="small" danger icon={<DeleteOutlined />}>
                删除
              </Button>
            </Popconfirm>
            <Dropdown menu={{ items: menuItems }}>
              <Button size="small" icon={<MoreOutlined />} />
            </Dropdown>
          </Space>
        );
      },
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>用户管理</Title>
      
      <Card>
        {/* 搜索和过滤区域 */}
        <div style={{ marginBottom: 16 }}>
          <Space wrap>
            <Input.Search
              placeholder="搜索用户名、姓名、邮箱、部门或职位"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              style={{ width: 300 }}
              allowClear
            />
            <Select
              value={statusFilter}
              onChange={setStatusFilter}
              style={{ width: 120 }}
            >
              <Option value="all">全部状态</Option>
              <Option value="enabled">已启用</Option>
              <Option value="disabled">已禁用</Option>
            </Select>
            <Select
              value={departmentFilter}
              onChange={setDepartmentFilter}
              style={{ width: 120 }}
            >
              <Option value="all">全部部门</Option>
              <Option value="内科">内科</Option>
              <Option value="外科">外科</Option>
              <Option value="儿科">儿科</Option>
              <Option value="妇科">妇科</Option>
              <Option value="药房">药房</Option>
              <Option value="护理部">护理部</Option>
              <Option value="行政部">行政部</Option>
            </Select>
          </Space>
        </div>

        {/* 操作按钮区域 */}
        <div style={{ marginBottom: 16 }}>
          <Space>
            <Button 
              type="primary" 
              icon={<PlusOutlined />}
              onClick={() => setIsCreateModalVisible(true)}
            >
              新增用户
            </Button>
            <Button 
              icon={<ReloadOutlined />}
              onClick={fetchUsers}
              loading={loading}
            >
              刷新
            </Button>
          </Space>
        </div>

        <Table
          columns={columns}
          dataSource={filteredUsers}
          loading={loading}
          rowKey="id"
          pagination={{
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条记录`,
          }}
        />
      </Card>

      {/* 创建用户模态框 */}
      <Modal
        title="新增用户"
        open={isCreateModalVisible}
        onOk={handleCreateUser}
        onCancel={() => {
          setIsCreateModalVisible(false);
          createForm.resetFields();
        }}
        width={600}
        destroyOnClose
      >
        <Form
          form={createForm}
          layout="vertical"
          initialValues={{ enabled: true }}
        >
          <Form.Item
            name="username"
            label="用户名"
            rules={[
              { required: true, message: '请输入用户名' },
              { min: 3, max: 50, message: '用户名长度必须在3-50个字符之间' },
              { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线' }
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
            <Input.Password placeholder="请输入密码（包含大小写字母、数字和特殊字符）" />
          </Form.Item>
          
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

          <Form.Item
            name="email"
            label="邮箱地址"
            rules={[
              { type: 'email', message: '邮箱格式不正确' },
              { max: 100, message: '邮箱长度不能超过100个字符' }
            ]}
          >
            <Input placeholder="请输入邮箱地址" />
          </Form.Item>

          <Form.Item
            name="phone"
            label="手机号码"
            rules={[
              { pattern: /^1[3-9]\d{9}$/, message: '手机号码格式不正确' }
            ]}
          >
            <Input placeholder="请输入手机号码" />
          </Form.Item>

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

          <Form.Item
            name="position"
            label="职位"
            rules={[
              { max: 100, message: '职位名称长度不能超过100个字符' }
            ]}
          >
            <Input placeholder="请输入职位" />
          </Form.Item>

          <Form.Item
            name="enabled"
            label="账号状态"
            valuePropName="checked"
          >
            <Switch checkedChildren="启用" unCheckedChildren="禁用" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 编辑用户模态框 */}
      <Modal
        title="编辑用户"
        open={isEditModalVisible}
        onOk={handleEditUser}
        onCancel={() => {
          setIsEditModalVisible(false);
          setSelectedUser(null);
          editForm.resetFields();
        }}
        width={600}
        destroyOnClose
      >
        <Form
          form={editForm}
          layout="vertical"
        >
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

          <Form.Item
            name="email"
            label="邮箱地址"
            rules={[
              { type: 'email', message: '邮箱格式不正确' },
              { max: 100, message: '邮箱长度不能超过100个字符' }
            ]}
          >
            <Input placeholder="请输入邮箱地址" />
          </Form.Item>

          <Form.Item
            name="phone"
            label="手机号码"
            rules={[
              { pattern: /^1[3-9]\d{9}$/, message: '手机号码格式不正确' }
            ]}
          >
            <Input placeholder="请输入手机号码" />
          </Form.Item>

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

          <Form.Item
            name="position"
            label="职位"
            rules={[
              { max: 100, message: '职位名称长度不能超过100个字符' }
            ]}
          >
            <Input placeholder="请输入职位" />
          </Form.Item>

          <Form.Item
            name="enabled"
            label="账号状态"
            valuePropName="checked"
          >
            <Switch checkedChildren="启用" unCheckedChildren="禁用" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 角色管理模态框 */}
      <Modal
        title={`管理用户角色 - ${selectedUser?.fullName}`}
        open={isRoleModalVisible}
        onCancel={() => {
          setIsRoleModalVisible(false);
          setSelectedUser(null);
          setUserRoles([]);
        }}
        footer={null}
        width={800}
      >
        <Transfer
          dataSource={roles.map(role => ({
            key: role.id.toString(),
            title: role.roleName,
            description: role.description,
          }))}
          targetKeys={userRoles.map(role => role.id.toString())}
          onChange={handleAssignRoles}
          render={item => item.title}
          showSearch
          listStyle={{
            width: 300,
            height: 400,
          }}
          titles={['可分配角色', '已分配角色']}
        />
      </Modal>

      {/* 重置密码模态框 */}
      <Modal
        title={`重置密码 - ${selectedUser?.fullName}`}
        open={isPasswordModalVisible}
        onOk={handleResetPassword}
        onCancel={() => {
          setIsPasswordModalVisible(false);
          setSelectedUser(null);
          passwordForm.resetFields();
        }}
        width={500}
      >
        <Form
          form={passwordForm}
          layout="vertical"
        >
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 8, max: 50, message: '密码长度必须在8-50个字符之间' },
              { 
                pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/, 
                message: '密码必须包含至少一个大写字母、一个小写字母、一个数字和一个特殊字符(@$!%*?&)' 
              }
            ]}
          >
            <Input.Password placeholder="请输入新密码（包含大小写字母、数字和特殊字符）" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default SimpleUserManagement;