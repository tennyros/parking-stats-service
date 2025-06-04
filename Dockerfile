FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /build

WORKDIR /build/parking-management-service
COPY /pom.xml /build/parking-management-service
COPY /src /build/parking-management-service/src
COPY /.env /build/parking-management-service/
RUN mvn -B -f pom.xml clean package -DskipTests -Dspring.profiles.active=dev

FROM eclipse-temurin:17-jre-alpine

COPY --from=build /build/parking-management-service/target/*.jar app.jar
COPY --from=build /build/parking-management-service/.env ./

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]