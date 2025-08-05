# 端到端集成测试文档

## 概述

本目录包含社区诊所管理系统的端到端集成测试，测试覆盖了从挂号到收费的完整业务流程，以及各个模块之间的集成。

## 测试结构

### 测试类说明

1. **CompleteWorkflowIntegrationTest** - 完整业务流程集成测试
   - 测试从患者挂号到收费结算的完整流程
   - 验证各模块间的数据传递和状态同步
   - 包含异常情况处理测试

2. **NurseWorkstationIntegrationTest** - 护士工作台集成测试
   - 测试分诊叫号完整流程
   - 测试生命体征录入和验证
   - 测试医嘱执行和记录
   - 测试异常数据处理

3. **PharmacyManagementIntegrationTest** - 药房管理集成测试
   - 测试处方验证和调剂流程
   - 测试库存管理和预警机制
   - 测试药品退回处理
   - 测试库存不足等异常情况

4. **SystemIntegrationTest** - 系统集成测试
   - 测试跨模块的系统集成
   - 测试并发处理能力
   - 测试系统错误恢复
   - 测试性能指标

5. **IntegrationTestSuite** - 集成测试套件
   - 统一管理所有集成测试的执行顺序
   - 提供测试环境的初始化和清理

## 运行方式

### 1. 运行所有集成测试

```bash
# 使用Maven运行集成测试
mvn clean verify -P integration-test

# 或者运行所有测试（单元测试 + 集成测试）
mvn clean verify -P all-tests
```

### 2. 运行特定的集成测试

```bash
# 运行完整业务流程测试
mvn test -Dtest=CompleteWorkflowIntegrationTest -Dspring.profiles.active=integration

# 运行护士工作台测试
mvn test -Dtest=NurseWorkstationIntegrationTest -Dspring.profiles.active=integration

# 运行药房管理测试
mvn test -Dtest=PharmacyManagementIntegrationTest -Dspring.profiles.active=integration

# 运行系统集成测试
mvn test -Dtest=SystemIntegrationTest -Dspring.profiles.active=integration
```

### 3. 在IDE中运行

在IntelliJ IDEA或Eclipse中：
1. 右键点击测试类或测试方法
2. 选择"Run"或"Debug"
3. 确保VM options中包含：`-Dspring.profiles.active=integration`

## 测试环境配置

### 数据库配置
- 使用H2内存数据库进行测试
- 每次测试运行时自动创建和销毁数据库
- 测试数据通过`integration-test-data.sql`初始化

### 配置文件
- `application-integration.yml` - 集成测试专用配置
- 包含测试数据库连接、日志级别、性能阈值等配置

### 测试数据
- `integration-test-data.sql` - 基础测试数据
- 包含角色、权限、用户、员工、药品等基础数据
- 每个测试类会根据需要创建额外的测试数据

## 测试覆盖范围

### 业务流程测试
- ✅ 患者建档和预约
- ✅ 前台挂号登记
- ✅ 护士分诊叫号
- ✅ 生命体征录入
- ✅ 医生诊疗开处方
- ✅ 费用计算和收费
- ✅ 药房调剂发药
- ✅ 数据分析报表

### 护士工作台测试
- ✅ 患者队列管理
- ✅ 分诊叫号流程
- ✅ 生命体征录入和验证
- ✅ 医嘱执行和记录
- ✅ 异常情况处理

### 药房管理测试
- ✅ 处方验证和调剂
- ✅ 库存管理和预警
- ✅ 药品出入库操作
- ✅ 过期药品管理
- ✅ 药品退回处理

### 系统集成测试
- ✅ 跨模块数据一致性
- ✅ 并发处理能力
- ✅ 错误恢复机制
- ✅ 性能指标验证

## 性能指标

### 响应时间要求
- 单次查询响应时间 < 1秒
- 批量操作响应时间 < 10秒
- 用户登录响应时间 < 500ms

### 并发处理能力
- 支持10个并发线程同时操作
- 支持50个并发用户访问
- 数据一致性保证

### 数据完整性
- 事务回滚机制验证
- 数据约束检查
- 业务规则验证

## 测试报告

### 代码覆盖率
集成测试运行后会生成JaCoCo代码覆盖率报告：
- 位置：`target/site/jacoco-it/index.html`
- 目标覆盖率：≥ 80%

### 测试结果
- Maven Surefire报告：`target/surefire-reports/`
- Maven Failsafe报告：`target/failsafe-reports/`
- 控制台输出包含详细的测试执行信息

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查H2数据库配置
   - 确认测试配置文件正确加载

2. **测试数据初始化失败**
   - 检查`integration-test-data.sql`语法
   - 确认Flyway配置正确

3. **权限验证失败**
   - 检查Spring Security测试配置
   - 确认@WithMockUser注解正确使用

4. **并发测试失败**
   - 检查线程池配置
   - 确认数据库事务隔离级别

### 调试技巧

1. **启用详细日志**
   ```yaml
   logging:
     level:
       org.me.joy.clinic: DEBUG
       org.springframework.web: DEBUG
   ```

2. **使用H2控制台**
   - 访问：http://localhost:8080/h2-console
   - JDBC URL: jdbc:h2:mem:clinic_integration_test

3. **断点调试**
   - 在IDE中设置断点
   - 使用Debug模式运行测试

## 持续集成

### CI/CD配置
```yaml
# GitHub Actions示例
- name: Run Integration Tests
  run: mvn clean verify -P integration-test
  
- name: Upload Coverage Reports
  uses: codecov/codecov-action@v3
  with:
    file: target/site/jacoco-it/jacoco.xml
```

### 测试策略
- 每次代码提交都运行单元测试
- 每日构建运行完整集成测试
- 发布前运行所有测试套件

## 维护指南

### 添加新的集成测试
1. 创建新的测试类，继承基础测试配置
2. 使用`@SpringBootTest`和`@ActiveProfiles("integration")`注解
3. 在`setUp()`方法中准备测试数据
4. 编写测试方法，验证业务流程
5. 更新本文档说明新增的测试内容

### 更新测试数据
1. 修改`integration-test-data.sql`文件
2. 确保数据的一致性和完整性
3. 运行所有集成测试验证修改

### 性能调优
1. 监控测试执行时间
2. 优化数据库查询
3. 调整并发参数
4. 更新性能指标阈值

## 联系信息

如有问题或建议，请联系开发团队：
- 邮箱：dev-team@clinic.com
- 项目地址：https://github.com/clinic/management-system