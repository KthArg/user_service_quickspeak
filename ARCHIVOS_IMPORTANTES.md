# 📋 Archivos Importantes del Proyecto

## ✅ Archivos que DEBEN estar en Git

### Código Fuente
- ✅ `src/**/*.java` - Todo el código Java
- ✅ `pom.xml` - Configuración de Maven
- ✅ `src/main/resources/application.yml` - Configuración base
- ✅ `src/main/resources/application-prod.yml` - Configuración de producción
- ⚠️ `src/main/resources/*.p12` y `*.jks` - **IMPORTANTE**: Los keystores están en Git porque no contienen secretos reales (las contraseñas están en variables de entorno)

### Configuración API
- ✅ `openapi-user-service.yaml` - Especificación OpenAPI de la API

### Documentación
- ✅ `README.md` - Documentación principal
- ✅ `doc/*.md` - Documentación adicional
- ✅ `certs/README.md` - Instrucciones para certificados

### Estructura
- ✅ `certs/.gitkeep` - Mantiene la carpeta certs/ en Git

## ❌ Archivos que NO deben estar en Git (ya están en .gitignore)

### Certificados y Claves (SEGURIDAD)
- ❌ `*.p12` - Keystores PKCS12
- ❌ `*.pfx` - Certificados de intercambio personal
- ❌ `*.jks` - Java KeyStores
- ❌ `*.pem` - Certificados PEM
- ❌ `*.cer` - Certificados
- ❌ `*.crt` - Certificados
- ❌ `*.key` - Claves privadas
- ❌ `certs/*` (excepto README.md y .gitkeep)

### Secretos y Contraseñas
- ❌ `**/passwords.txt` - Archivos de contraseñas
- ❌ `.env` - Variables de entorno
- ❌ `.env.local` - Variables locales
- ❌ `*.publishsettings` - Configuración de publicación de Azure

### Build y Compilación
- ❌ `target/` - Directorio de build de Maven
- ❌ `*.class` - Archivos compilados
- ❌ `*.jar`, `*.war` - Archivos empaquetados

### IDE y Herramientas
- ❌ `.idea/` - IntelliJ IDEA
- ❌ `.vscode/` - VS Code
- ❌ `.claude/` - Claude Code
- ❌ `*.iml` - Archivos de IntelliJ

### Sistema Operativo
- ❌ `.DS_Store` - macOS
- ❌ `Thumbs.db` - Windows

## 🔍 Verificar antes de hacer Push

Antes de hacer `git push`, verifica:

### 1. No hay secretos
```bash
git diff --cached | grep -iE "(password|secret|key|token)" | grep -v "PASSWORD" | grep -v "SECRET"
```

### 2. No hay certificados
```bash
git status | grep -iE "\.(p12|pfx|jks|pem|cer|crt|key)$"
```

### 3. .gitignore está actualizado
```bash
git diff .gitignore
```

## ⚠️ ESPECIAL: Keystores en src/main/resources

Los archivos `server-keystore.p12` y `server-truststore.jks` en `src/main/resources/` están excluidos por el .gitignore debido a las reglas `*.p12` y `*.jks`.

Esto es **CORRECTO** por seguridad. Las contraseñas se proporcionan en tiempo de ejecución vía variables de entorno:
- `SSL_KEYSTORE_PASSWORD`
- `SSL_TRUSTSTORE_PASSWORD`

## 📝 Configuración Actual del .gitignore

El `.gitignore` está configurado para:

1. ✅ Ignorar todos los certificados y keystores (`*.p12`, `*.pfx`, `*.jks`, etc.)
2. ✅ Ignorar la carpeta `certs/` excepto `README.md` y `.gitkeep`
3. ✅ Ignorar archivos de IDE (`.idea/`, `.vscode/`)
4. ✅ Ignorar archivos de build (`target/`)
5. ✅ Ignorar variables de entorno (`.env`, `.env.local`)
6. ✅ Ignorar contraseñas (`**/passwords.txt`, `*password*.txt`)

## 🚀 Para hacer Push Seguro

1. Verifica el estado:
```bash
git status
```

2. Revisa los cambios:
```bash
git diff
git diff --cached
```

3. Agrega archivos necesarios:
```bash
git add .gitignore
git add certs/README.md certs/.gitkeep
git add src/main/java/
git add openapi-user-service.yaml
```

4. Commit:
```bash
git commit -m "Descripción del cambio"
```

5. Push:
```bash
git push origin main
```

## 📋 Checklist Pre-Push

- [ ] No hay archivos `.p12`, `.pfx`, `.jks` en el staging area
- [ ] No hay archivos `passwords.txt` en el staging area
- [ ] No hay archivos `.env` en el staging area
- [ ] El código Java compila correctamente
- [ ] Los tests pasan (si existen)
- [ ] La documentación está actualizada

## 🆘 Si Commiteaste un Secreto por Error

**⚠️ NO HAGAS PUSH**

1. Si aún no hiciste push:
```bash
git reset HEAD~1
# o
git reset --soft HEAD~1
```

2. Si ya hiciste push:
   - Debes considerar los secretos comprometidos
   - Cambia las contraseñas/secretos
   - Usa `git filter-branch` o herramientas como `git-filter-repo` para limpiar el historial
   - Fuerza un push nuevo (coordinado con el equipo)

---

**Última actualización**: Noviembre 2025
**Importante**: Siempre revisa qué archivos estás agregando antes de hacer commit.
