# 🚀 INSTRUCCIONES DE CONFIGURACIÓN - USER SERVICE

## ✅ PASO 1: Crear el repositorio en GitHub

1. Ve a https://github.com/new
2. Configura:
   - **Repository name**: `user-service`
   - **Description**: "Microservicio de gestión de usuarios - Arquitectura Hexagonal"
   - ✅ Marca "Add a README file"
   - ✅ Marca "Add .gitignore" → selecciona "Java"
3. Clic en **"Create repository"**

## ✅ PASO 2: Clonar y subir el código

```bash
# 1. Clonar tu repositorio vacío
git clone https://github.com/TU-USUARIO/user-service.git
cd user-service

# 2. Copiar TODOS los archivos del proyecto generado
# (Copia todo el contenido de la carpeta user-service que descargaste)

# 3. Agregar y commitear archivos
git add .
git commit -m "Initial commit: Spring Boot project with Hexagonal Architecture"
git push origin main
```

## ✅ PASO 3: Verificar la estructura del proyecto

Tu proyecto debe tener esta estructura:

```
user-service/
├── src/
│   ├── main/
│   │   ├── java/com/yourteacher/userservice/
│   │   │   ├── UserServiceApplication.java
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   └── port/
│   │   │   ├── application/
│   │   │   │   └── service/
│   │   │   ├── adapter/
│   │   │   │   ├── in/web/
│   │   │   │   └── out/
│   │   │   └── infrastructure/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-prod.yml
│   │       └── data.sql
│   └── test/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── deploy-azure.sh
├── README.md
├── ARCHITECTURE.md
├── postman_collection.json
└── .gitignore
```

## ✅ PASO 4: Instalar prerequisitos

Asegúrate de tener instalado:

- ☕ **Java 17**: https://adoptium.net/
- 📦 **Maven 3.6+**: https://maven.apache.org/download.cgi
- 🐳 **Docker** (opcional): https://www.docker.com/get-started

Verifica las instalaciones:

```bash
java -version    # Debe mostrar Java 17
mvn -version     # Debe mostrar Maven 3.6+
```

## ✅ PASO 5: Compilar el proyecto

```bash
# Dentro de la carpeta user-service/
mvn clean install
```

Deberías ver: **BUILD SUCCESS** ✅

## ✅ PASO 6: Ejecutar la aplicación

```bash
mvn spring-boot:run
```

Deberías ver:
```
Started UserServiceApplication in X.XXX seconds
```

## ✅ PASO 7: Probar que funciona

### Opción A: Usar cURL

```bash
# Health check
curl http://localhost:8081/actuator/health

# Crear un usuario
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

### Opción B: Usar Postman

1. Abre Postman
2. Import → File → Selecciona `postman_collection.json`
3. Prueba los endpoints

### Opción C: Usar el navegador

- Health check: http://localhost:8081/actuator/health
- H2 Console: http://localhost:8081/h2-console
  - JDBC URL: `jdbc:h2:mem:userdb`
  - Username: `sa`
  - Password: `password`

## ✅ PASO 8: Verificar H2 Database

1. Ve a http://localhost:8081/h2-console
2. Login con:
   - JDBC URL: `jdbc:h2:mem:userdb`
   - Username: `sa`
   - Password: `password`
3. Ejecuta: `SELECT * FROM USERS;`
4. Deberías ver la tabla de usuarios

## ✅ PASO 9: (OPCIONAL) Ejecutar con Docker

Si quieres usar SQL Server en lugar de H2:

```bash
# Iniciar SQL Server y la app
docker-compose up -d

# Ver logs
docker-compose logs -f user-service

# Detener
docker-compose down
```

## 📝 NOTAS IMPORTANTES

### Sobre las contraseñas en data.sql
Las contraseñas en `data.sql` están como placeholders (`$2a$10$XYZ123...`). 
Para generar contraseñas BCrypt reales:

```java
// Ejecuta esto en un test o main temporal
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
System.out.println(encoder.encode("student123"));
```

O usa: https://bcrypt-generator.com/

### Variables de entorno

Para desarrollo local, la app usa H2 por defecto.
Para usar SQL Server localmente, crea `application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=userdb
    username: sa
    password: YourPassword
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.SQLServerDialect
```

Y ejecuta: `mvn spring-boot:run -Dspring-boot.run.profiles=local`

## 🎯 CHECKLIST DE VERIFICACIÓN

Antes de continuar a la siguiente tarea, verifica que:

- [ ] El repositorio está creado en GitHub
- [ ] Todo el código está subido al repositorio
- [ ] `mvn clean install` ejecuta sin errores
- [ ] La aplicación arranca con `mvn spring-boot:run`
- [ ] http://localhost:8081/actuator/health responde `{"status":"UP"}`
- [ ] Puedes crear un usuario con POST a `/api/v1/users`
- [ ] Puedes listar usuarios con GET a `/api/v1/users`
- [ ] H2 Console funciona (opcional)

## 🚨 SOLUCIÓN DE PROBLEMAS

### Error: "Cannot find symbol" al compilar
```bash
# Limpia y reinstala dependencias
mvn clean
rm -rf ~/.m2/repository
mvn install
```

### Error: "Port 8081 already in use"
```bash
# Cambiar puerto en application.yml
server:
  port: 8082
```

### Error: Lombok no funciona en IDE
1. IntelliJ: Install "Lombok Plugin" + Enable Annotation Processing
2. Eclipse: Instalar Lombok JAR desde https://projectlombok.org/

### La app arranca pero no responde
```bash
# Verificar que está escuchando
curl http://localhost:8081/actuator/health

# Ver logs completos
mvn spring-boot:run -X
```

## 📚 DOCUMENTACIÓN ADICIONAL

- **README.md**: Documentación general del proyecto
- **ARCHITECTURE.md**: Explicación detallada de la arquitectura hexagonal
- **postman_collection.json**: Colección de endpoints para Postman

## ✨ SIGUIENTES PASOS

Una vez que tengas todo funcionando, estás listo para:
✅ **Tarea 2**: Configurar Azure SQL Database

¡Avísame cuando estés listo para continuar! 🚀
