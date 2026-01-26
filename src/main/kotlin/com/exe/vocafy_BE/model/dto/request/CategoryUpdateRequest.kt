package com.exe.vocafy_BE.model.dto.request

import jakarta.validation.constraints.NotBlank

data class CategoryUpdateRequest(
    @field:NotBlank(message = "'name' can't be null")
    val name: String? = null,

    val description: String? = null
)
