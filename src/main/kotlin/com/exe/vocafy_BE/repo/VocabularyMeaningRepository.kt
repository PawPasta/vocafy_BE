package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VocabularyMeaningRepository : JpaRepository<VocabularyMeaning, Long> {
    fun findAllByVocabularyIdOrderBySenseOrderAscIdAsc(vocabId: Long): List<VocabularyMeaning>
    fun deleteAllByVocabularyId(vocabId: Long)

    @Query(
        value = "select unique_id from vocabulary_meanings where unique_id <> :excludeId order by rand() limit :limit",
        nativeQuery = true,
    )
    fun findRandomIdsExclude(
        @Param("excludeId") excludeId: Long,
        @Param("limit") limit: Int,
    ): List<Long>

    @Query(
        value = """
            select unique_id
            from vocabulary_meanings
            where unique_id <> :excludeId and language_code = :languageCode
            order by rand()
            limit :limit
        """,
        nativeQuery = true,
    )
    fun findRandomIdsExcludeAndLanguageCode(
        @Param("excludeId") excludeId: Long,
        @Param("languageCode") languageCode: String,
        @Param("limit") limit: Int,
    ): List<Long>
}
