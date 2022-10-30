#
# Build stage
#
FROM maven:3.6.3-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=build /home/app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]