package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.FeedbackCreateRequest
import com.exe.vocafy_BE.model.dto.request.FeedbackReplyRequest
import com.exe.vocafy_BE.model.dto.response.FeedbackRatingSummaryResponse
import com.exe.vocafy_BE.model.dto.response.FeedbackResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import org.springframework.data.domain.Pageable

interface FeedbackService {
    fun create(request: FeedbackCreateRequest): ServiceResult<FeedbackResponse>
    fun listMyFeedbacks(pageable: Pageable): ServiceResult<PageResponse<FeedbackResponse>>
    fun listAllFeedbacks(pageable: Pageable): ServiceResult<PageResponse<FeedbackResponse>>
    fun listAllFeedbacksForAdmin(pageable: Pageable): ServiceResult<PageResponse<FeedbackResponse>>
    fun replyFeedback(feedbackId: Long, request: FeedbackReplyRequest): ServiceResult<FeedbackResponse>
    fun getRatingSummary(): ServiceResult<FeedbackRatingSummaryResponse>
}
