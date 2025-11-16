@echo off
echo ========================================
echo PRUEBAS RAPIDAS - USER SERVICE
echo ========================================
echo.

REM CONFIGURAR AQUI TU URL
set APP_URL=https://TU-APP.azurewebsites.net

echo Usando URL: %APP_URL%
echo.
echo ========================================
echo.

echo [1/6] Health Check...
curl -s %APP_URL%/actuator/health
echo.
echo.

echo [2/6] Obtener Idiomas (primeros 3)...
curl -s %APP_URL%/api/v1/languages | head -50
echo.
echo.

echo [3/6] Registrar Usuario de Prueba...
curl -s -X POST %APP_URL%/api/v1/auth/register -H "Content-Type: application/json" -d "{\"email\":\"test-prod@quickspeak.com\",\"password\":\"Password123!\",\"firstName\":\"Test\",\"lastName\":\"Prod\"}"
echo.
echo.

echo [4/6] Login con Usuario Creado...
curl -s -X POST %APP_URL%/api/v1/auth/login -H "Content-Type: application/json" -d "{\"email\":\"test-prod@quickspeak.com\",\"password\":\"Password123!\"}"
echo.
echo.

echo [5/6] Idiomas de Inicio...
curl -s %APP_URL%/api/v1/languages/starting
echo.
echo.

echo [6/6] Health Check Final...
curl -s %APP_URL%/actuator/health
echo.
echo.

echo ========================================
echo PRUEBAS COMPLETADAS
echo ========================================
echo.
echo Si todas las respuestas son JSON validos = TODO OK
echo Si hay errores HTML o 500 = REVISAR LOGS
echo.
pause
