package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.enum.SubscriptionTransactionStatus
import com.exe.vocafy_BE.enum.EnrollmentStatus
import com.exe.vocafy_BE.model.entity.Category
import com.exe.vocafy_BE.model.entity.Enrollment
import com.exe.vocafy_BE.model.entity.Feedback
import com.exe.vocafy_BE.model.entity.PaymentMethod
import com.exe.vocafy_BE.model.entity.PremiumPackage
import com.exe.vocafy_BE.model.entity.Profile
import com.exe.vocafy_BE.model.entity.Subscription
import com.exe.vocafy_BE.model.entity.SubscriptionTransaction
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.CategoryRepository
import com.exe.vocafy_BE.repo.EnrollmentRepository
import com.exe.vocafy_BE.repo.FeedbackRepository
import com.exe.vocafy_BE.repo.PaymentMethodRepository
import com.exe.vocafy_BE.repo.PremiumPackageRepository
import com.exe.vocafy_BE.repo.ProfileRepository
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.repo.SubscriptionTransactionRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.SyllabusTargetLanguageRepository
import com.exe.vocafy_BE.repo.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime

@Configuration
class DataInitializer {

    companion object {
        private const val SEPAY_PROVIDER = "SEPAY"
        private const val DEFAULT_VIP_AMOUNT = 79000L

        private val FAKE_PAYMENT_USER_EMAILS = listOf(
            "khiem1371@gmail.com",
            "giabaostrike2004@gmail.com",
            "nguyen.an6649@vocafy.local",
            "ngia.binh30@vocafy.local",
            "nthanh.chau39@vocafy.local",
            "nguyendung41x@vocafy.local",
            "nguyengiang68x@vocafy.local",
            "hanh_nguyen26@vocafy.local",
            "nguyen.khanh8725@vocafy.local",
            "thu.linh.82@vocafy.local",
            "anh.nam.51@vocafy.local",
            "nguyenquynh47x@vocafy.local",
            "an_tran17@vocafy.local",
            "tranbinh24x@vocafy.local",
            "tranchau80x@vocafy.local",
            "duc.dung.78@vocafy.local",
            "quoc.giang.69@vocafy.local",
            "tran.hanh5677@vocafy.local",
            "tran.khanh4242@vocafy.local",
            "tranlinh38x@vocafy.local",
            "tran.anh62@vocafy.local",
            "tranquynh39x@vocafy.local",
            "lean57x@vocafy.local",
            "le.gia43@vocafy.local",
            "lechau22x@vocafy.local",
            "duc.dung.18@vocafy.local",
            "quoc.giang.39@vocafy.local",
            "bao.hanh.62@vocafy.local",
            "khanh_le25@vocafy.local",
            "thu.linh.54@vocafy.local",
            "nam_le90@vocafy.local",
            "le.tuan17@vocafy.local",
        )

        // Hai ảnh chỉ đọc rõ 29 mốc thời gian; 3 user cuối cùng dùng lại 3 mốc đầu để đủ 32 payment seed.
        private val FAKE_PAYMENT_CREATED_AT = listOf(
            LocalDateTime.of(2026, 3, 25, 22, 53, 11),
            LocalDateTime.of(2026, 3, 25, 22, 27, 28),
            LocalDateTime.of(2026, 3, 25, 21, 40, 19),
            LocalDateTime.of(2026, 3, 25, 21, 37, 14),
            LocalDateTime.of(2026, 3, 25, 21, 34, 29),
            LocalDateTime.of(2026, 3, 25, 21, 19, 49),
            LocalDateTime.of(2026, 3, 25, 20, 12, 51),
            LocalDateTime.of(2026, 3, 25, 20, 8, 57),
            LocalDateTime.of(2026, 3, 25, 19, 50, 45),
            LocalDateTime.of(2026, 3, 25, 19, 47, 54),
            LocalDateTime.of(2026, 3, 25, 19, 32, 8),
            LocalDateTime.of(2026, 3, 25, 19, 29, 19),
            LocalDateTime.of(2026, 3, 25, 19, 24, 46),
            LocalDateTime.of(2026, 3, 25, 19, 13, 5),
            LocalDateTime.of(2026, 3, 23, 14, 38, 10),
            LocalDateTime.of(2026, 3, 23, 10, 30, 7),
            LocalDateTime.of(2026, 3, 23, 10, 18, 46),
            LocalDateTime.of(2026, 3, 19, 17, 53, 11),
            LocalDateTime.of(2026, 3, 19, 15, 44, 40),
            LocalDateTime.of(2026, 3, 19, 12, 11, 55),
            LocalDateTime.of(2026, 3, 19, 9, 2, 56),
            LocalDateTime.of(2026, 3, 19, 8, 55, 31),
            LocalDateTime.of(2026, 3, 18, 22, 19, 38),
            LocalDateTime.of(2026, 3, 18, 22, 3, 49),
            LocalDateTime.of(2026, 3, 18, 19, 55, 31),
            LocalDateTime.of(2026, 3, 18, 19, 47, 36),
            LocalDateTime.of(2026, 3, 18, 19, 43, 55),
            LocalDateTime.of(2026, 3, 18, 19, 34, 13),
            LocalDateTime.of(2026, 3, 18, 19, 33, 36),
            LocalDateTime.of(2026, 3, 25, 22, 53, 11),
            LocalDateTime.of(2026, 3, 25, 22, 27, 28),
            LocalDateTime.of(2026, 3, 25, 21, 40, 19),
        )

        private data class SeedFeedback(
            val email: String,
            val rating: Int,
            val title: String,
            val content: String,
            val createdAt: LocalDateTime,
            val adminReply: String? = null,
            val repliedAt: LocalDateTime? = null,
        )

        private val FEEDBACK_SEEDS = listOf(
            SeedFeedback(
                email = "khiem1371@gmail.com",
                rating = 5,
                title = "Quá đã",
                content = "Quá đã luôn",
                createdAt = LocalDateTime.of(2026, 3, 26, 9, 12, 0),
                adminReply = "Quá ngon luôn",
                repliedAt = LocalDateTime.of(2026, 3, 26, 10, 5, 0),
            ),
            SeedFeedback(
                email = "giabaostrike2004@gmail.com",
                rating = 4,
                title = "Gọn gàng, dễ dùng",
                content = "Giao diện khá đẹp.",
                createdAt = LocalDateTime.of(2026, 3, 26, 11, 24, 0),
            ),
            SeedFeedback(
                email = "nguyen.an6649@vocafy.local",
                rating = 5,
                title = "Tốt",
                content = "Từ vựng N5 tương đối đủ",
                createdAt = LocalDateTime.of(2026, 3, 27, 8, 40, 0),
            ),
            SeedFeedback(
                email = "nthanh.chau39@vocafy.local",
                rating = 4,
                title = "Khá ổn, chỉ là chưa mượt lắm",
                content = "Nội dung với cách chia bài mình thấy ổn áp. Nhưng có vài lúc chuyển bộ từ hơi khựng nhẹ, chưa tới mức khó chịu nhưng mượt thêm tí thì đẹp.",
                createdAt = LocalDateTime.of(2026, 3, 27, 14, 18, 0),
                adminReply = "Cảm ơn bạn đã góp ý. Bên mình đang tối ưu lại phần chuyển và tải dữ liệu để trải nghiệm mượt hơn.",
                repliedAt = LocalDateTime.of(2026, 3, 27, 15, 2, 0),
            ),
            SeedFeedback(
                email = "tranbinh24x@vocafy.local",
                rating = 5,
                title = "Nên thêm tính năng leo rank",
                content = "Mình muốn admin nên bổ sung thêm tính năng leo rank cho có động lực cạnh tranh á.",
                createdAt = LocalDateTime.of(2026, 3, 28, 7, 55, 0),
            ),
            SeedFeedback(
                email = "duc.dung.78@vocafy.local",
                rating = 3,
                title = "Dùng ổn nhưng thiếu tính năng quá",
                content = "Hiện tại app còn hơi ít tính năng so với kỳ vọng, hy vọng tương lai sẽ bổ sung nhiêu hơn.",
                createdAt = LocalDateTime.of(2026, 3, 28, 20, 11, 0),
                adminReply = "Góp ý rất hữu ích. Bên mình đang xem xét bổ sung kiểu lọc chi tiết hơn theo mức độ ghi nhớ.",
                repliedAt = LocalDateTime.of(2026, 3, 28, 21, 0, 0),
            ),
            SeedFeedback(
                email = "tranlinh38x@vocafy.local",
                rating = 5,
                title = "Ngon",
                content = "App ngon, học là nhớ :))",
                createdAt = LocalDateTime.of(2026, 3, 29, 6, 48, 0),
            ),
            SeedFeedback(
                email = "lean57x@vocafy.local",
                rating = 4,
                title = "Cũng được",
                content = "Cũng được",
                createdAt = LocalDateTime.of(2026, 3, 29, 13, 16, 0),
            ),
            SeedFeedback(
                email = "bao.hanh.62@vocafy.local",
                rating = 5,
                title = "Có cảm giác tiến bộ thật",
                content = "Học tầm một tuần là mình bắt đầu nhận ra mấy từ cũ bật ra nhanh hơn. Cảm giác không còn học kiểu nay nhớ mai quên nữa.",
                createdAt = LocalDateTime.of(2026, 3, 30, 19, 35, 0),
            ),
            SeedFeedback(
                email = "le.tuan17@vocafy.local",
                rating = 4,
                title = "Nên thêm thống kê tiến độ vào course là đẹp",
                content = "Mình thích cách học hiện tại, nhìn chung dễ theo. Nhưng mà chưa có phần thống kê tiến độ theo course á.",
                createdAt = LocalDateTime.of(2026, 3, 31, 21, 8, 0),
            ),
            SeedFeedback(
                email = "nam_le90@vocafy.local",
                rating = 5,
                title = "Cách chia set từ mỗi lần học ok phết",
                content = "Chia kiều này chắc để vừa ôn vừa học cho nhớ lâu hơn",
                createdAt = LocalDateTime.of(2026, 4, 1, 8, 22, 0),
            ),
        )
    }

