# syntax=docker/dockerfile:1

# Build stage
FROM --platform=$BUILDPLATFORM maven:3.9.6-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .

# Download dependencies separately to cache this layer
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM --platform=$TARGETPLATFORM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
