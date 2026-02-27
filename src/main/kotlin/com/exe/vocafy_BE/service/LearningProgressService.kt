package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.LearningAnswerRequest
import com.exe.vocafy_BE.model.dto.response.LearningStateUpdateResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult

interface LearningProgressService {
    fun submitAnswer(request: LearningAnswerRequest): ServiceResult<LearningStateUpdateResponse>
}
