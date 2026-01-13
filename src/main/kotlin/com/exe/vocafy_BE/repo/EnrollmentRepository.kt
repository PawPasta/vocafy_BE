package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Enrollment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EnrollmentRepository : JpaRepository<Enrollment, Long> {
    fun findByUserIdAndSyllabusId(userId: UUID, syllabusId: Long): Enrollment?
}
