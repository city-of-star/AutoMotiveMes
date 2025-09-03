@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul
cd /d %~dp0

:: 设置根目录
set "ROOT_DIR=%~dp0"
echo 项目根目录: %ROOT_DIR%

echo 正在检测环境版本...
echo.

:: 1. JDK版本检测
where java >nul 2>nul
if %errorlevel% equ 0 (
    echo [JDK 版本]
    for /f "tokens=3" %%a in ('java -version 2^>^&1 ^| findstr /i "version"') do (
        set "jdk_ver=%%a"
    )
    set "jdk_ver=!jdk_ver:"=!"
    echo 当前版本: !jdk_ver!
    set "jdk_valid=0"
    for /f "tokens=1,2 delims=._" %%a in ("!jdk_ver!") do (
        if %%a geq 17 (
            echo ✅ JDK版本符合要求（≥17）
            set "jdk_valid=1"
        ) else (
            echo ❌ JDK版本过低，需要≥17
        )
    )
    if !jdk_valid! equ 0 (
        echo.
        echo 程序终止：JDK版本不符合要求
        pause
        exit /b 1
    )
) else (
    echo ❌ 未检测到JDK
    echo.
    echo 程序终止：未安装JDK
    pause
    exit /b 1
)
echo.

:: 2. Node.js版本检测
where node >nul 2>nul
if %errorlevel% equ 0 (
    echo [Node.js 版本]
    for /f "delims=" %%a in ('node -v 2^>nul') do set "node_ver=%%a"
    set "node_ver=!node_ver:~1!"
    echo 当前版本: !node_ver!
    set "node_valid=0"
    for /f "tokens=1 delims=." %%a in ("!node_ver!") do (
        if %%a geq 16 (
            echo ✅ Node.js版本符合要求（≥16）
            set "node_valid=1"
        ) else (
            echo ❌ Node.js版本过低，需要≥16
        )
    )
    if !node_valid! equ 0 (
        echo.
        echo 程序终止：Node.js版本不符合要求
        pause
        exit /b 1
    )
) else (
    echo ❌ 未检测到Node.js
    echo.
    echo 程序终止：未安装Node.js
    pause
    exit /b 1
)
echo.

:: 3. 检测 MySQL 服务状态
echo [MySQL status]
netstat -ano | findstr ":3306" | findstr "LISTENING" >nul
if %errorlevel% equ 0 (
    echo ✅ MySQL服务正在运行（监听端口：3306）
    set "mysql_pid="
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":3306" ^| findstr "LISTENING"') do (
        set "mysql_pid=%%a"
    )
    if defined mysql_pid (
        set "process_name="
        for /f "tokens=1,* delims=:" %%b in ('tasklist /fi "PID eq !mysql_pid!" /fo list ^| findstr /i "映像名称 Image"') do (
            set "process_name=%%c"
        )
        if not defined process_name (
            for /f "tokens=1,*" %%b in ('tasklist /fi "PID eq !mysql_pid!" /fo list') do (
                if /i "%%b"=="Image" set "process_name=%%c"
                if /i "%%b"=="映像名称" set "process_name=%%c"
            )
        )
        if defined process_name (
            set "process_name=!process_name: =!"
            echo 进程名称: !process_name!
        ) else (
            echo 进程PID: !mysql_pid!
        )
    )
) else (
    echo ❌ MySQL服务未运行（端口3306无监听）
    echo.
    echo 程序终止：MySQL服务未运行
    pause
    exit /b 1
)
echo.

:: 4. 启动 Redis 服务
echo [Redis 服务启动]
set "redis_path=%ROOT_DIR%src\main\resources\redis\redis-server.exe"
if exist "%redis_path%" (
    echo 正在启动Redis...
    start "" "%redis_path%"
    timeout /t 2 >nul
    netstat -ano | findstr ":6379" >nul
    if %errorlevel% equ 0 (
        echo ✅ Redis启动成功（监听端口：6379）
    ) else (
        echo ❌ Redis启动失败，端口未监听
        echo 请手动检查: %redis_path%
        echo.
        echo 程序终止：Redis启动失败
        pause
        exit /b 1
    )
) else (
    echo ❌ Redis可执行文件不存在：%redis_path%
    echo.
    echo 程序终止：Redis文件缺失
    pause
    exit /b 1
)
echo.

:: ============================================
:: 解析YAML文件并设置数据库
:: ============================================
echo [数据库配置解析]
set "YAML_FILE=%ROOT_DIR%src\main\resources\application-dev.yml"
set "SQL_DIR=%ROOT_DIR%src\main\resources\mysql\"

if not exist "%YAML_FILE%" (
    echo ❌ 配置文件不存在: %YAML_FILE%
    pause
    exit /b 1
)

