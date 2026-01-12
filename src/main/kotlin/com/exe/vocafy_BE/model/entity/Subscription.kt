package com.exe.vocafy_BE.model.entity

import com.exe.vocafy_BE.enum.SubscriptionPlan
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "subscriptions")
class Subscription(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "unique_id", nullable = false, unique = true)
    val id: UUID? = null,

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "unique_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 10)
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,

    @Column(name = "start_at")
    val startAt: LocalDate? = null,

    @Column(name = "end_at")
    val endAt: LocalDate? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
