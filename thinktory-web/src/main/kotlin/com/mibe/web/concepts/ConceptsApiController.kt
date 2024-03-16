package com.mibe.web.concepts

import com.mibe.web.apis.ConceptsApi
import com.mibe.web.apis.ConceptsApiDelegate
import org.springframework.web.bind.annotation.RestController

@RestController
class ConceptsApiController(val conceptsApiDelegate: ConceptsApiDelegate) : ConceptsApi