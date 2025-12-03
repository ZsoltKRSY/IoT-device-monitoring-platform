FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /users/pom.xml
WORKDIR /users
RUN mvn dependency:go-offline
COPY ./src/ /users/src
WORKDIR /users
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /users/target/users-0.0.1-SNAPSHOT.jar /app/users.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "users.jar"]
