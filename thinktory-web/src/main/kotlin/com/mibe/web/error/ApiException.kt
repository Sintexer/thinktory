package com.mibe.web.error

import dev.nesk.akkurate.ValidationResult
import org.springframework.http.HttpStatus

sealed class ApiException(msg: String, val code: Int) : Exception(msg)

class NotFoundException(msg: String, code: Int = HttpStatus.NOT_FOUND.value()) : ApiException(msg, code)

class ApiValidationException(
    validationException: ValidationResult.Exception,
) : ApiException(
    validationException.violations.joinToString(", "),
    HttpStatus.BAD_REQUEST.value()
)
