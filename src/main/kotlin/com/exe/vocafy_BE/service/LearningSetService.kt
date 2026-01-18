package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.LearningSetGenerateRequest
import com.exe.vocafy_BE.model.dto.request.LearningSetCompleteRequest
import com.exe.vocafy_BE.model.dto.response.LearningSetResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetCompleteResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult

interface LearningSetService {
    fun generate(request: LearningSetGenerateRequest): ServiceResult<LearningSetResponse>
    fun complete(request: LearningSetCompleteRequest): ServiceResult<LearningSetCompleteResponse>
    fun viewCourseVocabularySet(courseId: Long): ServiceResult<LearningSetResponse>
}
