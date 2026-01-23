package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    @JsonProperty("total_elements")
    val totalElements: Long,
    @JsonProperty("total_pages")
    val totalPages: Int,
    @JsonProperty("is_first")
    val isFirst: Boolean,
    @JsonProperty("is_last")
    val isLast: Boolean,
)

