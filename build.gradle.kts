plugins {
    id("java")
}

group = "com.epam.gymapp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-context:7.0.7")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")

    implementation("org.springframework:spring-web:7.0.7")
    implementation("org.springframework:spring-webmvc:7.0.7")
    implementation("jakarta.servlet:jakarta.servlet-api:6.1.0")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
    implementation("org.springframework.boot:spring-boot:4.0.0")
    implementation("org.springframework.boot:spring-boot-autoconfigure:4.0.0")

    implementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.22")

    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")

    implementation("org.hibernate:hibernate-core:7.3.5.Final")
    implementation("org.postgresql:postgresql:42.7.11")

    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    implementation("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")

    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    testCompileOnly("org.projectlombok:lombok:1.18.46")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.46")

    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")

    testImplementation("org.springframework:spring-test:7.0.7")

    testImplementation("org.assertj:assertj-core:3.27.7")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}