package com.mibe.web

import com.mibe.thinktory.service.ServicePackageScanMarker
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [
    App::class,
    ServicePackageScanMarker::class
], scanBasePackages = [
    "com.mibe.web",
    "com.mibe.thinktory.service",
    "com.mibe.thinktory.service.concept"
])
@EntityScan(basePackageClasses = [
    ServicePackageScanMarker::class
])
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}