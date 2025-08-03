package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.User;

import java.util.List;

/**
 * 用户数据访问接口
 * 提供用户相关的数据库操作方法
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND deleted = 0")
    User findByUsername(String username);
    
    /**
     * 根据用户名查找用户（返回Optional）
     * @param username 用户名
     * @return 用户信息（Optional包装）
     */
    default java.util.Optional<User> findByUsernameOptional(String username) {
        return java.util.Optional.ofNullable(findByUsername(username));
    }
    
    /**
     * 保存用户信息
     * @param user 用户信息
     * @return 保存后的用户信息
     */
    default User save(User user) {
        if (user.getId() == null) {
            insert(user);
        } else {
            updateById(user);
        }
        return user;
    }

    /**
     * 根据员工工号查找用户
     * @param employeeId 员工工号
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE employee_id = #{employeeId} AND deleted = 0")
    User findByEmployeeId(String employeeId);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱地址
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE email = #{email} AND deleted = 0")
    User findByEmail(String email);

    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE phone = #{phone} AND deleted = 0")
    User findByPhone(String phone);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username} AND deleted = 0")
    boolean existsByUsername(String username);

    /**
     * 检查员工工号是否存在
     * @param employeeId 员工工号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE employee_id = #{employeeId} AND deleted = 0")
    boolean existsByEmployeeId(String employeeId);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱地址
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email} AND deleted = 0")
    boolean existsByEmail(String email);

    /**
     * 根据用户名和启用状态查找用户（包含角色信息）
     * @param username 用户名
     * @param enabled 是否启用
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND enabled = #{enabled} AND deleted = 0")
    User findByUsernameAndEnabled(@Param("username") String username, @Param("enabled") Boolean enabled);

    /**
     * 根据部门查找用户列表
     * @param department 部门名称
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE department = #{department} AND enabled = 1 AND deleted = 0 ORDER BY full_name")
    List<User> findByDepartmentAndEnabledTrue(String department);

    /**
     * 根据角色ID查找用户列表
     * @param roleId 角色ID
     * @return 用户列表
     */
    @Select("SELECT u.* FROM users u " +
            "INNER JOIN user_roles ur ON u.id = ur.user_id " +
            "WHERE ur.role_id = #{roleId} AND u.deleted = 0")
    List<User> findByRoleId(Long roleId);

    /**
     * 根据用户名获取用户的所有权限代码
     * @param username 用户名
     * @return 权限代码列表
     */
    @Select("SELECT DISTINCT p.permission_code FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN user_roles ur ON rp.role_id = ur.role_id " +
            "INNER JOIN users u ON ur.user_id = u.id " +
            "WHERE u.username = #{username} AND u.enabled = 1 AND u.deleted = 0 " +
            "AND p.enabled = 1")
    List<String> findPermissionsByUsername(String username);
}