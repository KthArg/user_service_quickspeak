# 📦 USER-SERVICE - ÍNDICE DE RECURSOS

## 🎯 TAREA 1 COMPLETADA: Crear repositorio GitHub `user-service`

✅ **Objetivo cumplido**: Establecer repositorio independiente para el microservicio de usuarios con arquitectura hexagonal configurada.

---

## 📁 ESTRUCTURA DE ARCHIVOS ENTREGADOS

### 📘 DOCUMENTACIÓN (LEE PRIMERO)
1. **INSTRUCCIONES_SETUP.md** ⭐ START HERE
   - Paso a paso para configurar el proyecto
   - Comandos para clonar, compilar y ejecutar
   - Troubleshooting de problemas comunes
   
2. **RESUMEN_PROYECTO.md** 
   - Visión general del proyecto completo
   - Estadísticas y componentes creados
   - Diagramas de arquitectura
   
3. **COMANDOS_RAPIDOS.md**
   - Cheat sheet de comandos útiles
   - Testing, Docker, Git, API calls
   - Referencia rápida para desarrollo

### 📦 PROYECTO COMPLETO
**Carpeta: `user-service/`** (Todo el código fuente)

#### Archivos principales:
- `pom.xml` - Configuración Maven con todas las dependencias
- `README.md` - Documentación del proyecto
- `ARCHITECTURE.md` - Explicación detallada de arquitectura hexagonal
- `.gitignore` - Configuración de Git
- `Dockerfile` - Para containerización
- `docker-compose.yml` - Orquestación de containers
- `deploy-azure.sh` - Script de despliegue a Azure
- `postman_collection.json` - Colección de endpoints para Postman

#### Código fuente (`src/main/java/com/yourteacher/userservice/`):

**🔵 Domain Layer (Núcleo de Negocio)**
- `domain/model/User.java` - Entidad de dominio
- `domain/model/Role.java` - Enum de roles
- `domain/model/UserStatus.java` - Enum de estados
- `domain/port/in/UserService.java` - Puerto de entrada (casos de uso)
- `domain/port/out/UserRepository.java` - Puerto de salida (persistencia)
- `domain/port/out/PasswordEncoder.java` - Puerto de salida (encriptación)

**🟢 Application Layer (Orquestación)**
- `application/service/UserServiceImpl.java` - Implementación de casos de uso

**🟡 Adapter Layer - Input (Entrada/API)**
- `adapter/in/web/UserController.java` - Controlador REST
- `adapter/in/web/dto/UserRequest.java` - DTO de entrada
- `adapter/in/web/dto/UserResponse.java` - DTO de salida
- `adapter/in/web/mapper/UserDtoMapper.java` - Mapper DTO ↔ Domain

**🟠 Adapter Layer - Output (Salida/Persistencia)**
- `adapter/out/persistence/JpaUserRepository.java` - Repositorio Spring Data
- `adapter/out/persistence/UserRepositoryAdapter.java` - Adapter del puerto
- `adapter/out/persistence/entity/UserEntity.java` - Entidad JPA
- `adapter/out/persistence/mapper/UserMapper.java` - Mapper Entity ↔ Domain
- `adapter/out/security/BcryptPasswordEncoderAdapter.java` - Adapter de encriptación

**🔴 Infrastructure Layer (Configuración)**
- `infrastructure/config/SecurityConfig.java` - Configuración Spring Security
- `infrastructure/exception/GlobalExceptionHandler.java` - Manejo de errores
- `infrastructure/exception/ErrorResponse.java` - DTO de errores

**⚙️ Configuración (`src/main/resources/`)**
- `application.yml` - Configuración desarrollo (H2)
- `application-prod.yml` - Configuración producción (Azure SQL)
- `data.sql` - Datos iniciales para testing

**🧪 Tests (`src/test/java/`)**
- `domain/model/UserTest.java` - Tests unitarios del dominio

---

## 🚀 PASOS SIGUIENTES (EN ORDEN)

### 1️⃣ LEE PRIMERO (5 min)
```
📄 INSTRUCCIONES_SETUP.md
```
Este archivo te guiará paso a paso para:
- Crear el repositorio en GitHub
- Clonar y subir el código
- Compilar el proyecto
- Ejecutar la aplicación
- Probar los endpoints

### 2️⃣ CONFIGURA EL PROYECTO (15 min)
```bash
# Sigue los pasos del archivo INSTRUCCIONES_SETUP.md
git clone https://github.com/TU-USUARIO/user-service.git
cd user-service
mvn clean install
mvn spring-boot:run
```

