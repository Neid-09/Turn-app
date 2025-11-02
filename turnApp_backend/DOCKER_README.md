# 🐳 Docker Setup - Turn App Backend

Dockerfiles para los servicios de infraestructura de Spring Cloud.

## 📦 Servicios Incluidos

### 1. **Config Server** (Puerto 8888)

- Servidor de configuración centralizada
- Usa configuración nativa desde `classpath:/config`
- Spring Cloud Config Server

### 2. **Discovery Server** (Puerto 8761)

- Servidor Eureka para registro y descubrimiento de servicios
- Interfaz web disponible en `http://localhost:8761`
- Spring Cloud Netflix Eureka Server

### 3. **API Gateway** (Puerto 8080)

- Gateway reactivo con Spring Cloud Gateway WebFlux
- Integración con Keycloak para OAuth2
- Enrutamiento dinámico con Eureka

## 🚀 Inicio Rápido

### Opción 1: Docker Compose (Recomendado)

```powershell
# Construir y levantar todos los servicios
cd c:\dev-proyects\TURN-APP\Turn-app\turnApp_backend
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Reconstruir imágenes
docker-compose up -d --build
```

### Opción 2: Docker Individual

```powershell
# Config Server
cd config-server
docker build -t config-server:latest .
docker run -d -p 8888:8888 --name config-server config-server:latest

# Discovery Server
cd ../discovery-server
docker build -t discovery-server:latest .
docker run -d -p 8761:8761 \
  -e CONFIG_SERVER_URI="http://config-server:8888" \
  --name discovery-server \
  --link config-server \
  discovery-server:latest

# API Gateway
cd ../api-gateway
docker build -t api-gateway:latest .
docker run -d -p 8080:8080 \
  -e CONFIG_SERVER_URI="http://config-server:8888" \
  -e EUREKA_URI="http://discovery-server:8761/eureka/" \
  --name api-gateway \
  --link config-server \
  --link discovery-server \
  api-gateway:latest
```

## 🔧 Variables de Entorno

### Config Server

- `JAVA_OPTS`: Opciones JVM (default: `-Xmx512m -Xms256m`)
- `SERVER_PORT`: Puerto del servidor (default: `8888`)

### Discovery Server

- `JAVA_OPTS`: Opciones JVM (default: `-Xmx512m -Xms256m`)
- `SERVER_PORT`: Puerto del servidor (default: `8761`)
- `CONFIG_SERVER_URI`: URI del Config Server (default: `http://config-server:8888`)
- `INSTANCE_HOSTNAME`: Hostname de la instancia (default: `localhost`)

### API Gateway

- `JAVA_OPTS`: Opciones JVM (default: `-Xmx768m -Xms384m`)
- `SERVER_PORT`: Puerto del servidor (default: `8080`)
- `CONFIG_SERVER_URI`: URI del Config Server
- `EUREKA_URI`: URI de Eureka
- `KEYCLOAK_HOST`: Host de Keycloak (default: `keycloak`)
- `KEYCLOAK_PORT`: Puerto de Keycloak (default: `9090`)
- `INSTANCE_HOSTNAME`: Hostname de la instancia
- `INSTANCE_PORT`: Puerto de la instancia

## 🏥 Health Checks

Todos los servicios incluyen healthchecks:

```powershell
# Config Server
curl http://localhost:8888/actuator/health

# Discovery Server
curl http://localhost:8761/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health
```

## 🌐 Acceso a Servicios

- **Config Server**: <http://localhost:8888>
- **Eureka Dashboard**: <http://localhost:8761>
- **API Gateway**: <http://localhost:8080>

## 📋 Requisitos

- Docker 20.10+
- Docker Compose 1.29+
- Java 21 (para build local)
- Maven 3.8+ (para build local)

## 🔄 Orden de Inicio

El `docker-compose.yml` maneja automáticamente el orden:

1. **Config Server** inicia primero
2. **Discovery Server** espera a que Config Server esté saludable
3. **API Gateway** espera a que ambos anteriores estén saludables

## 🐛 Troubleshooting

### Los servicios no inician

```powershell
# Ver logs detallados
docker-compose logs -f [service-name]

# Verificar estado
docker-compose ps

# Reiniciar un servicio específico
docker-compose restart [service-name]
```

### Limpiar y reiniciar

```powershell
# Detener y eliminar contenedores
docker-compose down

# Eliminar volúmenes
docker-compose down -v

# Reconstruir todo desde cero
docker-compose up -d --build --force-recreate
```

### Problemas de red

```powershell
# Verificar red
docker network ls
docker network inspect turnapp_turnapp-network

# Recrear red
docker-compose down
docker network prune
docker-compose up -d
```

## 📝 Notas

- Las imágenes usan Alpine Linux para ser más ligeras
- Todos los servicios corren como usuario no-root (`spring`)
- Los JAR se construyen en modo multi-stage para optimizar el tamaño
- Los healthchecks aseguran que los servicios estén listos antes de aceptar tráfico

## 🔐 Seguridad

- Usuario no-root en todos los contenedores
- Secrets de Keycloak deben ser configurados en producción
- Considera usar Docker secrets o variables de entorno para datos sensibles

---

**Versiones:**

- Spring Boot: 3.5.7
- Java: 21
- Spring Cloud: 2025.0.0
