#!/bin/bash

# Script de despliegue para Azure App Service
# Asegúrate de tener Azure CLI instalado y autenticado

echo "🚀 Iniciando despliegue de user-service a Azure..."

# Variables (ajusta según tu configuración)
RESOURCE_GROUP="yourteacher-rg"
APP_SERVICE_PLAN="yourteacher-plan"
APP_NAME="user-service"
LOCATION="eastus"

# 1. Compilar la aplicación
echo "📦 Compilando aplicación..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar la aplicación"
    exit 1
fi

# 2. Verificar que existe el JAR
JAR_FILE="target/user-service-1.0.0-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ No se encontró el archivo JAR"
    exit 1
fi

echo "✅ Compilación exitosa"

# 3. Crear App Service (si no existe)
echo "🔍 Verificando App Service..."
az webapp show --resource-group $RESOURCE_GROUP --name $APP_NAME > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "📱 Creando App Service..."
    az webapp create \
        --resource-group $RESOURCE_GROUP \
        --plan $APP_SERVICE_PLAN \
        --name $APP_NAME \
        --runtime "JAVA:17-java17"
fi

# 4. Configurar variables de entorno
echo "⚙️  Configurando variables de entorno..."
az webapp config appsettings set \
    --resource-group $RESOURCE_GROUP \
    --name $APP_NAME \
    --settings \
        SPRING_PROFILE=prod \
        PORT=8080

echo "⚠️  IMPORTANTE: Configura manualmente las siguientes variables en Azure Portal:"
echo "  - DB_URL"
echo "  - DB_USERNAME"
echo "  - DB_PASSWORD"
echo "  - JWT_SECRET"

# 5. Desplegar aplicación
echo "🚀 Desplegando aplicación..."
az webapp deploy \
    --resource-group $RESOURCE_GROUP \
    --name $APP_NAME \
    --src-path $JAR_FILE \
    --type jar

if [ $? -eq 0 ]; then
    echo "✅ Despliegue exitoso"
    echo "🌐 URL: https://$APP_NAME.azurewebsites.net"
    echo "📊 Health check: https://$APP_NAME.azurewebsites.net/actuator/health"
else
    echo "❌ Error en el despliegue"
    exit 1
fi

echo "✨ Proceso completado"
