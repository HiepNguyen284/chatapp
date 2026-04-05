plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.jpa") version "2.2.21"
    kotlin("plugin.lombok") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("io.freefair.lombok") version "9.2.0"
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.group4"
version = "0.0.1-SNAPSHOT"

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
}

kotlin.compilerOptions {
    freeCompilerArgs.addAll(
        "-Xjsr305=strict",
        "-Xannotation-default-target=param-property"
    )
}

sourceSets.main {
    java.srcDirs("src/main/java+kotlin")
}

repositories { mavenCentral() }

dependencies {

    implementation("com.openai:openai-java:4.30.0") {
        exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
    }

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    implementation("software.amazon.awssdk:s3:2.42.21")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    runtimeOnly("org.postgresql:postgresql")

    implementation("org.bouncycastle:bcprov-jdk18on:1.83")
    implementation("org.msgpack:jackson-dataformat-msgpack:0.9.11")

    implementation("com.google.firebase:firebase-admin:9.8.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
