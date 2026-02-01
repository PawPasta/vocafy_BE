package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.SyllabusTopicLink
import com.exe.vocafy_BE.model.entity.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SyllabusTopicLinkRepository : JpaRepository<SyllabusTopicLink, Long> {
    @Query(
        """
        select st.topic
        from SyllabusTopicLink st
        where st.syllabus.id = :syllabusId
        order by st.topic.sortOrder asc, st.topic.id asc
        """
    )
    fun findTopicsBySyllabusId(@Param("syllabusId") syllabusId: Long): List<Topic>

    fun findBySyllabusIdAndTopicId(syllabusId: Long, topicId: Long): SyllabusTopicLink?

    fun findFirstByTopicIdOrderByIdAsc(topicId: Long): SyllabusTopicLink?

    @Modifying
    fun deleteAllBySyllabusId(syllabusId: Long)

    @Modifying
    fun deleteAllByTopicId(topicId: Long)

    @Modifying
    fun deleteBySyllabusIdAndTopicId(syllabusId: Long, topicId: Long)
}
