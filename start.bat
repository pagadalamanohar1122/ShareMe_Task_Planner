@echo off
echo Starting ShareMe TaskSphere Application...
echo.

echo Setting up database (if needed)...
echo Please ensure MySQL is running and execute database/setup.sql manually if this is your first run.
echo.

echo Starting Backend Server...
cd backend
start "Backend Server" cmd /k "mvn spring-boot:run"
echo Backend starting on http://localhost:8080
echo.

echo Waiting for backend to initialize...
timeout /t 10 /nobreak >nul

echo Starting Frontend Development Server...
cd ..\frontend
start "Frontend Server" cmd /k "npm run dev"
echo Frontend starting on http://localhost:3000
echo.

echo Both servers are starting...
echo Backend: http://localhost:8080
echo Frontend: http://localhost:3000
echo API Documentation: http://localhost:8080/swagger-ui/index.html
echo.
echo Press any key to continue...
pause >nul