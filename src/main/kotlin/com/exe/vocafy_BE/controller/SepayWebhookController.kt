package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.SepayWebhookRequest
import com.exe.vocafy_BE.service.SepayWebhookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Webhook")
@RestController
@RequestMapping("/api/webhook")
class SepayWebhookController(
    private val sepayWebhookService: SepayWebhookService,
) {

    private val logger = LoggerFactory.getLogger(SepayWebhookController::class.java)

    @PostMapping("/sepay")
    @Operation(summary = "Handle Sepay payment webhook (server-to-server)")
    fun handleSepayWebhook(
        @RequestBody request: SepayWebhookRequest,
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Received Sepay webhook request: $request")
        val result = sepayWebhookService.handleWebhook(request)
        return ResponseEntity.ok(result)
    }
}
