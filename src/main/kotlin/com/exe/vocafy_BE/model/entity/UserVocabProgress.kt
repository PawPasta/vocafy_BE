package com.exe.vocafy_BE.model.entity

import com.exe.vocafy_BE.enum.LearningState
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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

    @Enumerated(EnumType.STRING)
    @Column(name = "learning_state", nullable = false, length = 20)
    val learningState: LearningState = LearningState.INTRODUCED,

    @Column(name = "correct_streak", nullable = false)
    val correctStreak: Short = 0,

    @Column(name = "last_rating")
    val lastRating: Short? = null,

    @Column(name = "last_studied_at")
    val lastStudiedAt: LocalDateTime? = null,

    @Column(name = "next_review_at")
    val nextReviewAt: LocalDateTime? = null,

    @Column(name = "total_attempts", nullable = false)
    val totalAttempts: Int = 0,

    @Column(name = "total_failures", nullable = false)
    val totalFailures: Int = 0,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
