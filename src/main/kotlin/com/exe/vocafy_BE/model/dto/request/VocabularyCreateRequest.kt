package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class VocabularyCreateRequest(
    @JsonProperty("jp_kanji")
    val jpKanji: String? = null,

    @JsonProperty("jp_kana")
    val jpKana: String? = null,

    @JsonProperty("jp_romaji")
    val jpRomaji: String? = null,

    @JsonProperty("en_word")
    val enWord: String? = null,

    @JsonProperty("en_ipa")
    val enIpa: String? = null,

    @JsonProperty("meaning_vi")
    val meaningVi: String? = null,

    @JsonProperty("meaning_en")
    val meaningEn: String? = null,

    @JsonProperty("meaning_jp")
    val meaningJp: String? = null,

    val note: String? = null,

)
