# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此代码仓库中工作提供指导。

## 项目概述

这是一个基于 Spring Boot 3.2.0、MyBatis-Plus 和 JWT 认证构建的**社区诊所管理系统**。该系统管理患者、员工、用户、角色和权限，具有完整的 RBAC（基于角色的访问控制）系统。

## 开发命令

### 构建与运行
```bash
# 构建项目
mvn clean compile

# 运行测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserManagementServiceImplTest

# 运行特定测试方法
mvn test -Dtest=UserManagementServiceImplTest#testCreateUser

# 启动应用程序
mvn spring-boot:run

# 打包应用程序
mvn clean package
```

### 数据库
- **生产环境**: MySQL 数据库 `localhost:3306/clinic_management`
- **测试环境**: H2 内存数据库
- **数据库迁移**: 使用 Flyway 进行版本管理（迁移脚本位于 `src/main/resources/db/migration/`）

## 架构与核心组件

### 核心架构
- **分层结构**: Controller → Service → Mapper → Database
- **基础实体**: 所有实体继承 `BaseEntity`，自动管理 `id`、`createdAt`、`updatedAt` 和 `deleted` 字段
- **安全认证**: 基于 JWT 的身份验证和 Spring Security
- **权限系统**: 基于 AOP 的自定义权限检查，使用 `@RequiresPermission` 注解

### 关键技术
- **ORM**: MyBatis-Plus，XML 映射文件位于 `src/main/resources/mapper/`
- **数据库迁移**: Flyway，SQL 脚本位于 `src/main/resources/db/migration/`
- **身份验证**: JWT 令牌，30分钟过期时间
- **测试**: Spring Boot Test，使用 H2 数据库和测试数据 `src/test/resources/data.sql`

### 实体关系
- **用户（Users）** 拥有 **角色（Roles）**（通过 `user_roles` 表多对多关联）
- **角色（Roles）** 拥有 **权限（Permissions）**（通过 `role_permissions` 表多对多关联）
- **患者（Patients）** 拥有 **病史（MedicalHistory）** 和 **过敏史（AllergyHistory）**（一对多）
- **员工（Staff）** 管理 **排班（Schedules）**（一对多）
- **药品（Medicine）** 库存通过 **库存水平（InventoryLevel）** 和 **库存交易（StockTransaction）** 追踪

### 安全配置
- JWT 密钥和过期时间在 `application.yml` 中配置
- 公开端点：`/api/auth/**`、`/h2-console/**`（仅测试环境）
- 其他所有端点需要身份验证
- 使用自定义 `@RequiresPermission` 注解进行基于权限的授权

### MyBatis-Plus 配置
- `createdAt`、`updatedAt`、`deleted` 字段的自动填充处理器
- 启用分页支持
- 配置逻辑删除（deleted=1 表示已删除记录）
- 启用数据库字段的驼峰命名转换

## 开发指南

### 添加新实体
1. 继承 `BaseEntity` 以获得自动时间戳和软删除支持
2. 创建对应的 Mapper 接口和 XML 文件
3. 在 `db/migration/` 中添加 Flyway 迁移脚本
4. 遵循命名约定：`V{版本号}__描述.sql`

### 添加新权限
1. 通过迁移脚本在数据库中定义权限
2. 在控制器方法上使用 `@RequiresPermission("权限代码")` 注解
3. 权限检查由 `PermissionAspect` AOP 组件处理

### 测试
- 使用 `@SpringBootTest` 和测试配置文件（`application-test.yml`）
- 测试数据通过 `src/test/resources/data.sql` 初始化
- 测试期间可在 `/h2-console` 访问 H2 控制台

### 配置文件
- **主配置**: `src/main/resources/application.yml`（MySQL，生产环境设置）
- **测试配置**: `src/test/resources/application-test.yml`（H2，测试环境设置）
- **服务器**: 运行在端口 8080，上下文路径 `/api`