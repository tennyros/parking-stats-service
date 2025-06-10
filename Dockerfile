FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /build

WORKDIR /build/parking-stats-service
COPY /pom.xml /build/parking-stats-service
COPY /src /build/parking-stats-service/src
COPY /.env /build/parking-stats-service/
RUN mvn -B -f pom.xml clean package -DskipTests -Dspring.profiles.active=dev

FROM eclipse-temurin:17-jre-alpine

COPY --from=build /build/parking-stats-service/target/*.jar app.jar
COPY --from=build /build/parking-stats-service/.env ./

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]