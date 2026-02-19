package com.exe.vocafy_BE.util

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SecurityUtil(
    private val userRepository: UserRepository,
) {
    fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val jwt = authentication.principal as? Jwt
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val subject = jwt.subject ?: throw BaseException.BadRequestException("Invalid user_id")
        val parsed = runCatching { UUID.fromString(subject) }.getOrNull()
        if (parsed != null) {
            return userRepository.findById(parsed)
                .orElseThrow { BaseException.NotFoundException("User not found") }
        }
        return userRepository.findByEmail(subject)
            ?: throw BaseException.NotFoundException("User not found")
    }

    fun getCurrentUserId(): UUID {
        return getCurrentUser().id ?: throw BaseException.NotFoundException("User not found")
    }

    fun getCurrentUserIdOrNull(): UUID? {
        val authentication = SecurityContextHolder.getContext().authentication ?: return null
        val jwt = authentication.principal as? Jwt ?: return null
        val subject = jwt.subject ?: return null
        val parsed = runCatching { UUID.fromString(subject) }.getOrNull()
        if (parsed != null) {
            return parsed
        }
        return userRepository.findByEmail(subject)?.id
    }

    fun getCurrentRole(): String? {
        val authentication = SecurityContextHolder.getContext().authentication ?: return null
        val jwt = authentication.principal as? Jwt ?: return null
        return jwt.getClaimAsString("role")
    }
}
