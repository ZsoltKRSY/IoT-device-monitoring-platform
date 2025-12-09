FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /websocket/pom.xml
WORKDIR /websocket
RUN mvn dependency:go-offline
COPY ./src/ /websocket/src
WORKDIR /websocket
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /websocket/target/websocket-0.0.1-SNAPSHOT.jar /app/websocket.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "websocket.jar"]
