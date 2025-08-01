package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.me.joy.clinic.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RoleMapper测试类
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    void testInsertRole() {
        // 创建测试角色
        Role role = new Role();
        role.setRoleCode("TEST_ROLE");
        role.setRoleName("测试角色");
        role.setDescription("这是一个测试角色");
        role.setEnabled(true);
        role.setIsSystemRole(false);

        // 插入角色
        int result = roleMapper.insert(role);
        
        // 验证插入结果
        assertEquals(1, result);
        assertNotNull(role.getId());
        assertNotNull(role.getCreatedAt());
        assertNotNull(role.getUpdatedAt());
    }

    @Test
    void testFindByRoleCode() {
        // 先插入一个角色
        Role role = new Role();
        role.setRoleCode("FIND_TEST");
        role.setRoleName("查找测试角色");
        role.setDescription("用于测试查找功能");
        roleMapper.insert(role);

        // 根据角色代码查找
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_code", "FIND_TEST");
        Role foundRole = roleMapper.selectOne(queryWrapper);
        
        // 验证查找结果
        assertNotNull(foundRole);
        assertEquals("FIND_TEST", foundRole.getRoleCode());
        assertEquals("查找测试角色", foundRole.getRoleName());
    }

    @Test
    void testQueryByEnabled() {
        // 插入启用和禁用的角色
        Role enabledRole = new Role();
        enabledRole.setRoleCode("ENABLED_ROLE");
        enabledRole.setRoleName("启用角色");
        enabledRole.setEnabled(true);
        roleMapper.insert(enabledRole);

        Role disabledRole = new Role();
        disabledRole.setRoleCode("DISABLED_ROLE");
        disabledRole.setRoleName("禁用角色");
        disabledRole.setEnabled(false);
        roleMapper.insert(disabledRole);

        // 查找所有启用的角色
        QueryWrapper<Role> enabledQuery = new QueryWrapper<>();
        enabledQuery.eq("enabled", true);
        List<Role> enabledRoles = roleMapper.selectList(enabledQuery);
        
        // 验证查询结果
        assertFalse(enabledRoles.isEmpty());
        assertTrue(enabledRoles.stream().allMatch(Role::getEnabled));
    }

    @Test
    void testQueryBySystemRole() {
        // 插入系统角色和非系统角色
        Role systemRole = new Role();
        systemRole.setRoleCode("SYSTEM_ROLE");
        systemRole.setRoleName("系统角色");
        systemRole.setIsSystemRole(true);
        roleMapper.insert(systemRole);

        Role customRole = new Role();
        customRole.setRoleCode("CUSTOM_ROLE");
        customRole.setRoleName("自定义角色");
        customRole.setIsSystemRole(false);
        roleMapper.insert(customRole);

        // 查找所有系统角色
        QueryWrapper<Role> systemQuery = new QueryWrapper<>();
        systemQuery.eq("is_system_role", true);
        List<Role> systemRoles = roleMapper.selectList(systemQuery);
        
        // 验证查询结果
        assertFalse(systemRoles.isEmpty());
        assertTrue(systemRoles.stream().allMatch(Role::getIsSystemRole));
    }

    @Test
    void testUpdateRole() {
        // 先插入一个角色
        Role role = new Role();
        role.setRoleCode("UPDATE_TEST");
        role.setRoleName("更新测试角色");
        role.setDescription("原始描述");
        roleMapper.insert(role);

        // 更新角色信息
        role.setRoleName("已更新的角色");
        role.setDescription("更新后的描述");
        int result = roleMapper.updateById(role);
        
        // 验证更新结果
        assertEquals(1, result);
        
        // 重新查询验证
        Role updatedRole = roleMapper.selectById(role.getId());
        assertEquals("已更新的角色", updatedRole.getRoleName());
        assertEquals("更新后的描述", updatedRole.getDescription());
    }

    @Test
    void testLogicalDelete() {
        // 先插入一个角色
        Role role = new Role();
        role.setRoleCode("DELETE_TEST");
        role.setRoleName("删除测试角色");
        roleMapper.insert(role);
        
        Long roleId = role.getId();
        
        // 逻辑删除
        int result = roleMapper.deleteById(roleId);
        assertEquals(1, result);
        
        // 验证逻辑删除后查询不到
        Role deletedRole = roleMapper.selectById(roleId);
        assertNull(deletedRole);
    }
}