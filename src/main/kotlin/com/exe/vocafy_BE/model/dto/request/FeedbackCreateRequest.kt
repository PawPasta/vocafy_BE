package com.exe.vocafy_BE.model.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class FeedbackCreateRequest(
    @field:NotNull(message = "'rating' can't be null")
    @field:Min(value = 1, message = "'rating' must be between 1 and 5")
    @field:Max(value = 5, message = "'rating' must be between 1 and 5")
    val rating: Int? = null,

    @field:Size(max = 150, message = "'title' max length is 150")
    val title: String? = null,

    @field:Size(max = 5000, message = "'content' max length is 5000")
    val content: String? = null,
)
