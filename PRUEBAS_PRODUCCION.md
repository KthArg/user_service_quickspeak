# Pruebas de Producción - User Service

## URL Base
Reemplaza `<tu-app>` con el nombre de tu App Service.

```
https://<tu-app>.azurewebsites.net
```

Ejemplo: `https://quickspeak-users.azurewebsites.net`

---

## 1. Health Check (Más Básico)

**Verifica**: Servicio arrancó correctamente

```bash
curl https://<tu-app>.azurewebsites.net/actuator/health
```

**Respuesta esperada:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "Microsoft SQL Server"
      }
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

✅ **Éxito si**: `status: "UP"` y `db.status: "UP"`
❌ **Fallo si**: `status: "DOWN"` o no responde

---

## 2. Obtener Idiomas (Verifica BD)

**Verifica**: Conexión a base de datos + Datos cargados

```bash
curl https://<tu-app>.azurewebsites.net/api/v1/languages
```

**Respuesta esperada:**
```json
[
  {
    "id": 1,
    "code": "en",
    "name": "English",
    "flagUrl": "https://flagcdn.com/w320/us.png"
  },
  {
    "id": 2,
    "code": "es",
    "name": "Spanish",
    "flagUrl": "https://flagcdn.com/w320/es.png"
  }
  // ... 18 idiomas más
]
```

✅ **Éxito si**: Retorna array con 20 idiomas
❌ **Fallo si**: Error 500 o array vacío

---

## 3. Registrar Usuario (Verifica Escritura en BD)

**Verifica**: Puede escribir en base de datos + JWT funciona

```bash
curl -X POST https://<tu-app>.azurewebsites.net/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"prod-test@quickspeak.com\",\"password\":\"Password123!\",\"firstName\":\"Prod\",\"lastName\":\"Test\"}"
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJmaXJzdE5hbWUiOiJQcm9kI...",
  "user": {
    "id": 1,
    "email": "prod-test@quickspeak.com",
    "firstName": "Prod",
    "lastName": "Test",
    "status": "ACTIVE",
    "roles": ["STUDENT"]
  }
}
```

✅ **Éxito si**: Retorna token JWT y datos del usuario
❌ **Fallo si**: Error 400/500 o no retorna token

**Guardar el token** para siguientes pruebas:
```bash
TOKEN="<pegar-token-aqui>"
```

---

## 4. Login (Verifica Autenticación)

**Verifica**: Autenticación contra BD

```bash
curl -X POST https://<tu-app>.azurewebsites.net/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"prod-test@quickspeak.com\",\"password\":\"Password123!\"}"
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "user": {
    "id": 1,
    "email": "prod-test@quickspeak.com"
  }
}
```

✅ **Éxito si**: Retorna token válido
❌ **Fallo si**: Error 401/500

---

## 5. Obtener Perfil (Verifica JWT + Autorización)

**Verifica**: JWT válido + Endpoint protegido

```bash
curl https://<tu-app>.azurewebsites.net/api/v1/users/1/profile \
  -H "Authorization: Bearer <TOKEN>"
```

**Respuesta esperada:**
```json
{
  "id": 1,
  "email": "prod-test@quickspeak.com",
  "firstName": "Prod",
  "lastName": "Test",
  "status": "ACTIVE",
  "roles": ["STUDENT"],
  "nativeLanguage": null,
  "learningLanguages": []
}
```

✅ **Éxito si**: Retorna perfil del usuario
❌ **Fallo si**: Error 401 (token inválido) o 403 (no autorizado)

---

## 6. Agregar Idioma Nativo (Verifica Relaciones BD)

**Verifica**: Relaciones entre tablas (users ↔ languages)

```bash
curl -X POST "https://<tu-app>.azurewebsites.net/api/v1/users/1/languages?languageId=2&isNative=true" \
  -H "Authorization: Bearer <TOKEN>"
```

**Respuesta esperada:**
```json
{
  "id": 1,
  "userId": 1,
  "languageId": 2,
  "language": {
    "id": 2,
    "code": "es",
    "name": "Spanish"
  },
  "isNative": true,
  "addedAt": "2025-11-14T19:30:00"
}
```