    @Bean
    @Order(0)
    fun patchUserDailyActivityConstraints(
        jdbcTemplate: JdbcTemplate,
    ) = ApplicationRunner {
        val currentSchema = jdbcTemplate.queryForObject("select database()", String::class.java)
            ?: return@ApplicationRunner

        val tableExists = jdbcTemplate.queryForObject(
            """
                select count(*)
                from information_schema.tables
                where table_schema = ?
                  and table_name = 'user_daily_activity'
            """.trimIndent(),
            Int::class.java,
            currentSchema,
        ) ?: 0

        if (tableExists == 0) {
            return@ApplicationRunner
        }

        val legacyUniqueIndexes = jdbcTemplate.queryForList(
            """
                select distinct s1.index_name
                from information_schema.statistics s1
                where s1.table_schema = ?
                  and s1.table_name = 'user_daily_activity'
                  and s1.non_unique = 0
                  and s1.index_name <> 'PRIMARY'
                  and s1.column_name = 'user_id'
                  and not exists (
                      select 1
                      from information_schema.statistics s2
                      where s2.table_schema = s1.table_schema
                        and s2.table_name = s1.table_name
                        and s2.index_name = s1.index_name
                        and s2.column_name <> 'user_id'
                  )
            """.trimIndent(),
            String::class.java,
            currentSchema,
        )

        legacyUniqueIndexes.forEach { indexName ->
            val escapedIndexName = indexName.replace("`", "``")
            jdbcTemplate.execute("ALTER TABLE user_daily_activity DROP INDEX `$escapedIndexName`")
        }

        val hasUserDateUnique = jdbcTemplate.queryForObject(
            """
                select count(*)
                from information_schema.table_constraints tc
                where tc.table_schema = ?
                  and tc.table_name = 'user_daily_activity'
                  and tc.constraint_type = 'UNIQUE'
                  and exists (
                      select 1
                      from information_schema.key_column_usage k1
                      where k1.table_schema = tc.table_schema
                        and k1.table_name = tc.table_name
                        and k1.constraint_name = tc.constraint_name
                        and k1.column_name = 'user_id'
                  )
                  and exists (
                      select 1
                      from information_schema.key_column_usage k2
                      where k2.table_schema = tc.table_schema
                        and k2.table_name = tc.table_name
                        and k2.constraint_name = tc.constraint_name
                        and k2.column_name = 'activity_date'
                  )
            """.trimIndent(),
            Int::class.java,
            currentSchema,
        ) ?: 0

        if (hasUserDateUnique == 0) {
            jdbcTemplate.execute(
                "ALTER TABLE user_daily_activity ADD CONSTRAINT uk_user_daily_activity_user_date UNIQUE (user_id, activity_date)"
            )
        }
    }

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

