package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.UserStudyBudget
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserStudyBudgetRepository : JpaRepository<UserStudyBudget, Long> {
    fun findByUserId(userId: UUID): UserStudyBudget?
}
