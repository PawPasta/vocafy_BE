package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.FeedbackMapper
import com.exe.vocafy_BE.model.dto.request.FeedbackCreateRequest
import com.exe.vocafy_BE.model.dto.request.FeedbackReplyRequest
import com.exe.vocafy_BE.model.dto.response.FeedbackRatingSummaryResponse
import com.exe.vocafy_BE.model.dto.response.FeedbackResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.repo.FeedbackRepository
import com.exe.vocafy_BE.service.FeedbackService
import com.exe.vocafy_BE.util.SecurityUtil
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedbackServiceImpl(
    private val securityUtil: SecurityUtil,
    private val feedbackRepository: FeedbackRepository,
) : FeedbackService {

    @Transactional
    override fun create(request: FeedbackCreateRequest): ServiceResult<FeedbackResponse> {
        val user = securityUtil.getCurrentUser()
        val saved = feedbackRepository.save(FeedbackMapper.toEntity(request, user))
        return ServiceResult(
            message = "Created",
            result = FeedbackMapper.toResponse(saved),
        )
    }

    @Transactional(readOnly = true)
    override fun listMyFeedbacks(pageable: Pageable): ServiceResult<PageResponse<FeedbackResponse>> {
        val userId = securityUtil.getCurrentUserId()
        val page = feedbackRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
        val items = page.content.map(FeedbackMapper::toResponse)
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = items,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                isFirst = page.isFirst,
                isLast = page.isLast,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun listAllFeedbacks(pageable: Pageable): ServiceResult<PageResponse<FeedbackResponse>> {
        val page = feedbackRepository.findAllByOrderByCreatedAtDesc(pageable)
        val items = page.content.map(FeedbackMapper::toResponse)
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = items,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                isFirst = page.isFirst,
                isLast = page.isLast,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun listAllFeedbacksForAdmin(pageable: Pageable): ServiceResult<PageResponse<FeedbackResponse>> {
        ensureAdmin()
        val page = feedbackRepository.findAllByOrderByCreatedAtDesc(pageable)
        val items = page.content.map(FeedbackMapper::toResponse)
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = items,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                isFirst = page.isFirst,
                isLast = page.isLast,
            ),
        )
    }

    @Transactional
    override fun replyFeedback(feedbackId: Long, request: FeedbackReplyRequest): ServiceResult<FeedbackResponse> {
        ensureAdmin()
        val admin = securityUtil.getCurrentUser()
        val adminId = admin.id ?: throw BaseException.NotFoundException("Admin not found")
        val entity = feedbackRepository.findById(feedbackId)
            .orElseThrow { BaseException.NotFoundException("Feedback not found") }

        val repliedById = entity.repliedBy?.id
        if (repliedById != null && repliedById != adminId) {
            throw BaseException.ForbiddenException("You can only edit your own reply")
        }

        val adminReply = request.adminReply?.trim()
            ?: throw BaseException.BadRequestException("'admin_reply' can't be blank")
        if (adminReply.isBlank()) {
            throw BaseException.BadRequestException("'admin_reply' can't be blank")
        }

        val updated = feedbackRepository.save(FeedbackMapper.applyAdminReply(entity, admin, adminReply))
        return ServiceResult(
            message = if (repliedById == null) "Replied" else "Reply updated",
            result = FeedbackMapper.toResponse(updated),
        )
    }

    @Transactional(readOnly = true)
    override fun getRatingSummary(): ServiceResult<FeedbackRatingSummaryResponse> {
        ensureAdmin()
        val rating5 = feedbackRepository.countByRating(5)
        val rating4 = feedbackRepository.countByRating(4)
        val rating3 = feedbackRepository.countByRating(3)
        val rating2 = feedbackRepository.countByRating(2)
        val rating1 = feedbackRepository.countByRating(1)

        return ServiceResult(
            message = "Ok",
            result = FeedbackRatingSummaryResponse(
                totalRatings = rating5 + rating4 + rating3 + rating2 + rating1,
                rating5 = rating5,
                rating4 = rating4,
                rating3 = rating3,
                rating2 = rating2,
                rating1 = rating1,
            ),
        )
    }

    private fun ensureAdmin() {
        val role = securityUtil.getCurrentRole()
        if (role != Role.ADMIN.name) {
            throw BaseException.ForbiddenException("Forbidden")
        }
    }
}
