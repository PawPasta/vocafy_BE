package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class FeedbackReplyRequest(
    @JsonProperty("admin_reply")
    @field:NotBlank(message = "'admin_reply' can't be blank")
    @field:Size(max = 5000, message = "'admin_reply' max length is 5000")
    val adminReply: String? = null,
)
