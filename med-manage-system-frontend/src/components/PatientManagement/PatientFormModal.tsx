import React from 'react';
import { Modal, Form, Input, Select, DatePicker, Switch, Row, Col, Tabs } from 'antd';
import dayjs from 'dayjs';
import type { Patient, CreatePatientRequest, UpdatePatientRequest } from '../../services/patientService';

const { Option } = Select;
const { TabPane } = Tabs;

interface PatientFormModalProps {
  visible: boolean;
  editingPatient: Patient | null;
  form: any;
  onOk: () => void;
  onCancel: () => void;
}

const PatientFormModal: React.FC<PatientFormModalProps> = ({
  visible,
  editingPatient,
  form,
  onOk,
  onCancel,
}) => {
  return (
    <Modal
      title={editingPatient ? '编辑患者' : '新增患者'}
      open={visible}
      onOk={onOk}
      onCancel={onCancel}
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
  );
};

export default PatientFormModal;