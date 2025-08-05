package org.me.joy.clinic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.*;
import org.me.joy.clinic.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 完整业务流程集成测试
 * 测试从挂号到收费的完整流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class CompleteWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MedicineMapper medicineMapper;

    // 测试数据存储
    private Long patientId;
    private Long doctorId;
    private Long nurseId;
    private Long appointmentId;
    private Long registrationId;
    private Long medicalRecordId;
    private Long prescriptionId;
    private Long billId;
    private String authToken;

    @BeforeEach
    void setUp() {
        // 创建测试用户和角色
        setupTestData();
    }

    private void setupTestData() {
        // 创建角色
        Role doctorRole = new Role();
        doctorRole.setName("DOCTOR");
        doctorRole.setDescription("医生角色");
        doctorRole.setCreatedAt(LocalDateTime.now());
        roleMapper.insert(doctorRole);

        Role nurseRole = new Role();
        nurseRole.setName("NURSE");
        nurseRole.setDescription("护士角色");
        nurseRole.setCreatedAt(LocalDateTime.now());
        roleMapper.insert(nurseRole);

        // 创建医生用户
        User doctorUser = new User();
        doctorUser.setUsername("doctor001");
        doctorUser.setPassword("$2a$10$encrypted_password");
        doctorUser.setFullName("张医生");
        doctorUser.setEmail("doctor@clinic.com");
        doctorUser.setPhone("13800138001");
        doctorUser.setEnabled(true);
        doctorUser.setCreatedAt(LocalDateTime.now());
        userMapper.insert(doctorUser);

        // 创建护士用户
        User nurseUser = new User();
        nurseUser.setUsername("nurse001");
        nurseUser.setPassword("$2a$10$encrypted_password");
        nurseUser.setFullName("李护士");
        nurseUser.setEmail("nurse@clinic.com");
        nurseUser.setPhone("13800138002");
        nurseUser.setEnabled(true);
        nurseUser.setCreatedAt(LocalDateTime.now());
        userMapper.insert(nurseUser);

        // 创建医护人员
        Staff doctor = new Staff();
        doctor.setStaffNumber("DOC001");
        doctor.setName("张医生");
        doctor.setDepartment("内科");
        doctor.setPosition("主治医师");
        doctor.setQualification("执业医师");
        doctor.setPhone("13800138001");
        doctor.setEmail("doctor@clinic.com");
        doctor.setHireDate(LocalDate.now().minusYears(5));
        doctor.setActive(true);
        staffMapper.insert(doctor);
        doctorId = doctor.getId();

        Staff nurse = new Staff();
        nurse.setStaffNumber("NUR001");
        nurse.setName("李护士");
        nurse.setDepartment("护理部");
        nurse.setPosition("主管护师");
        nurse.setQualification("护士执业证");
        nurse.setPhone("13800138002");
        nurse.setEmail("nurse@clinic.com");
        nurse.setHireDate(LocalDate.now().minusYears(3));
        nurse.setActive(true);
        staffMapper.insert(nurse);
        nurseId = nurse.getId();

        // 创建测试药品
        Medicine medicine = new Medicine();
        medicine.setMedicineCode("MED001");
        medicine.setName("阿莫西林胶囊");
        medicine.setSpecification("0.25g*24粒");
        medicine.setManufacturer("华北制药");
        medicine.setBatchNumber("20240101");
        medicine.setExpiryDate(LocalDate.now().plusYears(2));
        medicine.setUnitPrice(new BigDecimal("15.50"));
        medicine.setStockQuantity(1000);
        medicine.setMinStockLevel(100);
        medicineMapper.insert(medicine);
    }

    /**
     * 测试完整的挂号到收费流程
     * 1. 患者挂号
     * 2. 护士分诊叫号
     * 3. 录入生命体征
     * 4. 医生诊疗开处方
     * 5. 费用计算
     * 6. 收费结算
     */
    @Test
    @Order(1)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCompleteRegistrationToBillingWorkflow() throws Exception {
        // 步骤1: 创建患者档案
        patientId = createPatient();
        assertNotNull(patientId, "患者创建失败");

        // 步骤2: 预约挂号
        appointmentId = createAppointment(patientId, doctorId);
        assertNotNull(appointmentId, "预约创建失败");

        // 步骤3: 挂号登记
        registrationId = createRegistration(patientId, appointmentId);
        assertNotNull(registrationId, "挂号登记失败");

        // 步骤4: 护士分诊叫号
        callPatientForTriage(patientId);

        // 步骤5: 录入生命体征
        recordVitalSigns(patientId, nurseId);

        // 步骤6: 医生创建病历
        medicalRecordId = createMedicalRecord(patientId, doctorId, registrationId);
        assertNotNull(medicalRecordId, "病历创建失败");

        // 步骤7: 开具处方
        prescriptionId = createPrescription(medicalRecordId, doctorId);
        assertNotNull(prescriptionId, "处方创建失败");

        // 步骤8: 费用计算
        billId = calculateBill(registrationId);
        assertNotNull(billId, "费用计算失败");

        // 步骤9: 收费结算
        processPayment(billId);

        // 验证整个流程的完整性
        verifyCompleteWorkflow();
    }

    private Long createPatient() throws Exception {
        CreatePatientRequest request = new CreatePatientRequest();
        request.setName("张三");
        request.setPhone("13800138888");
        request.setIdCard("110101199001011234");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setGender("男");
        request.setAddress("北京市朝阳区");

        MvcResult result = mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        PatientResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), PatientResponse.class);
        return response.getId();
    }

    private Long createAppointment(Long patientId, Long doctorId) throws Exception {
        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setAppointmentTime(LocalDateTime.now().plusHours(1));
        request.setAppointmentType("普通门诊");
        request.setNotes("常规检查");

        MvcResult result = mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Appointment appointment = objectMapper.readValue(responseBody, Appointment.class);
        return appointment.getId();
    }

    private Long createRegistration(Long patientId, Long appointmentId) throws Exception {
        CreateRegistrationRequest request = new CreateRegistrationRequest();
        request.setPatientId(patientId);
        request.setAppointmentId(appointmentId);
        request.setDepartment("内科");
        request.setRegistrationFee(new BigDecimal("10.00"));

        MvcResult result = mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Registration registration = objectMapper.readValue(responseBody, Registration.class);
        return registration.getId();
    }

    private void callPatientForTriage(Long patientId) throws Exception {
        CallPatientRequest request = new CallPatientRequest();
        request.setPatientId(patientId);

        mockMvc.perform(post("/api/triage/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 确认患者到达
        mockMvc.perform(post("/api/triage/confirm-arrival")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private void recordVitalSigns(Long patientId, Long nurseId) throws Exception {
        VitalSignsRequest request = new VitalSignsRequest();
        request.setPatientId(patientId);
        request.setRecordedBy(nurseId);
        request.setSystolicBP(120);
        request.setDiastolicBP(80);
        request.setTemperature(36.5);
        request.setHeartRate(72);
        request.setRespiratoryRate(18);

        mockMvc.perform(post("/api/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private Long createMedicalRecord(Long patientId, Long doctorId, Long registrationId) throws Exception {
        CreateMedicalRecordRequest request = new CreateMedicalRecordRequest();
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setRegistrationId(registrationId);
        request.setChiefComplaint("头痛、发热");
        request.setPresentIllness("患者3天前开始出现头痛，伴有发热，体温最高38.5°C");
        request.setPhysicalExamination("体温38.2°C，血压120/80mmHg，心率72次/分");
        request.setDiagnosis("上呼吸道感染");
        request.setTreatment("对症治疗，多休息，多饮水");

        MvcResult result = mockMvc.perform(post("/api/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        MedicalRecord record = objectMapper.readValue(responseBody, MedicalRecord.class);
        return record.getId();
    }

    private Long createPrescription(Long medicalRecordId, Long doctorId) throws Exception {
        // 创建处方的请求对象
        String prescriptionJson = """
            {
                "medicalRecordId": %d,
                "doctorId": %d,
                "items": [
                    {
                        "medicineId": 1,
                        "quantity": 2,
                        "dosage": "0.25g",
                        "frequency": "每日3次",
                        "duration": "7天",
                        "instructions": "饭后服用"
                    }
                ]
            }
            """.formatted(medicalRecordId, doctorId);

        MvcResult result = mockMvc.perform(post("/api/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(prescriptionJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Prescription prescription = objectMapper.readValue(responseBody, Prescription.class);
        return prescription.getId();
    }

    private Long calculateBill(Long registrationId) throws Exception {
        CreateBillRequest request = new CreateBillRequest();
        request.setRegistrationId(registrationId);

        MvcResult result = mockMvc.perform(post("/api/billing/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Bill bill = objectMapper.readValue(responseBody, Bill.class);
        return bill.getId();
    }

    private void processPayment(Long billId) throws Exception {
        String paymentJson = """
            {
                "billId": %d,
                "amount": 41.00,
                "paymentMethod": "现金",
                "transactionId": "TXN%d"
            }
            """.formatted(billId, System.currentTimeMillis());

        mockMvc.perform(post("/api/billing/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andExpect(status().isOk());
    }

    private void verifyCompleteWorkflow() throws Exception {
        // 验证患者信息
        mockMvc.perform(get("/api/patients/" + patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("张三"));

        // 验证挂号信息
        mockMvc.perform(get("/api/registrations/" + registrationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("已完成"));

        // 验证病历信息
        mockMvc.perform(get("/api/medical-records/" + medicalRecordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("上呼吸道感染"));

        // 验证账单信息
        mockMvc.perform(get("/api/billing/" + billId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("已支付"));
    }
}