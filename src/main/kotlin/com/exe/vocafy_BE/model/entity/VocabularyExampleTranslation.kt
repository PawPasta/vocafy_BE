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
    name = "vocabulary_example_translations",
    uniqueConstraints = [UniqueConstraint(columnNames = ["example_id", "language_code"])],
)
class VocabularyExampleTranslation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "example_id", referencedColumnName = "unique_id", nullable = false)
    val vocabularyExample: VocabularyExample,

    @Enumerated(EnumType.STRING)
    @Column(name = "language_code", nullable = false, length = 10)
    val languageCode: LanguageCode,

    @Column(name = "translation_text", columnDefinition = "TEXT", nullable = false)
    val translationText: String,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
