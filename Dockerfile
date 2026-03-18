# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

COPY gradle /build/gradle
COPY gradlew /build/gradlew
COPY build.gradle.kts /build/build.gradle.kts
COPY settings.gradle.kts /build/settings.gradle.kts

RUN chmod +x /build/gradlew

RUN --mount=type=cache,target=/root/.gradle \
    /build/gradlew --no-daemon dependencies

COPY src /build/src

RUN --mount=type=cache,target=/root/.gradle \
    /build/gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-alpine

COPY --from=builder /build/build/libs/*.jar /app.jar

CMD ["java", "-jar", "/app.jar"]
