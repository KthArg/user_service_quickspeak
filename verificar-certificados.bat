@echo off
REM Script para verificar que todos los certificados esten en su lugar

echo ========================================
echo Verificacion de Certificados para APIM
echo ========================================
echo.

echo [1/4] Verificando certificados en carpeta certs/...
if exist "certs\apim-client-cert.pfx" (
    echo   [OK] apim-client-cert.pfx encontrado
) else (
    echo   [ERROR] apim-client-cert.pfx NO encontrado
)

if exist "certs\server-keystore.p12" (
    echo   [OK] server-keystore.p12 encontrado en certs/
) else (
    echo   [WARN] server-keystore.p12 NO encontrado en certs/
)

if exist "certs\server-truststore.jks" (
    echo   [OK] server-truststore.jks encontrado en certs/
) else (
    echo   [WARN] server-truststore.jks NO encontrado en certs/
)

if exist "certs\passwords.txt" (
    echo   [OK] passwords.txt encontrado
) else (
    echo   [ERROR] passwords.txt NO encontrado
)

echo.
echo [2/4] Verificando keystores en src/main/resources/...
if exist "src\main\resources\server-keystore.p12" (
    echo   [OK] server-keystore.p12 copiado a resources
) else (
    echo   [ERROR] server-keystore.p12 NO encontrado en resources
    echo   [ACCION] Ejecutar: copy certs\server-keystore.p12 src\main\resources\
)

if exist "src\main\resources\server-truststore.jks" (
    echo   [OK] server-truststore.jks copiado a resources
) else (
    echo   [ERROR] server-truststore.jks NO encontrado en resources
    echo   [ACCION] Ejecutar: copy certs\server-truststore.jks src\main\resources\
)

echo.
echo [3/4] Verificando configuracion application-prod.yml...
findstr /C:"enabled: true" src\main\resources\application-prod.yml >nul
if %ERRORLEVEL% EQU 0 (
    echo   [OK] SSL habilitado en application-prod.yml
) else (
    echo   [ERROR] SSL NO habilitado en application-prod.yml
)

findstr /C:"client-auth: need" src\main\resources\application-prod.yml >nul
if %ERRORLEVEL% EQU 0 (
    echo   [OK] mTLS configurado (client-auth: need)
) else (
    echo   [ERROR] mTLS NO configurado correctamente
)

echo.
echo [4/4] Mostrando contraseñas de certificados...
if exist "certs\passwords.txt" (
    type certs\passwords.txt
) else (
    echo   [ERROR] No se puede mostrar passwords.txt
)

echo.
echo ========================================
echo Verificacion Completada
echo ========================================
echo.
echo Siguientes pasos:
echo   1. Subir certs\apim-client-cert.pfx a Azure APIM
echo   2. Configurar backend y API en APIM
echo   3. Configurar variables de entorno en App Service
echo   4. Compilar: mvn clean package -DskipTests
echo   5. Desplegar a Azure
echo.
pause
