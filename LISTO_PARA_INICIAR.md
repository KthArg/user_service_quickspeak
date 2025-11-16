# ✅ Ambiente Listo para Iniciar

## Cambios Realizados

### 1. Actualicé `application.yml`
- **JWT_SECRET**: Ahora tiene un valor por defecto seguro de 256 bits
- Ya no requiere configurar variables de entorno para desarrollo local

### 2. Creé Script de Limpieza
- **Archivo**: `limpiar-env.bat`
- **Propósito**: Limpia variables de entorno que puedan interferir

---

## Instrucciones para Iniciar

### Paso 1: Limpiar Variables de Entorno

Ejecuta el script de limpieza:

```cmd
limpiar-env.bat
```

**Esto elimina**:
- `SPRING_PROFILE`
- `JWT_SECRET`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- Todas las demás variables de configuración

### Paso 2: Iniciar el Microservicio

```cmd
mvn spring-boot:run
```

**El servicio iniciará con**:
- Base de datos: H2 (en memoria)
- Puerto: 8082
- Perfil: dev
- JWT Secret: Configurado por defecto

### Paso 3: Verificar que Funciona

En otra terminal:

```cmd
curl http://localhost:8082/actuator/health
```

**Respuesta esperada**:
```json
{
  "status": "UP"
}
```

---

## Acceso a la Consola H2

**URL**: http://localhost:8082/h2-console

**Credenciales**:
- JDBC URL: `jdbc:h2:mem:userdb`
- User Name: `sa`
- Password: `password`

---

## Configuración Actual

### Base de Datos (Desarrollo)
- Tipo: H2 (en memoria)
- Auto-creación de tablas: Activado
- Datos de prueba: Se cargan automáticamente desde `data.sql`

### Seguridad JWT
- Secret: Configurado por defecto (256 bits)
- Expiración: 24 horas

### Perfil Activo
- Por defecto: `dev`
- Sin necesidad de configurar `SPRING_PROFILE`

---

## Para Producción (Azure SQL)

Cuando quieras conectar a Azure SQL:

1. Crear la base de datos en Azure Portal (ver `CONFIGURACION_BASE_DATOS.md`)
2. Configurar variables de entorno:

```cmd
set SPRING_PROFILE=prod
set JWT_SECRET=tu-clave-secreta-de-256-bits-cambiar-en-produccion-12345678
set DB_URL=jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=userservice-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
set DB_USERNAME=sqladmin
set DB_PASSWORD=TuPassword123!
set DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
set HIBERNATE_DIALECT=org.hibernate.dialect.SQLServerDialect
```

3. Iniciar:

```cmd
mvn spring-boot:run
```

---

## Solución de Problemas

### Error: "Could not resolve placeholder 'JWT_SECRET'"
- Ejecutar `limpiar-env.bat`
- Reiniciar la terminal
- Intentar nuevamente

### Puerto 8082 en uso
```cmd
# Ver qué proceso usa el puerto
netstat -ano | findstr :8082

# Matar el proceso (reemplazar PID con el número)
taskkill /PID <PID> /F
```

### Tablas no se crean
- Verificar que exista `src/main/resources/schema.sql` y `data.sql`
- Revisar logs de Hibernate

---

## Resumen

**Estado Actual**: ✅ Listo para iniciar

**Para empezar ahora mismo**:
```cmd
limpiar-env.bat
mvn spring-boot:run
```

**Endpoints disponibles**:
- Health: http://localhost:8082/actuator/health
- H2 Console: http://localhost:8082/h2-console
- API Base: http://localhost:8082/api/v1/
