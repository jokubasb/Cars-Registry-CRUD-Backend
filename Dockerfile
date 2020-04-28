FROM maven:latest AS build
COPY src /home/app/src
COPY pom.xml /home/app
WORKDIR /home/app
ENTRYPOINT ["mvn", "spring-boot:run"]


#RUN mvn clean install

#FROM openjdk:11-jre-slim
#COPY --from=build /home/app/target/lab1-0.0.1.jar /usr/local/lib/web.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/usr/local/lib/web.jar"]