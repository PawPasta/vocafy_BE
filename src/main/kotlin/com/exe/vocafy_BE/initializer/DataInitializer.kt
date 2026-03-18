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
        data class SeedUser(
            val displayName: String,
            val email: String,
            val role: Role,
        )

        if (userRepository.count() == 0L) {
            val seedUsers = listOf(
                SeedUser("Admin User", "vocafy.exesp26@gmail.com", Role.ADMIN),
                SeedUser("Manager One", "khiemngse182188@fpt.edu.vn", Role.MANAGER),
                SeedUser("Manager Two", "manager2@vocafy.local", Role.MANAGER),
                SeedUser("Nguyen Van A", "baoltgse182138@fpt.edu.vn", Role.MANAGER),
                SeedUser("Tran Thi B", "phatttse182221@fpt.edu.vn", Role.MANAGER),
                SeedUser("Le Van C", "anltse184186@fpt.edu.vn", Role.MANAGER),
                SeedUser("Pham Thi D", "sondtse183892@fpt.edu.vn", Role.MANAGER),
                SeedUser("Hoang Van E", "thaodpss170172@fpt.edu.vn", Role.MANAGER),

                SeedUser("Khiem Nguyen", "khiem1371@gmail.com", Role.USER),
                SeedUser("Gia Bao Le", "giabaostrike2004@gmail.com", Role.USER),
                SeedUser("Nguyen Minh An", "nguyen.minh.an@vocafy.local", Role.USER),
                SeedUser("Nguyen Gia Binh", "nguyen.gia.binh@vocafy.local", Role.USER),
                SeedUser("Nguyen Thanh Chau", "nguyen.thanh.chau@vocafy.local", Role.USER),
                SeedUser("Nguyen Duc Dung", "nguyen.duc.dung@vocafy.local", Role.USER),
                SeedUser("Nguyen Quoc Giang", "nguyen.quoc.giang@vocafy.local", Role.USER),
                SeedUser("Nguyen Bao Hanh", "nguyen.bao.hanh@vocafy.local", Role.USER),
                SeedUser("Nguyen Ngoc Khanh", "nguyen.ngoc.khanh@vocafy.local", Role.USER),
                SeedUser("Nguyen Thu Linh", "nguyen.thu.linh@vocafy.local", Role.USER),
                SeedUser("Nguyen Anh Nam", "nguyen.anh.nam@vocafy.local", Role.USER),
                SeedUser("Nguyen Tuan Quynh", "nguyen.tuan.quynh@vocafy.local", Role.USER),
                SeedUser("Tran Minh An", "tran.minh.an@vocafy.local", Role.USER),
                SeedUser("Tran Gia Binh", "tran.gia.binh@vocafy.local", Role.USER),
                SeedUser("Tran Thanh Chau", "tran.thanh.chau@vocafy.local", Role.USER),
                SeedUser("Tran Duc Dung", "tran.duc.dung@vocafy.local", Role.USER),
                SeedUser("Tran Quoc Giang", "tran.quoc.giang@vocafy.local", Role.USER),
                SeedUser("Tran Bao Hanh", "tran.bao.hanh@vocafy.local", Role.USER),
                SeedUser("Tran Ngoc Khanh", "tran.ngoc.khanh@vocafy.local", Role.USER),
                SeedUser("Tran Thu Linh", "tran.thu.linh@vocafy.local", Role.USER),
                SeedUser("Tran Anh Nam", "tran.anh.nam@vocafy.local", Role.USER),
                SeedUser("Tran Tuan Quynh", "tran.tuan.quynh@vocafy.local", Role.USER),
                SeedUser("Le Minh An", "le.minh.an@vocafy.local", Role.USER),
                SeedUser("Le Gia Binh", "le.gia.binh@vocafy.local", Role.USER),
                SeedUser("Le Thanh Chau", "le.thanh.chau@vocafy.local", Role.USER),
                SeedUser("Le Duc Dung", "le.duc.dung@vocafy.local", Role.USER),
                SeedUser("Le Quoc Giang", "le.quoc.giang@vocafy.local", Role.USER),
                SeedUser("Le Bao Hanh", "le.bao.hanh@vocafy.local", Role.USER),
                SeedUser("Le Ngoc Khanh", "le.ngoc.khanh@vocafy.local", Role.USER),
                SeedUser("Le Thu Linh", "le.thu.linh@vocafy.local", Role.USER),
                SeedUser("Le Anh Nam", "le.anh.nam@vocafy.local", Role.USER),
                SeedUser("Le Tuan Quynh", "le.tuan.quynh@vocafy.local", Role.USER),
                SeedUser("Pham Minh An", "pham.minh.an@vocafy.local", Role.USER),
                SeedUser("Pham Gia Binh", "pham.gia.binh@vocafy.local", Role.USER),
                SeedUser("Pham Thanh Chau", "pham.thanh.chau@vocafy.local", Role.USER),
                SeedUser("Pham Duc Dung", "pham.duc.dung@vocafy.local", Role.USER),
                SeedUser("Pham Quoc Giang", "pham.quoc.giang@vocafy.local", Role.USER),
                SeedUser("Pham Bao Hanh", "pham.bao.hanh@vocafy.local", Role.USER),
                SeedUser("Pham Ngoc Khanh", "pham.ngoc.khanh@vocafy.local", Role.USER),
                SeedUser("Pham Thu Linh", "pham.thu.linh@vocafy.local", Role.USER),
                SeedUser("Pham Anh Nam", "pham.anh.nam@vocafy.local", Role.USER),
                SeedUser("Pham Tuan Quynh", "pham.tuan.quynh@vocafy.local", Role.USER),
                SeedUser("Hoang Minh An", "hoang.minh.an@vocafy.local", Role.USER),
                SeedUser("Hoang Gia Binh", "hoang.gia.binh@vocafy.local", Role.USER),
                SeedUser("Hoang Thanh Chau", "hoang.thanh.chau@vocafy.local", Role.USER),
                SeedUser("Hoang Duc Dung", "hoang.duc.dung@vocafy.local", Role.USER),
                SeedUser("Hoang Quoc Giang", "hoang.quoc.giang@vocafy.local", Role.USER),
                SeedUser("Hoang Bao Hanh", "hoang.bao.hanh@vocafy.local", Role.USER),
                SeedUser("Hoang Ngoc Khanh", "hoang.ngoc.khanh@vocafy.local", Role.USER),
                SeedUser("Hoang Thu Linh", "hoang.thu.linh@vocafy.local", Role.USER),
                SeedUser("Hoang Anh Nam", "hoang.anh.nam@vocafy.local", Role.USER),
                SeedUser("Hoang Tuan Quynh", "hoang.tuan.quynh@vocafy.local", Role.USER),
                SeedUser("Phan Minh An", "phan.minh.an@vocafy.local", Role.USER),
                SeedUser("Phan Gia Binh", "phan.gia.binh@vocafy.local", Role.USER),
                SeedUser("Phan Thanh Chau", "phan.thanh.chau@vocafy.local", Role.USER),
                SeedUser("Phan Duc Dung", "phan.duc.dung@vocafy.local", Role.USER),
                SeedUser("Phan Quoc Giang", "phan.quoc.giang@vocafy.local", Role.USER),
                SeedUser("Phan Bao Hanh", "phan.bao.hanh@vocafy.local", Role.USER),
                SeedUser("Phan Ngoc Khanh", "phan.ngoc.khanh@vocafy.local", Role.USER),
                SeedUser("Phan Thu Linh", "phan.thu.linh@vocafy.local", Role.USER),
                SeedUser("Phan Anh Nam", "phan.anh.nam@vocafy.local", Role.USER),
                SeedUser("Phan Tuan Quynh", "phan.tuan.quynh@vocafy.local", Role.USER),
                SeedUser("Vu Minh An", "vu.minh.an@vocafy.local", Role.USER),
                SeedUser("Vu Gia Binh", "vu.gia.binh@vocafy.local", Role.USER),
                SeedUser("Vu Thanh Chau", "vu.thanh.chau@vocafy.local", Role.USER),
                SeedUser("Vu Duc Dung", "vu.duc.dung@vocafy.local", Role.USER),
                SeedUser("Vu Quoc Giang", "vu.quoc.giang@vocafy.local", Role.USER),
                SeedUser("Vu Bao Hanh", "vu.bao.hanh@vocafy.local", Role.USER),
                SeedUser("Vu Ngoc Khanh", "vu.ngoc.khanh@vocafy.local", Role.USER),
                SeedUser("Vu Thu Linh", "vu.thu.linh@vocafy.local", Role.USER),
                SeedUser("Vu Anh Nam", "vu.anh.nam@vocafy.local", Role.USER),
                SeedUser("Vu Tuan Quynh", "vu.tuan.quynh@vocafy.local", Role.USER),
                SeedUser("Dang Minh An", "dang.minh.an@vocafy.local", Role.USER),
                SeedUser("Dang Gia Binh", "dang.gia.binh@vocafy.local", Role.USER),
                SeedUser("Dang Thanh Chau", "dang.thanh.chau@vocafy.local", Role.USER),
                SeedUser("Dang Duc Dung", "dang.duc.dung@vocafy.local", Role.USER),
                SeedUser("Dang Quoc Giang", "dang.quoc.giang@vocafy.local", Role.USER),
                SeedUser("Dang Bao Hanh", "dang.bao.hanh@vocafy.local", Role.USER),
                SeedUser("Dang Ngoc Khanh", "dang.ngoc.khanh@vocafy.local", Role.USER),
                SeedUser("Dang Thu Linh", "dang.thu.linh@vocafy.local", Role.USER),
                SeedUser("Dang Anh Nam", "dang.anh.nam@vocafy.local", Role.USER),
                SeedUser("Dang Tuan Quynh", "dang.tuan.quynh@vocafy.local", Role.USER),
                SeedUser("Bui Minh An", "bui.minh.an@vocafy.local", Role.USER),
                SeedUser("Bui Gia Binh", "bui.gia.binh@vocafy.local", Role.USER),
                SeedUser("Bui Thanh Chau", "bui.thanh.chau@vocafy.local", Role.USER),
                SeedUser("Bui Duc Dung", "bui.duc.dung@vocafy.local", Role.USER),
                SeedUser("Bui Quoc Giang", "bui.quoc.giang@vocafy.local", Role.USER),
                SeedUser("Bui Bao Hanh", "bui.bao.hanh@vocafy.local", Role.USER),
                SeedUser("Bui Ngoc Khanh", "bui.ngoc.khanh@vocafy.local", Role.USER),
                SeedUser("Bui Thu Linh", "bui.thu.linh@vocafy.local", Role.USER),
                SeedUser("Bui Anh Nam", "bui.anh.nam@vocafy.local", Role.USER),
                SeedUser("Bui Tuan Quynh", "bui.tuan.quynh@vocafy.local", Role.USER),
                SeedUser("Do Minh An", "do.minh.an@vocafy.local", Role.USER),
                SeedUser("Do Gia Binh", "do.gia.binh@vocafy.local", Role.USER),
                SeedUser("Do Thanh Chau", "do.thanh.chau@vocafy.local", Role.USER),
                SeedUser("Do Duc Dung", "do.duc.dung@vocafy.local", Role.USER),
                SeedUser("Do Quoc Giang", "do.quoc.giang@vocafy.local", Role.USER),
                SeedUser("Do Bao Hanh", "do.bao.hanh@vocafy.local", Role.USER),
                SeedUser("Do Ngoc Khanh", "do.ngoc.khanh@vocafy.local", Role.USER),
                SeedUser("Do Thu Linh", "do.thu.linh@vocafy.local", Role.USER),
            )

            val users = seedUsers.map { seedUser ->
                User(email = seedUser.email, role = seedUser.role, status = Status.ACTIVE)
            }
            val savedUsers = userRepository.saveAll(users)
            val profiles = savedUsers.zip(seedUsers).map { (user, seedUser) ->
                Profile(
                    user = user,
                    displayName = seedUser.displayName,
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
