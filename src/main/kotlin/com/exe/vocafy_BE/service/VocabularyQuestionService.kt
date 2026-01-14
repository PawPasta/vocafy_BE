package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyQuestionResponse

interface VocabularyQuestionService {
    fun getRandom(): ServiceResult<VocabularyQuestionResponse>
}
