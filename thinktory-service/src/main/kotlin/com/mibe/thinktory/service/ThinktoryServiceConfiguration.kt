package com.mibe.thinktory.service

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(basePackageClasses = [ServicePackageScanMarker::class])
@Configuration
class ThinktoryServiceConfiguration {
}