plugins {
    java
    id("org.springframework.boot") version "3.5.11"
    id("io.spring.dependency-management") version "1.1.7"
    checkstyle
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"
description = "Менеджер задач"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    // Основные зависимости для веб-приложения
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")

    // База данных
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.h2database:h2:2.4.240")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Для разработки
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Маппер
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Json
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")

    // JWT (если используете)
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Тесты
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("net.datafaker:datafaker:2.5.4")
    implementation("org.instancio:instancio-junit:3.3.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
