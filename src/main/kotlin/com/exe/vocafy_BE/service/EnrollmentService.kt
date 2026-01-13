package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.EnrollmentCreateRequest
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.EnrollmentResponse

interface EnrollmentService {
    fun register(request: EnrollmentCreateRequest): ServiceResult<EnrollmentResponse>
}
