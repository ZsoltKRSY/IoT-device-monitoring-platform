FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /monitoring/pom.xml
WORKDIR /monitoring
RUN mvn dependency:go-offline
COPY ./src/ /monitoring/src
WORKDIR /monitoring
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /monitoring/target/monitoring-0.0.1-SNAPSHOT.jar /app/monitoring.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "monitoring.jar"]
