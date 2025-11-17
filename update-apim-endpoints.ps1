# Script para actualizar Azure APIM con los nuevos endpoints
# Ejecutar desde PowerShell con: .\update-apim-endpoints.ps1

$resourceGroup = "rg-quick-speak"
$apimName = "apim-quick-speak"
$apiId = "users-api"
$serviceUrl = "https://user-service-quickspeak.azurewebsites.net"

Write-Host "Actualizando Azure APIM con nuevos endpoints..." -ForegroundColor Cyan

# Endpoint 1: PATCH /users/api/v1/users/{id}/password
Write-Host "`nAgregando endpoint: Change User Password" -ForegroundColor Yellow

$passwordOperation = @{
    displayName = "Change User Password"
    method = "PATCH"
    urlTemplate = "/users/api/v1/users/{id}/password"
    templateParameters = @(
        @{
            name = "id"
            type = "string"
            required = $true
            description = "User ID"
        }
    )
    request = @{
        description = "Change user password request"
        representations = @(
            @{
                contentType = "application/json"
                sample = @{
                    currentPassword = "currentPassword123"
                    newPassword = "newPassword123"
                } | ConvertTo-Json
            }
        )
    }
    responses = @(
        @{
            statusCode = 200
            description = "Password changed successfully"
            representations = @(
                @{
                    contentType = "application/json"
                }
            )
        }
        @{
            statusCode = 400
            description = "Invalid request or incorrect current password"
        }
        @{
            statusCode = 404
            description = "User not found"
        }
    )
}

az apim api operation create `
    --resource-group $resourceGroup `
    --service-name $apimName `
    --api-id $apiId `
    --url-template "/users/api/v1/users/{id}/password" `
    --method "PATCH" `
    --display-name "Change User Password" `
    --description "Change user password with current password validation"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Endpoint de cambio de contraseña agregado exitosamente" -ForegroundColor Green
} else {
    Write-Host "✗ Error agregando endpoint de contraseña" -ForegroundColor Red
}

# Endpoint 2: PATCH /users/api/v1/users/{id}/email
Write-Host "`nAgregando endpoint: Change User Email" -ForegroundColor Yellow

az apim api operation create `
    --resource-group $resourceGroup `
    --service-name $apimName `
    --api-id $apiId `
    --url-template "/users/api/v1/users/{id}/email" `
    --method "PATCH" `
    --display-name "Change User Email" `
    --description "Change user email with uniqueness validation"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Endpoint de cambio de email agregado exitosamente" -ForegroundColor Green
} else {
    Write-Host "✗ Error agregando endpoint de email" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Actualización completada!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`nNuevos endpoints disponibles:" -ForegroundColor White
Write-Host "  1. PATCH /users/api/v1/users/{id}/password" -ForegroundColor Gray
Write-Host "  2. PATCH /users/api/v1/users/{id}/email" -ForegroundColor Gray
Write-Host "`nVerifica en Azure Portal: https://portal.azure.com" -ForegroundColor White
