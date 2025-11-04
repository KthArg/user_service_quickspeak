# 🚀 GUÍA COMPLETA: CONFIGURACIÓN DE AZURE SQL DATABASE

## 📋 Índice
1. [Requisitos Previos](#requisitos-previos)
2. [Crear Azure SQL Database](#paso-1-crear-azure-sql-database)
3. [Configurar Firewall](#paso-2-configurar-firewall)
4. [Ejecutar Script de Inicialización](#paso-3-ejecutar-script-de-inicialización)
5. [Configurar Variables de Entorno](#paso-4-configurar-variables-de-entorno)
6. [Actualizar Proyecto Local](#paso-5-actualizar-proyecto-local)
7. [Configurar Azure App Service](#paso-6-configurar-azure-app-service)
8. [Verificación y Testing](#paso-7-verificación-y-testing)
9. [Troubleshooting](#troubleshooting)

---

## ✅ Requisitos Previos

### Verificar que tienes:
- [ ] Cuenta de Azure (Azure for Students)
- [ ] Azure CLI instalado y configurado
- [ ] Acceso al portal de Azure (https://portal.azure.com)
- [ ] Créditos disponibles en tu cuenta
- [ ] Proyecto `user-service` clonado localmente

### Instalar Azure CLI (si no lo tienes)

**Windows:**
```bash
# Descargar instalador desde:
https://aka.ms/installazurecliwindows
```

**macOS:**
```bash
brew update && brew install azure-cli
```

**Linux:**
```bash
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
```

### Login en Azure CLI
```bash
# Login
az login

# Verificar cuenta activa
az account show

# Listar suscripciones disponibles
az account list --output table

# Seleccionar suscripción (si tienes varias)
az account set --subscription "<SUBSCRIPTION_ID>"
```

---

## 📦 PASO 1: Crear Azure SQL Database

### 1.1 Acceder al Portal de Azure

1. Ve a https://portal.azure.com
2. Inicia sesión con tu cuenta de estudiante
3. En el dashboard, busca "SQL databases" en la barra superior

### 1.2 Crear Resource Group (si no existe)

**Opción A: Por Portal**
1. Click en "Resource groups" en el menú lateral
2. Click "+ Create"
3. Configuración:
   ```
   Resource group: yourteacher-resources
   Region: Chile Central
   ```
4. Click "Review + create" → "Create"

**Opción B: Por CLI**
```bash
az group create \
  --name yourteacher-resources \
  --location chilecenter
```

### 1.3 Crear SQL Database

**Por Portal de Azure:**

1. **Navegar a SQL Databases:**
   - En la búsqueda superior: "SQL databases"
   - Click en "SQL databases"
   - Click "+ Create"

2. **Configuración Básica (Basics):**
   ```
   Subscription: Azure for Students
   Resource group: yourteacher-resources
   Database name: userservice-db
   Server: [Create new]
   ```

3. **Crear SQL Server (Click en "Create new"):**
   ```
   Server name: yourteacher-sql-server
   ⚠️ IMPORTANTE: Este nombre debe ser ÚNICO globalmente
   Si está tomado, prueba: yourteacher-sql-[tus-iniciales]
   
   Location: Chile Central
   Authentication: Use SQL authentication
   Server admin login: sqladmin
   Password: [Crea una contraseña segura]
   Confirm password: [Repite la contraseña]
   ```

   **Requisitos de contraseña:**
   - Mínimo 8 caracteres
   - Al menos 1 mayúscula
   - Al menos 1 minúscula
   - Al menos 1 número
   - Al menos 1 símbolo especial (!@#$%^&*)
   
   **Ejemplo:** `SqlAdmin123!`

   ⚠️ **GUARDA ESTAS CREDENCIALES EN UN LUGAR SEGURO**

4. **Compute + Storage:**
   - Click en "Configure database"
   - Selecciona "Serverless"
   - Configuración recomendada:
     ```
     Service tier: General Purpose
     Compute tier: Serverless
     vCores: 1
     Data max size: 2 GB
     Auto-pause delay: 1 hour
     ```
   - Click "Apply"

5. **Networking:**
   ```
   Connectivity method: Public endpoint
   
   Firewall rules:
   ☑️ Allow Azure services and resources to access this server
   ☑️ Add current client IP address
   ```

6. **Additional Settings:**
   ```
   Use existing data: None
   
   Collation: SQL_Latin1_General_CP1_CI_AS
   
   Enable Microsoft Defender for SQL: Not now
   ```

7. **Tags (opcional):**
   ```
   Environment: Development
   Project: YourTeacher
   Service: user-service
   ```

8. **Review + Create:**
   - Verifica toda la configuración
   - Revisa el costo estimado mensual
   - Click "Create"
   - ⏱️ Espera 2-5 minutos mientras se crea

### 1.4 Por Azure CLI (Alternativa más rápida)

```bash
# Variables
RESOURCE_GROUP="yourteacher-resources"
LOCATION="chilecenter"
SQL_SERVER="yourteacher-sql-server"
DATABASE_NAME="userservice-db"
ADMIN_USER="sqladmin"
ADMIN_PASSWORD="SqlAdmin123!"  # Cámbialo

# Crear servidor SQL
az sql server create \
  --name $SQL_SERVER \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --admin-user $ADMIN_USER \
  --admin-password $ADMIN_PASSWORD

# Configurar firewall para permitir servicios de Azure
az sql server firewall-rule create \
  --resource-group $RESOURCE_GROUP \
  --server $SQL_SERVER \
  --name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0

# Agregar tu IP actual
MY_IP=$(curl -s https://ifconfig.me)
az sql server firewall-rule create \
  --resource-group $RESOURCE_GROUP \
  --server $SQL_SERVER \
  --name AllowMyIP \
  --start-ip-address $MY_IP \
  --end-ip-address $MY_IP

# Crear base de datos serverless
az sql db create \
  --resource-group $RESOURCE_GROUP \
  --server $SQL_SERVER \
  --name $DATABASE_NAME \
  --edition GeneralPurpose \
  --compute-model Serverless \
  --family Gen5 \
  --capacity 1 \
  --auto-pause-delay 60 \
  --max-size 2GB

echo "✅ Azure SQL Database creado exitosamente"
```

---

## 🔒 PASO 2: Configurar Firewall

### 2.1 Verificar Reglas de Firewall

1. En el portal de Azure, ve a tu SQL Server: `yourteacher-sql-server`
2. En el menú lateral: "Security" → "Networking"
3. Verifica que existan estas reglas:

```
Firewall rules:
┌─────────────────────────┬─────────────┬─────────────┐
│ Rule Name               │ Start IP    │ End IP      │
├─────────────────────────┼─────────────┼─────────────┤
│ AllowAllWindowsAzureIps │ 0.0.0.0     │ 0.0.0.0     │
│ ClientIPAddress         │ [Tu IP]     │ [Tu IP]     │
└─────────────────────────┴─────────────┴─────────────┘
```

### 2.2 Agregar IP Adicional (si es necesario)

**Por Portal:**
1. En "Networking" → "Firewall rules"
2. Click "+ Add client IP"
3. O manualmente:
   ```
   Rule name: MiComputador
   Start IP: [Tu IP]
   End IP: [Tu IP]
   ```
4. Click "Save"

**Por CLI:**
```bash
az sql server firewall-rule create \
  --resource-group yourteacher-resources \
  --server yourteacher-sql-server \
  --name MiIP \
  --start-ip-address $(curl -s https://ifconfig.me) \
  --end-ip-address $(curl -s https://ifconfig.me)
```

### 2.3 Obtener Connection String

1. Ve a tu base de datos: `userservice-db`
2. En el menú lateral: "Settings" → "Connection strings"
3. Copia el **JDBC connection string**:

```
jdbc:sqlserver://yourteacher-sql-server.database.windows.net:1433;database=userservice-db;user=sqladmin@yourteacher-sql-server;password={your_password_here};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
```

4. **Guarda esto** - lo necesitarás más adelante

---

## 💾 PASO 3: Ejecutar Script de Inicialización

### 3.1 Conectarse a la Base de Datos

**Opción A: Azure Data Studio (Recomendado)**

1. Descargar Azure Data Studio:
   - https://aka.ms/azuredatastudio

2. Instalar y abrir Azure Data Studio

3. Crear nueva conexión:
   ```
   Connection type: Microsoft SQL Server
   Server: yourteacher-sql-server.database.windows.net
   Authentication type: SQL Login
   User name: sqladmin
   Password: [Tu password]
   Database: userservice-db
   Encrypt: True
   ```

4. Click "Connect"

**Opción B: Azure Portal Query Editor**

1. Ve a tu base de datos: `userservice-db`
2. En el menú lateral: "Query editor (preview)"
3. Login con:
   ```
   Authorization type: SQL Server authentication
   Login: sqladmin
   Password: [Tu password]
   ```

**Opción C: SQL Server Management Studio (SSMS)**

1. Descargar SSMS (solo Windows):
   - https://aka.ms/ssmsfullsetup

2. Conectar con:
   ```
   Server name: yourteacher-sql-server.database.windows.net
   Authentication: SQL Server Authentication
   Login: sqladmin
   Password: [Tu password]
   ```

### 3.2 Ejecutar el Script de Inicialización

1. **Abrir el archivo:** `init-azure-sql.sql` (que generamos antes)

2. **Revisar el script:**
   - Crea tablas `users` y `user_roles`
   - Configura constraints e índices
   - Crea triggers para auditoría
   - Inserta datos iniciales (admin y estudiante de prueba)
   - Crea vistas y stored procedures útiles

3. **Ejecutar el script completo:**
   - En Azure Data Studio: Click en "Run" o F5
   - En Query Editor: Click en "Run"
   - En SSMS: Click en "Execute" o F5

4. **Verificar ejecución exitosa:**
   ```sql
   -- Verificar tablas creadas
   SELECT TABLE_NAME 
   FROM INFORMATION_SCHEMA.TABLES 
   WHERE TABLE_NAME IN ('users', 'user_roles');
   
   -- Verificar datos iniciales
   SELECT * FROM users;
   SELECT * FROM user_roles;
   ```

   Deberías ver:
   ```
   2 usuarios creados:
   - admin@yourteacher.com (ADMIN)
   - student@yourteacher.com (STUDENT)
   ```

---

## 🔧 PASO 4: Configurar Variables de Entorno Localmente

### 4.1 Crear Archivo .env.local

En la raíz de tu proyecto `user-service/`:

```bash
# Crear archivo de variables de entorno
touch .env.local
```

### 4.2 Configurar Variables

Edita `.env.local` con tus valores reales:

```bash
# Perfil de Spring
SPRING_PROFILE=prod

# Azure SQL Database
DB_URL=jdbc:sqlserver://yourteacher-sql-server.database.windows.net:1433;database=userservice-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
DB_USERNAME=sqladmin
DB_PASSWORD=TuPasswordAqui123!
DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA/Hibernate
DDL_AUTO=validate
SHOW_SQL=false
HIBERNATE_DIALECT=org.hibernate.dialect.SQLServerDialect

# JWT
JWT_SECRET=$(openssl rand -base64 32)
JWT_EXPIRATION=86400000

# Server
PORT=8080
```

### 4.3 Generar JWT Secret Seguro

```bash
# Generar secret aleatorio
openssl rand -base64 32

# Copiar el resultado y pegarlo en JWT_SECRET
```

### 4.4 Proteger el Archivo

```bash
# Agregar al .gitignore
echo ".env.local" >> .gitignore
echo ".env" >> .gitignore

# Verificar que no se commitea
git status
```

---

## 📝 PASO 5: Actualizar Proyecto Local

### 5.1 Copiar Archivos de Configuración

Copia los archivos generados a tu proyecto:

```bash
# Ir al directorio del proyecto
cd user-service/

# Copiar application-prod.yml
cp /ruta/a/application-prod.yml src/main/resources/

# Copiar .env.example
cp /ruta/a/.env.example .

# Copiar script de configuración
cp /ruta/a/configure-azure-env.sh .
chmod +x configure-azure-env.sh
```

### 5.2 Actualizar pom.xml

Verifica que tengas la dependencia de SQL Server:

```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>
```

Si no la tienes, agrégala en la sección `<dependencies>`.

### 5.3 Verificar Estructura de Archivos

```
user-service/
├── src/main/resources/
│   ├── application.yml           (dev - H2)
│   └── application-prod.yml      (prod - Azure SQL) ✅ NUEVO
├── .env.example                  ✅ NUEVO
├── .env.local                    ✅ NUEVO (no commitear)
├── configure-azure-env.sh        ✅ NUEVO
└── init-azure-sql.sql            ✅ NUEVO
```

### 5.4 Testing Local con Azure SQL

```bash
# Cargar variables de entorno
export $(cat .env.local | xargs)

# O en Windows PowerShell:
Get-Content .env.local | ForEach-Object { $name, $value = $_.Split('='); Set-Item -Path "Env:$name" -Value $value }

# Compilar
mvn clean install

# Ejecutar con perfil de producción
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# O con Java directamente
java -jar -Dspring.profiles.active=prod target/user-service-1.0.0-SNAPSHOT.jar
```

### 5.5 Verificar Conexión

```bash
# Health check
curl http://localhost:8080/actuator/health

# Debe responder:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "Microsoft SQL Server"
      }
    }
  }
}
```

---

## ☁️ PASO 6: Configurar Azure App Service

### 6.1 Crear App Service (si no existe)

```bash
# Variables
RESOURCE_GROUP="yourteacher-resources"
APP_NAME="yourteacher-user-service"
LOCATION="chilecenter"

# Crear App Service Plan
az appservice plan create \
  --name yourteacher-plan \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --sku B1 \
  --is-linux

# Crear Web App con Java 17
az webapp create \
  --resource-group $RESOURCE_GROUP \
  --plan yourteacher-plan \
  --name $APP_NAME \
  --runtime "JAVA:17-java17"
```

### 6.2 Configurar Variables de Entorno en Azure

**Opción A: Script Automatizado**

```bash
# Ejecutar script de configuración
./configure-azure-env.sh yourteacher-resources yourteacher-user-service
```

El script te pedirá:
- SQL Server name
- Database name
- Username
- Password
- JWT Secret (o lo genera automáticamente)

**Opción B: Manual por CLI**

```bash
az webapp config appsettings set \
  --resource-group yourteacher-resources \
  --name yourteacher-user-service \
  --settings \
    SPRING_PROFILE="prod" \
    DB_URL="jdbc:sqlserver://yourteacher-sql-server.database.windows.net:1433;database=userservice-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;" \
    DB_USERNAME="sqladmin" \
    DB_PASSWORD="TuPassword123!" \
    DB_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver" \
    DDL_AUTO="validate" \
    SHOW_SQL="false" \
    HIBERNATE_DIALECT="org.hibernate.dialect.SQLServerDialect" \
    JWT_SECRET="tu-secret-generado" \
    JWT_EXPIRATION="86400000" \
    PORT="8080"
```

**Opción C: Por Portal de Azure**

1. Ve a tu App Service: `yourteacher-user-service`
2. En el menú lateral: "Settings" → "Configuration"
3. Click en "+ New application setting"
4. Agrega cada variable una por una:

```
Name: SPRING_PROFILE        Value: prod
Name: DB_URL                Value: jdbc:sqlserver://...
Name: DB_USERNAME           Value: sqladmin
Name: DB_PASSWORD           Value: TuPassword123!
Name: DB_DRIVER             Value: com.microsoft.sqlserver.jdbc.SQLServerDriver
Name: DDL_AUTO              Value: validate
Name: SHOW_SQL              Value: false
Name: HIBERNATE_DIALECT     Value: org.hibernate.dialect.SQLServerDialect
Name: JWT_SECRET            Value: [tu secret]
Name: JWT_EXPIRATION        Value: 86400000
Name: PORT                  Value: 8080
```

5. Click "Save" en la parte superior

### 6.3 Desplegar la Aplicación

```bash
# Compilar el JAR
mvn clean package -DskipTests

# Desplegar a Azure
az webapp deployment source config-zip \
  --resource-group yourteacher-resources \
  --name yourteacher-user-service \
  --src target/user-service-1.0.0-SNAPSHOT.jar

# O usar el script de despliegue existente
./deploy-azure.sh
```

### 6.4 Verificar Despliegue

```bash
# Obtener URL de la app
az webapp show \
  --resource-group yourteacher-resources \
  --name yourteacher-user-service \
  --query defaultHostName \
  --output tsv

# Health check
curl https://yourteacher-user-service.azurewebsites.net/actuator/health
```

---

## ✅ PASO 7: Verificación y Testing

### 7.1 Verificar Conectividad a Base de Datos

**Test 1: Health Check**
```bash
curl https://yourteacher-user-service.azurewebsites.net/actuator/health
```

Respuesta esperada:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "Microsoft SQL Server",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

**Test 2: Listar Usuarios**
```bash
curl https://yourteacher-user-service.azurewebsites.net/api/v1/users
```

Debería devolver los 2 usuarios iniciales (admin y student).

### 7.2 Testing Completo con Postman

1. **Importar colección:**
   - Abre Postman
   - Import → File → Selecciona `postman_collection.json`

2. **Configurar variables:**
   ```
   baseUrl: https://yourteacher-user-service.azurewebsites.net
   ```

3. **Ejecutar tests:**
   - GET /users
   - GET /users/{id}
   - POST /users (crear nuevo usuario)
   - PUT /users/{id}
   - DELETE /users/{id}

### 7.3 Verificar en Azure SQL

Conectarse a la base de datos y verificar:

```sql
-- Ver todos los usuarios
SELECT * FROM users;

-- Ver estadísticas
SELECT * FROM vw_user_statistics;

-- Verificar logs de auditoría
SELECT 
    id,
    email,
    created_at,
    updated_at,
    DATEDIFF(second, created_at, updated_at) as seconds_diff
FROM users
ORDER BY created_at DESC;
```

### 7.4 Monitoreo en Azure Portal

1. Ve a tu SQL Database: `userservice-db`
2. En el menú lateral: "Monitoring" → "Metrics"
3. Selecciona métricas:
   - DTU/CPU percentage
   - Storage used
   - Connections
   - Deadlocks

---

## 🔧 Troubleshooting

### Problema 1: No puedo conectarme a la base de datos

**Error:**
```
Cannot open server 'yourteacher-sql-server' requested by the login
```

**Solución:**
1. Verifica que tu IP esté en el firewall:
   ```bash
   # Obtener tu IP
   curl https://ifconfig.me
   
   # Agregar regla de firewall
   az sql server firewall-rule create \
     --resource-group yourteacher-resources \
     --server yourteacher-sql-server \
     --name MiIP \
     --start-ip-address [TU_IP] \
     --end-ip-address [TU_IP]
   ```

### Problema 2: Error de autenticación

**Error:**
```
Login failed for user 'sqladmin'
```

**Solución:**
1. Verifica el username y password
2. En el connection string, asegúrate que el formato sea:
   ```
   user=sqladmin@yourteacher-sql-server
   ```
   (Incluye el `@server-name`)

### Problema 3: Timeout de conexión

**Error:**
```
The TCP/IP connection to the host has failed
```

**Solución:**
1. Verifica que el firewall permita Azure Services:
   ```bash
   az sql server firewall-rule create \
     --resource-group yourteacher-resources \
     --server yourteacher-sql-server \
     --name AllowAzureServices \
     --start-ip-address 0.0.0.0 \
     --end-ip-address 0.0.0.0
   ```

### Problema 4: Tabla no existe

**Error:**
```
Invalid object name 'users'
```

**Solución:**
1. Verifica que el script de inicialización se ejecutó correctamente
2. Conéctate a la BD y verifica:
   ```sql
   SELECT * FROM INFORMATION_SCHEMA.TABLES;
   ```
3. Si las tablas no existen, ejecuta `init-azure-sql.sql` de nuevo

### Problema 5: Variables de entorno no se cargan

**Error:**
```
The server name specified in the connection string is invalid
```

**Solución:**
1. Verifica que las variables estén configuradas en Azure:
   ```bash
   az webapp config appsettings list \
     --resource-group yourteacher-resources \
     --name yourteacher-user-service \
     --output table
   ```
2. Reinicia el App Service:
   ```bash
   az webapp restart \
     --resource-group yourteacher-resources \
     --name yourteacher-user-service
   ```

### Problema 6: Costos muy altos

**Síntoma:**
- Factura mayor a lo esperado

**Solución:**
1. Verifica la configuración serverless:
   ```bash
   az sql db show \
     --resource-group yourteacher-resources \
     --server yourteacher-sql-server \
     --name userservice-db \
     --query "{computeModel:currentServiceObjectiveName, maxSizeBytes:maxSizeBytes}"
   ```
2. Configura auto-pause:
   ```bash
   az sql db update \
     --resource-group yourteacher-resources \
     --server yourteacher-sql-server \
     --name userservice-db \
     --auto-pause-delay 60  # 1 hora
   ```

### Logs y Debugging

**Ver logs del App Service:**
```bash
# Streaming logs
az webapp log tail \
  --resource-group yourteacher-resources \
  --name yourteacher-user-service

# Descargar logs
az webapp log download \
  --resource-group yourteacher-resources \
  --name yourteacher-user-service \
  --log-file logs.zip
```

**Ver métricas de SQL:**
```bash
az monitor metrics list \
  --resource /subscriptions/{subscription-id}/resourceGroups/yourteacher-resources/providers/Microsoft.Sql/servers/yourteacher-sql-server/databases/userservice-db \
  --metric "cpu_percent" \
  --interval PT1H
```

---

## 📚 Referencias Útiles

### Documentación Oficial
- [Azure SQL Database](https://docs.microsoft.com/azure/azure-sql/database/)
- [Serverless Compute Tier](https://docs.microsoft.com/azure/azure-sql/database/serverless-tier-overview)
- [Spring Boot with Azure SQL](https://docs.microsoft.com/azure/developer/java/spring-framework/configure-spring-data-jpa-with-azure-sql-server)

### Herramientas
- [Azure Data Studio](https://aka.ms/azuredatastudio)
- [SQL Server Management Studio](https://aka.ms/ssmsfullsetup)
- [Azure CLI Reference](https://docs.microsoft.com/cli/azure/)

### Tutoriales
- [Conectar Spring Boot con Azure SQL](https://spring.io/guides/gs/accessing-data-mysql/)
- [Desplegar Spring Boot en Azure](https://docs.microsoft.com/azure/app-service/quickstart-java)

---

## ✅ Checklist Final

Marca cada item al completarlo:

### Configuración de Azure
- [ ] Azure SQL Server creado
- [ ] Base de datos `userservice-db` creada
- [ ] Firewall configurado
- [ ] Connection string obtenido

### Inicialización de Base de Datos
- [ ] Script `init-azure-sql.sql` ejecutado
- [ ] Tablas `users` y `user_roles` creadas
- [ ] Datos iniciales insertados
- [ ] Vistas y stored procedures creados

### Configuración Local
- [ ] `application-prod.yml` creado
- [ ] `.env.local` configurado con valores reales
- [ ] Dependencia SQL Server en `pom.xml`
- [ ] Testing local exitoso

### Configuración de Azure App Service
- [ ] Variables de entorno configuradas en Azure
- [ ] Aplicación desplegada
- [ ] Health check pasando
- [ ] API respondiendo correctamente

### Documentación
- [ ] README actualizado
- [ ] `.env.example` commitado
- [ ] `.env.local` en `.gitignore`
- [ ] Justificación técnica documentada

---

## 🎉 ¡Configuración Completada!

Tu microservicio `user-service` ahora está:
- ✅ Conectado a Azure SQL Database
- ✅ Con datos inicializados
- ✅ Desplegado en Azure App Service
- ✅ Listo para producción

**Próximos pasos:**
1. Implementar autenticación JWT completa
2. Agregar más endpoints según necesidad
3. Configurar CI/CD con GitHub Actions
4. Implementar monitoreo con Application Insights

---

**Documento creado:** 2025-11-03  
**Versión:** 1.0  
**Autor:** Equipo YourTeacher  
**Estado:** ✅ Completo
