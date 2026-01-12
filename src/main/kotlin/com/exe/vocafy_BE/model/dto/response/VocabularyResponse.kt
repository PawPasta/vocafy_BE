package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class VocabularyResponse(
    val id: Long,
    @JsonProperty("jp_kanji")
    val jpKanji: String?,
    @JsonProperty("jp_kana")
    val jpKana: String?,
    @JsonProperty("jp_romaji")
    val jpRomaji: String?,
    @JsonProperty("en_word")
    val enWord: String?,
    @JsonProperty("en_ipa")
    val enIpa: String?,
    @JsonProperty("meaning_vi")
    val meaningVi: String?,
    @JsonProperty("meaning_en")
    val meaningEn: String?,
    @JsonProperty("meaning_jp")
    val meaningJp: String?,
    val note: String?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime?,
)