set "db_username="
set "db_password="
set "db_name=auto_motive_mes"

:: 解析YAML文件
for /f "usebackq delims=" %%a in ("%YAML_FILE%") do (
    set "line=%%a"

    :: 提取用户名
    if "!line:username=!" neq "!line!" (
        for /f "tokens=2 delims=: " %%b in ("!line!") do (
            set "db_username=%%b"
        )
    )

    :: 提取密码
    if "!line:password=!" neq "!line!" (
        for /f "tokens=2 delims=: " %%b in ("!line!") do (
            set "db_password=%%b"
        )
    )
)

echo 用户名: !db_username!
echo 密码: !db_password!
echo 数据库名: auto_motive_mes
echo.

:: 验证所有变量都已设置
if not defined db_username (
    echo ❌ 未在配置文件中找到数据库用户名
    pause
    exit /b 1
)

if not defined db_password (
    echo ❌ 未在配置文件中找到数据库密码
    pause
    exit /b 1
)

:: ============================================
:: 创建数据库架构
:: ============================================
echo [数据库初始化]
echo 正在创建数据库架构: !db_name!

:: 添加MySQL路径到PATH环境变量
set "MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin"
if exist "!MYSQL_PATH!\mysql.exe" (
    set "PATH=!PATH!;!MYSQL_PATH!"
) else (
    echo ⚠ 未找到MySQL安装路径，尝试使用系统PATH（环境变量）中的mysql
)

:: 检查mysql命令是否可用
where mysql >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ 未找到mysql命令，请确保MySQL已安装并添加到PATH（环境变量）
    pause
    exit /b 1
)

mysql -u!db_username! -p!db_password! -e "CREATE DATABASE IF NOT EXISTS !db_name! CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul

if %errorlevel% equ 0 (
    echo ✅ 数据库架构创建成功
) else (
    echo ❌ 数据库架构创建失败
    echo 请检查以下可能的问题：
    echo 1. MySQL用户名和密码是否正确
    echo 2. MySQL服务是否正在运行
    echo 3. 用户是否有创建数据库的权限
    pause
    exit /b 1
)
echo.

:: ============================================
:: 导入SQL文件
:: ============================================
echo [导入SQL数据]
set "sql_file=!SQL_DIR!init_data.sql"

if exist "!sql_file!" (
    echo 正在导入: init_data.sql
    mysql -u!db_username! -p!db_password! !db_name! < "!sql_file!" 2>nul

    if %errorlevel% equ 0 (
        echo ✅ 导入成功
    ) else (
        echo ❌ 导入失败: init_data.sql
        echo 请检查SQL文件语法
    )
    echo.
) else (
    echo ❌ SQL文件不存在: !sql_file!
)

echo 数据库初始化完成!
echo.

:: ============================================
:: 启动后端服务
:: ============================================
echo [启动后端服务]
set "BACKEND_DIR=%ROOT_DIR%"
set "MVN_CMD=mvn"

:: 检查是否使用Maven Wrapper
if exist "%BACKEND_DIR%mvnw.cmd" (
    set "MVN_CMD=mvnw.cmd"
    echo 检测到Maven Wrapper，使用mvnw命令
)

echo 正在安装后端依赖并启动Spring Boot...
start "后端服务" cmd /k "cd /d "%BACKEND_DIR%" && %MVN_CMD% clean install && %MVN_CMD% spring-boot:run"
echo ✅ 后端服务启动中...请查看新打开的终端窗口
timeout /t 5 >nul
echo.

:: ============================================
:: 启动前端服务
:: ============================================
echo [启动前端服务]
set "FRONTEND_DIR=%ROOT_DIR%automotivemes-ui"

if not exist "%FRONTEND_DIR%" (
    echo ❌ 前端目录不存在: %FRONTEND_DIR%
    pause
    exit /b 1
)

echo 正在安装前端依赖...
cd /d "%FRONTEND_DIR%"
if exist "node_modules" (
    echo ✅ node_modules已存在，跳过安装
) else (
    npm install
    if %errorlevel% neq 0 (
        echo ❌ npm install 失败
        pause
        exit /b 1
    )
    echo ✅ 前端依赖安装完成
)

echo 正在启动前端开发服务器...
start "前端服务" cmd /k "cd /d "%FRONTEND_DIR%" && npm run serve"
echo ✅ 前端服务启动中...请查看新打开的终端窗口
echo.

:: ============================================
:: 完成提示
:: ============================================
echo ============================================
echo 所有服务已启动!
echo.
echo 访问前端: http://localhost:8080
echo ⚠️ 窗口说明：

echo 1. 请勿关闭自动打开的「后端服务」、「前端服务」、「Redis服务」窗口

echo 2. 本窗口（启动脚本窗口）现在可以安全关闭
echo ============================================
echo.

pause