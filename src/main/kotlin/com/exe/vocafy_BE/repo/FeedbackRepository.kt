package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Feedback
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FeedbackRepository : JpaRepository<Feedback, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: UUID, pageable: Pageable): Page<Feedback>
    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Feedback>
    fun countByRating(rating: Int): Long
}
