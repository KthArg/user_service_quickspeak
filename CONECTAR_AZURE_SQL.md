# Conectar a Azure SQL Database

## Pasos para Conectar

### 1. Detener el Servicio Actual

Si tienes el servicio corriendo con H2:
- Presiona **Ctrl+C** en la terminal donde corre `mvn spring-boot:run`

### 2. Ejecutar Script de Conexión

```cmd
conectar-azure.bat
```

Este script:
- ✅ Carga todas las variables de entorno de Azure SQL
- ✅ Configura el perfil de producción
- ✅ Inicia el servicio conectado a Azure
- ✅ Crea las tablas automáticamente en Azure SQL

### 3. Verificar Conexión

Cuando veas este mensaje:
```
Started UserServiceApplication in X.XXX seconds
```

El servicio está conectado a Azure SQL.

---

## Diferencias con H2

### Con H2 (Local)
- Base de datos: En memoria (temporal)
- Datos: Se pierden al detener el servicio
- Comando: `mvn spring-boot:run`

### Con Azure SQL (Producción)
- Base de datos: Azure SQL Database (permanente)
- Datos: Persisten aunque detengas el servicio
- Comando: `conectar-azure.bat`

---

## Verificar Tablas en Azure Data Studio

### 1. Abrir Azure Data Studio

### 2. Conectar (si no estás conectado)
- Server: `quickspeak-sql-server.database.windows.net`
- Database: `quickspeak-users-db`
- User: `sqladmin`
- Password: `TuPassword123!`

### 3. Ver Tablas

Después de iniciar el servicio:
1. Expandir `quickspeak-users-db` → `Tables`
2. Click derecho → **Refresh**
3. Deberías ver:
   - `dbo.languages` (20 idiomas)
   - `dbo.users`
   - `dbo.user_languages`
   - `dbo.user_roles`

### 4. Consultar Datos

```sql
-- Ver idiomas
SELECT * FROM languages;

-- Ver usuarios (vacío al inicio)
SELECT * FROM users;
```

---

## Registrar Usuario de Prueba

Con el servicio corriendo en Azure SQL:

```cmd
curl -X POST http://localhost:8082/api/v1/auth/register -H "Content-Type: application/json" -d "{\"email\":\"test@quickspeak.com\",\"password\":\"password123\",\"firstName\":\"Test\",\"lastName\":\"User\"}"
```

**Este usuario SÍ quedará guardado permanentemente en Azure SQL.**

Verifica en Azure Data Studio:
```sql
SELECT * FROM users;
```

---

## Cambiar entre H2 y Azure SQL

### Usar H2 (desarrollo local)
```cmd
limpiar-env.bat
mvn spring-boot:run
```

### Usar Azure SQL (producción)
```cmd
conectar-azure.bat
```

---

## Solución de Problemas

### Error: Cannot create PoolableConnectionFactory

**Causa**: No puede conectar a Azure SQL

**Soluciones**:
1. Verificar que la base de datos existe en Azure Portal
2. Verificar firewall rules (tu IP debe estar permitida)
3. Verificar que el password es correcto: `TuPassword123!`
4. Verificar que el servidor no está pausado (serverless)

**Agregar tu IP en Azure Portal**:
1. Ir a: SQL Server `quickspeak-sql-server`
2. **Networking** → **Firewall rules**
3. Click **Add client IP**
4. **Save**

### Error: Schema-validation: missing table

**Causa**: La base de datos existe pero está vacía

**Solución**: El script actualizado `conectar-azure.bat` ahora crea las tablas automáticamente usando `ddl-auto=update`

### Error: Login failed for user 'sqladmin'

- Verificar password en `conectar-azure.bat`
- Debe ser exactamente: `TuPassword123!`

### Base de datos pausada (Serverless)

- Primera conexión puede tardar 1-2 minutos (se reactiva automáticamente)
- Espera y reintenta

---

## Costos

**Azure SQL Serverless**:
- Se pausa después de 1 hora sin uso
- Solo cobran cuando está activo
- Costo estimado: $5-15/mes

**Recomendación**:
- Usa H2 para desarrollo diario
- Usa Azure SQL solo cuando necesites probar persistencia o integración completa
