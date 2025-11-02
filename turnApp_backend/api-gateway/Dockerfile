# Etapa 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build

# Instalar Maven
RUN apk add --no-cache maven

# Establecer directorio de trabajo
WORKDIR /app

# Copiar los archivos de configuración de Maven primero (para aprovechar el caché de Docker)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Descargar dependencias (se cachea si el pom.xml no cambia)
RUN mvn dependency:go-offline -B || true

# Copiar el código fuente
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Variables de entorno
ENV JAVA_OPTS="-Xmx768m -Xms384m"
ENV SERVER_PORT=8080
ENV CONFIG_SERVER_URI="http://config-server:8888"
ENV EUREKA_URI="http://discovery-server:8761/eureka/"
ENV KEYCLOAK_HOST="keycloak"
ENV KEYCLOAK_PORT="9090"
ENV INSTANCE_HOSTNAME="localhost"
ENV INSTANCE_PORT="8080"

# Crear usuario no-root para ejecutar la aplicación
RUN addgroup -S spring && adduser -S spring -G spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar permisos
RUN chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring

# Exponer el puerto
EXPOSE ${SERVER_PORT}

# Healthcheck (usa actuator si está disponible, si no verifica el puerto)
HEALTHCHECK --interval=30s --timeout=3s --start-period=90s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Comando de inicio con opciones optimizadas para contenedores y WebFlux
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -Djava.security.egd=file:/dev/./urandom \
  -Dspring.cloud.config.uri=${CONFIG_SERVER_URI} \
  -Deureka.client.service-url.defaultZone=${EUREKA_URI} \
  -DKEYCLOAK_HOST=${KEYCLOAK_HOST} \
  -DKEYCLOAK_PORT=${KEYCLOAK_PORT} \
  -DINSTANCE_HOSTNAME=${INSTANCE_HOSTNAME} \
  -DINSTANCE_PORT=${INSTANCE_PORT} \
  -jar app.jar"]
