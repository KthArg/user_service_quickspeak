@echo off
echo Limpiando variables de entorno para desarrollo local con H2...
echo.

REM Limpiar variables de Spring Profile
set SPRING_PROFILE=

REM Limpiar variables de JWT
set JWT_SECRET=
set JWT_EXPIRATION=

REM Limpiar variables de Base de Datos
set DB_URL=
set DB_USERNAME=
set DB_PASSWORD=
set DB_DRIVER=
set HIBERNATE_DIALECT=
set DDL_AUTO=
set SHOW_SQL=

echo Variables de entorno limpiadas exitosamente.
echo.
echo El microservicio ahora usara H2 (en memoria) por defecto.
echo Para iniciar el servicio ejecuta: mvn spring-boot:run
echo.
