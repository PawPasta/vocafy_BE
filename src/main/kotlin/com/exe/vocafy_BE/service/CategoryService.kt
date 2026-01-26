package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.CategoryCreateRequest
import com.exe.vocafy_BE.model.dto.request.CategoryUpdateRequest
import com.exe.vocafy_BE.model.dto.response.CategoryResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import org.springframework.data.domain.Pageable

interface CategoryService {
    fun create(request: CategoryCreateRequest): ServiceResult<CategoryResponse>
    fun getById(id: Long): ServiceResult<CategoryResponse>
    fun list(name: String?, pageable: Pageable): ServiceResult<PageResponse<CategoryResponse>>
    fun update(id: Long, request: CategoryUpdateRequest): ServiceResult<CategoryResponse>
    fun delete(id: Long): ServiceResult<Unit>
}
