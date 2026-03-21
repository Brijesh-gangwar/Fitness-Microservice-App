FROM maven:3.9.11-eclipse-temurin-24 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests clean package

FROM eclipse-temurin:24-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
