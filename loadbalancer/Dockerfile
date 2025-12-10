FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /loadbalancer/pom.xml
WORKDIR /loadbalancer
RUN mvn dependency:go-offline
COPY ./src/ /loadbalancer/src
WORKDIR /loadbalancer
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /loadbalancer/target/loadbalancer-0.0.1-SNAPSHOT.jar /app/loadbalancer.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "loadbalancer.jar"]
