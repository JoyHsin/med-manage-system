package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.Permission;

import java.util.List;

/**
 * 角色权限关联数据访问接口
 * 提供角色权限关联相关的数据库操作方法
 */
@Mapper
public interface RolePermissionMapper {

    /**
     * 添加角色权限关联
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 影响行数
     */
    @Insert("INSERT INTO role_permissions (role_id, permission_id) VALUES (#{roleId}, #{permissionId})")
    int addRolePermission(Long roleId, Long permissionId);

    /**
     * 删除角色权限关联
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 影响行数
     */
    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int removeRolePermission(Long roleId, Long permissionId);

    /**
     * 删除角色的所有权限关联
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId}")
    int removeAllRolePermissions(Long roleId);

    /**
     * 统计角色权限关联数量
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 关联数量
     */
    @Select("SELECT COUNT(*) FROM role_permissions WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int countRolePermission(Long roleId, Long permissionId);

    /**
     * 根据角色ID查找权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("SELECT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.enabled = 1 AND p.deleted = 0")
    List<Permission> findPermissionsByRoleId(Long roleId);
}