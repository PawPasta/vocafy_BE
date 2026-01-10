package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseResponse <T> (
    val statusCode: Int,
    val message: String,
    val result: T? = null
)


