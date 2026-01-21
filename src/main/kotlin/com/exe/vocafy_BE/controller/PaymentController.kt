package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.PaymentUrlResponse
import com.exe.vocafy_BE.model.dto.response.PremiumPackageResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Payments")
@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService,
) {

    @GetMapping("/packages")
    @Operation(summary = "Get all active premium packages")
    fun getActivePackages(): ResponseEntity<BaseResponse<List<PremiumPackageResponse>>> {
        val result = paymentService.getActivePackages()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PostMapping("/subscribe/{packageId}")
    @Operation(summary = "Generate payment URL for subscription")
    fun generatePaymentUrl(
        @PathVariable packageId: Long,
    ): ResponseEntity<BaseResponse<PaymentUrlResponse>> {
        val result = paymentService.generatePaymentUrl(packageId)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
