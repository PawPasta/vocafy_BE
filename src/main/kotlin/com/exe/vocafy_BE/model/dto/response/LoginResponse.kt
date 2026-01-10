package com.exe.vocafy_BE.model.dto.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)
