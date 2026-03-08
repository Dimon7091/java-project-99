FROM gradle:8.7-jdk21 AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon
COPY src ./src

# Диагностика: проверяем наличие статических файлов
RUN echo "=== STATIC FILES ===" && \
    ls -la src/main/resources/static/ && \
    ls -la src/main/resources/static/assets/ && \
    echo "=== BUILDING JAR ==="

# Используем assemble вместо bootJar
RUN gradle clean assemble -x test --no-daemon --stacktrace

# Диагностика: проверяем содержимое JAR
RUN echo "=== JAR CONTENTS ===" && \
    jar tf /app/build/libs/*.jar | grep -E "static|assets|index|\.js|\.css" || echo "⚠️ No static files in JAR!"

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar

# Копируем статику напрямую (запасной вариант)
COPY --from=builder /app/src/main/resources/static /app/static

# Финальная проверка
RUN echo "=== FINAL CHECK ===" && \
    ls -la /app/static/ && \
    ls -la /app/static/assets/

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.web.resources.static-locations=classpath:/static/,file:/app/static/"]




