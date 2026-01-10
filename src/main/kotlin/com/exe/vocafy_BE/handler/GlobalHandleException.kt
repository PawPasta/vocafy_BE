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
        val response = BaseResponse<Nothing>(
            statusCode = ex.statusCode,
            message = ex.message.ifBlank { "error" }
        )
        return ResponseEntity.status(ex.statusCode).body(response)
    }

    @ExceptionHandler(MissingTokenException::class)
    fun handleMissingToken(ex: MissingTokenException): ResponseEntity<BaseResponse<Nothing>> =
        ResponseEntity.badRequest().body(
            BaseResponse(
                statusCode = 400,
                message = "missing token",
            )
        )

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(ex: InvalidTokenException): ResponseEntity<BaseResponse<Nothing>> =
        ResponseEntity.status(401).body(
            BaseResponse(
                statusCode = 401,
                message = "invalid token",
            )
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<BaseResponse<Nothing>> {
        val details = ex.bindingResult.fieldErrors.joinToString("; ") {
            "${it.field}: ${it.defaultMessage ?: "invalid"}"
        }
        return ResponseEntity.badRequest().body(
            BaseResponse(
                statusCode = 400,
                message = if (details.isBlank()) "validation error" else details,
            )
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<BaseResponse<Nothing>> {
        val details = ex.constraintViolations.joinToString("; ") {
            "${it.propertyPath}: ${it.message}"
        }
        return ResponseEntity.badRequest().body(
            BaseResponse(
                statusCode = 400,
                message = if (details.isBlank()) "validation error" else details,
            )
        )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameter(ex: MissingServletRequestParameterException): ResponseEntity<BaseResponse<Nothing>> =
        ResponseEntity.badRequest().body(
            BaseResponse(
                statusCode = 400,
                message = "missing parameter: ${ex.parameterName}",
            )
        )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableBody(ex: HttpMessageNotReadableException): ResponseEntity<BaseResponse<Nothing>> =
        ResponseEntity.badRequest().body(
            BaseResponse(
                statusCode = 400,
                message = "invalid request body",
            )
        )

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(ex: DataIntegrityViolationException): ResponseEntity<BaseResponse<Nothing>> =
        ResponseEntity.status(409).body(
            BaseResponse(
                statusCode = 409,
                message = "data integrity violation",
            )
        )

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<BaseResponse<Nothing>> =
        ResponseEntity.status(401).body(
            BaseResponse(
                statusCode = 401,
                message = "unauthorized",
            )
        )

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<BaseResponse<Nothing>> =
        ResponseEntity.status(403).body(
            BaseResponse(
                statusCode = 403,
                message = "forbidden",
            )
        )

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(ex: Exception): ResponseEntity<BaseResponse<Nothing>> =
        ResponseEntity
            .status(500)
            .body(
                BaseResponse(
                    statusCode = 500,
                    message = ex.message?.ifBlank { "internal server error" } ?: "internal server error"
                )
            )
}
