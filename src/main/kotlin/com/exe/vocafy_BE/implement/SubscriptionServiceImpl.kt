package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.SubscriptionMapper
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.SubscriptionResponse
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.service.SubscriptionService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SubscriptionServiceImpl(
    private val subscriptionRepository: SubscriptionRepository,
) : SubscriptionService {

    @Transactional(readOnly = true)
    override fun getMe(): ServiceResult<SubscriptionResponse> {
        val userId = currentUserId()
        val subscription = subscriptionRepository.findByUserId(userId)
            ?: throw BaseException.NotFoundException("Subscription not found")
        return ServiceResult(
            message = "Ok",
            result = SubscriptionMapper.toResponse(subscription),
        )
    }

    @Transactional(readOnly = true)
    override fun getByUserId(userId: String): ServiceResult<SubscriptionResponse> {
        ensureAdminOrManager()
        val parsed = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw BaseException.BadRequestException("Invalid user_id")
        val subscription = subscriptionRepository.findByUserId(parsed)
            ?: throw BaseException.NotFoundException("Subscription not found")
        return ServiceResult(
            message = "Ok",
            result = SubscriptionMapper.toResponse(subscription),
        )
    }

    private fun currentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val jwt = authentication.principal as? Jwt
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        return runCatching { UUID.fromString(jwt.subject) }.getOrNull()
            ?: throw BaseException.BadRequestException("Invalid user_id")
    }

    private fun ensureAdminOrManager() {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val jwt = authentication.principal as? Jwt
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val role = jwt.getClaimAsString("role") ?: ""
        if (role != Role.ADMIN.name && role != Role.MANAGER.name) {
            throw BaseException.ForbiddenException("Forbidden")
        }
    }
}
