package org.me.joy.clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.me.joy.clinic.dto.CreateMedicalOrderRequest;
import org.me.joy.clinic.dto.ExecuteMedicalOrderRequest;
import org.me.joy.clinic.entity.MedicalOrder;
import org.me.joy.clinic.entity.User;
import org.me.joy.clinic.security.CustomUserPrincipal;
import org.me.joy.clinic.service.MedicalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 医嘱控制器测试类
 */
@WebMvcTest(MedicalOrderController.class)
class MedicalOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalOrderService medicalOrderService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateMedicalOrderRequest createRequest;
    private ExecuteMedicalOrderRequest executeRequest;
    private MedicalOrder medicalOrder;
    private CustomUserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        createRequest = new CreateMedicalOrderRequest();
        createRequest.setPatientId(1L);
        createRequest.setOrderType("INJECTION");
        createRequest.setContent("静脉注射生理盐水");
        createRequest.setDosage("250ml");
        createRequest.setFrequency("每日一次");
        createRequest.setPriority("NORMAL");

        executeRequest = new ExecuteMedicalOrderRequest();
        executeRequest.setExecutionNotes("执行顺利");
        executeRequest.setMedicationName("生理盐水");
        executeRequest.setDosage("250ml");
        executeRequest.setInjectionSite("左手背");

        medicalOrder = new MedicalOrder();
        medicalOrder.setId(1L);
        medicalOrder.setPatientId(1L);
        medicalOrder.setPrescribedBy(1L);
        medicalOrder.setOrderType("INJECTION");
        medicalOrder.setContent("静脉注射生理盐水");
        medicalOrder.setStatus("PENDING");
        medicalOrder.setPrescribedAt(LocalDateTime.now());

        User user = new User();
        user.setId(1L);
        user.setUsername("doctor");
        user.setPassword("password");
        user.setEnabled(true);
        userPrincipal = new CustomUserPrincipal(user);
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_CREATE")
    void testCreateMedicalOrder_Success() throws Exception {
        // 准备数据
        when(medicalOrderService.createMedicalOrder(any(CreateMedicalOrderRequest.class), eq(1L)))
                .thenReturn(medicalOrder);

        // 执行测试
        mockMvc.perform(post("/api/medical-orders")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patientId").value(1L))
                .andExpect(jsonPath("$.orderType").value("INJECTION"))
                .andExpect(jsonPath("$.content").value("静脉注射生理盐水"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_CREATE")
    void testCreateMedicalOrder_InvalidRequest() throws Exception {
        // 准备无效请求数据
        createRequest.setPatientId(null);

        // 执行测试
        mockMvc.perform(post("/api/medical-orders")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_READ")
    void testGetPendingOrders_Success() throws Exception {
        // 准备数据
        List<MedicalOrder> orders = Arrays.asList(medicalOrder);
        when(medicalOrderService.getPendingOrders(1L)).thenReturn(orders);

        // 执行测试
        mockMvc.perform(get("/api/medical-orders/patient/1/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_READ")
    void testGetExecutedOrders_Success() throws Exception {
        // 准备数据
        medicalOrder.setStatus("EXECUTED");
        List<MedicalOrder> orders = Arrays.asList(medicalOrder);
        when(medicalOrderService.getExecutedOrders(1L)).thenReturn(orders);

        // 执行测试
        mockMvc.perform(get("/api/medical-orders/patient/1/executed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("EXECUTED"));
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_EXECUTE")
    void testExecuteOrder_Success() throws Exception {
        // 准备数据
        medicalOrder.setStatus("EXECUTED");
        medicalOrder.setExecutedBy(1L);
        medicalOrder.setExecutedAt(LocalDateTime.now());
        when(medicalOrderService.executeOrder(eq(1L), any(ExecuteMedicalOrderRequest.class), eq(1L)))
                .thenReturn(medicalOrder);

        // 执行测试
        mockMvc.perform(post("/api/medical-orders/1/execute")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(executeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("EXECUTED"))
                .andExpect(jsonPath("$.executedBy").value(1L));
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_EXECUTE")
    void testPostponeOrder_Success() throws Exception {
        // 准备数据
        medicalOrder.setStatus("POSTPONED");
        medicalOrder.setPostponeReason("患者不在");
        when(medicalOrderService.postponeOrder(eq(1L), eq("患者不在"), eq(1L)))
                .thenReturn(medicalOrder);

        // 执行测试
        mockMvc.perform(post("/api/medical-orders/1/postpone")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\":\"患者不在\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("POSTPONED"))
                .andExpect(jsonPath("$.postponeReason").value("患者不在"));
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_CANCEL")
    void testCancelOrder_Success() throws Exception {
        // 准备数据
        medicalOrder.setStatus("CANCELLED");
        medicalOrder.setPostponeReason("医生要求取消");
        when(medicalOrderService.cancelOrder(eq(1L), eq("医生要求取消"), eq(1L)))
                .thenReturn(medicalOrder);

        // 执行测试
        mockMvc.perform(post("/api/medical-orders/1/cancel")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\":\"医生要求取消\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_READ")
    void testGetMedicalOrderById_Success() throws Exception {
        // 准备数据
        when(medicalOrderService.getMedicalOrderById(1L)).thenReturn(medicalOrder);

        // 执行测试
        mockMvc.perform(get("/api/medical-orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patientId").value(1L))
                .andExpect(jsonPath("$.orderType").value("INJECTION"));
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_READ")
    void testGetUrgentPendingOrders_Success() throws Exception {
        // 准备数据
        medicalOrder.setPriority("URGENT");
        List<MedicalOrder> orders = Arrays.asList(medicalOrder);
        when(medicalOrderService.getUrgentPendingOrders()).thenReturn(orders);

        // 执行测试
        mockMvc.perform(get("/api/medical-orders/urgent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].priority").value("URGENT"));
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_READ")
    void testGetOrdersByNurse_Success() throws Exception {
        // 准备数据
        List<MedicalOrder> orders = Arrays.asList(medicalOrder);
        when(medicalOrderService.getOrdersByNurseAndTimeRange(eq(2L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(orders);

        // 执行测试
        mockMvc.perform(get("/api/medical-orders/nurse/2")
                .param("startTime", "2024-01-01T00:00:00")
                .param("endTime", "2024-01-01T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_READ")
    void testGetOrdersByDoctor_Success() throws Exception {
        // 准备数据
        List<MedicalOrder> orders = Arrays.asList(medicalOrder);
        when(medicalOrderService.getOrdersByDoctorAndTimeRange(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(orders);

        // 执行测试
        mockMvc.perform(get("/api/medical-orders/doctor/1")
                .param("startTime", "2024-01-01T00:00:00")
                .param("endTime", "2024-01-01T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(authorities = "MEDICAL_ORDER_READ")
    void testCountOrdersByPatientAndStatus_Success() throws Exception {
        // 准备数据
        when(medicalOrderService.countOrdersByPatientIdAndStatus(1L, "PENDING")).thenReturn(3L);

        // 执行测试
        mockMvc.perform(get("/api/medical-orders/patient/1/count")
                .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    void testCreateMedicalOrder_Unauthorized() throws Exception {
        // 执行测试（无权限）
        mockMvc.perform(post("/api/medical-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "WRONG_PERMISSION")
    void testCreateMedicalOrder_Forbidden() throws Exception {
        // 执行测试（权限不足）
        mockMvc.perform(post("/api/medical-orders")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
}