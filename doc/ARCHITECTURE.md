# Documentación de Arquitectura - User Service

## 🏗️ Arquitectura Hexagonal

Este microservicio implementa **Arquitectura Hexagonal** (también conocida como **Ports and Adapters**), un patrón arquitectónico que busca crear aplicaciones débilmente acopladas, independientes del framework y fácilmente testeables.

## 📐 Capas de la Arquitectura

### 1. Domain Layer (Núcleo de Negocio)

La capa de dominio contiene la **lógica de negocio pura** y está completamente aislada de detalles técnicos.

#### Componentes:

**Models (Entidades de Dominio)**
- `User.java`: Entidad principal que representa un usuario
- `Role.java`: Enum con los roles del sistema
- `UserStatus.java`: Enum con los estados del usuario

**Ports (Interfaces)**
- **Input Ports** (`domain/port/in/`): Definen los casos de uso
  - `UserService.java`: Operaciones disponibles para usuarios
  
- **Output Ports** (`domain/port/out/`): Definen dependencias externas
  - `UserRepository.java`: Operaciones de persistencia
  - `PasswordEncoder.java`: Encriptación de contraseñas

#### Características:
✅ Sin dependencias de frameworks
✅ Lógica de negocio pura
✅ Fácilmente testeable
✅ Inmutable y thread-safe

### 2. Application Layer (Casos de Uso)

Orquesta la lógica de negocio y coordina el flujo de datos entre adaptadores y dominio.

#### Componentes:

**Services**
- `UserServiceImpl.java`: Implementa los casos de uso definidos en `UserService`

#### Responsabilidades:
- Validar reglas de negocio
- Coordinar operaciones entre puertos
- Gestionar transacciones
- Transformar datos entre capas

### 3. Adapter Layer (Conectores)

Conecta el dominio con el mundo exterior mediante implementaciones concretas de los puertos.

#### 3.1 Input Adapters (Adaptadores de Entrada)

**REST API** (`adapter/in/web/`)
- `UserController.java`: Expone endpoints REST
- `dto/`: Data Transfer Objects para requests/responses
- `mapper/`: Convierte entre DTOs y modelos de dominio

#### 3.2 Output Adapters (Adaptadores de Salida)

**Persistence** (`adapter/out/persistence/`)
- `JpaUserRepository.java`: Repositorio Spring Data JPA
- `UserRepositoryAdapter.java`: Implementa `UserRepository` del dominio
- `entity/UserEntity.java`: Entidad JPA para persistencia
- `mapper/UserMapper.java`: Convierte entre entidad JPA y modelo dominio

**Security** (`adapter/out/security/`)
- `BcryptPasswordEncoderAdapter.java`: Implementa `PasswordEncoder` del dominio

### 4. Infrastructure Layer (Configuración)

Configuración transversal y aspectos técnicos.

#### Componentes:

**Configuration** (`infrastructure/config/`)
- `SecurityConfig.java`: Configuración de Spring Security

**Exception Handling** (`infrastructure/exception/`)
- `GlobalExceptionHandler.java`: Manejo centralizado de errores
- `ErrorResponse.java`: DTO para respuestas de error

## 🔄 Flujo de Datos

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────────────────┐
│         INPUT ADAPTER (REST Controller)         │
│  • Recibe HTTP Request                          │
│  • Valida DTO                                   │
│  • Mapea DTO → Domain Model                     │
└───────────────────┬─────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────┐
│        APPLICATION LAYER (Service)              │
│  • Ejecuta lógica de negocio                    │
│  • Valida reglas de dominio                     │
│  • Coordina operaciones                         │
└──────┬──────────────────────┬───────────────────┘
       │                      │
       ▼                      ▼
┌─────────────┐      ┌──────────────────┐
│   DOMAIN    │      │  OUTPUT ADAPTERS │
│   MODEL     │      │  • Repository    │
│  (User)     │◄─────┤  • Encoder       │
└─────────────┘      └──────────────────┘
                              │
                              ▼
                     ┌─────────────────┐
                     │    Database     │
                     └─────────────────┘
