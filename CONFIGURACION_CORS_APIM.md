# 🔧 Configuración CORS en Azure APIM

## 🚨 Problema Actual

El frontend está recibiendo este error al intentar hacer login con Google OAuth:

```
Cross-Origin Request Blocked: The Same Origin Policy disallows reading the
remote resource at https://apim-quick-speak.azure-api.net/users/api/v1/auth/oauth.
(Reason: CORS header 'Access-Control-Allow-Origin' missing).
```

**Causa:** Azure API Management (APIM) no está configurado para permitir requests CORS desde el dominio de Azure Static Web Apps.

---

## ✅ Solución: Configurar CORS en APIM

### Paso 1: Acceder a Azure API Management

1. Ir a: https://portal.azure.com
2. Buscar: **API Management services**
3. Click en: **apim-quick-speak**
4. En el menú lateral: **APIs** (bajo la sección "APIs")
5. Click en tu API: **user-service-api** (o el nombre que le hayas dado)

---

### Paso 2: Configurar Política CORS Global

Hay dos formas de configurar CORS en APIM:

#### Opción A: CORS a nivel de API (Recomendado)

1. En **APIs**, seleccionar tu API de usuarios
2. Click en la pestaña **"Design"** (arriba)
3. En la sección **"All operations"**, buscar el símbolo **</>** (Policy code editor)
4. Click en **"Inbound processing"** → **"+ Add policy"**
5. Seleccionar **"CORS"**
6. O editar directamente el XML de la política

Agregar/modificar la política CORS en la sección `<inbound>`:

```xml
<policies>
    <inbound>
        <base />
        <cors allow-credentials="true">
            <allowed-origins>
                <origin>https://quickspeak-web-g5f7b5c6b7bearf6.chilecentral-01.azurewebsites.net</origin>
                <origin>http://localhost:3000</origin>
            </allowed-origins>
            <allowed-methods>
                <method>GET</method>
                <method>POST</method>
                <method>PUT</method>
                <method>DELETE</method>
                <method>OPTIONS</method>
                <method>PATCH</method>
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

7. Click **"Save"**

---

#### Opción B: CORS Solo para el Endpoint OAuth (Menos Recomendado)

Si prefieres configurar CORS solo para el endpoint de OAuth:

1. En **APIs**, expandir **user-service-api**
2. Buscar el endpoint: **POST /api/v1/auth/oauth**
3. Click en ese endpoint específico
4. Click en **"Inbound processing"** → **"+ Add policy"** → **"CORS"**
5. Agregar la misma política CORS de arriba

---

### Paso 3: Verificar la Configuración

Después de guardar la política CORS:

1. **Esperar 1-2 minutos** para que se aplique el cambio
2. Abrir tu aplicación en el navegador
3. Intentar login con Google
4. Abrir DevTools (F12) → Network Tab
5. Buscar el request a `/auth/oauth`
6. Verificar que ahora tenga estos headers en la **Response**:
   ```
   Access-Control-Allow-Origin: https://quickspeak-web-g5f7b5c6b7bearf6.chilecentral-01.azurewebsites.net
   Access-Control-Allow-Credentials: true
   Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
   ```

---

## 🔍 Configuración Detallada de la Política CORS

### Explicación de cada elemento:

```xml
<cors allow-credentials="true">
```
- `allow-credentials="true"`: Permite enviar cookies y headers de autenticación


```xml
<allowed-origins>
    <origin>https://quickspeak-web-g5f7b5c6b7bearf6.chilecentral-01.azurewebsites.net</origin>
    <origin>http://localhost:3000</origin>
</allowed-origins>
```
- **Producción:** URL de tu Azure Static Web App
- **Desarrollo:** localhost para testing local
- ⚠️ **IMPORTANTE:** Cambiar la URL si tu Static Web App tiene un dominio diferente


```xml
<allowed-methods>
    <method>GET</method>
    <method>POST</method>
    <method>PUT</method>
    <method>DELETE</method>
    <method>OPTIONS</method>
    <method>PATCH</method>
</allowed-methods>
```
- Métodos HTTP permitidos
- `OPTIONS` es **requerido** para CORS preflight requests


```xml
<allowed-headers>
    <header>*</header>
</allowed-headers>
```
- `*` permite todos los headers
- Necesario para `Ocp-Apim-Subscription-Key`, `Authorization`, `Content-Type`, etc.


```xml
<expose-headers>
    <header>*</header>
</expose-headers>
```
- Headers que el navegador puede leer en la respuesta

---

## ⚠️ Importante: URL del Frontend

**Actualmente:** `https://quickspeak-web-g5f7b5c6b7bearf6.chilecentral-01.azurewebsites.net`

