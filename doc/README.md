# User Service - Microservicio de Gestión de Usuarios

Microservicio para la gestión de usuarios implementado con **Spring Boot** y **Arquitectura Hexagonal**.

## 🏗️ Arquitectura

Este proyecto sigue los principios de **Arquitectura Hexagonal (Ports & Adapters)**, separando claramente las responsabilidades:

```
user-service/
├── domain/                          # Capa de Dominio (Lógica de Negocio)
│   ├── model/                       # Entidades de dominio
│   │   ├── User.java
│   │   ├── Role.java
│   │   └── UserStatus.java
│   └── port/                        # Puertos (Interfaces)
│       ├── in/                      # Puertos de entrada (Use Cases)
│       │   └── UserService.java
│       └── out/                     # Puertos de salida (Repositorios)
│           ├── UserRepository.java
│           └── PasswordEncoder.java
├── application/                     # Capa de Aplicación (Orquestación)
│   └── service/
│       └── UserServiceImpl.java    # Implementación de casos de uso
├── adapter/                         # Capa de Adaptadores (Infraestructura)
│   ├── in/                         # Adaptadores de entrada
│   │   └── web/                    # REST API
│   │       ├── UserController.java
│   │       ├── dto/
│   │       └── mapper/
│   └── out/                        # Adaptadores de salida
│       ├── persistence/            # Base de datos
│       │   ├── JpaUserRepository.java
│       │   ├── UserRepositoryAdapter.java
│       │   ├── entity/
│       │   └── mapper/
│       └── security/               # Seguridad
│           └── BcryptPasswordEncoderAdapter.java
└── infrastructure/                  # Configuración e infraestructura
    ├── config/
    │   └── SecurityConfig.java
    └── exception/
        ├── GlobalExceptionHandler.java
        └── ErrorResponse.java
```

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **Azure SQL Database** (Producción)
- **H2 Database** (Desarrollo)
- **Lombok**
- **Maven**
- **JWT** (Para autenticación futura)

## 📋 Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- Git

## 🔧 Configuración Local

### 1. Clonar el repositorio

```bash
git clone https://github.com/TU-USUARIO/user-service.git
cd user-service
```

### 2. Compilar el proyecto

```bash
mvn clean install
```

### 3. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8081/api/v1`

### 4. Acceder a H2 Console (solo desarrollo)

URL: `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:userdb`
- Username: `sa`
- Password: `password`

## 🌐 API Endpoints

### Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/users` | Registrar nuevo usuario |
| GET | `/api/v1/users` | Listar todos los usuarios |
| GET | `/api/v1/users/{id}` | Obtener usuario por ID |
| GET | `/api/v1/users/email/{email}` | Obtener usuario por email |
| PUT | `/api/v1/users/{id}` | Actualizar usuario |
| DELETE | `/api/v1/users/{id}` | Eliminar usuario |
| PATCH | `/api/v1/users/{id}/activate` | Activar usuario |
| PATCH | `/api/v1/users/{id}/deactivate` | Desactivar usuario |

### Catálogo de Idiomas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/languages` | Listar todos los idiomas disponibles |
| GET | `/api/v1/languages/{id}` | Obtener idioma por ID |
| GET | `/api/v1/languages/code/{code}` | Obtener idioma por código ISO (ej: "es", "en") |
| GET | `/api/v1/languages/starting` | Obtener idiomas recomendados para empezar (top 10) |

### Gestión de Idiomas de Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/users/{userId}/languages` | Obtener todos los idiomas del usuario |
| GET | `/api/v1/users/{userId}/languages/native` | Obtener idioma nativo del usuario |
| GET | `/api/v1/users/{userId}/languages/learning` | Obtener idiomas que está aprendiendo |
| POST | `/api/v1/users/{userId}/languages` | Agregar idioma al usuario |
| PATCH | `/api/v1/users/{userId}/languages/{languageId}/native` | Marcar idioma como nativo |
| DELETE | `/api/v1/users/{userId}/languages/{languageId}` | Eliminar idioma del usuario |

