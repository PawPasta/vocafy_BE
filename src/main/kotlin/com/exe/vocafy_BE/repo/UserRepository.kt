package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findBySepayCode(sepayCode: String): User?
}
