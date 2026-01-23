package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.model.dto.response.SubscriptionTransactionResponse
import com.exe.vocafy_BE.service.SubscriptionTransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Subscription Transactions")
@RestController
@RequestMapping("/api/subscription-transactions")
class SubscriptionTransactionController(
    private val subscriptionTransactionService: SubscriptionTransactionService,
) {

    @GetMapping
    @Operation(summary = "Get all subscription transactions (all fields) - paginated")
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<BaseResponse<PageResponse<SubscriptionTransactionResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = subscriptionTransactionService.getAll(pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}

