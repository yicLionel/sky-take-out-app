FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY sky-common sky-common
COPY sky-pojo sky-pojo
COPY sky-server sky-server

RUN ./mvnw -pl sky-server -am -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/sky-server/target/sky-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
