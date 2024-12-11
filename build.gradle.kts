plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.github.therenegade"
version = "0.0.1-SNAPSHOT"

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
}

val jpaVersion = "3.3.4"
val lombokVersion = "1.18.34"
val junitVersion = "5.8.1"
val log4jVersion = "2.20.0"
val apacheCommonsVersion = "3.17.0"
val wiremockStandaloneVersion = "3.6.0"
val wiremockTestcontainersVersion = "1.0-alpha-13"
val testContainersVersion = "1.20.2"
val caffeineCacheVersion = "3.1.8"
val springDocVersion = "2.6.0"
val postgresqlVersion = "42.7.3"
val mapstructVersion = "1.6.2"

dependencies {
	// spring
	implementation("org.springframework.boot:spring-boot-starter-web")

	// validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// cache
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("com.github.ben-manes.caffeine:caffeine:$caffeineCacheVersion")

	// db
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.postgresql:postgresql:$postgresqlVersion")

	// migrations
	implementation("org.liquibase:liquibase-core")

	// kafka
	implementation("org.springframework.kafka:spring-kafka")

	// devtools
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// lombok
	compileOnly("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.projectlombok:lombok:$lombokVersion")

	// log4j2
	implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
	implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
	implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

	// mapstruct
	implementation("org.mapstruct:mapstruct:$mapstructVersion")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

	// swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")

	// utils
	implementation("org.apache.commons:commons-lang3:$apacheCommonsVersion")

	// tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.wiremock:wiremock-standalone:$wiremockStandaloneVersion")
	testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:$wiremockTestcontainersVersion")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