    @Bean
    @Order(2)
    fun seedFakePaymentTransactions(
        userRepository: UserRepository,
        subscriptionRepository: SubscriptionRepository,
        paymentMethodRepository: PaymentMethodRepository,
        premiumPackageRepository: PremiumPackageRepository,
        subscriptionTransactionRepository: SubscriptionTransactionRepository,
    ) = ApplicationRunner {
        val sepayPaymentMethod = paymentMethodRepository.findByProvider(SEPAY_PROVIDER) ?: return@ApplicationRunner
        val premiumPackage = premiumPackageRepository.findAll().firstOrNull()
        val amount = premiumPackage?.price?.toLong() ?: DEFAULT_VIP_AMOUNT
        val durationDays = premiumPackage?.durationDays?.toLong() ?: 30L

        FAKE_PAYMENT_USER_EMAILS.zip(FAKE_PAYMENT_CREATED_AT).forEach { (email, transactionAt) ->
            val user = userRepository.findByEmail(email) ?: return@forEach
            val userId = user.id ?: return@forEach

            if (subscriptionTransactionRepository.findTopByUserIdOrderByCreatedAtDesc(userId) != null) {
                return@forEach
            }

            val currentSubscription = subscriptionRepository.findByUserId(userId)
                ?: Subscription(user = user, plan = SubscriptionPlan.FREE)
            val startAt = transactionAt.toLocalDate()

            subscriptionRepository.save(
                Subscription(
                    id = currentSubscription.id,
                    user = user,
                    plan = SubscriptionPlan.VIP,
                    startAt = startAt,
                    endAt = startAt.plusDays(durationDays),
                )
            )

            subscriptionTransactionRepository.saveAll(
                listOf(
                    SubscriptionTransaction(
                        user = user,
                        paymentMethod = sepayPaymentMethod,
                        amount = amount,
                        status = SubscriptionTransactionStatus.DEBIT,
                        note = "Seeded fake payment debit from screenshot data",
                        createdAt = transactionAt,
                    ),
                    SubscriptionTransaction(
                        user = user,
                        paymentMethod = sepayPaymentMethod,
                        amount = amount,
                        status = SubscriptionTransactionStatus.CREDIT,
                        note = "Seeded fake payment credit from screenshot data",
                        createdAt = transactionAt,
                    ),
                )
            )
        }
    }

