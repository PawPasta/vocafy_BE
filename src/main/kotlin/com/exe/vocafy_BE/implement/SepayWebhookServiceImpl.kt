package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.enum.SubscriptionTransactionStatus
import com.exe.vocafy_BE.model.dto.request.SepayWebhookRequest
import com.exe.vocafy_BE.model.entity.Subscription
import com.exe.vocafy_BE.model.entity.SubscriptionTransaction
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.PaymentMethodRepository
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.repo.SubscriptionTransactionRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.SepayWebhookService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class SepayWebhookServiceImpl(
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionTransactionRepository: SubscriptionTransactionRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : SepayWebhookService {

    private val logger = LoggerFactory.getLogger(SepayWebhookServiceImpl::class.java)

    @Transactional
    override fun handleWebhook(request: SepayWebhookRequest): Map<String, Any> {
        logger.info("Received Sepay webhook: content=${request.content}, amount=${request.transferAmount}")

        val content = request.content?.trim()
        if (content.isNullOrBlank()) {
            logger.warn("Webhook content is empty")
            return mapOf("success" to false, "message" to "Content is empty")
        }

        val user = userRepository.findBySepayCode(content)
        if (user == null) {
            logger.warn("No user found with sepayCode: $content")
            return mapOf("success" to false, "message" to "Invalid sepay code")
        }

        val subscription = subscriptionRepository.findByUserId(user.id!!)
        if (subscription == null) {
            logger.warn("No subscription found for user: ${user.id}")
            return mapOf("success" to false, "message" to "Subscription not found")
        }

        val sepayPaymentMethod = paymentMethodRepository.findAll()
            .firstOrNull { it.provider == "SEPAY" }
        if (sepayPaymentMethod == null) {
            logger.warn("SEPAY payment method not found")
            return mapOf("success" to false, "message" to "Payment method not found")
        }

        val amount = request.transferAmount ?: 0
        val now = LocalDate.now()
        val currentEndDate = subscription.endAt ?: now
        val newEndDate = if (currentEndDate.isAfter(now)) {
            currentEndDate.plusDays(30)
        } else {
            now.plusDays(30)
        }

        val updatedSubscription = Subscription(
            id = subscription.id,
            user = subscription.user,
            plan = SubscriptionPlan.VIP,
            startAt = subscription.startAt ?: now,
            endAt = newEndDate,
        )
        subscriptionRepository.save(updatedSubscription)

        val transaction = SubscriptionTransaction(
            user = user,
            paymentMethod = sepayPaymentMethod,
            amount = amount,
            status = SubscriptionTransactionStatus.SUCCESS,
            note = "Payment via Sepay - Code: $content",
        )
        subscriptionTransactionRepository.save(transaction)

        val updatedUser = User(
            id = user.id,
            email = user.email,
            role = user.role,
            status = user.status,
            lastLoginAt = user.lastLoginAt,
            lastActiveAt = user.lastActiveAt,
            sepayCode = null,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )
        userRepository.save(updatedUser)

        logger.info("Successfully upgraded user ${user.id} to VIP, transaction amount: $amount")

        return mapOf(
            "success" to true,
            "message" to "Payment processed successfully",
        )
    }
}
