# 📁 Carpeta de Certificados

Esta carpeta contiene los certificados necesarios para mTLS (Mutual TLS) entre Azure APIM y el microservicio.

## ⚠️ IMPORTANTE - SEGURIDAD

**Los certificados NO están en el repositorio Git** por motivos de seguridad. Están excluidos en `.gitignore`.

## 📜 Certificados Necesarios

Después de generar los certificados, esta carpeta debe contener:

### Para Azure APIM:
- `apim-client-cert.pfx` - Certificado del cliente (APIM) para subir a Azure
  - **Contraseña**: `quickspeak-client-pass`
  - **Dónde se usa**: Azure APIM → Certificates

### Para el Microservicio (ya copiados a src/main/resources):
- `server-keystore.p12` - KeyStore del servidor
  - **Contraseña**: `quickspeak-keystore-pass`
  - **Copiado a**: `src/main/resources/server-keystore.p12`

- `server-truststore.jks` - TrustStore del servidor
  - **Contraseña**: `quickspeak-truststore-pass`
  - **Copiado a**: `src/main/resources/server-truststore.jks`

### Otros archivos (opcionales/backup):
- `server-cert.cer` - Certificado público del servidor
- `client-cert.cer` - Certificado público del cliente
- `passwords.txt` - Contraseñas de certificados (NUNCA commitear)

## 🔐 Generar Certificados

Para generar los certificados, ejecuta el script correspondiente:

**Windows (PowerShell)**:
```powershell
# El script debería estar en la raíz del proyecto
.\generate-certificates.ps1
```

**Linux/Mac**:
```bash
# El script debería estar en la raíz del proyecto
./generate-certificates.sh
```

## 📋 Checklist Post-Generación

Después de generar los certificados:

- [ ] Verificar que `apim-client-cert.pfx` existe
- [ ] Verificar que `server-keystore.p12` existe
- [ ] Verificar que `server-truststore.jks` exists
- [ ] Copiar `server-keystore.p12` a `src/main/resources/`
- [ ] Copiar `server-truststore.jks` a `src/main/resources/`
- [ ] Guardar las contraseñas en un gestor seguro (no en el repo)
- [ ] **NO commitear** estos archivos a Git

## 🚀 Uso en Azure

1. **Subir a APIM**:
   - Azure Portal → APIM → Certificates → Add
   - Subir: `apim-client-cert.pfx`
   - Contraseña: `quickspeak-client-pass`

2. **Configurar en App Service**:
   - Las variables de entorno deben tener las contraseñas:
     - `SSL_KEYSTORE_PASSWORD=quickspeak-keystore-pass`
     - `SSL_TRUSTSTORE_PASSWORD=quickspeak-truststore-pass`

## 🔄 Renovación de Certificados

Los certificados generados son válidos por **10 años**.

Para renovar cuando expiren:
1. Ejecutar nuevamente el script de generación
2. Reemplazar certificados en Azure APIM
3. Redesplegar el microservicio con los nuevos keystores

## 📞 Ayuda

Ver documentación completa en:
- `INSTRUCCIONES_AZURE_MTLS.md` - Configuración paso a paso
- `RESUMEN_MTLS.md` - Resumen de la configuración

---

**Última actualización**: Noviembre 2025
