package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.PremiumPackageMapper
import com.exe.vocafy_BE.model.dto.response.PaymentUrlResponse
import com.exe.vocafy_BE.model.dto.response.PremiumPackageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.PremiumPackageRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.PaymentService
import com.exe.vocafy_BE.util.SePayUtil
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PaymentServiceImpl(
    private val premiumPackageRepository: PremiumPackageRepository,
    private val userRepository: UserRepository,
    private val sePayUtil: SePayUtil,
) : PaymentService {

    @Transactional(readOnly = true)
    override fun getActivePackages(): ServiceResult<List<PremiumPackageResponse>> {
        val packages = premiumPackageRepository.findByActiveTrue()
        return ServiceResult(
            message = "Ok",
            result = packages.map { PremiumPackageMapper.toResponse(it) },
        )
    }

    @Transactional
    override fun generatePaymentUrl(packageId: Long): ServiceResult<PaymentUrlResponse> {
        val userId = currentUserId()
        val user = userRepository.findById(userId)
            .orElseThrow { BaseException.NotFoundException("User not found") }

        val premiumPackage = premiumPackageRepository.findById(packageId)
            .orElseThrow { BaseException.NotFoundException("Premium package not found") }

        if (!premiumPackage.active) {
            throw BaseException.BadRequestException("Premium package is not available")
        }

        val sepayCode = generateSepayCode(userId)
        
        val updatedUser = User(
            id = user.id,
            email = user.email,
            role = user.role,
            status = user.status,
            lastLoginAt = user.lastLoginAt,
            lastActiveAt = user.lastActiveAt,
            sepayCode = sepayCode,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )
        userRepository.save(updatedUser)

        val amount = premiumPackage.price
        val url = sePayUtil.generateSePayQrUrl(amount.toDouble(), sepayCode)

        return ServiceResult(
            message = "Ok",
            result = PaymentUrlResponse(
                url = url,
                amount = amount,
                ref1 = sepayCode,
            ),
        )
    }

    private fun generateSepayCode(userId: UUID): String {
        val timestamp = System.currentTimeMillis()
        val shortUuid = userId.toString().replace("-", "").take(8).uppercase()
        return "VCF${shortUuid}${timestamp % 100000}"
    }

    private fun currentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val jwt = authentication.principal as? Jwt
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        return runCatching { UUID.fromString(jwt.subject) }.getOrNull()
            ?: throw BaseException.BadRequestException("Invalid user_id")
    }
}
