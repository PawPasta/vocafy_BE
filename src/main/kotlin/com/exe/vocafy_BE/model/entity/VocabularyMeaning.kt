package com.exe.vocafy_BE.model.entity

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.PartOfSpeech
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
@Table(name = "vocabulary_meanings")
class VocabularyMeaning(
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

    @Column(name = "meaning_text", columnDefinition = "TEXT", nullable = false)
    val meaningText: String,

    @Column(name = "example_sentence", columnDefinition = "TEXT")
    val exampleSentence: String? = null,

    @Column(name = "example_translation", columnDefinition = "TEXT")
    val exampleTranslation: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "part_of_speech", nullable = false, length = 20)
    val partOfSpeech: PartOfSpeech,

    @Column(name = "sense_order")
    val senseOrder: Int? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
