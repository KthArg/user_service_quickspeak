# ⚡ COMANDOS RÁPIDOS - USER SERVICE

## 🚀 INICIO RÁPIDO

```bash
# Clonar tu repo
git clone https://github.com/TU-USUARIO/user-service.git
cd user-service

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

## 🧪 TESTING

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html  # Mac
start target/site/jacoco/index.html # Windows
```

## 📦 BUILD & PACKAGE

```bash
# Compilar sin tests
mvn clean package -DskipTests

# Crear JAR ejecutable
mvn clean package

# Ejecutar JAR
java -jar target/user-service-1.0.0-SNAPSHOT.jar
```

## 🐳 DOCKER

```bash
# Build imagen
docker build -t user-service:latest .

# Ejecutar container
docker run -p 8081:8081 user-service:latest

# Con Docker Compose (SQL Server)
docker-compose up -d

# Ver logs
docker-compose logs -f user-service

# Detener
docker-compose down

# Limpiar volúmenes
docker-compose down -v
```

## 🌐 PRUEBAS DE API

```bash
# Health check
curl http://localhost:8081/actuator/health

# Crear usuario
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

# Obtener usuario por ID
curl http://localhost:8081/api/v1/users/1

# Obtener usuario por email
curl http://localhost:8081/api/v1/users/email/test@example.com

# Actualizar usuario
curl -X PUT http://localhost:8081/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Updated",
    "lastName": "User",
    "roles": ["STUDENT"]
  }'

# Activar usuario
curl -X PATCH http://localhost:8081/api/v1/users/1/activate

# Desactivar usuario
curl -X PATCH http://localhost:8081/api/v1/users/1/deactivate

# Eliminar usuario
curl -X DELETE http://localhost:8081/api/v1/users/1
```

## 🗄️ H2 CONSOLE

```bash
# URL: http://localhost:8081/h2-console

# Configuración:
JDBC URL: jdbc:h2:mem:userdb
Username: sa
Password: password

# Queries útiles:
SELECT * FROM USERS;
SELECT * FROM USER_ROLES;
SELECT u.*, r.role FROM USERS u LEFT JOIN USER_ROLES r ON u.id = r.user_id;
```

## 🔧 GIT

```bash
# Status
git status

# Agregar cambios
git add .

# Commit
git commit -m "feat: descripción del cambio"

# Push
git push origin main

# Ver historial
git log --oneline

# Crear rama
git checkout -b feature/nueva-funcionalidad

# Cambiar rama
git checkout main
```

## 🔍 DEBUGGING

```bash
# Ejecutar con debug logging
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.root=DEBUG"

# Ver logs en tiempo real
tail -f /var/log/user-service.log

# Ejecutar con debug remoto (puerto 5005)
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
```

## 🌍 VARIABLES DE ENTORNO

```bash
# Desarrollo con H2
export SPRING_PROFILE=dev

# Producción con Azure SQL
export SPRING_PROFILE=prod
export DB_URL="jdbc:sqlserver://yourserver.database.windows.net:1433;database=userdb"
export DB_USERNAME="yourusername"
export DB_PASSWORD="yourpassword"
export JWT_SECRET="your-256-bit-secret-key"

# Ejecutar con variables
mvn spring-boot:run
```

## 🚀 DESPLIEGUE A AZURE

```bash
# Dar permisos al script
chmod +x deploy-azure.sh

# Ejecutar despliegue
./deploy-azure.sh

# O manualmente:
mvn clean package -DskipTests
az webapp deploy \
  --resource-group yourteacher-rg \
  --name user-service \
  --src-path target/user-service-1.0.0-SNAPSHOT.jar
```

## 🧹 LIMPIEZA

```bash
# Limpiar build
mvn clean

# Limpiar caché de Maven
rm -rf ~/.m2/repository

# Limpiar Docker
docker system prune -a
docker volume prune
```

## 📊 MÉTRICAS

```bash
# Health check
curl http://localhost:8081/actuator/health

# Info de la app
curl http://localhost:8081/actuator/info

# Métricas (si está habilitado)
curl http://localhost:8081/actuator/metrics
```

## 🔐 SEGURIDAD

```bash
# Generar contraseña BCrypt
# En un test temporal o main:
# BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
# System.out.println(encoder.encode("mypassword"));

# O usar herramienta online:
# https://bcrypt-generator.com/
```

## 📱 POSTMAN

```bash
# Importar colección
Postman → Import → File → postman_collection.json

# Variables de entorno en Postman:
baseUrl: http://localhost:8081/api/v1
```

## 🎯 COMANDOS DE DESARROLLO FRECUENTES

```bash
# 1. Iniciar desarrollo
mvn spring-boot:run

# 2. Rebuild rápido (sin tests)
mvn clean install -DskipTests

# 3. Ver cambios en tiempo real (requiere spring-boot-devtools)
# Guarda el archivo y la app se recarga automáticamente

# 4. Verificar que todo funciona
curl http://localhost:8081/actuator/health
curl http://localhost:8081/api/v1/users

# 5. Push a Git
git add .
git commit -m "feat: nueva funcionalidad"
git push origin main
```

## ⚠️ TROUBLESHOOTING

```bash
# Puerto ocupado
lsof -ti:8081 | xargs kill -9  # Mac/Linux
netstat -ano | findstr :8081   # Windows

# Maven no encuentra dependencias
mvn clean install -U

# Lombok no funciona
# IntelliJ: Settings → Plugins → Install "Lombok"
# IntelliJ: Settings → Build → Compiler → Annotation Processors → Enable

# App no arranca
mvn clean install
rm -rf target/
mvn spring-boot:run -X  # Ver logs detallados
```

## 📚 RECURSOS ÚTILES

```bash
# Ver dependencias del proyecto
mvn dependency:tree

# Ver versiones de dependencias
mvn versions:display-dependency-updates

# Ver plugins disponibles
mvn help:describe -Dplugin=spring-boot

# Generar sitio de documentación
mvn site
```

---

💡 **Tip**: Guarda este archivo como referencia rápida durante el desarrollo
