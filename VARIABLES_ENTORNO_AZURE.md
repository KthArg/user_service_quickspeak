# Variables de Entorno para Azure App Service

## Variables Obligatorias

### Base de Datos
```
DB_URL=jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=quickspeak-users-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
DB_USERNAME=sqladmin
DB_PASSWORD=TuPassword123!
DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
HIBERNATE_DIALECT=org.hibernate.dialect.SQLServerDialect
```

### Perfil de Spring
```
SPRING_PROFILE=prod
```

### JWT (Seguridad)
```
JWT_SECRET=tZy7m0lxDUoGEaaWrg3YhRrICMFW8gJ2MpZqgEzFzyE=
JWT_EXPIRATION=86400000
```

### Hibernate
```
DDL_AUTO=update
SHOW_SQL=false
```

---

## Configuración en Azure Portal

### Opción 1: Portal Web

1. Ir a tu **App Service**
2. **Configuration** (menú izquierdo)
3. **Application settings** → **New application setting**
4. Agregar cada variable:

| Name | Value |
|------|-------|
| `DB_URL` | `jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=quickspeak-users-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;` |
| `DB_USERNAME` | `sqladmin` |
| `DB_PASSWORD` | `TuPassword123!` |
| `DB_DRIVER` | `com.microsoft.sqlserver.jdbc.SQLServerDriver` |
| `HIBERNATE_DIALECT` | `org.hibernate.dialect.SQLServerDialect` |
| `SPRING_PROFILE` | `prod` |
| `JWT_SECRET` | `tZy7m0lxDUoGEaaWrg3YhRrICMFW8gJ2MpZqgEzFzyE=` |
| `JWT_EXPIRATION` | `86400000` |
| `DDL_AUTO` | `update` |
| `SHOW_SQL` | `false` |

5. Click **Save**
6. Restart el App Service

### Opción 2: Azure CLI

```bash
# Definir variables
RESOURCE_GROUP="quickspeak-rg"
APP_NAME="tu-app-service-name"

# Configurar todas las variables
az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --settings \
    SPRING_PROFILE=prod \
    DB_URL="jdbc:sqlserver://quickspeak-sql-server.database.windows.net:1433;database=quickspeak-users-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;" \
    DB_USERNAME=sqladmin \
    DB_PASSWORD=TuPassword123! \
    DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver \
    HIBERNATE_DIALECT=org.hibernate.dialect.SQLServerDialect \
    DDL_AUTO=update \
    SHOW_SQL=false \
    JWT_SECRET=tZy7m0lxDUoGEaaWrg3YhRrICMFW8gJ2MpZqgEzFzyE= \
    JWT_EXPIRATION=86400000
```

---

## Variables Opcionales

### Puerto (NO configurar en Azure)
Azure App Service maneja el puerto automáticamente vía variable `PORT`.
**No configurar manualmente.**

---

## Seguridad - IMPORTANTE

### JWT_SECRET
⚠️ **CAMBIAR en producción**

Generar una clave segura:
```bash
# PowerShell
$bytes = New-Object byte[] 32
(New-Object Security.Cryptography.RNGCryptoServiceProvider).GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```

### DB_PASSWORD
⚠️ **Usar Azure Key Vault en producción**

Mejor práctica:
1. Guardar password en Key Vault
2. Referenciar desde App Service:
```
@Microsoft.KeyVault(SecretUri=https://tu-vault.vault.azure.net/secrets/db-password/)
```

---

## Valores Recomendados por Ambiente

### Producción
```
SPRING_PROFILE=prod
DDL_AUTO=validate
SHOW_SQL=false
JWT_EXPIRATION=3600000  # 1 hora
```

### Staging
```
SPRING_PROFILE=staging
DDL_AUTO=update
SHOW_SQL=false
JWT_EXPIRATION=86400000  # 24 horas
```

### Development (Azure)
```
SPRING_PROFILE=dev
DDL_AUTO=update
SHOW_SQL=true
JWT_EXPIRATION=86400000  # 24 horas
```

---

## Verificar Configuración

Después de configurar las variables:

1. Restart el App Service
2. Verificar logs:
```bash
az webapp log tail --resource-group quickspeak-rg --name tu-app-name
```

3. Verificar health check:
```bash
curl https://tu-app.azurewebsites.net/actuator/health
```

Esperado:
```json
{
  "status": "UP"
}
```

---

## Lista de Chequeo

Antes de desplegar:

- [ ] Todas las 10 variables configuradas
- [ ] JWT_SECRET cambiado (no usar el de desarrollo)
- [ ] DB_PASSWORD correcto
- [ ] SPRING_PROFILE=prod
- [ ] DDL_AUTO=validate o update
- [ ] SHOW_SQL=false
- [ ] App Service reiniciado
- [ ] Health check funciona
- [ ] Registro de usuario de prueba funciona
