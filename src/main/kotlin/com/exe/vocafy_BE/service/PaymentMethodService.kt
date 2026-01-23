package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.PaymentMethodActiveRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodCreateRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.PaymentMethodResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import org.springframework.data.domain.Pageable

interface PaymentMethodService {
    fun create(request: PaymentMethodCreateRequest): ServiceResult<PaymentMethodResponse>
    fun getById(id: Long): ServiceResult<PaymentMethodResponse>
    fun list(pageable: Pageable): ServiceResult<PageResponse<PaymentMethodResponse>>
    fun update(id: Long, request: PaymentMethodUpdateRequest): ServiceResult<PaymentMethodResponse>
    fun updateActive(id: Long, request: PaymentMethodActiveRequest): ServiceResult<PaymentMethodResponse>
}
