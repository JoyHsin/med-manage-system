# 社区诊所管理系统

这是一个基于Spring Boot和React的社区诊所管理系统，包含前后端分离的架构设计。

## 项目结构

- `src/` - 后端Java项目（Spring Boot）
- `med-manage-system-frontend/` - 前端项目（React + TypeScript + Vite）

## 技术栈

### 后端

- Spring Boot 3.2.0
- Spring Security
- MyBatis-Plus
- MySQL
- Flyway
- JWT认证

### 前端

- React 19
- TypeScript
- Vite 7
- Ant Design 5
- React Router 7
- Axios

## 开发环境设置

### 后端

1. 确保已安装JDK 17+和Maven
2. 创建MySQL数据库：`clinic_management`
3. 配置数据库连接（如需修改）：`src/main/resources/application.yml`
4. 启动后端服务：
   ```bash
   mvn spring-boot:run
   ```
   服务将在 http://localhost:8080 启动

### 前端

1. 确保已安装Node.js 18+和npm 9+
2. 进入前端项目目录：
   ```bash
   cd med-manage-system-frontend
   ```
3. 安装依赖：
   ```bash
   npm install
   ```
4. 启动开发服务器：
   ```bash
   npm run dev
   ```
   开发服务器将在 http://localhost:5173 启动

## 功能特性

- 用户认证与授权
- 患者管理
- 预约管理
- 电子病历
- 药品库存管理
- 挂号与分诊
- 员工管理
- 角色与权限管理