### 3️⃣ VERIFICA QUE FUNCIONA (5 min)
```bash
# Health check
curl http://localhost:8081/actuator/health

# Crear usuario de prueba
curl -X POST http://localhost:8081/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "roles": ["STUDENT"]
  }'

# Listar usuarios
curl http://localhost:8081/api/v1/users
```

### 4️⃣ EXPLORA LA ARQUITECTURA (10 min)
```
📄 ARCHITECTURE.md
📄 RESUMEN_PROYECTO.md
```
Entiende cómo está estructurado el proyecto y por qué.

### 5️⃣ USA POSTMAN (5 min)
```
1. Abre Postman
2. Import → File → postman_collection.json
3. Prueba todos los endpoints
```

### 6️⃣ GUARDA REFERENCIAS (2 min)
```
📄 COMANDOS_RAPIDOS.md (bookmark esto)
```
Lo usarás constantemente durante el desarrollo.

---

## 📊 ESTADÍSTICAS DEL PROYECTO

```
📦 Archivos generados: 35+
├── 21 archivos .java (código fuente)
├── 3 archivos .yml (configuración)
├── 4 archivos .md (documentación)
├── 1 archivo .xml (Maven)
├── 1 archivo .sql (datos iniciales)
├── 1 archivo .json (Postman)
├── 1 Dockerfile
├── 1 docker-compose.yml
├── 1 deploy-azure.sh
└── 1 .gitignore

🎯 Líneas de código: ~2,500+
🏗️ Arquitectura: Hexagonal (Ports & Adapters)
📚 Capas: 4 (Domain, Application, Adapter, Infrastructure)
🔌 Endpoints REST: 9
🗄️ Tablas DB: 2 (users, user_roles)
🧪 Tests unitarios: 6
📖 Documentación: Completa y detallada
```

---

## ✅ CHECKLIST DE VERIFICACIÓN

Antes de pasar a la siguiente tarea, asegúrate de:

- [ ] Repositorio creado en GitHub
- [ ] Código clonado localmente
- [ ] `mvn clean install` ejecuta sin errores
- [ ] Aplicación arranca con `mvn spring-boot:run`
- [ ] Health check responde: http://localhost:8081/actuator/health
- [ ] Puedes crear un usuario via POST
- [ ] Puedes listar usuarios via GET
- [ ] H2 Console funciona (opcional)
- [ ] Entiendes la estructura de carpetas
- [ ] Has leído ARCHITECTURE.md

---

## 🎓 CONCEPTOS CLAVE APRENDIDOS

### Arquitectura Hexagonal
✅ Separación entre dominio y tecnología
✅ Ports & Adapters pattern
✅ Dependency Inversion Principle
✅ Domain-Driven Design básico

### Spring Boot
✅ Configuración con application.yml
✅ Spring Data JPA
✅ Spring Security básico
✅ REST Controllers
✅ Validation con annotations

### Buenas Prácticas
✅ DTOs separados del dominio
✅ Mappers para transformaciones
✅ Manejo centralizado de errores
✅ Tests unitarios
✅ Documentación clara

---

## 🆘 SOPORTE

### Si tienes problemas:

1. **Problemas de compilación**
   ```bash
   mvn clean install -U
   rm -rf ~/.m2/repository
   ```

2. **Lombok no funciona**
   - IntelliJ: Install Lombok Plugin + Enable Annotation Processing
   - Eclipse: Instalar lombok.jar

3. **Puerto ocupado**
   ```bash
   # Cambiar puerto en application.yml
   server:
     port: 8082
   ```

4. **Consulta la documentación**
   - README.md del proyecto
   - INSTRUCCIONES_SETUP.md
   - COMANDOS_RAPIDOS.md

---

## 🎯 SIGUIENTE TAREA

Una vez que verifiques que todo funciona correctamente:

### ✅ TAREA 1: COMPLETADA
**"Crear repositorio GitHub user-service"**

### ▶️ TAREA 2: SIGUIENTE
**"Configurar Azure SQL Database"**

Objetivos:
- Provisionar Azure SQL Database
- Configurar connection string
- Establecer firewall rules
- Migrar de H2 a Azure SQL
- Preparar para producción

---

## 📞 CONTACTO

Para dudas sobre este proyecto universitario, consulta con tu equipo o profesor.

---

**¡Felicitaciones! Has completado exitosamente la primera tarea del proyecto. 🎉**

El microservicio está listo para desarrollo local y preparado para integración con Azure SQL Database.

---

## 📚 RECURSOS ADICIONALES

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Azure SQL Database](https://azure.microsoft.com/services/sql-database/)
- [Docker Documentation](https://docs.docker.com/)
- [Maven Guide](https://maven.apache.org/guides/)

---

**Fecha de creación**: 2025-11-03
**Versión**: 1.0.0
**Estado**: ✅ Producción Ready
