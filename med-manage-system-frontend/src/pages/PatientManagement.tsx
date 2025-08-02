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
  Tooltip,
  Tabs,
  Badge
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ReloadOutlined,
  UserOutlined,
  StarOutlined,
  StarFilled,
  MedicineBoxOutlined,
  HistoryOutlined
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { patientService, Patient, CreatePatientRequest, UpdatePatientRequest } from '../services/patientService';

const { Search } = Input;
const { Option } = Select;
const { TabPane } = Tabs;

const PatientManagement: React.FC = () => {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingPatient, setEditingPatient] = useState<Patient | null>(null);
  const [form] = Form.useForm();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [genderFilter, setGenderFilter] = useState<string | undefined>();
  const [statusFilter, setStatusFilter] = useState<string | undefined>();
  const [isVipFilter, setIsVipFilter] = useState<boolean | undefined>();

  useEffect(() => {
    fetchPatients();
  }, []);

  const fetchPatients = async () => {
    setLoading(true);
    try {
      let response;
      
      if (searchKeyword) {
        response = await patientService.searchPatients(searchKeyword);
      } else {
        response = await patientService.getAllPatients();
      }
      
      if (response.success && response.data) {
        let filteredData = response.data;
        
        if (genderFilter) {
          filteredData = filteredData.filter(patient => patient.gender === genderFilter);
        }
        
        if (statusFilter) {
          filteredData = filteredData.filter(patient => patient.status === statusFilter);
        }
        
        if (isVipFilter !== undefined) {
          filteredData = filteredData.filter(patient => patient.isVip === isVipFilter);
        }
        
        setPatients(filteredData);
      }
    } catch (error) {
      message.error('获取患者列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePatient = () => {
    setEditingPatient(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEditPatient = (patient: Patient) => {
    setEditingPatient(patient);
    form.setFieldsValue({
      ...patient,
      birthDate: patient.birthDate ? dayjs(patient.birthDate) : undefined
    });
    setIsModalVisible(true);
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingPatient) {
        // 更新患者
        const updateRequest: UpdatePatientRequest = {
          ...values,
          birthDate: values.birthDate ? values.birthDate.format('YYYY-MM-DD') : undefined
        };
        
        const response = await patientService.updatePatient(editingPatient.id, updateRequest);
        if (response.success) {
          message.success('患者信息更新成功');
          fetchPatients();
          setIsModalVisible(false);
        } else {
          message.error(response.message || '患者信息更新失败');
        }
      } else {
        // 创建患者
        const createRequest: CreatePatientRequest = {
          ...values,
          birthDate: values.birthDate ? values.birthDate.format('YYYY-MM-DD') : undefined
        };
        
        const response = await patientService.createPatient(createRequest);
        if (response.success) {
          message.success('患者创建成功');
          fetchPatients();
          setIsModalVisible(false);
        } else {
          message.error(response.message || '患者创建失败');
        }
      }
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const handleDeletePatient = async (patientId: number) => {
    try {
      const response = await patientService.deletePatient(patientId);
      if (response.success) {
        message.success('患者删除成功');
        fetchPatients();
      } else {
        message.error(response.message || '患者删除失败');
      }
    } catch (error) {
      message.error('患者删除失败');
    }
  };

  const handleToggleVipStatus = async (patient: Patient) => {
    try {
      let response;
      if (patient.isVip) {
        response = await patientService.removePatientVipStatus(patient.id);
      } else {
        response = await patientService.setPatientAsVip(patient.id);
      }
      
      if (response.success) {
        message.success(patient.isVip ? '已取消VIP状态' : '已设置为VIP');
        fetchPatients();
      } else {
        message.error(response.message || '操作失败');
      }
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleUpdateStatus = async (patient: Patient, newStatus: string) => {
    try {
      const response = await patientService.updatePatientStatus(patient.id, newStatus);
      if (response.success) {
        message.success('患者状态更新成功');
        fetchPatients();
      } else {
        message.error(response.message || '状态更新失败');
      }
    } catch (error) {
      message.error('状态更新失败');
    }
  };

  const columns: ColumnsType<Patient> = [
    {
      title: '患者编号',
      dataIndex: 'patientNumber',
      key: 'patientNumber',
      width: 120,
      fixed: 'left'
    },
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 100,
      render: (text: string, record: Patient) => (
        <Space>
          {text}
          {record.isVip && <StarFilled style={{ color: '#faad14' }} />}
        </Space>
      )
    },
    {
      title: '性别',
      dataIndex: 'gender',
      key: 'gender',
      width: 60
    },
    {
      title: '年龄',
      dataIndex: 'age',
      key: 'age',
      width: 60
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
      width: 130
    },
    {
      title: '血型',
      dataIndex: 'bloodType',
      key: 'bloodType',
      width: 70
    },
    {
      title: '医保类型',
      dataIndex: 'insuranceType',
      key: 'insuranceType',
      width: 100
    },
    {
      title: '就诊次数',
      dataIndex: 'visitCount',
      key: 'visitCount',
      width: 80,
      render: (count: number) => <Badge count={count} />
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => {
        const color = status === '正常' ? 'green' : status === '黑名单' ? 'red' : 'orange';
        return <Tag color={color}>{status}</Tag>;
      }
    },
    {
      title: '最后就诊',
      dataIndex: 'lastVisitTime',
      key: 'lastVisitTime',
      width: 120,
      render: (date: string) => date ? dayjs(date).format('YYYY-MM-DD') : '-'
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
              onClick={() => handleEditPatient(record)}
              size="small"
            />
          </Tooltip>
          <Tooltip title={record.isVip ? '取消VIP' : '设为VIP'}>
            <Button
              type="link"
              icon={record.isVip ? <StarFilled /> : <StarOutlined />}
              onClick={() => handleToggleVipStatus(record)}
              size="small"
              style={{ color: record.isVip ? '#faad14' : undefined }}
            />
          </Tooltip>
          <Tooltip title="病史">
            <Button
              type="link"
              icon={<HistoryOutlined />}
              size="small"
            />
          </Tooltip>
          <Tooltip title="删除">
            <Popconfirm
              title="确定删除这个患者吗？"
              onConfirm={() => handleDeletePatient(record.id)}
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
              placeholder="搜索患者姓名、编号或手机号"
              onSearch={(value) => {
                setSearchKeyword(value);
                fetchPatients();
              }}
              onChange={(e) => setSearchKeyword(e.target.value)}
              allowClear
            />
          </Col>
          <Col span={3}>
            <Select
              placeholder="选择性别"
              allowClear
              style={{ width: '100%' }}
              onChange={(value) => {
                setGenderFilter(value);
                fetchPatients();
              }}
            >
              <Option value="男">男</Option>
              <Option value="女">女</Option>
              <Option value="未知">未知</Option>
            </Select>
          </Col>
          <Col span={3}>
            <Select
              placeholder="选择状态"
              allowClear
              style={{ width: '100%' }}
              onChange={(value) => {
                setStatusFilter(value);
                fetchPatients();
              }}
            >
              <Option value="正常">正常</Option>
              <Option value="黑名单">黑名单</Option>
              <Option value="暂停服务">暂停服务</Option>
            </Select>
          </Col>
          <Col span={3}>
            <Select
              placeholder="VIP状态"
              allowClear
              style={{ width: '100%' }}
              onChange={(value) => {
                setIsVipFilter(value);
                fetchPatients();
              }}
            >
              <Option value={true}>VIP患者</Option>
              <Option value={false}>普通患者</Option>
            </Select>
          </Col>
          <Col span={9}>
            <Space>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={handleCreatePatient}
              >
                新增患者
              </Button>
              <Button
                icon={<ReloadOutlined />}
                onClick={fetchPatients}
              >
                刷新
              </Button>
            </Space>
          </Col>
        </Row>

        <Table
          columns={columns}
          dataSource={patients}
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
        title={editingPatient ? '编辑患者' : '新增患者'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        width={800}
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{ 
            gender: '未知',
            maritalStatus: '未知',
            ethnicity: '汉族',
            isVip: false 
          }}
        >
          <Tabs defaultActiveKey="basic">
            <TabPane tab="基本信息" key="basic">
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="name"
                    label="患者姓名"
                    rules={[
                      { required: true, message: '请输入患者姓名' },
                      { max: 100, message: '患者姓名长度不能超过100个字符' }
                    ]}
                  >
                    <Input placeholder="请输入患者姓名" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="gender"
                    label="性别"
                    rules={[{ required: true, message: '请选择性别' }]}
                  >
                    <Select placeholder="请选择性别">
                      <Option value="男">男</Option>
                      <Option value="女">女</Option>
                      <Option value="未知">未知</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
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
                <Col span={12}>
                  <Form.Item
                    name="birthDate"
                    label="出生日期"
                  >
                    <DatePicker 
                      style={{ width: '100%' }}
                      placeholder="请选择出生日期"
                    />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item
                name="idCard"
                label="身份证号码"
                rules={[
                  { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '身份证号码格式不正确' }
                ]}
              >
                <Input placeholder="请输入身份证号码" />
              </Form.Item>

              <Form.Item
                name="address"
                label="联系地址"
                rules={[
                  { max: 500, message: '联系地址长度不能超过500个字符' }
                ]}
              >
                <Input.TextArea rows={2} placeholder="请输入联系地址" />
              </Form.Item>
            </TabPane>

            <TabPane tab="详细信息" key="details">
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="bloodType"
                    label="血型"
                    rules={[
                      { pattern: /^(A|B|AB|O)[+-]?$/, message: '血型格式不正确' }
                    ]}
                  >
                    <Select placeholder="请选择血型" allowClear>
                      <Option value="A+">A+</Option>
                      <Option value="A-">A-</Option>
                      <Option value="B+">B+</Option>
                      <Option value="B-">B-</Option>
                      <Option value="AB+">AB+</Option>
                      <Option value="AB-">AB-</Option>
                      <Option value="O+">O+</Option>
                      <Option value="O-">O-</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="maritalStatus"
                    label="婚姻状况"
                  >
                    <Select placeholder="请选择婚姻状况">
                      <Option value="未婚">未婚</Option>
                      <Option value="已婚">已婚</Option>
                      <Option value="离异">离异</Option>
                      <Option value="丧偶">丧偶</Option>
                      <Option value="未知">未知</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="occupation"
                    label="职业"
                    rules={[
                      { max: 100, message: '职业长度不能超过100个字符' }
                    ]}
                  >
                    <Input placeholder="请输入职业" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="ethnicity"
                    label="民族"
                    rules={[
                      { max: 50, message: '民族长度不能超过50个字符' }
                    ]}
                  >
                    <Input placeholder="请输入民族" />
                  </Form.Item>
                </Col>
              </Row>
            </TabPane>

            <TabPane tab="紧急联系人" key="emergency">
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="emergencyContactName"
                    label="紧急联系人姓名"
                    rules={[
                      { max: 100, message: '紧急联系人姓名长度不能超过100个字符' }
                    ]}
                  >
                    <Input placeholder="请输入紧急联系人姓名" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="emergencyContactPhone"
                    label="紧急联系人电话"
                    rules={[
                      { pattern: /^1[3-9]\d{9}$/, message: '紧急联系人电话格式不正确' }
                    ]}
                  >
                    <Input placeholder="请输入紧急联系人电话" />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item
                name="emergencyContactRelation"
                label="与患者关系"
                rules={[
                  { max: 50, message: '关系长度不能超过50个字符' }
                ]}
              >
                <Input placeholder="请输入与患者关系" />
              </Form.Item>
            </TabPane>

            <TabPane tab="医保信息" key="insurance">
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="insuranceType"
                    label="医保类型"
                    rules={[
                      { max: 50, message: '医保类型长度不能超过50个字符' }
                    ]}
                  >
                    <Select placeholder="请选择医保类型" allowClear>
                      <Option value="职工医保">职工医保</Option>
                      <Option value="居民医保">居民医保</Option>
                      <Option value="新农合">新农合</Option>
                      <Option value="商业保险">商业保险</Option>
                      <Option value="自费">自费</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="insuranceNumber"
                    label="医保号码"
                    rules={[
                      { max: 100, message: '医保号码长度不能超过100个字符' }
                    ]}
                  >
                    <Input placeholder="请输入医保号码" />
                  </Form.Item>
                </Col>
              </Row>
            </TabPane>

            <TabPane tab="其他信息" key="other">
              <Form.Item
                name="isVip"
                label="VIP患者"
                valuePropName="checked"
              >
                <Switch checkedChildren="是" unCheckedChildren="否" />
              </Form.Item>

              <Form.Item
                name="remarks"
                label="备注信息"
                rules={[
                  { max: 1000, message: '备注信息长度不能超过1000个字符' }
                ]}
              >
                <Input.TextArea rows={4} placeholder="请输入备注信息" />
              </Form.Item>
            </TabPane>
          </Tabs>
        </Form>
      </Modal>
    </div>
  );
};

export default PatientManagement;