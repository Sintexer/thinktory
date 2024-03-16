package com.mibe.thinktory.service.concept

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConceptRepository : JpaRepository<Concept, Long>