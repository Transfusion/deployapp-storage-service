import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.12.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    id("maven-publish")
}

group = "io.github.transfusion"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/Transfusion/app-info-java-graalvm")
        credentials {
            username = project.findProperty("gpr.username") as String? ?: System.getenv("GPR_USERNAME")
            password = project.findProperty("gpr.pat") as String? ?: System.getenv("GPR_PAT")
        }
    }
}

extra["mockito.version"] = "4.11.0";

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.session:spring-session-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // mac silicon only https://github.com/netty/netty/issues/11020
    // https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/SystemUtils.java#L1173
    val isMacOS: Boolean = System.getProperty("os.name").startsWith("Mac OS X")
    val architecture = System.getProperty("os.arch").toLowerCase()
    if (isMacOS && architecture == "aarch64") {
        developmentOnly("io.netty:netty-resolver-dns-native-macos:4.1.72.Final:osx-aarch_64")
    }

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springdoc:springdoc-openapi-ui:1.6.9")
    implementation("org.mapstruct:mapstruct:1.5.0.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.0.Final")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    implementation("org.jobrunr:jobrunr-spring-boot-starter:5.3.0")
    implementation("org.apache.commons:commons-pool2:2.11.1")

    implementation("org.springframework.boot:spring-boot-starter-amqp")

    implementation(platform("software.amazon.awssdk:bom:2.17.247"))
    implementation("org.springdoc:springdoc-openapi-ui:1.6.9")
    implementation("software.amazon.awssdk:s3")

    implementation("io.github.transfusion:app-info-java-graalvm:0.1.0-SNAPSHOT")
    implementation("com.vladmihalcea:hibernate-types-55:2.19.2") // for direct storage of JsonNodes in JPA

    implementation("commons-io:commons-io:2.11.0")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-text
    implementation(group = "org.apache.commons", name = "commons-text", version = "1.10.0")
    // https://mvnrepository.com/artifact/commons-net/commons-net
    implementation(group = "commons-net", name = "commons-net", version = "3.9.0")

    // https://mvnrepository.com/artifact/org.springframework.retry/spring-retry
    implementation("org.springframework.retry:spring-retry:1.3.4")

    // https://stackoverflow.com/questions/67299161/mock-static-method-in-junit-5-using-mockito
    testImplementation("org.springframework.boot:spring-boot-starter-test") // already includes mockito

    // https://mvnrepository.com/artifact/com.h2database/h2
    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
