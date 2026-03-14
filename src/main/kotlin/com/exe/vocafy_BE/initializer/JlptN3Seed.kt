package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus

object JlptN3Seed : SyllabusSeedModule {
    override val key: String = "jlpt-n3"

    override fun seed(context: SyllabusSeedContext): Syllabus = with(context) {
        val n3Syllabus = syllabusRepository.save(
            Syllabus(
                title = "JLPT N3",
                description = "Upper-intermediate Japanese vocabulary for JLPT N3 level.",
                imageBackGroud = "https://apps.apple.com/vn/app/n3-jlpt-basic-edition/id6748867320?l=vi",
                imageIcon = "https://play.google.com/store/apps/details?id=com.ocoder.nguphap.tuvung.tiengnhat.japaness.n3",
                totalDays = 50,
                languageSet = LanguageSet.EN_JP_VI,
                studyLanguage = LanguageCode.JA,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
                category = generalCategory
            )
        )

        val n3TopicSeeds = listOf(
            TopicSeed(
                title = "Communication",
                description = "Polite forms, requests, and suggestions.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Polite Forms",
                        description = "Politeness and respect terms.",
                        vocabularies = listOf(
                            VocabSeed("丁寧", "ていねい", "polite", "polite", PartOfSpeech.ADJ),
                            VocabSeed("謙遜", "けんそん", "humility", "humility", PartOfSpeech.NOUN),
                            VocabSeed("尊敬", "そんけい", "respect", "respect", PartOfSpeech.NOUN),
                            VocabSeed("断る", "ことわる", "refuse", "to refuse", PartOfSpeech.VERB),
                            VocabSeed("了承", "りょうしょう", "acknowledgement", "consent/acknowledgement", PartOfSpeech.NOUN),
                            VocabSeed("配慮", "はいりょ", "consideration", "consideration", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Requests & Suggestions",
                        description = "Requests, suggestions, and advice.",
                        vocabularies = listOf(
                            VocabSeed("依頼", "いらい", "request", "request", PartOfSpeech.NOUN),
                            VocabSeed("提案", "ていあん", "proposal", "suggestion/proposal", PartOfSpeech.NOUN),
                            VocabSeed("助言", "じょげん", "advice", "advice", PartOfSpeech.NOUN),
                            VocabSeed("相談する", "そうだんする", "consult", "to consult", PartOfSpeech.VERB),
                            VocabSeed("許可", "きょか", "permission", "permission", PartOfSpeech.NOUN),
                            VocabSeed("同意", "どうい", "consent", "agreement/consent", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Daily Situations",
                description = "Public facilities and emergencies.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Public Facilities",
                        description = "Common public services.",
                        vocabularies = listOf(
                            VocabSeed("役所", "やくしょ", "government office", "city/ward office", PartOfSpeech.NOUN),
                            VocabSeed("保健所", "ほけんじょ", "public health center", "health center", PartOfSpeech.NOUN),
                            VocabSeed("図書館", "としょかん", "library", "library", PartOfSpeech.NOUN),
                            VocabSeed("税務署", "ぜいむしょ", "tax office", "tax office", PartOfSpeech.NOUN),
                            VocabSeed("手続き", "てつづき", "procedure", "procedure", PartOfSpeech.NOUN),
                            VocabSeed("申請", "しんせい", "application", "application", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Emergencies",
                        description = "Emergency situations and responses.",
                        vocabularies = listOf(
                            VocabSeed("救急", "きゅうきゅう", "first aid", "emergency/first aid", PartOfSpeech.NOUN),
                            VocabSeed("通報", "つうほう", "report", "to report to authorities", PartOfSpeech.NOUN),
                            VocabSeed("避難", "ひなん", "evacuation", "evacuation", PartOfSpeech.NOUN),
                            VocabSeed("火災", "かさい", "fire", "fire (disaster)", PartOfSpeech.NOUN),
                            VocabSeed("事故", "じこ", "accident", "accident", PartOfSpeech.NOUN),
                            VocabSeed("連絡先", "れんらくさき", "contact", "contact information", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(n3Syllabus, n3TopicSeeds)

        return n3Syllabus
    }
}
