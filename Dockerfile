FROM maven:3.6.1-jdk-8-alpine AS build
COPY src /home/app/src
COPY pom.xml /home/app
EXPOSE 8080
ENTRYPOINT ["mvn", "-f", "/home/app/pom.xml", "spring-boot:run"]