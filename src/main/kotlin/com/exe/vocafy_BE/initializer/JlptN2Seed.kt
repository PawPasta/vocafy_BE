package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus

object JlptN2Seed : SyllabusSeedModule {
    override val key: String = "jlpt-n2"

    override fun seed(context: SyllabusSeedContext): Syllabus = with(context) {
        val n2Syllabus = syllabusRepository.save(
            Syllabus(
                title = "JLPT N2",
                description = "Advanced Japanese vocabulary for JLPT N2 level.",
                imageBackGroud = "https://apps.apple.com/vn/app/n2-jlpt-basic-edition/id6748867093?l=vi",
                imageIcon = "https://play.google.com/store/apps/details?id=com.harusankaigo.kakomon.N2",
                totalDays = 60,
                languageSet = LanguageSet.EN_JP_VI,
                studyLanguage = LanguageCode.JA,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
                category = generalCategory
            )
        )

        val n2TopicSeeds = listOf(
            TopicSeed(
                title = "Business & News",
                description = "Business terminology and media language.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "Business Terms",
                        description = "Advanced business vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("企業", "きぎょう", "enterprise", "enterprise/company", PartOfSpeech.NOUN),
                            VocabSeed("景気", "けいき", "economy", "economic conditions", PartOfSpeech.NOUN),
                            VocabSeed("投資", "とうし", "investment", "investment", PartOfSpeech.NOUN),
                            VocabSeed("雇用", "こよう", "employment", "employment", PartOfSpeech.NOUN),
                            VocabSeed("需要", "じゅよう", "demand", "demand", PartOfSpeech.NOUN),
                            VocabSeed("供給", "きょうきゅう", "supply", "supply", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Media & News",
                        description = "Vocabulary in news reports.",
                        vocabularies = listOf(
                            VocabSeed("報道", "ほうどう", "reporting", "news reporting", PartOfSpeech.NOUN),
                            VocabSeed("世論", "よろん", "public opinion", "public opinion", PartOfSpeech.NOUN),
                            VocabSeed("速報", "そくほう", "breaking news", "news flash", PartOfSpeech.NOUN),
                            VocabSeed("取材", "しゅざい", "coverage", "news coverage/interview", PartOfSpeech.NOUN),
                            VocabSeed("編集", "へんしゅう", "editing", "editing", PartOfSpeech.NOUN),
                            VocabSeed("掲載", "けいさい", "publication", "publication", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Abstract Concepts",
                description = "Society, economy, and technology.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "Society & Economy",
                        description = "Abstract societal terms.",
                        vocabularies = listOf(
                            VocabSeed("格差", "かくさ", "disparity", "gap/disparity", PartOfSpeech.NOUN),
                            VocabSeed("平等", "びょうどう", "equality", "equality", PartOfSpeech.NOUN),
                            VocabSeed("福祉", "ふくし", "welfare", "social welfare", PartOfSpeech.NOUN),
                            VocabSeed("賛成", "さんせい", "approval", "support/approval", PartOfSpeech.NOUN),
                            VocabSeed("反対", "はんたい", "opposition", "opposition", PartOfSpeech.NOUN),
                            VocabSeed("規制", "きせい", "regulation", "regulation", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Technology",
                        description = "Tech-related advanced terms.",
                        vocabularies = listOf(
                            VocabSeed("革新", "かくしん", "innovation", "innovation", PartOfSpeech.NOUN),
                            VocabSeed("導入", "どうにゅう", "implementation", "introduction/implementation", PartOfSpeech.NOUN),
                            VocabSeed("普及", "ふきゅう", "diffusion", "spread/adoption", PartOfSpeech.NOUN),
                            VocabSeed("性能", "せいのう", "performance", "performance", PartOfSpeech.NOUN),
                            VocabSeed("互換性", "ごかんせい", "compatibility", "compatibility", PartOfSpeech.NOUN),
                            VocabSeed("最適化", "さいてきか", "optimization", "optimization", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(n2Syllabus, n2TopicSeeds)

        return n2Syllabus
    }
}
