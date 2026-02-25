package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.model.entity.VocabularyExampleTranslation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VocabularyExampleTranslationRepository : JpaRepository<VocabularyExampleTranslation, Long> {
    fun findAllByVocabularyExampleIdOrderByIdAsc(vocabularyExampleId: Long): List<VocabularyExampleTranslation>
    fun findAllByVocabularyExampleIdInOrderByIdAsc(vocabularyExampleIds: List<Long>): List<VocabularyExampleTranslation>

    @Query(
        value = """
            select count(distinct ve.vocab_id)
            from vocabulary_examples ve
            join vocabulary_example_translations vet on vet.example_id = ve.unique_id
            where ve.vocab_id in (:vocabIds)
              and ve.is_active = true
              and vet.language_code = :languageCode
        """,
        nativeQuery = true,
    )
    fun countDistinctVocabularyIdsReadyForLanguage(
        @Param("vocabIds") vocabIds: List<Long>,
        @Param("languageCode") languageCode: String,
    ): Long

    fun findByVocabularyExampleIdAndLanguageCode(
        vocabularyExampleId: Long,
        languageCode: LanguageCode,
    ): VocabularyExampleTranslation?
}
