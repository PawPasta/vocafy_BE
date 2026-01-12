package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.VocabularyMedia
import org.springframework.data.jpa.repository.JpaRepository

interface VocabularyMediaRepository : JpaRepository<VocabularyMedia, Long> {
    fun findAllByVocabularyIdOrderByIdAsc(vocabId: Long): List<VocabularyMedia>
    fun deleteAllByVocabularyId(vocabId: Long)
}
