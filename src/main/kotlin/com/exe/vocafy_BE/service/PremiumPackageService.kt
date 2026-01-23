package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.PremiumPackageCreateRequest
import com.exe.vocafy_BE.model.dto.request.PremiumPackageUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.PremiumPackageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import org.springframework.data.domain.Pageable

interface PremiumPackageService {
    fun getAll(pageable: Pageable): ServiceResult<PageResponse<PremiumPackageResponse>>
    fun getById(id: Long): ServiceResult<PremiumPackageResponse>
    fun create(request: PremiumPackageCreateRequest): ServiceResult<PremiumPackageResponse>
    fun update(id: Long, request: PremiumPackageUpdateRequest): ServiceResult<PremiumPackageResponse>
    fun delete(id: Long): ServiceResult<Unit>
}
