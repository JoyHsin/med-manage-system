package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.CreateStaffRequest;
import org.me.joy.clinic.dto.StaffResponse;
import org.me.joy.clinic.dto.UpdateStaffRequest;
import org.me.joy.clinic.entity.Schedule;

import java.time.LocalDate;
import java.util.List;

/**
 * 医护人员管理服务接口
 * 提供医护人员档案的创建、更新、查询和管理功能
 */
public interface StaffManagementService {

    /**
     * 创建新员工
     * @param createStaffRequest 创建员工请求
     * @return 员工响应信息
     */
    StaffResponse createStaff(CreateStaffRequest createStaffRequest);

    /**
     * 更新员工信息
     * @param staffId 员工ID
     * @param updateStaffRequest 更新员工请求
     * @return 更新后的员工信息
     */
    StaffResponse updateStaff(Long staffId, UpdateStaffRequest updateStaffRequest);

    /**
     * 根据ID获取员工信息
     * @param staffId 员工ID
     * @return 员工信息
     */
    StaffResponse getStaffById(Long staffId);

    /**
     * 根据员工编号获取员工信息
     * @param staffNumber 员工编号
     * @return 员工信息
     */
    StaffResponse getStaffByNumber(String staffNumber);

    /**
     * 根据身份证号获取员工信息
     * @param idCard 身份证号
     * @return 员工信息
     */
    StaffResponse getStaffByIdCard(String idCard);

    /**
     * 根据用户ID获取员工信息
     * @param userId 用户ID
     * @return 员工信息
     */
    StaffResponse getStaffByUserId(Long userId);

    /**
     * 获取所有员工列表
     * @return 员工列表
     */
    List<StaffResponse> getAllStaff();

    /**
     * 根据职位获取员工列表
     * @param position 职位
     * @return 员工列表
     */
    List<StaffResponse> getStaffByPosition(String position);

    /**
     * 根据科室获取员工列表
     * @param department 科室
     * @return 员工列表
     */
    List<StaffResponse> getStaffByDepartment(String department);

    /**
     * 根据工作状态获取员工列表
     * @param workStatus 工作状态
     * @return 员工列表
     */
    List<StaffResponse> getStaffByWorkStatus(String workStatus);

    /**
     * 获取在职员工列表
     * @return 在职员工列表
     */
    List<StaffResponse> getActiveStaff();

    /**
     * 获取可排班员工列表
     * @return 可排班员工列表
     */
    List<StaffResponse> getSchedulableStaff();

    /**
     * 根据职位和科室获取员工列表
     * @param position 职位
     * @param department 科室
     * @return 员工列表
     */
    List<StaffResponse> getStaffByPositionAndDepartment(String position, String department);

    /**
     * 搜索员工
     * @param keyword 关键词（姓名、员工编号、手机号）
     * @return 员工列表
     */
    List<StaffResponse> searchStaff(String keyword);

    /**
     * 获取执业证书即将过期的员工
     * @param days 提前天数（默认30天）
     * @return 员工列表
     */
    List<StaffResponse> getStaffWithExpiringLicense(Integer days);

    /**
     * 删除员工（软删除）
     * @param staffId 员工ID
     */
    void deleteStaff(Long staffId);

    /**
     * 更新员工工作状态
     * @param staffId 员工ID
     * @param workStatus 新状态
     */
    void updateStaffWorkStatus(Long staffId, String workStatus);

    /**
     * 员工离职处理
     * @param staffId 员工ID
     * @param resignationDate 离职日期
     */
    void resignStaff(Long staffId, LocalDate resignationDate);

    /**
     * 员工复职处理
     * @param staffId 员工ID
     */
    void reinstateStaff(Long staffId);

    /**
     * 更新员工排班状态
     * @param staffId 员工ID
     * @param isSchedulable 是否可排班
     */
    void updateStaffSchedulableStatus(Long staffId, Boolean isSchedulable);

    /**
     * 创建员工排班
     * @param staffId 员工ID
     * @param schedule 排班信息
     * @return 创建的排班
     */
    Schedule createSchedule(Long staffId, Schedule schedule);

    /**
     * 更新员工排班
     * @param scheduleId 排班ID
     * @param schedule 排班信息
     * @return 更新后的排班
     */
    Schedule updateSchedule(Long scheduleId, Schedule schedule);

    /**
     * 删除员工排班
     * @param scheduleId 排班ID
     */
    void deleteSchedule(Long scheduleId);

    /**
     * 获取员工排班
     * @param staffId 员工ID
     * @return 排班列表
     */
    List<Schedule> getStaffSchedules(Long staffId);

    /**
     * 获取员工指定日期范围的排班
     * @param staffId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    List<Schedule> getStaffSchedulesByDateRange(Long staffId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取指定日期的所有排班
     * @param date 日期
     * @return 排班列表
     */
    List<Schedule> getSchedulesByDate(LocalDate date);

    /**
     * 获取今日排班
     * @return 今日排班列表
     */
    List<Schedule> getTodaySchedules();

    /**
     * 获取本周排班
     * @return 本周排班列表
     */
    List<Schedule> getWeeklySchedules();

    /**
     * 获取本月排班
     * @return 本月排班列表
     */
    List<Schedule> getMonthlySchedules();

    /**
     * 批量创建排班
     * @param schedules 排班列表
     * @return 创建的排班列表
     */
    List<Schedule> batchCreateSchedules(List<Schedule> schedules);

    /**
     * 检查排班冲突
     * @param staffId 员工ID
     * @param scheduleDate 排班日期
     * @param excludeScheduleId 排除的排班ID（用于更新时）
     * @return 是否有冲突
     */
    boolean hasScheduleConflict(Long staffId, LocalDate scheduleDate, Long excludeScheduleId);

    /**
     * 检查员工编号是否存在
     * @param staffNumber 员工编号
     * @return 是否存在
     */
    boolean existsByStaffNumber(String staffNumber);

    /**
     * 检查身份证号是否存在
     * @param idCard 身份证号
     * @return 是否存在
     */
    boolean existsByIdCard(String idCard);

    /**
     * 生成员工编号
     * @param position 职位
     * @return 员工编号
     */
    String generateStaffNumber(String position);

    /**
     * 统计员工总数
     * @return 员工总数
     */
    Long countAllStaff();

    /**
     * 统计指定职位的员工数量
     * @param position 职位
     * @return 员工数量
     */
    Long countStaffByPosition(String position);

    /**
     * 统计指定科室的员工数量
     * @param department 科室
     * @return 员工数量
     */
    Long countStaffByDepartment(String department);

    /**
     * 统计在职员工数量
     * @return 在职员工数量
     */
    Long countActiveStaff();

    /**
     * 统计员工工作时长
     * @param staffId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 工作时长（小时）
     */
    Double calculateStaffWorkHours(Long staffId, LocalDate startDate, LocalDate endDate);
}