#!/bin/bash

# ============================================
# SCRIPT DE CONFIGURACIÓN PARA AZURE APP SERVICE
# ============================================
# Configura las variables de entorno en Azure App Service
# Uso: ./configure-azure-env.sh <resource-group> <app-name>
# ============================================

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar argumentos
if [ "$#" -ne 2 ]; then
    echo -e "${RED}Error: Número incorrecto de argumentos${NC}"
    echo "Uso: $0 <resource-group> <app-name>"
    echo "Ejemplo: $0 yourteacher-resources yourteacher-user-service"
    exit 1
fi

RESOURCE_GROUP=$1
APP_NAME=$2

echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN}CONFIGURANDO VARIABLES DE ENTORNO EN AZURE${NC}"
echo -e "${GREEN}============================================${NC}"
echo ""
echo "Resource Group: $RESOURCE_GROUP"
echo "App Name: $APP_NAME"
echo ""

# Verificar que el usuario está logueado en Azure CLI
echo -e "${YELLOW}Verificando login en Azure...${NC}"
az account show > /dev/null 2>&1 || {
    echo -e "${RED}No estás logueado en Azure CLI${NC}"
    echo "Por favor ejecuta: az login"
    exit 1
}
echo -e "${GREEN}✓ Login verificado${NC}"
echo ""

# Solicitar información de la base de datos
echo -e "${YELLOW}Ingresa la información de Azure SQL Database:${NC}"
read -p "SQL Server name (ej: yourteacher-sql-server): " SQL_SERVER
read -p "Database name (ej: userservice-db): " DATABASE_NAME
read -p "Database username (ej: sqladmin): " DB_USER
read -sp "Database password: " DB_PASS
echo ""
echo ""

# Solicitar JWT secret
echo -e "${YELLOW}Ingresa el JWT Secret (o presiona Enter para generar uno):${NC}"
read -p "JWT Secret: " JWT_SECRET

if [ -z "$JWT_SECRET" ]; then
    JWT_SECRET=$(openssl rand -base64 32)
    echo -e "${GREEN}✓ JWT Secret generado automáticamente${NC}"
fi
echo ""

# Construir connection string
DB_URL="jdbc:sqlserver://${SQL_SERVER}.database.windows.net:1433;database=${DATABASE_NAME};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

# Configurar variables de entorno en Azure App Service
echo -e "${YELLOW}Configurando variables de entorno...${NC}"

az webapp config appsettings set \
    --resource-group "$RESOURCE_GROUP" \
    --name "$APP_NAME" \
    --settings \
        SPRING_PROFILE="prod" \
        DB_URL="$DB_URL" \
        DB_USERNAME="$DB_USER" \
        DB_PASSWORD="$DB_PASS" \
        DB_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver" \
        DDL_AUTO="validate" \
        SHOW_SQL="false" \
        HIBERNATE_DIALECT="org.hibernate.dialect.SQLServerDialect" \
        JWT_SECRET="$JWT_SECRET" \
        JWT_EXPIRATION="86400000" \
        PORT="8080" \
    > /dev/null

echo -e "${GREEN}✓ Variables de entorno configuradas exitosamente${NC}"
echo ""

# Verificar configuración
echo -e "${YELLOW}Verificando configuración...${NC}"
az webapp config appsettings list \
    --resource-group "$RESOURCE_GROUP" \
    --name "$APP_NAME" \
    --query "[?name=='DB_USERNAME' || name=='SPRING_PROFILE'].{Name:name, Value:value}" \
    --output table

echo ""
echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN}CONFIGURACIÓN COMPLETADA${NC}"
echo -e "${GREEN}============================================${NC}"
echo ""
echo -e "${YELLOW}Notas importantes:${NC}"
echo "1. Las variables están configuradas en Azure App Service"
echo "2. El password de la BD NO se muestra por seguridad"
echo "3. JWT Secret generado y almacenado de forma segura"
echo "4. Reinicia el App Service para aplicar cambios:"
echo "   az webapp restart --resource-group $RESOURCE_GROUP --name $APP_NAME"
echo ""
echo -e "${GREEN}✓ Listo para desplegar el servicio${NC}"
