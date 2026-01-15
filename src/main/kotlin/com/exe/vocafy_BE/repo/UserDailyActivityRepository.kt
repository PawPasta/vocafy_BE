package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.UserDailyActivity
import org.springframework.data.jpa.repository.JpaRepository

interface UserDailyActivityRepository : JpaRepository<UserDailyActivity, Long>
