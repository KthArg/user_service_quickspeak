# 🔧 Arreglo de Errores OAuth/EasyAuth - QuickSpeak

## 📝 Resumen de Cambios Realizados

### Problema Identificado

Los errores que estabas viendo:
```
Error: 804C3B5927730000:error:0A00010B:SSL routines:ssl3_get_record:wrong version
500 Internal Server Error en /api/auth/easyauth-info
"Failed to get authentication information from EasyAuth"
```

**Causa raíz:** El endpoint `/api/auth/easyauth-info` estaba intentando hacer un `fetch` a `/.auth/me` desde el servidor de Next.js, lo cual causaba problemas de SSL porque el servidor intentaba conectarse a sí mismo vía HTTPS.

### Solución Implementada

✅ **Cambios en el Frontend** (ya pusheados a `master`):

1. **Eliminado `/api/auth/easyauth-info`**: Este endpoint causaba los errores SSL
2. **Modificado `/auth/callback`**: Ahora llama directamente a `/.auth/me` desde el cliente (navegador)
3. **Limpieza de documentación**: Eliminados archivos de documentación antiguos

✅ **Cambios en el Backend** (ya pusheados a `main`):

1. **Limpieza de archivos**: Eliminados archivos de documentación temporales
2. **Conservado DEPLOYMENT_FINAL.md**: Documento con instrucciones esenciales

---

## 🚀 Pasos que Debes Seguir (Manual)

### Paso 1: Verificar Variables de Entorno en Azure Static Web Apps

**CRÍTICO:** Asegúrate de que estas variables estén configuradas:

1. Ir a: https://portal.azure.com
2. Navegar a: **Static Web Apps** → **quickspeak** → **Configuration** → **Application settings**
3. Verificar que existan estas 2 variables:

| Name                     | Value                                  |
|--------------------------|----------------------------------------|
| `NEXT_PUBLIC_API_BASE_URL` | `https://apim-quick-speak.azure-api.net` |
| `NEXT_PUBLIC_API_KEY`      | `c081b2299247481f827d5b08211624f2`       |

Si no están, agregarlas:
- Click **"+ Add"** (dos veces)
- Ingresar Name y Value
- Click **"Save"**
- Esperar 1-2 minutos que se reinicie la app

---

### Paso 2: Esperar el Deployment Automático

GitHub Actions deployará automáticamente los cambios:

1. Ir a: https://github.com/KthArg/quickspeak_web/actions
2. Verificar que el workflow **"Azure Static Web Apps CI/CD"** esté corriendo
3. Esperar a que termine (2-5 minutos)
4. Debe mostrar ✅ verde cuando termine exitosamente

Si falla:
- Revisar los logs del workflow
- Verificar que las variables de entorno estén configuradas
- Re-deployar manualmente si es necesario

---

### Paso 3: Verificar que OAuth Funcione

Una vez deployado:

#### 3.1 Abrir la Aplicación

```
https://quickspeak-web-g5f7b5c6b7bearf6.chilecentral-01.azurewebsites.net
```

#### 3.2 Probar Login con Google

1. Ir a la página de login
2. Click en **"Login with Google"**
3. Autorizar con tu cuenta de Google
4. Deberías ser redirigido a `/auth/callback`
5. Deberías ver: **"Obtaining user information..."** → **"Creating user session..."** → **"Redirecting..."**
6. Finalmente: Redirección exitosa al dashboard o `/pick_native_language`

#### 3.3 Verificar en DevTools (F12)

**Console:**
```javascript
localStorage.getItem('authToken')  // Debe mostrar JWT token
localStorage.getItem('userId')     // Debe mostrar ID numérico
```

**Network Tab:**
1. Filtrar por: `auth`
2. Buscar el request a: `/.auth/me`
   - Status: **200 OK**
   - Response: Array con user claims de Google
3. Buscar el request a: `https://apim-quick-speak.azure-api.net/users/api/v1/auth/oauth/google`
   - Status: **200 OK** o **201 Created**
   - Response: `{ token: "...", userId: ... }`
   - Headers: Debe incluir `Ocp-Apim-Subscription-Key`

---

## 🔍 Cómo Funciona Ahora (Flujo Técnico)

### Antes (❌ Fallaba)

```
Usuario → Google OAuth → EasyAuth → /.auth/callback
                                        ↓
                    [Frontend] /auth/callback page
                                        ↓
                    fetch('/api/auth/easyauth-info') [Server-side]
                                        ↓
                    fetch('/.auth/me') [Server → Server] ❌ SSL ERROR
```

### Ahora (✅ Funciona)

```
Usuario → Google OAuth → EasyAuth → /.auth/callback
                                        ↓
                    [Frontend] /auth/callback page
                                        ↓
                    fetch('/.auth/me') [Client-side] ✅ OK
                                        ↓
                    Extrae claims directamente
                                        ↓
                    POST https://apim-quick-speak.azure-api.net/users/api/v1/auth/oauth/google
                                        ↓
                    Guarda token + userId en localStorage
                                        ↓
                    Redirect a /pick_native_language o /dashboard
```

