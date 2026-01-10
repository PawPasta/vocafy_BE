package com.exe.vocafy_BE.hanler

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.response.BaseResponse

@RestControllerAdvice
class GlobalHandleException {

    @ExceptionHandler(BaseException::class)
    fun handleCustomException(ex: BaseException): ResponseEntity<BaseResponse<Nothing>> {
        val response = BaseResponse<Nothing>(
            statusCode = ex.statusCode,
            message = ex.message
        )
        return ResponseEntity.status(ex.statusCode).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(ex: Exception): ResponseEntity<BaseResponse<Nothing>> =
        ResponseEntity
            .status(500)
            .body(
                BaseResponse(
                    statusCode = 500,
                    message = "Internal Server Error"
                )
            )
}