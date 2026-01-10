package com.exe.vocafy_BE.model.dto.response

data class ServiceResult<T>(
    val message: String,
    val result: T,
)
