package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.enum.SubscriptionTransactionStatus
import com.exe.vocafy_BE.model.entity.SubscriptionTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.UUID

interface SubscriptionTransactionRepository : JpaRepository<SubscriptionTransaction, UUID> {
    fun findTopByUserIdOrderByCreatedAtDesc(userId: UUID): SubscriptionTransaction?

    @Query("SELECT COALESCE(SUM(st.amount), 0) FROM SubscriptionTransaction st WHERE st.status = :status")
    fun sumAmountByStatus(status: SubscriptionTransactionStatus): Long?

    @Query(
        """
        SELECT COALESCE(SUM(st.amount), 0)
        FROM SubscriptionTransaction st
        WHERE st.status = :status
          AND st.createdAt >= :startAt
          AND st.createdAt < :endAt
        """,
    )
    fun sumAmountByStatusAndCreatedAtBetween(
        status: SubscriptionTransactionStatus,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
    ): Long?
}
