# 🚀 Deployment Final - QuickSpeak User Service Integration

## ✅ Completado

### 1. Backend (user_service_quickspeak)
- ✅ Código commiteado y pusheado a `main`
- ✅ Backend ya está en producción en Azure App Service
- ✅ Última verificación: todos los endpoints funcionando

### 2. Frontend (quickspeak)
- ✅ Código commiteado y pusheado a rama `user_service_integration`
- ✅ Integración completa con APIM
- ✅ API client con mapeo automático de rutas
- ✅ Auth (login, signup, OAuth) integrado

---

## 📋 Pasos Pendientes (Manual)

### Paso 1: Configurar Variables de Entorno en Azure Static Web Apps

**Ubicación:** Azure Portal → Static Web Apps → quickspeak → Configuration → Application settings

**Variables a agregar:**

| Name                     | Value                                  |
|--------------------------|----------------------------------------|
| `NEXT_PUBLIC_API_BASE_URL` | `https://apim-quick-speak.azure-api.net` |
| `NEXT_PUBLIC_API_KEY`      | `c081b2299247481f827d5b08211624f2`       |

**Pasos:**
1. Ir a https://portal.azure.com
2. Buscar y abrir tu Static Web App (quickspeak)
3. En el menú lateral: **Configuration** → **Application settings**
4. Click en **"+ Add"** dos veces para agregar ambas variables
5. Click en **"Save"** (arriba)
6. La aplicación se reiniciará automáticamente (tarda ~1-2 minutos)

---

### Paso 2: Mergear a Main (Opcional pero Recomendado)

Tienes dos opciones:

#### Opción A: Merge a main localmente y push
```bash
cd /c/Users/Kenneth/Documents/TEC/diseño/proyecto/quickspeak
git checkout main
git pull origin main
git merge user_service_integration
git push origin main
```

#### Opción B: Crear Pull Request en GitHub
1. Ir a https://github.com/KthArg/quickspeak_web
2. Verás un banner para crear PR desde `user_service_integration`
3. Click en "Compare & pull request"
4. Review y merge el PR

**Nota:** GitHub Actions deployará automáticamente a Azure Static Web Apps cuando pushes a `main`.

---

### Paso 3: Verificar Deployment

Una vez que el deployment termine (2-5 minutos después del push/merge):

#### 3.1 Verificar Variables de Entorno

En Azure Portal → Static Web Apps → Configuration → Application settings:
- ✅ `NEXT_PUBLIC_API_BASE_URL` debe estar presente
- ✅ `NEXT_PUBLIC_API_KEY` debe estar presente

#### 3.2 Verificar la Aplicación en Vivo

1. **Abrir la aplicación:**
   ```
   https://[tu-static-web-app].azurestaticapps.net
   ```

2. **Probar Sign Up:**
   - Ir a `/sign_up`
   - Crear cuenta de prueba:
     - Email: `test-deployment@quickspeak.com`
     - Password: `Password123!`
     - First Name: `Test`
     - Last Name: `User`
   - Click "Sign Up"

3. **Verificar éxito:**
   - Debe mostrar mensaje de éxito
   - Debe redirigir a `/pick_native_language`

4. **Verificar localStorage (F12 → Console):**
   ```javascript
   localStorage.getItem('authToken')  // Debe mostrar JWT token
   localStorage.getItem('userId')     // Debe mostrar ID numérico
   ```

5. **Verificar Network Tab (F12 → Network):**
   - Filtrar por "apim"
   - Requests deben ir a: `https://apim-quick-speak.azure-api.net/users/...`
   - Headers deben incluir: `Ocp-Apim-Subscription-Key`
   - Responses deben ser: `200 OK`

#### 3.3 Probar Login

1. Ir a `/login`
2. Usar las credenciales creadas arriba
3. Verificar:
   - Login exitoso
   - Redirección correcta
   - Token y userId guardados en localStorage

---

## 🔍 Troubleshooting

### Error: "Failed to fetch" o "Network error"

