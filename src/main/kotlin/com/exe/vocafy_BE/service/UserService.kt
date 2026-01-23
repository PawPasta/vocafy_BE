package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.UserResponse
import org.springframework.data.domain.Pageable

interface UserService {
    fun getAll(pageable: Pageable): ServiceResult<PageResponse<UserResponse>>
}
