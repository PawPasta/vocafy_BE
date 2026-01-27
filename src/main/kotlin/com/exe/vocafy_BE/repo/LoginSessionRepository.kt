package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.LoginSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface LoginSessionRepository : JpaRepository<LoginSession, Long> {
    fun findByAccessTokenAndExpiredFalse(accessToken: String): LoginSession?
    fun findByRefreshTokenAndExpiredFalse(refreshToken: String): LoginSession?

    @Modifying
    @Query(
        """
        update LoginSession ls
        set ls.expired = true
        where ls.user.id = :userId and ls.expired = false
        """
    )
    fun expireActiveSessions(@Param("userId") userId: UUID): Int

    @Modifying
    @Query(
        """
        update LoginSession ls
        set ls.expired = true
        where ls.id = :sessionId
        """
    )
    fun expireSession(@Param("sessionId") sessionId: Long): Int
}
