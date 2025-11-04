# Script para cargar variables de entorno desde .env.local en PowerShell
# Uso: .\load-env.ps1

Write-Host "Cargando variables de entorno desde .env.local..." -ForegroundColor Cyan

if (-not (Test-Path ".env.local")) {
    Write-Host "ERROR: Archivo .env.local no encontrado" -ForegroundColor Red
    Write-Host "Asegurate de estar en el directorio raiz del proyecto" -ForegroundColor Yellow
    exit 1
}

$envVars = @{}
$loadedCount = 0

Get-Content .env.local | ForEach-Object {
    $line = $_.Trim()
    
    # Ignorar lineas vacias y comentarios
    if ($line -and !$line.StartsWith("#")) {
        $parts = $line -split '=', 2
        if ($parts.Count -eq 2) {
            $name = $parts[0].Trim()
            $value = $parts[1].Trim()
            
            if ($name) {
                [Environment]::SetEnvironmentVariable($name, $value, "Process")
                $envVars[$name] = $value
                $loadedCount++
                
                # Mostrar variables cargadas (ocultar passwords)
                if ($name -match "PASSWORD|SECRET") {
                    Write-Host "  OK $name = ********" -ForegroundColor Green
                } else {
                    Write-Host "  OK $name = $value" -ForegroundColor Green
                }
            }
        }
    }
}

Write-Host ""
Write-Host "EXITO: $loadedCount variables de entorno cargadas" -ForegroundColor Green
Write-Host ""
Write-Host "Variables configuradas:" -ForegroundColor Cyan
$envVars.Keys | Sort-Object | ForEach-Object {
    Write-Host "  * $_" -ForegroundColor Gray
}
Write-Host ""
Write-Host "Ahora puedes ejecutar:" -ForegroundColor Yellow
Write-Host "   mvn spring-boot:run -Dspring-boot.run.profiles=prod" -ForegroundColor White
Write-Host "   o" -ForegroundColor Yellow
Write-Host "   java -jar target/user-service-1.0.0-SNAPSHOT.jar" -ForegroundColor White