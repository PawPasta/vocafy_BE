package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.PremiumPackageMapper
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.PaymentTransactionCheckResponse
import com.exe.vocafy_BE.model.dto.response.PaymentUrlResponse
import com.exe.vocafy_BE.model.dto.response.PremiumPackageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.PremiumPackageRepository
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.repo.SubscriptionTransactionRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.PaymentService
import com.exe.vocafy_BE.util.SePayUtil
import com.exe.vocafy_BE.util.SecurityUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class PaymentServiceImpl(
    private val securityUtil: SecurityUtil,
    private val premiumPackageRepository: PremiumPackageRepository,
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionTransactionRepository: SubscriptionTransactionRepository,
    private val sePayUtil: SePayUtil,
) : PaymentService {

    private val log = LoggerFactory.getLogger(PaymentServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun getActivePackages(pageable: Pageable): ServiceResult<PageResponse<PremiumPackageResponse>> {
        val page = premiumPackageRepository.findByActiveTrue(pageable)
        val mapped = page.map(PremiumPackageMapper::toResponse)
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = mapped.content,
                page = mapped.number,
                size = mapped.size,
                totalElements = mapped.totalElements,
                totalPages = mapped.totalPages,
                isFirst = mapped.isFirst,
                isLast = mapped.isLast,
            ),
        )
    }

    @Transactional
    override fun generatePaymentUrl(packageId: Long): ServiceResult<PaymentUrlResponse> {
        val userId = securityUtil.getCurrentUserId()

        log.info("[Payment] generatePaymentUrl called: userId={}, packageId={}", userId, packageId)

        val user = userRepository.findById(userId)
            .orElseThrow { BaseException.NotFoundException("User not found") }

        // Guard: user already has an active VIP subscription
        val subscription = subscriptionRepository.findByUserId(userId)
        val today = LocalDate.now()
        val endAt = subscription?.endAt
        val isPremiumActive = subscription?.plan == SubscriptionPlan.VIP &&
            (endAt == null || !endAt.isBefore(today))

        log.info(
            "[Payment] Subscription check: userId={}, hasSubscription={}, plan={}, endAt={}, today={}, isPremiumActive={}",
            userId,
            subscription != null,
            subscription?.plan,
            endAt,
            today,
            isPremiumActive,
        )

        if (isPremiumActive) {
            log.warn("[Payment] Block generatePaymentUrl: user already premium. userId={}", userId)
            throw BaseException.AlreadyPremiumException("User already has premium")
        }

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
            fcmToken = user.fcmToken,
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

    @Transactional(readOnly = true)
    override fun checkMyPaymentTransaction(): ServiceResult<PaymentTransactionCheckResponse> {
        val user = securityUtil.getCurrentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val subscription = subscriptionRepository.findByUserId(userId)
        val latestTransaction = subscriptionTransactionRepository.findTopByUserIdOrderByCreatedAtDesc(userId)

        val today = LocalDate.now()
        val subscriptionEndAt = subscription?.endAt
        val isVipActive = subscription?.plan == SubscriptionPlan.VIP &&
            (subscriptionEndAt == null || !subscriptionEndAt.isBefore(today))
        val isPending = !user.sepayCode.isNullOrBlank()

        val paymentStatus = when {
            isVipActive -> "SUCCESS"
            isPending -> "PENDING"
            else -> "NOT_PAID"
        }

        val responseMessage = when (paymentStatus) {
            "SUCCESS" -> "Subscription registration successful"
            "PENDING" -> "Transaction is being processed"
            else -> "No successful transaction found"
        }

        return ServiceResult(
            message = responseMessage,
            result = PaymentTransactionCheckResponse(
                isRegistrationSuccessful = isVipActive,
                paymentStatus = paymentStatus,
                subscriptionPlan = subscription?.plan,
                subscriptionEndAt = subscriptionEndAt,
                latestTransactionStatus = latestTransaction?.status,
                latestTransactionAmount = latestTransaction?.amount,
            ),
        )
    }


    private fun generateSepayCode(userId: UUID): String {
        return "VY-${userId.toString().replace("-", "").take(12)}".uppercase()
    }
}
