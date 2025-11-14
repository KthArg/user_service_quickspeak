#!/bin/bash

# Script para generar certificados SSL/TLS para mTLS entre APIM y Microservicio
# Genera: CA, certificado de servidor, certificado de cliente

echo "=== Generando Certificados SSL/TLS para mTLS ==="
echo ""

# Configuración
VALIDITY_DAYS=3650  # 10 años
KEY_SIZE=2048
COUNTRY="CR"
STATE="San Jose"
CITY="Cartago"
ORG="TEC"
OU="QuickSpeak"

# Contraseñas (cambiar en producción)
CA_PASSWORD="quickspeak-ca-pass"
SERVER_PASSWORD="quickspeak-server-pass"
CLIENT_PASSWORD="quickspeak-client-pass"
KEYSTORE_PASSWORD="quickspeak-keystore-pass"
TRUSTSTORE_PASSWORD="quickspeak-truststore-pass"

echo "Paso 1: Generando Certificate Authority (CA)..."
# Generar clave privada de CA
openssl genrsa -out ca-key.pem ${KEY_SIZE}

# Generar certificado de CA autofirmado
openssl req -new -x509 -days ${VALIDITY_DAYS} -key ca-key.pem -out ca-cert.pem \
  -subj "/C=${COUNTRY}/ST=${STATE}/L=${CITY}/O=${ORG}/OU=${OU}/CN=QuickSpeak-CA"

echo "✓ CA generado"
echo ""

echo "Paso 2: Generando certificado de SERVIDOR (para el microservicio)..."
# Generar clave privada del servidor
openssl genrsa -out server-key.pem ${KEY_SIZE}

# Generar CSR del servidor
openssl req -new -key server-key.pem -out server.csr \
  -subj "/C=${COUNTRY}/ST=${STATE}/L=${CITY}/O=${ORG}/OU=${OU}/CN=user-service-quickspeak.azurewebsites.net"

# Firmar certificado del servidor con CA
openssl x509 -req -days ${VALIDITY_DAYS} -in server.csr -CA ca-cert.pem -CAkey ca-key.pem \
  -CAcreateserial -out server-cert.pem

echo "✓ Certificado de servidor generado"
echo ""

echo "Paso 3: Generando certificado de CLIENTE (para APIM)..."
# Generar clave privada del cliente
openssl genrsa -out client-key.pem ${KEY_SIZE}

# Generar CSR del cliente
openssl req -new -key client-key.pem -out client.csr \
  -subj "/C=${COUNTRY}/ST=${STATE}/L=${CITY}/O=${ORG}/OU=${OU}/CN=apim-quick-speak"

# Firmar certificado del cliente con CA
openssl x509 -req -days ${VALIDITY_DAYS} -in client.csr -CA ca-cert.pem -CAkey ca-key.pem \
  -CAcreateserial -out client-cert.pem

echo "✓ Certificado de cliente generado"
echo ""

echo "Paso 4: Generando KeyStore para Spring Boot (servidor)..."
# Crear PKCS12 con certificado y clave del servidor
openssl pkcs12 -export -in server-cert.pem -inkey server-key.pem \
  -out server-keystore.p12 -name server -password pass:${KEYSTORE_PASSWORD}

echo "✓ KeyStore generado: server-keystore.p12"
echo ""

echo "Paso 5: Generando TrustStore para Spring Boot (servidor)..."
# Importar CA al TrustStore (para validar certificados de cliente)
keytool -import -trustcacerts -noprompt -alias ca -file ca-cert.pem \
  -keystore server-truststore.jks -storepass ${TRUSTSTORE_PASSWORD}

echo "✓ TrustStore generado: server-truststore.jks"
echo ""

echo "Paso 6: Generando certificado PFX para APIM..."
# Crear PFX para APIM (incluye certificado del cliente + clave privada)
openssl pkcs12 -export -in client-cert.pem -inkey client-key.pem \
  -out apim-client-cert.pfx -password pass:${CLIENT_PASSWORD}

echo "✓ Certificado PFX para APIM generado: apim-client-cert.pfx"
echo ""

echo "Paso 7: Generando archivo de contraseñas..."
cat > passwords.txt <<EOF
=== Contraseñas de Certificados ===

CA Password: ${CA_PASSWORD}
Server Password: ${SERVER_PASSWORD}
Client Password: ${CLIENT_PASSWORD}
KeyStore Password: ${KEYSTORE_PASSWORD}
TrustStore Password: ${TRUSTSTORE_PASSWORD}

APIM Client Certificate Password: ${CLIENT_PASSWORD}

IMPORTANTE: Guarda estas contraseñas de forma segura.
En producción, usa contraseñas fuertes y variables de entorno.
EOF

echo "✓ Contraseñas guardadas en passwords.txt"
echo ""

echo "Paso 8: Limpiando archivos temporales..."
rm -f server.csr client.csr ca-cert.srl

echo "✓ Archivos temporales eliminados"
echo ""

echo "=== Generación de Certificados COMPLETADA ==="
echo ""
echo "Archivos generados:"
echo "  - ca-cert.pem              (Certificado CA)"
echo "  - ca-key.pem               (Clave privada CA)"
echo "  - server-cert.pem          (Certificado servidor)"
echo "  - server-key.pem           (Clave privada servidor)"
echo "  - client-cert.pem          (Certificado cliente)"
echo "  - client-key.pem           (Clave privada cliente)"
echo "  - server-keystore.p12      (KeyStore para Spring Boot)"
echo "  - server-truststore.jks    (TrustStore para Spring Boot)"
echo "  - apim-client-cert.pfx     (Certificado para subir a APIM)"
echo "  - passwords.txt            (Contraseñas de certificados)"
echo ""
echo "Siguiente paso:"
echo "  1. Copia server-keystore.p12 y server-truststore.jks a src/main/resources"
echo "  2. Sube apim-client-cert.pfx a Azure APIM"
echo "  3. Configura application.yml con las contraseñas"
echo ""
