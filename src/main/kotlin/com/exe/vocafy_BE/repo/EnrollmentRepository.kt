package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Enrollment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface EnrollmentRepository : JpaRepository<Enrollment, Long> {
    fun findByUserIdAndSyllabusId(userId: UUID, syllabusId: Long): Enrollment?
    fun findByUserIdAndIsFocusedTrue(userId: UUID): Enrollment?
    fun findAllByUserIdOrderByStartDateDescIdDesc(userId: UUID): List<Enrollment>

    @Modifying
    @Query(
        """
        update Enrollment e
        set e.isFocused = false
        where e.user.id = :userId and e.isFocused = true
        """
    )
    fun clearFocused(@Param("userId") userId: UUID): Int
}
