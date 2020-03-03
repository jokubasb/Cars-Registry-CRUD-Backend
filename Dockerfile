#
# Build stage
#
FROM maven:3.6.1-jdk-8-alpine AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:8-jre-alpine
COPY --from=build /home/app/target/lab1-0.0.1.jar /usr/local/lib/web-server.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/web-server.jar"]