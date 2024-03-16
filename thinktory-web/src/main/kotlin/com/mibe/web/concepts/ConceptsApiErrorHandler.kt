package com.mibe.web.concepts

import com.mibe.web.error.ApiValidationException
import dev.nesk.akkurate.ValidationResult
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ConceptsApiErrorHandler {

    @ExceptionHandler
    fun handleValidationException(exception: ValidationResult.Exception) {
        throw ApiValidationException(exception)
    }

}
