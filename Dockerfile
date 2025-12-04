# Multi-stage build for Spring Boot application

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace/app

# Copy pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create a non-root user to run the application
RUN groupadd -r spring && useradd --no-log-init -r -g spring spring

# Copy the built artifact from the build stage
COPY --from=build /workspace/app/target/*.war app.war

# Change ownership to the non-root user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.war"]
