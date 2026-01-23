package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findBySepayCode(sepayCode: String): User?

    @Query("SELECT u.fcmToken FROM User u WHERE u.fcmToken IS NOT NULL AND u.status = :status")
    fun findAllFcmTokensByStatus(status: Status = Status.ACTIVE): List<String>

    @Query("SELECT u.fcmToken FROM User u WHERE u.fcmToken IS NOT NULL AND u.role = :role AND u.status = :status")
    fun findAllFcmTokensByRoleAndStatus(role: Role = Role.USER, status: Status = Status.ACTIVE): List<String>

    @Query("SELECT u.fcmToken FROM User u WHERE u.id IN :userIds AND u.fcmToken IS NOT NULL")
    fun findFcmTokensByUserIds(userIds: List<UUID>): List<String>
}
