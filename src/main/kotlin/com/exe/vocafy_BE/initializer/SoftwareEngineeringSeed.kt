package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus

object SoftwareEngineeringSeed : SyllabusSeedModule {
    override val key: String = "software-engineering"

    override fun seed(context: SyllabusSeedContext): Syllabus = with(context) {
        val swEngSyllabus = syllabusRepository.save(
            Syllabus(
                title = "Software Engineering",
                description = "BE/FE/BA/Test domain vocabulary.",
                imageBackGroud = "https://luvina.net/top-it-companies-in-japan-best-tech-firms/",
                imageIcon = "https://apps.apple.com/vn/app/ti%E1%BA%BFng-nh%E1%BA%ADt-t%E1%BA%A1i-genba-it/id1601564315?l=vi",
                totalDays = 60,
                languageSet = LanguageSet.EN_JP_VI,
                studyLanguage = LanguageCode.JA,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
                category = techCategory
            )
        )

        val swEngTopicSeeds = listOf(
            TopicSeed(
                title = "Backend (BE)",
                description = "Server, API và dữ liệu.",
                totalDays = 12,
                courses = listOf(
                    CourseSeed(
                        title = "Database",
                        description = "Khái niệm cơ sở dữ liệu.",
                        vocabularies = listOf(
                            VocabSeed("スキーマ", "すきーま", "schema", "schema", PartOfSpeech.NOUN),
                            VocabSeed("テーブル", "てーぶる", "table", "table", PartOfSpeech.NOUN),
                            VocabSeed("主キー", "しゅきー", "primary key", "primary key", PartOfSpeech.NOUN),
                            VocabSeed("外部キー", "がいぶきー", "foreign key", "foreign key", PartOfSpeech.NOUN),
                            VocabSeed("インデックス", "いんでっくす", "index", "index", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "REST API",
                        description = "Thiết kế API và HTTP.",
                        vocabularies = listOf(
                            VocabSeed("エンドポイント", "えんどぽいんと", "endpoint", "endpoint", PartOfSpeech.NOUN),
                            VocabSeed("HTTP", "えいちてぃーてぃーぴー", "HTTP", "http", PartOfSpeech.NOUN),
                            VocabSeed("認証", "にんしょう", "authentication", "authentication", PartOfSpeech.NOUN),
                            VocabSeed("認可", "にんか", "authorization", "authorization", PartOfSpeech.NOUN),
                            VocabSeed("ステータスコード", "すてーたすこーど", "status code", "status code", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Frontend (FE)",
                description = "UI và trình duyệt.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "HTML & CSS",
                        description = "Đánh dấu và style cơ bản.",
                        vocabularies = listOf(
                            VocabSeed("HTML", "えいちてぃーえむえる", "HTML", "markup language", PartOfSpeech.NOUN),
                            VocabSeed("CSS", "しーえすえす", "CSS", "stylesheet", PartOfSpeech.NOUN),
                            VocabSeed("タグ", "たぐ", "tag", "tag", PartOfSpeech.NOUN),
                            VocabSeed("クラス", "くらす", "class", "css class", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Business Analyst (BA)",
                description = "Phân tích yêu cầu.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Requirements",
                        description = "Khái niệm yêu cầu.",
                        vocabularies = listOf(
                            VocabSeed("要件", "ようけん", "requirements", "requirements", PartOfSpeech.NOUN),
                            VocabSeed("ユーザーストーリー", "ゆーざーすとーりー", "user story", "user story", PartOfSpeech.NOUN),
                            VocabSeed("受入基準", "うけいれきじゅん", "acceptance criteria", "acceptance criteria", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Testing (QA)",
                description = "Kiểm thử phần mềm.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Testing Types",
                        description = "Các loại kiểm thử.",
                        vocabularies = listOf(
                            VocabSeed("単体試験", "たんたいしけん", "unit test", "unit test", PartOfSpeech.NOUN),
                            VocabSeed("結合試験", "けつごうしけん", "integration test", "integration test", PartOfSpeech.NOUN),
                            VocabSeed("回帰試験", "かいきしけん", "regression test", "regression test", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(swEngSyllabus, swEngTopicSeeds)

        return swEngSyllabus
    }
}
