# syntax=docker/dockerfile:1

# --- Build stage ---
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy only Gradle wrapper + build scripts first (better layer caching)
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle

# Make wrapper executable on Linux images
RUN chmod +x ./gradlew

# Pre-download dependencies (keeps later builds fast)
# Note: This will still succeed even if tests fail later.
RUN ./gradlew --no-daemon -q dependencies || true

# Copy sources
COPY src ./src

# Build executable jar
RUN ./gradlew --no-daemon clean bootJar


# --- Runtime stage ---
FROM eclipse-temurin:21-jre

# Spring Boot listens on 8080 by default
EXPOSE 8080

# Non-root user (safer default)
RUN useradd -r -u 1001 appuser
USER appuser

WORKDIR /app

# Copy the fat jar built by Spring Boot
COPY --from=build /app/build/libs/*-SNAPSHOT.jar /app/app.jar

# Tune JVM memory for containers + faster startup; can be overridden by JAVA_OPTS
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=25.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

