@echo off
echo Starting Clinic Management System...
echo.
echo [1/2] Starting Firebase Functions...
start "Firebase Functions" cmd /k "cd /d %~dp0 && firebase emulators:start --only functions"

timeout /t 5 /nobreak > nul

echo [2/2] Starting React App...
start "React App" cmd /k "cd /d %~dp0 && npm run start:frontend-only"

echo.
echo ✅ Both services are starting...
echo 🔥 Firebase Functions: http://localhost:5001
echo ⚛️  React App: http://localhost:3000
pause
