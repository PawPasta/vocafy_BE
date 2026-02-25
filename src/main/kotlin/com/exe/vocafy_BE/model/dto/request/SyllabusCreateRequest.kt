package com.exe.vocafy_BE.model.dto.request

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SyllabusCreateRequest(
    @field:NotBlank(message = "'title' can't be null")
    val title: String? = null,

    val description: String? = null,

    @JsonProperty("image_background")
    val imageBackGroud: String? = null,

    @JsonProperty("image_icon")
    val imageIcon: String? = null,

    @JsonProperty("total_days")
    @field:NotNull(message = "'total_days' can't be null")
    val totalDays: Int? = null,

    @JsonProperty("language_set")
    val languageSet: LanguageSet? = null,

    @JsonProperty("study_language")
    val studyLanguage: LanguageCode? = null,

    @JsonProperty("target_languages")
    val targetLanguages: List<LanguageCode>? = null,

    @field:NotNull(message = "'visibility' can't be null")
    val visibility: SyllabusVisibility? = null,

    @JsonProperty("source_type")
    @field:NotNull(message = "'source_type' can't be null")
    val sourceType: SyllabusSourceType? = null,

    @JsonProperty("created_by_user_id")
    val createdByUserId: String? = null,

    val active: Boolean? = null,

    @JsonProperty("topic_ids")
    val topicIds: List<Long>? = null,

    @JsonProperty("category_id")
    val categoryId: Long? = null
)
