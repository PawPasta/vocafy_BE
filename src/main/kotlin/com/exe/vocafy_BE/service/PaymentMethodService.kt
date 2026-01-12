package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.PaymentMethodActiveRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodCreateRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PaymentMethodResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult

interface PaymentMethodService {
    fun create(request: PaymentMethodCreateRequest): ServiceResult<PaymentMethodResponse>
    fun getById(id: Long): ServiceResult<PaymentMethodResponse>
    fun list(): ServiceResult<List<PaymentMethodResponse>>
    fun update(id: Long, request: PaymentMethodUpdateRequest): ServiceResult<PaymentMethodResponse>
    fun updateActive(id: Long, request: PaymentMethodActiveRequest): ServiceResult<PaymentMethodResponse>
}
