@echo off
echo Waiting for server to start...
timeout /t 10 /nobreak
echo.
echo Sending POST to /api/auth/login...
curl -v -H "Content-Type: application/json" -d "{\"email\":\"admin@springride.com\",\"password\":\"admin123\"}" http://localhost:8081/api/auth/login
echo.
echo Test complete.
pause
