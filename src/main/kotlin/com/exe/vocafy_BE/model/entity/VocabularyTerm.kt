package com.exe.vocafy_BE.model.entity

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.ScriptType
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

@Entity
@Table(name = "vocabulary_terms")
class VocabularyTerm(
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

    @Enumerated(EnumType.STRING)
    @Column(name = "script_type", nullable = false, length = 20)
    val scriptType: ScriptType,

    @Column(name = "text_value", nullable = false, length = 255)
    val textValue: String,

    @Column(name = "extra_meta", columnDefinition = "JSON")
    val extraMeta: String? = null,
)
