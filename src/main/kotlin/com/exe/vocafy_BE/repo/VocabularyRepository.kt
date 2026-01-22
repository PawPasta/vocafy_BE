package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Vocabulary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

interface VocabularyRepository : JpaRepository<Vocabulary, Long> {
    fun findAllByCourseIdOrderBySortOrderAscIdAsc(courseId: Long): List<Vocabulary>

    @Modifying
    fun deleteAllByCourseId(courseId: Long)
}
