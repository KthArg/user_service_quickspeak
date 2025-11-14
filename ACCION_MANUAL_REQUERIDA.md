# ⚠️ ACCIÓN MANUAL REQUERIDA

Este documento lista **exactamente** lo que TÚ debes hacer manualmente porque no puedo hacerlo por ti.

---

## 📋 RESUMEN RÁPIDO

**He hecho TODO lo que puedo hacer con código**:
- ✅ Código completo (100%)
- ✅ Documentación completa
- ✅ Scripts SQL
- ✅ Configuración

**Tú debes hacer** (requiere Azure):
- ⚠️ Crear recursos en Azure
- ⚠️ Configurar base de datos
- ⚠️ Desplegar aplicación
- ⚠️ Configurar APIM

**Tiempo estimado**: 2-3 horas

---

## 🎯 LO QUE DEBES HACER (EN ORDEN)

### PASO 1: COMMIT Y PUSH (10 minutos)

```bash
# 1. Ver qué cambió
git status

# 2. Agregar archivos
git add .gitignore
git add src/
git add database/
git add openapi-user-service.yaml
git add *.md
git add certs/README.md certs/.gitkeep
git add ARCHIVOS_IMPORTANTES.md
git add VARIABLES_ENTORNO.md
git add MIGRACION_AZURE_COMPLETA.md
git add ESTADO_TAREAS_PROYECTO.md

# 3. Commit
git commit -m "Add DataLoader, Azure migration guides, database scripts, and OpenAPI spec

- Add DataLoader for language seeding (20 languages)
- Add comprehensive Azure migration guide
- Add SQL scripts for Azure SQL and PostgreSQL
- Add complete environment variables documentation
- Add OpenAPI 3.0 specification with 23 endpoints
- Add APIM configuration guides
- Update .gitignore for proper security
- Document all pending tasks and manual actions required"

# 4. Push
git push origin main
```

**⚠️ Antes de push, verificar**:
- [ ] NO hay certificados (.p12, .pfx, .jks) staged
- [ ] NO hay passwords.txt staged
- [ ] NO hay archivos .env staged

---

### PASO 2: ACCESO A AZURE (con cuenta del compañero)

**Qué necesitas**:
- [ ] Email y password de la cuenta Azure del compañero
- [ ] Permisos de Contributor en la subscripción
- [ ] Azure CLI instalado en tu máquina

**Cómo verificar**:
```bash
# Instalar Azure CLI si no lo tienes
# Windows: https://aka.ms/installazurecliwindows
# O usar winget:
winget install -e --id Microsoft.AzureCLI

# Verificar instalación
az --version

# Login
az login
# Se abrirá el navegador, usar credenciales del compañero

# Verificar subscripción
az account show
az account list --output table
```

---

### PASO 3: EJECUTAR GUÍA DE MIGRACIÓN (2-3 horas)

**Abrir y seguir EXACTAMENTE**:
```
MIGRACION_AZURE_COMPLETA.md
```

**Fases a completar**:

#### Fase 1: Preparar Azure (5 min)
- [ ] Login a Azure
- [ ] Crear Resource Group

#### Fase 2: Configurar Base de Datos (15-20 min)
- [ ] Decidir: ¿Azure SQL o PostgreSQL?
- [ ] Crear servidor de BD
- [ ] Crear database
- [ ] Configurar firewall
- [ ] **Ejecutar script SQL**:
  - `database/schema.sql` (si SQL Server)
  - `database/schema-postgres.sql` (si PostgreSQL)
- [ ] Guardar connection string

#### Fase 3: Crear App Service (10 min)
- [ ] Crear App Service Plan
- [ ] Crear Web App (Java 17)
- [ ] Configurar HTTPS Only

#### Fase 4: Configurar Variables de Entorno (15 min)

**⚠️ MUY IMPORTANTE**: Usar el documento `VARIABLES_ENTORNO.md` como referencia.

