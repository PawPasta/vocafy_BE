package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Syllabus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface SyllabusRepository : JpaRepository<Syllabus, Long> {
    fun findAllByActiveTrue(): List<Syllabus>
    fun findByIdAndActiveTrue(id: Long): Optional<Syllabus>
}
