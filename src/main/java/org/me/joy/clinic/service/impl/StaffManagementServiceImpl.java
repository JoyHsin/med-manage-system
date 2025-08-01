package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.dto.CreateStaffRequest;
import org.me.joy.clinic.dto.StaffResponse;
import org.me.joy.clinic.dto.UpdateStaffRequest;
import org.me.joy.clinic.entity.Schedule;
import org.me.joy.clinic.entity.Staff;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.ScheduleMapper;
import org.me.joy.clinic.mapper.StaffMapper;
import org.me.joy.clinic.service.StaffManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 医护人员管理服务实现类
 */
@Service
@Transactional
public class StaffManagementServiceImpl implements StaffManagementService {

    private static final Logger logger = LoggerFactory.getLogger(StaffManagementServiceImpl.class);

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public StaffResponse createStaff(CreateStaffRequest createStaffRequest) {
        logger.info("创建新员工: {}", createStaffRequest.getName());

        // 验证身份证号是否已存在
        if (StringUtils.hasText(createStaffRequest.getIdCard()) && 
            existsByIdCard(createStaffRequest.getIdCard())) {
            throw new ValidationException("STAFF_ID_CARD_EXISTS", "身份证号已存在");
        }

        // 生成员工编号
        String staffNumber = generateStaffNumber(createStaffRequest.getPosition());

        // 创建员工实体
        Staff staff = new Staff();
        staff.setStaffNumber(staffNumber);
        staff.setName(createStaffRequest.getName());
        staff.setPhone(createStaffRequest.getPhone());
        staff.setIdCard(createStaffRequest.getIdCard());
        staff.setBirthDate(createStaffRequest.getBirthDate());
        staff.setGender(createStaffRequest.getGender());
        staff.setEmail(createStaffRequest.getEmail());
        staff.setAddress(createStaffRequest.getAddress());
        staff.setEmergencyContactName(createStaffRequest.getEmergencyContactName());
        staff.setEmergencyContactPhone(createStaffRequest.getEmergencyContactPhone());
        staff.setEmergencyContactRelation(createStaffRequest.getEmergencyContactRelation());
        staff.setPosition(createStaffRequest.getPosition());
        staff.setDepartment(createStaffRequest.getDepartment());
        staff.setTitle(createStaffRequest.getTitle());
        staff.setSpecialization(createStaffRequest.getSpecialization());
        staff.setEducation(createStaffRequest.getEducation());
        staff.setGraduateSchool(createStaffRequest.getGraduateSchool());
        staff.setLicenseNumber(createStaffRequest.getLicenseNumber());
        staff.setLicenseExpiryDate(createStaffRequest.getLicenseExpiryDate());
        staff.setHireDate(createStaffRequest.getHireDate());
        staff.setBaseSalary(createStaffRequest.getBaseSalary());
        staff.setExperienceYears(createStaffRequest.getExperienceYears());
        staff.setRemarks(createStaffRequest.getRemarks());
        staff.setIsSchedulable(createStaffRequest.getIsSchedulable() != null ? 
                              createStaffRequest.getIsSchedulable() : true);
        staff.setUserId(createStaffRequest.getUserId());
        staff.setWorkStatus("在职");

        // 保存员工
        staffMapper.insert(staff);

        logger.info("员工创建成功: {}, ID: {}", staff.getName(), staff.getId());

        return convertToStaffResponse(staff);
    }

