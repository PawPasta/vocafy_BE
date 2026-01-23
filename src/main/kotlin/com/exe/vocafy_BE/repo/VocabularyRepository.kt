package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Vocabulary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

interface VocabularyRepository : JpaRepository<Vocabulary, Long> {
    fun findAllByCourseIdOrderBySortOrderAscIdAsc(courseId: Long): List<Vocabulary>

    fun findAllByCourseId(courseId: Long, pageable: Pageable): Page<Vocabulary>

    @Modifying
    fun deleteAllByCourseId(courseId: Long)
}
