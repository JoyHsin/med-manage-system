package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.me.joy.clinic.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserMapper测试类
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsertUser() {
        // 创建测试用户
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setFullName("测试用户");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setHireDate(LocalDate.now());
        user.setEmployeeId("EMP001");
        user.setDepartment("IT部门");
        user.setPosition("开发工程师");

        // 插入用户
        int result = userMapper.insert(user);
        
        // 验证插入结果
        assertEquals(1, result);
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void testFindByUsername() {
        // 先插入一个用户
        User user = new User();
        user.setUsername("findtest");
        user.setPassword("password123");
        user.setFullName("查找测试用户");
        user.setEmail("findtest@example.com");
        userMapper.insert(user);

        // 根据用户名查找
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", "findtest");
        User foundUser = userMapper.selectOne(queryWrapper);
        
        // 验证查找结果
        assertNotNull(foundUser);
        assertEquals("findtest", foundUser.getUsername());
        assertEquals("查找测试用户", foundUser.getFullName());
    }

    @Test
    void testFindByEmail() {
        // 先插入一个用户
        User user = new User();
        user.setUsername("emailtest");
        user.setPassword("password123");
        user.setFullName("邮箱测试用户");
        user.setEmail("emailtest@example.com");
        userMapper.insert(user);

        // 根据邮箱查找
        QueryWrapper<User> emailQueryWrapper = new QueryWrapper<>();
        emailQueryWrapper.eq("email", "emailtest@example.com");
        User foundUser = userMapper.selectOne(emailQueryWrapper);
        
        // 验证查找结果
        assertNotNull(foundUser);
        assertEquals("emailtest@example.com", foundUser.getEmail());
        assertEquals("邮箱测试用户", foundUser.getFullName());
    }

    @Test
    void testQueryWrapper() {
        // 插入测试数据
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password123");
        user1.setFullName("用户1");
        user1.setDepartment("IT部门");
        user1.setEnabled(true);
        userMapper.insert(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password123");
        user2.setFullName("用户2");
        user2.setDepartment("HR部门");
        user2.setEnabled(true);
        userMapper.insert(user2);

        // 使用QueryWrapper查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department", "IT部门")
                   .eq("enabled", true);
        
        List<User> users = userMapper.selectList(queryWrapper);
        
        // 验证查询结果
        assertFalse(users.isEmpty());
        assertTrue(users.stream().allMatch(u -> "IT部门".equals(u.getDepartment())));
        assertTrue(users.stream().allMatch(User::getEnabled));
    }

    @Test
    void testUpdateUser() {
        // 先插入一个用户
        User user = new User();
        user.setUsername("updatetest");
        user.setPassword("password123");
        user.setFullName("更新测试用户");
        user.setEmail("updatetest@example.com");
        userMapper.insert(user);

        // 更新用户信息
        user.setFullName("已更新的用户");
        user.setDepartment("新部门");
        int result = userMapper.updateById(user);
        
        // 验证更新结果
        assertEquals(1, result);
        
        // 重新查询验证
        User updatedUser = userMapper.selectById(user.getId());
        assertEquals("已更新的用户", updatedUser.getFullName());
        assertEquals("新部门", updatedUser.getDepartment());
    }

    @Test
    void testLogicalDelete() {
        // 先插入一个用户
        User user = new User();
        user.setUsername("deletetest");
        user.setPassword("password123");
        user.setFullName("删除测试用户");
        userMapper.insert(user);
        
        Long userId = user.getId();
        
        // 逻辑删除
        int result = userMapper.deleteById(userId);
        assertEquals(1, result);
        
        // 验证逻辑删除后查询不到
        User deletedUser = userMapper.selectById(userId);
        assertNull(deletedUser);
        
        // 但是数据库中记录仍然存在，只是deleted字段被设置为1
    }
}