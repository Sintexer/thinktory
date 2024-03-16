plugins {
    kotlin("jvm")
    id("org.openapi.generator")
    id("org.jetbrains.kotlin.plugin.spring")
    id("com.google.devtools.ksp")
}

group = "${project.group}.web"

val kotestVersion by project.properties
val mockkVersion by project.properties

dependencies {
    implementation(project(":thinktory-service"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

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


kotlin {
    jvmToolchain(17)
}

val generatedApiRelativePath = "generate-resources/main"
val apiSpecRelativePath = "src/main/resources/api"

sourceSets {
    main {
        kotlin {
            srcDir("${buildDir}/$generatedApiRelativePath/src/main")
            srcDir("${buildDir}/$generatedApiRelativePath/src/main/kotlin")
        }
    }
}

tasks.named("openApiGenerate") {
    dependsOn("clean", ":thinktory-web:kspKotlin")
}

openApiGenerate {
    generatorName.set("kotlin-spring")

    inputSpec.set("${project.projectDir}/$apiSpecRelativePath/openapi.yaml")

    packageName.set("$group")
    groupId.set("$group")
    configOptions.set(mapOf(
        "interfaceOnly" to "true",
        "delegatePattern" to "true",
        "dateLibrary" to "java8",
        "useSpringBoot3" to "true",
        "exceptionHandler" to "false"
    ))
}

openApiValidate {
    inputSpec.set("${project.projectDir}/$apiSpecRelativePath/openapi.yaml")
}

tasks.named("compileKotlin") {
    dependsOn("openApiGenerate")
}
