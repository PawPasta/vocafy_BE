package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Course
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CourseRepository : JpaRepository<Course, Long> {
    fun findAllBySyllabusTopicIdOrderByIdAsc(syllabusTopicId: Long): List<Course>

    fun findAllBySyllabusTopicId(syllabusTopicId: Long, pageable: Pageable): Page<Course>

    @Modifying
    fun deleteAllBySyllabusTopicId(syllabusTopicId: Long)

    @Query(
        """
        select c
        from Course c
        join c.syllabusTopic t
        where t.syllabus.id = :syllabusId
        order by t.sortOrder asc, c.sortOrder asc, c.id asc
        """
    )
    fun findAllBySyllabusIdOrderByTopicSortOrderAscCourseSortOrderAscIdAsc(
        @Param("syllabusId") syllabusId: Long,
    ): List<Course>
}
