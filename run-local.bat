@echo off
REM ============================================
REM Script para ejecutar el servicio localmente
REM conectado a Azure SQL Database
REM ============================================

echo.
echo ============================================
echo   Cargando variables de entorno...
echo ============================================
echo.

REM Cargar variables desde .env.local
for /f "usebackq tokens=1,* delims==" %%a in (".env.local") do (
    set "line=%%a"
    REM Ignorar líneas vacías y comentarios
    if not "!line!"=="" (
        if not "!line:~0,1!"=="#" (
            set "%%a=%%b"
            echo [OK] %%a configurado
        )
    )
)

REM Establecer perfil activo de Spring
set SPRING_PROFILES_ACTIVE=prod

echo.
echo ============================================
echo   Variables cargadas exitosamente
echo ============================================
echo.
echo DB_URL: %DB_URL%
echo DB_USERNAME: %DB_USERNAME%
echo JWT_SECRET: [OCULTO]
echo.
echo ============================================
echo   Ejecutando Spring Boot...
echo ============================================
echo.

REM Ejecutar Maven
mvn spring-boot:run

pause
