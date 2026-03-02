package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class DashboardGrowthRatesResponse(
    @JsonProperty("user_growth_rate")
    val userGrowthRate: Double,

    @JsonProperty("syllabus_growth_rate")
    val syllabusGrowthRate: Double,

    @JsonProperty("vocabulary_growth_rate")
    val vocabularyGrowthRate: Double,

    @JsonProperty("active_enrollment_growth_rate")
    val activeEnrollmentGrowthRate: Double,

    @JsonProperty("revenue_growth_rate")
    val revenueGrowthRate: Double,

    @JsonProperty("year")
    val year: Int,

    @JsonProperty("month")
    val month: Int,
)
