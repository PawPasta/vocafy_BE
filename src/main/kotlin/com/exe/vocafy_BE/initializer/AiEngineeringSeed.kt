package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus

object AiEngineeringSeed : SyllabusSeedModule {
    override val key: String = "ai-engineering"

    override fun seed(context: SyllabusSeedContext): Syllabus = with(context) {
        val aiEngSyllabus = syllabusRepository.save(
            Syllabus(
                title = "AI Engineering",
                description = "ML/DL/MLOps domain vocabulary.",
                imageBackGroud = "https://www.jafco.co.jp/english/portfolio/japan_ai/",
                imageIcon = "https://www.pinterest.com/pin/tees--503066220887810014/",
                totalDays = 45,
                languageSet = LanguageSet.EN_JP_VI,
                studyLanguage = LanguageCode.JA,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
                category = techCategory
            )
        )

        val aiEngTopicSeeds = listOf(
            TopicSeed(
                title = "ML Fundamentals",
                description = "Khái niệm học máy cơ bản.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "Supervised Learning",
                        description = "Các khái niệm chính.",
                        vocabularies = listOf(
                            VocabSeed("データセット", "でーたせっと", "dataset", "dataset", PartOfSpeech.NOUN),
                            VocabSeed("特徴量", "とくちょうりょう", "feature", "feature", PartOfSpeech.NOUN),
                            VocabSeed("ラベル", "らべる", "label", "label", PartOfSpeech.NOUN),
                            VocabSeed("損失関数", "そんしつかんすう", "loss function", "loss function", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Deep Learning",
                description = "Mạng nơ-ron và tối ưu.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "Neural Networks",
                        description = "Khái niệm DL cơ bản.",
                        vocabularies = listOf(
                            VocabSeed("ニューラルネットワーク", "にゅーらるねっとわーく", "neural network", "neural network", PartOfSpeech.NOUN),
                            VocabSeed("活性化関数", "かっせいかかんすう", "activation function", "activation function", PartOfSpeech.NOUN),
                            VocabSeed("勾配降下", "こうばいこうか", "gradient descent", "gradient descent", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "MLOps",
                        description = "Triển khai và vận hành mô hình.",
                        vocabularies = listOf(
                            VocabSeed("モデルデプロイ", "もでるでぷろい", "model deployment", "model deployment", PartOfSpeech.NOUN),
                            VocabSeed("監視", "かんし", "monitoring", "monitoring", PartOfSpeech.NOUN),
                            VocabSeed("パイプライン", "ぱいぷらいん", "pipeline", "pipeline", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(aiEngSyllabus, aiEngTopicSeeds)

        return aiEngSyllabus
    }
}
