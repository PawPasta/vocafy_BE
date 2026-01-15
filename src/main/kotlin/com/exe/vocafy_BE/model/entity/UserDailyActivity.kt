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
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "user_daily_activity")
class UserDailyActivity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "unique_id", nullable = false)
    val user: User,

    @Column(name = "activity_date", nullable = false)
    val activityDate: LocalDate,

    @Column(name = "is_goal_completed", nullable = false)
    val isGoalCompleted: Boolean,

    @Column(name = "streak_snapshot", nullable = false)
    val streakSnapshot: Int,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
