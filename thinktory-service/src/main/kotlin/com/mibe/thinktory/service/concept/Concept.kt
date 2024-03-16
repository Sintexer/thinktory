package com.mibe.thinktory.service.concept

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

const val CONCEPT_SEQUENCE_NAME = "concepts_seq"

@Entity
@Table(name = "concepts")
@SequenceGenerator(name = CONCEPT_SEQUENCE_NAME, sequenceName = CONCEPT_SEQUENCE_NAME, allocationSize = 50)
data class Concept(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = CONCEPT_SEQUENCE_NAME)
    val id: Long = 0,

    val title: String,

    @Column(columnDefinition = "TEXT")
    val theory: String? = null,

    val labels: Set<String> = emptySet(),
)