package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.VocabularyExample
import org.springframework.data.jpa.repository.JpaRepository

interface VocabularyExampleRepository : JpaRepository<VocabularyExample, Long> {
    fun findAllByVocabularyIdOrderBySortOrderAscIdAsc(vocabularyId: Long): List<VocabularyExample>
}
