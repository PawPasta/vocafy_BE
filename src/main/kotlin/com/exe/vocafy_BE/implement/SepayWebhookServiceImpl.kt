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
import com.exe.vocafy_BE.util.FirebaseNotificationUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class SepayWebhookServiceImpl(
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionTransactionRepository: SubscriptionTransactionRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val firebaseNotificationUtil: FirebaseNotificationUtil,
) : SepayWebhookService {

    @Transactional
    override fun handleWebhook(request: SepayWebhookRequest): Map<String, Any> {
        if (!request.transferType.equals(INCOMING_TRANSFER_TYPE, ignoreCase = true)) {
            return mapOf("success" to false, "message" to "Invalid transfer type")
        }

        // ✅ Chỉ parse format: "QR - XXXXXXXX" => lấy "XXXXXXXX"
        val sepayCode = extractSepayCode(request)
            ?: return mapOf("success" to false, "message" to "Invalid sepay code")

        val user = userRepository.findBySepayCode(sepayCode)
            ?: return mapOf("success" to false, "message" to "Invalid sepay code")

        val subscription = subscriptionRepository.findByUserId(user.id!!)
            ?: return mapOf("success" to false, "message" to "Subscription not found")

        val sepayPaymentMethod = paymentMethodRepository.findByProvider(SEPAY_PROVIDER)
            ?: return mapOf("success" to false, "message" to "Payment method not found")

        val amountLong = request.transferAmount
            ?: return mapOf("success" to false, "message" to "Missing transfer amount")

        if (amountLong <= 0L) {
            return mapOf("success" to false, "message" to "Invalid transfer amount")
        }

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

        val transactionDebit = SubscriptionTransaction(
            user = user,
            paymentMethod = sepayPaymentMethod,
            amount = amountLong,
            status = SubscriptionTransactionStatus.DEBIT,
            note = "Debit from your account",
        )
        subscriptionTransactionRepository.save(transactionDebit)

        val transactionCredit = SubscriptionTransaction(
            user = user,
            paymentMethod = sepayPaymentMethod,
            amount = amountLong,
            status = SubscriptionTransactionStatus.CREDIT,
            note = "Subscription successful",
        )
        subscriptionTransactionRepository.save(transactionCredit)

        val updatedUser = User(
            id = user.id,
            email = user.email,
            role = user.role,
            status = user.status,
            lastLoginAt = user.lastLoginAt,
            lastActiveAt = user.lastActiveAt,
            sepayCode = null,
            fcmToken = user.fcmToken,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )
        userRepository.save(updatedUser)

        val notificationSent = runCatching {
            firebaseNotificationUtil.sendToUser(
                fcmToken = user.fcmToken,
                title = "Thanh toan thanh cong",
                body = "Ban da dang ky goi VIP thanh cong.",
                data = mapOf(
                    "type" to "SUBSCRIPTION_SUCCESS",
                    "plan" to SubscriptionPlan.VIP.name,
                    "amount" to amountLong.toString(),
                ),
            )
        }.getOrDefault(false)

        return mapOf(
            "success" to true,
            "message" to "Payment processed successfully",
            "notificationSent" to notificationSent,
            "sepayCode" to sepayCode, // tiện debug
        )
    }

    /**
     * ✅ Chỉ quan tâm format "QR - XXXXX" (bỏ "QR -", lấy phần phía sau)
     * - Check lần lượt content/description/code
     * - Trim + uppercase + gom khoảng trắng
     */
    private fun extractSepayCode(request: SepayWebhookRequest): String? {
        val rawSources = listOf(request.content, request.description, request.code)

        for (raw in rawSources) {
            val normalized = raw
                ?.trim()
                ?.uppercase()
                ?.replace(Regex("\\s+"), " ")
                .orEmpty()

            if (normalized.isBlank()) continue

            // Match các biến thể: "QR - XXXX", "QR-XXXX", "QR   -   XXXX"
            val match = QR_PAYLOAD_REGEX.find(normalized)
            if (match != null) {
                val code = match.groupValues.getOrNull(1)?.trim().orEmpty()
                if (code.isNotBlank()) return code
            }
        }
        return null
    }

    companion object {
        private const val INCOMING_TRANSFER_TYPE = "in"
        private const val SEPAY_PROVIDER = "SEPAY"

        // "QR - XXXXX" => group(1) là XXXXX (lấy hết phần phía sau)
        private val QR_PAYLOAD_REGEX = Regex("^QR\\s*-\\s*(.+)$")
    }
}