    @Bean
    @Order(3)
    fun seedFeedbacks(
        userRepository: UserRepository,
        feedbackRepository: FeedbackRepository,
    ) = ApplicationRunner {
        if (feedbackRepository.count() > 0L) {
            return@ApplicationRunner
        }

        val admin = userRepository.findByEmail("vocafy.exesp26@gmail.com")

        val feedbacks = FEEDBACK_SEEDS.mapNotNull { seed ->
            val user = userRepository.findByEmail(seed.email) ?: return@mapNotNull null
            Feedback(
                user = user,
                rating = seed.rating,
                title = seed.title,
                content = seed.content,
                adminReply = seed.adminReply,
                repliedBy = if (seed.adminReply != null) admin else null,
                repliedAt = seed.repliedAt,
                createdAt = seed.createdAt,
                updatedAt = seed.repliedAt ?: seed.createdAt,
            )
        }

        feedbackRepository.saveAll(feedbacks)
    }

    @Bean
    @Order(4)
    fun seedEnrollments(
        userRepository: UserRepository,
        syllabusRepository: SyllabusRepository,
        syllabusTargetLanguageRepository: SyllabusTargetLanguageRepository,
        enrollmentRepository: EnrollmentRepository,
    ) = ApplicationRunner {
        val activeSyllabuses = syllabusRepository.findAll()
            .filter { it.active && !it.isDeleted }
            .sortedBy { it.id ?: Long.MAX_VALUE }

        if (activeSyllabuses.isEmpty()) {
            return@ApplicationRunner
        }

        FAKE_PAYMENT_USER_EMAILS.zip(FAKE_PAYMENT_CREATED_AT).forEachIndexed { index, (email, transactionAt) ->
            val user = userRepository.findByEmail(email) ?: return@forEachIndexed
            val userId = user.id ?: return@forEachIndexed
            val syllabus = activeSyllabuses[index % activeSyllabuses.size]
            val syllabusId = syllabus.id ?: return@forEachIndexed

            if (enrollmentRepository.findByUserIdAndSyllabusId(userId, syllabusId) != null) {
                return@forEachIndexed
            }

            val preferredTargetLanguage = syllabusTargetLanguageRepository
                .findAllBySyllabusIdOrderByIdAsc(syllabusId)
                .firstOrNull()
                ?.languageCode

            val hasFocused = enrollmentRepository.findByUserIdAndIsFocusedTrue(userId) != null

            enrollmentRepository.save(
                Enrollment(
                    user = user,
                    syllabus = syllabus,
                    startDate = transactionAt.toLocalDate(),
                    status = EnrollmentStatus.ACTIVE,
                    preferredTargetLanguage = preferredTargetLanguage,
                    isFocused = !hasFocused,
                )
            )
        }
    }
}
