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
 * 药房管理端到端集成测试
 * 测试药房管理的完整业务流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class PharmacyManagementIntegrationTest {

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

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private PrescriptionItemMapper prescriptionItemMapper;

    @Autowired
    private DispenseRecordMapper dispenseRecordMapper;

    @Autowired
    private DispenseItemMapper dispenseItemMapper;

    @Autowired
    private StockTransactionMapper stockTransactionMapper;

    @Autowired
    private InventoryLevelMapper inventoryLevelMapper;

    // 测试数据
    private Long pharmacistId;
    private Long doctorId;
    private Long patientId1;
    private Long patientId2;
    private Long medicineId1;
    private Long medicineId2;
    private Long medicineId3;
    private Long prescriptionId1;
    private Long prescriptionId2;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        // 创建角色
        Role pharmacistRole = new Role();
        pharmacistRole.setName("PHARMACIST");
        pharmacistRole.setDescription("药剂师角色");
        pharmacistRole.setCreatedAt(LocalDateTime.now());
        roleMapper.insert(pharmacistRole);

        Role doctorRole = new Role();
        doctorRole.setName("DOCTOR");
        doctorRole.setDescription("医生角色");
        doctorRole.setCreatedAt(LocalDateTime.now());
        roleMapper.insert(doctorRole);

        // 创建药剂师用户
        User pharmacistUser = new User();
        pharmacistUser.setUsername("pharmacist001");
        pharmacistUser.setPassword("$2a$10$encrypted_password");
        pharmacistUser.setFullName("王药师");
        pharmacistUser.setEmail("pharmacist@clinic.com");
        pharmacistUser.setPhone("13800138001");
        pharmacistUser.setEnabled(true);
        pharmacistUser.setCreatedAt(LocalDateTime.now());
        userMapper.insert(pharmacistUser);

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

        // 创建药剂师
        Staff pharmacist = new Staff();
        pharmacist.setStaffNumber("PHA001");
        pharmacist.setName("王药师");
        pharmacist.setDepartment("药房");
        pharmacist.setPosition("主管药师");
        pharmacist.setQualification("执业药师");
        pharmacist.setPhone("13800138001");
        pharmacist.setEmail("pharmacist@clinic.com");
        pharmacist.setHireDate(LocalDate.now().minusYears(8));
        pharmacist.setActive(true);
        staffMapper.insert(pharmacist);
        pharmacistId = pharmacist.getId();

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
        
        // 创建测试药品
        createTestMedicines();
        
        // 创建测试处方
        createTestPrescriptions();
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
    }

    private void createTestMedicines() {
        // 药品1 - 阿莫西林胶囊
        Medicine medicine1 = new Medicine();
        medicine1.setMedicineCode("MED001");
        medicine1.setName("阿莫西林胶囊");
        medicine1.setSpecification("0.25g*24粒");
        medicine1.setManufacturer("华北制药");
        medicine1.setBatchNumber("20240101");
        medicine1.setExpiryDate(LocalDate.now().plusYears(2));
        medicine1.setUnitPrice(new BigDecimal("15.50"));
        medicine1.setStockQuantity(1000);
        medicine1.setMinStockLevel(100);
        medicineMapper.insert(medicine1);
        medicineId1 = medicine1.getId();

        // 药品2 - 布洛芬片
        Medicine medicine2 = new Medicine();
        medicine2.setMedicineCode("MED002");
        medicine2.setName("布洛芬片");
        medicine2.setSpecification("0.2g*20片");
        medicine2.setManufacturer("扬子江药业");
        medicine2.setBatchNumber("20240102");
        medicine2.setExpiryDate(LocalDate.now().plusYears(3));
        medicine2.setUnitPrice(new BigDecimal("8.80"));
        medicine2.setStockQuantity(500);
        medicine2.setMinStockLevel(50);
        medicineMapper.insert(medicine2);
        medicineId2 = medicine2.getId();

        // 药品3 - 维生素C片（库存不足）
        Medicine medicine3 = new Medicine();
        medicine3.setMedicineCode("MED003");
        medicine3.setName("维生素C片");
        medicine3.setSpecification("0.1g*100片");
        medicine3.setManufacturer("石药集团");
        medicine3.setBatchNumber("20240103");
        medicine3.setExpiryDate(LocalDate.now().plusYears(2));
        medicine3.setUnitPrice(new BigDecimal("12.00"));
        medicine3.setStockQuantity(20); // 库存不足
        medicine3.setMinStockLevel(50);
        medicineMapper.insert(medicine3);
        medicineId3 = medicine3.getId();

        // 创建库存记录
        createInventoryLevels();
    }

    private void createInventoryLevels() {
        // 为每个药品创建库存记录
        InventoryLevel level1 = new InventoryLevel();
        level1.setMedicineId(medicineId1);
        level1.setCurrentStock(1000);
        level1.setMinLevel(100);
        level1.setMaxLevel(2000);
        level1.setLastUpdated(LocalDateTime.now());
        inventoryLevelMapper.insert(level1);

        InventoryLevel level2 = new InventoryLevel();
        level2.setMedicineId(medicineId2);
        level2.setCurrentStock(500);
        level2.setMinLevel(50);
        level2.setMaxLevel(1000);
        level2.setLastUpdated(LocalDateTime.now());
        inventoryLevelMapper.insert(level2);

        InventoryLevel level3 = new InventoryLevel();
        level3.setMedicineId(medicineId3);
        level3.setCurrentStock(20);
        level3.setMinLevel(50);
        level3.setMaxLevel(500);
        level3.setLastUpdated(LocalDateTime.now());
        inventoryLevelMapper.insert(level3);
    }

    private void createTestPrescriptions() {
        // 处方1 - 患者1
        Prescription prescription1 = new Prescription();
        prescription1.setMedicalRecordId(1L);
        prescription1.setDoctorId(doctorId);
        prescription1.setPrescriptionNumber("RX001");
        prescription1.setStatus("待调剂");
        prescription1.setPrescribedAt(LocalDateTime.now());
        prescriptionMapper.insert(prescription1);
        prescriptionId1 = prescription1.getId();

        // 处方1的药品项目
        PrescriptionItem item1 = new PrescriptionItem();
        item1.setPrescriptionId(prescriptionId1);
        item1.setMedicineId(medicineId1);
        item1.setQuantity(2); // 2盒
        item1.setDosage("0.25g");
        item1.setFrequency("每日3次");
        item1.setDuration("7天");
        item1.setInstructions("饭后服用");
        prescriptionItemMapper.insert(item1);

        PrescriptionItem item2 = new PrescriptionItem();
        item2.setPrescriptionId(prescriptionId1);
        item2.setMedicineId(medicineId2);
        item2.setQuantity(1); // 1盒
        item2.setDosage("0.2g");
        item2.setFrequency("每日2次");
        item2.setDuration("5天");
        item2.setInstructions("饭后服用");
        prescriptionItemMapper.insert(item2);

        // 处方2 - 患者2（包含库存不足的药品）
        Prescription prescription2 = new Prescription();
        prescription2.setMedicalRecordId(2L);
        prescription2.setDoctorId(doctorId);
        prescription2.setPrescriptionNumber("RX002");
        prescription2.setStatus("待调剂");
        prescription2.setPrescribedAt(LocalDateTime.now());
        prescriptionMapper.insert(prescription2);
        prescriptionId2 = prescription2.getId();

        // 处方2的药品项目（库存不足）
        PrescriptionItem item3 = new PrescriptionItem();
        item3.setPrescriptionId(prescriptionId2);
        item3.setMedicineId(medicineId3);
        item3.setQuantity(3); // 需要3盒，但库存只有20片（不足1盒）
        item3.setDosage("0.1g");
        item3.setFrequency("每日1次");
        item3.setDuration("30天");
        item3.setInstructions("饭后服用");
        prescriptionItemMapper.insert(item3);
    }

    /**
     * 测试药房管理完整流程
     * 1. 查看待调剂处方
     * 2. 处方验证和库存检查
     * 3. 药品调剂和发药
     * 4. 库存管理
     * 5. 药品退回处理
     */
    @Test
    @Order(1)
    @WithMockUser(username = "pharmacist001", roles = {"PHARMACIST"})
    void testCompletePharmacyManagementWorkflow() throws Exception {
        // 步骤1: 查看待调剂处方
        verifyPendingPrescriptions();

        // 步骤2: 处方验证和库存检查
        performPrescriptionValidation();

        // 步骤3: 药品调剂和发药
        dispenseMedicines();

        // 步骤4: 库存管理操作
        performStockManagement();

        // 步骤5: 药品退回处理
        handleMedicineReturn();

        // 验证整个药房管理流程
        verifyPharmacyWorkflow();
    }

    private void verifyPendingPrescriptions() throws Exception {
        // 获取待调剂处方列表
        MvcResult result = mockMvc.perform(get("/api/pharmacy/prescriptions/pending"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        List<?> prescriptionList = objectMapper.readValue(responseBody, List.class);
        
        assertEquals(2, prescriptionList.size(), "应有2个待调剂处方");
    }

    private void performPrescriptionValidation() throws Exception {
        // 验证处方1（库存充足）
        mockMvc.perform(post("/api/pharmacy/prescriptions/" + prescriptionId1 + "/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.stockSufficient").value(true));

        // 验证处方2（库存不足）
        mockMvc.perform(post("/api/pharmacy/prescriptions/" + prescriptionId2 + "/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.stockSufficient").value(false));

        // 检查库存不足的药品
        mockMvc.perform(get("/api/pharmacy/medicines/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicineCode").value("MED003"));
    }

    private void dispenseMedicines() throws Exception {
        // 调剂处方1（正常调剂）
        String dispenseRequest1 = """
            {
                "prescriptionId": %d,
                "pharmacistId": %d,
                "items": [
                    {
                        "medicineId": %d,
                        "dispensedQuantity": 2,
                        "batchNumber": "20240101",
                        "expiryDate": "2026-01-01"
                    },
                    {
                        "medicineId": %d,
                        "dispensedQuantity": 1,
                        "batchNumber": "20240102",
                        "expiryDate": "2027-01-02"
                    }
                ],
                "notes": "正常调剂，患者已确认用药方法"
            }
            """.formatted(prescriptionId1, pharmacistId, medicineId1, medicineId2);

        mockMvc.perform(post("/api/pharmacy/dispense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dispenseRequest1))
                .andExpect(status().isOk());

        // 尝试调剂处方2（库存不足，部分调剂）
        String dispenseRequest2 = """
            {
                "prescriptionId": %d,
                "pharmacistId": %d,
                "items": [
                    {
                        "medicineId": %d,
                        "dispensedQuantity": 1,
                        "batchNumber": "20240103",
                        "expiryDate": "2026-01-03"
                    }
                ],
                "notes": "库存不足，仅能提供1盒，建议患者到其他药房购买",
                "partialDispense": true
            }
            """.formatted(prescriptionId2, pharmacistId, medicineId3);

        mockMvc.perform(post("/api/pharmacy/dispense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dispenseRequest2))
                .andExpect(status().isOk());

        // 验证调剂记录
        mockMvc.perform(get("/api/pharmacy/dispense-records/prescription/" + prescriptionId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("已调剂"))
                .andExpect(jsonPath("$.pharmacistId").value(pharmacistId));
    }

    private void performStockManagement() throws Exception {
        // 药品入库
        StockInRequest stockInRequest = new StockInRequest();
        stockInRequest.setMedicineId(medicineId3);
        stockInRequest.setQuantity(500);
        stockInRequest.setBatchNumber("20240104");
        stockInRequest.setExpiryDate(LocalDate.now().plusYears(2));
        stockInRequest.setSupplier("北京医药公司");
        stockInRequest.setUnitCost(new BigDecimal("10.00"));
        stockInRequest.setOperatorId(pharmacistId);
        stockInRequest.setNotes("紧急补货");

        mockMvc.perform(post("/api/stock/in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockInRequest)))
                .andExpect(status().isOk());

        // 验证库存更新
        mockMvc.perform(get("/api/stock/level/" + medicineId3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStock").value(519)); // 20 - 1 + 500

        // 库存盘点
        StockTakingRequest stockTakingRequest = new StockTakingRequest();
        stockTakingRequest.setMedicineId(medicineId1);
        stockTakingRequest.setActualQuantity(995); // 实际库存比系统记录少5
        stockTakingRequest.setOperatorId(pharmacistId);
        stockTakingRequest.setNotes("月度盘点，发现损耗5盒");

        mockMvc.perform(post("/api/stock/taking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockTakingRequest)))
                .andExpect(status().isOk());

        // 查看库存交易记录
        mockMvc.perform(get("/api/stock/transactions/" + medicineId3))
                .andExpect(status().isOk());
    }

    private void handleMedicineReturn() throws Exception {
        // 创建一个已调剂的处方用于退药测试
        DispenseRecord dispenseRecord = new DispenseRecord();
        dispenseRecord.setPrescriptionId(prescriptionId1);
        dispenseRecord.setPharmacistId(pharmacistId);
        dispenseRecord.setDispenseTime(LocalDateTime.now().minusHours(1));
        dispenseRecord.setStatus("已调剂");
        dispenseRecord.setNotes("正常调剂");
        dispenseRecordMapper.insert(dispenseRecord);

        DispenseItem dispenseItem = new DispenseItem();
        dispenseItem.setDispenseRecordId(dispenseRecord.getId());
        dispenseItem.setMedicineId(medicineId1);
        dispenseItem.setDispensedQuantity(2);
        dispenseItem.setBatchNumber("20240101");
        dispenseItem.setExpiryDate(LocalDate.now().plusYears(2));
        dispenseItemMapper.insert(dispenseItem);

        // 药品退回
        String returnRequest = """
            {
                "dispenseRecordId": %d,
                "items": [
                    {
                        "medicineId": %d,
                        "returnQuantity": 1,
                        "reason": "患者过敏反应，医生建议停药"
                    }
                ],
                "operatorId": %d,
                "notes": "患者出现皮疹，确认为药物过敏"
            }
            """.formatted(dispenseRecord.getId(), medicineId1, pharmacistId);

        mockMvc.perform(post("/api/pharmacy/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(returnRequest))
                .andExpect(status().isOk());

        // 验证退药后库存恢复
        mockMvc.perform(get("/api/stock/level/" + medicineId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStock").value(996)); // 995 + 1 (退回)
    }

    private void verifyPharmacyWorkflow() throws Exception {
        // 验证处方状态更新
        mockMvc.perform(get("/api/prescriptions/" + prescriptionId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("已调剂"));

        // 验证库存预警
        mockMvc.perform(get("/api/pharmacy/medicines/low-stock"))
                .andExpect(status().isOk());

        // 验证即将过期药品
        mockMvc.perform(get("/api/pharmacy/medicines/expiring?days=30"))
                .andExpect(status().isOk());

        // 验证调剂统计
        mockMvc.perform(get("/api/pharmacy/statistics/daily?date=" + LocalDate.now()))
                .andExpect(status().isOk());
    }

    /**
     * 测试药房管理异常情况处理
     */
    @Test
    @Order(2)
    @WithMockUser(username = "pharmacist001", roles = {"PHARMACIST"})
    void testPharmacyExceptionHandling() throws Exception {
        // 测试调剂过期药品
        testDispenseExpiredMedicine();

        // 测试库存不足情况
        testInsufficientStock();

        // 测试重复调剂
        testDuplicateDispense();
    }

    private void testDispenseExpiredMedicine() throws Exception {
        // 创建过期药品
        Medicine expiredMedicine = new Medicine();
        expiredMedicine.setMedicineCode("MED999");
        expiredMedicine.setName("过期药品");
        expiredMedicine.setSpecification("测试规格");
        expiredMedicine.setManufacturer("测试厂家");
        expiredMedicine.setBatchNumber("20220101");
        expiredMedicine.setExpiryDate(LocalDate.now().minusDays(1)); // 已过期
        expiredMedicine.setUnitPrice(new BigDecimal("10.00"));
        expiredMedicine.setStockQuantity(100);
        expiredMedicine.setMinStockLevel(10);
        medicineMapper.insert(expiredMedicine);

        // 尝试调剂过期药品应该失败
        String dispenseExpiredRequest = """
            {
                "prescriptionId": %d,
                "pharmacistId": %d,
                "items": [
                    {
                        "medicineId": %d,
                        "dispensedQuantity": 1,
                        "batchNumber": "20220101",
                        "expiryDate": "%s"
                    }
                ]
            }
            """.formatted(prescriptionId1, pharmacistId, expiredMedicine.getId(), 
                         LocalDate.now().minusDays(1));

        mockMvc.perform(post("/api/pharmacy/dispense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dispenseExpiredRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("过期")));
    }

    private void testInsufficientStock() throws Exception {
        // 尝试调剂超出库存数量的药品
        String insufficientStockRequest = """
            {
                "prescriptionId": %d,
                "pharmacistId": %d,
                "items": [
                    {
                        "medicineId": %d,
                        "dispensedQuantity": 1000,
                        "batchNumber": "20240103",
                        "expiryDate": "2026-01-03"
                    }
                ]
            }
            """.formatted(prescriptionId2, pharmacistId, medicineId3);

        mockMvc.perform(post("/api/pharmacy/dispense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(insufficientStockRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("库存不足")));
    }

    private void testDuplicateDispense() throws Exception {
        // 创建已调剂的处方
        Prescription dispensedPrescription = new Prescription();
        dispensedPrescription.setMedicalRecordId(3L);
        dispensedPrescription.setDoctorId(doctorId);
        dispensedPrescription.setPrescriptionNumber("RX999");
        dispensedPrescription.setStatus("已调剂");
        dispensedPrescription.setPrescribedAt(LocalDateTime.now());
        dispensedPrescription.setDispensedAt(LocalDateTime.now());
        prescriptionMapper.insert(dispensedPrescription);

        // 尝试重复调剂应该失败
        String duplicateDispenseRequest = """
            {
                "prescriptionId": %d,
                "pharmacistId": %d,
                "items": [
                    {
                        "medicineId": %d,
                        "dispensedQuantity": 1,
                        "batchNumber": "20240101",
                        "expiryDate": "2026-01-01"
                    }
                ]
            }
            """.formatted(dispensedPrescription.getId(), pharmacistId, medicineId1);

        mockMvc.perform(post("/api/pharmacy/dispense")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateDispenseRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("已调剂")));
    }

    /**
     * 测试药房库存预警和过期提醒功能
     */
    @Test
    @Order(3)
    @WithMockUser(username = "pharmacist001", roles = {"PHARMACIST"})
    void testPharmacyAlertSystem() throws Exception {
        // 测试库存预警
        mockMvc.perform(get("/api/pharmacy/medicines/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].stockQuantity").value(org.hamcrest.Matchers.lessThan(50)));

        // 测试即将过期药品提醒
        mockMvc.perform(get("/api/pharmacy/medicines/expiring?days=365"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 测试已过期药品查询
        mockMvc.perform(get("/api/pharmacy/medicines/expired"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 测试药品搜索功能
        MedicineSearchCriteria searchCriteria = new MedicineSearchCriteria();
        searchCriteria.setName("阿莫西林");
        searchCriteria.setManufacturer("华北制药");

        mockMvc.perform(post("/api/pharmacy/medicines/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchCriteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(org.hamcrest.Matchers.containsString("阿莫西林")));
    }
}