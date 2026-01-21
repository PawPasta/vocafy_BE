package com.exe.vocafy_BE.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user_study_budget")
class UserStudyBudget(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "unique_id", nullable = false)
    val user: User,

    @Column(name = "daily_minutes", nullable = false)
    val dailyMinutes: Int,

    @Column(name = "daily_card_limit", nullable = false)
    val dailyCardLimit: Int,

    @Column(name = "used_cards_today", nullable = false)
    val usedCardsToday: Int = 0,

    @Column(name = "streak_count", nullable = false)
    val streakCount: Int = 0,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
