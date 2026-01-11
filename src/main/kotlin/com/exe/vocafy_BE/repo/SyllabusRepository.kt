package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.enum.SyllabusVisibility
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface SyllabusRepository : JpaRepository<Syllabus, Long> {
    fun findAllByActiveTrueAndVisibilityNot(visibility: SyllabusVisibility): List<Syllabus>
    fun findByIdAndActiveTrueAndVisibilityNot(id: Long, visibility: SyllabusVisibility): Optional<Syllabus>
}
