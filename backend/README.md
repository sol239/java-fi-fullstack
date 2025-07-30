# Backend

## Description

This is the backend part of the Java FI Fullstack application, built with Spring Boot. It provides RESTful APIs for the frontend to interact with.

### Structure

The application is structured as a typical Spring Boot project: in controllers/ you will find the REST controllers, in services/ the business logic, and in repositories/ the data access layer. Configuration files are located in src/main/resources.

---

## Run

### Docker

```shell
docker build -t java-fi-backend .
docker run -p 8080:8080 java-fi-backend   # Adjust port if needed
```
### Without Docker

```shell
mvn spring-boot:run
```

### Configuration

In the `src/main/resources/application.properties` file, you can configure various properties such as server port, database settings, and more.

---
