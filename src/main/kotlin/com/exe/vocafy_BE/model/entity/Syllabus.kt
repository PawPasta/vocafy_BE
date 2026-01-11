package com.exe.vocafy_BE.model.entity

import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
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
@Table(name = "syllabus")
class Syllabus(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @Column(name = "title", nullable = false, length = 200)
    val title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "total_days", nullable = false)
    val totalDays: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "language_set", nullable = false, length = 20)
    val languageSet: LanguageSet,

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 10)
    val visibility: SyllabusVisibility = SyllabusVisibility.PUBLIC,

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    val sourceType: SyllabusSourceType,

    @Column(name = "active", nullable = false)
    val active: Boolean = true,

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "unique_id")
    val createdBy: User? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
