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
@Table(name = "vocabularies")
class Vocabulary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @Column(name = "jp_kanji", length = 100)
    val jpKanji: String? = null,

    @Column(name = "jp_kana", length = 100)
    val jpKana: String? = null,

    @Column(name = "jp_romaji", length = 120)
    val jpRomaji: String? = null,

    @Column(name = "en_word", length = 120)
    val enWord: String? = null,

    @Column(name = "en_ipa", length = 120)
    val enIpa: String? = null,

    @Column(name = "meaning_vi", columnDefinition = "TEXT")
    val meaningVi: String? = null,

    @Column(name = "meaning_en", columnDefinition = "TEXT")
    val meaningEn: String? = null,

    @Column(name = "meaning_jp", columnDefinition = "TEXT")
    val meaningJp: String? = null,

    @Column(name = "note", columnDefinition = "TEXT")
    val note: String? = null,

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
