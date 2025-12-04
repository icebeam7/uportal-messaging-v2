# Dockerfile for Spring Boot application
# Build the application first: mvn clean package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create non-root user
RUN groupadd -r spring && useradd --no-log-init -r -g spring spring

# Copy the pre-built WAR file
COPY target/*.war app.war

# Set ownership
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Health check (using wget since curl is not available in base image)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.war"]
