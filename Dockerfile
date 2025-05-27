# Multi-stage build for Spring Boot application
FROM openjdk:17-jdk-slim as builder

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Create non-root user
RUN groupadd -r fintech && useradd -r -g fintech fintech

# Copy the built JAR from builder stage
COPY --from=builder /app/target/fintech-backend-*.jar app.jar

# Change ownership to non-root user
RUN chown fintech:fintech app.jar

# Switch to non-root user
USER fintech

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
