package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.UserDailyActivity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface UserDailyActivityRepository : JpaRepository<UserDailyActivity, Long> {
    fun findByUserIdAndActivityDate(userId: UUID, activityDate: LocalDate): UserDailyActivity?
    fun findTopByUserIdOrderByActivityDateDesc(userId: UUID): UserDailyActivity?
}