✅ **Éxito si**: Retorna el idioma agregado
❌ **Fallo si**: Error 400/500

---

## 7. Verificar en Azure Data Studio

**Verifica**: Datos realmente persistieron en Azure SQL

Conectar a: `quickspeak-sql-server.database.windows.net`

```sql
-- Ver usuarios creados
SELECT * FROM users;

-- Ver idiomas de usuario
SELECT u.email, l.name, ul.is_native
FROM users u
JOIN user_languages ul ON u.id = ul.user_id
JOIN languages l ON l.id = ul.language_id;

-- Contar registros
SELECT
  (SELECT COUNT(*) FROM users) as total_users,
  (SELECT COUNT(*) FROM languages) as total_languages,
  (SELECT COUNT(*) FROM user_languages) as total_user_languages;
```

✅ **Éxito si**: Ves el usuario creado y sus idiomas
❌ **Fallo si**: Tablas vacías o sin el usuario

---

## 8. Ver Logs en Azure

**Verifica**: Errores en tiempo real

```bash
# Azure CLI
az webapp log tail --resource-group quickspeak-rg --name <tu-app>
```

**O en Azure Portal:**
1. App Service → **Log stream** (menú izquierdo)
2. Observar logs en tiempo real

**Buscar:**
- ✅ `Started UserServiceApplication in X seconds`
- ✅ `Successfully initialized 20 languages`
- ❌ Errores: `ERROR`, `Exception`, `Failed`

---

## Checklist de Pruebas

| # | Prueba | Verifica | Estado |
|---|--------|----------|--------|
| 1 | Health Check | Servicio UP | ⬜ |
| 2 | GET /languages | BD conectada + Datos | ⬜ |
| 3 | POST /register | Escritura BD + JWT | ⬜ |
| 4 | POST /login | Autenticación | ⬜ |
| 5 | GET /profile | JWT + Autorización | ⬜ |
| 6 | POST /languages | Relaciones BD | ⬜ |
| 7 | Azure Data Studio | Persistencia real | ⬜ |
| 8 | Logs Azure | Sin errores | ⬜ |

---

## Prueba Rápida (Todo en Uno)

Script completo para copiar y pegar (Windows CMD):

```cmd
set APP_URL=https://<tu-app>.azurewebsites.net

echo === 1. Health Check ===
curl %APP_URL%/actuator/health

echo.
echo === 2. Obtener Idiomas ===
curl %APP_URL%/api/v1/languages

echo.
echo === 3. Registrar Usuario ===
curl -X POST %APP_URL%/api/v1/auth/register -H "Content-Type: application/json" -d "{\"email\":\"test@test.com\",\"password\":\"Pass123!\",\"firstName\":\"Test\",\"lastName\":\"User\"}"

echo.
echo === 4. Login ===
curl -X POST %APP_URL%/api/v1/auth/login -H "Content-Type: application/json" -d "{\"email\":\"test@test.com\",\"password\":\"Pass123!\"}"
```

---

## Problemas Comunes

### Error: 503 Service Unavailable
**Causa**: App Service iniciando (cold start)
**Solución**: Esperar 30-60 segundos, reintentar

### Error: 500 Internal Server Error
**Causa**: Variables de entorno mal configuradas o BD no conecta
**Solución**:
1. Verificar variables en Configuration
2. Ver logs: `az webapp log tail`

### Health check DOWN
**Causa**: No puede conectar a Azure SQL
**Solución**:
1. Verificar firewall rules (permitir Azure services)
2. Verificar DB_URL, DB_USERNAME, DB_PASSWORD
3. Verificar que BD no está pausada

### JWT Invalid
**Causa**: JWT_SECRET diferente entre local y producción
**Solución**: Usar el mismo JWT_SECRET o generar nuevos tokens

---

## Siguiente Paso

Una vez que **todas** las pruebas pasen ✅:

→ **Configurar APIM** para exponer el microservicio con políticas, rate limiting, etc.