**Causa:** Variables de entorno no configuradas

**Solución:**
1. Verificar que las variables estén en Azure Portal
2. Reiniciar el Static Web App
3. Esperar 2-3 minutos para que se apliquen

### Error: "401 Unauthorized"

**Causa:** Subscription key incorrecta o faltante

**Solución:**
1. Verificar que `NEXT_PUBLIC_API_KEY` tenga el valor correcto
2. Verificar en Network tab que el header `Ocp-Apim-Subscription-Key` esté presente

### Error: "404 Not Found"

**Causa:** Ruta incorrecta o APIM policy mal configurada

**Solución:**
1. Verificar que `NEXT_PUBLIC_API_BASE_URL` sea exactamente: `https://apim-quick-speak.azure-api.net`
2. Verificar en Network tab la URL completa del request

### El deployment no se activa automáticamente

**Solución:**
1. Ir a GitHub → Actions
2. Verificar que el workflow haya corrido
3. Si falló, revisar los logs
4. Si no corrió, puede que necesites hacer un push adicional a `main`

---

## 📊 Estado de Endpoints

### ✅ Funcionando en Producción (Backend)

**Auth:**
- POST `/users/api/v1/auth/login`
- POST `/users/api/v1/auth/signup`
- POST `/users/api/v1/auth/oauth/google`

**Users:**
- GET `/users/api/v1/users/{userId}`
- PUT `/users/api/v1/users/{userId}`

**Languages:**
- GET `/users/api/v1/languages/starting`
- GET `/users/api/v1/languages/catalog`
- GET `/users/api/v1/users/{userId}/languages`
- POST `/users/api/v1/users/{userId}/languages`
- POST `/users/api/v1/users/{userId}/languages/{languageId}/make-native`
- DELETE `/users/api/v1/users/{userId}/languages/{languageId}`

### ✅ Integrado en Frontend

**Auth Pages:**
- `/login` → Usa APIM + guarda userId
- `/sign_up` → Usa APIM + guarda userId
- `/auth/callback` → OAuth usa APIM + guarda userId

**API Routes (Next.js):**
- `/api/auth/login` → Proxy a APIM
- `/api/auth/signup` → Proxy a APIM
- `/api/languages/*` → Proxy a APIM con mapeo automático
- `/api/user/*` → Proxy a APIM con userId injection

---

## 🎯 Checklist Final

Antes de considerar el deployment completo:

- [ ] Variables de entorno configuradas en Azure Portal
- [ ] Código mergeado a `main` (o pusheado a `user_service_integration`)
- [ ] GitHub Actions deployment exitoso (verde)
- [ ] Sign up funciona en producción
- [ ] Login funciona en producción
- [ ] localStorage guarda token + userId
- [ ] Network tab muestra requests a APIM con subscription key
- [ ] Responses son 200 OK

---

## 📚 Documentación de Referencia

Para más detalles técnicos, consulta:

1. **INTEGRACION_COMPLETA_USER_SERVICE.md** - Arquitectura y detalles técnicos
2. **INSTRUCCIONES_INTEGRACION_APIM.md** - Configuración de APIM
3. **DEPLOYMENT_APIM_INTEGRATION.md** - Guía de deployment detallada

---

## 🚀 Próximos Pasos (Opcional)

Después de verificar que todo funcione:

1. **Integrar otros microservicios** (chat, speakers, dictionary)
2. **Configurar CI/CD automático** para backend
3. **Agregar tests E2E** con Playwright
4. **Configurar monitoring** en Azure Application Insights
5. **Implementar rate limiting** en APIM

---

## ℹ️ Información de Contacto

**Repositorios:**
- Backend: https://github.com/KthArg/user_service_quickspeak
- Frontend: https://github.com/KthArg/quickspeak_web

**Azure Resources:**
- Static Web App: quickspeak
- App Service: quickspeak-user-service
- APIM: apim-quick-speak
- Resource Group: QuickSpeak

---

**Última actualización:** 2025-11-15
**Estado:** ✅ Listo para deployment final
