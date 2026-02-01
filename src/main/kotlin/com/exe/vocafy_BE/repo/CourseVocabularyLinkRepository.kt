package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.CourseVocabularyLink
import com.exe.vocafy_BE.model.entity.Vocabulary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CourseVocabularyLinkRepository : JpaRepository<CourseVocabularyLink, Long> {
    @Query(
        """
        select cv.vocabulary
        from CourseVocabularyLink cv
        where cv.course.id = :courseId
        order by cv.vocabulary.sortOrder asc, cv.vocabulary.id asc
        """
    )
    fun findVocabulariesByCourseId(@Param("courseId") courseId: Long): List<Vocabulary>

    @Query(
        value = """
        select cv.vocabulary
        from CourseVocabularyLink cv
        where cv.course.id = :courseId
        order by cv.vocabulary.sortOrder asc, cv.vocabulary.id asc
        """,
        countQuery = """
        select count(cv)
        from CourseVocabularyLink cv
        where cv.course.id = :courseId
        """
    )
    fun findVocabulariesByCourseId(@Param("courseId") courseId: Long, pageable: Pageable): Page<Vocabulary>

    fun findByCourseIdAndVocabularyId(courseId: Long, vocabularyId: Long): CourseVocabularyLink?

    fun findFirstByVocabularyIdOrderByIdAsc(vocabularyId: Long): CourseVocabularyLink?

    @Modifying
    fun deleteAllByCourseId(courseId: Long)

    @Modifying
    fun deleteAllByVocabularyId(vocabularyId: Long)

    @Modifying
    fun deleteByCourseIdAndVocabularyId(courseId: Long, vocabularyId: Long)
}
