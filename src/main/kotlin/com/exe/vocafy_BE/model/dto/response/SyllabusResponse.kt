package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class SyllabusResponse(
    val id: Long,
    val title: String,
    val description: String?,
    @JsonProperty("image_background")
    val imageBackGroud: String?,
    @JsonProperty("image_icon")
    val imageIcon: String?,
    @JsonProperty("total_days")
    val totalDays: Int,
    @JsonProperty("language_set")
    val languageSet: LanguageSet,
    @JsonProperty("study_language")
    val studyLanguage: LanguageCode,
    @JsonProperty("target_languages")
    val targetLanguages: List<LanguageCode>,
    val visibility: SyllabusVisibility,
    @JsonProperty("source_type")
    val sourceType: SyllabusSourceType,
    @JsonProperty("created_by_user_id")
    val createdByUserId: String?,
    val active: Boolean?,
    @JsonProperty("is_deleted")
    val isDeleted: Boolean?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime?,
    val topics: List<SyllabusTopicResponse>? = null,
    @JsonProperty("category_name")
    val categoryName: String? = null
)
