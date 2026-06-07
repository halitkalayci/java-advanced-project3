# ============================================================
# Generic multi-stage Dockerfile for ANY SmartOrder backend module.
# Build context must be the `backend/` directory.
#
#   docker build --build-arg MODULE=order-service -f backend/Dockerfile \
#     -t smartorder/order-service backend
#
# (docker-compose passes MODULE per service.)
# ============================================================
ARG MODULE

# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
ARG MODULE
WORKDIR /build

# Parent POM first for better layer caching.
COPY pom.xml ./pom.xml
COPY ${MODULE}/pom.xml ./${MODULE}/pom.xml
COPY ${MODULE}/src ./${MODULE}/src

WORKDIR /build/${MODULE}
RUN mvn -B clean package -DskipTests

# Extract Spring Boot layers for cache-friendly runtime image.
WORKDIR /build/${MODULE}/target
RUN sh -c 'java -Djarmode=layertools -jar *.jar extract'

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:25-jre-alpine AS runtime
ARG MODULE
LABEL maintainer="smartorder-team"
LABEL module="${MODULE}"

RUN apk add --no-cache curl \
    && addgroup -S spring && adduser -S spring -G spring

WORKDIR /app
COPY --from=builder --chown=spring:spring /build/${MODULE}/target/dependencies/ ./
COPY --from=builder --chown=spring:spring /build/${MODULE}/target/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /build/${MODULE}/target/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /build/${MODULE}/target/application/ ./

USER spring:spring

ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
