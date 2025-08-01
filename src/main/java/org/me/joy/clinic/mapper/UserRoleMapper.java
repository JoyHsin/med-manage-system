package org.me.joy.clinic.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.Role;

import java.util.List;

/**
 * 用户角色关联数据访问接口
 * 提供用户角色关联相关的数据库操作方法
 */
@Mapper
public interface UserRoleMapper {

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Insert("INSERT INTO user_roles (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void addUserRole(Long userId, Long roleId);

    /**
     * 移除用户角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    void removeUserRole(Long userId, Long roleId);

    /**
     * 移除用户的所有角色
     * @param userId 用户ID
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId}")
    void removeAllUserRoles(Long userId);

    /**
     * 移除角色的所有用户关联
     * @param roleId 角色ID
     */
    @Delete("DELETE FROM user_roles WHERE role_id = #{roleId}")
    void removeAllRoleUsers(Long roleId);

    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 关联数量（0表示没有，1表示有）
     */
    @Select("SELECT COUNT(*) FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    int countUserRole(Long userId, Long roleId);

    /**
     * 根据用户ID查找用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0 " +
            "ORDER BY r.role_name")
    List<Role> findRolesByUserId(Long userId);

    /**
     * 根据角色ID查找拥有该角色的用户数量
     * @param roleId 角色ID
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM user_roles ur " +
            "INNER JOIN users u ON ur.user_id = u.id " +
            "WHERE ur.role_id = #{roleId} AND u.deleted = 0")
    Long countUsersByRoleId(Long roleId);
}