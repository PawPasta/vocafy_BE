package com.exe.vocafy_BE.handler

import com.exe.vocafy_BE.model.dto.response.BaseResponse
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global exception handler for the application.
 * This class only handles exception responses - all business logic exceptions
 * should be defined in BaseException.
 */
@RestControllerAdvice
class GlobalHandleException {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<BaseResponse<Nothing>> =
        buildResponse(ex.statusCode, ex.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<BaseResponse<Nothing>> =
        buildResponse(400, ex.bindingResult.fieldErrors.joinToString("; ") { it.defaultMessage ?: "Invalid" }.ifBlank { "Validation error" })

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<BaseResponse<Nothing>> =
        buildResponse(400, ex.constraintViolations.joinToString("; ") { it.message }.ifBlank { "Validation error" })

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameter(ex: MissingServletRequestParameterException): ResponseEntity<BaseResponse<Nothing>> =
        buildResponse(400, "'${ex.parameterName}' can't be null")

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableBody(ex: HttpMessageNotReadableException): ResponseEntity<BaseResponse<Nothing>> =
        buildResponse(400, "Invalid request body")

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(ex: DataIntegrityViolationException): ResponseEntity<BaseResponse<Nothing>> =
        buildResponse(409, "Data integrity violation")

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<BaseResponse<Nothing>> =
        buildResponse(401, "Unauthorized")

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<BaseResponse<Nothing>> =
        buildResponse(403, "Forbidden")

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(ex: Exception): ResponseEntity<BaseResponse<Nothing>> =
        buildResponse(500, ex.message?.ifBlank { "Internal server error" } ?: "Internal server error")

    private fun buildResponse(statusCode: Int, message: String): ResponseEntity<BaseResponse<Nothing>> =
        ResponseEntity.status(statusCode).body(
            BaseResponse(
                success = false,
                message = message.replaceFirstChar { it.uppercaseChar() },
            )
        )
}
