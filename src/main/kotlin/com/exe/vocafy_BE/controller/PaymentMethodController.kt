package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.PaymentMethodActiveRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodCreateRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodUpdateRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.PaymentMethodResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.PaymentMethodService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Payment Methods")
@RestController
@RequestMapping("/api/payment-methods")
class PaymentMethodController(
    private val paymentMethodService: PaymentMethodService,
) {

    @PostMapping
    @Operation(summary = "Create payment method (admin, manager)")
    fun create(@Valid @RequestBody request: PaymentMethodCreateRequest): ResponseEntity<BaseResponse<PaymentMethodResponse>> {
        val result = paymentMethodService.create(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment method by id (all)")
    fun getById(@PathVariable id: Long): ResponseEntity<BaseResponse<PaymentMethodResponse>> {
        val result = paymentMethodService.getById(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping
    @Operation(summary = "List payment methods (all)")
    fun list(): ResponseEntity<BaseResponse<List<PaymentMethodResponse>>> {
        val result = paymentMethodService.list()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment method (admin, manager)")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: PaymentMethodUpdateRequest,
    ): ResponseEntity<BaseResponse<PaymentMethodResponse>> {
        val result = paymentMethodService.update(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PatchMapping("/{id}/active")
    @Operation(summary = "Toggle payment method active (admin, manager)")
    fun updateActive(
        @PathVariable id: Long,
        @Valid @RequestBody request: PaymentMethodActiveRequest,
    ): ResponseEntity<BaseResponse<PaymentMethodResponse>> {
        val result = paymentMethodService.updateActive(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
