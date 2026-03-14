package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus

object DataEngineeringSeed : SyllabusSeedModule {
    override val key: String = "data-engineering"

    override fun seed(context: SyllabusSeedContext): Syllabus = with(context) {
        val dataEngSyllabus = syllabusRepository.save(
            Syllabus(
                title = "Data Engineering",
                description = "Modeling, ETL, Warehousing vocabulary.",
                imageBackGroud = "https://www.snowflake.com/en/why-snowflake/partners/all-partners/ntt-data-japan-corporation/",
                imageIcon = "https://www.dreamstime.com/print-image161175844",
                totalDays = 40,
                languageSet = LanguageSet.EN_JP_VI,
                studyLanguage = LanguageCode.JA,
                visibility = SyllabusVisibility.PUBLIC,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
                category = techCategory
            )
        )

        val dataEngTopicSeeds = listOf(
            TopicSeed(
                title = "Data Modeling",
                description = "Mô hình dữ liệu và tối ưu.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Modeling Basics",
                        description = "Khái niệm cốt lõi.",
                        vocabularies = listOf(
                            VocabSeed("スキーマ", "すきーま", "schema", "schema", PartOfSpeech.NOUN),
                            VocabSeed("正規化", "せいきか", "normalization", "normalization", PartOfSpeech.NOUN),
                            VocabSeed("ER図", "いーあーるず", "ER diagram", "er diagram", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "ETL Pipelines",
                description = "Luồng xử lý dữ liệu.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "ETL",
                        description = "Các bước ETL.",
                        vocabularies = listOf(
                            VocabSeed("抽出", "ちゅうしゅつ", "extract", "extract", PartOfSpeech.NOUN),
                            VocabSeed("変換", "へんかん", "transform", "transform", PartOfSpeech.NOUN),
                            VocabSeed("ロード", "ろーど", "load", "load", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Warehousing",
                description = "Kho dữ liệu và tối ưu truy vấn.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Warehouse Basics",
                        description = "Khái niệm kho dữ liệu.",
                        vocabularies = listOf(
                            VocabSeed("データウェアハウス", "でーたうぇあはうす", "data warehouse", "data warehouse", PartOfSpeech.NOUN),
                            VocabSeed("クエリ", "くえり", "query", "query", PartOfSpeech.NOUN),
                            VocabSeed("最適化", "さいてきか", "optimization", "optimization", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(dataEngSyllabus, dataEngTopicSeeds)

        return dataEngSyllabus
    }
}
