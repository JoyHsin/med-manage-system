package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.CreateStaffRequest;
import org.me.joy.clinic.dto.StaffResponse;
import org.me.joy.clinic.dto.UpdateStaffRequest;
import org.me.joy.clinic.entity.Schedule;
import org.me.joy.clinic.entity.Staff;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.ScheduleMapper;
import org.me.joy.clinic.mapper.StaffMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 医护人员管理服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class StaffManagementServiceImplTest {

    @Mock
    private StaffMapper staffMapper;

    @Mock
    private ScheduleMapper scheduleMapper;

    @InjectMocks
    private StaffManagementServiceImpl staffManagementService;

    private Staff testStaff;
    private CreateStaffRequest createStaffRequest;
    private UpdateStaffRequest updateStaffRequest;
    private Schedule testSchedule;

    @BeforeEach
    void setUp() {
        testStaff = new Staff();
        testStaff.setId(1L);
        testStaff.setStaffNumber("DOC20240101001");
        testStaff.setName("张医生");
        testStaff.setPhone("13800138000");
        testStaff.setIdCard("110101199001011234");
        testStaff.setBirthDate(LocalDate.of(1990, 1, 1));
        testStaff.setGender("男");
        testStaff.setEmail("doctor@clinic.com");
        testStaff.setPosition("医生");
        testStaff.setDepartment("内科");
        testStaff.setTitle("主治医师");
        testStaff.setHireDate(LocalDate.of(2020, 1, 1));
        testStaff.setWorkStatus("在职");
        testStaff.setIsSchedulable(true);

        createStaffRequest = new CreateStaffRequest();
        createStaffRequest.setName("李护士");
        createStaffRequest.setPhone("13900139000");
        createStaffRequest.setIdCard("110101199002021234");
        createStaffRequest.setBirthDate(LocalDate.of(1990, 2, 2));
        createStaffRequest.setGender("女");
        createStaffRequest.setEmail("nurse@clinic.com");
        createStaffRequest.setPosition("护士");
        createStaffRequest.setDepartment("内科");
        createStaffRequest.setHireDate(LocalDate.of(2021, 1, 1));
        createStaffRequest.setIsSchedulable(true);

        updateStaffRequest = new UpdateStaffRequest();
        updateStaffRequest.setName("张医生（更新）");
        updateStaffRequest.setPhone("13800138001");
        updateStaffRequest.setTitle("副主任医师");

        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setStaffId(1L);
        testSchedule.setScheduleDate(LocalDate.now());
        testSchedule.setShiftType("白班");
        testSchedule.setStartTime(LocalTime.of(8, 0));
        testSchedule.setEndTime(LocalTime.of(17, 0));
        testSchedule.setDepartment("内科");
        testSchedule.setStatus("正常");
    }

    @Test
    void createStaff_WithValidRequest_ShouldCreateStaff() {
        // Given
        when(staffMapper.findByIdCard(createStaffRequest.getIdCard())).thenReturn(Optional.empty());
        when(staffMapper.findByStaffNumber(anyString())).thenReturn(Optional.empty());
        when(staffMapper.insert(any(Staff.class))).thenReturn(1);

        // When
        StaffResponse response = staffManagementService.createStaff(createStaffRequest);

        // Then
        assertNotNull(response);
        assertEquals(createStaffRequest.getName(), response.getName());
        assertEquals(createStaffRequest.getPhone(), response.getPhone());
        assertEquals(createStaffRequest.getPosition(), response.getPosition());
        assertEquals("在职", response.getWorkStatus());
        assertTrue(response.getIsSchedulable());

        verify(staffMapper).insert(any(Staff.class));
    }

    @Test
    void createStaff_WithExistingIdCard_ShouldThrowValidationException() {
        // Given
        when(staffMapper.findByIdCard(createStaffRequest.getIdCard())).thenReturn(Optional.of(testStaff));

        // When & Then
        assertThrows(ValidationException.class, () -> {
            staffManagementService.createStaff(createStaffRequest);
        });

        verify(staffMapper, never()).insert(any(Staff.class));
    }

    @Test
    void updateStaff_WithValidRequest_ShouldUpdateStaff() {
        // Given
        when(staffMapper.selectById(1L)).thenReturn(testStaff);
        when(staffMapper.updateById(any(Staff.class))).thenReturn(1);
        when(scheduleMapper.findByStaffId(1L)).thenReturn(Arrays.asList());

        // When
        StaffResponse response = staffManagementService.updateStaff(1L, updateStaffRequest);

        // Then
        assertNotNull(response);
        assertEquals(updateStaffRequest.getName(), response.getName());
        assertEquals(updateStaffRequest.getPhone(), response.getPhone());
        assertEquals(updateStaffRequest.getTitle(), response.getTitle());

        verify(staffMapper).updateById(any(Staff.class));
    }

    @Test
    void updateStaff_WithNonExistentStaff_ShouldThrowBusinessException() {
        // Given
        when(staffMapper.selectById(1L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            staffManagementService.updateStaff(1L, updateStaffRequest);
        });

        verify(staffMapper, never()).updateById(any(Staff.class));
    }

    @Test
    void getStaffById_WithValidId_ShouldReturnStaff() {
        // Given
        when(staffMapper.selectById(1L)).thenReturn(testStaff);
        when(scheduleMapper.findByStaffId(1L)).thenReturn(Arrays.asList());

        // When
        StaffResponse response = staffManagementService.getStaffById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testStaff.getId(), response.getId());
        assertEquals(testStaff.getName(), response.getName());
        assertEquals(testStaff.getStaffNumber(), response.getStaffNumber());

        verify(staffMapper).selectById(1L);
    }

    @Test
    void getStaffById_WithNonExistentId_ShouldThrowBusinessException() {
        // Given
        when(staffMapper.selectById(1L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            staffManagementService.getStaffById(1L);
        });
    }

    @Test
    void getStaffByNumber_WithValidNumber_ShouldReturnStaff() {
        // Given
        when(staffMapper.findByStaffNumber("DOC20240101001")).thenReturn(Optional.of(testStaff));
        when(scheduleMapper.findByStaffId(1L)).thenReturn(Arrays.asList());

        // When
        StaffResponse response = staffManagementService.getStaffByNumber("DOC20240101001");

        // Then
        assertNotNull(response);
        assertEquals(testStaff.getStaffNumber(), response.getStaffNumber());
        assertEquals(testStaff.getName(), response.getName());

        verify(staffMapper).findByStaffNumber("DOC20240101001");
    }

    @Test
    void getStaffByNumber_WithNonExistentNumber_ShouldThrowBusinessException() {
        // Given
        when(staffMapper.findByStaffNumber("DOC20240101001")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () -> {
            staffManagementService.getStaffByNumber("DOC20240101001");
        });
    }

    @Test
    void getAllStaff_ShouldReturnAllStaff() {
        // Given
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffMapper.selectList(null)).thenReturn(staffList);
        when(scheduleMapper.findByStaffId(anyLong())).thenReturn(Arrays.asList());

        // When
        List<StaffResponse> responses = staffManagementService.getAllStaff();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testStaff.getName(), responses.get(0).getName());

        verify(staffMapper).selectList(null);
    }

    @Test
    void getStaffByPosition_WithValidPosition_ShouldReturnStaff() {
        // Given
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffMapper.findByPosition("医生")).thenReturn(staffList);
        when(scheduleMapper.findByStaffId(anyLong())).thenReturn(Arrays.asList());

        // When
        List<StaffResponse> responses = staffManagementService.getStaffByPosition("医生");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("医生", responses.get(0).getPosition());

        verify(staffMapper).findByPosition("医生");
    }

    @Test
    void getActiveStaff_ShouldReturnActiveStaff() {
        // Given
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffMapper.findActiveStaff()).thenReturn(staffList);
        when(scheduleMapper.findByStaffId(anyLong())).thenReturn(Arrays.asList());

        // When
        List<StaffResponse> responses = staffManagementService.getActiveStaff();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("在职", responses.get(0).getWorkStatus());

        verify(staffMapper).findActiveStaff();
    }

    @Test
    void searchStaff_WithKeyword_ShouldReturnMatchingStaff() {
        // Given
        List<Staff> staffList = Arrays.asList(testStaff);
        when(staffMapper.searchStaff("张医生")).thenReturn(staffList);
        when(scheduleMapper.findByStaffId(anyLong())).thenReturn(Arrays.asList());

        // When
        List<StaffResponse> responses = staffManagementService.searchStaff("张医生");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("张医生", responses.get(0).getName());

        verify(staffMapper).searchStaff("张医生");
    }

    @Test
    void deleteStaff_WithValidId_ShouldDeleteStaff() {
        // Given
        when(staffMapper.selectById(1L)).thenReturn(testStaff);
        when(staffMapper.deleteById(1L)).thenReturn(1);

        // When
        staffManagementService.deleteStaff(1L);

        // Then
        verify(staffMapper).deleteById(1L);
    }

    @Test
    void updateStaffWorkStatus_WithValidStatus_ShouldUpdateStatus() {
        // Given
        when(staffMapper.selectById(1L)).thenReturn(testStaff);
        when(staffMapper.updateById(any(Staff.class))).thenReturn(1);

        // When
        staffManagementService.updateStaffWorkStatus(1L, "休假");

        // Then
        assertEquals("休假", testStaff.getWorkStatus());
        verify(staffMapper).updateById(testStaff);
    }

    @Test
    void resignStaff_WithValidId_ShouldResignStaff() {
        // Given
        when(staffMapper.selectById(1L)).thenReturn(testStaff);
        when(staffMapper.updateById(any(Staff.class))).thenReturn(1);
        LocalDate resignationDate = LocalDate.now();

        // When
        staffManagementService.resignStaff(1L, resignationDate);

        // Then
        assertEquals("离职", testStaff.getWorkStatus());
        assertEquals(resignationDate, testStaff.getResignationDate());
        assertFalse(testStaff.getIsSchedulable());
        assertEquals(resignationDate, testStaff.getLastWorkDate());
        verify(staffMapper).updateById(testStaff);
    }

    @Test
    void reinstateStaff_WithValidId_ShouldReinstateStaff() {
        // Given
        testStaff.setWorkStatus("离职");
        testStaff.setResignationDate(LocalDate.now().minusDays(30));
        testStaff.setIsSchedulable(false);
        when(staffMapper.selectById(1L)).thenReturn(testStaff);
        when(staffMapper.updateById(any(Staff.class))).thenReturn(1);

        // When
        staffManagementService.reinstateStaff(1L);

        // Then
        assertEquals("在职", testStaff.getWorkStatus());
        assertNull(testStaff.getResignationDate());
        assertTrue(testStaff.getIsSchedulable());
        verify(staffMapper).updateById(testStaff);
    }

    @Test
    void createSchedule_WithValidData_ShouldCreateSchedule() {
        // Given
        when(staffMapper.selectById(1L)).thenReturn(testStaff);
        when(scheduleMapper.findConflictingSchedules(anyLong(), any(LocalDate.class), isNull()))
                .thenReturn(Arrays.asList());
        when(scheduleMapper.insert(any(Schedule.class))).thenReturn(1);

        // When
        Schedule result = staffManagementService.createSchedule(1L, testSchedule);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getStaffId());
        assertEquals(testSchedule.getScheduleDate(), result.getScheduleDate());
        assertEquals(testSchedule.getShiftType(), result.getShiftType());

        verify(scheduleMapper).insert(testSchedule);
    }

    @Test
    void createSchedule_WithNonSchedulableStaff_ShouldThrowBusinessException() {
        // Given
        testStaff.setIsSchedulable(false);
        when(staffMapper.selectById(1L)).thenReturn(testStaff);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            staffManagementService.createSchedule(1L, testSchedule);
        });

        verify(scheduleMapper, never()).insert(any(Schedule.class));
    }

    @Test
    void createSchedule_WithConflictingSchedule_ShouldThrowBusinessException() {
        // Given
        when(staffMapper.selectById(1L)).thenReturn(testStaff);
        when(scheduleMapper.findConflictingSchedules(anyLong(), any(LocalDate.class), isNull()))
                .thenReturn(Arrays.asList(testSchedule));

        // When & Then
        assertThrows(BusinessException.class, () -> {
            staffManagementService.createSchedule(1L, testSchedule);
        });

        verify(scheduleMapper, never()).insert(any(Schedule.class));
    }

    @Test
    void updateSchedule_WithValidData_ShouldUpdateSchedule() {
        // Given
        Schedule updateData = new Schedule();
        updateData.setScheduleDate(LocalDate.now().plusDays(1)); // 设置不同的日期以触发冲突检查
        updateData.setShiftType("夜班");
        updateData.setStartTime(LocalTime.of(20, 0));
        updateData.setEndTime(LocalTime.of(8, 0));

        when(scheduleMapper.selectById(1L)).thenReturn(testSchedule);
        when(scheduleMapper.findConflictingSchedules(anyLong(), any(LocalDate.class), eq(1L)))
                .thenReturn(Arrays.asList());
        when(scheduleMapper.updateById(any(Schedule.class))).thenReturn(1);

        // When
        Schedule result = staffManagementService.updateSchedule(1L, updateData);

        // Then
        assertNotNull(result);
        assertEquals("夜班", result.getShiftType());
        assertEquals(LocalTime.of(20, 0), result.getStartTime());
        assertEquals(LocalTime.of(8, 0), result.getEndTime());

        verify(scheduleMapper).updateById(testSchedule);
    }

    @Test
    void deleteSchedule_WithValidId_ShouldDeleteSchedule() {
        // Given
        when(scheduleMapper.selectById(1L)).thenReturn(testSchedule);
        when(scheduleMapper.deleteById(1L)).thenReturn(1);

        // When
        staffManagementService.deleteSchedule(1L);

        // Then
        verify(scheduleMapper).deleteById(1L);
    }

    @Test
    void getStaffSchedules_WithValidStaffId_ShouldReturnSchedules() {
        // Given
        List<Schedule> schedules = Arrays.asList(testSchedule);
        when(staffMapper.selectById(1L)).thenReturn(testStaff);
        when(scheduleMapper.findByStaffId(1L)).thenReturn(schedules);

        // When
        List<Schedule> result = staffManagementService.getStaffSchedules(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSchedule.getId(), result.get(0).getId());

        verify(scheduleMapper).findByStaffId(1L);
    }

    @Test
    void hasScheduleConflict_WithConflict_ShouldReturnTrue() {
        // Given
        when(scheduleMapper.findConflictingSchedules(1L, LocalDate.now(), null))
                .thenReturn(Arrays.asList(testSchedule));

        // When
        boolean hasConflict = staffManagementService.hasScheduleConflict(1L, LocalDate.now(), null);

        // Then
        assertTrue(hasConflict);
        verify(scheduleMapper).findConflictingSchedules(1L, LocalDate.now(), null);
    }

    @Test
    void hasScheduleConflict_WithoutConflict_ShouldReturnFalse() {
        // Given
        when(scheduleMapper.findConflictingSchedules(1L, LocalDate.now(), null))
                .thenReturn(Arrays.asList());

        // When
        boolean hasConflict = staffManagementService.hasScheduleConflict(1L, LocalDate.now(), null);

        // Then
        assertFalse(hasConflict);
        verify(scheduleMapper).findConflictingSchedules(1L, LocalDate.now(), null);
    }

    @Test
    void generateStaffNumber_ShouldGenerateUniqueNumber() {
        // Given
        when(staffMapper.findByStaffNumber(anyString())).thenReturn(Optional.empty());

        // When
        String staffNumber = staffManagementService.generateStaffNumber("医生");

        // Then
        assertNotNull(staffNumber);
        assertTrue(staffNumber.startsWith("DOC"));
        assertEquals(14, staffNumber.length()); // DOC + 8位日期 + 3位序号
    }

    @Test
    void existsByStaffNumber_WithExistingNumber_ShouldReturnTrue() {
        // Given
        when(staffMapper.findByStaffNumber("DOC20240101001")).thenReturn(Optional.of(testStaff));

        // When
        boolean exists = staffManagementService.existsByStaffNumber("DOC20240101001");

        // Then
        assertTrue(exists);
        verify(staffMapper).findByStaffNumber("DOC20240101001");
    }

    @Test
    void existsByIdCard_WithExistingIdCard_ShouldReturnTrue() {
        // Given
        when(staffMapper.findByIdCard("110101199001011234")).thenReturn(Optional.of(testStaff));

        // When
        boolean exists = staffManagementService.existsByIdCard("110101199001011234");

        // Then
        assertTrue(exists);
        verify(staffMapper).findByIdCard("110101199001011234");
    }

    @Test
    void countAllStaff_ShouldReturnTotalCount() {
        // Given
        when(staffMapper.countAllStaff()).thenReturn(50L);

        // When
        Long count = staffManagementService.countAllStaff();

        // Then
        assertEquals(50L, count);
        verify(staffMapper).countAllStaff();
    }

    @Test
    void countActiveStaff_ShouldReturnActiveCount() {
        // Given
        when(staffMapper.countActiveStaff()).thenReturn(45L);

        // When
        Long count = staffManagementService.countActiveStaff();

        // Then
        assertEquals(45L, count);
        verify(staffMapper).countActiveStaff();
    }

    @Test
    void calculateStaffWorkHours_ShouldReturnWorkHours() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(staffMapper.selectById(1L)).thenReturn(testStaff);
        when(scheduleMapper.sumStaffWorkHours(1L, startDate, endDate)).thenReturn(40.0);

        // When
        Double workHours = staffManagementService.calculateStaffWorkHours(1L, startDate, endDate);

        // Then
        assertEquals(40.0, workHours);
        verify(scheduleMapper).sumStaffWorkHours(1L, startDate, endDate);
    }
}