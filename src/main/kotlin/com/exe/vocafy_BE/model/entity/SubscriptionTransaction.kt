package com.exe.vocafy_BE.model.entity

import com.exe.vocafy_BE.enum.SubscriptionTransactionStatus
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
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "subscription_transactions")
class SubscriptionTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "unique_id", nullable = false, unique = true)
    val id: UUID? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "unique_id", nullable = false)
    val user: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_method_id", referencedColumnName = "unique_id", nullable = false)
    val paymentMethod: PaymentMethod,

    @Column(name = "amount", nullable = false)
    val amount: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    val status: SubscriptionTransactionStatus,

    @Column(name = "note", columnDefinition = "TEXT")
    val note: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,
)
