import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.google.devtools.ksp")
}

group = "${project.group}.service"

val kotestVersion by project.properties
val mockkVersion by project.properties

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.flywaydb:flyway-core:10.10.0")
    implementation("org.postgresql:postgresql:42.7.3")

    implementation("dev.nesk.akkurate:akkurate-core")
    implementation("dev.nesk.akkurate:akkurate-ksp-plugin")
    ksp("dev.nesk.akkurate:akkurate-ksp-plugin")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

tasks.withType<BootJar> {
    enabled = false
}