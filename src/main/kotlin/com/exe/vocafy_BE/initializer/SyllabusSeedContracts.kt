package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.model.entity.Category
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.SyllabusRepository

data class VocabSeed(
    val jaKanji: String,
    val jaKana: String,
    val en: String,
    val meaning: String,
    val partOfSpeech: PartOfSpeech,
    val viMeaning: String? = null,
)

data class CourseSeed(
    val title: String,
    val description: String,
    val vocabularies: List<VocabSeed>,
)

data class TopicSeed(
    val title: String,
    val description: String,
    val totalDays: Int,
    val courses: List<CourseSeed>,
)

data class SyllabusSeedContext(
    val owner: User,
    val generalCategory: Category?,
    val businessCategory: Category?,
    val techCategory: Category?,
    val syllabusRepository: SyllabusRepository,
    val seedSyllabusContent: (Syllabus, List<TopicSeed>) -> Unit,
)

interface SyllabusSeedModule {
    val key: String
    fun seed(context: SyllabusSeedContext): Syllabus
}

object SyllabusSeedRegistry {
    val modules: List<SyllabusSeedModule> = listOf(
        JlptN5PublicSeed,
        JlptN5PrivateSeed,
        JlptN4Seed,
        BusinessJapaneseSeed,
        JlptN3Seed,
        JlptN2Seed,
        JlptN1Seed,
        SoftwareEngineeringSeed,
        AiEngineeringSeed,
        TestEngineeringSeed,
        DataEngineeringSeed,
        InternationalBusinessSeed,
    )
}
