package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.enum.SubscriptionTransactionStatus
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.response.DashboardGrowthRatesResponse
import com.exe.vocafy_BE.model.dto.response.DashboardMetricResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.repo.SubscriptionTransactionRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.service.DashboardService
import com.exe.vocafy_BE.util.SecurityUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.math.round

@Service
class DashboardServiceImpl(
    private val securityUtil: SecurityUtil,
    private val userRepository: UserRepository,
    private val syllabusRepository: SyllabusRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionTransactionRepository: SubscriptionTransactionRepository,
) : DashboardService {

    @Transactional(readOnly = true)
    override fun getUserMetrics(year: Int?, month: Int?): ServiceResult<DashboardMetricResponse> {
        ensureAdminOrManager()
        val window = resolveMonthWindow(year, month)
        val currentMonthCount = userRepository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            window.currentMonthStartAt,
            window.nextMonthStartAt,
        )
        val previousMonthCount = userRepository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            window.previousMonthStartAt,
            window.currentMonthStartAt,
        )
        return ServiceResult(
            message = "Ok",
            result = DashboardMetricResponse(
                count = userRepository.count(),
                currentMonthCount = currentMonthCount,
                previousMonthCount = previousMonthCount,
                growthRate = growthRate(currentMonthCount, previousMonthCount),
                year = window.year,
                month = window.month,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun getSyllabusMetrics(year: Int?, month: Int?): ServiceResult<DashboardMetricResponse> {
        ensureAdminOrManager()
        val window = resolveMonthWindow(year, month)
        val currentMonthCount = syllabusRepository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThanAndIsDeletedFalse(
            window.currentMonthStartAt,
            window.nextMonthStartAt,
        )
        val previousMonthCount = syllabusRepository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThanAndIsDeletedFalse(
            window.previousMonthStartAt,
            window.currentMonthStartAt,
        )
        return ServiceResult(
            message = "Ok",
            result = DashboardMetricResponse(
                count = syllabusRepository.countByIsDeletedFalse(),
                currentMonthCount = currentMonthCount,
                previousMonthCount = previousMonthCount,
                growthRate = growthRate(currentMonthCount, previousMonthCount),
                year = window.year,
                month = window.month,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun getVocabularyMetrics(year: Int?, month: Int?): ServiceResult<DashboardMetricResponse> {
        ensureAdminOrManager()
        val window = resolveMonthWindow(year, month)
        val currentMonthCount = vocabularyRepository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThanAndIsDeletedFalse(
            window.currentMonthStartAt,
            window.nextMonthStartAt,
        )
        val previousMonthCount = vocabularyRepository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThanAndIsDeletedFalse(
            window.previousMonthStartAt,
            window.currentMonthStartAt,
        )
        return ServiceResult(
            message = "Ok",
            result = DashboardMetricResponse(
                count = vocabularyRepository.countByIsDeletedFalse(),
                currentMonthCount = currentMonthCount,
                previousMonthCount = previousMonthCount,
                growthRate = growthRate(currentMonthCount, previousMonthCount),
                year = window.year,
                month = window.month,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun getActiveEnrollmentMetrics(year: Int?, month: Int?): ServiceResult<DashboardMetricResponse> {
        ensureAdminOrManager()
        val window = resolveMonthWindow(year, month)
        val currentCount = subscriptionRepository.countActiveByPlanAtDate(
            SubscriptionPlan.VIP,
            window.currentReferenceDate,
        )
        val previousCount = subscriptionRepository.countActiveByPlanAtDate(
            SubscriptionPlan.VIP,
            window.previousReferenceDate,
        )
        return ServiceResult(
            message = "Ok",
            result = DashboardMetricResponse(
                count = currentCount,
                currentMonthCount = currentCount,
                previousMonthCount = previousCount,
                growthRate = growthRate(currentCount, previousCount),
                year = window.year,
                month = window.month,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun getRevenueMetrics(year: Int?, month: Int?): ServiceResult<DashboardMetricResponse> {
        ensureAdminOrManager()
        val window = resolveMonthWindow(year, month)
        val currentMonthRevenue = subscriptionTransactionRepository
            .sumAmountByStatusAndCreatedAtBetween(
                SubscriptionTransactionStatus.CREDIT,
                window.currentMonthStartAt,
                window.nextMonthStartAt,
            ) ?: 0L
        val previousMonthRevenue = subscriptionTransactionRepository
            .sumAmountByStatusAndCreatedAtBetween(
                SubscriptionTransactionStatus.CREDIT,
                window.previousMonthStartAt,
                window.currentMonthStartAt,
            ) ?: 0L

        return ServiceResult(
            message = "Ok",
            result = DashboardMetricResponse(
                count = subscriptionTransactionRepository.sumAmountByStatus(SubscriptionTransactionStatus.CREDIT) ?: 0L,
                currentMonthCount = currentMonthRevenue,
                previousMonthCount = previousMonthRevenue,
                growthRate = growthRate(currentMonthRevenue, previousMonthRevenue),
                year = window.year,
                month = window.month,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun getGrowthRates(year: Int?, month: Int?): ServiceResult<DashboardGrowthRatesResponse> {
        ensureAdminOrManager()
        val userMetrics = getUserMetrics(year, month).result
        val syllabusMetrics = getSyllabusMetrics(year, month).result
        val vocabularyMetrics = getVocabularyMetrics(year, month).result
        val activeEnrollmentMetrics = getActiveEnrollmentMetrics(year, month).result
        val revenueMetrics = getRevenueMetrics(year, month).result

        return ServiceResult(
            message = "Ok",
            result = DashboardGrowthRatesResponse(
                userGrowthRate = userMetrics.growthRate,
                syllabusGrowthRate = syllabusMetrics.growthRate,
                vocabularyGrowthRate = vocabularyMetrics.growthRate,
                activeEnrollmentGrowthRate = activeEnrollmentMetrics.growthRate,
                revenueGrowthRate = revenueMetrics.growthRate,
                year = userMetrics.year,
                month = userMetrics.month,
            ),
        )
    }

    private fun ensureAdminOrManager() {
        val role = securityUtil.getCurrentRole()
        if (role != Role.ADMIN.name && role != Role.MANAGER.name) {
            throw BaseException.ForbiddenException("Forbidden")
        }
    }

    private fun resolveMonthWindow(year: Int?, month: Int?): MonthWindow {
        if ((year == null) != (month == null)) {
            throw BaseException.BadRequestException("Both 'year' and 'month' must be provided together")
        }

        val now = LocalDate.now()
        val selectedYearMonth = if (year == null || month == null) {
            YearMonth.of(now.year, now.monthValue)
        } else {
            runCatching { YearMonth.of(year, month) }
                .getOrElse { throw BaseException.BadRequestException("Invalid 'year' or 'month'") }
        }

        val currentMonthStartAt = selectedYearMonth.atDay(1).atStartOfDay()
        val nextMonthStartAt = selectedYearMonth.plusMonths(1).atDay(1).atStartOfDay()
        val previousMonthStartAt = selectedYearMonth.minusMonths(1).atDay(1).atStartOfDay()

        val currentReferenceDate = if (selectedYearMonth == YearMonth.of(now.year, now.monthValue)) {
            now
        } else {
            selectedYearMonth.atEndOfMonth()
        }
        val previousReferenceDate = selectedYearMonth.minusMonths(1).atEndOfMonth()

        return MonthWindow(
            year = selectedYearMonth.year,
            month = selectedYearMonth.monthValue,
            currentMonthStartAt = currentMonthStartAt,
            nextMonthStartAt = nextMonthStartAt,
            previousMonthStartAt = previousMonthStartAt,
            currentReferenceDate = currentReferenceDate,
            previousReferenceDate = previousReferenceDate,
        )
    }

    private fun growthRate(currentValue: Long, previousValue: Long): Double {
        if (previousValue == 0L) {
            return if (currentValue == 0L) 0.0 else 100.0
        }
        val rawRate = ((currentValue - previousValue).toDouble() / previousValue.toDouble()) * 100.0
        return round(rawRate * 100.0) / 100.0
    }

    private data class MonthWindow(
        val year: Int,
        val month: Int,
        val currentMonthStartAt: LocalDateTime,
        val nextMonthStartAt: LocalDateTime,
        val previousMonthStartAt: LocalDateTime,
        val currentReferenceDate: LocalDate,
        val previousReferenceDate: LocalDate,
    )
}
