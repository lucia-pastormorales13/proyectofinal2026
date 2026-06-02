FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY Backend/pom.xml .
COPY Backend/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
COPY frontend-thymeleaf ./frontend-thymeleaf
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
