plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

group = "com.mibe.thinktory"
version = "0.0.1-SNAPSHOT"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
