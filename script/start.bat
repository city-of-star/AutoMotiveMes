@echo off
chcp 65001 > nul
title 汽车MES生产监控系统一键部署
echo === 汽车MES生产监控系统一键部署 ===
echo.
echo 当前路径: %cd%
echo.

:: 设置根目录（从当前script目录上移一级）
for %%i in ("..") do set "ROOT_DIR=%%~fi"
echo 项目根目录: %ROOT_DIR%

:: 配置文件路径
set "CONFIG_FILE=%ROOT_DIR%\src\main\resources\application-dev.yml"
if not exist "%CONFIG_FILE%" (
    echo [错误] 配置文件不存在: %CONFIG_FILE%
    echo [解决方案] 请创建application-dev.yml并配置数据库信息
    pause
    exit /b 1
)

:: 解析数据库配置
echo 正在解析数据库配置...
set "DB_URL="
set "DB_USER="
set "DB_PASS="

:: 提取配置值（带引号支持）
for /f "tokens=2* delims=: " %%a in ('findstr /c:"url:" "%CONFIG_FILE%"') do (
    set "DB_URL=%%a"
)
for /f "tokens=2* delims=: " %%a in ('findstr /c:"username:" "%CONFIG_FILE%"') do (
    set "DB_USER=%%a"
)
for /f "tokens=2* delims=: " %%a in ('findstr /c:"password:" "%CONFIG_FILE%"') do (
    set "DB_PASS=%%a"
)

:: 设置默认用户名（如果未配置）
if not defined DB_USER set "DB_USER=root"

:: 显示配置信息
echo 数据库配置:
echo URL: %DB_URL%
echo 用户: %DB_USER%
if defined DB_PASS echo 密码: ********
echo.

:: 直接进入数据库初始化步骤
echo [1/4] 创建数据库并导入表结构...
cd /D "%ROOT_DIR%\src\main\resources\mysql"
echo 执行位置: %cd%
echo.

:: 构建MySQL命令
set "MYSQL_CMD=mysql -h localhost -u %DB_USER%"
if defined DB_PASS set "MYSQL_CMD=%MYSQL_CMD% -p%DB_PASS%"

:: 测试数据库连接
echo 正在测试数据库连接...
%MYSQL_CMD% -e "SELECT 1;" > nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 数据库连接失败
    echo [解决方案]
    echo 1. 确保MySQL服务已启动并运行
    echo 2. 检查application-dev.yml中的数据库配置
    echo 3. 尝试手动连接: mysql -u %DB_USER%
    echo 4. 确保MySQL用户有足够权限
    pause
    exit /b 1
)

:: 执行SQL脚本
echo 正在创建数据库...
%MYSQL_CMD% -e "CREATE DATABASE IF NOT EXISTS auto_motive_mes;"

echo 正在导入表结构...
%MYSQL_CMD% auto_motive_mes < equipment.sql
%MYSQL_CMD% auto_motive_mes < user.sql
%MYSQL_CMD% auto_motive_mes < order.sql
echo 表结构导入完成
cd /D "%ROOT_DIR%"
echo.

:: 启动Redis服务
start "" "%ROOT_DIR%\redis\redis-server.exe"

:: 启动后端服务
echo [3/4] 启动后端服务...
cd /D "%ROOT_DIR%"

:: 先尝试使用mvnw，失败后使用系统maven
if exist "mvnw.cmd" (
    set "BUILD_CMD=mvnw.cmd"
) else (
    where mvn >nul 2>&1
    if %errorlevel% equ 0 (
        set "BUILD_CMD=mvn"
    ) else (
        echo [错误] 未找到任何Maven可执行文件
        echo 请安装Maven或添加mvnw.cmd到项目根目录
        pause
        exit /b 1
    )
)

start "" cmd /c "echo [后端服务] 正在启动... && title MES后端服务 && %BUILD_CMD% spring-boot:run -Dspring-boot.run.profiles=dev"
echo 后端服务启动中(使用 %BUILD_CMD%)...
echo.

:: 启动前端服务
echo [4/4] 启动前端服务...
cd /D "%ROOT_DIR%"
if exist "automotivemes-ui\package.json" (
    start "" cmd /c "cd /D "%ROOT_DIR%\automotivemes-ui" && echo 正在安装前端依赖... && npm install && echo 启动前端服务器... && npm run serve && echo 前端访问: http://localhost:8081"
    echo 前端服务启动中...
) else (
    echo [错误] 未找到前端项目
    echo [解决方案] 请确认automotivemes-ui目录存在
    pause
    exit /b 1
)

:: 完成提示
echo === 部署完成 ===
echo.
echo 后端服务访问: http://localhost:3000
echo 前端服务访问: http://localhost:8080
echo.
echo 提示: 后端和前端服务已在独立窗口中运行
echo 按任意键关闭此窗口...
pause