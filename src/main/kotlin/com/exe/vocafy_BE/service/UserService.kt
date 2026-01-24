package com.exe.vocafy_BE.service


import com.exe.vocafy_BE.model.dto.response.MyProfileResponse
import com.exe.vocafy_BE.model.dto.response.MyProfileUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.UserResponse
import org.springframework.data.domain.Pageable

interface UserService {
    fun getAll(pageable: Pageable): ServiceResult<PageResponse<UserResponse>>

    fun getMyProfile(): ServiceResult<MyProfileResponse>

    fun updateMyProfile(request: MyProfileUpdateRequest): ServiceResult<MyProfileResponse>
}
