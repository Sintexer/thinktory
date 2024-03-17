package com.mibe.web.error

import com.mibe.thinktory.service.NotFoundException
import dev.nesk.akkurate.ValidationResult
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ApiErrorHandler {
    @ExceptionHandler(value = [ApiException::class])
    fun onApiException(ex: ApiException, response: HttpServletResponse): Unit =
        response.sendError(ex.code, ex.message)

    @ExceptionHandler(value = [NotImplementedError::class])
    fun onNotImplemented(ex: NotImplementedError, response: HttpServletResponse): Unit =
        response.sendError(HttpStatus.NOT_IMPLEMENTED.value())

    @ExceptionHandler(value = [NotFoundException::class])
    fun onNotImplemented(ex: NotFoundException, response: HttpServletResponse): Unit =
        response.sendError(HttpStatus.NOT_FOUND.value())

    @ExceptionHandler(value = [ValidationResult.Exception::class])
    fun onConstraintViolation(ex: ValidationResult.Exception, response: HttpServletResponse): Unit =
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.violations.joinToString(", "))

}
