package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Vocabulary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VocabularyRepository : JpaRepository<Vocabulary, Long> {
    fun findAllByCreatedById(createdById: UUID, pageable: Pageable): Page<Vocabulary>
}
