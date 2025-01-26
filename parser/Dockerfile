FROM maven:3.9-eclipse-temurin-22-alpine AS build
WORKDIR /usr/app
COPY . /usr/app
RUN mvn package -DskipTests

FROM eclipse-temurin:22
WORKDIR /usr/app
COPY --from=build /usr/app/target/*.jar /usr/app/app.jar
CMD ["java", "-jar", "/usr/app/app.jar"]