@echo off
echo ========================================
echo Configurando Azure SQL Database
echo ========================================
echo.
echo ✓ Base de datos: Azure SQL (quickspeak-users-db)
echo ✓ Servidor: quickspeak-sql-server.database.windows.net
echo ✓ Puerto: 8082
echo ✓ DDL Auto: update (creara/actualizara tablas automaticamente)
echo.
echo Iniciando servicio...
echo.
echo ========================================

mvn spring-boot:run ^
-Dspring-boot.run.arguments="--spring.profiles.active=prod --spring.datasource.url=jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=quickspeak-users-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30; --spring.datasource.username=sqladmin --spring.datasource.password=TuPassword123! --spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver --spring.jpa.hibernate.ddl-auto=update --spring.jpa.show-sql=true --spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect --jwt.secret=tZy7m0lxDUoGEaaWrg3YhRrICMFW8gJ2MpZqgEzFzyE= --jwt.expiration=86400000 --server.port=8082"
