# 🚀 GUÍA COMPLETA: MIGRACIÓN A NUEVA CUENTA AZURE

**Contexto**: Tu subscripción Azure Student expiró. Esta guía te ayudará a migrar el microservicio a la cuenta de un compañero paso a paso.

---

## 📋 TABLA DE CONTENIDOS

1. [Pre-requisitos](#pre-requisitos)
2. [Fase 1: Preparar Azure](#fase-1-preparar-azure)
3. [Fase 2: Configurar Base de Datos](#fase-2-configurar-base-de-datos)
4. [Fase 3: Crear App Service](#fase-3-crear-app-service)
5. [Fase 4: Configurar Variables de Entorno](#fase-4-configurar-variables-de-entorno)
6. [Fase 5: Desplegar Aplicación](#fase-5-desplegar-aplicación)
7. [Fase 6: Configurar APIM](#fase-6-configurar-apim)
8. [Fase 7: Verificación](#fase-7-verificación)
9. [Troubleshooting](#troubleshooting)

---

## PRE-REQUISITOS

### ✅ Lo que necesitas tener listo:

- [ ] Cuenta Azure activa (del compañero)
- [ ] Azure CLI instalado: `az --version`
- [ ] Java 17 instalado: `java -version`
- [ ] Maven instalado: `mvn -version`
- [ ] Git instalado
- [ ] Acceso al repositorio GitHub
- [ ] Código local actualizado

### 📁 Archivos importantes en el proyecto:

```
user_service_quickspeak/
├── database/
│   ├── schema.sql                  ← Script SQL para Azure SQL
│   └── schema-postgres.sql         ← Script SQL para PostgreSQL
├── pom.xml                         ← Dependencias Maven
├── src/main/resources/
│   ├── application.yml             ← Configuración base
│   └── application-prod.yml        ← Configuración producción
└── MIGRACION_AZURE_COMPLETA.md     ← Este archivo
```

---

## FASE 1: PREPARAR AZURE

### 1.1 Login a Azure

```bash
# Login con la cuenta del compañero
az login

# Verificar la subscripción activa
az account show

# Si tiene múltiples subscripciones, seleccionar la correcta
az account list --output table
az account set --subscription "NOMBRE_O_ID_DE_SUBSCRIPCION"
```

### 1.2 Crear Resource Group

```bash
# Crear resource group (ajusta el nombre y región)
az group create \
  --name quickspeak-resources \
  --location eastus

# Verificar
az group show --name quickspeak-resources
```

**Regiones recomendadas**:
- `eastus` - East US (barato, rápido)
- `westeurope` - West Europe
- `eastus2` - East US 2

---

## FASE 2: CONFIGURAR BASE DE DATOS

Tienes **DOS OPCIONES**: Azure SQL Database o Azure PostgreSQL

### OPCIÓN A: Azure SQL Database (Recomendado para Azure)

#### 2.A.1 Crear SQL Server

```bash
# Variables
SQL_SERVER_NAME="quickspeak-sql-server"
SQL_ADMIN_USER="quickspeakadmin"
SQL_ADMIN_PASSWORD="TuPasswordSegura123!"  # CÁMBIALO
RESOURCE_GROUP="quickspeak-resources"
LOCATION="eastus"

# Crear SQL Server
az sql server create \
  --name $SQL_SERVER_NAME \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --admin-user $SQL_ADMIN_USER \
  --admin-password $SQL_ADMIN_PASSWORD

# Configurar firewall para permitir servicios de Azure
az sql server firewall-rule create \
  --resource-group $RESOURCE_GROUP \
  --server $SQL_SERVER_NAME \
  --name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0

# Permitir tu IP para acceso desde tu máquina
MI_IP=$(curl -s https://api.ipify.org)
az sql server firewall-rule create \
  --resource-group $RESOURCE_GROUP \
  --server $SQL_SERVER_NAME \
  --name AllowMyIP \
  --start-ip-address $MI_IP \
  --end-ip-address $MI_IP
```

#### 2.A.2 Crear Database

```bash
DB_NAME="userdb"

# Crear database (tier Basic para desarrollo)
az sql db create \
  --resource-group $RESOURCE_GROUP \
  --server $SQL_SERVER_NAME \
  --name $DB_NAME \
  --service-objective Basic \
  --edition Basic

# Para producción, usar Standard:
# --service-objective S0 \
# --edition Standard
```

#### 2.A.3 Obtener Connection String

```bash
# Connection string
az sql db show-connection-string \
  --client jdbc \
  --name $DB_NAME \
  --server $SQL_SERVER_NAME

# Salida ejemplo:
# jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=userdb;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
```

**Guarda esto**:
```
DB_URL=jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=userdb;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
DB_USERNAME=quickspeakadmin
DB_PASSWORD=TuPasswordSegura123!
DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

#### 2.A.4 Ejecutar Schema SQL

**Opción 1: Usando Azure Data Studio / SSMS**
1. Conectar a: `quickspeak-sql-server.database.windows.net`
2. Usuario: `quickspeakadmin`
3. Password: Tu password
4. Database: `userdb`
5. Abrir y ejecutar: `database/schema.sql`

**Opción 2: Usando sqlcmd**
```bash
sqlcmd -S quickspeak-sql-server.database.windows.net \
  -d userdb \
  -U quickspeakadmin \
  -P TuPasswordSegura123! \
  -i database/schema.sql
```

---

### OPCIÓN B: Azure Database for PostgreSQL

#### 2.B.1 Crear PostgreSQL Server

```bash
# Variables
POSTGRES_SERVER="quickspeak-postgres"
POSTGRES_ADMIN_USER="quickspeakadmin"
POSTGRES_ADMIN_PASSWORD="TuPasswordSegura123!"
RESOURCE_GROUP="quickspeak-resources"
LOCATION="eastus"

# Crear servidor PostgreSQL
az postgres flexible-server create \
  --resource-group $RESOURCE_GROUP \
  --name $POSTGRES_SERVER \
  --location $LOCATION \
  --admin-user $POSTGRES_ADMIN_USER \
  --admin-password $POSTGRES_ADMIN_PASSWORD \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --version 14 \
  --storage-size 32

# Configurar firewall
az postgres flexible-server firewall-rule create \
  --resource-group $RESOURCE_GROUP \
  --name $POSTGRES_SERVER \
  --rule-name AllowAllAzure \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0
```

#### 2.B.2 Crear Database

```bash
DB_NAME="userdb"

az postgres flexible-server db create \
  --resource-group $RESOURCE_GROUP \
  --server-name $POSTGRES_SERVER \
  --database-name $DB_NAME
```

#### 2.B.3 Connection String

```
DB_URL=jdbc:postgresql://quickspeak-postgres.postgres.database.azure.com:5432/userdb?sslmode=require
DB_USERNAME=quickspeakadmin
DB_PASSWORD=TuPasswordSegura123!
DB_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

#### 2.B.4 Ejecutar Schema SQL

```bash
# Instalar psql si no lo tienes
psql -h quickspeak-postgres.postgres.database.azure.com \
  -U quickspeakadmin \
  -d userdb \
  -f database/schema-postgres.sql
```

---

## FASE 3: CREAR APP SERVICE

### 3.1 Crear App Service Plan

```bash
APP_SERVICE_PLAN="quickspeak-plan"
RESOURCE_GROUP="quickspeak-resources"
LOCATION="eastus"

# Crear plan (Free tier para desarrollo)
az appservice plan create \
  --name $APP_SERVICE_PLAN \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --sku F1 \
  --is-linux

# Para producción, usar B1 o S1:
# --sku B1
```

### 3.2 Crear Web App

```bash
APP_NAME="user-service-quickspeak"

az webapp create \
  --resource-group $RESOURCE_GROUP \
  --plan $APP_SERVICE_PLAN \
  --name $APP_NAME \
  --runtime "JAVA:17-java17"

# Verificar
az webapp show \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --query defaultHostName -o tsv

# Salida: user-service-quickspeak.azurewebsites.net
```

### 3.3 Configurar HTTPS Only

```bash
az webapp update \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --https-only true
```

---

## FASE 4: CONFIGURAR VARIABLES DE ENTORNO

### 4.1 Variables Requeridas

```bash
APP_NAME="user-service-quickspeak"
RESOURCE_GROUP="quickspeak-resources"

# Generar JWT secret (256-bit)
JWT_SECRET=$(openssl rand -base64 32)

# Configurar TODAS las variables
az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --settings \
    SPRING_PROFILES_ACTIVE="prod" \
    PORT="8443" \
    DB_URL="jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=userdb;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;" \
    DB_USERNAME="quickspeakadmin" \
    DB_PASSWORD="TuPasswordSegura123!" \
    DB_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver" \
    HIBERNATE_DIALECT="org.hibernate.dialect.SQLServerDialect" \
    DDL_AUTO="update" \
    SHOW_SQL="false" \
    JWT_SECRET="$JWT_SECRET" \
    JWT_EXPIRATION="86400000"
```

**⚠️ Si usas PostgreSQL**, cambia:
```bash
DB_URL="jdbc:postgresql://quickspeak-postgres.postgres.database.azure.com:5432/userdb?sslmode=require"
DB_DRIVER="org.postgresql.Driver"
HIBERNATE_DIALECT="org.hibernate.dialect.PostgreSQLDialect"
```

### 4.2 Variables de mTLS (después de generar certificados)

```bash
az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --settings \
    SSL_KEYSTORE_PASSWORD="quickspeak-keystore-pass" \
    SSL_TRUSTSTORE_PASSWORD="quickspeak-truststore-pass"
```

### 4.3 Verificar Variables

```bash
# Ver todas las variables
az webapp config appsettings list \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --output table
```

---

## FASE 5: DESPLEGAR APLICACIÓN

### 5.1 Compilar el Proyecto

```bash
# En el directorio del proyecto
cd "C:\Users\Kenneth\Documents\TEC\diseño\proyecto\user_service_quickspeak"

# Limpiar y compilar
mvn clean package -DskipTests

# Verificar que se generó el JAR
ls -lh target/*.jar

# Salida esperada:
# target/user-service-1.0.0-SNAPSHOT.jar
```

### 5.2 Desplegar a Azure

**Opción A: Deployment via Azure CLI**

```bash
APP_NAME="user-service-quickspeak"
RESOURCE_GROUP="quickspeak-resources"

# Desplegar JAR
az webapp deploy \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --src-path target/user-service-1.0.0-SNAPSHOT.jar \
  --type jar

# Ver progreso
az webapp log tail \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME
```

**Opción B: Deployment via Maven Plugin**

1. Agregar plugin en `pom.xml`:

```xml
<plugin>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-webapp-maven-plugin</artifactId>
    <version>2.11.0</version>
    <configuration>
        <subscriptionId>YOUR_SUBSCRIPTION_ID</subscriptionId>
        <resourceGroup>quickspeak-resources</resourceGroup>
        <appName>user-service-quickspeak</appName>
        <region>eastus</region>
        <pricingTier>F1</pricingTier>
        <runtime>
            <os>Linux</os>
            <javaVersion>Java 17</javaVersion>
            <webContainer>Java SE</webContainer>
        </runtime>
        <deployment>
            <resources>
                <resource>
                    <directory>${project.basedir}/target</directory>
                    <includes>
                        <include>*.jar</include>
                    </includes>
                </resource>
            </resources>
        </deployment>
    </configuration>
</plugin>
```

2. Desplegar:
```bash
mvn azure-webapp:deploy
```

### 5.3 Reiniciar App Service

```bash
az webapp restart \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP
```

---

## FASE 6: CONFIGURAR APIM

### 6.1 Crear APIM (si no existe)

```bash
APIM_NAME="apim-quick-speak"
RESOURCE_GROUP="quickspeak-resources"
LOCATION="eastus"
PUBLISHER_EMAIL="tu-email@example.com"
PUBLISHER_NAME="QuickSpeak Team"

# Crear APIM (Consumption tier - más económico)
az apim create \
  --name $APIM_NAME \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --publisher-email $PUBLISHER_EMAIL \
  --publisher-name "$PUBLISHER_NAME" \
  --sku-name Consumption \
  --no-wait

# ⚠️ Esto toma 20-30 minutos
```

### 6.2 Importar API

Ver archivo: `IMPORTAR_OPENAPI_A_AZURE.md`

Pasos resumidos:
1. Azure Portal → APIM → APIs
2. + Add API → OpenAPI
3. Subir `openapi-user-service.yaml`
4. URL suffix: `users`
5. Create

### 6.3 Configurar mTLS

Ver archivos:
- `INSTRUCCIONES_AZURE_MTLS.md`
- `GUIA_RAPIDA_BACKEND.md`

---

## FASE 7: VERIFICACIÓN

### 7.1 Health Check

```bash
# Verificar que la app está corriendo
curl https://user-service-quickspeak.azurewebsites.net/actuator/health

# Respuesta esperada:
# {"status":"UP"}
```

### 7.2 Test de Endpoints

```bash
# Test 1: Listar idiomas
curl https://user-service-quickspeak.azurewebsites.net/api/v1/languages

# Test 2: Registro de usuario
curl -X POST https://user-service-quickspeak.azurewebsites.net/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@quickspeak.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'

# Test 3: Login
curl -X POST https://user-service-quickspeak.azurewebsites.net/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@quickspeak.com",
    "password": "password123"
  }'
```

### 7.3 Ver Logs

```bash
# Logs en tiempo real
az webapp log tail \
  --name user-service-quickspeak \
  --resource-group quickspeak-resources

# Descargar logs
az webapp log download \
  --name user-service-quickspeak \
  --resource-group quickspeak-resources \
  --log-file logs.zip
```

---

## TROUBLESHOOTING

### Problema: App no inicia

**Síntomas**: Health check falla, error 503

**Soluciones**:
1. Verificar variables de entorno:
   ```bash
   az webapp config appsettings list --name APP_NAME --resource-group RESOURCE_GROUP
   ```

2. Ver logs:
   ```bash
   az webapp log tail --name APP_NAME --resource-group RESOURCE_GROUP
   ```

3. Verificar que el JAR se desplegó:
   ```bash
   az webapp deployment list-publishing-credentials --name APP_NAME --resource-group RESOURCE_GROUP
   ```

### Problema: No puede conectar a la base de datos

**Síntomas**: Errores de conexión en logs

**Soluciones**:
1. Verificar firewall rules:
   ```bash
   az sql server firewall-rule list --server SQL_SERVER --resource-group RESOURCE_GROUP
   ```

2. Verificar connection string:
   ```bash
   az webapp config appsettings list --name APP_NAME --resource-group RESOURCE_GROUP | grep DB_
   ```

3. Test de conexión:
   ```bash
   sqlcmd -S quickspeak-sql-server.database.windows.net -d userdb -U username -P password
   ```

### Problema: 401 Unauthorized

**Síntomas**: Endpoints protegidos retornan 401

**Soluciones**:
1. Verificar que JWT_SECRET está configurado
2. Verificar que el token se genera correctamente en login
3. Verificar que el header Authorization se envía correctamente

### Problema: mTLS no funciona

**Síntomas**: Error SSL handshake

**Soluciones**:
1. Verificar que el certificado está subido a APIM
2. Verificar que el backend está configurado correctamente
3. Verificar variables SSL_KEYSTORE_PASSWORD y SSL_TRUSTSTORE_PASSWORD

---

## 📊 RESUMEN DE COSTOS (Estimado)

| Recurso | Tier | Costo Mensual (USD) |
|---------|------|---------------------|
| Azure SQL Database | Basic | ~$5 |
| App Service | F1 (Free) | $0 |
| App Service | B1 (Basic) | ~$13 |
| APIM | Consumption | ~$0.035/10k calls |
| **Total (Free)** | | **~$5-10** |
| **Total (Básico)** | | **~$18-25** |

---

## ✅ CHECKLIST FINAL

Marca cuando completes cada paso:

### Base de Datos
- [ ] Resource Group creado
- [ ] SQL Server/PostgreSQL creado
- [ ] Database creada
- [ ] Firewall configurado
- [ ] Schema SQL ejecutado
- [ ] Connection string guardada

### App Service
- [ ] App Service Plan creado
- [ ] Web App creada
- [ ] HTTPS only habilitado
- [ ] Variables de entorno configuradas
- [ ] JAR desplegado
- [ ] App reiniciada

### Verificación
- [ ] Health check responde
- [ ] Endpoints de idiomas funcionan
- [ ] Registro de usuario funciona
- [ ] Login funciona y retorna JWT
- [ ] Logs sin errores

### APIM (Opcional si aún no)
- [ ] APIM creado
- [ ] API importada
- [ ] Backend configurado
- [ ] mTLS configurado

---

## 📞 AYUDA ADICIONAL

### Documentación de referencia:
- Azure CLI: https://docs.microsoft.com/en-us/cli/azure/
- Azure SQL: https://docs.microsoft.com/en-us/azure/azure-sql/
- Azure App Service: https://docs.microsoft.com/en-us/azure/app-service/
- Spring Boot Azure: https://spring.io/guides/gs/spring-boot-for-azure/

### Archivos del proyecto:
- `database/schema.sql` - Schema para Azure SQL
- `database/schema-postgres.sql` - Schema para PostgreSQL
- `IMPORTAR_OPENAPI_A_AZURE.md` - Importar API a APIM
- `INSTRUCCIONES_AZURE_MTLS.md` - Configurar mTLS
- `VARIABLES_ENTORNO.md` - Todas las variables explicadas

---

**¡Éxito con la migración!** 🚀

Si encuentras problemas, revisa los logs y verifica las variables de entorno.
