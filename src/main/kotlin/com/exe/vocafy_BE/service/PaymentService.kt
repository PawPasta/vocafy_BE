package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.response.PaymentUrlResponse
import com.exe.vocafy_BE.model.dto.response.PremiumPackageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult

interface PaymentService {
    fun getActivePackages(): ServiceResult<List<PremiumPackageResponse>>
    fun generatePaymentUrl(packageId: Long): ServiceResult<PaymentUrlResponse>
}
