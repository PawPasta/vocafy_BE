package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.EnrollmentCreateRequest
import com.exe.vocafy_BE.model.dto.request.EnrollmentFocusRequest
import com.exe.vocafy_BE.model.dto.request.EnrollmentPreferredTargetLanguageRequest
import com.exe.vocafy_BE.model.dto.response.EnrolledSyllabusResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.EnrollmentResponse
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse
import org.springframework.data.domain.Pageable

interface EnrollmentService {
    fun register(request: EnrollmentCreateRequest): ServiceResult<EnrollmentResponse>
    fun getFocusedSyllabus(): ServiceResult<SyllabusResponse>
    fun focus(request: EnrollmentFocusRequest): ServiceResult<EnrollmentResponse>
    fun updatePreferredTargetLanguage(request: EnrollmentPreferredTargetLanguageRequest): ServiceResult<EnrollmentResponse>
    fun listEnrolledSyllabuses(pageable: Pageable): ServiceResult<PageResponse<EnrolledSyllabusResponse>>
}
