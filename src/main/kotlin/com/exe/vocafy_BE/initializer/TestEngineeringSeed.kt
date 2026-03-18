package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus

object TestEngineeringSeed : SyllabusSeedModule {
    override val key: String = "test-engineering"

    override fun seed(context: SyllabusSeedContext): Syllabus = with(context) {
        val testEngSyllabus = syllabusRepository.save(
            Syllabus(
                title = "Test Engineering",
                description = "Testing processes and automation vocabulary.",
                imageBackGroud = "https://www.designcrowd.ca/design/13586880",
                imageIcon = "https://www.dreamstime.com/tester-icon-trendy-design-style-tester-icon-isolated-white-background-tester-vector-icon-simple-modern-flat-symbol-image135735152",
                totalDays = 35,
                languageSet = LanguageSet.EN_JP_VI,
                studyLanguage = LanguageCode.JA,
                visibility = SyllabusVisibility.PUBLIC,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
                category = techCategory
            )
        )

        val testEngTopicSeeds = listOf(
            TopicSeed(
                title = "Testing Fundamentals",
                description = "Các khái niệm kiểm thử.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Artifacts",
                        description = "Tạo tác kiểm thử.",
                        vocabularies = listOf(
                            VocabSeed("テスト計画", "てすとけいかく", "test plan", "test plan", PartOfSpeech.NOUN),
                            VocabSeed("テスト設計", "てすとせっけい", "test design", "test design", PartOfSpeech.NOUN),
                            VocabSeed("テストケース", "てすとけーす", "test case", "test case", PartOfSpeech.NOUN),
                            VocabSeed("テストデータ", "てすとでーた", "test data", "test data", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Automation",
                description = "Tự động hóa kiểm thử.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Automation Basics",
                        description = "Khái niệm và công cụ.",
                        vocabularies = listOf(
                            VocabSeed("テスト自動化", "てすとじどうか", "test automation", "test automation", PartOfSpeech.NOUN),
                            VocabSeed("フレームワーク", "ふれーむわーく", "framework", "framework", PartOfSpeech.NOUN),
                            VocabSeed("レポート", "れぽーと", "report", "report", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(testEngSyllabus, testEngTopicSeeds)

        return testEngSyllabus
    }
}