### Health Check

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/actuator/health` | Estado del servicio |

## 📝 Ejemplos de Uso

### Registrar un nuevo usuario

```bash
curl -X POST http://localhost:8081/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "estudiante@example.com",
    "password": "password123",
    "firstName": "Juan",
    "lastName": "Pérez",
    "roles": ["STUDENT"]
  }'
```

### Obtener todos los usuarios

```bash
curl http://localhost:8081/api/v1/users
```

### Obtener usuario por ID

```bash
curl http://localhost:8081/api/v1/users/1
```

### Obtener todos los idiomas disponibles

```bash
curl http://localhost:8081/api/v1/languages
```

### Obtener idiomas recomendados para empezar

```bash
curl http://localhost:8081/api/v1/languages/starting
```

### Obtener idioma por código

```bash
curl http://localhost:8081/api/v1/languages/code/es
```

### Agregar idioma a usuario

```bash
curl -X POST http://localhost:8081/api/v1/users/1/languages \
  -H "Content-Type: application/json" \
  -d '{
    "languageId": 1
  }'
```

### Marcar idioma como nativo

```bash
curl -X PATCH http://localhost:8081/api/v1/users/1/languages/2/native
```

### Obtener idiomas de un usuario

```bash
curl http://localhost:8081/api/v1/users/1/languages
```

### Obtener idioma nativo de un usuario

```bash
curl http://localhost:8081/api/v1/users/1/languages/native
```

### Eliminar idioma de usuario

```bash
curl -X DELETE http://localhost:8081/api/v1/users/1/languages/2
```

## 🔐 Roles de Usuario

- **STUDENT**: Estudiante
- **TEACHER**: Profesor/Tutor
- **ADMIN**: Administrador

## 🗄️ Configuración de Base de Datos

### Desarrollo (H2)

Por defecto usa H2 en memoria. La configuración está en `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:userdb
    username: sa
    password: password
```

### Producción (Azure SQL)

Para producción, configura las variables de entorno:

```bash
export SPRING_PROFILE=prod
export DB_URL=jdbc:sqlserver://your-server.database.windows.net:1433;database=userdb
export DB_USERNAME=your-username
export DB_PASSWORD=your-password
export JWT_SECRET=your-secret-key
```

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report
```

## 📦 Despliegue en Azure

### 1. Crear Azure SQL Database (Ver siguiente tarea)

### 2. Configurar App Service

```bash
# Crear App Service
az webapp create --resource-group your-rg --plan your-plan --name user-service --runtime "JAVA:17-java17"

# Configurar variables de entorno
az webapp config appsettings set --resource-group your-rg --name user-service --settings \
  SPRING_PROFILE=prod \
  DB_URL="jdbc:sqlserver://..." \
  DB_USERNAME="your-username" \
  DB_PASSWORD="your-password" \
  JWT_SECRET="your-secret"

# Desplegar
mvn clean package
az webapp deploy --resource-group your-rg --name user-service --src-path target/user-service-1.0.0-SNAPSHOT.jar
```

## 🏛️ Principios de Arquitectura Hexagonal

1. **Domain Layer**: Contiene la lógica de negocio pura, sin dependencias externas
2. **Application Layer**: Orquesta los casos de uso usando el dominio
3. **Adapter Layer**: Conecta el dominio con el mundo exterior (REST, BD, etc.)
4. **Infrastructure Layer**: Configuración y aspectos transversales

### Beneficios

✅ **Independencia del framework**: El dominio no depende de Spring
✅ **Testeable**: Fácil de testear sin dependencias externas
✅ **Mantenible**: Cambios en la infraestructura no afectan el dominio
✅ **Flexible**: Fácil cambiar adaptadores (REST → GraphQL, SQL → NoSQL)

## 📚 Recursos Adicionales

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Azure SQL Database](https://azure.microsoft.com/en-us/products/azure-sql/database/)

## 👥 Equipo

Proyecto universitario - YourTeacher AI

## 📄 Licencia

Este proyecto es parte de un trabajo universitario.
