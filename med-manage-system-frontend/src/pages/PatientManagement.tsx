import React, { useState, useEffect } from 'react';
import { Form, message, Card } from 'antd';
import dayjs from 'dayjs';
import { patientService, Patient, CreatePatientRequest, UpdatePatientRequest } from '../services/patientService';
import PatientSearchForm from '../components/PatientManagement/PatientSearchForm';
import PatientFormModal from '../components/PatientManagement/PatientFormModal';
import PatientTable from '../components/PatientManagement/PatientTable';

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
        
        // 应用过滤器
        filteredData = applyFilters(filteredData);
        setPatients(filteredData);
      }
    } catch (error) {
      message.error('获取患者列表失败');
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = (data: Patient[]) => {
    let filtered = data;

    if (genderFilter) {
      filtered = filtered.filter(patient => patient.gender === genderFilter);
    }
    
    if (statusFilter) {
      filtered = filtered.filter(patient => patient.status === statusFilter);
    }
    
    if (isVipFilter !== undefined) {
      filtered = filtered.filter(patient => patient.isVip === isVipFilter);
    }

    return filtered;
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

  // 处理搜索参数变化
  const handleSearchChange = (value: string) => {
    setSearchKeyword(value);
  };

  const handleGenderChange = (value?: string) => {
    setGenderFilter(value);
    fetchPatients();
  };

  const handleStatusChange = (value?: string) => {
    setStatusFilter(value);
    fetchPatients();
  };

  const handleVipChange = (value?: boolean) => {
    setIsVipFilter(value);
    fetchPatients();
  };

  const handleSearch = () => {
    fetchPatients();
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <PatientSearchForm
          searchKeyword={searchKeyword}
          genderFilter={genderFilter}
          statusFilter={statusFilter}
          isVipFilter={isVipFilter}
          onSearchChange={handleSearchChange}
          onGenderChange={handleGenderChange}
          onStatusChange={handleStatusChange}
          onVipChange={handleVipChange}
          onSearch={handleSearch}
          onRefresh={fetchPatients}
          onCreatePatient={handleCreatePatient}
        />

        <PatientTable
          patients={patients}
          loading={loading}
          onEdit={handleEditPatient}
          onDelete={handleDeletePatient}
          onToggleVip={handleToggleVipStatus}
        />
      </Card>

      <PatientFormModal
        visible={isModalVisible}
        editingPatient={editingPatient}
        form={form}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
      />
    </div>
  );
};

export default PatientManagement;