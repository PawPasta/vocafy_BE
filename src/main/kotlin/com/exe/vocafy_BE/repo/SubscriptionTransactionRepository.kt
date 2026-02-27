package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.SubscriptionTransaction
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SubscriptionTransactionRepository : JpaRepository<SubscriptionTransaction, UUID> {
    fun findTopByUserIdOrderByCreatedAtDesc(userId: UUID): SubscriptionTransaction?
}