Variables CRÍTICAS:
```bash
SPRING_PROFILES_ACTIVE=prod
PORT=8443
DB_URL=<de la fase 2>
DB_USERNAME=<de la fase 2>
DB_PASSWORD=<de la fase 2>
DB_DRIVER=<según BD elegida>
HIBERNATE_DIALECT=<según BD elegida>
DDL_AUTO=validate
SHOW_SQL=false
JWT_SECRET=<GENERAR: openssl rand -base64 32>
JWT_EXPIRATION=86400000
```

**Copiar y pegar** (ajustar valores):
```bash
az webapp config appsettings set \
  --resource-group quickspeak-resources \
  --name user-service-quickspeak \
  --settings \
    SPRING_PROFILES_ACTIVE="prod" \
    PORT="8443" \
    DB_URL="<TU_CONNECTION_STRING>" \
    DB_USERNAME="<TU_USER>" \
    DB_PASSWORD="<TU_PASSWORD>" \
    DB_DRIVER="<DRIVER>" \
    HIBERNATE_DIALECT="<DIALECT>" \
    DDL_AUTO="validate" \
    SHOW_SQL="false" \
    JWT_SECRET="$(openssl rand -base64 32)" \
    JWT_EXPIRATION="86400000"
```

#### Fase 5: Desplegar Aplicación (15 min)
```bash
# En el directorio del proyecto
cd "C:\Users\Kenneth\Documents\TEC\diseño\proyecto\user_service_quickspeak"

# Compilar
mvn clean package -DskipTests

# Desplegar
az webapp deploy \
  --resource-group quickspeak-resources \
  --name user-service-quickspeak \
  --src-path target/user-service-1.0.0-SNAPSHOT.jar \
  --type jar

# Ver logs
az webapp log tail \
  --resource-group quickspeak-resources \
  --name user-service-quickspeak
```

#### Fase 6: Verificar Deployment (10 min)
```bash
# Health check
curl https://user-service-quickspeak.azurewebsites.net/actuator/health

# Debe responder:
# {"status":"UP"}

# Test de idiomas (debe retornar 20 idiomas)
curl https://user-service-quickspeak.azurewebsites.net/api/v1/languages

# Test de registro
curl -X POST https://user-service-quickspeak.azurewebsites.net/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@quickspeak.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

---

### PASO 4: CONFIGURAR APIM (30-45 min) - OPCIONAL

**Seguir**:
```
IMPORTAR_OPENAPI_A_AZURE.md
```

**Pasos resumidos**:
1. Crear APIM (si no existe) - Toma 20-30 min
2. Azure Portal → APIM → APIs → + Add API
3. Seleccionar **OpenAPI**
4. Subir archivo: `openapi-user-service.yaml`
5. URL suffix: `users`
6. Create

**Resultado**: 23 endpoints automáticamente configurados

---

### PASO 5: CONFIGURAR mTLS (30-45 min) - OPCIONAL pero RECOMENDADO

**Seguir**:
```
INSTRUCCIONES_AZURE_MTLS.md
```

**Pasos resumidos**:
1. Subir certificado a APIM (los certificados ya están en `/certs`)
2. Crear backend con certificado
3. Conectar API al backend
4. Configurar variables SSL en App Service

**Ayuda adicional**:
- `GUIA_RAPIDA_BACKEND.md` - Si no sabes cómo configurar backend
- `GUIA_BACKEND_APIM_ACTUALIZADA.md` - Guía detallada

---

## 🚨 ERRORES COMUNES Y SOLUCIONES

### Error: "Database connection failed"

**Causa**: Firewall no permite conexión desde App Service

**Solución**:
```bash
az sql server firewall-rule create \
  --resource-group quickspeak-resources \
  --server quickspeak-sql-server \
  --name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0
```

### Error: "Invalid JWT token"

**Causa**: JWT_SECRET no está configurado o es diferente al usar en desarrollo

**Solución**: Verificar variable
```bash
az webapp config appsettings list \
  --name user-service-quickspeak \
  --resource-group quickspeak-resources \
  | grep JWT_SECRET
```

### Error: "Port 8443 not accessible"

**Causa**: Variable PORT no configurada o App Service en puerto incorrecto

**Solución**:
```bash
az webapp config appsettings set \
  --name user-service-quickspeak \
  --resource-group quickspeak-resources \
  --settings PORT="8443"

