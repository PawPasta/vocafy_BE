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
@Table(name = "feedbacks")
class Feedback(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "unique_id", nullable = false)
    val user: User,

    @Column(name = "rating", nullable = false)
    val rating: Int,

    @Column(name = "title", length = 150)
    val title: String? = null,

    @Column(name = "content", columnDefinition = "TEXT")
    val content: String? = null,

    @Column(name = "admin_reply", columnDefinition = "TEXT")
    val adminReply: String? = null,

    @ManyToOne
    @JoinColumn(name = "replied_by_user_id", referencedColumnName = "unique_id")
    val repliedBy: User? = null,

    @Column(name = "replied_at")
    val repliedAt: LocalDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
