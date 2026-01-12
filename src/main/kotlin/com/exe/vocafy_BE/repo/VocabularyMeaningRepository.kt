package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import org.springframework.data.jpa.repository.JpaRepository

interface VocabularyMeaningRepository : JpaRepository<VocabularyMeaning, Long> {
    fun findAllByVocabularyIdOrderBySenseOrderAscIdAsc(vocabId: Long): List<VocabularyMeaning>
    fun deleteAllByVocabularyId(vocabId: Long)
}
