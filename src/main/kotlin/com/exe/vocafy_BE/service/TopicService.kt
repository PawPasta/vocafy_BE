package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.TopicCreateRequest
import com.exe.vocafy_BE.model.dto.request.TopicUpdateRequest
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.TopicResponse

interface TopicService {
    fun create(request: TopicCreateRequest): ServiceResult<TopicResponse>
    fun getById(id: Long): ServiceResult<TopicResponse>
    fun list(): ServiceResult<List<TopicResponse>>
    fun listBySyllabusId(syllabusId: Long): ServiceResult<List<TopicResponse>>
    fun update(id: Long, request: TopicUpdateRequest): ServiceResult<TopicResponse>
    fun delete(id: Long): ServiceResult<Unit>
}

