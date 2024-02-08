#FROM openjdk:17-alpine
#WORKDIR app
#COPY . /app/
#RUN ./mvnw clean package
#EXPOSE 8080
#CMD ["java", "-jar", "/app/target/wine-store-app-0.0.1-SNAPSHOT.jar"]


#var1 start
## Используйте официальный образ OpenJDK
FROM openjdk:17-alpine
WORKDIR /usr/src/application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8080
CMD ["java", "-jar", "application.jar"]
#var1 end