package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Topic
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

interface TopicRepository : JpaRepository<Topic, Long> {
    fun findAllBySyllabusIdOrderBySortOrderAsc(syllabusId: Long): List<Topic>

    fun findAllBySyllabusId(syllabusId: Long, pageable: Pageable): Page<Topic>

    @Modifying
    fun deleteAllBySyllabusId(syllabusId: Long)
}
