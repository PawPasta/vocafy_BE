package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.DashboardGrowthRatesResponse
import com.exe.vocafy_BE.model.dto.response.DashboardMetricResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.DashboardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Dashboard")
@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val dashboardService: DashboardService,
) {

    @GetMapping("/users")
    @Operation(summary = "Dashboard metric: user count + growth rate by month (admin, manager)")
    fun users(
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) month: Int?,
    ): ResponseEntity<BaseResponse<DashboardMetricResponse>> {
        val result = dashboardService.getUserMetrics(year, month)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/syllabi")
    @Operation(summary = "Dashboard metric: syllabus count + growth rate by month (admin, manager)")
    fun syllabi(
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) month: Int?,
    ): ResponseEntity<BaseResponse<DashboardMetricResponse>> {
        val result = dashboardService.getSyllabusMetrics(year, month)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/vocabularies")
    @Operation(summary = "Dashboard metric: vocabulary count + growth rate by month (admin, manager)")
    fun vocabularies(
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) month: Int?,
    ): ResponseEntity<BaseResponse<DashboardMetricResponse>> {
        val result = dashboardService.getVocabularyMetrics(year, month)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/active-enrollments")
    @Operation(summary = "Dashboard metric: active VIP enrollments + growth rate (admin, manager)")
    fun activeEnrollments(
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) month: Int?,
    ): ResponseEntity<BaseResponse<DashboardMetricResponse>> {
        val result = dashboardService.getActiveEnrollmentMetrics(year, month)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/revenue")
    @Operation(summary = "Dashboard metric: revenue + growth rate by month (admin, manager)")
    fun revenue(
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) month: Int?,
    ): ResponseEntity<BaseResponse<DashboardMetricResponse>> {
        val result = dashboardService.getRevenueMetrics(year, month)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/growth-rates")
    @Operation(summary = "Dashboard metric: growth rates of users/syllabi/vocabularies/active enrollments/revenue (admin, manager)")
    fun growthRates(
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) month: Int?,
    ): ResponseEntity<BaseResponse<DashboardGrowthRatesResponse>> {
        val result = dashboardService.getGrowthRates(year, month)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
