package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseResponse <T> (
    val success: Boolean,
    val message: String,
    val result: T? = null,
)

object ResponseFactory {
    fun <T> success(result: ServiceResult<T>): BaseResponse<T> =
        BaseResponse(
            success = true,
            message = result.message,
            result = result.result,
        )

    fun <T> success(result: T, message: String = "Ok"): BaseResponse<T> =
        BaseResponse(
            success = true,
            message = message,
            result = result,
        )

    fun failed(message: String): BaseResponse<Nothing> =
        BaseResponse(
            success = false,
            message = message,
        )
}
