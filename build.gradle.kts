import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    id("org.openapi.generator") version "7.4.0"
}

allprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")

    group = "com.mibe"
    version = project.version

    java {
        sourceCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencyManagement {
        dependencies {
            dependency("dev.nesk.akkurate:akkurate-core:0.5.0")
            dependency("dev.nesk.akkurate:akkurate-ksp-plugin:0.5.0")
            dependency("dev.nesk.akkurate:akkurate-ksp-plugin:0.5.0")
        }
    }

}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

springBoot {
    mainClass.set("com.mibe.thinktory.telegram.ThinktoryBotApplication")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}