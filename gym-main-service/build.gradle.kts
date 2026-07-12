plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
}
val springCloudVersion by extra("2025.1.2")

group = "com.epam.gymapp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot web / REST / embedded Tomcat
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // JPA + Hibernate
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Bean validation: @Valid, @NotBlank, @NotNull, etc.
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Actuator: /actuator/health, /actuator/metrics, etc.
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Prometheus metrics endpoint
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.auth0:java-jwt:4.5.2")

    // Dev Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // PostgreSQL driver
    runtimeOnly("org.postgresql:postgresql")

    // Swagger / OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // Circuit Breaker for inter-service communication
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

    // Eureka Client
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // Feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // Spring Boot tests: JUnit, Mockito, AssertJ, Spring Test, Security Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")

    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.test {
    useJUnitPlatform()
}