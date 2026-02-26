package com.exe.vocafy_BE.model.entity

import com.exe.vocafy_BE.enum.LanguageCode
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
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "vocabulary_examples",
    uniqueConstraints = [UniqueConstraint(columnNames = ["vocab_id", "language_code", "sort_order"])],
)
class VocabularyExample(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "vocab_id", referencedColumnName = "unique_id", nullable = false)
    val vocabulary: Vocabulary,

    @Enumerated(EnumType.STRING)
    @Column(name = "language_code", nullable = false, length = 10)
    val languageCode: LanguageCode,

    @Column(name = "sentence_text", columnDefinition = "TEXT", nullable = false)
    val sentenceText: String,

    @Column(name = "sort_order", nullable = false)
    val sortOrder: Int = 1,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