---

## 📊 Cambios en el Código

### Frontend: `src/app/auth/callback/page.tsx`

**Cambio principal:**
```typescript
// ANTES (❌ causaba SSL errors)
const easyAuthResponse = await fetch('/api/auth/easyauth-info');

// AHORA (✅ funciona)
const easyAuthResponse = await fetch('/.auth/me');
const easyAuthArray = await easyAuthResponse.json();
const authData = easyAuthArray[0];
const claims = authData.user_claims || [];

// Extraer claims directamente
const getClaim = (type: string) => {
  const claim = claims.find((c: any) => c.typ === type);
  return claim ? claim.val : null;
};

const email = getClaim('http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress');
```

### Frontend: Endpoint Eliminado

**Archivo eliminado:** `src/app/api/auth/easyauth-info/route.ts`

Ya no es necesario porque ahora llamamos directamente a `/.auth/me` desde el cliente.

---

## 🐛 Troubleshooting

### Error: "Failed to get authentication information from EasyAuth"

**Posibles causas:**

1. **EasyAuth no está configurado en Azure Static Web Apps**
   - Ir a: Static Web Apps → Authentication
   - Verificar que Google esté configurado como proveedor
   - Verificar que la redirección sea a `/auth/callback`

2. **Usuario no autenticado**
   - El usuario debe pasar por el flujo de Google OAuth primero
   - Verificar que `/.auth/me` retorne datos (probar manualmente en el navegador)

3. **Variables de entorno faltantes**
   - Verificar que `NEXT_PUBLIC_API_BASE_URL` y `NEXT_PUBLIC_API_KEY` estén configuradas

### Error: "No token received from authentication service"

**Posibles causas:**

1. **APIM no está recibiendo el request correctamente**
   - Verificar en Network tab que el request llegue a APIM
   - Verificar que el header `Ocp-Apim-Subscription-Key` esté presente

2. **Backend no está respondiendo correctamente**
   - Verificar logs del backend en Azure App Service
   - Verificar que el endpoint `/users/api/v1/auth/oauth/google` esté funcionando

3. **CORS issues**
   - Verificar configuración de CORS en APIM
   - Verificar que el origen del frontend esté permitido

### Error: CORS en Google OAuth

Los errores de CORS con `play.google.com` son **normales** y **no afectan la funcionalidad**. Estos son requests internos de Google para analytics/logging.

---

## ✅ Checklist de Verificación

Marca cada item cuando lo hayas verificado:

- [ ] Variables de entorno configuradas en Azure Portal
- [ ] GitHub Actions deployment completado exitosamente (verde)
- [ ] Puedo acceder a la aplicación en el navegador
- [ ] Login con Google funciona
- [ ] Soy redirigido a `/auth/callback` después de autorizar
- [ ] Veo los mensajes de progreso en callback
- [ ] Soy redirigido al dashboard o `/pick_native_language`
- [ ] `localStorage.getItem('authToken')` retorna un JWT
- [ ] `localStorage.getItem('userId')` retorna un número
- [ ] En Network tab veo request exitoso a `/.auth/me`
- [ ] En Network tab veo request exitoso a APIM con subscription key
- [ ] No veo errores 500 en la consola del navegador

---

## 📚 Archivos Importantes

### Backend (`user_service_quickspeak`)
- `DEPLOYMENT_FINAL.md` - Instrucciones de deployment

### Frontend (`quickspeak`)
- `README.md` - Documentación general
- `src/app/auth/callback/page.tsx` - Callback de OAuth (MODIFICADO)
- `src/app/lib/api.ts` - API client con APIM
- `.env.local` - Variables de entorno locales (no subir a Git)

---

## 🎯 Próximos Pasos (Opcional)

Una vez que verifiques que todo funciona:

1. **Probar flujo completo de usuario:**
   - Sign up nuevo usuario
   - Login usuario existente
   - OAuth con Google
   - Selección de idioma nativo
   - Navegación al dashboard

2. **Monitoring:**
   - Configurar Application Insights para detectar errores
   - Configurar alertas para errores 500
   - Monitorear latencia de requests a APIM

3. **Testing:**
   - Agregar tests E2E con Playwright
   - Agregar tests de integración para OAuth
   - Verificar edge cases (usuario sin email, sin nombre, etc.)

---

## ℹ️ Información de Contacto

**Repositorios:**
- Backend: https://github.com/KthArg/user_service_quickspeak
- Frontend: https://github.com/KthArg/quickspeak_web

**Commits relacionados:**
- Frontend: Fix OAuth callback SSL errors (commit en `master`)
- Backend: Clean up documentation (commit en `main`)

---

**Última actualización:** 2025-11-16
**Status:** ✅ Arreglos completados y pusheados
**Acción requerida:** Verificar deployment en Azure
