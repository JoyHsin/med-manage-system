package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.Role;

import java.util.List;

/**
 * 角色数据访问接口
 * 提供角色相关的数据库操作方法
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色代码查找角色
     * @param roleCode 角色代码
     * @return 角色信息
     */
    @Select("SELECT * FROM roles WHERE role_code = #{roleCode} AND deleted = 0")
    Role findByRoleCode(String roleCode);

    /**
     * 根据角色名称查找角色
     * @param roleName 角色名称
     * @return 角色信息
     */
    @Select("SELECT * FROM roles WHERE role_name = #{roleName} AND deleted = 0")
    Role findByRoleName(String roleName);

    /**
     * 根据角色代码查找角色（不检查删除状态）
     * @param roleCode 角色代码
     * @return 角色信息
     */
    @Select("SELECT * FROM roles WHERE role_code = #{roleCode}")
    Role findByRoleCodeIgnoreDeleted(String roleCode);

    /**
     * 查找所有启用的角色
     * @return 角色列表
     */
    @Select("SELECT * FROM roles WHERE enabled = 1 AND deleted = 0 ORDER BY role_name")
    List<Role> findAllEnabled();
    
    /**
     * 查找所有启用的角色（别名方法，与findAllEnabled功能相同）
     * @return 角色列表
     */
    @Select("SELECT * FROM roles WHERE enabled = 1 AND deleted = 0 ORDER BY role_name")
    List<Role> findByEnabledTrue();

    /**
     * 查找所有系统预设角色
     * @return 角色列表
     */
    @Select("SELECT * FROM roles WHERE is_system_role = 1 AND deleted = 0 ORDER BY role_name")
    List<Role> findAllSystemRoles();

    /**
     * 查找所有系统预设角色并按名称排序
     * @return 角色列表
     */
    @Select("SELECT * FROM roles WHERE is_system_role = 1 AND deleted = 0 ORDER BY role_name")
    List<Role> findByIsSystemRoleTrue();

    /**
     * 根据角色代码查找角色（忽略大小写）
     * @param roleCode 角色代码
     * @return 角色信息
     */
    @Select("SELECT * FROM roles WHERE LOWER(role_code) = LOWER(#{roleCode}) AND deleted = 0")
    Role findByRoleCodeIgnoreCase(String roleCode);
    
    /**
     * 统计使用指定角色的用户数量
     * @param roleId 角色ID
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM user_roles WHERE role_id = #{roleId}")
    Long countUsersByRoleId(Long roleId);
}