az webapp restart \
  --name user-service-quickspeak \
  --resource-group quickspeak-resources
```

### Error: "Table 'languages' doesn't exist"

**Causa**: No se ejecutó el script SQL

**Solución**: Ejecutar `database/schema.sql` o `database/schema-postgres.sql` en la base de datos

---

## ✅ CHECKLIST DE VERIFICACIÓN

Después de completar todos los pasos, verifica:

### Base de Datos
- [ ] Puedo conectarme a la BD desde Azure Data Studio/psql
- [ ] Las tablas existen (users, languages, user_languages)
- [ ] Hay 20 idiomas en la tabla languages

### App Service
- [ ] Health check responde: `/actuator/health` → `{"status":"UP"}`
- [ ] Endpoint de idiomas retorna 20 idiomas: `/api/v1/languages`
- [ ] Puedo registrar un usuario: `POST /api/v1/auth/register`
- [ ] Puedo hacer login: `POST /api/v1/auth/login` → retorna JWT
- [ ] Logs no muestran errores

### APIM (si configuraste)
- [ ] API visible en APIM → APIs
- [ ] 23 operaciones listadas
- [ ] Backend configurado con certificado (si mTLS)
- [ ] Prueba via APIM funciona: `https://apim-quick-speak.azure-api.net/users/api/v1/languages`

---

## 📞 SI NECESITAS AYUDA

### Logs en tiempo real:
```bash
az webapp log tail \
  --name user-service-quickspeak \
  --resource-group quickspeak-resources
```

### Ver configuración:
```bash
az webapp config appsettings list \
  --name user-service-quickspeak \
  --resource-group quickspeak-resources \
  --output table
```

### Reiniciar App Service:
```bash
az webapp restart \
  --name user-service-quickspeak \
  --resource-group quickspeak-resources
```

### Ver estado de la BD:
```bash
az sql db show \
  --resource-group quickspeak-resources \
  --server quickspeak-sql-server \
  --name userdb
```

---

## 📚 ARCHIVOS DE REFERENCIA

Mientras ejecutas los pasos, ten estos archivos abiertos:

1. **`MIGRACION_AZURE_COMPLETA.md`** - Guía principal (paso a paso)
2. **`VARIABLES_ENTORNO.md`** - Referencia de variables
3. **`ESTADO_TAREAS_PROYECTO.md`** - Para ver qué está hecho y qué falta
4. **Este archivo** - Para saber qué hacer en cada momento

---

## 🎯 RESULTADO ESPERADO

Al terminar, tendrás:

✅ Microservicio desplegado en Azure
✅ Base de datos configurada con 20 idiomas
✅ API funcionando con 23 endpoints
✅ JWT authentication funcionando
✅ Health checks pasando
✅ (Opcional) APIM configurado
✅ (Opcional) mTLS funcionando

---

## ⏱️ TIEMPO ESTIMADO

| Tarea | Tiempo |
|-------|--------|
| Push a Git | 10 min |
| Login a Azure | 5 min |
| Setup Azure | 1 hora |
| Deployment | 30 min |
| Verificación | 15 min |
| APIM (opcional) | 30-45 min |
| mTLS (opcional) | 30-45 min |
| **TOTAL (mínimo)** | **2 horas** |
| **TOTAL (completo)** | **3-4 horas** |

---

## 🚀 ¡EMPECEMOS!

**Tu siguiente acción AHORA MISMO**:

1. Abrir terminal
2. Navegar al proyecto:
   ```bash
   cd "C:\Users\Kenneth\Documents\TEC\diseño\proyecto\user_service_quickspeak"
   ```
3. Ver el estado:
   ```bash
   git status
   ```
4. Hacer commit y push (comandos arriba en PASO 1)

**Después de push**:
1. Abrir `MIGRACION_AZURE_COMPLETA.md`
2. Seguir paso a paso

---

**¡TODO ESTÁ LISTO!** Solo falta ejecutar. 💪

Si encuentras algún problema, usa la sección "ERRORES COMUNES" o revisa los logs.

**¡Éxito con el deployment!** 🚀
