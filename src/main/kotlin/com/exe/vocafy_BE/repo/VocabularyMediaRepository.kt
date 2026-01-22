package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.VocabularyMedia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

interface VocabularyMediaRepository : JpaRepository<VocabularyMedia, Long> {
    fun findAllByVocabularyIdOrderByIdAsc(vocabId: Long): List<VocabularyMedia>

    @Modifying
    fun deleteAllByVocabularyId(vocabId: Long)
}
