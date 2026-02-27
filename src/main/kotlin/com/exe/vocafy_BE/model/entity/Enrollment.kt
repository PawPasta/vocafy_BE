package com.exe.vocafy_BE.model.entity

import com.exe.vocafy_BE.enum.EnrollmentStatus
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
import java.time.LocalDate

@Entity
@Table(name = "enrollments")
class Enrollment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "unique_id", nullable = false)
    val user: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "syllabus_id", referencedColumnName = "unique_id", nullable = false)
    val syllabus: Syllabus,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    val status: EnrollmentStatus = EnrollmentStatus.ACTIVE,

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_target_language", length = 10)
    val preferredTargetLanguage: LanguageCode? = null,

    @Column(name = "is_focused", nullable = false)
    val isFocused: Boolean = false,
)
