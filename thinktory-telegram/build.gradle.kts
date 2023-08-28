plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

group = "com.mibe.thinktory"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("eu.vendeli:telegram-bot:3.0.3")
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
