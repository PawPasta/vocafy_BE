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
@Table(name = "topic")
class Topic(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(optional = true)
    @JoinColumn(name = "syllabus_id", referencedColumnName = "unique_id", nullable = true)
    val syllabus: Syllabus? = null,

    @Column(name = "title", nullable = false, length = 200)
    val title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "total_days", nullable = false)
    val totalDays: Int,

    @Column(name = "sort_order", nullable = false)
    val sortOrder: Int,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean = false,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
