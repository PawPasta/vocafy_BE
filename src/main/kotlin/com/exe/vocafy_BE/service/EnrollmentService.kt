package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.EnrollmentCreateRequest
import com.exe.vocafy_BE.model.dto.request.EnrollmentFocusRequest
import com.exe.vocafy_BE.model.dto.response.EnrolledSyllabusResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.EnrollmentResponse
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse

interface EnrollmentService {
    fun register(request: EnrollmentCreateRequest): ServiceResult<EnrollmentResponse>
    fun getFocusedSyllabus(): ServiceResult<SyllabusResponse>
    fun focus(request: EnrollmentFocusRequest): ServiceResult<EnrollmentResponse>
    fun listEnrolledSyllabuses(): ServiceResult<List<EnrolledSyllabusResponse>>
}
