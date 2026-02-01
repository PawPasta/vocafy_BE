package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.CourseCreateRequest
import com.exe.vocafy_BE.model.dto.request.CourseUpdateRequest
import com.exe.vocafy_BE.model.dto.response.CourseResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import org.springframework.data.domain.Pageable

interface CourseService {
    fun create(request: CourseCreateRequest): ServiceResult<CourseResponse>
    fun getById(id: Long): ServiceResult<CourseResponse>
    fun list(pageable: Pageable): ServiceResult<PageResponse<CourseResponse>>
    fun listByTopicId(topicId: Long, pageable: Pageable): ServiceResult<PageResponse<CourseResponse>>
    fun update(id: Long, request: CourseUpdateRequest): ServiceResult<CourseResponse>
    fun delete(id: Long): ServiceResult<Unit>
    fun attachVocabularies(id: Long, vocabularyIds: List<Long>): ServiceResult<Unit>
    fun detachVocabulary(id: Long, vocabularyId: Long): ServiceResult<Unit>
}
