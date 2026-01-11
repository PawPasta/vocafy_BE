package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse

interface VocabularyService {
    fun create(request: VocabularyCreateRequest): ServiceResult<VocabularyResponse>
    fun getById(id: Long): ServiceResult<VocabularyResponse>
    fun list(): ServiceResult<List<VocabularyResponse>>
    fun update(id: Long, request: VocabularyUpdateRequest): ServiceResult<VocabularyResponse>
}
