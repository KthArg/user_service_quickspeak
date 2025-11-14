# Script PowerShell para generar certificados SSL/TLS para mTLS
# Compatible con Windows

Write-Host "=== Generando Certificados SSL/TLS para mTLS ===" -ForegroundColor Green
Write-Host ""

# Configuración
$ValidityDays = 3650  # 10 años
$KeySize = 2048

# Contraseñas (cambiar en producción)
$KeystorePassword = "quickspeak-keystore-pass"
$TruststorePassword = "quickspeak-truststore-pass"
$ClientPassword = "quickspeak-client-pass"

# Paso 1: Generar KeyStore para el servidor (incluye clave privada y certificado)
Write-Host "Paso 1: Generando KeyStore para el servidor..." -ForegroundColor Cyan
keytool -genkeypair -alias server `
    -keyalg RSA `
    -keysize $KeySize `
    -validity $ValidityDays `
    -dname "CN=user-service-quickspeak.azurewebsites.net, OU=QuickSpeak, O=TEC, L=Cartago, ST=San Jose, C=CR" `
    -keypass $KeystorePassword `
    -keystore server-keystore.p12 `
    -storetype PKCS12 `
    -storepass $KeystorePassword

if ($?) {
    Write-Host "✓ KeyStore del servidor generado: server-keystore.p12" -ForegroundColor Green
} else {
    Write-Host "✗ Error generando KeyStore del servidor" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Paso 2: Exportar certificado del servidor
Write-Host "Paso 2: Exportando certificado del servidor..." -ForegroundColor Cyan
keytool -exportcert -alias server `
    -keystore server-keystore.p12 `
    -storetype PKCS12 `
    -storepass $KeystorePassword `
    -file server-cert.cer

if ($?) {
    Write-Host "✓ Certificado del servidor exportado: server-cert.cer" -ForegroundColor Green
} else {
    Write-Host "✗ Error exportando certificado del servidor" -ForegroundColor Red
}
Write-Host ""

# Paso 3: Generar certificado de cliente (APIM)
Write-Host "Paso 3: Generando certificado de cliente para APIM..." -ForegroundColor Cyan
keytool -genkeypair -alias apim-client `
    -keyalg RSA `
    -keysize $KeySize `
    -validity $ValidityDays `
    -dname "CN=apim-quick-speak, OU=QuickSpeak, O=TEC, L=Cartago, ST=San Jose, C=CR" `
    -keypass $ClientPassword `
    -keystore client-keystore.p12 `
    -storetype PKCS12 `
    -storepass $ClientPassword

if ($?) {
    Write-Host "✓ KeyStore del cliente generado: client-keystore.p12" -ForegroundColor Green
} else {
    Write-Host "✗ Error generando KeyStore del cliente" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Paso 4: Exportar certificado del cliente
Write-Host "Paso 4: Exportando certificado del cliente..." -ForegroundColor Cyan
keytool -exportcert -alias apim-client `
    -keystore client-keystore.p12 `
    -storetype PKCS12 `
    -storepass $ClientPassword `
    -file client-cert.cer

if ($?) {
    Write-Host "✓ Certificado del cliente exportado: client-cert.cer" -ForegroundColor Green
} else {
    Write-Host "✗ Error exportando certificado del cliente" -ForegroundColor Red
}
Write-Host ""

# Paso 5: Crear TrustStore para el servidor (importar certificado del cliente)
Write-Host "Paso 5: Creando TrustStore para el servidor..." -ForegroundColor Cyan
keytool -importcert -alias apim-client `
    -file client-cert.cer `
    -keystore server-truststore.jks `
    -storetype JKS `
    -storepass $TruststorePassword `
    -noprompt

if ($?) {
    Write-Host "✓ TrustStore del servidor generado: server-truststore.jks" -ForegroundColor Green
} else {
    Write-Host "✗ Error generando TrustStore del servidor" -ForegroundColor Red
}
Write-Host ""

# Paso 6: Crear archivo PFX para APIM (mismo que client-keystore.p12)
Write-Host "Paso 6: Preparando certificado para APIM..." -ForegroundColor Cyan
Copy-Item -Path "client-keystore.p12" -Destination "apim-client-cert.pfx" -Force
Write-Host "✓ Certificado PFX para APIM: apim-client-cert.pfx" -ForegroundColor Green
Write-Host ""

# Paso 7: Guardar contraseñas
Write-Host "Paso 7: Guardando contraseñas..." -ForegroundColor Cyan
@"
=== Contraseñas de Certificados ===

KeyStore Password (servidor): $KeystorePassword
TrustStore Password (servidor): $TruststorePassword
Client Certificate Password (APIM): $ClientPassword

IMPORTANTE: Guarda estas contraseñas de forma segura.
En producción, usa contraseñas fuertes y variables de entorno.

=== Archivos Generados ===

Para Spring Boot (copiar a src/main/resources):
  - server-keystore.p12         (KeyStore con certificado del servidor)
  - server-truststore.jks       (TrustStore con certificados de clientes confiables)

Para Azure APIM:
  - apim-client-cert.pfx        (Certificado de cliente para subir a APIM)
  - Password: $ClientPassword

Archivos de respaldo:
  - server-cert.cer             (Certificado público del servidor)
  - client-cert.cer             (Certificado público del cliente)
  - client-keystore.p12         (KeyStore del cliente - respaldo)

=== Configuración application.yml ===

server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:server-keystore.p12
    key-store-password: $KeystorePassword
    key-store-type: PKCS12
    key-alias: server
    client-auth: need
    trust-store: classpath:server-truststore.jks
    trust-store-password: $TruststorePassword
    trust-store-type: JKS
"@ | Out-File -FilePath "passwords.txt" -Encoding UTF8

Write-Host "✓ Contraseñas guardadas en passwords.txt" -ForegroundColor Green
Write-Host ""

Write-Host "=== Generación de Certificados COMPLETADA ===" -ForegroundColor Green
Write-Host ""
Write-Host "Próximos pasos:" -ForegroundColor Yellow
Write-Host "  1. Copia server-keystore.p12 y server-truststore.jks a src/main/resources"
Write-Host "  2. Sube apim-client-cert.pfx a Azure APIM (password: $ClientPassword)"
Write-Host "  3. Configura application.yml con las propiedades SSL"
Write-Host "  4. Lee MTLS_CONFIGURATION.md para instrucciones completas de Azure"
Write-Host ""
