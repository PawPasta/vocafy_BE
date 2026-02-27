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

    //Thắc Mắc Tại Sao Phải Dùng Cái Này Phải Không
    //HAHAHAHAHA vì đây là cách dễ nhất ( hay còn gọi là hạn chế ) của QR code thanh toán thay vì việc dùng các ví điện tử
    //SEPAY nó sẽ gửi webhook về khi có giao dịch thành công (cái này sẽ dựa vào content khi chuyển khoản) - cai nay sẽ là mã code của user - để xác định user nào đã thanh toán
    //Nó hơi ngu, nhưng đây là cách giải quyết dễ dàng nhất
    //Nó sẽ có 1 số lỗi về bảo mật, nhưng do chỉ là demo môn học nên nó sẽ không cần phải quá chi tiết hehe

    @Transactional
    override fun handleWebhook(request: SepayWebhookRequest): Map<String, Any> {
        val content = request.content?.trim()
        if (content.isNullOrBlank()) {
            return mapOf("success" to false, "message" to "Content is empty")
        }

        val user = userRepository.findBySepayCode(content)
        if (user == null) {
            return mapOf("success" to false, "message" to "Invalid sepay code")
        }

        val subscription = subscriptionRepository.findByUserId(user.id!!)
        if (subscription == null) {
            return mapOf("success" to false, "message" to "Subscription not found")
        }

        val sepayPaymentMethod = paymentMethodRepository.findAll()
            .firstOrNull { it.provider == "SEPAY" }
        if (sepayPaymentMethod == null) {
            return mapOf("success" to false, "message" to "Payment method not found")
        }

        val amountLong = request.transferAmount ?: 0L

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
            note = "Debit from your account"
        )
        subscriptionTransactionRepository.save(transactionDebit)

        val transactionCredit = SubscriptionTransaction(
            user = user,
            paymentMethod = sepayPaymentMethod,
            amount = amountLong,
            status = SubscriptionTransactionStatus.CREDIT,
            note = "Subscription successful"
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
        )
    }
}
