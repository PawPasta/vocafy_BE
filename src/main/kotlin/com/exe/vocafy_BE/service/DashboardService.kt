package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.response.DashboardGrowthRatesResponse
import com.exe.vocafy_BE.model.dto.response.DashboardMetricResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult

interface DashboardService {
    fun getUserMetrics(year: Int?, month: Int?): ServiceResult<DashboardMetricResponse>
    fun getSyllabusMetrics(year: Int?, month: Int?): ServiceResult<DashboardMetricResponse>
    fun getVocabularyMetrics(year: Int?, month: Int?): ServiceResult<DashboardMetricResponse>
    fun getActiveEnrollmentMetrics(year: Int?, month: Int?): ServiceResult<DashboardMetricResponse>
    fun getRevenueMetrics(year: Int?, month: Int?): ServiceResult<DashboardMetricResponse>
    fun getGrowthRates(year: Int?, month: Int?): ServiceResult<DashboardGrowthRatesResponse>
}
