# 🔐 VARIABLES DE ENTORNO - USER SERVICE

Documentación completa de todas las variables de entorno necesarias para el microservicio.

---

## 📋 TABLA DE VARIABLES

| Variable | Requerido | Default | Descripción |
|----------|-----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | ✅ | dev | Perfil de Spring Boot (dev/prod) |
| `PORT` | ✅ | 8082 | Puerto HTTP (8082) o HTTPS (8443) |
| `DB_URL` | ✅ | jdbc:h2:mem:userdb | JDBC URL de la base de datos |
| `DB_USERNAME` | ✅ | sa | Usuario de la base de datos |
| `DB_PASSWORD` | ✅ | password | Contraseña de la base de datos |
| `DB_DRIVER` | ✅ | org.h2.Driver | Driver JDBC |
| `HIBERNATE_DIALECT` | ⚠️ | (auto) | Dialecto de Hibernate |
| `DDL_AUTO` | ⚠️ | update | Estrategia DDL de Hibernate |
| `SHOW_SQL` | ❌ | true | Mostrar SQL en logs |
| `JWT_SECRET` | ✅ | (insecure) | Secret key para JWT (256-bit) |
| `JWT_EXPIRATION` | ❌ | 86400000 | Tiempo de expiración JWT (ms) |
| `SSL_KEYSTORE_PASSWORD` | ⚠️ | - | Password del KeyStore (mTLS) |
| `SSL_TRUSTSTORE_PASSWORD` | ⚠️ | - | Password del TrustStore (mTLS) |

**Leyenda**:
- ✅ **Requerido siempre**
- ⚠️ **Requerido en producción**
- ❌ **Opcional**

---

## 🔧 CONFIGURACIÓN POR ENTORNO

### DESARROLLO LOCAL (dev)

```bash
# .env (local development)
SPRING_PROFILES_ACTIVE=dev
PORT=8082
DB_URL=jdbc:h2:mem:userdb
DB_USERNAME=sa
DB_PASSWORD=password
DB_DRIVER=org.h2.Driver
JWT_SECRET=your-256-bit-secret-key-change-this-in-production
JWT_EXPIRATION=86400000
SHOW_SQL=true
DDL_AUTO=update
```

**Características**:
- Usa H2 en memoria (no requiere BD externa)
- Puerto 8082 (HTTP)
- SQL visible en logs
- DDL auto-update (crea/actualiza tablas)

---

### PRODUCCIÓN AZURE - SQL SERVER

```bash
# Azure App Service → Configuration → Application Settings
SPRING_PROFILES_ACTIVE=prod
PORT=8443

# Base de datos Azure SQL
DB_URL=jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=userdb;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
DB_USERNAME=quickspeakadmin
DB_PASSWORD=TuPasswordSegura123!
DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
HIBERNATE_DIALECT=org.hibernate.dialect.SQLServerDialect

# Configuración JPA
DDL_AUTO=validate
SHOW_SQL=false

# JWT
JWT_SECRET=<GENERAR_CON: openssl rand -base64 32>
JWT_EXPIRATION=86400000

# mTLS (después de configurar certificados)
SSL_KEYSTORE_PASSWORD=quickspeak-keystore-pass
SSL_TRUSTSTORE_PASSWORD=quickspeak-truststore-pass
```

---

### PRODUCCIÓN AZURE - POSTGRESQL

```bash
# Azure App Service → Configuration → Application Settings
SPRING_PROFILES_ACTIVE=prod
PORT=8443

# Base de datos Azure PostgreSQL
DB_URL=jdbc:postgresql://quickspeak-postgres.postgres.database.azure.com:5432/userdb?sslmode=require
DB_USERNAME=quickspeakadmin
DB_PASSWORD=TuPasswordSegura123!
DB_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# Configuración JPA
DDL_AUTO=validate
SHOW_SQL=false

# JWT
JWT_SECRET=<GENERAR_CON: openssl rand -base64 32>
JWT_EXPIRATION=86400000

# mTLS
SSL_KEYSTORE_PASSWORD=quickspeak-keystore-pass
SSL_TRUSTSTORE_PASSWORD=quickspeak-truststore-pass
```

---

## 📖 DESCRIPCIÓN DETALLADA

### 1. SPRING_PROFILES_ACTIVE

**Valores posibles**:
- `dev` - Desarrollo local
- `prod` - Producción en Azure

