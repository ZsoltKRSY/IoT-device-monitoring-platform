FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /authentication/pom.xml
WORKDIR /authentication
RUN mvn dependency:go-offline
COPY ./src/ /authentication/src
WORKDIR /authentication
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /authentication/target/authentication-0.0.1-SNAPSHOT.jar /app/authentication.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "authentication.jar"]
