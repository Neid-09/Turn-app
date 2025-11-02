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
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=8761
ENV CONFIG_SERVER_URI="http://config-server:8888"
ENV INSTANCE_HOSTNAME="localhost"

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

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Comando de inicio con opciones optimizadas para contenedores
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -Djava.security.egd=file:/dev/./urandom \
  -Dspring.config.import=optional:configserver:${CONFIG_SERVER_URI} \
  -DINSTANCE_HOSTNAME=${INSTANCE_HOSTNAME} \
  -jar app.jar"]
