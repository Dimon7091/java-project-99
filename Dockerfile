FROM gradle:8.7-jdk21 AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon
COPY src ./src
RUN gradle bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]