**Impacto**:
- Carga `application.yml` + `application-{profile}.yml`
- En prod: activa configuración mTLS, SSL, etc.

**Ejemplo**:
```bash
SPRING_PROFILES_ACTIVE=prod
```

---

### 2. PORT

**Puerto del servidor**:
- `8082` - HTTP (desarrollo)
- `8443` - HTTPS (producción con SSL)

**Importante**: En Azure, App Service espera el puerto configurado aquí.

**Ejemplo**:
```bash
PORT=8443
```

---

### 3. DB_URL

**Formato JDBC URL**:

**Azure SQL Server**:
```
jdbc:sqlserver://SERVER.database.windows.net:1433;database=DBNAME;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
```

**PostgreSQL**:
```
jdbc:postgresql://SERVER.postgres.database.azure.com:5432/DBNAME?sslmode=require
```

**H2 (desarrollo)**:
```
jdbc:h2:mem:userdb
```

---

### 4. DB_USERNAME y DB_PASSWORD

**Usuario y contraseña de la base de datos**.

**⚠️ SEGURIDAD**:
- NUNCA commitear en Git
- Usar contraseñas fuertes en producción
- Considerar Azure Key Vault para secretos

**Ejemplo**:
```bash
DB_USERNAME=quickspeakadmin
DB_PASSWORD=MyStr0ngP@ssw0rd!
```

---

### 5. DB_DRIVER

**Driver JDBC a usar**:
- `com.microsoft.sqlserver.jdbc.SQLServerDriver` - SQL Server
- `org.postgresql.Driver` - PostgreSQL
- `org.h2.Driver` - H2 (desarrollo)

**Debe coincidir con la dependencia en pom.xml**.

---

### 6. HIBERNATE_DIALECT

**Dialecto SQL de Hibernate** (opcional, Hibernate lo detecta automáticamente):
- `org.hibernate.dialect.SQLServerDialect`
- `org.hibernate.dialect.PostgreSQLDialect`
- `org.hibernate.dialect.H2Dialect`

**Recomendación**: Especificarlo explícitamente en producción.

---

### 7. DDL_AUTO

**Estrategia de generación de schema**:
- `create` - Borra y recrea tablas (⚠️ PELIGRO)
- `create-drop` - Crea al iniciar, borra al terminar
- `update` - Actualiza schema sin borrar datos (desarrollo)
- `validate` - Solo valida que coincide (producción) ✅
- `none` - No hace nada

**Recomendación**:
- **Desarrollo**: `update`
- **Producción**: `validate` (ejecutar scripts SQL manualmente)

---

### 8. SHOW_SQL

**Mostrar SQL queries en logs**:
- `true` - Muestra SQL (desarrollo)
- `false` - No muestra (producción)

**Ejemplo**:
```bash
SHOW_SQL=false
```

---

### 9. JWT_SECRET

**Secret key para firmar tokens JWT**.

**⚠️ MUY IMPORTANTE**:
- Mínimo 256 bits (32 bytes)
- Usar valor aleatorio y único
- NUNCA usar el valor por defecto en producción

**Generar**:
```bash
# Linux/Mac/Windows (Git Bash)
openssl rand -base64 32

# PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

**Ejemplo**:
```bash
JWT_SECRET=aB3dF6hJ9kL2mN5pQ8rT1uV4wX7yZ0aC3eF6gH9iJ2kL5mN8pQ1rT4uV7wX0yZ
```

---

### 10. JWT_EXPIRATION

**Tiempo de vida del token en milisegundos**:
- `3600000` - 1 hora
- `86400000` - 24 horas (default)
- `604800000` - 7 días

**Ejemplo**:
```bash
JWT_EXPIRATION=86400000  # 24 horas
```

---

### 11. SSL_KEYSTORE_PASSWORD

**Password del archivo KeyStore** (`server-keystore.p12`).

**Requerido cuando**: `SPRING_PROFILES_ACTIVE=prod` y mTLS activado.

**Valor actual** (cambiar en producción real):
```bash
SSL_KEYSTORE_PASSWORD=quickspeak-keystore-pass
```

---

### 12. SSL_TRUSTSTORE_PASSWORD

**Password del archivo TrustStore** (`server-truststore.jks`).

**Requerido cuando**: mTLS activado.

**Valor actual**:
```bash
SSL_TRUSTSTORE_PASSWORD=quickspeak-truststore-pass
```

---

## 🔄 CÓMO CONFIGURAR EN AZURE

### Opción 1: Azure Portal (UI)

1. Ir a **Azure Portal** → **App Service**
2. Seleccionar `user-service-quickspeak`
3. Menú izquierdo → **Configuration**
4. Tab **Application settings**
5. Click **+ New application setting**
6. Agregar cada variable:
   - Name: `SPRING_PROFILES_ACTIVE`
   - Value: `prod`
7. Click **OK**
8. Repetir para todas las variables
9. Click **Save** (arriba)
10. Click **Continue** cuando pida confirmar

---

### Opción 2: Azure CLI (Command Line)

```bash
APP_NAME="user-service-quickspeak"
RESOURCE_GROUP="quickspeak-resources"

