package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Syllabus
import org.springframework.data.jpa.repository.JpaRepository

interface SyllabusRepository : JpaRepository<Syllabus, Long>
