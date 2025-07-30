# Stage 1: Build with Maven
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run with JDK
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar
COPY assets /app/assets

# Create a volume for the H2 database file
VOLUME ["/app/data"]

# Expose the port defined in application.properties
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
