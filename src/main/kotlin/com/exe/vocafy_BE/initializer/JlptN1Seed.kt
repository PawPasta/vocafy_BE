package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus

object JlptN1Seed : SyllabusSeedModule {
    override val key: String = "jlpt-n1"

    override fun seed(context: SyllabusSeedContext): Syllabus = with(context) {
        val n1Syllabus = syllabusRepository.save(
            Syllabus(
                title = "JLPT N1",
                description = "Advanced/proficient Japanese vocabulary for JLPT N1 level.",
                imageBackGroud = "https://apps.apple.com/vn/app/jlpt-test-n1-japanese-exam/id1572168848",
                imageIcon = "https://play.google.com/store/apps/details?id=com.harusankaigo.kakomon.N1",
                totalDays = 70,
                languageSet = LanguageSet.EN_JP_VI,
                studyLanguage = LanguageCode.JA,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
                category = generalCategory
            )
        )

        val n1TopicSeeds = listOf(
            TopicSeed(
                title = "Advanced Abstract",
                description = "Philosophy, logic, and policy.",
                totalDays = 12,
                courses = listOf(
                    CourseSeed(
                        title = "Philosophy & Logic",
                        description = "Highly abstract concepts.",
                        vocabularies = listOf(
                            VocabSeed("概念", "がいねん", "concept", "concept", PartOfSpeech.NOUN),
                            VocabSeed("仮説", "かせつ", "hypothesis", "hypothesis", PartOfSpeech.NOUN),
                            VocabSeed("論証", "ろんしょう", "proof", "logical proof", PartOfSpeech.NOUN),
                            VocabSeed("矛盾", "むじゅん", "contradiction", "contradiction", PartOfSpeech.NOUN),
                            VocabSeed("整合性", "せいごうせい", "consistency", "consistency", PartOfSpeech.NOUN),
                            VocabSeed("妥当性", "だとうせい", "validity", "validity", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Law & Policy",
                        description = "Legal and policy terms.",
                        vocabularies = listOf(
                            VocabSeed("立法", "りっぽう", "legislation", "legislation", PartOfSpeech.NOUN),
                            VocabSeed("行政", "ぎょうせい", "administration", "administration", PartOfSpeech.NOUN),
                            VocabSeed("司法", "しほう", "judiciary", "judicial branch", PartOfSpeech.NOUN),
                            VocabSeed("判例", "はんれい", "precedent", "case law", PartOfSpeech.NOUN),
                            VocabSeed("規範", "きはん", "norm", "norm/standard", PartOfSpeech.NOUN),
                            VocabSeed("権限", "けんげん", "authority", "authority/power", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Academic & Research",
                description = "Research and statistics vocabulary.",
                totalDays = 12,
                courses = listOf(
                    CourseSeed(
                        title = "Research",
                        description = "Academic research terms.",
                        vocabularies = listOf(
                            VocabSeed("調査", "ちょうさ", "survey", "investigation/survey", PartOfSpeech.NOUN),
                            VocabSeed("解析", "かいせき", "analysis", "analysis", PartOfSpeech.NOUN),
                            VocabSeed("検証", "けんしょう", "verification", "verification", PartOfSpeech.NOUN),
                            VocabSeed("仮定", "かてい", "assumption", "assumption", PartOfSpeech.NOUN),
                            VocabSeed("引用", "いんよう", "citation", "citation", PartOfSpeech.NOUN),
                            VocabSeed("結論", "けつろん", "conclusion", "conclusion", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Statistics",
                        description = "Statistical terms.",
                        vocabularies = listOf(
                            VocabSeed("平均", "へいきん", "average", "mean/average", PartOfSpeech.NOUN),
                            VocabSeed("分布", "ぶんぷ", "distribution", "distribution", PartOfSpeech.NOUN),
                            VocabSeed("標本", "ひょうほん", "sample", "sample", PartOfSpeech.NOUN),
                            VocabSeed("母集団", "ぼしゅうだん", "population", "population", PartOfSpeech.NOUN),
                            VocabSeed("相関", "そうかん", "correlation", "correlation", PartOfSpeech.NOUN),
                            VocabSeed("分散", "ぶんさん", "variance", "variance", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(n1Syllabus, n1TopicSeeds)

        return n1Syllabus
    }
}
