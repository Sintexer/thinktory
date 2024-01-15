plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.google.devtools.ksp")
}

group = "com.mibe.thinktory"
version = "0.0.1-SNAPSHOT"
val kotestVersion by project.properties

dependencies {
    implementation(project(":thinktory-service"))

    implementation("eu.vendeli:telegram-bot:3.3.1")
    implementation("org.reflections:reflections:0.10.2")

    implementation("org.springframework.boot:spring-boot-starter")

    implementation("dev.nesk.akkurate:akkurate-core")
    implementation("dev.nesk.akkurate:akkurate-ksp-plugin")
    ksp("dev.nesk.akkurate:akkurate-ksp-plugin")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}

