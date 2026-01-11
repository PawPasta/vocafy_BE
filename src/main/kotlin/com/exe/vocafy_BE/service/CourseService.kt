package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.CourseCreateRequest
import com.exe.vocafy_BE.model.dto.request.CourseUpdateRequest
import com.exe.vocafy_BE.model.dto.response.CourseResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult

interface CourseService {
    fun create(request: CourseCreateRequest): ServiceResult<CourseResponse>
    fun getById(id: Long): ServiceResult<CourseResponse>
    fun list(): ServiceResult<List<CourseResponse>>
    fun update(id: Long, request: CourseUpdateRequest): ServiceResult<CourseResponse>
}
