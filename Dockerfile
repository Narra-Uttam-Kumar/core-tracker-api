# Stage 1: Build the application
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]