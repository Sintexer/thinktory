plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

group = "com.mibe.thinktory"
version = "0.0.1-SNAPSHOT"

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
