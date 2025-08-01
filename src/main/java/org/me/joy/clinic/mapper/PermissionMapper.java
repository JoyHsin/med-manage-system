package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.Permission;

import java.util.List;

/**
 * 权限数据访问接口
 * 提供权限相关的数据库操作方法
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据权限代码查找权限
     * @param permissionCode 权限代码
     * @return 权限信息
     */
    @Select("SELECT * FROM permissions WHERE permission_code = #{permissionCode} AND deleted = 0")
    Permission findByPermissionCode(String permissionCode);

    /**
     * 根据权限名称查找权限
     * @param permissionName 权限名称
     * @return 权限信息
     */
    @Select("SELECT * FROM permissions WHERE permission_name = #{permissionName} AND deleted = 0")
    Permission findByPermissionName(String permissionName);

    /**
     * 根据模块查找权限
     * @param module 模块名称
     * @return 权限列表
     */
    @Select("SELECT * FROM permissions WHERE module = #{module} AND deleted = 0 ORDER BY permission_name")
    List<Permission> findByModule(String module);

    /**
     * 查找所有启用的权限
     * @return 权限列表
     */
    @Select("SELECT * FROM permissions WHERE enabled = 1 AND deleted = 0 ORDER BY permission_name")
    List<Permission> findAllEnabled();
    
    /**
     * 查找所有启用的权限（别名方法，与findAllEnabled功能相同）
     * @return 权限列表
     */
    @Select("SELECT * FROM permissions WHERE enabled = 1 AND deleted = 0 ORDER BY permission_name")
    List<Permission> findByEnabledTrue();

    /**
     * 根据模块查找所有启用的权限
     * @param module 模块名称
     * @return 权限列表
     */
    @Select("SELECT * FROM permissions WHERE module = #{module} AND enabled = 1 AND deleted = 0 ORDER BY permission_name")
    List<Permission> findByModuleAndEnabled(String module);
    
    /**
     * 根据模块查找所有启用的权限（别名方法，与findByModuleAndEnabled功能相同）
     * @param module 模块名称
     * @return 权限列表
     */
    @Select("SELECT * FROM permissions WHERE module = #{module} AND enabled = 1 AND deleted = 0 ORDER BY permission_name")
    List<Permission> findByModuleAndEnabledTrue(String module);

    /**
     * 根据用户ID查找用户的权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("SELECT DISTINCT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN roles r ON rp.role_id = r.id " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "INNER JOIN users u ON ur.user_id = u.id " +
            "WHERE u.id = #{userId} AND p.enabled = 1 AND r.enabled = 1 AND p.deleted = 0 AND r.deleted = 0 AND u.deleted = 0")
    List<Permission> findPermissionsByUserId(Long userId);


    
    /**
     * 根据用户ID查找用户的权限列表（别名方法，与findPermissionsByUserId功能相同）
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("SELECT DISTINCT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN roles r ON rp.role_id = r.id " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "INNER JOIN users u ON ur.user_id = u.id " +
            "WHERE u.id = #{userId} AND p.enabled = 1 AND r.enabled = 1 AND p.deleted = 0 AND r.deleted = 0 AND u.deleted = 0")
    List<Permission> findByUserId(Long userId);
}