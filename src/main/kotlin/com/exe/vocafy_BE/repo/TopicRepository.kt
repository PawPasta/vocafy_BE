package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Topic
import org.springframework.data.jpa.repository.JpaRepository

interface TopicRepository : JpaRepository<Topic, Long> {
    fun findAllBySyllabusIdOrderBySortOrderAsc(syllabusId: Long): List<Topic>
}
