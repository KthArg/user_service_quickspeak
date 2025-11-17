# Guía de Actualización de APIM - Nuevos Endpoints

## Resumen de Cambios

Se agregaron dos nuevos endpoints PATCH para manejar cambios de email y contraseña de forma segura:

1. **PATCH /users/api/v1/users/{id}/password** - Cambiar contraseña
2. **PATCH /users/api/v1/users/{id}/email** - Cambiar email

---

## Opción 1: Actualización Automática con Script

### Requisitos Previos
- Azure CLI instalado
- Sesión iniciada con `az login`

### Ejecutar Script
```powershell
cd "C:\Users\Kenneth\Documents\TEC\diseño\proyecto\user_service_quickspeak"
.\update-apim-endpoints.ps1
```

---

## Opción 2: Actualización Manual en Azure Portal

### Paso 1: Acceder a APIM
1. Ir a [Azure Portal](https://portal.azure.com)
2. Navegar a **Resource Groups** → `rg-quick-speak`
3. Seleccionar **API Management** → `apim-quick-speak`
4. Ir a **APIs** → Seleccionar tu API de usuarios

### Paso 2: Agregar Endpoint de Cambio de Contraseña

**Configuración General:**
- **Display name:** Change User Password
- **Name:** change-user-password
- **Method:** PATCH
- **URL template:** `/users/api/v1/users/{id}/password`

**Template Parameters:**
- **Name:** id
- **Type:** string
- **Required:** Yes
- **Description:** User ID

**Request Body (JSON Schema):**
```json
{
  "type": "object",
  "required": ["currentPassword", "newPassword"],
  "properties": {
    "currentPassword": {
      "type": "string",
      "description": "Current password for verification"
    },
    "newPassword": {
      "type": "string",
      "minLength": 8,
      "description": "New password (minimum 8 characters)"
    }
  }
}
```

**Request Example:**
```json
{
  "currentPassword": "oldPassword123",
  "newPassword": "newSecurePassword456"
}
```

**Responses:**
- **200 OK:** Password changed successfully (UserResponse)
- **400 Bad Request:** Invalid request or incorrect current password
- **404 Not Found:** User not found

### Paso 3: Agregar Endpoint de Cambio de Email

**Configuración General:**
- **Display name:** Change User Email
- **Name:** change-user-email
- **Method:** PATCH
- **URL template:** `/users/api/v1/users/{id}/email`

**Template Parameters:**
- **Name:** id
- **Type:** string
- **Required:** Yes
- **Description:** User ID

**Request Body (JSON Schema):**
```json
{
  "type": "object",
  "required": ["newEmail"],
  "properties": {
    "newEmail": {
      "type": "string",
      "format": "email",
      "description": "New email address"
    }
  }
}
```

**Request Example:**
```json
{
  "newEmail": "newemail@example.com"
}
```

**Responses:**
- **200 OK:** Email changed successfully (UserResponse)
- **400 Bad Request:** Invalid email format or email already in use
- **404 Not Found:** User not found

---

## Opción 3: Actualización con Azure CLI (Comandos Directos)

```bash
# Configurar variables
RESOURCE_GROUP="rg-quick-speak"
APIM_NAME="apim-quick-speak"
API_ID="users-api"

# Agregar endpoint de cambio de contraseña
az apim api operation create \
  --resource-group $RESOURCE_GROUP \
  --service-name $APIM_NAME \
  --api-id $API_ID \
  --url-template "/users/api/v1/users/{id}/password" \
  --method "PATCH" \
  --display-name "Change User Password" \
  --description "Change user password with current password validation"

# Agregar endpoint de cambio de email
az apim api operation create \
  --resource-group $RESOURCE_GROUP \
  --service-name $APIM_NAME \
  --api-id $API_ID \
  --url-template "/users/api/v1/users/{id}/email" \
  --method "PATCH" \
  --display-name "Change User Email" \
  --description "Change user email with uniqueness validation"
```

---

## Políticas de CORS (si es necesario)

Si necesitas agregar políticas de CORS específicas para estos endpoints:

```xml
<policies>
    <inbound>
        <cors allow-credentials="true">
            <allowed-origins>
                <origin>https://quickspeak.azurestaticapps.net</origin>
                <origin>http://localhost:3000</origin>
            </allowed-origins>
            <allowed-methods>
                <method>GET</method>
                <method>POST</method>
                <method>PUT</method>
                <method>PATCH</method>
                <method>DELETE</method>
            </allowed-methods>
            <allowed-headers>
                <header>*</header>
            </allowed-headers>
        </cors>
        <base />
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

---

## Validación de Endpoints

Después de actualizar APIM, valida que los endpoints funcionan:

### Test 1: Cambiar Contraseña
```bash
curl -X PATCH "https://apim-quick-speak.azure-api.net/users/api/v1/users/5/password" \
  -H "Ocp-Apim-Subscription-Key: YOUR_API_KEY" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "oldPassword123",
    "newPassword": "newPassword456"
  }'
```

### Test 2: Cambiar Email
```bash
curl -X PATCH "https://apim-quick-speak.azure-api.net/users/api/v1/users/5/email" \
  -H "Ocp-Apim-Subscription-Key: YOUR_API_KEY" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "newEmail": "newemail@test.com"
  }'
```

---

## Troubleshooting

### Error: "Operation already exists"
Si los endpoints ya existen, primero elimínalos:
```bash
az apim api operation delete \
  --resource-group rg-quick-speak \
  --service-name apim-quick-speak \
  --api-id users-api \
  --operation-id change-user-password

az apim api operation delete \
  --resource-group rg-quick-speak \
  --service-name apim-quick-speak \
  --api-id users-api \
  --operation-id change-user-email
```

### Error: "API not found"
Verifica el nombre correcto de tu API:
```bash
az apim api list \
  --resource-group rg-quick-speak \
  --service-name apim-quick-speak \
  --query "[].{Name:name, DisplayName:displayName}"
```

---

## Notas Importantes

1. **Autenticación:** Ambos endpoints requieren JWT token válido en el header `Authorization`
2. **Subscription Key:** Requieren la API key de APIM en el header `Ocp-Apim-Subscription-Key`
3. **Validaciones Backend:**
   - Cambio de contraseña valida que la contraseña actual sea correcta
   - Cambio de email valida que el nuevo email no esté en uso
4. **Frontend:** El frontend ya está configurado para usar estos endpoints automáticamente

---

## URLs de Referencia

- **Azure Portal:** https://portal.azure.com
- **APIM Endpoint:** https://apim-quick-speak.azure-api.net
- **Backend Service:** https://user-service-quickspeak.azurewebsites.net
