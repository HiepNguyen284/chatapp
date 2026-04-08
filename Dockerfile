# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:21-jdk-alpine AS builder

RUN jlink --add-modules ALL-MODULE-PATH --output /jre --strip-debug --no-header-files --no-man-pages

WORKDIR /build

COPY gradle /build/gradle
COPY gradlew /build/gradlew
COPY build.gradle.kts /build/build.gradle.kts
COPY gradle.properties /build/settings.gradle.kts
COPY settings.gradle.kts /build/settings.gradle.kts

RUN chmod +x /build/gradlew

RUN --mount=type=cache,target=/root/.gradle /build/gradlew --no-daemon wrapper
RUN --mount=type=cache,target=/root/.gradle /build/gradlew --no-daemon dependencies

COPY src /build/src

RUN --mount=type=cache,target=/root/.gradle /build/gradlew --no-daemon bootJar

FROM alpine:3

WORKDIR /app

COPY --from=builder /jre /jre
COPY --from=builder /build/build/libs/*.jar /app.jar

ENTRYPOINT ["/jre/bin/java", "-jar", "/app.jar"]
