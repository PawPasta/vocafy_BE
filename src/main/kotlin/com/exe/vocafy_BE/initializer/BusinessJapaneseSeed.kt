package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus

object BusinessJapaneseSeed : SyllabusSeedModule {
    override val key: String = "business-japanese"

    override fun seed(context: SyllabusSeedContext): Syllabus = with(context) {
        val businessSyllabus = syllabusRepository.save(
            Syllabus(
                title = "Business Japanese",
                description = "Essential Japanese vocabulary for business communication and professional settings.",
                imageBackGroud = "https://www.lingualift.com/blog/business-japanese-politeness-levels/",
                imageIcon = "https://dummyimage.com/100x100/000/fff&text=Biz",
                totalDays = 30,
                languageSet = LanguageSet.EN_JP_VI,
                studyLanguage = LanguageCode.JA,
                visibility = SyllabusVisibility.PUBLIC,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
                category = businessCategory
            )
        )

        val businessTopicSeeds = listOf(
            TopicSeed(
                title = "Business Etiquette",
                description = "Professional greetings and etiquette.",
                totalDays = 5,
                courses = listOf(
                    CourseSeed(
                        title = "Business Greetings",
                        description = "Formal greetings in business.",
                        vocabularies = listOf(
                            VocabSeed("お疲れ様です", "おつかれさまです", "good work", "thank you for your work", PartOfSpeech.INTERJ),
                            VocabSeed("ご苦労様です", "ごくろうさまです", "thanks for your effort", "thank you for your trouble", PartOfSpeech.INTERJ),
                            VocabSeed("失礼します", "しつれいします", "excuse me", "excuse me (entering/leaving)", PartOfSpeech.INTERJ),
                            VocabSeed("よろしくお願いします", "よろしくおねがいします", "please", "I look forward to working with you", PartOfSpeech.INTERJ),
                            VocabSeed("お世話になります", "おせわになります", "thank you", "thank you for your help", PartOfSpeech.INTERJ),
                            VocabSeed("申し訳ございません", "もうしわけございません", "I'm sorry", "I'm very sorry (formal)", PartOfSpeech.INTERJ),
                            VocabSeed("恐れ入ります", "おそれいります", "sorry to bother", "I'm sorry to trouble you", PartOfSpeech.INTERJ),
                            VocabSeed("かしこまりました", "かしこまりました", "understood", "certainly (formal)", PartOfSpeech.INTERJ),
                            VocabSeed("承知しました", "しょうちしました", "understood", "I understand (formal)", PartOfSpeech.INTERJ),
                            VocabSeed("ご連絡ください", "ごれんらくください", "please contact", "please contact me", PartOfSpeech.INTERJ),
                        ),
                    ),
                    CourseSeed(
                        title = "Business Cards",
                        description = "Exchanging business cards.",
                        vocabularies = listOf(
                            VocabSeed("名刺", "めいし", "business card", "business card", PartOfSpeech.NOUN),
                            VocabSeed("名刺交換", "めいしこうかん", "card exchange", "business card exchange", PartOfSpeech.NOUN),
                            VocabSeed("肩書き", "かたがき", "title", "job title", PartOfSpeech.NOUN),
                            VocabSeed("役職", "やくしょく", "position", "position", PartOfSpeech.NOUN),
                            VocabSeed("所属", "しょぞく", "affiliation", "affiliation/department", PartOfSpeech.NOUN),
                            VocabSeed("連絡先", "れんらくさき", "contact", "contact information", PartOfSpeech.NOUN),
                            VocabSeed("頂戴します", "ちょうだいします", "receive", "I humbly receive", PartOfSpeech.VERB),
                            VocabSeed("拝見します", "はいけんします", "look at", "I humbly look at", PartOfSpeech.VERB),
                            VocabSeed("初めまして", "はじめまして", "nice to meet you", "nice to meet you", PartOfSpeech.INTERJ),
                            VocabSeed("ご紹介", "ごしょうかい", "introduction", "introduction", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Meetings",
                description = "Vocabulary for business meetings.",
                totalDays = 6,
                courses = listOf(
                    CourseSeed(
                        title = "Meeting Basics",
                        description = "Basic meeting vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("会議", "かいぎ", "meeting", "meeting", PartOfSpeech.NOUN),
                            VocabSeed("打ち合わせ", "うちあわせ", "briefing", "meeting/briefing", PartOfSpeech.NOUN),
                            VocabSeed("議題", "ぎだい", "agenda", "agenda", PartOfSpeech.NOUN),
                            VocabSeed("議事録", "ぎじろく", "minutes", "meeting minutes", PartOfSpeech.NOUN),
                            VocabSeed("資料", "しりょう", "materials", "materials/documents", PartOfSpeech.NOUN),
                            VocabSeed("発表", "はっぴょう", "presentation", "presentation", PartOfSpeech.NOUN),
                            VocabSeed("提案", "ていあん", "proposal", "proposal", PartOfSpeech.NOUN),
                            VocabSeed("意見", "いけん", "opinion", "opinion", PartOfSpeech.NOUN),
                            VocabSeed("賛成", "さんせい", "agree", "agreement", PartOfSpeech.NOUN),
                            VocabSeed("反対", "はんたい", "oppose", "opposition", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Meeting Phrases",
                        description = "Useful meeting phrases.",
                        vocabularies = listOf(
                            VocabSeed("ご確認ください", "ごかくにんください", "please confirm", "please confirm", PartOfSpeech.INTERJ),
                            VocabSeed("ご検討ください", "ごけんとうください", "please consider", "please consider", PartOfSpeech.INTERJ),
                            VocabSeed("ご質問", "ごしつもん", "question", "question (polite)", PartOfSpeech.NOUN),
                            VocabSeed("ご意見", "ごいけん", "opinion", "opinion (polite)", PartOfSpeech.NOUN),
                            VocabSeed("まとめ", "まとめ", "summary", "summary", PartOfSpeech.NOUN),
                            VocabSeed("結論", "けつろん", "conclusion", "conclusion", PartOfSpeech.NOUN),
                            VocabSeed("決定", "けってい", "decision", "decision", PartOfSpeech.NOUN),
                            VocabSeed("延期", "えんき", "postpone", "postponement", PartOfSpeech.NOUN),
                            VocabSeed("中止", "ちゅうし", "cancel", "cancellation", PartOfSpeech.NOUN),
                            VocabSeed("参加", "さんか", "participate", "participation", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Email & Communication",
                description = "Email and business communication.",
                totalDays = 6,
                courses = listOf(
                    CourseSeed(
                        title = "Email Writing",
                        description = "Email vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("件名", "けんめい", "subject", "subject line", PartOfSpeech.NOUN),
                            VocabSeed("添付", "てんぷ", "attachment", "attachment", PartOfSpeech.NOUN),
                            VocabSeed("送信", "そうしん", "send", "send", PartOfSpeech.NOUN),
                            VocabSeed("受信", "じゅしん", "receive", "receive", PartOfSpeech.NOUN),
                            VocabSeed("返信", "へんしん", "reply", "reply", PartOfSpeech.NOUN),
                            VocabSeed("転送", "てんそう", "forward", "forward", PartOfSpeech.NOUN),
                            VocabSeed("CC", "しーしー", "CC", "carbon copy", PartOfSpeech.NOUN),
                            VocabSeed("宛先", "あてさき", "recipient", "recipient", PartOfSpeech.NOUN),
                            VocabSeed("署名", "しょめい", "signature", "signature", PartOfSpeech.NOUN),
                            VocabSeed("お忙しいところ", "おいそがしいところ", "sorry to bother", "despite your busy schedule", PartOfSpeech.INTERJ),
                        ),
                    ),
                    CourseSeed(
                        title = "Phone Etiquette",
                        description = "Phone conversation vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("電話", "でんわ", "phone", "telephone", PartOfSpeech.NOUN),
                            VocabSeed("内線", "ないせん", "extension", "extension number", PartOfSpeech.NOUN),
                            VocabSeed("外線", "がいせん", "outside line", "outside line", PartOfSpeech.NOUN),
                            VocabSeed("留守電", "るすでん", "voicemail", "voicemail", PartOfSpeech.NOUN),
                            VocabSeed("折り返し", "おりかえし", "callback", "callback", PartOfSpeech.NOUN),
                            VocabSeed("伝言", "でんごん", "message", "message", PartOfSpeech.NOUN),
                            VocabSeed("お待たせしました", "おまたせしました", "sorry for waiting", "sorry to keep you waiting", PartOfSpeech.INTERJ),
                            VocabSeed("少々お待ちください", "しょうしょうおまちください", "please wait", "please wait a moment", PartOfSpeech.INTERJ),
                            VocabSeed("お電話ください", "おでんわください", "please call", "please call me", PartOfSpeech.INTERJ),
                            VocabSeed("お掛け直しください", "おかけなおしください", "please call again", "please call back", PartOfSpeech.INTERJ),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Reports & Documents",
                description = "Document and report vocabulary.",
                totalDays = 5,
                courses = listOf(
                    CourseSeed(
                        title = "Documents",
                        description = "Document types.",
                        vocabularies = listOf(
                            VocabSeed("書類", "しょるい", "documents", "documents", PartOfSpeech.NOUN),
                            VocabSeed("報告書", "ほうこくしょ", "report", "report", PartOfSpeech.NOUN),
                            VocabSeed("企画書", "きかくしょ", "proposal", "proposal document", PartOfSpeech.NOUN),
                            VocabSeed("見積書", "みつもりしょ", "estimate", "estimate/quote", PartOfSpeech.NOUN),
                            VocabSeed("請求書", "せいきゅうしょ", "invoice", "invoice", PartOfSpeech.NOUN),
                            VocabSeed("領収書", "りょうしゅうしょ", "receipt", "receipt", PartOfSpeech.NOUN),
                            VocabSeed("申請書", "しんせいしょ", "application", "application form", PartOfSpeech.NOUN),
                            VocabSeed("承認", "しょうにん", "approval", "approval", PartOfSpeech.NOUN),
                            VocabSeed("却下", "きゃっか", "rejection", "rejection", PartOfSpeech.NOUN),
                            VocabSeed("提出", "ていしゅつ", "submission", "submission", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Report Writing",
                        description = "Report writing vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("目的", "もくてき", "purpose", "purpose", PartOfSpeech.NOUN),
                            VocabSeed("背景", "はいけい", "background", "background", PartOfSpeech.NOUN),
                            VocabSeed("方法", "ほうほう", "method", "method", PartOfSpeech.NOUN),
                            VocabSeed("結果", "けっか", "result", "result", PartOfSpeech.NOUN),
                            VocabSeed("考察", "こうさつ", "discussion", "discussion/analysis", PartOfSpeech.NOUN),
                            VocabSeed("課題", "かだい", "issue", "issue/task", PartOfSpeech.NOUN),
                            VocabSeed("対策", "たいさく", "countermeasure", "countermeasure", PartOfSpeech.NOUN),
                            VocabSeed("改善", "かいぜん", "improvement", "improvement", PartOfSpeech.NOUN),
                            VocabSeed("進捗", "しんちょく", "progress", "progress", PartOfSpeech.NOUN),
                            VocabSeed("達成", "たっせい", "achievement", "achievement", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Negotiations",
                description = "Negotiation and deal vocabulary.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Negotiation Terms",
                        description = "Negotiation vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("交渉", "こうしょう", "negotiation", "negotiation", PartOfSpeech.NOUN),
                            VocabSeed("条件", "じょうけん", "condition", "condition/terms", PartOfSpeech.NOUN),
                            VocabSeed("価格", "かかく", "price", "price", PartOfSpeech.NOUN),
                            VocabSeed("割引", "わりびき", "discount", "discount", PartOfSpeech.NOUN),
                            VocabSeed("納期", "のうき", "delivery date", "delivery date", PartOfSpeech.NOUN),
                            VocabSeed("保証", "ほしょう", "guarantee", "guarantee", PartOfSpeech.NOUN),
                            VocabSeed("取引", "とりひき", "transaction", "transaction/deal", PartOfSpeech.NOUN),
                            VocabSeed("妥協", "だきょう", "compromise", "compromise", PartOfSpeech.NOUN),
                            VocabSeed("合意", "ごうい", "agreement", "agreement", PartOfSpeech.NOUN),
                            VocabSeed("署名", "しょめい", "sign", "signature", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Contract Terms",
                        description = "Contract vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("契約", "けいやく", "contract", "contract", PartOfSpeech.NOUN),
                            VocabSeed("契約書", "けいやくしょ", "contract document", "contract document", PartOfSpeech.NOUN),
                            VocabSeed("期間", "きかん", "period", "period", PartOfSpeech.NOUN),
                            VocabSeed("更新", "こうしん", "renewal", "renewal", PartOfSpeech.NOUN),
                            VocabSeed("解約", "かいやく", "cancellation", "cancellation", PartOfSpeech.NOUN),
                            VocabSeed("違約金", "いやくきん", "penalty", "penalty fee", PartOfSpeech.NOUN),
                            VocabSeed("責任", "せきにん", "responsibility", "responsibility", PartOfSpeech.NOUN),
                            VocabSeed("義務", "ぎむ", "obligation", "obligation", PartOfSpeech.NOUN),
                            VocabSeed("権利", "けんり", "right", "right", PartOfSpeech.NOUN),
                            VocabSeed("秘密保持", "ひみつほじ", "confidentiality", "confidentiality", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(businessSyllabus, businessTopicSeeds)

        return businessSyllabus
    }
}