    @Override
    public StaffResponse updateStaff(Long staffId, UpdateStaffRequest updateStaffRequest) {
        logger.info("更新员工信息: {}", staffId);

        Staff staff = getStaffEntityById(staffId);

        // 验证身份证号是否已被其他员工使用
        if (StringUtils.hasText(updateStaffRequest.getIdCard()) && 
            !updateStaffRequest.getIdCard().equals(staff.getIdCard())) {
            if (existsByIdCard(updateStaffRequest.getIdCard())) {
                throw new ValidationException("STAFF_ID_CARD_EXISTS", "身份证号已存在");
            }
        }

        // 更新员工信息
        if (StringUtils.hasText(updateStaffRequest.getName())) {
            staff.setName(updateStaffRequest.getName());
        }
        if (StringUtils.hasText(updateStaffRequest.getPhone())) {
            staff.setPhone(updateStaffRequest.getPhone());
        }
        if (StringUtils.hasText(updateStaffRequest.getIdCard())) {
            staff.setIdCard(updateStaffRequest.getIdCard());
        }
        if (updateStaffRequest.getBirthDate() != null) {
            staff.setBirthDate(updateStaffRequest.getBirthDate());
        }
        if (StringUtils.hasText(updateStaffRequest.getGender())) {
            staff.setGender(updateStaffRequest.getGender());
        }
        if (updateStaffRequest.getEmail() != null) {
            staff.setEmail(updateStaffRequest.getEmail());
        }
        if (updateStaffRequest.getAddress() != null) {
            staff.setAddress(updateStaffRequest.getAddress());
        }
        if (updateStaffRequest.getEmergencyContactName() != null) {
            staff.setEmergencyContactName(updateStaffRequest.getEmergencyContactName());
        }
        if (updateStaffRequest.getEmergencyContactPhone() != null) {
            staff.setEmergencyContactPhone(updateStaffRequest.getEmergencyContactPhone());
        }
        if (updateStaffRequest.getEmergencyContactRelation() != null) {
            staff.setEmergencyContactRelation(updateStaffRequest.getEmergencyContactRelation());
        }
        if (StringUtils.hasText(updateStaffRequest.getPosition())) {
            staff.setPosition(updateStaffRequest.getPosition());
        }
        if (updateStaffRequest.getDepartment() != null) {
            staff.setDepartment(updateStaffRequest.getDepartment());
        }
        if (updateStaffRequest.getTitle() != null) {
            staff.setTitle(updateStaffRequest.getTitle());
        }
        if (updateStaffRequest.getSpecialization() != null) {
            staff.setSpecialization(updateStaffRequest.getSpecialization());
        }
        if (updateStaffRequest.getEducation() != null) {
            staff.setEducation(updateStaffRequest.getEducation());
        }
        if (updateStaffRequest.getGraduateSchool() != null) {
            staff.setGraduateSchool(updateStaffRequest.getGraduateSchool());
        }
        if (updateStaffRequest.getLicenseNumber() != null) {
            staff.setLicenseNumber(updateStaffRequest.getLicenseNumber());
        }
        if (updateStaffRequest.getLicenseExpiryDate() != null) {
            staff.setLicenseExpiryDate(updateStaffRequest.getLicenseExpiryDate());
        }
        if (updateStaffRequest.getResignationDate() != null) {
            staff.setResignationDate(updateStaffRequest.getResignationDate());
        }
        if (StringUtils.hasText(updateStaffRequest.getWorkStatus())) {
            staff.setWorkStatus(updateStaffRequest.getWorkStatus());
        }
        if (updateStaffRequest.getBaseSalary() != null) {
            staff.setBaseSalary(updateStaffRequest.getBaseSalary());
        }
        if (updateStaffRequest.getExperienceYears() != null) {
            staff.setExperienceYears(updateStaffRequest.getExperienceYears());
        }
        if (updateStaffRequest.getRemarks() != null) {
            staff.setRemarks(updateStaffRequest.getRemarks());
        }
        if (updateStaffRequest.getIsSchedulable() != null) {
            staff.setIsSchedulable(updateStaffRequest.getIsSchedulable());
        }
        if (updateStaffRequest.getUserId() != null) {
            staff.setUserId(updateStaffRequest.getUserId());
        }

        staffMapper.updateById(staff);

        logger.info("员工信息更新成功: {}", staffId);

        return convertToStaffResponse(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getStaffById(Long staffId) {
        Staff staff = getStaffEntityById(staffId);
        return convertToStaffResponse(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getStaffByNumber(String staffNumber) {
        if (!StringUtils.hasText(staffNumber)) {
            throw new ValidationException("STAFF_NUMBER_EMPTY", "员工编号不能为空");
        }

        Optional<Staff> staffOpt = staffMapper.findByStaffNumber(staffNumber);
        if (staffOpt.isEmpty()) {
            throw new BusinessException("STAFF_NOT_FOUND", "员工不存在");
        }

        return convertToStaffResponse(staffOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getStaffByIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            throw new ValidationException("ID_CARD_EMPTY", "身份证号不能为空");
        }

        Optional<Staff> staffOpt = staffMapper.findByIdCard(idCard);
        if (staffOpt.isEmpty()) {
            throw new BusinessException("STAFF_NOT_FOUND", "员工不存在");
        }

        return convertToStaffResponse(staffOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getStaffByUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("USER_ID_NULL", "用户ID不能为空");
        }

        Optional<Staff> staffOpt = staffMapper.findByUserId(userId);
        if (staffOpt.isEmpty()) {
            throw new BusinessException("STAFF_NOT_FOUND", "员工不存在");
        }

        return convertToStaffResponse(staffOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getAllStaff() {
        List<Staff> staffList = staffMapper.selectList(null);
        return staffList.stream()
                .map(this::convertToStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffByPosition(String position) {
        if (!StringUtils.hasText(position)) {
            throw new ValidationException("POSITION_EMPTY", "职位不能为空");
        }

        List<Staff> staffList = staffMapper.findByPosition(position);
        return staffList.stream()
                .map(this::convertToStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffByDepartment(String department) {
        if (!StringUtils.hasText(department)) {
            throw new ValidationException("DEPARTMENT_EMPTY", "科室不能为空");
        }

        List<Staff> staffList = staffMapper.findByDepartment(department);
        return staffList.stream()
                .map(this::convertToStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffByWorkStatus(String workStatus) {
        if (!StringUtils.hasText(workStatus)) {
            throw new ValidationException("WORK_STATUS_EMPTY", "工作状态不能为空");
        }

        List<Staff> staffList = staffMapper.findByWorkStatus(workStatus);
        return staffList.stream()
                .map(this::convertToStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getActiveStaff() {
        List<Staff> staffList = staffMapper.findActiveStaff();
        return staffList.stream()
                .map(this::convertToStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getSchedulableStaff() {
        List<Staff> staffList = staffMapper.findSchedulableStaff();
        return staffList.stream()
                .map(this::convertToStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffByPositionAndDepartment(String position, String department) {
        if (!StringUtils.hasText(position)) {
            throw new ValidationException("POSITION_EMPTY", "职位不能为空");
        }
        if (!StringUtils.hasText(department)) {
            throw new ValidationException("DEPARTMENT_EMPTY", "科室不能为空");
        }

        List<Staff> staffList = staffMapper.findByPositionAndDepartment(position, department);
        return staffList.stream()
                .map(this::convertToStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> searchStaff(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllStaff();
        }

        List<Staff> staffList = staffMapper.searchStaff(keyword.trim());
        return staffList.stream()
                .map(this::convertToStaffResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffWithExpiringLicense(Integer days) {
        if (days == null || days <= 0) {
            days = 30; // 默认30天
        }

        LocalDate expiryDate = LocalDate.now().plusDays(days);
        List<Staff> staffList = staffMapper.findByLicenseExpiring(expiryDate);
        return staffList.stream()
                .map(this::convertToStaffResponse)
                .collect(Collectors.toList());
    }    
@Override
    public void deleteStaff(Long staffId) {
        logger.info("删除员工: {}", staffId);

        Staff staff = getStaffEntityById(staffId);

        // 软删除
        staffMapper.deleteById(staffId);

        logger.info("员工删除成功: {}", staffId);
    }

    @Override
    public void updateStaffWorkStatus(Long staffId, String workStatus) {
        if (!StringUtils.hasText(workStatus)) {
            throw new ValidationException("WORK_STATUS_EMPTY", "工作状态不能为空");
        }

        logger.info("更新员工工作状态: staffId={}, workStatus={}", staffId, workStatus);

        Staff staff = getStaffEntityById(staffId);
        staff.setWorkStatus(workStatus);
        
        // 如果是离职，设置离职日期
        if ("离职".equals(workStatus) && staff.getResignationDate() == null) {
            staff.setResignationDate(LocalDate.now());
        }
        
        staffMapper.updateById(staff);

        logger.info("员工工作状态更新成功: staffId={}, workStatus={}", staffId, workStatus);
    }

    @Override
    public void resignStaff(Long staffId, LocalDate resignationDate) {
        if (resignationDate == null) {
            resignationDate = LocalDate.now();
        }

        logger.info("员工离职处理: staffId={}, resignationDate={}", staffId, resignationDate);

        Staff staff = getStaffEntityById(staffId);
        staff.setWorkStatus("离职");
        staff.setResignationDate(resignationDate);
        staff.setIsSchedulable(false);
        staff.setLastWorkDate(resignationDate);

        staffMapper.updateById(staff);

        logger.info("员工离职处理完成: staffId={}", staffId);
    }

    @Override
    public void reinstateStaff(Long staffId) {
        logger.info("员工复职处理: {}", staffId);

        Staff staff = getStaffEntityById(staffId);
        staff.setWorkStatus("在职");
        staff.setResignationDate(null);
        staff.setIsSchedulable(true);

        staffMapper.updateById(staff);

        logger.info("员工复职处理完成: {}", staffId);
    }

    @Override
    public void updateStaffSchedulableStatus(Long staffId, Boolean isSchedulable) {
        if (isSchedulable == null) {
            throw new ValidationException("SCHEDULABLE_STATUS_NULL", "排班状态不能为空");
        }

        logger.info("更新员工排班状态: staffId={}, isSchedulable={}", staffId, isSchedulable);

        Staff staff = getStaffEntityById(staffId);
        staff.setIsSchedulable(isSchedulable);

        staffMapper.updateById(staff);

        logger.info("员工排班状态更新成功: staffId={}, isSchedulable={}", staffId, isSchedulable);
    }

    @Override
    public Schedule createSchedule(Long staffId, Schedule schedule) {
        logger.info("创建员工排班: staffId={}, date={}, shiftType={}", 
                   staffId, schedule.getScheduleDate(), schedule.getShiftType());

        // 验证员工存在且可排班
        Staff staff = getStaffEntityById(staffId);
        if (!staff.getIsSchedulable()) {
            throw new BusinessException("STAFF_NOT_SCHEDULABLE", "员工不可排班");
        }

        // 检查排班冲突
        if (hasScheduleConflict(staffId, schedule.getScheduleDate(), null)) {
            throw new BusinessException("SCHEDULE_CONFLICT", "排班时间冲突");
        }

        // 设置员工ID
        schedule.setStaffId(staffId);
        if (schedule.getDepartment() == null) {
            schedule.setDepartment(staff.getDepartment());
        }

        // 保存排班
        scheduleMapper.insert(schedule);

        logger.info("员工排班创建成功: staffId={}, scheduleId={}", staffId, schedule.getId());

        return schedule;
    }

    @Override
    public Schedule updateSchedule(Long scheduleId, Schedule schedule) {
        logger.info("更新员工排班: {}", scheduleId);

        Schedule existingSchedule = scheduleMapper.selectById(scheduleId);
        if (existingSchedule == null) {
            throw new BusinessException("SCHEDULE_NOT_FOUND", "排班不存在");
        }

        // 检查排班冲突（排除当前排班）
        if (schedule.getScheduleDate() != null && 
            hasScheduleConflict(existingSchedule.getStaffId(), schedule.getScheduleDate(), scheduleId)) {
            throw new BusinessException("SCHEDULE_CONFLICT", "排班时间冲突");
        }

        // 更新排班信息
        if (schedule.getScheduleDate() != null) {
            existingSchedule.setScheduleDate(schedule.getScheduleDate());
        }
        if (StringUtils.hasText(schedule.getShiftType())) {
            existingSchedule.setShiftType(schedule.getShiftType());
        }
        if (schedule.getStartTime() != null) {
            existingSchedule.setStartTime(schedule.getStartTime());
        }
        if (schedule.getEndTime() != null) {
            existingSchedule.setEndTime(schedule.getEndTime());
        }
        if (schedule.getWorkLocation() != null) {
            existingSchedule.setWorkLocation(schedule.getWorkLocation());
        }
        if (schedule.getDepartment() != null) {
            existingSchedule.setDepartment(schedule.getDepartment());
        }
        if (StringUtils.hasText(schedule.getStatus())) {
            existingSchedule.setStatus(schedule.getStatus());
        }
        if (schedule.getIsOvertime() != null) {
            existingSchedule.setIsOvertime(schedule.getIsOvertime());
        }
        if (schedule.getRemarks() != null) {
            existingSchedule.setRemarks(schedule.getRemarks());
        }

        scheduleMapper.updateById(existingSchedule);

        logger.info("员工排班更新成功: {}", scheduleId);

        return existingSchedule;
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        logger.info("删除员工排班: {}", scheduleId);

        Schedule existingSchedule = scheduleMapper.selectById(scheduleId);
        if (existingSchedule == null) {
            throw new BusinessException("SCHEDULE_NOT_FOUND", "排班不存在");
        }

        scheduleMapper.deleteById(scheduleId);

        logger.info("员工排班删除成功: {}", scheduleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getStaffSchedules(Long staffId) {
        // 验证员工存在
        getStaffEntityById(staffId);

        return scheduleMapper.findByStaffId(staffId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getStaffSchedulesByDateRange(Long staffId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("DATE_RANGE_INVALID", "日期范围不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("DATE_RANGE_INVALID", "开始日期不能晚于结束日期");
        }

        // 验证员工存在
        getStaffEntityById(staffId);

        return scheduleMapper.findByStaffIdAndDateRange(staffId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        if (date == null) {
            throw new ValidationException("DATE_NULL", "日期不能为空");
        }

        return scheduleMapper.findByDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getTodaySchedules() {
        return scheduleMapper.findTodaySchedules(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getWeeklySchedules() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        
        return scheduleMapper.findWeeklySchedules(startOfWeek, endOfWeek);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getMonthlySchedules() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        
        return scheduleMapper.findMonthlySchedules(startOfMonth, endOfMonth);
    }

    @Override
    public List<Schedule> batchCreateSchedules(List<Schedule> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            throw new ValidationException("SCHEDULES_EMPTY", "排班列表不能为空");
        }

        logger.info("批量创建排班: count={}", schedules.size());

        // 验证每个排班
        for (Schedule schedule : schedules) {
            // 验证员工存在且可排班
            Staff staff = getStaffEntityById(schedule.getStaffId());
            if (!staff.getIsSchedulable()) {
                throw new BusinessException("STAFF_NOT_SCHEDULABLE", 
                                          "员工不可排班: " + staff.getName());
            }

            // 检查排班冲突
            if (hasScheduleConflict(schedule.getStaffId(), schedule.getScheduleDate(), null)) {
                throw new BusinessException("SCHEDULE_CONFLICT", 
                                          "排班时间冲突: " + staff.getName() + " " + schedule.getScheduleDate());
            }

            // 设置默认科室
            if (schedule.getDepartment() == null) {
                schedule.setDepartment(staff.getDepartment());
            }
        }

        // 批量插入
        scheduleMapper.batchInsert(schedules);

        logger.info("批量创建排班成功: count={}", schedules.size());

        return schedules;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasScheduleConflict(Long staffId, LocalDate scheduleDate, Long excludeScheduleId) {
        List<Schedule> conflictingSchedules = scheduleMapper.findConflictingSchedules(
                staffId, scheduleDate, excludeScheduleId);
        return !conflictingSchedules.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStaffNumber(String staffNumber) {
        if (!StringUtils.hasText(staffNumber)) {
            return false;
        }
        return staffMapper.findByStaffNumber(staffNumber).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return false;
        }
        return staffMapper.findByIdCard(idCard).isPresent();
    }

    @Override
    public String generateStaffNumber(String position) {
        // 生成格式：职位前缀 + 年月日 + 3位序号，例如：DOC20240101001
        String positionPrefix = getPositionPrefix(position);
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = positionPrefix + datePrefix;
        
        // 查询当日最大序号
        int sequence = 1;
        String staffNumber;
        do {
            staffNumber = prefix + String.format("%03d", sequence);
            sequence++;
        } while (existsByStaffNumber(staffNumber));
        
        return staffNumber;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAllStaff() {
        return staffMapper.countAllStaff();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countStaffByPosition(String position) {
        if (!StringUtils.hasText(position)) {
            throw new ValidationException("POSITION_EMPTY", "职位不能为空");
        }
        return staffMapper.countStaffByPosition(position);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countStaffByDepartment(String department) {
        if (!StringUtils.hasText(department)) {
            throw new ValidationException("DEPARTMENT_EMPTY", "科室不能为空");
        }
        return staffMapper.countStaffByDepartment(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveStaff() {
        return staffMapper.countActiveStaff();
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateStaffWorkHours(Long staffId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("DATE_RANGE_INVALID", "日期范围不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("DATE_RANGE_INVALID", "开始日期不能晚于结束日期");
        }

        // 验证员工存在
        getStaffEntityById(staffId);

        Double workHours = scheduleMapper.sumStaffWorkHours(staffId, startDate, endDate);
        return workHours != null ? workHours : 0.0;
    }

    /**
     * 根据职位获取前缀
     */
    private String getPositionPrefix(String position) {
        switch (position) {
            case "医生":
                return "DOC";
            case "护士":
                return "NUR";
            case "药剂师":
                return "PHA";
            case "前台":
                return "REC";
            case "管理员":
                return "ADM";
            default:
                return "STA";
        }
    }

    /**
     * 根据ID获取员工实体
     */
    private Staff getStaffEntityById(Long staffId) {
        if (staffId == null) {
            throw new ValidationException("STAFF_ID_NULL", "员工ID不能为空");
        }

        Staff staff = staffMapper.selectById(staffId);
        if (staff == null) {
            throw new BusinessException("STAFF_NOT_FOUND", "员工不存在");
        }
        return staff;
    }

    /**
     * 将员工实体转换为响应DTO
     */
    private StaffResponse convertToStaffResponse(Staff staff) {
        StaffResponse response = new StaffResponse();
        response.setId(staff.getId());
        response.setStaffNumber(staff.getStaffNumber());
        response.setName(staff.getName());
        response.setPhone(staff.getPhone());
        response.setIdCard(staff.getIdCard());
        response.setBirthDate(staff.getBirthDate());
        response.setAge(staff.getAge());
        response.setGender(staff.getGender());
        response.setEmail(staff.getEmail());
        response.setAddress(staff.getAddress());
        response.setEmergencyContactName(staff.getEmergencyContactName());
        response.setEmergencyContactPhone(staff.getEmergencyContactPhone());
        response.setEmergencyContactRelation(staff.getEmergencyContactRelation());
        response.setPosition(staff.getPosition());
        response.setDepartment(staff.getDepartment());
        response.setTitle(staff.getTitle());
        response.setSpecialization(staff.getSpecialization());
        response.setEducation(staff.getEducation());
        response.setGraduateSchool(staff.getGraduateSchool());
        response.setLicenseNumber(staff.getLicenseNumber());
        response.setLicenseExpiryDate(staff.getLicenseExpiryDate());
        response.setHireDate(staff.getHireDate());
        response.setResignationDate(staff.getResignationDate());
        response.setWorkStatus(staff.getWorkStatus());
        response.setBaseSalary(staff.getBaseSalary());
        response.setExperienceYears(staff.getExperienceYears());
        response.setWorkYears(staff.getWorkYears());
        response.setRemarks(staff.getRemarks());
        response.setIsSchedulable(staff.getIsSchedulable());
        response.setLastWorkDate(staff.getLastWorkDate());
        response.setUserId(staff.getUserId());
        response.setCreatedAt(staff.getCreatedAt());
        response.setUpdatedAt(staff.getUpdatedAt());
        response.setLicenseExpiringSoon(staff.isLicenseExpiringSoon());
        response.setIsActive(staff.isActive());

        // 获取排班信息
        try {
            List<Schedule> schedules = scheduleMapper.findByStaffId(staff.getId());
            response.setSchedules(schedules);
        } catch (Exception e) {
            logger.warn("获取员工排班信息失败: staffId={}", staff.getId(), e);
        }

        return response;
    }
}