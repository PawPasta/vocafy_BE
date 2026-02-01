package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty

data class SyllabusTopicLinkRequest(
    @JsonProperty("topic_ids")
    @field:NotEmpty(message = "'topic_ids' can't be empty")
    val topicIds: List<Long>,
)
