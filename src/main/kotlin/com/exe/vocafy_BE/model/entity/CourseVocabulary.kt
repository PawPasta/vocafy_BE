package com.exe.vocafy_BE.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "course_vocabulary")
class CourseVocabulary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", referencedColumnName = "unique_id", nullable = false)
    val course: Course,

    @ManyToOne(optional = false)
    @JoinColumn(name = "vocab_id", referencedColumnName = "unique_id", nullable = false)
    val vocabulary: Vocabulary,

    @Column(name = "sort_order", nullable = false)
    val sortOrder: Int,
)
