package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class DashboardMetricResponse(
    @JsonProperty("count")
    val count: Long,

    @JsonProperty("current_month_count")
    val currentMonthCount: Long,

    @JsonProperty("previous_month_count")
    val previousMonthCount: Long,

    @JsonProperty("growth_rate")
    val growthRate: Double,

    @JsonProperty("year")
    val year: Int,

    @JsonProperty("month")
    val month: Int,
)
