package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleLoginRequest(
    @JsonProperty("id_token")
    val idToken: String? = null,
)
