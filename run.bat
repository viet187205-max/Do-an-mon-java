@echo off
setlocal
cd /d "%~dp0"

where mvn >nul 2>nul
if errorlevel 1 (
    echo Khong tim thay Maven. Hay cai Maven va them vao PATH truoc.
    pause
    exit /b 1
)

echo Dang khoi dong Quan Ly Ban Hang...
echo Ung dung se yeu cau mat khau SQL trong cua so CMD.
call mvn clean compile exec:java
set "APP_EXIT_CODE=%ERRORLEVEL%"
echo.
echo Chuong trinh ket thuc voi ma: %APP_EXIT_CODE%
pause
exit /b %APP_EXIT_CODE%
