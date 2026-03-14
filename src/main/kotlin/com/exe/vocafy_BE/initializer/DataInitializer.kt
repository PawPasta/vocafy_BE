package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.model.entity.Category
import com.exe.vocafy_BE.model.entity.PaymentMethod
import com.exe.vocafy_BE.model.entity.PremiumPackage
import com.exe.vocafy_BE.model.entity.Profile
import com.exe.vocafy_BE.model.entity.Subscription
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.CategoryRepository
import com.exe.vocafy_BE.repo.PaymentMethodRepository
import com.exe.vocafy_BE.repo.PremiumPackageRepository
import com.exe.vocafy_BE.repo.ProfileRepository
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.repo.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

@Configuration
class DataInitializer {

    @Bean
    @Order(1)
    fun seedBaseData(
        userRepository: UserRepository,
        profileRepository: ProfileRepository,
        subscriptionRepository: SubscriptionRepository,
        paymentMethodRepository: PaymentMethodRepository,
        premiumPackageRepository: PremiumPackageRepository,
        categoryRepository: CategoryRepository,
    ) = ApplicationRunner {
        if (userRepository.count() == 0L) {
            val users = listOf(
                User(email = "vocafy.exesp26@gmail.com", role = Role.ADMIN, status = Status.ACTIVE),

                User(email = "khiemngse182188@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "manager2@vocafy.local", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "baoltgse182138@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE), // Manager 3 - Nguyễn Văn A
                User(email = "phatttse182221@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE), // Manager 4 - Trần Thị B
                User(email = "anltse184186@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE), // Manager 5 - Lê Văn C
                User(email = "sondtse183892@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE), // Manager 6 - Phạm Thị D
                User(email = "thaodpss170172@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE),

                User(email = "khiem1371@gmail.com", role = Role.USER, status = Status.ACTIVE),
                User(email = "giabaostrike2004@gmail.com", role = Role.USER, status = Status.ACTIVE),
                User(email = "user3@vocafy.local", role = Role.USER, status = Status.ACTIVE),
            )
            val savedUsers = userRepository.saveAll(users)
            val profiles = savedUsers.mapIndexed { index, user ->
                Profile(
                    user = user,
                    displayName = when (index) {
                        0 -> "Admin User"
                        1 -> "Manager One"
                        2 -> "Manager Two"
                        3 -> "Nguyễn Văn A"
                        4 -> "Trần Thị B"
                        5 -> "Lê Văn C"
                        6 -> "Phạm Thị D"
                        7 -> "Hoàng Văn E"
                        else -> "User ${index - 7}"
                    },
                )
            }
            userRepository.flush()
            profileRepository.saveAll(profiles)

            if (subscriptionRepository.count() == 0L) {
                val subscriptions = savedUsers.map { user ->
                    if (user.email == "khiem1371@gmail.com" && user.email == "giabaostrike2004@gmail.com") {
                        Subscription(
                            user = user,
                            plan = SubscriptionPlan.VIP,
                            startAt = java.time.LocalDate.now(),
                            endAt = java.time.LocalDate.now().plusYears(1000),
                        )
                    } else {
                        Subscription(
                            user = user,
                            plan = SubscriptionPlan.FREE,
                        )
                    }
                }
                subscriptionRepository.saveAll(subscriptions)
            }
        }

        if (paymentMethodRepository.count() == 0L) {
            paymentMethodRepository.saveAll(
                listOf(
                    PaymentMethod(provider = "SEPAY", description = "SEPAY wallet"),
                    PaymentMethod(provider = "MOMO", description = "MoMo wallet"),
                    PaymentMethod(provider = "VNPAY", description = "VNPay gateway"),
                )
            )
        }

        if (premiumPackageRepository.count() == 0L) {
            premiumPackageRepository.saveAll(
                listOf(
                    PremiumPackage(
                        name = "VIP Monthly",
                        description = "Gói VIP 1 tháng - Truy cập tất cả nội dung premium",
                        price = 79000,
                        durationDays = 30,
                        active = true,
                    ),
                )
            )
        }

        if (categoryRepository.count() == 0L) {
            categoryRepository.saveAll(
                listOf(
                    Category(name = "General", description = "General purpose vocabulary"),
                    Category(name = "Business", description = "Business and professional vocabulary"),
                    Category(name = "Academic", description = "Academic and research vocabulary"),
                    Category(name = "Travel", description = "Travel and tourism vocabulary"),
                    Category(name = "Technology", description = "Technology and engineering vocabulary"),
                )
            )
        }
    }
}
