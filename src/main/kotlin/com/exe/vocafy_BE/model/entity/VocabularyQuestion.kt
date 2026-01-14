package com.exe.vocafy_BE.model.entity

import com.exe.vocafy_BE.enum.VocabularyQuestionType
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
@Table(name = "vocabulary_questions")
class VocabularyQuestion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "vocab_id", referencedColumnName = "unique_id", nullable = false)
    val vocabulary: Vocabulary,

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 50)
    val questionType: VocabularyQuestionType,

    @Column(name = "question_ref_id", nullable = false)
    val questionRefId: Long,

    @Column(name = "answer_ref_id", nullable = false)
    val answerRefId: Long,

    @Column(name = "difficulty_level", nullable = false)
    val difficultyLevel: Short = 1,

    @Column(name = "extra_meta", columnDefinition = "JSON")
    val extraMeta: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
