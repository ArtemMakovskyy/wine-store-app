# Builder stage
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /application
COPY . .
RUN mvn clean package -DskipTests -Dcheckstyle.skip

# Final stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /application
COPY --from=builder /application/target/*.jar /application/app.jar
ENTRYPOINT ["java", "-jar", "/application/app.jar"]
EXPOSE 8080