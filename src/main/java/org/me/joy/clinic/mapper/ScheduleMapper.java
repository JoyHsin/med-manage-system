package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.me.joy.clinic.entity.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 排班Mapper接口
 */
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    /**
     * 根据员工ID查找排班
     * @param staffId 员工ID
     * @return 排班列表
     */
    List<Schedule> findByStaffId(@Param("staffId") Long staffId);

    /**
     * 根据员工ID和日期查找排班
     * @param staffId 员工ID
     * @param scheduleDate 排班日期
     * @return 排班信息
     */
    Optional<Schedule> findByStaffIdAndDate(@Param("staffId") Long staffId, 
                                           @Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 根据日期查找所有排班
     * @param scheduleDate 排班日期
     * @return 排班列表
     */
    List<Schedule> findByDate(@Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 根据日期范围查找排班
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    List<Schedule> findByDateRange(@Param("startDate") LocalDate startDate, 
                                  @Param("endDate") LocalDate endDate);

    /**
     * 根据员工ID和日期范围查找排班
     * @param staffId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    List<Schedule> findByStaffIdAndDateRange(@Param("staffId") Long staffId,
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);

    /**
     * 根据班次类型查找排班
     * @param shiftType 班次类型
     * @param scheduleDate 排班日期
     * @return 排班列表
     */
    List<Schedule> findByShiftTypeAndDate(@Param("shiftType") String shiftType, 
                                         @Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 根据科室查找排班
     * @param department 科室
     * @param scheduleDate 排班日期
     * @return 排班列表
     */
    List<Schedule> findByDepartmentAndDate(@Param("department") String department, 
                                          @Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 根据状态查找排班
     * @param status 排班状态
     * @param scheduleDate 排班日期
     * @return 排班列表
     */
    List<Schedule> findByStatusAndDate(@Param("status") String status, 
                                      @Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 查找加班排班
     * @param scheduleDate 排班日期
     * @return 加班排班列表
     */
    List<Schedule> findOvertimeSchedules(@Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 查找今日排班
     * @param today 今日日期
     * @return 今日排班列表
     */
    List<Schedule> findTodaySchedules(@Param("today") LocalDate today);

    /**
     * 查找本周排班
     * @param startOfWeek 本周开始日期
     * @param endOfWeek 本周结束日期
     * @return 本周排班列表
     */
    List<Schedule> findWeeklySchedules(@Param("startOfWeek") LocalDate startOfWeek, 
                                      @Param("endOfWeek") LocalDate endOfWeek);

    /**
     * 查找本月排班
     * @param startOfMonth 本月开始日期
     * @param endOfMonth 本月结束日期
     * @return 本月排班列表
     */
    List<Schedule> findMonthlySchedules(@Param("startOfMonth") LocalDate startOfMonth, 
                                       @Param("endOfMonth") LocalDate endOfMonth);

    /**
     * 检查排班冲突
     * @param staffId 员工ID
     * @param scheduleDate 排班日期
     * @param excludeId 排除的排班ID（用于更新时）
     * @return 冲突的排班列表
     */
    List<Schedule> findConflictingSchedules(@Param("staffId") Long staffId,
                                           @Param("scheduleDate") LocalDate scheduleDate,
                                           @Param("excludeId") Long excludeId);

    /**
     * 统计员工的排班数量
     * @param staffId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班数量
     */
    Long countStaffSchedules(@Param("staffId") Long staffId,
                            @Param("startDate") LocalDate startDate, 
                            @Param("endDate") LocalDate endDate);

    /**
     * 统计员工的工作时长
     * @param staffId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 工作时长（小时）
     */
    Double sumStaffWorkHours(@Param("staffId") Long staffId,
                            @Param("startDate") LocalDate startDate, 
                            @Param("endDate") LocalDate endDate);

    /**
     * 删除员工的所有排班
     * @param staffId 员工ID
     * @return 删除记录数
     */
    int deleteByStaffId(@Param("staffId") Long staffId);

    /**
     * 批量插入排班
     * @param schedules 排班列表
     * @return 插入记录数
     */
    int batchInsert(@Param("schedules") List<Schedule> schedules);

    /**
     * 批量更新排班状态
     * @param scheduleIds 排班ID列表
     * @param status 新状态
     * @return 更新记录数
     */
    int batchUpdateStatus(@Param("scheduleIds") List<Long> scheduleIds, 
                         @Param("status") String status);
}