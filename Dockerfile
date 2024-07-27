FROM openjdk:17-jdk-slim

ARG JAR_FILE=build/libs/*.jar
ARG PROFILES
ARG ENV
ARG JASYPT_KEY

WORKDIR /app

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILES}", "-Dserver.env=${ENV}", "-Dencryptor.key=${JASYPT_KEY}", "-jar", "app.jar"]