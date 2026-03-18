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
                SeedUser("Nguyen Minh An", "nguyen.an6649@vocafy.local", Role.USER),
                SeedUser("Nguyen Gia Binh", "ngia.binh30@vocafy.local", Role.USER),
                SeedUser("Nguyen Thanh Chau", "nthanh.chau39@vocafy.local", Role.USER),
                SeedUser("Nguyen Duc Dung", "nguyendung41x@vocafy.local", Role.USER),
                SeedUser("Nguyen Quoc Giang", "nguyengiang68x@vocafy.local", Role.USER),
                SeedUser("Nguyen Bao Hanh", "hanh_nguyen26@vocafy.local", Role.USER),
                SeedUser("Nguyen Ngoc Khanh", "nguyen.khanh8725@vocafy.local", Role.USER),
                SeedUser("Nguyen Thu Linh", "thu.linh.82@vocafy.local", Role.USER),
                SeedUser("Nguyen Anh Nam", "anh.nam.51@vocafy.local", Role.USER),
                SeedUser("Nguyen Tuan Quynh", "nguyenquynh47x@vocafy.local", Role.USER),
                SeedUser("Tran Minh An", "an_tran17@vocafy.local", Role.USER),
                SeedUser("Tran Gia Binh", "tranbinh24x@vocafy.local", Role.USER),
                SeedUser("Tran Thanh Chau", "tranchau80x@vocafy.local", Role.USER),
                SeedUser("Tran Duc Dung", "duc.dung.78@vocafy.local", Role.USER),
                SeedUser("Tran Quoc Giang", "quoc.giang.69@vocafy.local", Role.USER),
                SeedUser("Tran Bao Hanh", "tran.hanh5677@vocafy.local", Role.USER),
                SeedUser("Tran Ngoc Khanh", "tran.khanh4242@vocafy.local", Role.USER),
                SeedUser("Tran Thu Linh", "tranlinh38x@vocafy.local", Role.USER),
                SeedUser("Tran Anh Nam", "tran.anh62@vocafy.local", Role.USER),
                SeedUser("Tran Tuan Quynh", "tranquynh39x@vocafy.local", Role.USER),
                SeedUser("Le Minh An", "lean57x@vocafy.local", Role.USER),
                SeedUser("Le Gia Binh", "le.gia43@vocafy.local", Role.USER),
                SeedUser("Le Thanh Chau", "lechau22x@vocafy.local", Role.USER),
                SeedUser("Le Duc Dung", "duc.dung.18@vocafy.local", Role.USER),
                SeedUser("Le Quoc Giang", "quoc.giang.39@vocafy.local", Role.USER),
                SeedUser("Le Bao Hanh", "bao.hanh.62@vocafy.local", Role.USER),
                SeedUser("Le Ngoc Khanh", "khanh_le25@vocafy.local", Role.USER),
                SeedUser("Le Thu Linh", "thu.linh.54@vocafy.local", Role.USER),
                SeedUser("Le Anh Nam", "nam_le90@vocafy.local", Role.USER),
                SeedUser("Le Tuan Quynh", "le.tuan17@vocafy.local", Role.USER),
                SeedUser("Pham Minh An", "pminh.an90@vocafy.local", Role.USER),
                SeedUser("Pham Gia Binh", "phambinh55x@vocafy.local", Role.USER),
                SeedUser("Pham Thanh Chau", "pham.chau8119@vocafy.local", Role.USER),
                SeedUser("Pham Duc Dung", "pham.duc68@vocafy.local", Role.USER),
                SeedUser("Pham Quoc Giang", "pham.giang8990@vocafy.local", Role.USER),
                SeedUser("Pham Bao Hanh", "pham.bao29@vocafy.local", Role.USER),
                SeedUser("Pham Ngoc Khanh", "ngoc.khanh.40@vocafy.local", Role.USER),
                SeedUser("Pham Thu Linh", "pthu.linh46@vocafy.local", Role.USER),
                SeedUser("Pham Anh Nam", "pham.anh17@vocafy.local", Role.USER),
                SeedUser("Pham Tuan Quynh", "ptuan.quynh30@vocafy.local", Role.USER),
                SeedUser("Hoang Minh An", "hoang.minh62@vocafy.local", Role.USER),
                SeedUser("Hoang Gia Binh", "hoang.binh5030@vocafy.local", Role.USER),
                SeedUser("Hoang Thanh Chau", "hthanh.chau61@vocafy.local", Role.USER),
                SeedUser("Hoang Duc Dung", "dung_hoang81@vocafy.local", Role.USER),
                SeedUser("Hoang Quoc Giang", "hoanggiang81x@vocafy.local", Role.USER),
                SeedUser("Hoang Bao Hanh", "hanh_hoang83@vocafy.local", Role.USER),
                SeedUser("Hoang Ngoc Khanh", "hoang.ngoc15@vocafy.local", Role.USER),
                SeedUser("Hoang Thu Linh", "hoang.linh7125@vocafy.local", Role.USER),
                SeedUser("Hoang Anh Nam", "hoang.nam6694@vocafy.local", Role.USER),
                SeedUser("Hoang Tuan Quynh", "hoang.quynh4004@vocafy.local", Role.USER),
                SeedUser("Phan Minh An", "minh.an.95@vocafy.local", Role.USER),
                SeedUser("Phan Gia Binh", "phan.gia54@vocafy.local", Role.USER),
                SeedUser("Phan Thanh Chau", "chau_phan34@vocafy.local", Role.USER),
                SeedUser("Phan Duc Dung", "pduc.dung48@vocafy.local", Role.USER),
                SeedUser("Phan Quoc Giang", "phan.giang4908@vocafy.local", Role.USER),
                SeedUser("Phan Bao Hanh", "phan.bao10@vocafy.local", Role.USER),
                SeedUser("Phan Ngoc Khanh", "phan.khanh1622@vocafy.local", Role.USER),
                SeedUser("Phan Thu Linh", "phanlinh94x@vocafy.local", Role.USER),
                SeedUser("Phan Anh Nam", "phannam54x@vocafy.local", Role.USER),
                SeedUser("Phan Tuan Quynh", "phan.quynh3988@vocafy.local", Role.USER),
                SeedUser("Vu Minh An", "vminh.an59@vocafy.local", Role.USER),
                SeedUser("Vu Gia Binh", "vu.gia25@vocafy.local", Role.USER),
                SeedUser("Vu Thanh Chau", "vthanh.chau74@vocafy.local", Role.USER),
                SeedUser("Vu Duc Dung", "vduc.dung92@vocafy.local", Role.USER),
                SeedUser("Vu Quoc Giang", "vquoc.giang68@vocafy.local", Role.USER),
                SeedUser("Vu Bao Hanh", "bao.hanh.13@vocafy.local", Role.USER),
                SeedUser("Vu Ngoc Khanh", "vu.ngoc25@vocafy.local", Role.USER),
                SeedUser("Vu Thu Linh", "linh_vu25@vocafy.local", Role.USER),
                SeedUser("Vu Anh Nam", "nam_vu22@vocafy.local", Role.USER),
                SeedUser("Vu Tuan Quynh", "quynh_vu33@vocafy.local", Role.USER),
                SeedUser("Dang Minh An", "dminh.an23@vocafy.local", Role.USER),
                SeedUser("Dang Gia Binh", "dangbinh90x@vocafy.local", Role.USER),
                SeedUser("Dang Thanh Chau", "dangchau63x@vocafy.local", Role.USER),
                SeedUser("Dang Duc Dung", "dduc.dung17@vocafy.local", Role.USER),
                SeedUser("Dang Quoc Giang", "dang.giang1905@vocafy.local", Role.USER),
                SeedUser("Dang Bao Hanh", "hanh_dang18@vocafy.local", Role.USER),
                SeedUser("Dang Ngoc Khanh", "ngoc.khanh.82@vocafy.local", Role.USER),
                SeedUser("Dang Thu Linh", "danglinh22x@vocafy.local", Role.USER),
                SeedUser("Dang Anh Nam", "anh.nam.80@vocafy.local", Role.USER),
                SeedUser("Dang Tuan Quynh", "dangquynh38x@vocafy.local", Role.USER),
                SeedUser("Bui Minh An", "minh.an.35@vocafy.local", Role.USER),
                SeedUser("Bui Gia Binh", "buibinh21x@vocafy.local", Role.USER),
                SeedUser("Bui Thanh Chau", "bui.chau9489@vocafy.local", Role.USER),
                SeedUser("Bui Duc Dung", "buidung11x@vocafy.local", Role.USER),
                SeedUser("Bui Quoc Giang", "bui.quoc52@vocafy.local", Role.USER),
                SeedUser("Bui Bao Hanh", "buihanh52x@vocafy.local", Role.USER),
                SeedUser("Bui Ngoc Khanh", "ngoc.khanh.47@vocafy.local", Role.USER),
                SeedUser("Bui Thu Linh", "linh_bui71@vocafy.local", Role.USER),
                SeedUser("Bui Anh Nam", "anh.nam.46@vocafy.local", Role.USER),
                SeedUser("Bui Tuan Quynh", "buiquynh29x@vocafy.local", Role.USER),
                SeedUser("Do Minh An", "an_do51@vocafy.local", Role.USER),
                SeedUser("Do Gia Binh", "binh_do41@vocafy.local", Role.USER),
                SeedUser("Do Thanh Chau", "do.chau1554@vocafy.local", Role.USER),
                SeedUser("Do Duc Dung", "do.duc12@vocafy.local", Role.USER),
                SeedUser("Do Quoc Giang", "quoc.giang.45@vocafy.local", Role.USER),
                SeedUser("Do Bao Hanh", "dbao.hanh79@vocafy.local", Role.USER),
                SeedUser("Do Ngoc Khanh", "khanh_do27@vocafy.local", Role.USER),
                SeedUser("Do Thu Linh", "dolinh36x@vocafy.local", Role.USER),
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
