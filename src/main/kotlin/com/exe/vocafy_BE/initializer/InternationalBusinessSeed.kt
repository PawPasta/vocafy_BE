package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus

object InternationalBusinessSeed : SyllabusSeedModule {
    override val key: String = "international-business"

    override fun seed(context: SyllabusSeedContext): Syllabus = with(context) {
        val ibSyllabus = syllabusRepository.save(
            Syllabus(
                title = "International Business",
                description = "Trade, logistics, and finance vocabulary.",
                imageBackGroud = "https://www.vecteezy.com/vector-art/48472875-traditional-japanese-bank-icon-for-finance-and-banking-graphics-ideal-for-representing-japanese-financial-institutions-and-banking-services",
                imageIcon = "https://pngtree.com/freepng/yen-icon-japanese-currency-symbol-coin-symbol-isolated-market-finance-vector_12544102.html",
                totalDays = 40,
                languageSet = LanguageSet.EN_JP_VI,
                studyLanguage = LanguageCode.JA,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
                category = businessCategory
            )
        )

        val ibTopicSeeds = listOf(
            TopicSeed(
                title = "Trade Specialist",
                description = "Trade terms, logistics, and finance.",
                totalDays = 12,
                courses = listOf(
                    CourseSeed(
                        title = "Trade Terms",
                        description = "Incoterms và thuật ngữ thương mại.",
                        vocabularies = listOf(
                            VocabSeed("輸出", "ゆしゅつ", "export", "export", PartOfSpeech.NOUN),
                            VocabSeed("輸入", "ゆにゅう", "import", "import", PartOfSpeech.NOUN),
                            VocabSeed("関税", "かんぜい", "tariff", "tariff", PartOfSpeech.NOUN),
                            VocabSeed("インコタームズ", "いんこたーむず", "Incoterms", "incoterms", PartOfSpeech.NOUN),
                            VocabSeed("FOB", "えふおーびー", "FOB", "free on board", PartOfSpeech.NOUN),
                            VocabSeed("CIF", "しーあいえふ", "CIF", "cost insurance freight", PartOfSpeech.NOUN),
                            VocabSeed("原産地証明", "げんさんちしょうめい", "certificate of origin", "certificate of origin", PartOfSpeech.NOUN),
                            VocabSeed("請求書", "せいきゅうしょ", "invoice", "invoice", PartOfSpeech.NOUN),
                            VocabSeed("梱包", "こんぽう", "packaging", "packaging", PartOfSpeech.NOUN),
                            VocabSeed("船荷証券", "ふなにしょうけん", "bill of lading", "bill of lading", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Logistics",
                        description = "Vận chuyển và chuỗi cung ứng.",
                        vocabularies = listOf(
                            VocabSeed("物流", "ぶつりゅう", "logistics", "logistics", PartOfSpeech.NOUN),
                            VocabSeed("サプライチェーン", "さぷらいちぇーん", "supply chain", "supply chain", PartOfSpeech.NOUN),
                            VocabSeed("在庫", "ざいこ", "inventory", "inventory", PartOfSpeech.NOUN),
                            VocabSeed("倉庫", "そうこ", "warehouse", "warehouse", PartOfSpeech.NOUN),
                            VocabSeed("配送", "はいそう", "delivery", "delivery", PartOfSpeech.NOUN),
                            VocabSeed("通関", "つうかん", "customs clearance", "customs clearance", PartOfSpeech.NOUN),
                            VocabSeed("フォワーダー", "ふぉわーだー", "forwarder", "freight forwarder", PartOfSpeech.NOUN),
                            VocabSeed("トラッキング", "とらっきんぐ", "tracking", "shipment tracking", PartOfSpeech.NOUN),
                            VocabSeed("リードタイム", "りーどたいむ", "lead time", "lead time", PartOfSpeech.NOUN),
                            VocabSeed("需要予測", "じゅようよそく", "demand forecasting", "demand forecasting", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Finance",
                        description = "Tài chính trong thương mại quốc tế.",
                        vocabularies = listOf(
                            VocabSeed("信用状", "しんようじょう", "letter of credit", "letter of credit", PartOfSpeech.NOUN),
                            VocabSeed("為替", "かわせ", "exchange", "foreign exchange", PartOfSpeech.NOUN),
                            VocabSeed("送金", "そうきん", "remittance", "remittance", PartOfSpeech.NOUN),
                            VocabSeed("保証", "ほしょう", "guarantee", "guarantee", PartOfSpeech.NOUN),
                            VocabSeed("利息", "りそく", "interest", "interest", PartOfSpeech.NOUN),
                            VocabSeed("為替レート", "かわせれーと", "exchange rate", "exchange rate", PartOfSpeech.NOUN),
                            VocabSeed("決済", "けっさい", "settlement", "settlement", PartOfSpeech.NOUN),
                            VocabSeed("融資", "ゆうし", "financing", "financing", PartOfSpeech.NOUN),
                            VocabSeed("資金調達", "しきんちょうたつ", "fundraising", "fundraising", PartOfSpeech.NOUN),
                            VocabSeed("信用調査", "しんようちょうさ", "credit check", "credit investigation", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(ibSyllabus, ibTopicSeeds)

        return ibSyllabus
    }
}