# Configurar todas las variables de una vez
az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --settings \
    SPRING_PROFILES_ACTIVE="prod" \
    PORT="8443" \
    DB_URL="jdbc:sqlserver://..." \
    DB_USERNAME="quickspeakadmin" \
    DB_PASSWORD="TuPassword" \
    DB_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver" \
    HIBERNATE_DIALECT="org.hibernate.dialect.SQLServerDialect" \
    DDL_AUTO="validate" \
    SHOW_SQL="false" \
    JWT_SECRET="$(openssl rand -base64 32)" \
    JWT_EXPIRATION="86400000" \
    SSL_KEYSTORE_PASSWORD="quickspeak-keystore-pass" \
    SSL_TRUSTSTORE_PASSWORD="quickspeak-truststore-pass"
```

---

### Opción 3: Archivo de Configuración (NO recomendado)

**⚠️ NO hacer esto con secretos**:
```yaml
# application-prod.yml (NO incluir passwords aquí)
jwt:
  secret: ${JWT_SECRET}  # ✅ Usar variable de entorno
  # secret: hardcoded-value  # ❌ NUNCA hacer esto
```

---

## ✅ VERIFICACIÓN

### Verificar que las variables están configuradas:

```bash
# Listar todas las variables
az webapp config appsettings list \
  --name user-service-quickspeak \
  --resource-group quickspeak-resources \
  --output table

# Verificar una variable específica
az webapp config appsettings list \
  --name user-service-quickspeak \
  --resource-group quickspeak-resources \
  --query "[?name=='DB_URL'].value" \
  --output tsv
```

---

## 🔒 SEGURIDAD

### ⚠️ NUNCA hacer esto:

❌ Commitear variables en Git
❌ Compartir JWT_SECRET
❌ Usar passwords débiles
❌ Dejar defaults en producción
❌ Hardcodear secretos en código

### ✅ Mejores prácticas:

✅ Usar variables de entorno
✅ Generar JWT_SECRET aleatorio
✅ Passwords fuertes (16+ caracteres)
✅ Rotar secretos periódicamente
✅ Usar Azure Key Vault en producción real
✅ Limitar acceso a Configuration en Azure

---

## 📊 CHECKLIST DE VARIABLES

### Desarrollo Local
- [ ] SPRING_PROFILES_ACTIVE=dev
- [ ] PORT=8082
- [ ] DB_URL (H2 en memoria)
- [ ] JWT_SECRET (cualquier valor para dev)

### Producción Azure
- [ ] SPRING_PROFILES_ACTIVE=prod
- [ ] PORT=8443
- [ ] DB_URL (Azure SQL/PostgreSQL)
- [ ] DB_USERNAME
- [ ] DB_PASSWORD
- [ ] DB_DRIVER
- [ ] HIBERNATE_DIALECT
- [ ] DDL_AUTO=validate
- [ ] SHOW_SQL=false
- [ ] JWT_SECRET (generado aleatoriamente)
- [ ] JWT_EXPIRATION
- [ ] SSL_KEYSTORE_PASSWORD (si mTLS)
- [ ] SSL_TRUSTSTORE_PASSWORD (si mTLS)

---

## 📞 AYUDA

Si una variable no funciona:
1. Verificar el nombre (sensible a mayúsculas)
2. Verificar que no tenga espacios extra
3. Reiniciar el App Service después de cambios
4. Ver logs para errores de configuración

```bash
az webapp log tail --name APP_NAME --resource-group RESOURCE_GROUP
```

---

**Última actualización**: Noviembre 2025
**Importante**: Mantener los secretos seguros y rotar periódicamente.