Si tu URL es diferente:
1. Ir a Azure Portal → Static Web Apps → quickspeak
2. Copiar la URL exacta (está en la parte superior)
3. Usar esa URL en `<allowed-origins>`

**Para dominios personalizados:**
Si configuraste un dominio custom (ej: `www.quickspeak.com`):
```xml
<allowed-origins>
    <origin>https://www.quickspeak.com</origin>
    <origin>http://localhost:3000</origin>
</allowed-origins>
```

---

## 🐛 Troubleshooting CORS

### Error: "CORS policy: No 'Access-Control-Allow-Origin' header"

**Causas posibles:**

1. **La política CORS no se guardó correctamente**
   - Volver a APIM y verificar que la política esté presente
   - Re-guardar la política

2. **Cache del navegador**
   - Limpiar cache del navegador (Ctrl + Shift + Delete)
   - O abrir en modo incógnito

3. **URL incorrecta en allowed-origins**
   - Verificar que la URL sea **exactamente** la misma
   - Incluir `https://` (no `http://` para producción)
   - No incluir barra final `/`

### Error: "CORS policy: Credentials flag is 'true'"

Verificar que la política tenga:
```xml
<cors allow-credentials="true">
```

### Error persiste después de configurar CORS

1. **Esperar 2-3 minutos** para que la política se propague
2. **Limpiar cache** del navegador
3. Verificar en Network tab que el request OPTIONS (preflight) tenga status **200 OK**
4. Si sigue fallando, verificar logs de APIM:
   - Azure Portal → APIM → Monitoring → Logs
   - Buscar requests fallidos

---

## 📋 Checklist de Verificación CORS

Después de configurar CORS, verifica:

- [ ] Política CORS agregada a APIM
- [ ] URL del frontend correcta en `<allowed-origins>`
- [ ] `allow-credentials="true"` presente
- [ ] Método `OPTIONS` incluido en `<allowed-methods>`
- [ ] Política guardada correctamente
- [ ] Esperado 2-3 minutos para que se propague
- [ ] Cache del navegador limpiado
- [ ] Request OPTIONS retorna 200 OK en Network tab
- [ ] Headers CORS presentes en la respuesta
- [ ] OAuth login funciona sin errores

---

## 🔄 Cambios Adicionales Realizados en el Código

### Frontend: Endpoint OAuth Corregido

**Antes (❌ incorrecto):**
```typescript
const oauthResponse = await fetch(
  `${APIM_URL}/users/api/v1/auth/oauth/google`,
  { ... }
);
```

**Ahora (✅ correcto):**
```typescript
const oauthResponse = await fetch(
  `${APIM_URL}/users/api/v1/auth/oauth`,
  { ... }
);
```

**Razón:** El backend tiene el endpoint en `/oauth` (no `/oauth/google`). El provider se envía en el body del request.

---

## 📊 Flujo Completo de OAuth (Actualizado)

```
1. Usuario → Click "Login with Google"
   ↓
2. Azure EasyAuth → Autenticación con Google
   ↓
3. Redirect → /auth/callback
   ↓
4. Frontend → fetch('/.auth/me') [Cliente]
   ↓
5. Frontend → Extrae email, firstName, lastName de claims
   ↓
6. Frontend → POST https://apim-quick-speak.azure-api.net/users/api/v1/auth/oauth
              Headers: Ocp-Apim-Subscription-Key
              Body: { email, firstName, lastName, provider: "google", providerId }
   ↓
7. APIM → [✅ CORS CHECK] → Verifica origen permitido
   ↓
8. APIM → Forward request a Backend
   ↓
9. Backend → Crear/actualizar usuario → Generar JWT
   ↓
10. Response → { token, userId }
   ↓
11. Frontend → Guardar en localStorage
   ↓
12. Redirect → /pick_native_language o /dashboard
```

---

## 🎯 Próximos Pasos

Una vez configurado CORS:

1. ✅ Configurar política CORS en APIM (paso manual)
2. ✅ Deploy automático del fix del endpoint (ya pusheado a GitHub)
3. ✅ Esperar que GitHub Actions termine
4. ✅ Probar OAuth login
5. ✅ Verificar que funcione end-to-end

---

## 📚 Referencias

**Documentación oficial:**
- [CORS en Azure APIM](https://learn.microsoft.com/en-us/azure/api-management/api-management-cross-domain-policies#CORS)
- [Políticas en APIM](https://learn.microsoft.com/en-us/azure/api-management/api-management-policies)

**Repositorios:**
- Backend: https://github.com/KthArg/user_service_quickspeak
- Frontend: https://github.com/KthArg/quickspeak_web

---

**Última actualización:** 2025-11-16
**Status:** ⚠️ Requiere configuración manual de CORS en APIM
**Cambios en código:** ✅ Endpoint OAuth corregido y pusheado
