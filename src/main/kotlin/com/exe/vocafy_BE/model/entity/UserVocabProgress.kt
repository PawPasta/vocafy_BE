package com.exe.vocafy_BE.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user_vocab_progress")
class UserVocabProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "unique_id", nullable = false)
    val user: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "vocab_id", referencedColumnName = "unique_id", nullable = false)
    val vocabulary: Vocabulary,

    @Column(name = "learning_state", nullable = false)
    val learningState: Int = com.exe.vocafy_BE.enum.LearningState.INTRODUCED.code,

    @Column(name = "exposure_count", nullable = false)
    val exposureCount: Int = 0,

    @Column(name = "last_exposed_at")
    val lastExposedAt: LocalDateTime? = null,

    @Column(name = "correct_streak", nullable = false)
    val correctStreak: Short = 0,

    @Column(name = "wrong_streak", nullable = false)
    val wrongStreak: Short = 0,

    @Column(name = "next_review_after")
    val nextReviewAfter: Int? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
