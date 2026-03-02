package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.model.entity.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.UUID

interface SubscriptionRepository : JpaRepository<Subscription, UUID> {
    fun findByUserId(userId: UUID): Subscription?

    @Query(
        """
        SELECT COUNT(s)
        FROM Subscription s
        WHERE s.plan = :plan
          AND (s.startAt IS NULL OR s.startAt <= :targetDate)
          AND (s.endAt IS NULL OR s.endAt >= :targetDate)
        """,
    )
    fun countActiveByPlanAtDate(plan: SubscriptionPlan, targetDate: LocalDate): Long
}
