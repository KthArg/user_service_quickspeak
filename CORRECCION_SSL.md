# Corrección Error SSL en Producción

## Problema Identificado

El servicio intentaba cargar certificados SSL que no existen:
```
Could not load store from 'classpath:server-keystore.p12'
```

## Cambios Realizados

### 1. Deshabilitado SSL en `application-prod.yml`
- SSL comentado temporalmente
- Puerto cambiado de 8443 a 80
- Azure App Service maneja HTTPS automáticamente

### 2. Cambiado `ddl-auto` de `validate` a `update`
- Permite crear/actualizar tablas automáticamente
- Necesario para la primera ejecución

---

## Pasos para Redesplegar

### Opción 1: Maven (Recomendado)

```bash
# 1. Compilar el nuevo JAR
mvn clean package -DskipTests

# 2. Desplegar a Azure
az webapp deploy \
  --resource-group quickspeak-rg \
  --name user-service-quickspeak \
  --src-path target/user-service-1.0.0-SNAPSHOT.jar \
  --type jar
```

### Opción 2: Azure Portal

1. Ir a App Service `user-service-quickspeak`
2. **Deployment Center** (menú izquierdo)
3. Seleccionar método de deployment (GitHub, Local Git, etc.)
4. Hacer push de los cambios

### Opción 3: VS Code Azure Extension

1. Abrir VS Code
2. Click derecho en el proyecto → **Deploy to Web App**
3. Seleccionar `user-service-quickspeak`

---

## Verificar Deployment

### 1. Ver Logs en Tiempo Real

```bash
az webapp log tail \
  --resource-group quickspeak-rg \
  --name user-service-quickspeak
```

**Buscar:**
```
Started UserServiceApplication in X.XXX seconds
Successfully initialized 20 languages in the database
```

### 2. Health Check

```bash
curl https://user-service-quickspeak.azurewebsites.net/actuator/health
```

**Esperado:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" }
  }
}
```

### 3. Obtener Idiomas

```bash
curl https://user-service-quickspeak.azurewebsites.net/api/v1/languages
```

Debe retornar 20 idiomas.

---

## Notas Importantes

### SSL/mTLS
- **Actualmente deshabilitado**
- Se habilitará cuando configures APIM con mTLS
- Azure App Service ya maneja HTTPS automáticamente
- Los usuarios acceden vía `https://` sin problema

### Certificados
Los certificados solo son necesarios cuando:
1. Configures comunicación mTLS entre APIM y el backend
2. Quieras autenticación de certificado de cliente

Para ahora, **no son necesarios**.

---

## Habilitar SSL Más Adelante

Cuando quieras configurar mTLS con APIM:

1. Generar certificados:
```bash
# Server certificate
keytool -genkeypair -alias server -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore server-keystore.p12 \
  -validity 365

# Trust store
keytool -import -alias apim -file apim-cert.pem \
  -keystore server-truststore.jks -storetype JKS
```

2. Copiar a `src/main/resources/`
3. Descomentar sección SSL en `application-prod.yml`
4. Redesplegar

---

## Siguiente Paso

Una vez que el servicio arranque correctamente:

→ **Ejecutar las pruebas de PRUEBAS_PRODUCCION.md**
