# syntax=docker/dockerfile:1

# Base Maven stage with source code and dependencies
FROM --platform=$BUILDPLATFORM maven:3.9.6-amazoncorretto-17 AS source-builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src

# Stage to run tests
FROM source-builder AS test-runner
# This stage has mvn, pom.xml, src, and dependencies.
# Tests will be run here using 'mvn test' via docker-compose.

# Stage to build the application package (JAR)
FROM source-builder AS package-builder
RUN mvn package -DskipTests

# Run stage
FROM --platform=$TARGETPLATFORM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=package-builder /app/target/*.jar app.jar

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
