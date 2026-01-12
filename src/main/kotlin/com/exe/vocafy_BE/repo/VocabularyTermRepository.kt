package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.VocabularyTerm
import org.springframework.data.jpa.repository.JpaRepository

interface VocabularyTermRepository : JpaRepository<VocabularyTerm, Long> {
    fun findAllByVocabularyIdOrderByIdAsc(vocabId: Long): List<VocabularyTerm>
    fun deleteAllByVocabularyId(vocabId: Long)
}
