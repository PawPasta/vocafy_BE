package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

interface TopicRepository : JpaRepository<Topic, Long> {
    fun findAllBySyllabusIdOrderBySortOrderAsc(syllabusId: Long): List<Topic>

    @Modifying
    fun deleteAllBySyllabusId(syllabusId: Long)
}
