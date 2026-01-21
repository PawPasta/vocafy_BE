package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.SepayWebhookRequest

interface SepayWebhookService {
    fun handleWebhook(request: SepayWebhookRequest): Map<String, Any>
}
