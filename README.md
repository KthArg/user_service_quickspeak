# QuickSpeak User Service

**Microservicio de gestión de usuarios para QuickSpeak** - Sistema de autenticación, perfiles y gestión de idiomas para la plataforma de aprendizaje de idiomas QuickSpeak.

## Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Características Principales](#características-principales)
- [Arquitectura](#arquitectura)
- [Stack Tecnológico](#stack-tecnológico)
- [Requisitos Previos](#requisitos-previos)
- [Configuración y Setup](#configuración-y-setup)
- [Variables de Entorno](#variables-de-entorno)
- [Ejecución Local](#ejecución-local)
- [Endpoints de la API](#endpoints-de-la-api)
- [Autenticación y Autorización](#autenticación-y-autorización)
- [Integración con Azure](#integración-con-azure)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Testing](#testing)
- [Despliegue](#despliegue)
- [Documentación Adicional](#documentación-adicional)
- [Solución de Problemas](#solución-de-problemas)

---

## Descripción General

El User Service es un microservicio RESTful construido con Spring Boot que proporciona funcionalidades completas de gestión de usuarios para la plataforma QuickSpeak. El servicio maneja registro, autenticación, perfiles de usuario, gestión de idiomas y preferencias.

### ¿Qué hace este servicio?

- **Gestión de usuarios**: Registro, actualización, eliminación y consulta de usuarios
- **Autenticación**: Soporte para autenticación local (email/password) y OAuth (Google, Microsoft, Facebook) vía Azure EasyAuth
- **Gestión de idiomas**: Administración de idiomas nativos y en aprendizaje por usuario
- **Perfiles**: Gestión de información personal, avatares y preferencias
- **Seguridad**: Encriptación de contraseñas con BCrypt, validación JWT y protección de endpoints

---

## Características Principales

### Gestión de Usuarios
- Registro de nuevos usuarios con validación de email único
- Actualización de perfil (nombre, apellido, roles)
- Cambio de email y contraseña para usuarios locales
- Activación/desactivación de cuentas
- Generación automática de avatar seeds para servicios como DiceBear

### Autenticación Múltiple
- **Local**: Autenticación tradicional con email y contraseña
- **OAuth**: Integración con Azure EasyAuth para Google, Microsoft y Facebook
- Restricciones adecuadas: usuarios OAuth no pueden cambiar email/password en el servicio

### Gestión de Idiomas
- Selección de idioma nativo del usuario
- Gestión de idiomas en aprendizaje
- Priorización de idiomas
- Seguimiento de progreso por idioma

### Roles y Permisos
- **STUDENT**: Usuario estudiante estándar
- **TEACHER**: Usuario con permisos de enseñanza
- **ADMIN**: Administrador del sistema

### Estados de Usuario
- **ACTIVE**: Usuario activo
- **INACTIVE**: Usuario desactivado
- **SUSPENDED**: Usuario suspendido temporalmente

---

## Arquitectura

Este microservicio implementa **Arquitectura Hexagonal (Ports and Adapters)**, un patrón que promueve la separación de responsabilidades y la independencia de frameworks.

### Capas principales:

```
┌─────────────────────────────────────────┐
│        Adapter Layer (REST API)         │
│     Controllers, DTOs, Validators       │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│      Application Layer (Use Cases)      │
│         Service Implementations         │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│      Domain Layer (Business Logic)      │
│      Models, Ports, Business Rules      │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│   Adapter Layer (Persistence, Security) │
│      JPA Repositories, BCrypt, etc.     │
└─────────────────────────────────────────┘
```

**Para más detalles**: Ver [`doc/ARCHITECTURE.md`](doc/ARCHITECTURE.md)

---

## Stack Tecnológico

### Core
- **Spring Boot 3.2.0**: Framework principal
- **Java 17**: Lenguaje de programación
- **Maven**: Gestión de dependencias

### Persistencia
- **Spring Data JPA**: ORM y repositorios
- **Azure SQL Database**: Base de datos en producción
- **H2 Database**: Base de datos en memoria para desarrollo

### Seguridad
- **Spring Security**: Framework de seguridad
- **BCrypt**: Encriptación de contraseñas
- **JWT**: Tokens de autenticación (validados por frontend/APIM)

### Validación
- **Jakarta Validation (Bean Validation)**: Validación de DTOs
- **Hibernate Validator**: Implementación de validaciones

### Utilidades
- **Lombok**: Reducción de código boilerplate
- **Jackson**: Serialización/deserialización JSON

### Infraestructura
- **Azure App Service**: Hosting del servicio
- **Azure API Management (APIM)**: Gateway y gestión de APIs
- **GitHub Actions**: CI/CD

---

## Requisitos Previos

Asegúrate de tener instalado:

- **Java 17+**: [Descargar OpenJDK](https://adoptium.net/)
- **Maven 3.6+**: [Descargar Maven](https://maven.apache.org/download.cgi)
- **Git**: [Descargar Git](https://git-scm.com/downloads)

### Verificar instalaciones

```bash
java -version   # Debe mostrar Java 17 o superior
mvn -version    # Debe mostrar Maven 3.6 o superior
git --version   # Debe mostrar Git 2.x
```

---

## Configuración y Setup

### 1. Clonar el repositorio

```bash
git clone https://github.com/your-username/user_service_quickspeak.git
cd user_service_quickspeak
```

### 2. Instalar dependencias

```bash
mvn clean install
```

Deberías ver: `BUILD SUCCESS`

### 3. Configurar base de datos (Desarrollo)

Por defecto, el servicio usa H2 (base de datos en memoria) para desarrollo local. No requiere configuración adicional.

### 4. (Opcional) Configurar SQL Server local

Si deseas usar SQL Server en lugar de H2:

1. Crea un archivo `src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=userdb;encrypt=false
    username: sa
    password: YourPassword123
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.SQLServerDialect
```

2. Ejecuta con el perfil local:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

---

## Variables de Entorno

### Desarrollo Local

Para desarrollo, las variables están en `application.yml` y no requieren configuración adicional.

### Producción (Azure)

Configura estas variables de entorno en Azure App Service:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | URL de Azure SQL Database | `jdbc:sqlserver://your-server.database.windows.net:1433;database=userdb` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | `sqladmin` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | `YourSecurePassword123!` |
| `JWT_SECRET` | Secreto para firmar JWTs | `your-256-bit-secret-key` |
| `JWT_EXPIRATION` | Tiempo de expiración del JWT (ms) | `86400000` (24 horas) |

### Configurar en Azure:

```bash
az webapp config appsettings set \
  --resource-group your-resource-group \
  --name your-app-service-name \
  --settings \
    SPRING_DATASOURCE_URL="jdbc:sqlserver://..." \
    SPRING_DATASOURCE_USERNAME="sqladmin" \
    SPRING_DATASOURCE_PASSWORD="YourPassword"
```

---

## Ejecución Local

### Opción 1: Con Maven

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8081`

### Opción 2: Como JAR ejecutable

```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

### Verificar que funciona

```bash
# Health check
curl http://localhost:8081/actuator/health

# Debería responder: {"status":"UP"}
```

### Acceder a H2 Console (solo desarrollo)

1. Navega a: `http://localhost:8081/h2-console`
2. Usa estas credenciales:
   - **JDBC URL**: `jdbc:h2:mem:userdb`
   - **Username**: `sa`
   - **Password**: `password`

---

## Endpoints de la API

### Base URL

- **Local**: `http://localhost:8081`
- **Producción**: `https://apim-quick-speak.azure-api.net/users` (vía APIM)

### Usuarios

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/users` | Listar todos los usuarios | No |
| `GET` | `/api/v1/users/{id}` | Obtener usuario por ID | No |
| `GET` | `/api/v1/users/email/{email}` | Obtener usuario por email | No |
| `POST` | `/api/v1/users` | Registrar nuevo usuario | No |
| `PUT` | `/api/v1/users/{id}` | Actualizar usuario | Sí |
| `PATCH` | `/api/v1/users/{id}/password` | Cambiar contraseña | Sí |
| `PATCH` | `/api/v1/users/{id}/email` | Cambiar email | Sí |
| `DELETE` | `/api/v1/users/{id}` | Eliminar usuario | Sí |
| `PATCH` | `/api/v1/users/{id}/activate` | Activar usuario | Sí |
| `PATCH` | `/api/v1/users/{id}/deactivate` | Desactivar usuario | Sí |

### Autenticación

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/v1/auth/login` | Login (obtener JWT) | No |
| `POST` | `/api/v1/auth/validate` | Validar credenciales | No |

### Idiomas

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/languages` | Listar idiomas disponibles | No |
| `GET` | `/api/v1/languages/select-native` | Obtener idiomas para selección | No |
| `POST` | `/api/v1/users/{userId}/languages/native` | Establecer idioma nativo | Sí |
| `POST` | `/api/v1/users/{userId}/languages/learning` | Agregar idioma en aprendizaje | Sí |
| `DELETE` | `/api/v1/users/{userId}/languages/learning/{languageId}` | Eliminar idioma en aprendizaje | Sí |
| `PATCH` | `/api/v1/users/{userId}/languages/learning/{languageId}/priority` | Actualizar prioridad | Sí |

### Perfil

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/users/{userId}/profile` | Obtener perfil completo | Sí |

### Ejemplos de uso

#### Registrar un usuario

```bash
curl -X POST http://localhost:8081/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria@test.com",
    "password": "SecurePass123",
    "firstName": "Maria",
    "lastName": "Lopez",
    "roles": ["STUDENT"]
  }'
```

#### Login

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria@test.com",
    "password": "SecurePass123"
  }'
```

#### Cambiar contraseña

```bash
curl -X PATCH http://localhost:8081/api/v1/users/5/password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "currentPassword": "SecurePass123",
    "newPassword": "NewSecurePass456"
  }'
```

**Nota**: Los usuarios OAuth (Google, Microsoft, Facebook) no pueden cambiar su email o contraseña a través de este servicio.

---

## Autenticación y Autorización

### Flujo de Autenticación

1. **Usuario se registra**: `POST /api/v1/users`
2. **Usuario hace login**: `POST /api/v1/auth/login`
3. **Servicio retorna JWT**: El token contiene userId, email, roles
4. **Cliente incluye JWT**: `Authorization: Bearer <token>` en headers
5. **Endpoints protegidos validan token**: Spring Security valida firma y expiración

### Autenticación Local vs OAuth

#### Usuarios Locales (LOCAL)
- Se registran con email y contraseña
- Pueden cambiar email y contraseña
- Password encriptado con BCrypt

#### Usuarios OAuth (GOOGLE, MICROSOFT, FACEBOOK)
- Autenticados vía Azure EasyAuth
- **No pueden** cambiar email o contraseña en este servicio
- Gestión de credenciales a través del proveedor OAuth

### Roles y Permisos

```java
public enum Role {
    STUDENT,   // Usuario estudiante
    TEACHER,   // Usuario profesor
    ADMIN      // Administrador
}
```

Actualmente, los endpoints no tienen restricciones de rol específicas (se puede agregar con `@PreAuthorize`).

---

## Integración con Azure

### Azure API Management (APIM)

El servicio está expuesto a través de Azure APIM:

- **URL Base**: `https://apim-quick-speak.azure-api.net/users`
- **Autenticación**: Subscription Key en header `Ocp-Apim-Subscription-Key`

#### Actualizar APIM con nuevos endpoints

Si agregas nuevos endpoints, debes actualizar APIM. Ver [`APIM_UPDATE_GUIDE.md`](APIM_UPDATE_GUIDE.md) para instrucciones detalladas.

### Azure App Service

El servicio está desplegado en Azure App Service:

```bash
# Ver logs
az webapp log tail \
  --resource-group your-resource-group \
  --name your-app-service-name

# Reiniciar servicio
az webapp restart \
  --resource-group your-resource-group \
  --name your-app-service-name
```

### Azure SQL Database

La base de datos de producción es Azure SQL Database:

```bash
# Conectar a la base de datos
az sql server show \
  --resource-group your-resource-group \
  --name your-sql-server
```

---

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/yourteacher/userservice/
│   │   ├── UserServiceApplication.java          # Entry point
│   │   │
│   │   ├── domain/                              # DOMAIN LAYER
│   │   │   ├── model/                           # Entidades de dominio
│   │   │   │   ├── User.java                    # Usuario (inmutable)
│   │   │   │   ├── Role.java                    # Enum de roles
│   │   │   │   ├── UserStatus.java              # Enum de estados
│   │   │   │   ├── AuthProvider.java            # Enum de proveedores auth
│   │   │   │   ├── Language.java                # Idioma
│   │   │   │   └── UserLanguage.java            # Relación usuario-idioma
│   │   │   │
│   │   │   └── port/                            # Interfaces (contratos)
│   │   │       ├── in/                          # Input Ports (casos de uso)
│   │   │       │   ├── UserService.java
│   │   │       │   ├── AuthService.java
│   │   │       │   └── LanguageService.java
│   │   │       │
│   │   │       └── out/                         # Output Ports (dependencias)
│   │   │           ├── UserRepository.java
│   │   │           ├── LanguageRepository.java
│   │   │           └── PasswordEncoder.java
│   │   │
│   │   ├── application/                         # APPLICATION LAYER
│   │   │   └── service/                         # Implementación de casos de uso
│   │   │       ├── UserServiceImpl.java
│   │   │       ├── AuthServiceImpl.java
│   │   │       ├── LanguageServiceImpl.java
│   │   │       └── GetUserProfileService.java
│   │   │
│   │   ├── adapter/                             # ADAPTER LAYER
│   │   │   ├── in/                              # Input Adapters
│   │   │   │   └── web/                         # REST Controllers
│   │   │   │       ├── UserController.java
│   │   │   │       ├── AuthController.java
│   │   │   │       ├── LanguageController.java
│   │   │   │       │
│   │   │   │       ├── dto/                     # DTOs (Request/Response)
│   │   │   │       │   ├── UserRequest.java
│   │   │   │       │   ├── UserResponse.java
│   │   │   │       │   ├── LoginRequest.java
│   │   │   │       │   ├── ChangePasswordRequest.java
│   │   │   │       │   └── ...
│   │   │   │       │
│   │   │   │       └── mapper/                  # DTO <-> Domain
│   │   │   │           └── UserDtoMapper.java
│   │   │   │
│   │   │   └── out/                             # Output Adapters
│   │   │       ├── persistence/                 # JPA Implementation
│   │   │       │   ├── JpaUserRepository.java
│   │   │       │   ├── JpaLanguageRepository.java
│   │   │       │   ├── UserRepositoryAdapter.java
│   │   │       │   │
│   │   │       │   ├── entity/                  # JPA Entities
│   │   │       │   │   ├── UserEntity.java
│   │   │       │   │   ├── LanguageEntity.java
│   │   │       │   │   └── UserLanguageEntity.java
│   │   │       │   │
│   │   │       │   └── mapper/                  # Entity <-> Domain
│   │   │       │       ├── UserMapper.java
│   │   │       │       └── LanguageMapper.java
│   │   │       │
│   │   │       └── security/                    # Security Implementations
│   │   │           └── BcryptPasswordEncoderAdapter.java
│   │   │
│   │   └── infrastructure/                      # INFRASTRUCTURE LAYER
│   │       ├── config/                          # Configuración
│   │       │   └── SecurityConfig.java
│   │       │
│   │       └── exception/                       # Exception Handling
│   │           ├── GlobalExceptionHandler.java
│   │           └── ErrorResponse.java
│   │
│   └── resources/
│       ├── application.yml                      # Configuración principal
│       ├── application-prod.yml                 # Configuración producción
│       └── data.sql                             # Datos iniciales (H2)
│
└── test/
    └── java/com/yourteacher/userservice/
        └── UserServiceApplicationTests.java
```

---

## Testing

### Ejecutar todos los tests

```bash
mvn test
```

### Ejecutar tests específicos

```bash
mvn test -Dtest=UserServiceImplTest
```

### Cobertura de código

```bash
mvn clean test jacoco:report
```

El reporte estará en: `target/site/jacoco/index.html`

---

## Despliegue

### Despliegue a Azure App Service

El proyecto usa GitHub Actions para CI/CD automático. Cada push a `main` despliega automáticamente.

#### Despliegue manual

```bash
# 1. Compilar
mvn clean package -DskipTests

# 2. Deploy con Azure CLI
az webapp deploy \
  --resource-group your-resource-group \
  --name your-app-service-name \
  --src-path target/user-service-0.0.1-SNAPSHOT.jar \
  --type jar
```

### Prevenir despliegue en commits

Si quieres hacer commit sin desplegar:

```bash
git commit -m "[skip ci] Your commit message"
```

---

## Documentación Adicional

- **[ARCHITECTURE.md](doc/ARCHITECTURE.md)**: Explicación detallada de la arquitectura hexagonal
- **[INSTRUCCIONES_SETUP.md](doc/INSTRUCCIONES_SETUP.md)**: Instrucciones paso a paso para setup inicial
- **[START_HERE.md](doc/START_HERE.md)**: Guía rápida de inicio
- **[APIM_UPDATE_GUIDE.md](APIM_UPDATE_GUIDE.md)**: Cómo actualizar Azure APIM con nuevos endpoints

---

## Solución de Problemas

### Error: "Port 8081 already in use"

```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8081 | xargs kill -9
```

O cambia el puerto en `application.yml`:

```yaml
server:
  port: 8082
```

### Error: "Cannot find symbol" al compilar

```bash
mvn clean install -U
```

Si persiste:

```bash
# Limpiar cache de Maven
rm -rf ~/.m2/repository
mvn clean install
```

### Lombok no funciona en el IDE

**IntelliJ IDEA**:
1. Instala el plugin "Lombok"
2. Habilita Annotation Processing: `Settings > Build > Compiler > Annotation Processors`

**Eclipse**:
1. Descarga `lombok.jar` de https://projectlombok.org/
2. Ejecuta: `java -jar lombok.jar`

### Conexión a Azure SQL Database falla

Verifica:

1. **Firewall**: Asegúrate de que tu IP está permitida en Azure SQL
```bash
az sql server firewall-rule create \
  --resource-group your-resource-group \
  --server your-sql-server \
  --name AllowMyIP \
  --start-ip-address YOUR_IP \
  --end-ip-address YOUR_IP
```

2. **Connection String**: Verifica que la URL incluye `encrypt=true;trustServerCertificate=false`

3. **Credenciales**: Verifica username y password

### H2 Console no carga

Verifica que `application.yml` tiene:

```yaml
spring:
  h2:
    console:
      enabled: true
      path: /h2-console
```

---

## Contribuciones

Este proyecto es parte de la plataforma QuickSpeak. Para contribuir:

1. Crea una rama desde `main`
2. Implementa tus cambios
3. Escribe tests
4. Crea un Pull Request

---

## Licencia

Este proyecto es privado y pertenece a QuickSpeak.

---

## Contacto

Para preguntas o soporte:
- Email: kenneth@quickspeak.com
- GitHub: [@your-username](https://github.com/your-username)

---

**Última actualización**: Enero 2025
