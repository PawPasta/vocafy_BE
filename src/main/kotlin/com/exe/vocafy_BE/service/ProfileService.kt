package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.ProfileUpdateRequest
import com.exe.vocafy_BE.model.dto.response.ProfileResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult

interface ProfileService {
    fun getByUserId(userId: String): ServiceResult<ProfileResponse>
    fun updateMe(request: ProfileUpdateRequest): ServiceResult<ProfileResponse>
    fun getMe(): ServiceResult<ProfileResponse>
}
