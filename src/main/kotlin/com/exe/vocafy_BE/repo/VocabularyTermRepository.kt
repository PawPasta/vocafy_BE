package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.VocabularyTerm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VocabularyTermRepository : JpaRepository<VocabularyTerm, Long> {
    fun findAllByVocabularyIdOrderByIdAsc(vocabId: Long): List<VocabularyTerm>

    @Modifying
    fun deleteAllByVocabularyId(vocabId: Long)

    @Query(
        value = "select unique_id from vocabulary_terms where unique_id <> :excludeId order by rand() limit :limit",
        nativeQuery = true,
    )
    fun findRandomIdsExclude(
        @Param("excludeId") excludeId: Long,
        @Param("limit") limit: Int,
    ): List<Long>

    @Query(
        value = """
            select unique_id
            from vocabulary_terms
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
