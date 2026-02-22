FROM gradle:8.7-jdk21 AS builder
WORKDIR /app

# Копируем только необходимое
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

# Используем gradle из образа, а не wrapper
RUN gradle build -x test --no-daemon --stacktrace

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]




