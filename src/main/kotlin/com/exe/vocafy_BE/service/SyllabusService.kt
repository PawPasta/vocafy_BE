package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.SyllabusActiveRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusCreateRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusUpdateRequest
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse

interface SyllabusService {
    fun create(request: SyllabusCreateRequest): ServiceResult<SyllabusResponse>
    fun getById(id: Long): ServiceResult<SyllabusResponse>
    fun list(): ServiceResult<List<SyllabusResponse>>
    fun update(id: Long, request: SyllabusUpdateRequest): ServiceResult<SyllabusResponse>
    fun updateActive(id: Long, request: SyllabusActiveRequest): ServiceResult<SyllabusResponse>
}
