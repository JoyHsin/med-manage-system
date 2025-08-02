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
  Tooltip
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { roleService } from '../services/roleService'; // 假设有roleService
import type { Role, CreateRoleRequest, UpdateRoleRequest } from '../types/auth'; // 假设有Role类型

const { Search } = Input;
const { Option } = Select;

const RolePermissionManagement: React.FC = () => {
  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRole, setEditingRole] = useState<Role | null>(null);
  const [form] = Form.useForm();
  const [searchKeyword, setSearchKeyword] = useState('');

  useEffect(() => {
    fetchRoles();
  }, [searchKeyword]);

  const fetchRoles = async () => {
    setLoading(true);
    try {
      const response = await roleService.getAllRoles({ keyword: searchKeyword || undefined });
      if (response.success && response.data) {
        setRoles(response.data);
      }
    } catch (error) {
      message.error('获取角色列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateRole = () => {
    setEditingRole(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEditRole = (role: Role) => {
    setEditingRole(role);
    form.setFieldsValue({
      ...role,
      code: role.code,
    });
    setIsModalVisible(true);
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingRole) {
        // 更新角色
        const updateRequest: UpdateRoleRequest = {
          roleName: values.name,
          description: values.description,
        };
        
        const response = await roleService.updateRole(editingRole.id, updateRequest);
        if (response.success) {
          message.success('角色更新成功');
          fetchRoles();
          setIsModalVisible(false);
        } else {
          message.error(response.message || '角色更新失败');
        }
      } else {
        // 创建角色
        const createRequest: CreateRoleRequest = {
          roleName: values.name,
          roleCode: values.code,
          description: values.description,
        };
        
        const response = await roleService.createRole(createRequest);
        if (response.success) {
          message.success('角色创建成功');
          fetchRoles();
          setIsModalVisible(false);
        } else {
          message.error(response.message || '角色创建失败');
        }
      }
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const handleDeleteRole = async (roleId: number) => {
    try {
      const response = await roleService.deleteRole(roleId);
      if (response.success) {
        message.success('角色删除成功');
        fetchRoles();
      } else {
        message.error(response.message || '角色删除失败');
      }
    } catch (error) {
      message.error('角色删除失败');
    }
  };

  const columns: ColumnsType<Role> = [
    {
      title: '角色名称',
      dataIndex: 'name',
      key: 'name',
      sorter: (a, b) => a.name.localeCompare(b.name),
    },
    {
      title: '角色编码',
      dataIndex: 'code',
      key: 'code',
      sorter: (a, b) => a.code.localeCompare(b.code),
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (text: string) => new Date(text).toLocaleString(),
      sorter: (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
    },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      render: (text: string) => new Date(text).toLocaleString(),
      sorter: (a, b) => new Date(a.updatedAt).getTime() - new Date(b.updatedAt).getTime(),
    },
    {
      title: '操作',
      key: 'actions',
      render: (_, record) => (
        <Space size="middle">
          <Tooltip title="编辑">
            <Button icon={<EditOutlined />} onClick={() => handleEditRole(record)} />
          </Tooltip>
          <Popconfirm
            title="确定删除此角色吗？"
            onConfirm={() => handleDeleteRole(record.id)}
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
    <Card title="角色权限管理" extra={
      <Space>
        <Search
          placeholder="搜索角色名称或描述"
          onSearch={setSearchKeyword}
          style={{ width: 200 }}
          allowClear
        />
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreateRole}>
          新增角色
        </Button>
        <Button icon={<ReloadOutlined />} onClick={fetchRoles}>
          刷新
        </Button>
      </Space>
    }>
      <Table
        columns={columns}
        dataSource={roles}
        loading={loading}
        rowKey="id"
        pagination={{ pageSize: 10 }}
      />

      <Modal
        title={editingRole ? '编辑角色' : '新增角色'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="角色名称"
            rules={[{ required: true, message: '请输入角色名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="code"
            label="角色编码"
            rules={[{ required: true, message: '请输入角色编码!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default RolePermissionManagement;