```

## 🎯 Principios Aplicados

### 1. Dependency Inversion Principle (DIP)

El dominio **no depende** de detalles de implementación. Los adaptadores dependen del dominio.

```java
// ❌ MAL: Dominio depende de implementación
class UserService {
    private JpaUserRepository repository; // Dependencia concreta
}

// ✅ BIEN: Dominio depende de abstracción
class UserServiceImpl implements UserService {
    private UserRepository repository; // Dependencia abstracta (puerto)
}
```

### 2. Single Responsibility Principle (SRP)

Cada clase tiene una única responsabilidad:
- `User.java`: Lógica de negocio de usuario
- `UserEntity.java`: Mapeo a base de datos
- `UserController.java`: Exponer API REST
- `UserServiceImpl.java`: Orquestar casos de uso

### 3. Open/Closed Principle (OCP)

Abierto a extensión, cerrado a modificación:
- Nuevos adaptadores sin cambiar dominio
- Nuevos endpoints sin cambiar lógica de negocio

## 🧪 Testabilidad

### Unit Tests (Dominio)
```java
@Test
void testUserValidation() {
    User user = User.builder()
        .email("invalid-email")
        .build();
    
    assertFalse(user.hasValidEmail());
}
```

### Integration Tests (Application)
```java
@Test
void testRegisterUser() {
    UserRepository mockRepo = mock(UserRepository.class);
    PasswordEncoder mockEncoder = mock(PasswordEncoder.class);
    
    UserService service = new UserServiceImpl(mockRepo, mockEncoder);
    // Test sin dependencias reales
}
```

## 🔌 Extensibilidad

### Agregar un nuevo adaptador de entrada (GraphQL)

1. Crear `GraphQLUserController` en `adapter/in/graphql/`
2. Usar el mismo `UserService` del dominio
3. **No modificar** dominio ni application layer

### Cambiar de SQL a MongoDB

1. Crear `MongoUserRepositoryAdapter` en `adapter/out/persistence/`
2. Implementar `UserRepository` del dominio
3. **No modificar** dominio ni application layer

## 📊 Diagrama de Dependencias

```
┌─────────────────────────────────────────┐
│           APPLICATION LAYER             │
│                                         │
│  ┌───────────────────────────────────┐ │
│  │      UserServiceImpl              │ │
│  └───────────┬───────────────────────┘ │
└──────────────┼─────────────────────────┘
               │
               │ depends on
               ▼
┌─────────────────────────────────────────┐
│            DOMAIN LAYER                 │
│                                         │
│  ┌─────────────┐    ┌─────────────┐   │
│  │ UserService │    │    User     │   │
│  │   (Port)    │    │  (Entity)   │   │
│  └─────────────┘    └─────────────┘   │
│                                         │
│  ┌─────────────┐    ┌─────────────┐   │
│  │   UserRepo  │    │   PasswordE │   │
│  │   (Port)    │    │   (Port)    │   │
│  └─────────────┘    └─────────────┘   │
└──────────┬──────────────────┬──────────┘
           ▲                  ▲
           │                  │
           │ implements       │ implements
           │                  │
┌──────────┴──────────────────┴──────────┐
│          ADAPTER LAYER                  │
│                                         │
│  ┌──────────────────┐  ┌─────────────┐│
│  │ UserRepoAdapter  │  │   BCrypt    ││
│  │                  │  │   Adapter   ││
│  └──────────────────┘  └─────────────┘│
└─────────────────────────────────────────┘
```

## 🚀 Ventajas de esta Arquitectura

### 1. Independencia de Framework
- El dominio no conoce Spring Boot
- Fácil migrar a otro framework

### 2. Testabilidad Mejorada
- Tests unitarios sin dependencias externas
- Fácil usar mocks

### 3. Mantenibilidad
- Cambios aislados por capa
- Menor acoplamiento

### 4. Escalabilidad
- Fácil agregar nuevos adaptadores
- Microservicios desacoplados

### 5. Evolución del Sistema
- Cambiar base de datos sin afectar lógica
- Agregar nuevos canales (GraphQL, gRPC)

## 📚 Referencias

- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Spring Boot + Hexagonal Architecture](https://www.baeldung.com/hexagonal-architecture-ddd-spring)
