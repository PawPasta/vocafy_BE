package com.exe.vocafy_BE.handler

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.service.InvalidTokenException
import com.exe.vocafy_BE.service.MissingTokenException
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

@RestControllerAdvice
class GlobalHandleException {

    @ExceptionHandler(BaseException::class)
    fun handleCustomException(ex: BaseException): ResponseEntity<BaseResponse<Nothing>> {
        return error(ex.statusCode, ex.message.ifBlank { "error" })
    }

    @ExceptionHandler(MissingTokenException::class)
    fun handleMissingToken(ex: MissingTokenException): ResponseEntity<BaseResponse<Nothing>> =
        error(400, "missing token")

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(ex: InvalidTokenException): ResponseEntity<BaseResponse<Nothing>> =
        error(401, "invalid token")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<BaseResponse<Nothing>> {
        val details = ex.bindingResult.fieldErrors.joinToString("; ") {
            it.defaultMessage ?: "invalid"
        }
        return error(400, if (details.isBlank()) "validation error" else details)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<BaseResponse<Nothing>> {
        val details = ex.constraintViolations.joinToString("; ") {
            it.message
        }
        return error(400, if (details.isBlank()) "validation error" else details)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameter(ex: MissingServletRequestParameterException): ResponseEntity<BaseResponse<Nothing>> =
        error(400, "'${ex.parameterName}' can't be null")

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableBody(ex: HttpMessageNotReadableException): ResponseEntity<BaseResponse<Nothing>> =
        error(400, "invalid request body")

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(ex: DataIntegrityViolationException): ResponseEntity<BaseResponse<Nothing>> =
        error(409, "data integrity violation")

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<BaseResponse<Nothing>> =
        error(401, "unauthorized")

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<BaseResponse<Nothing>> =
        error(403, "forbidden")

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(ex: Exception): ResponseEntity<BaseResponse<Nothing>> =
        error(500, ex.message?.ifBlank { "internal server error" } ?: "internal server error")

    private fun error(statusCode: Int, message: String): ResponseEntity<BaseResponse<Nothing>> {
        val response = BaseResponse<Nothing>(
            success = false,
            message = capitalizeMessage(message),
        )
        return ResponseEntity.status(statusCode).body(response)
    }

    private fun capitalizeMessage(message: String): String {
        if (message.isEmpty()) {
            return message
        }
        val first = message[0]
        return if (first in 'a'..'z') {
            first.uppercaseChar() + message.substring(1)
        } else {
            message
        }
    }
}
