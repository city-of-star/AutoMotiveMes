# 汽车MES生产监控系统

## 技术栈
### 后端
- **框架**: Spring Boot 3.4.2
- **安全**: Spring Security + JWT
- **数据库**: MySQL8.0.40/H2 + MyBatis-Plus 3.5.5
- **实时通信**: WebSocket/STOMP
- **缓存**: Redis5.0.14.1
- **工具**: Lombok, AOP

### 前端
- **框架**: Vue 3 + Composition API
- **UI库**: Element Plus 2.x
- **状态管理**: Vuex 4
- **路由**: Vue Router 4
- **可视化**: ECharts
- **通信**: Axios

## 系统功能
- 🛡️ JWT身份认证
- 📊 实时生产看板
- 🔔 设备异常预警
- 📦 工单生命周期管理
- 🔍 质量追溯系统

## 🚀 快速部署指南

### 前置条件
确保系统已安装：
- MySQL 8.0+ 服务（需开启服务）
- Redis 5.0+
- JDK 17+
- Node.js 16+
- Maven 3.6+

### 配置步骤
1. **修改数据库配置**  
   编辑配置文件：  
   `/src/main/resources/application-dev.yml`  
   修改以下字段：
   ```yaml
   spring:
     datasource:
       url: [您的MySQL连接地址]  # 示例：jdbc:mysql://localhost:3306/auto_motive_mes?useSSL=false
       username: [数据库用户名]   # 默认root
       password: [数据库密码]

2. **启动系统**
    运行部署脚本：
    `/script/start.bat  # Windows系统双击运行`

3. **访问地址**
|  服务   | 访问地址  |
|  ----  | ----  |
| 🔌 后端API  | http://localhost:3000 |
| 🖥️ 前端界面  | http://localhost:8080 |

💡 提示：所有服务启动后将自动打开独立终端窗口，请勿关闭启动窗口
