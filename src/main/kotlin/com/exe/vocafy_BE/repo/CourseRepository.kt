package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Course
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CourseRepository : JpaRepository<Course, Long> {
    @Query(
        """
        select distinct tc.course
        from TopicCourseLink tc
        join SyllabusTopicLink st on st.topic = tc.topic
        where st.syllabus.id = :syllabusId
        order by st.topic.sortOrder asc, tc.course.sortOrder asc, tc.course.id asc
        """
    )
    fun findAllBySyllabusIdOrderByTopicSortOrderAscCourseSortOrderAscIdAsc(
        @Param("syllabusId") syllabusId: Long,
    ): List<Course>
}
