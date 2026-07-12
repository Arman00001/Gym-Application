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
    // Eureka Discovery
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")

    // Dev Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Spring Boot tests: JUnit, Mockito, AssertJ, Spring Test, Security Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

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