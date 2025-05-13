# Builder stage
FROM maven:3-openjdk-17-slim AS builder
WORKDIR /application
COPY . .
RUN mvn clean package -DskipTests

# Final stage
FROM openjdk:17-jdk-slim
WORKDIR /application
COPY --from=builder /application/target/*.jar /application/app.jar
ENTRYPOINT ["java", "-jar", "/application/app.jar"]
EXPOSE 8080
