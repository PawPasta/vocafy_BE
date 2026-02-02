package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.TopicCourseLink
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TopicCourseLinkRepository : JpaRepository<TopicCourseLink, Long> {
    @Query(
        """
        select tc.course
        from TopicCourseLink tc
        where tc.topic.id = :topicId
        order by tc.course.sortOrder asc, tc.course.id asc
        """
    )
    fun findCoursesByTopicId(@Param("topicId") topicId: Long): List<Course>

    @Query(
        """
        select tc.course
        from TopicCourseLink tc
        join SyllabusTopicLink st on st.topic = tc.topic
        where st.syllabus.id = :syllabusId
        group by tc.course
        order by min(st.topic.sortOrder) asc, tc.course.sortOrder asc, tc.course.id asc
        """
    )
    fun findCoursesBySyllabusId(@Param("syllabusId") syllabusId: Long): List<Course>

    fun findByTopicIdAndCourseId(topicId: Long, courseId: Long): TopicCourseLink?

    fun findFirstByCourseIdOrderByIdAsc(courseId: Long): TopicCourseLink?

    @Modifying
    fun deleteAllByTopicId(topicId: Long)

    @Modifying
    fun deleteAllByCourseId(courseId: Long)

    @Modifying
    fun deleteByTopicIdAndCourseId(topicId: Long, courseId: Long)
}
