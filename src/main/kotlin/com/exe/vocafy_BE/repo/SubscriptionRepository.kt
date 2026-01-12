package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SubscriptionRepository : JpaRepository<Subscription, UUID> {
    fun findByUserId(userId: UUID): Subscription?
}
