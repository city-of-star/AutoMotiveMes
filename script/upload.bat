@echo off
chcp 65001 > nul  # 关键修复：强制控制台使用 UTF-8 编码
setlocal enabledelayedexpansion

set "project_dir=D:\other\obsidian\obsidian_store"
set "counter_file=%project_dir%\script\upload_counter.txt"

cd /d "%project_dir%"
if %errorlevel% neq 0 (
    echo Failed to change directory to "%project_dir%".
    pause
    exit /b
)

if not exist "%counter_file%" (
    echo 1 > "%counter_file%"
    set /a upload_count=1
) else (
    set /p upload_count=<"%counter_file%"
    set /a upload_count+=1
    echo !upload_count! > "%counter_file%"
)

git pull origin main
if %errorlevel% neq 0 (
    echo Git pull failed.
    pause
    exit /b
)

git diff --quiet --exit-code
if %errorlevel% equ 0 (
    echo No changes to commit.
    pause
    exit /b
)

git add .
git commit -m "第!upload_count!次上传"  # 已通过脚本编码修复中文问题
if %errorlevel% neq 0 (
    echo Git commit failed.
    pause
    exit /b
)

git push origin main
if %errorlevel% neq 0 (
    echo Git push failed.
    pause
    exit /b
)

echo ObsidionStore has been pushed successfully.
pause