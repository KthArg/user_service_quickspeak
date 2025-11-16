# Configuración Base de Datos - User Service

## Plan Recomendado (Evitar Consumo Excesivo)

**Azure SQL Database - Serverless**
- Compute: Serverless
- vCores: 1 (mínimo)
- Auto-pause: 1 hora
- Storage: 32 GB
- **Costo**: ~$5-15/mes (solo cuando está activo)

---

## Crear Base de Datos en Azure Portal

### 1. Acceder a Azure Portal

1. Ir a https://portal.azure.com
2. Login con tu cuenta

### 2. Crear SQL Database

1. Click en **"Create a resource"**
2. Buscar **"SQL Database"**
3. Click en **"Create"**

### 3. Configuración Básica

**Subscription**: Tu subscripción

**Resource Group**:
- Seleccionar existente o crear nuevo: `quickspeak-rg`

**Database name**: `userservice-db`

**Server**: Click en **"Create new"**
- Server name: `quickspeak-sql-server` (debe ser único globalmente)
- Location: **East US** (o el más cercano)
- Authentication method: **Use SQL authentication**
- Server admin login: `sqladmin`
- Password: `TuPassword123!` (guardar este password)
- Click **"OK"**

**Want to use SQL elastic pool?**: **No**

**Workload environment**: **Development**

### 4. Configuración Compute + Storage (IMPORTANTE)

Click en **"Configure database"**

**Service tier**:
- Seleccionar **"General Purpose"**

**Compute tier**:
- Seleccionar **"Serverless"** ⚠️ (IMPORTANTE para evitar costos)

**Hardware**:
- Gen5

**vCores**:
- Min: **0.5**
- Max: **1**

**Data max size**: **32 GB**

**Auto-pause delay**:
- **1 hour** (se pausa automáticamente después de 1 hora sin uso)

**Enable zone redundancy**: **No**

Click **"Apply"**

### 5. Networking

**Connectivity method**: **Public endpoint**

**Firewall rules**:
- ✅ Allow Azure services and resources to access this server
- ✅ Add current client IP address

### 6. Additional Settings

**Use existing data**: **None**

**Collation**: `SQL_Latin1_General_CP1_CI_AS` (default)

**Enable Microsoft Defender for SQL**: **Not now**

### 7. Review + Create

1. Click **"Review + create"**
2. Verificar que dice **"Serverless"** y **"1 vCore"**
3. Click **"Create"**
4. Esperar 2-3 minutos

---

## Conectar con Azure Data Studio

### 1. Instalar Azure Data Studio

Descargar de: https://aka.ms/azuredatastudio

### 2. Obtener Connection String

En Azure Portal:
1. Ir a tu base de datos `userservice-db`
2. Click en **"Connection strings"** (menú izquierdo)
3. Copiar el **Server name**: `quickspeak-sql-server.database.windows.net`

### 3. Conectar en Azure Data Studio

1. Abrir Azure Data Studio
2. Click en **"New Connection"** (o Ctrl+N)

**Connection Details**:
- Connection type: **Microsoft SQL Server**
- Server: `quickspeak-sql-server.database.windows.net`
- Authentication type: **SQL Login**
- User name: `sqladmin`
- Password: `TuPassword123!`
- Database: `userservice-db`
- Encrypt: **Mandatory**
- Trust server certificate: **False**

3. Click **"Connect"**

### 4. Verificar Tablas

1. Expandir servidor → `userservice-db` → `Tables`
2. Debería estar vacío (las tablas se crearán automáticamente al iniciar el servicio)

---

## Configurar Microservicio

### 1. Crear Archivo de Variables de Entorno

En la raíz del proyecto, crear archivo `.env`:

```bash
DB_URL=jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=userservice-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
DB_USERNAME=sqladmin
DB_PASSWORD=TuPassword123!
DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
HIBERNATE_DIALECT=org.hibernate.dialect.SQLServerDialect
DDL_AUTO=update
SHOW_SQL=false
SPRING_PROFILE=prod
JWT_SECRET=tu-clave-secreta-de-256-bits-cambiar-en-produccion-12345678
JWT_EXPIRATION=86400000
```

### 2. Cargar Variables (Windows CMD)

```cmd
set DB_URL=jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=userservice-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
set DB_USERNAME=sqladmin
set DB_PASSWORD=TuPassword123!
set DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
set HIBERNATE_DIALECT=org.hibernate.dialect.SQLServerDialect
set SPRING_PROFILE=prod
```

### 3. Iniciar Microservicio

```bash
mvn spring-boot:run
```

Las tablas se crean automáticamente al iniciar.

### 4. Verificar en Azure Data Studio

1. Refrescar la lista de tablas (F5)
2. Deberías ver:
   - `users`
   - `user_languages`
   - `languages`

---

## Administración

### Ver Datos en Azure Data Studio

```sql
-- Ver usuarios
SELECT * FROM users;

-- Ver idiomas
SELECT * FROM languages;

-- Ver relación usuario-idiomas
SELECT u.email, l.name, ul.is_native
FROM users u
JOIN user_languages ul ON u.id = ul.user_id
JOIN languages l ON l.id = ul.language_id;
```

### Pausar Base de Datos (Evitar Costos)

**Azure Portal**:
1. Ir a `userservice-db`
2. Click **"Pause"** en la parte superior
3. Para reanudar: Click **"Resume"**

**Nota**: Con Serverless, se pausa automáticamente después de 1 hora sin uso.

### Monitorear Costos

1. Azure Portal → **Cost Management**
2. Filtrar por Resource Group: `quickspeak-rg`
3. Ver costos diarios/semanales

---

## Solución de Problemas

### Error: Cannot connect to server

1. Verificar firewall rules en Azure Portal
2. Agregar tu IP actual:
   - Ir a SQL Server `quickspeak-sql-server`
   - **Networking** → **Firewall rules**
   - Click **"Add client IP"**
   - Save

### Error: Login failed

- Verificar usuario: `sqladmin`
- Verificar password correcto
- Verificar nombre de servidor completo

### Base de datos pausada

- Esperar 1-2 minutos a que se reactive automáticamente
- O activar manualmente en Azure Portal

---

## Desarrollo Local (Alternativa Gratis)

Para desarrollo sin costos, usar H2 (ya configurado):

```bash
# Sin variables de entorno
mvn spring-boot:run
```

**Acceso**: http://localhost:8082/h2-console
- URL: `jdbc:h2:mem:userdb`
- User: `sa`
- Password: `password`
