package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.UserVocabProgress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.UUID

interface UserVocabProgressRepository : JpaRepository<UserVocabProgress, Long> {
    fun findAllByUserIdAndVocabularyIdIn(userId: UUID, vocabIds: List<Long>): List<UserVocabProgress>
    fun findByUserIdAndVocabularyId(userId: UUID, vocabularyId: Long): UserVocabProgress?

    @Query(
        value = """
            select vocab_id
            from user_vocab_progress
            where user_id = :userId and learning_state <> :state
            order by rand()
            limit :limit
        """,
        nativeQuery = true,
    )
    fun findRandomVocabIdsByUserIdAndLearningStateNot(
        @Param("userId") userId: UUID,
        @Param("state") state: Int,
        @Param("limit") limit: Int,
    ): List<Long>

    @Query(
        """
        select count(p)
        from UserVocabProgress p
        where p.user.id = :userId
          and p.createdAt >= :startTime
          and p.createdAt < :endTime
        """
    )
    fun countNewToday(
        @Param("userId") userId: UUID,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime,
    ): Long
}
