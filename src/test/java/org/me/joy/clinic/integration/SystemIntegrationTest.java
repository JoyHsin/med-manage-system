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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 系统集成测试
 * 测试跨模块的系统集成和数据一致性
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 所有必要的Mapper
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

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private PatientQueueMapper patientQueueMapper;

    @Autowired
    private VitalSignsMapper vitalSignsMapper;

    @Autowired
    private MedicalOrderMapper medicalOrderMapper;

    // 测试数据ID存储
    private Long adminUserId;
    private Long doctorUserId;
    private Long nurseUserId;
    private Long pharmacistUserId;
    private Long receptionistUserId;

    private Long doctorStaffId;
    private Long nurseStaffId;
    private Long pharmacistStaffId;
    private Long receptionistStaffId;

    @BeforeEach
    void setUp() {
        setupCompleteTestEnvironment();
    }

    private void setupCompleteTestEnvironment() {
        // 创建所有角色
        createAllRoles();
        
        // 创建所有用户
        createAllUsers();
        
        // 创建所有员工
        createAllStaff();
        
        // 创建测试药品
        createTestMedicines();
    }

    private void createAllRoles() {
        String[] roleNames = {"ADMIN", "DOCTOR", "NURSE", "PHARMACIST", "RECEPTIONIST"};
        String[] roleDescriptions = {"系统管理员", "医生", "护士", "药剂师", "前台接待"};
        
        for (int i = 0; i < roleNames.length; i++) {
            Role role = new Role();
            role.setName(roleNames[i]);
            role.setDescription(roleDescriptions[i]);
            role.setCreatedAt(LocalDateTime.now());
            roleMapper.insert(role);
        }
    }

    private void createAllUsers() {
        // 管理员用户
        User admin = createUser("admin", "管理员", "admin@clinic.com", "13800000001");
        userMapper.insert(admin);
        adminUserId = admin.getId();

        // 医生用户
        User doctor = createUser("doctor001", "张医生", "doctor@clinic.com", "13800000002");
        userMapper.insert(doctor);
        doctorUserId = doctor.getId();

        // 护士用户
        User nurse = createUser("nurse001", "李护士", "nurse@clinic.com", "13800000003");
        userMapper.insert(nurse);
        nurseUserId = nurse.getId();

        // 药剂师用户
        User pharmacist = createUser("pharmacist001", "王药师", "pharmacist@clinic.com", "13800000004");
        userMapper.insert(pharmacist);
        pharmacistUserId = pharmacist.getId();

        // 前台用户
        User receptionist = createUser("receptionist001", "赵前台", "receptionist@clinic.com", "13800000005");
        userMapper.insert(receptionist);
        receptionistUserId = receptionist.getId();
    }

    private User createUser(String username, String fullName, String email, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("$2a$10$encrypted_password");
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private void createAllStaff() {
        // 医生
        Staff doctor = createStaff("DOC001", "张医生", "内科", "主治医师", "执业医师", "13800000002", "doctor@clinic.com");
        staffMapper.insert(doctor);
        doctorStaffId = doctor.getId();

        // 护士
        Staff nurse = createStaff("NUR001", "李护士", "护理部", "主管护师", "护士执业证", "13800000003", "nurse@clinic.com");
        staffMapper.insert(nurse);
        nurseStaffId = nurse.getId();

        // 药剂师
        Staff pharmacist = createStaff("PHA001", "王药师", "药房", "主管药师", "执业药师", "13800000004", "pharmacist@clinic.com");
        staffMapper.insert(pharmacist);
        pharmacistStaffId = pharmacist.getId();

        // 前台
        Staff receptionist = createStaff("REC001", "赵前台", "前台", "接待员", "岗位证书", "13800000005", "receptionist@clinic.com");
        staffMapper.insert(receptionist);
        receptionistStaffId = receptionist.getId();
    }

    private Staff createStaff(String staffNumber, String name, String department, String position, 
                             String qualification, String phone, String email) {
        Staff staff = new Staff();
        staff.setStaffNumber(staffNumber);
        staff.setName(name);
        staff.setDepartment(department);
        staff.setPosition(position);
        staff.setQualification(qualification);
        staff.setPhone(phone);
        staff.setEmail(email);
        staff.setHireDate(LocalDate.now().minusYears(2));
        staff.setActive(true);
        return staff;
    }

    private void createTestMedicines() {
        String[][] medicineData = {
            {"MED001", "阿莫西林胶囊", "0.25g*24粒", "华北制药", "20240101", "15.50", "1000", "100"},
            {"MED002", "布洛芬片", "0.2g*20片", "扬子江药业", "20240102", "8.80", "500", "50"},
            {"MED003", "维生素C片", "0.1g*100片", "石药集团", "20240103", "12.00", "800", "80"},
            {"MED004", "感冒灵颗粒", "10g*12袋", "同仁堂", "20240104", "25.00", "300", "30"},
            {"MED005", "头孢克肟胶囊", "0.1g*12粒", "齐鲁制药", "20240105", "32.50", "200", "20"}
        };

        for (String[] data : medicineData) {
            Medicine medicine = new Medicine();
            medicine.setMedicineCode(data[0]);
            medicine.setName(data[1]);
            medicine.setSpecification(data[2]);
            medicine.setManufacturer(data[3]);
            medicine.setBatchNumber(data[4]);
            medicine.setExpiryDate(LocalDate.now().plusYears(2));
            medicine.setUnitPrice(new BigDecimal(data[5]));
            medicine.setStockQuantity(Integer.parseInt(data[6]));
            medicine.setMinStockLevel(Integer.parseInt(data[7]));
            medicineMapper.insert(medicine);
        }
    }

    /**
     * 测试完整的诊所业务流程集成
     * 涵盖所有模块的协同工作
     */
    @Test
    @Order(1)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCompleteClinicWorkflowIntegration() throws Exception {
        // 步骤1: 患者建档和预约
        Long patientId = createPatientAndAppointment();
        
        // 步骤2: 前台挂号登记
        Long registrationId = performRegistration(patientId);
        
        // 步骤3: 护士分诊和生命体征录入
        performNurseWorkflow(patientId);
        
        // 步骤4: 医生诊疗和开处方
        Long medicalRecordId = performDoctorWorkflow(patientId, registrationId);
        
        // 步骤5: 费用计算和收费
        Long billId = performBillingWorkflow(registrationId);
        
        // 步骤6: 药房调剂
        performPharmacyWorkflow(medicalRecordId);
        
        // 步骤7: 数据分析和报表生成
        performAnalyticsWorkflow();
        
        // 验证整个流程的数据一致性
        verifyDataConsistency(patientId, registrationId, medicalRecordId, billId);
    }

    private Long createPatientAndAppointment() throws Exception {
        // 创建患者
        CreatePatientRequest patientRequest = new CreatePatientRequest();
        patientRequest.setName("张三");
        patientRequest.setPhone("13800138888");
        patientRequest.setIdCard("110101199001011234");
        patientRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        patientRequest.setGender("男");
        patientRequest.setAddress("北京市朝阳区");

        MvcResult patientResult = mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequest)))
                .andExpect(status().isOk())
                .andReturn();

        PatientResponse patientResponse = objectMapper.readValue(
                patientResult.getResponse().getContentAsString(), PatientResponse.class);
        Long patientId = patientResponse.getId();

        // 创建预约
        CreateAppointmentRequest appointmentRequest = new CreateAppointmentRequest();
        appointmentRequest.setPatientId(patientId);
        appointmentRequest.setDoctorId(doctorStaffId);
        appointmentRequest.setAppointmentTime(LocalDateTime.now().plusHours(1));
        appointmentRequest.setAppointmentType("普通门诊");
        appointmentRequest.setNotes("常规检查");

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isOk());

        return patientId;
    }

    private Long performRegistration(Long patientId) throws Exception {
        CreateRegistrationRequest registrationRequest = new CreateRegistrationRequest();
        registrationRequest.setPatientId(patientId);
        registrationRequest.setDepartment("内科");
        registrationRequest.setRegistrationFee(new BigDecimal("10.00"));

        MvcResult result = mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Registration registration = objectMapper.readValue(
                result.getResponse().getContentAsString(), Registration.class);
        return registration.getId();
    }

    private void performNurseWorkflow(Long patientId) throws Exception {
        // 分诊叫号
        CallPatientRequest callRequest = new CallPatientRequest();
        callRequest.setPatientId(patientId);

        mockMvc.perform(post("/api/triage/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(callRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/triage/confirm-arrival")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(callRequest)))
                .andExpect(status().isOk());

        // 录入生命体征
        VitalSignsRequest vitalSignsRequest = new VitalSignsRequest();
        vitalSignsRequest.setPatientId(patientId);
        vitalSignsRequest.setRecordedBy(nurseStaffId);
        vitalSignsRequest.setSystolicBP(120);
        vitalSignsRequest.setDiastolicBP(80);
        vitalSignsRequest.setTemperature(36.5);
        vitalSignsRequest.setHeartRate(72);
        vitalSignsRequest.setRespiratoryRate(18);

        mockMvc.perform(post("/api/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vitalSignsRequest)))
                .andExpect(status().isOk());
    }

    private Long performDoctorWorkflow(Long patientId, Long registrationId) throws Exception {
        // 创建病历
        CreateMedicalRecordRequest recordRequest = new CreateMedicalRecordRequest();
        recordRequest.setPatientId(patientId);
        recordRequest.setDoctorId(doctorStaffId);
        recordRequest.setRegistrationId(registrationId);
        recordRequest.setChiefComplaint("头痛、发热");
        recordRequest.setPresentIllness("患者3天前开始出现头痛，伴有发热");
        recordRequest.setPhysicalExamination("体温36.5°C，血压120/80mmHg");
        recordRequest.setDiagnosis("上呼吸道感染");
        recordRequest.setTreatment("对症治疗，多休息");

        MvcResult recordResult = mockMvc.perform(post("/api/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recordRequest)))
                .andExpect(status().isOk())
                .andReturn();

        MedicalRecord record = objectMapper.readValue(
                recordResult.getResponse().getContentAsString(), MedicalRecord.class);
        Long medicalRecordId = record.getId();

        // 开具处方
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
                    },
                    {
                        "medicineId": 2,
                        "quantity": 1,
                        "dosage": "0.2g",
                        "frequency": "每日2次",
                        "duration": "5天",
                        "instructions": "饭后服用"
                    }
                ]
            }
            """.formatted(medicalRecordId, doctorStaffId);

        mockMvc.perform(post("/api/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(prescriptionJson))
                .andExpect(status().isOk());

        // 开具医嘱
        CreateMedicalOrderRequest orderRequest = new CreateMedicalOrderRequest();
        orderRequest.setPatientId(patientId);
        orderRequest.setPrescribedBy(doctorStaffId);
        orderRequest.setOrderType("注射");
        orderRequest.setContent("维生素C注射液");
        orderRequest.setDosage("2ml");
        orderRequest.setFrequency("每日1次");

        mockMvc.perform(post("/api/medical-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk());

        return medicalRecordId;
    }

    private Long performBillingWorkflow(Long registrationId) throws Exception {
        // 计算费用
        CreateBillRequest billRequest = new CreateBillRequest();
        billRequest.setRegistrationId(registrationId);

        MvcResult billResult = mockMvc.perform(post("/api/billing/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(billRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Bill bill = objectMapper.readValue(
                billResult.getResponse().getContentAsString(), Bill.class);
        Long billId = bill.getId();

        // 处理支付
        String paymentJson = """
            {
                "billId": %d,
                "amount": %s,
                "paymentMethod": "现金",
                "transactionId": "TXN%d"
            }
            """.formatted(billId, bill.getTotalAmount(), System.currentTimeMillis());

        mockMvc.perform(post("/api/billing/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentJson))
                .andExpect(status().isOk());

        return billId;
    }

    private void performPharmacyWorkflow(Long medicalRecordId) throws Exception {
        // 获取处方
        MvcResult prescriptionResult = mockMvc.perform(get("/api/prescriptions/medical-record/" + medicalRecordId))
                .andExpect(status().isOk())
                .andReturn();

        // 调剂药品
        String dispenseJson = """
            {
                "prescriptionId": 1,
                "pharmacistId": %d,
                "items": [
                    {
                        "medicineId": 1,
                        "dispensedQuantity": 2,
                        "batchNumber": "20240101",
                        "expiryDate": "2026-01-01"
                    },
                    {
                        "medicineId": 2,
                        "dispensedQuantity": 1,
                        "batchNumber": "20240102",
                        "expiryDate": "2027-01-02"
                    }
                ],
                "notes": "正常调剂"
            }
            """.formatted(pharmacistStaffId);

        mockMvc.perform(post("/api/pharmacy/dispense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dispenseJson))
                .andExpect(status().isOk());
    }

    private void performAnalyticsWorkflow() throws Exception {
        // 生成日报
        mockMvc.perform(get("/api/reports/financial/daily?date=" + LocalDate.now()))
                .andExpect(status().isOk());

        // 生成运营分析
        mockMvc.perform(get("/api/analytics/operational/patient-visits?startDate=" + LocalDate.now() + "&endDate=" + LocalDate.now()))
                .andExpect(status().isOk());

        // 生成患者分析
        mockMvc.perform(get("/api/analytics/patient/demographics"))
                .andExpect(status().isOk());
    }

    private void verifyDataConsistency(Long patientId, Long registrationId, Long medicalRecordId, Long billId) throws Exception {
        // 验证患者数据一致性
        mockMvc.perform(get("/api/patients/" + patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("张三"));

        // 验证挂号数据一致性
        mockMvc.perform(get("/api/registrations/" + registrationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(patientId));

        // 验证病历数据一致性
        mockMvc.perform(get("/api/medical-records/" + medicalRecordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(patientId))
                .andExpect(jsonPath("$.registrationId").value(registrationId));

        // 验证账单数据一致性
        mockMvc.perform(get("/api/billing/" + billId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationId").value(registrationId))
                .andExpect(jsonPath("$.status").value("已支付"));

        // 验证生命体征记录
        mockMvc.perform(get("/api/vital-signs/patient/" + patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(patientId))
                .andExpect(jsonPath("$[0].recordedBy").value(nurseStaffId));

        // 验证库存更新
        mockMvc.perform(get("/api/stock/level/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStock").value(998)); // 1000 - 2

        mockMvc.perform(get("/api/stock/level/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStock").value(499)); // 500 - 1
    }

    /**
     * 测试系统并发处理能力
     */
    @Test
    @Order(2)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSystemConcurrency() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 并发创建多个患者
        for (int i = 0; i < 10; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    createConcurrentPatient(index);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);
            futures.add(future);
        }

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 验证并发创建结果
        MvcResult result = mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andReturn();

        // 验证创建了正确数量的患者
        String responseBody = result.getResponse().getContentAsString();
        List<?> patients = objectMapper.readValue(responseBody, List.class);
        assertTrue(patients.size() >= 10, "应该创建了至少10个患者");

        executor.shutdown();
    }

    private void createConcurrentPatient(int index) throws Exception {
        CreatePatientRequest request = new CreatePatientRequest();
        request.setName("并发患者" + index);
        request.setPhone("1380013" + String.format("%04d", index));
        request.setIdCard("11010119900101" + String.format("%04d", index));
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setGender(index % 2 == 0 ? "男" : "女");
        request.setAddress("北京市朝阳区" + index + "号");

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试系统错误恢复能力
     */
    @Test
    @Order(3)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSystemErrorRecovery() throws Exception {
        // 测试数据库连接异常恢复
        testDatabaseErrorRecovery();

        // 测试业务逻辑异常处理
        testBusinessLogicErrorHandling();

        // 测试系统资源不足处理
        testResourceLimitHandling();
    }

    private void testDatabaseErrorRecovery() throws Exception {
        // 尝试创建重复的患者ID
        CreatePatientRequest request1 = new CreatePatientRequest();
        request1.setName("测试患者1");
        request1.setPhone("13800000001");
        request1.setIdCard("110101199001011111");
        request1.setBirthDate(LocalDate.of(1990, 1, 1));
        request1.setGender("男");
        request1.setAddress("北京市");

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // 尝试创建相同身份证的患者（应该失败）
        CreatePatientRequest request2 = new CreatePatientRequest();
        request2.setName("测试患者2");
        request2.setPhone("13800000002");
        request2.setIdCard("110101199001011111"); // 相同身份证
        request2.setBirthDate(LocalDate.of(1990, 1, 1));
        request2.setGender("女");
        request2.setAddress("上海市");

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }

    private void testBusinessLogicErrorHandling() throws Exception {
        // 测试无效的生命体征数据
        VitalSignsRequest invalidVitalSigns = new VitalSignsRequest();
        invalidVitalSigns.setPatientId(1L);
        invalidVitalSigns.setRecordedBy(nurseStaffId);
        invalidVitalSigns.setSystolicBP(-10); // 无效血压
        invalidVitalSigns.setDiastolicBP(300); // 无效血压
        invalidVitalSigns.setTemperature(50.0); // 无效体温
        invalidVitalSigns.setHeartRate(500); // 无效心率
        invalidVitalSigns.setRespiratoryRate(200); // 无效呼吸频率

        mockMvc.perform(post("/api/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidVitalSigns)))
                .andExpect(status().isBadRequest());
    }

    private void testResourceLimitHandling() throws Exception {
        // 测试库存不足情况
        String insufficientStockDispense = """
            {
                "prescriptionId": 1,
                "pharmacistId": %d,
                "items": [
                    {
                        "medicineId": 1,
                        "dispensedQuantity": 10000,
                        "batchNumber": "20240101",
                        "expiryDate": "2026-01-01"
                    }
                ]
            }
            """.formatted(pharmacistStaffId);

        mockMvc.perform(post("/api/pharmacy/dispense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(insufficientStockDispense))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("库存不足")));
    }

    /**
     * 测试系统性能指标
     */
    @Test
    @Order(4)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSystemPerformance() throws Exception {
        // 测试响应时间
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk());
        
        long responseTime = System.currentTimeMillis() - startTime;
        assertTrue(responseTime < 1000, "患者查询响应时间应小于1秒，实际: " + responseTime + "ms");

        // 测试批量操作性能
        startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 50; i++) {
            CreatePatientRequest request = new CreatePatientRequest();
            request.setName("性能测试患者" + i);
            request.setPhone("1380000" + String.format("%04d", i));
            request.setIdCard("11010119900101" + String.format("%04d", i + 1000));
            request.setBirthDate(LocalDate.of(1990, 1, 1));
            request.setGender(i % 2 == 0 ? "男" : "女");
            request.setAddress("性能测试地址" + i);

            mockMvc.perform(post("/api/patients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
        
        long batchTime = System.currentTimeMillis() - startTime;
        assertTrue(batchTime < 10000, "批量创建50个患者应小于10秒，实际: " + batchTime + "ms");
        
        System.out.println("性能测试结果:");
        System.out.println("- 单次查询响应时间: " + responseTime + "ms");
        System.out.println("- 批量创建50个患者耗时: " + batchTime + "ms");
        System.out.println("- 平均每个患者创建耗时: " + (batchTime / 50) + "ms");
    }
}