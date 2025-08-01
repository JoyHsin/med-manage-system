package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.me.joy.clinic.entity.Staff;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 医护人员Mapper接口
 */
@Mapper
public interface StaffMapper extends BaseMapper<Staff> {

    /**
     * 根据员工编号查找员工
     * @param staffNumber 员工编号
     * @return 员工信息
     */
    Optional<Staff> findByStaffNumber(@Param("staffNumber") String staffNumber);

    /**
     * 根据身份证号查找员工
     * @param idCard 身份证号
     * @return 员工信息
     */
    Optional<Staff> findByIdCard(@Param("idCard") String idCard);

    /**
     * 根据手机号查找员工
     * @param phone 手机号
     * @return 员工列表
     */
    List<Staff> findByPhone(@Param("phone") String phone);

    /**
     * 根据姓名模糊查找员工
     * @param name 员工姓名
     * @return 员工列表
     */
    List<Staff> findByNameLike(@Param("name") String name);

    /**
     * 根据职位查找员工
     * @param position 职位
     * @return 员工列表
     */
    List<Staff> findByPosition(@Param("position") String position);

    /**
     * 根据科室查找员工
     * @param department 科室
     * @return 员工列表
     */
    List<Staff> findByDepartment(@Param("department") String department);

    /**
     * 根据工作状态查找员工
     * @param workStatus 工作状态
     * @return 员工列表
     */
    List<Staff> findByWorkStatus(@Param("workStatus") String workStatus);

    /**
     * 查找在职员工
     * @return 在职员工列表
     */
    List<Staff> findActiveStaff();

    /**
     * 查找可排班员工
     * @return 可排班员工列表
     */
    List<Staff> findSchedulableStaff();

    /**
     * 根据职位和科室查找员工
     * @param position 职位
     * @param department 科室
     * @return 员工列表
     */
    List<Staff> findByPositionAndDepartment(@Param("position") String position, 
                                           @Param("department") String department);

    /**
     * 根据入职日期范围查找员工
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 员工列表
     */
    List<Staff> findByHireDateRange(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);

    /**
     * 查找执业证书即将过期的员工
     * @param expiryDate 过期日期阈值
     * @return 员工列表
     */
    List<Staff> findByLicenseExpiring(@Param("expiryDate") LocalDate expiryDate);

    /**
     * 搜索员工（根据姓名、员工编号、手机号）
     * @param keyword 关键词
     * @return 员工列表
     */
    List<Staff> searchStaff(@Param("keyword") String keyword);

    /**
     * 根据用户ID查找员工
     * @param userId 用户ID
     * @return 员工信息
     */
    Optional<Staff> findByUserId(@Param("userId") Long userId);

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
    Long countStaffByPosition(@Param("position") String position);

    /**
     * 统计指定科室的员工数量
     * @param department 科室
     * @return 员工数量
     */
    Long countStaffByDepartment(@Param("department") String department);

    /**
     * 统计在职员工数量
     * @return 在职员工数量
     */
    Long countActiveStaff();

    /**
     * 批量更新员工状态
     * @param staffIds 员工ID列表
     * @param workStatus 新状态
     * @return 更新记录数
     */
    int batchUpdateWorkStatus(@Param("staffIds") List<Long> staffIds, 
                             @Param("workStatus") String workStatus);

    /**
     * 更新员工最后工作日期
     * @param staffId 员工ID
     * @param lastWorkDate 最后工作日期
     * @return 更新记录数
     */
    int updateLastWorkDate(@Param("staffId") Long staffId, 
                          @Param("lastWorkDate") LocalDate lastWorkDate);
}