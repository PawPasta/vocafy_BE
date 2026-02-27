package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.PaymentTransactionCheckResponse
import com.exe.vocafy_BE.model.dto.response.PaymentUrlResponse
import com.exe.vocafy_BE.model.dto.response.PremiumPackageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import org.springframework.data.domain.Pageable

interface PaymentService {
    fun getActivePackages(pageable: Pageable): ServiceResult<PageResponse<PremiumPackageResponse>>
    fun generatePaymentUrl(packageId: Long): ServiceResult<PaymentUrlResponse>
    fun checkMyPaymentTransaction(): ServiceResult<PaymentTransactionCheckResponse>
}
