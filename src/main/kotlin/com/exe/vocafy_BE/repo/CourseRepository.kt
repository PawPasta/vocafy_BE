package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Course
import org.springframework.data.jpa.repository.JpaRepository

interface CourseRepository : JpaRepository<Course, Long> {
    fun findAllBySyllabusTopicIdOrderByIdAsc(syllabusTopicId: Long): List<Course>
}
