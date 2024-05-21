FROM ubuntu:latest
FROM maven:3.8.3-openjdk-17
LABEL authors="lutsewu"

WORKDIR /opt

COPY target/*.jar /opt/app.jar
ENTRYPOINT exec java  -jar app.jar