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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 护士工作台端到端集成测试
 * 测试护士工作台的完整业务流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class NurseWorkstationIntegrationTest {

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
    private PatientQueueMapper patientQueueMapper;

    @Autowired
    private MedicalOrderMapper medicalOrderMapper;

    @Autowired
    private VitalSignsMapper vitalSignsMapper;

    // 测试数据
    private Long nurseId;
    private Long doctorId;
    private Long patientId1;
    private Long patientId2;
    private Long patientId3;
    private Long medicalOrderId1;
    private Long medicalOrderId2;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        // 创建角色
        Role nurseRole = new Role();
        nurseRole.setName("NURSE");
        nurseRole.setDescription("护士角色");
        nurseRole.setCreatedAt(LocalDateTime.now());
        roleMapper.insert(nurseRole);

        Role doctorRole = new Role();
        doctorRole.setName("DOCTOR");
        doctorRole.setDescription("医生角色");
        doctorRole.setCreatedAt(LocalDateTime.now());
        roleMapper.insert(doctorRole);

        // 创建护士用户
        User nurseUser = new User();
        nurseUser.setUsername("nurse001");
        nurseUser.setPassword("$2a$10$encrypted_password");
        nurseUser.setFullName("李护士");
        nurseUser.setEmail("nurse@clinic.com");
        nurseUser.setPhone("13800138001");
        nurseUser.setEnabled(true);
        nurseUser.setCreatedAt(LocalDateTime.now());
        userMapper.insert(nurseUser);

        // 创建医生用户
        User doctorUser = new User();
        doctorUser.setUsername("doctor001");
        doctorUser.setPassword("$2a$10$encrypted_password");
        doctorUser.setFullName("张医生");
        doctorUser.setEmail("doctor@clinic.com");
        doctorUser.setPhone("13800138002");
        doctorUser.setEnabled(true);
        doctorUser.setCreatedAt(LocalDateTime.now());
        userMapper.insert(doctorUser);

        // 创建护士
        Staff nurse = new Staff();
        nurse.setStaffNumber("NUR001");
        nurse.setName("李护士");
        nurse.setDepartment("护理部");
        nurse.setPosition("主管护师");
        nurse.setQualification("护士执业证");
        nurse.setPhone("13800138001");
        nurse.setEmail("nurse@clinic.com");
        nurse.setHireDate(LocalDate.now().minusYears(3));
        nurse.setActive(true);
        staffMapper.insert(nurse);
        nurseId = nurse.getId();

        // 创建医生
        Staff doctor = new Staff();
        doctor.setStaffNumber("DOC001");
        doctor.setName("张医生");
        doctor.setDepartment("内科");
        doctor.setPosition("主治医师");
        doctor.setQualification("执业医师");
        doctor.setPhone("13800138002");
        doctor.setEmail("doctor@clinic.com");
        doctor.setHireDate(LocalDate.now().minusYears(5));
        doctor.setActive(true);
        staffMapper.insert(doctor);
        doctorId = doctor.getId();

        // 创建测试患者
        createTestPatients();
        
        // 创建患者队列
        createPatientQueue();
        
        // 创建医嘱
        createMedicalOrders();
    }

    private void createTestPatients() {
        // 患者1
        Patient patient1 = new Patient();
        patient1.setPatientNumber("P001");
        patient1.setName("张三");
        patient1.setPhone("13800138888");
        patient1.setIdCard("110101199001011234");
        patient1.setBirthDate(LocalDate.of(1990, 1, 1));
        patient1.setGender("男");
        patient1.setAddress("北京市朝阳区");
        patient1.setCreatedAt(LocalDateTime.now());
        patientMapper.insert(patient1);
        patientId1 = patient1.getId();

        // 患者2
        Patient patient2 = new Patient();
        patient2.setPatientNumber("P002");
        patient2.setName("李四");
        patient2.setPhone("13800138889");
        patient2.setIdCard("110101199002021234");
        patient2.setBirthDate(LocalDate.of(1990, 2, 2));
        patient2.setGender("女");
        patient2.setAddress("北京市海淀区");
        patient2.setCreatedAt(LocalDateTime.now());
        patientMapper.insert(patient2);
        patientId2 = patient2.getId();

        // 患者3
        Patient patient3 = new Patient();
        patient3.setPatientNumber("P003");
        patient3.setName("王五");
        patient3.setPhone("13800138890");
        patient3.setIdCard("110101199003031234");
        patient3.setBirthDate(LocalDate.of(1990, 3, 3));
        patient3.setGender("男");
        patient3.setAddress("北京市西城区");
        patient3.setCreatedAt(LocalDateTime.now());
        patientMapper.insert(patient3);
        patientId3 = patient3.getId();
    }

    private void createPatientQueue() {
        // 患者1排队
        PatientQueue queue1 = new PatientQueue();
        queue1.setPatientId(patientId1);
        queue1.setQueueDate(LocalDate.now());
        queue1.setQueueNumber(1);
        queue1.setStatus("等待叫号");
        patientQueueMapper.insert(queue1);

        // 患者2排队
        PatientQueue queue2 = new PatientQueue();
        queue2.setPatientId(patientId2);
        queue2.setQueueDate(LocalDate.now());
        queue2.setQueueNumber(2);
        queue2.setStatus("等待叫号");
        patientQueueMapper.insert(queue2);

        // 患者3排队
        PatientQueue queue3 = new PatientQueue();
        queue3.setPatientId(patientId3);
        queue3.setQueueDate(LocalDate.now());
        queue3.setQueueNumber(3);
        queue3.setStatus("等待叫号");
        patientQueueMapper.insert(queue3);
    }

    private void createMedicalOrders() {
        // 医嘱1 - 注射类
        MedicalOrder order1 = new MedicalOrder();
        order1.setPatientId(patientId1);
        order1.setPrescribedBy(doctorId);
        order1.setOrderType("注射");
        order1.setContent("青霉素注射液");
        order1.setDosage("80万单位");
        order1.setFrequency("每日2次");
        order1.setStatus("待执行");
        order1.setPrescribedAt(LocalDateTime.now());
        medicalOrderMapper.insert(order1);
        medicalOrderId1 = order1.getId();

        // 医嘱2 - 测量类
        MedicalOrder order2 = new MedicalOrder();
        order2.setPatientId(patientId2);
        order2.setPrescribedBy(doctorId);
        order2.setOrderType("测量");
        order2.setContent("血压测量");
        order2.setDosage("");
        order2.setFrequency("每4小时1次");
        order2.setStatus("待执行");
        order2.setPrescribedAt(LocalDateTime.now());
        medicalOrderMapper.insert(order2);
        medicalOrderId2 = order2.getId();
    }

    /**
     * 测试护士工作台完整流程
     * 1. 查看当日患者队列
     * 2. 分诊叫号
     * 3. 录入生命体征
     * 4. 执行医嘱
     * 5. 查看执行历史
     */
    @Test
    @Order(1)
    @WithMockUser(username = "nurse001", roles = {"NURSE"})
    void testCompleteNurseWorkstationWorkflow() throws Exception {
        // 步骤1: 查看当日患者队列
        verifyPatientQueue();

        // 步骤2: 分诊叫号流程
        performTriageWorkflow();

        // 步骤3: 录入生命体征
        recordVitalSignsForPatients();

        // 步骤4: 执行医嘱
        executeMedicalOrders();

        // 步骤5: 查看执行历史
        verifyExecutionHistory();

        // 验证整个护士工作台流程
        verifyNurseWorkstationWorkflow();
    }

    private void verifyPatientQueue() throws Exception {
        // 获取当日患者队列
        MvcResult result = mockMvc.perform(get("/api/triage/queue/today"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        List<?> queueList = objectMapper.readValue(responseBody, List.class);
        
        assertEquals(3, queueList.size(), "当日队列应有3个患者");
    }

    private void performTriageWorkflow() throws Exception {
        // 叫号患者1
        CallPatientRequest callRequest1 = new CallPatientRequest();
        callRequest1.setPatientId(patientId1);

        mockMvc.perform(post("/api/triage/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(callRequest1)))
                .andExpect(status().isOk());

        // 确认患者1到达
        mockMvc.perform(post("/api/triage/confirm-arrival")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(callRequest1)))
                .andExpect(status().isOk());

        // 叫号患者2
        CallPatientRequest callRequest2 = new CallPatientRequest();
        callRequest2.setPatientId(patientId2);

        mockMvc.perform(post("/api/triage/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(callRequest2)))
                .andExpect(status().isOk());

        // 确认患者2到达
        mockMvc.perform(post("/api/triage/confirm-arrival")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(callRequest2)))
                .andExpect(status().isOk());

        // 患者3未响应叫号
        CallPatientRequest callRequest3 = new CallPatientRequest();
        callRequest3.setPatientId(patientId3);

        mockMvc.perform(post("/api/triage/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(callRequest3)))
                .andExpect(status().isOk());

        // 标记患者3为未到
        mockMvc.perform(post("/api/triage/mark-absent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(callRequest3)))
                .andExpect(status().isOk());
    }

    private void recordVitalSignsForPatients() throws Exception {
        // 为患者1录入生命体征
        VitalSignsRequest vitalSigns1 = new VitalSignsRequest();
        vitalSigns1.setPatientId(patientId1);
        vitalSigns1.setRecordedBy(nurseId);
        vitalSigns1.setSystolicBP(130);
        vitalSigns1.setDiastolicBP(85);
        vitalSigns1.setTemperature(37.2);
        vitalSigns1.setHeartRate(88);
        vitalSigns1.setRespiratoryRate(20);

        mockMvc.perform(post("/api/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vitalSigns1)))
                .andExpect(status().isOk());

        // 为患者2录入生命体征（正常值）
        VitalSignsRequest vitalSigns2 = new VitalSignsRequest();
        vitalSigns2.setPatientId(patientId2);
        vitalSigns2.setRecordedBy(nurseId);
        vitalSigns2.setSystolicBP(120);
        vitalSigns2.setDiastolicBP(80);
        vitalSigns2.setTemperature(36.5);
        vitalSigns2.setHeartRate(72);
        vitalSigns2.setRespiratoryRate(18);

        mockMvc.perform(post("/api/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vitalSigns2)))
                .andExpect(status().isOk());

        // 验证异常数据警告（患者1的体温偏高）
        mockMvc.perform(get("/api/vital-signs/patient/" + patientId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].temperature").value(37.2));
    }

    private void executeMedicalOrders() throws Exception {
        // 执行注射类医嘱
        ExecuteMedicalOrderRequest executeRequest1 = new ExecuteMedicalOrderRequest();
        executeRequest1.setOrderId(medicalOrderId1);
        executeRequest1.setExecutedBy(nurseId);
        executeRequest1.setExecutionNotes("左臀部肌肉注射，患者无不良反应");
        executeRequest1.setInjectionSite("左臀部");

        mockMvc.perform(post("/api/medical-orders/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(executeRequest1)))
                .andExpect(status().isOk());

        // 执行测量类医嘱
        ExecuteMedicalOrderRequest executeRequest2 = new ExecuteMedicalOrderRequest();
        executeRequest2.setOrderId(medicalOrderId2);
        executeRequest2.setExecutedBy(nurseId);
        executeRequest2.setExecutionNotes("血压130/85mmHg，略偏高");
        executeRequest2.setMeasurementResult("130/85mmHg");

        mockMvc.perform(post("/api/medical-orders/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(executeRequest2)))
                .andExpect(status().isOk());

        // 测试暂缓执行医嘱的情况
        MedicalOrder postponeOrder = new MedicalOrder();
        postponeOrder.setPatientId(patientId3);
        postponeOrder.setPrescribedBy(doctorId);
        postponeOrder.setOrderType("注射");
        postponeOrder.setContent("胰岛素注射");
        postponeOrder.setDosage("10单位");
        postponeOrder.setFrequency("餐前30分钟");
        postponeOrder.setStatus("待执行");
        postponeOrder.setPrescribedAt(LocalDateTime.now());
        medicalOrderMapper.insert(postponeOrder);

        String postponeJson = """
            {
                "orderId": %d,
                "reason": "患者血糖过低，暂缓执行"
            }
            """.formatted(postponeOrder.getId());

        mockMvc.perform(post("/api/medical-orders/postpone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postponeJson))
                .andExpect(status().isOk());
    }

    private void verifyExecutionHistory() throws Exception {
        // 查看患者1的医嘱执行历史
        mockMvc.perform(get("/api/medical-orders/patient/" + patientId1 + "/executed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("已执行"))
                .andExpect(jsonPath("$[0].executedBy").value(nurseId));

        // 查看患者2的医嘱执行历史
        mockMvc.perform(get("/api/medical-orders/patient/" + patientId2 + "/executed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("已执行"));

        // 查看所有待执行医嘱
        mockMvc.perform(get("/api/medical-orders/pending"))
                .andExpect(status().isOk());
    }

    private void verifyNurseWorkstationWorkflow() throws Exception {
        // 验证患者队列状态更新
        mockMvc.perform(get("/api/triage/queue/today"))
                .andExpect(status().isOk());

        // 验证生命体征记录
        mockMvc.perform(get("/api/vital-signs/patient/" + patientId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recordedBy").value(nurseId));

        mockMvc.perform(get("/api/vital-signs/patient/" + patientId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recordedBy").value(nurseId));

        // 验证医嘱执行状态
        mockMvc.perform(get("/api/medical-orders/" + medicalOrderId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("已执行"))
                .andExpect(jsonPath("$.executedBy").value(nurseId));

        mockMvc.perform(get("/api/medical-orders/" + medicalOrderId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("已执行"))
                .andExpect(jsonPath("$.executedBy").value(nurseId));
    }

    /**
     * 测试护士工作台异常情况处理
     */
    @Test
    @Order(2)
    @WithMockUser(username = "nurse001", roles = {"NURSE"})
    void testNurseWorkstationExceptionHandling() throws Exception {
        // 测试录入异常生命体征数据
        testAbnormalVitalSigns();

        // 测试医嘱执行异常情况
        testMedicalOrderExecutionExceptions();
    }

    private void testAbnormalVitalSigns() throws Exception {
        // 录入异常生命体征（血压过高）
        VitalSignsRequest abnormalVitalSigns = new VitalSignsRequest();
        abnormalVitalSigns.setPatientId(patientId1);
        abnormalVitalSigns.setRecordedBy(nurseId);
        abnormalVitalSigns.setSystolicBP(180); // 异常高血压
        abnormalVitalSigns.setDiastolicBP(110);
        abnormalVitalSigns.setTemperature(39.5); // 高热
        abnormalVitalSigns.setHeartRate(120); // 心动过速
        abnormalVitalSigns.setRespiratoryRate(30); // 呼吸急促

        // 系统应该接受数据但给出警告
        mockMvc.perform(post("/api/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(abnormalVitalSigns)))
                .andExpect(status().isOk());

        // 验证数据验证
        VitalSignsRequest invalidVitalSigns = new VitalSignsRequest();
        invalidVitalSigns.setPatientId(patientId1);
        invalidVitalSigns.setRecordedBy(nurseId);
        invalidVitalSigns.setSystolicBP(350); // 超出正常范围
        invalidVitalSigns.setDiastolicBP(-10); // 负值
        invalidVitalSigns.setTemperature(50.0); // 不可能的体温
        invalidVitalSigns.setHeartRate(300); // 不可能的心率
        invalidVitalSigns.setRespiratoryRate(100); // 不可能的呼吸频率

        mockMvc.perform(post("/api/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidVitalSigns)))
                .andExpect(status().isBadRequest());
    }

    private void testMedicalOrderExecutionExceptions() throws Exception {
        // 创建测试医嘱
        MedicalOrder testOrder = new MedicalOrder();
        testOrder.setPatientId(patientId1);
        testOrder.setPrescribedBy(doctorId);
        testOrder.setOrderType("注射");
        testOrder.setContent("测试药物");
        testOrder.setDosage("1ml");
        testOrder.setFrequency("单次");
        testOrder.setStatus("待执行");
        testOrder.setPrescribedAt(LocalDateTime.now());
        medicalOrderMapper.insert(testOrder);

        // 测试重复执行医嘱
        ExecuteMedicalOrderRequest executeRequest = new ExecuteMedicalOrderRequest();
        executeRequest.setOrderId(testOrder.getId());
        executeRequest.setExecutedBy(nurseId);
        executeRequest.setExecutionNotes("首次执行");

        // 第一次执行应该成功
        mockMvc.perform(post("/api/medical-orders/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(executeRequest)))
                .andExpect(status().isOk());

        // 第二次执行应该失败
        executeRequest.setExecutionNotes("重复执行");
        mockMvc.perform(post("/api/medical-orders/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(executeRequest)))
                .andExpect(status().isBadRequest());

        // 测试执行不存在的医嘱
        ExecuteMedicalOrderRequest invalidRequest = new ExecuteMedicalOrderRequest();
        invalidRequest.setOrderId(99999L);
        invalidRequest.setExecutedBy(nurseId);
        invalidRequest.setExecutionNotes("不存在的医嘱");

        mockMvc.perform(post("/api/medical-orders/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound());
    }
}