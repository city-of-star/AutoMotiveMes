# 🚗 汽车MES生产监控系统

## 🧰 技术栈

### ⚙️ 后端
- **核心框架**: Spring Boot 3.4.2
- **安全认证**: Spring Security + JWT
- **数据库**: MySQL8.0.40/H2 + MyBatis-Plus 3.5.5
- **实时通信**: WebSocket/STOMP
- **缓存系统**: Redis 5.0.14.1
- **开发工具**: Lombok, AOP

### 🖥️ 前端
- **核心框架**: Vue 3 + Composition API
- **UI组件库**: Element Plus 2.x
- **状态管理**: Vuex 4
- **路由管理**: Vue Router 4
- **数据可视化**: ECharts
- **网络通信**: Axios

## 📋 系统功能
- 🔐 JWT身份认证
- 📈 实时生产看板
- 🚨 设备异常预警
- 📆 工单生命周期管理
- 🔎 质量追溯系统
- 📝 生产报表统计

---

## 🚀 快速部署指南

### 前置要求
请确保已安装以下服务：

| 软件名称 | 版本要求 |
|----------|----------|
| MySQL    | 8.0+     |
| Redis    | 5.0+     |
| JDK      | 17+      |
| Node.js  | 16+      |
| Maven    | 3.6+     |

> Redis服务已包含在项目中，可直接启动

### 🔧 配置步骤

1. **数据库配置**  
   编辑配置文件：  
   `src/main/resources/application-dev.yml`  
   修改以下关键配置：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/auto_motive_mes?useSSL=false&characterEncoding=utf8
       username: your_username  # 默认root
       password: your_password

2. **启动系统**  
   运行启动脚本：双击 /start.bat

### 🌐 访问地址
- 🔌 后端API：http://localhost:3000
- 🖥️ 前端界面：http://localhost:8080

> ⚠️ 请勿关闭启动时弹出的终端窗口，所有服务将在独立终端运行

---

## 🔍 手动部署（当脚本启动失败时）
1. 项目配置
    1. 设置SDK版本为Java 17
    2. 创建MySQL数据库：
        ```sql
        CREATE DATABASE auto_motive_mes CHARACTER SET utf8mb4;
        ```
2. 数据库初始化
    1. 按顺序执行以下SQL脚本：
        ```
        src/main/resources/mysql/
        └─ user.sql       # 用户相关表
        └─ equipment.sql  # 设备相关表
        └─ order.sql      # 工单相关表
        ```
3. 启动服务
    1. 启动Redis服务：
        ```
        src/main/resources/redis/redis-server.exe
        ```
    2. 启动后端服务
    3. 启动前端服务:
        ```
        cd automotivemes-ui
        npm install       # 安装依赖
        npm run serve     # 启动前端
        ```
## 📬 系统访问
1. 打开浏览器访问：http://127.0.0.1:8080
2. 使用初始凭证登录：
    - 👤 用户名: admin
    - 🔑 密码: 123456

#### ✨ 系统采用模块化设计，支持二次开发扩展
#### 📞 技术支持请联系: 2722562862@qq.com / 18255097030