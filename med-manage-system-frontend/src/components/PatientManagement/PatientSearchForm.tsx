import React from 'react';
import { Row, Col, Input, Select, Button, Space } from 'antd';
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons';

const { Search } = Input;
const { Option } = Select;

interface PatientSearchFormProps {
  searchKeyword: string;
  genderFilter?: string;
  statusFilter?: string;
  isVipFilter?: boolean;
  onSearchChange: (value: string) => void;
  onGenderChange: (value?: string) => void;
  onStatusChange: (value?: string) => void;
  onVipChange: (value?: boolean) => void;
  onSearch: () => void;
  onRefresh: () => void;
  onCreatePatient: () => void;
}

const PatientSearchForm: React.FC<PatientSearchFormProps> = ({
  searchKeyword,
  genderFilter,
  statusFilter,
  isVipFilter,
  onSearchChange,
  onGenderChange,
  onStatusChange,
  onVipChange,
  onSearch,
  onRefresh,
  onCreatePatient,
}) => {
  return (
    <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
      <Col span={6}>
        <Search
          placeholder="搜索患者姓名、编号或手机号"
          value={searchKeyword}
          onSearch={onSearch}
          onChange={(e) => onSearchChange(e.target.value)}
          allowClear
        />
      </Col>
      <Col span={3}>
        <Select
          placeholder="选择性别"
          allowClear
          style={{ width: '100%' }}
          value={genderFilter}
          onChange={onGenderChange}
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
          value={statusFilter}
          onChange={onStatusChange}
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
          value={isVipFilter}
          onChange={onVipChange}
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
            onClick={onCreatePatient}
          >
            新增患者
          </Button>
          <Button
            icon={<ReloadOutlined />}
            onClick={onRefresh}
          >
            刷新
          </Button>
        </Space>
      </Col>
    </Row>
  );
};

export default PatientSearchForm;