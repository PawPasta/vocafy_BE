package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.SubscriptionResponse

interface SubscriptionService {
    fun getMe(): ServiceResult<SubscriptionResponse>
    fun getByUserId(userId: String): ServiceResult<SubscriptionResponse>
}
