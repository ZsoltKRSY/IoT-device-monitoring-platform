FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /devices/pom.xml
WORKDIR /devices
RUN mvn dependency:go-offline
COPY ./src/ /devices/src
WORKDIR /devices
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /devices/target/devices-0.0.1-SNAPSHOT.jar /app/devices.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "devices.jar"]
