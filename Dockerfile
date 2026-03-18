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

RUN mkdir -p /build/src/main/resources/certs/jwts && \
    openssl genrsa -out /build/src/main/resources/certs/jwts/keypair.pem 2048 && \
    openssl rsa -in /build/src/main/resources/certs/jwts/keypair.pem -pubout -out /build/src/main/resources/certs/jwts/public.pem && \
    openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in /build/src/main/resources/certs/jwts/keypair.pem -out /build/src/main/resources/certs/jwts/private.pem

RUN --mount=type=cache,target=/root/.gradle \
    /build/gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-alpine

COPY --from=builder /build/build/libs/*.jar /app.jar

CMD ["java", "-jar", "/app.jar"]
