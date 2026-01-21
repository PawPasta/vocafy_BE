package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.model.dto.response.SubscriptionResponse
import com.exe.vocafy_BE.service.SubscriptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Subscriptions")
@RestController
@RequestMapping("/api/subscriptions")
class SubscriptionController(
    private val subscriptionService: SubscriptionService,
) {

    @GetMapping("/me")
    @Operation(summary = "Get my subscription (all)")
    fun getMe(): ResponseEntity<BaseResponse<SubscriptionResponse>> {
        val result = subscriptionService.getMe()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get subscription by user id (admin, manager)")
    fun getByUserId(@PathVariable userId: String): ResponseEntity<BaseResponse<SubscriptionResponse>> {
        val result = subscriptionService.getByUserId(userId)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
