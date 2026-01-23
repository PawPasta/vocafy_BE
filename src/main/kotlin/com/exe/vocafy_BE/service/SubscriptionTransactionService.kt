package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.SubscriptionTransactionResponse
import org.springframework.data.domain.Pageable

interface SubscriptionTransactionService {
    fun getAll(pageable: Pageable): ServiceResult<PageResponse<SubscriptionTransactionResponse>>
}

