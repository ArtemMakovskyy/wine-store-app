## Stage 1: Build Stage
#FROM maven:3.8.4-openjdk-17 AS build
#WORKDIR /usr/src/application
#COPY . .
#RUN mvn clean install
#
## Stage 2: Production Stage
#FROM openjdk:17-alpine
#WORKDIR /usr/src/application
#COPY --from=build /usr/src/application/target/*.jar application.jar
#EXPOSE 8080
#CMD ["java", "-jar", "application.jar"]


#var1 start
## Используйте официальный образ OpenJDK
FROM openjdk:17-alpine
WORKDIR /usr/src/application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8080
CMD ["java", "-jar", "application.jar"]
#var1 end

#Var2 start
## Builder stage
#FROM openjdk:17-jdk-slim as builder
#WORKDIR application
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} application.jar
#RUN java -Djarmode=layertools -jar application.jar extract
#
## Final stage
#FROM openjdk:17-jdk-slim
#WORKDIR application
#COPY --from=builder application/dependencies/ ./
#COPY --from=builder application/spring-boot-loader/ ./
#COPY --from=builder application/snapshot-dependencies/ ./
#COPY --from=builder application/application/ ./
#ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
##ENV DOCKER_ENV=false
##ENTRYPOINT ["sh", "-c", "if [ \"$DOCKER_ENV\" = \"true\" ]; then java org.springframework.boot.loader.launch.JarLauncher; else echo \"Container is not set to start automatically.\"; fi"]
#EXPOSE 8080
#Var2 end

##---------------------
## Соберите приложение (убедитесь, что у вас установлен Apache Maven)
##mvn clean package
#
## Соберите Docker-образ
## docker build -t docker-image-mysql:v01 .
#
## docker run -p 8080:8080 docker-image-mysql:v01:v01
#docker tag controller-without-db:v01 artemmakivskyy/controller-without-db:v01
# docker push artemmakivskyy/artemmakivskyy/controller-without-db