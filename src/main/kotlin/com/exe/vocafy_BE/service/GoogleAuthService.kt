package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.response.LoginResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult

interface GoogleAuthService {
    fun login(idToken: String): ServiceResult<LoginResponse>
    fun refresh(refreshToken: String): ServiceResult<LoginResponse>
}
