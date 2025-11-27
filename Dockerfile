FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/ogloszone-0.0.1-SNAPSHOT.jar app.jar

RUN ls -R /app


ENV JAVA_OPTS=""

ENV MONGODB_URI=""
ENV MAIL=""
ENV MAIL_PASSWORD=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar", "--spring.profiles.active=prod"]

EXPOSE 8080
