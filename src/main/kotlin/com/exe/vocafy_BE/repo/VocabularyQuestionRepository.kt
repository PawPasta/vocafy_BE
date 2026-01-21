package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.VocabularyQuestion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VocabularyQuestionRepository : JpaRepository<VocabularyQuestion, Long> {
    @Query(
        value = """
            select *
            from vocabulary_questions
            where question_type not in ('LISTEN_SELECT_TERM', 'LOOK_IMAGE_SELECT_TERM')
            order by rand()
            limit 1
        """,
        nativeQuery = true,
    )
    fun findRandom(): VocabularyQuestion?
}
