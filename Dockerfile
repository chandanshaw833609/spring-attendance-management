# Build stage
# FROM ubuntu:latest as build
# RUN apt-get update && apt-get install -y openjdk-17-jdk maven
# WORKDIR /app
# COPY . .
# RUN mvn package
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests


# Production stage
FROM openjdk:17-jdk-slim
COPY --from=build /target/attendance-1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
