# Instrucciones para Integrar Azure API Management (APIM) con mTLS

## Estado Actual del Proyecto ✅

**Lo que ya está completado:**
- ✅ Certificados generados (en carpeta `certs/`)
- ✅ Keystores copiados a `src/main/resources/`
- ✅ Configuración de mTLS en `application-prod.yml`
- ✅ APIM existente: `https://apim-quick-speak.azure-api.net`

**Lo que falta por hacer:** (ver pasos abajo)
- ⏳ Subir certificado cliente a APIM
- ⏳ Configurar API y backend en APIM
- ⏳ Configurar variables de entorno en Azure App Service
- ⏳ Desplegar y probar

---

## Tabla de Contenidos
1. [Resumen de la Arquitectura](#resumen-de-la-arquitectura)
2. [Información de Certificados Existentes](#informacion-de-certificados-existentes)
3. [Paso 1: Subir Certificado a APIM](#paso-1-subir-certificado-a-apim)
4. [Paso 2: Configurar Backend en APIM](#paso-2-configurar-backend-en-apim)
5. [Paso 3: Crear y Configurar API en APIM](#paso-3-crear-y-configurar-api-en-apim)
6. [Paso 4: Configurar Variables de Entorno en Azure App Service](#paso-4-configurar-variables-de-entorno-en-azure-app-service)
7. [Paso 5: Desplegar el Microservicio](#paso-5-desplegar-el-microservicio)
8. [Paso 6: Probar la Integración](#paso-6-probar-la-integración)
9. [Solución de Problemas](#solucion-de-problemas)

---

## Resumen de la Arquitectura

```
Cliente/Frontend
    ↓ HTTPS
Azure API Management (APIM)
    ↓ mTLS (Mutual TLS)
Azure App Service (User Service Backend)
    ↓
Azure Database (PostgreSQL/SQL)
```

**Beneficios de usar APIM con mTLS:**
- Autenticación mutua (cliente y servidor verifican identidad)
- Cifrado de datos en tránsito
- Control de acceso y políticas centralizadas
- Rate limiting, throttling, y caching
- Documentación automática de APIs
- Monitoreo y analytics

---

## Información de Certificados Existentes

Los certificados ya han sido generados y están ubicados en la carpeta `certs/`:

### Archivos Disponibles

| Archivo | Ubicación | Contraseña | Uso |
|---------|-----------|------------|-----|
| `apim-client-cert.pfx` | `certs/` | `quickspeak-client-pass` | Subir a Azure APIM |
| `server-keystore.p12` | `src/main/resources/` | `quickspeak-keystore-pass` | KeyStore del servidor (Spring Boot) |
| `server-truststore.jks` | `src/main/resources/` | `quickspeak-truststore-pass` | TrustStore del servidor (Spring Boot) |

**Archivo de contraseñas completo**: `certs/passwords.txt`

### Información del APIM Existente

- **Nombre**: `apim-quick-speak`
- **Gateway URL**: `https://apim-quick-speak.azure-api.net`
- **Resource Group**: `quickspeak-resources` (verificar nombre exacto)

### Configuración de Backend Actual

El archivo `application-prod.yml` ya está configurado con mTLS:
- Puerto: 8443
- SSL habilitado
- Client authentication: `need` (requiere certificado de cliente)
- Protocolos: TLSv1.2, TLSv1.3

---

## Paso 1: Subir Certificado a APIM

El certificado de cliente que APIM usará para autenticarse con el backend debe ser subido a Azure.

### Opción A: Usando Azure Portal (Recomendado)

1. Ir a Azure Portal (https://portal.azure.com)
2. Navegar a: **API Management services** → **apim-quick-speak**
3. En el menú izquierdo, seleccionar: **Certificates**
4. Click en **+ Add**
5. Configurar:
   - **Id**: `apim-client-cert`
   - **Certificate**: Subir el archivo `certs/apim-client-cert.pfx`
   - **Password**: `quickspeak-client-pass`
6. Click en **Create**

### Opción B: Usando Azure CLI

```bash
# Variables
RESOURCE_GROUP="quickspeak-resources"  # Verificar nombre exacto
APIM_NAME="apim-quick-speak"

# Subir certificado PFX a APIM desde la carpeta certs
az apim certificate create \
  --resource-group $RESOURCE_GROUP \
  --service-name $APIM_NAME \
  --certificate-id apim-client-cert \
  --data @certs/apim-client-cert.pfx \
  --password "quickspeak-client-pass"
```

### Verificar Certificado Subido

```bash
# Listar certificados en APIM
az apim certificate list \
  --resource-group $RESOURCE_GROUP \
  --service-name $APIM_NAME \
  --query "[].{Id:id, Subject:subject, Thumbprint:thumbprint}" \
  -o table
```

---

## Paso 2: Configurar Backend en APIM

El backend es la referencia al microservicio user-service en Azure App Service.

### Opción A: Usando Azure Portal

1. En APIM → **Backends** → **+ Add**
2. Configurar:
   - **Name**: `user-service-backend`
   - **Backend type**: HTTP(s) endpoint
   - **Runtime URL**: `https://user-service-quickspeak.azurewebsites.net`
   - **Protocol**: HTTPS
3. En la pestaña **Security**:
   - **Client certificate**: Seleccionar `apim-client-cert`
4. Click **Create**

### Opción B: Usando Azure CLI

```bash
# Variables
RESOURCE_GROUP="quickspeak-resources"
APIM_NAME="apim-quick-speak"
BACKEND_URL="https://user-service-quickspeak.azurewebsites.net"

# Crear backend
az apim backend create \
  --resource-group $RESOURCE_GROUP \
  --service-name $APIM_NAME \
  --backend-id user-service-backend \
  --url $BACKEND_URL \
  --protocol https \
  --description "User Service Backend with mTLS"
```

**Nota**: La configuración del certificado de cliente en el backend se hace mediante políticas (ver Paso 3).

---

## Paso 3: Crear y Configurar API en APIM

### 3.1 Crear la API

#### Opción A: Desde Azure Portal (Recomendado)

1. Ir a Azure Portal → **apim-quick-speak** → **APIs**
2. Click en **+ Add API** → **Blank API**
3. Configurar:
   - **Display name**: `User Service API`
   - **Name**: `user-service-api`
   - **Web service URL**: (dejar vacío, usaremos backend)
   - **API URL suffix**: `user`
4. Click **Create**

#### Opción B: Usando Azure CLI

```bash
RESOURCE_GROUP="quickspeak-resources"
APIM_NAME="apim-quick-speak"

az apim api create \
  --resource-group $RESOURCE_GROUP \
  --service-name $APIM_NAME \
  --api-id user-service-api \
  --path user \
  --display-name "User Service API" \
  --protocols https
```

### 3.2 Configurar Operaciones de la API

Puedes agregar las operaciones una por una en el portal o usar OpenAPI.

#### Opción A: Importar OpenAPI (Más Rápido)

Si tienes un archivo OpenAPI (`openapi-user-service.yaml` en la raíz del proyecto):

1. En APIM → APIs → **user-service-api**
2. Click en **...** → **Import**
3. Seleccionar **OpenAPI**
4. Subir el archivo `openapi-user-service.yaml`

#### Opción B: Agregar Operaciones Manualmente

En Azure Portal → APIM → APIs → user-service-api → **+ Add operation**:

**Operaciones principales:**

| Display Name | Method | URL Template |
|--------------|--------|--------------|
| Register User | POST | /api/v1/auth/register |
| Login User | POST | /api/v1/auth/login |
| Get All Languages | GET | /api/v1/languages |
| Get Starting Languages | GET | /api/v1/languages/starting |
| Get User Profile | GET | /api/v1/users/{id}/profile |
| Update User | PUT | /api/v1/users/{id} |
| Get User Languages | GET | /api/v1/users/{userId}/languages |
| Add User Language | POST | /api/v1/users/{userId}/languages |

### 3.3 Configurar Política de API con mTLS

Esta es la configuración más importante para habilitar mTLS.

1. En APIM → APIs → **user-service-api**
2. Click en **All operations**
3. En la sección **Inbound processing**, click en **</>** (Code editor)
4. Reemplazar todo el contenido con:

```xml
<policies>
    <inbound>
        <base />
        <!-- Usar el backend configurado -->
        <set-backend-service backend-id="user-service-backend" />
        <!-- Autenticación con certificado de cliente (mTLS) -->
        <authentication-certificate certificate-id="apim-client-cert" />
        <!-- CORS (opcional, ajustar según necesidad) -->
        <cors allow-credentials="true">
            <allowed-origins>
                <origin>http://localhost:3000</origin>
                <origin>https://tu-frontend-produccion.com</origin>
            </allowed-origins>
            <allowed-methods>
                <method>GET</method>
                <method>POST</method>
                <method>PUT</method>
                <method>DELETE</method>
                <method>OPTIONS</method>
            </allowed-methods>
            <allowed-headers>
                <header>*</header>
            </allowed-headers>
        </cors>
    </inbound>
    <backend>
        <base />
    </backend>
    <outbound>
        <base />
    </outbound>
    <on-error>
        <base />
    </on-error>
</policies>
```

5. Click **Save**

#### Usando Azure CLI (Alternativa)

Crear archivo `policies/api-policy.xml` con el contenido anterior, luego:

```bash
az apim api policy create \
  --resource-group $RESOURCE_GROUP \
  --service-name $APIM_NAME \
  --api-id user-service-api \
  --xml-file policies/api-policy.xml
```

---

## Paso 4: Configurar Variables de Entorno en Azure App Service

El App Service necesita las contraseñas de los keystores y otras configuraciones.

### Variables Requeridas

| Variable | Valor | Propósito |
|----------|-------|-----------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Activar perfil de producción |
| `PORT` | `8443` | Puerto HTTPS con mTLS |
| `WEBSITES_PORT` | `8443` | Puerto que Azure debe exponer |
| `SSL_KEYSTORE_PASSWORD` | `quickspeak-keystore-pass` | Contraseña del KeyStore |
| `SSL_TRUSTSTORE_PASSWORD` | `quickspeak-truststore-pass` | Contraseña del TrustStore |
| `JWT_SECRET` | (generar uno seguro) | Secret para firmar tokens JWT |
| `JWT_EXPIRATION` | `86400000` | Expiración del JWT (24h) |
| `DB_URL` | (tu URL de BD) | URL de base de datos |
| `DB_USERNAME` | (tu usuario) | Usuario de base de datos |
| `DB_PASSWORD` | (tu password) | Password de base de datos |
| `DB_DRIVER` | `org.postgresql.Driver` | Driver de PostgreSQL |
| `HIBERNATE_DIALECT` | `org.hibernate.dialect.PostgreSQLDialect` | Dialecto de Hibernate |

### Configurar usando Azure Portal

1. Ir a Azure Portal → **App Service** → **user-service-quickspeak**
2. En el menú izquierdo: **Configuration** → **Application settings**
3. Click **+ New application setting** para cada variable de la tabla
4. Click **Save** y **Continue**

### Configurar usando Azure CLI

```bash
# Variables
RESOURCE_GROUP="quickspeak-resources"
APP_SERVICE_NAME="user-service-quickspeak"

# Generar JWT secret seguro (ejecutar y copiar resultado)
openssl rand -base64 64 | tr -d '\n'

# Configurar todas las variables de una vez
az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_SERVICE_NAME \
  --settings \
    SPRING_PROFILES_ACTIVE="prod" \
    PORT="8443" \
    WEBSITES_PORT="8443" \
    SSL_KEYSTORE_PASSWORD="quickspeak-keystore-pass" \
    SSL_TRUSTSTORE_PASSWORD="quickspeak-truststore-pass" \
    JWT_SECRET="<pegar-secret-generado-arriba>" \
    JWT_EXPIRATION="86400000" \
    DB_URL="<tu-database-url>" \
    DB_USERNAME="<tu-usuario>" \
    DB_PASSWORD="<tu-password>" \
    DB_DRIVER="org.postgresql.Driver" \
    HIBERNATE_DIALECT="org.hibernate.dialect.PostgreSQLDialect"
```

**Importante**: Reemplaza `<tu-database-url>`, `<tu-usuario>`, y `<tu-password>` con los valores reales de tu base de datos Azure.

---

## Paso 5: Desplegar el Microservicio

Antes de desplegar, asegúrate de que los keystores estén incluidos en el JAR.

### 5.1 Verificar que los Keystores estén en el Proyecto

```bash
# Verificar que los archivos existan en src/main/resources
ls -la src/main/resources/*.p12 src/main/resources/*.jks
```

Deberías ver:
- `server-keystore.p12`
- `server-truststore.jks`

### 5.2 Compilar el Proyecto

```bash
# Limpiar y compilar
mvn clean package -DskipTests

# Verificar que los keystores estén en el JAR
jar tf target/user-service-*.jar | grep -E "keystore|truststore"
```

### 5.3 Desplegar a Azure

#### Opción A: Usando Maven Plugin

Si tienes configurado el plugin de Azure en `pom.xml`:

```bash
mvn azure-webapp:deploy
```

#### Opción B: Usando Azure CLI

```bash
RESOURCE_GROUP="quickspeak-resources"
APP_SERVICE_NAME="user-service-quickspeak"

# Desplegar JAR
az webapp deploy \
  --resource-group $RESOURCE_GROUP \
  --name $APP_SERVICE_NAME \
  --src-path target/user-service-0.0.1-SNAPSHOT.jar \
  --type jar
```

#### Opción C: Desde Azure Portal

1. Ir a **App Service** → **user-service-quickspeak**
2. En el menú: **Deployment Center**
3. Subir el archivo JAR desde `target/user-service-*.jar`

### 5.4 Verificar Despliegue

```bash
# Ver logs en tiempo real
az webapp log tail \
  --resource-group $RESOURCE_GROUP \
  --name $APP_SERVICE_NAME

# Verificar que el servicio esté corriendo
az webapp show \
  --resource-group $RESOURCE_GROUP \
  --name $APP_SERVICE_NAME \
  --query "state" \
  -o tsv
```

---

## Paso 6: Probar la Integración

### 6.1 Información de Conexión

- **APIM Gateway URL**: `https://apim-quick-speak.azure-api.net`
- **API Path**: `/user`
- **Full Base URL**: `https://apim-quick-speak.azure-api.net/user`

### 6.2 Obtener Subscription Key

#### Desde Azure Portal
1. Ir a **apim-quick-speak** → **Subscriptions**
2. Seleccionar una subscription (ej: "Built-in all-access subscription")
3. Click en **Show/hide keys**
4. Copiar una de las keys

c081b2299247481f827d5b08211624f2

#### Usando Azure CLI

```bash
az apim subscription list \
  --resource-group quickspeak-resources \
  --service-name apim-quick-speak \
  --query "[0].{name:name, primaryKey:primaryKey}" \
  -o table
```

### 6.3 Probar Endpoints Públicos

```bash
# Variables
APIM_URL="https://apim-quick-speak.azure-api.net/user"
SUBSCRIPTION_KEY="<tu-subscription-key>"

# Test 1: Health check
curl "$APIM_URL/actuator/health" \
  -H "Ocp-Apim-Subscription-Key: $SUBSCRIPTION_KEY"

# Test 2: Obtener idiomas
curl "$APIM_URL/api/v1/languages" \
  -H "Ocp-Apim-Subscription-Key: $SUBSCRIPTION_KEY"

# Test 3: Obtener idiomas de inicio
curl "$APIM_URL/api/v1/languages/starting" \
  -H "Ocp-Apim-Subscription-Key: $SUBSCRIPTION_KEY"
```

### 6.4 Probar Autenticación

```bash
# Test 4: Registro de usuario
curl -X POST "$APIM_URL/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -H "Ocp-Apim-Subscription-Key: $SUBSCRIPTION_KEY" \
  -d '{
    "email": "test@quickspeak.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'

# Test 5: Login (guardar el token de la respuesta)
curl -X POST "$APIM_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -H "Ocp-Apim-Subscription-Key: $SUBSCRIPTION_KEY" \
  -d '{
    "email": "test@quickspeak.com",
    "password": "password123"
  }'
```

### 6.5 Probar Endpoints Protegidos con JWT

```bash
# Copiar el token de la respuesta del login
TOKEN="eyJhbGciOiJIUzM4NCJ9..."

# Test 6: Obtener perfil de usuario
curl "$APIM_URL/api/v1/users/1/profile" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Ocp-Apim-Subscription-Key: $SUBSCRIPTION_KEY"

# Test 7: Actualizar usuario
curl -X PUT "$APIM_URL/api/v1/users/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Ocp-Apim-Subscription-Key: $SUBSCRIPTION_KEY" \
  -d '{
    "firstName": "Test Updated",
    "lastName": "User Updated",
    "email": "test@quickspeak.com"
  }'
```

### 6.6 Verificar mTLS en los Logs

Verifica que el mTLS está funcionando revisando los logs del App Service:

```bash
az webapp log tail \
  --resource-group quickspeak-resources \
  --name user-service-quickspeak
```

Busca líneas que indiquen:
- `SSL handshake successful`
- `Client certificate verified`
- Peticiones llegando correctamente

---

## Solución de Problemas

### Problema 1: Error 502 Bad Gateway desde APIM

**Causas posibles**:
- El App Service no está corriendo
- El certificado de cliente no está configurado en la política
- El backend no está accesible

**Soluciones**:
1. Verificar estado del App Service:
   ```bash
   az webapp show --resource-group quickspeak-resources \
     --name user-service-quickspeak --query "state"
   ```

2. Verificar logs del App Service:
   ```bash
   az webapp log tail --resource-group quickspeak-resources \
     --name user-service-quickspeak
   ```

3. Verificar que la política de APIM incluya `<authentication-certificate certificate-id="apim-client-cert" />`

### Problema 2: Error SSL Handshake Failed

**Causa**: Problema con certificados SSL/TLS.

**Soluciones**:
1. Verificar que las contraseñas en App Settings sean correctas:
   - `SSL_KEYSTORE_PASSWORD=quickspeak-keystore-pass`
   - `SSL_TRUSTSTORE_PASSWORD=quickspeak-truststore-pass`

2. Verificar que los keystores estén en el JAR:
   ```bash
   jar tf target/user-service-*.jar | grep -E "keystore|truststore"
   ```

3. Revisar logs del App Service para errores SSL específicos

### Problema 3: Error 401 Unauthorized

**Causa**: Problema con Subscription Key o JWT.

**Soluciones**:
1. Verificar que incluyes el header `Ocp-Apim-Subscription-Key`
2. Verificar que el JWT sea válido y no haya expirado
3. Verificar que el `JWT_SECRET` en App Service coincida con el usado para firmar tokens

### Problema 4: Error 404 Not Found

**Causa**: La ruta de la API no coincide.

**Soluciones**:
1. Verificar que la URL sea: `https://apim-quick-speak.azure-api.net/user/api/v1/...`
2. Verificar que las operaciones estén creadas en APIM
3. Verificar la política `<set-backend-service backend-id="user-service-backend" />`

### Problema 5: Los Keystores No Se Encuentran

**Causa**: Los archivos `.p12` y `.jks` no están en el JAR.

**Soluciones**:
1. Verificar que estén en `src/main/resources/`:
   ```bash
   ls -la src/main/resources/*.p12 src/main/resources/*.jks
   ```

2. Recompilar y verificar:
   ```bash
   mvn clean package -DskipTests
   jar tf target/user-service-*.jar | grep -E "keystore|truststore"
   ```

3. Si no aparecen, copiarlos nuevamente:
   ```bash
   cp certs/server-keystore.p12 src/main/resources/
   cp certs/server-truststore.jks src/main/resources/
   ```

### Problema 6: CORS Errors en el Frontend

**Causa**: Política de CORS no configurada correctamente.

**Solución**:
1. Verificar que la política de APIM incluya la sección `<cors>` con el origen del frontend
2. Agregar el dominio del frontend a `<allowed-origins>`

---

## Arquitectura de Seguridad Final

```
┌─────────────────────────────────────────────────────────────┐
│                         Cliente                              │
│                    (Frontend / Postman)                      │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTPS + Subscription Key
                             ▼
┌─────────────────────────────────────────────────────────────┐
│              Azure API Management (APIM)                     │
│  - Rate Limiting                                            │
│  - Caching                                                  │
│  - Authentication (Subscription Key)                        │
│  - mTLS Client Certificate                                 │
└────────────────────────────┬────────────────────────────────┘
                             │ mTLS (Mutual TLS)
                             │ - APIM presenta certificado de cliente
                             │ - Backend verifica certificado de APIM
                             ▼
┌─────────────────────────────────────────────────────────────┐
│           Azure App Service (User Service)                   │
│  - Verifica certificado de cliente (APIM)                   │
│  - TLS Server Certificate                                   │
│  - JWT Authentication                                       │
└────────────────────────────┬────────────────────────────────┘
                             │ Encrypted Connection
                             ▼
┌─────────────────────────────────────────────────────────────┐
│              Azure Database (PostgreSQL/SQL)                 │
└─────────────────────────────────────────────────────────────┘
```

---

## Resumen de URLs y Endpoints

| Componente | URL | Notas |
|------------|-----|-------|
| APIM Gateway | https://apim-quick-speak.azure-api.net | Punto de entrada público |
| API Base URL | https://apim-quick-speak.azure-api.net/user | Usar esta URL para todas las peticiones |
| Backend Directo | https://user-service-quickspeak.azurewebsites.net | No usar directamente (protegido con mTLS) |
| Health Check | https://apim-quick-speak.azure-api.net/user/actuator/health | Verificación de salud |

---

## Checklist de Integración

Marca cada paso conforme lo completes:

- [ ] **Paso 1**: Subir certificado `apim-client-cert.pfx` a APIM
- [ ] **Paso 2**: Crear backend `user-service-backend` en APIM
- [ ] **Paso 3**: Crear API `user-service-api` con operaciones
- [ ] **Paso 3**: Configurar política con mTLS y CORS
- [ ] **Paso 4**: Configurar variables de entorno en App Service
- [ ] **Paso 5**: Compilar y desplegar el microservicio
- [ ] **Paso 6**: Probar endpoints públicos
- [ ] **Paso 6**: Probar autenticación y JWT
- [ ] **Paso 6**: Verificar logs de mTLS

---

## Resumen Ejecutivo

### ✅ Ya Completado

- Certificados generados en `certs/`
- Keystores copiados a `src/main/resources/`
- Configuración de mTLS en `application-prod.yml`
- APIM existente: `apim-quick-speak`

### 🔄 Por Hacer (en orden)

1. **Subir certificado a APIM** (5 min)
   - Archivo: `certs/apim-client-cert.pfx`
   - Password: `quickspeak-client-pass`

2. **Configurar APIM** (15-20 min)
   - Crear backend
   - Crear API y operaciones
   - Configurar política con mTLS

3. **Configurar App Service** (5 min)
   - Variables de entorno
   - Contraseñas de keystores

4. **Desplegar** (10 min)
   - Compilar JAR
   - Subir a Azure

5. **Probar** (10 min)
   - Obtener subscription key
   - Probar endpoints

**Tiempo total estimado**: 45-50 minutos

---

## Archivos Importantes del Proyecto

- `certs/apim-client-cert.pfx` - Certificado para APIM
- `certs/passwords.txt` - Contraseñas de todos los certificados
- `src/main/resources/server-keystore.p12` - KeyStore del servidor
- `src/main/resources/server-truststore.jks` - TrustStore del servidor
- `src/main/resources/application-prod.yml` - Configuración de producción

---

## Mejoras Futuras Recomendadas

1. **Seguridad**:
   - Migrar secretos a Azure Key Vault
   - Implementar Azure AD OAuth 2.0
   - Habilitar Application Insights

2. **Optimización**:
   - Configurar caché en APIM
   - Implementar rate limiting
   - Auto-scaling en App Service

3. **Red**:
   - Restricciones de IP en App Service (solo APIM)
   - Azure Private Link para conexión privada
   - VNet integration

4. **CI/CD**:
   - GitHub Actions para despliegue automático
   - Ambientes separados (dev, staging, prod)

---

**Última actualización**: 14 de noviembre de 2025
**Versión**: 2.0 (Adaptada a certificados existentes)
