# Actualizar API en Azure APIM con nuevo endpoint

Este documento explica cómo agregar el nuevo endpoint `/api/v1/languages/select-native` a Azure API Management.

## Problema resuelto

El frontend llamaba a `/api/languages/select-native` pero el endpoint no existía, causando error **405 Method Not Allowed**.

## Cambios realizados

✅ **Backend**: Agregado endpoint GET `/api/v1/languages/select-native` en `LanguageController.java`
✅ **Frontend**: Agregado método GET en route handler `/api/languages/select-native/route.ts`
✅ **OpenAPI**: Documentado nuevo endpoint en `openapi-user-service.yaml`

## Opciones para actualizar APIM

Tienes **3 opciones** para actualizar APIM con el nuevo endpoint:

---

## 🔄 Opción 1: Re-importar OpenAPI completo (RECOMENDADO)

Esta es la forma más rápida y garantiza que APIM esté sincronizado con la especificación OpenAPI.

### Pasos:

1. **Ir a Azure Portal**: https://portal.azure.com
2. **Navegar a**: API Management services → **apim-quick-speak**
3. **En el menú izquierdo**: APIs → Seleccionar tu API **User Service API**
4. **Click en los tres puntos** (...) al lado del nombre de la API
5. **Seleccionar**: "Import" → "OpenAPI"
6. **Configurar**:
   - **OpenAPI specification**: Seleccionar "From URL" o "Upload file"
   - **URL**: `https://raw.githubusercontent.com/KthArg/user_service_quickspeak/main/openapi-user-service.yaml`
     - O subir el archivo `openapi-user-service.yaml` localmente
   - **Import mode**: Seleccionar **"Update"** (no "Create new")
7. **Click**: "Import"
8. **Verificar**: El nuevo endpoint aparece en la lista de operaciones

### ✅ Ventajas:
- Actualiza todos los endpoints de una vez
- Mantiene APIM sincronizado con OpenAPI
- Incluye metadatos, descripciones y ejemplos

---

## ➕ Opción 2: Agregar endpoint manualmente

Si prefieres agregar solo este endpoint específico sin re-importar todo.

### Pasos:

1. **Ir a Azure Portal**: https://portal.azure.com
2. **Navegar a**: API Management services → **apim-quick-speak**
3. **En el menú izquierdo**: APIs → **User Service API**
4. **Click en**: "+ Add operation"
5. **Configurar el endpoint**:
   ```
   Display name: Get Languages for Native Selection
   Name: get-languages-for-native-selection
   URL: GET /api/v1/languages/select-native
   Description: Obtener idiomas disponibles para seleccionar como idioma nativo durante el registro
   ```
6. **En la pestaña "Responses"**:
   - **Add response**: 200 OK
   - **Representations**:
     - Content type: `application/json`
     - Sample:
       ```json
       [
         {
           "id": 1,
           "name": "English",
           "code": "en",
           "nativeName": "English",
           "flagEmoji": "🇺🇸",
           "isStartingLanguage": true
         }
       ]
       ```
7. **Click**: "Save"

### ⚙️ Configurar políticas (si es necesario):

Si tu API tiene políticas CORS u otras configuraciones, asegúrate de que se apliquen también a este endpoint.

1. **Click en el nuevo endpoint**: `GET /api/v1/languages/select-native`
2. **En "Inbound processing"**: Click en **"</>""** (Code view)
3. **Verificar que incluya CORS**:
   ```xml
   <policies>
       <inbound>
           <base />
           <cors allow-credentials="false">
               <allowed-origins>
                   <origin>https://quickspeak-web-*.azurewebsites.net</origin>
                   <origin>http://localhost:3000</origin>
               </allowed-origins>
               <allowed-methods>
                   <method>GET</method>
                   <method>POST</method>
                   <method>PUT</method>
                   <method>PATCH</method>
                   <method>DELETE</method>
                   <method>OPTIONS</method>
               </allowed-methods>
               <allowed-headers>
                   <header>*</header>
               </allowed-headers>
               <expose-headers>
                   <header>*</header>
               </expose-headers>
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
4. **Click**: "Save"

---

## 🔍 Opción 3: Usar Azure CLI

Para automatizar la actualización usando línea de comandos:

```bash
# Re-importar OpenAPI desde URL
az apim api import \
  --resource-group <tu-resource-group> \
  --service-name apim-quick-speak \
  --api-id user-service-api \
  --specification-format OpenApi \
  --specification-url https://raw.githubusercontent.com/KthArg/user_service_quickspeak/main/openapi-user-service.yaml \
  --path /users
```

---

## ✅ Verificación

Después de actualizar APIM, verifica que el endpoint funcione:

### Desde Azure Portal:
1. **APIs** → **User Service API** → **GET languages/select-native**
2. **Test tab** → **Send**
3. Deberías ver respuesta **200 OK** con lista de idiomas

### Desde el navegador o Postman:
```bash
GET https://apim-quick-speak.azure-api.net/users/api/v1/languages/select-native
```

### Desde el frontend:
- Navega a la página de registro
- Ir a "Pick Native Language"
- Deberías ver la lista de idiomas sin error 405

---

## 🚨 Troubleshooting

### Error 404 después de importar:
- Verifica que el path incluya el prefijo correcto (ej: `/users` en APIM)
- El endpoint completo sería: `https://apim-quick-speak.azure-api.net/users/api/v1/languages/select-native`

### Error CORS:
- Verifica que las políticas CORS estén configuradas
- Asegúrate de que el origen del frontend esté en la lista de allowed-origins

### Endpoint no aparece en la lista:
- Refresca la página del Azure Portal
- Verifica que el import mode fue "Update" y no "Create new"

---

## 📚 Referencias

- [Azure APIM - Import API](https://learn.microsoft.com/en-us/azure/api-management/import-api-from-oas)
- [Azure APIM - Add operations](https://learn.microsoft.com/en-us/azure/api-management/add-api-manually)
- [OpenAPI Specification](https://swagger.io/specification/)
