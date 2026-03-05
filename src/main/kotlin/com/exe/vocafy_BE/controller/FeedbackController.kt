package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.FeedbackCreateRequest
import com.exe.vocafy_BE.model.dto.request.FeedbackReplyRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.FeedbackRatingSummaryResponse
import com.exe.vocafy_BE.model.dto.response.FeedbackResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.FeedbackService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Feedbacks")
@RestController
@RequestMapping("/api/feedbacks")
class FeedbackController(
    private val feedbackService: FeedbackService,
) {

    @PostMapping
    @Operation(summary = "Create app feedback (all)")
    fun create(@Valid @RequestBody request: FeedbackCreateRequest): ResponseEntity<BaseResponse<FeedbackResponse>> {
        val result = feedbackService.create(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping
    @Operation(summary = "List all feedbacks (read-only for all)")
    fun listAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<BaseResponse<PageResponse<FeedbackResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = feedbackService.listAllFeedbacks(pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/me")
    @Operation(summary = "List my feedbacks (all)")
    fun myFeedbacks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<BaseResponse<PageResponse<FeedbackResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = feedbackService.listMyFeedbacks(pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/admin")
    @Operation(summary = "List all feedbacks (admin)")
    fun listAllForAdmin(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<BaseResponse<PageResponse<FeedbackResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = feedbackService.listAllFeedbacksForAdmin(pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{id}/reply")
    @Operation(summary = "Reply feedback or edit own reply (admin)")
    fun reply(
        @PathVariable id: Long,
        @Valid @RequestBody request: FeedbackReplyRequest,
    ): ResponseEntity<BaseResponse<FeedbackResponse>> {
        val result = feedbackService.replyFeedback(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/admin/rating-summary")
    @Operation(summary = "Get rating summary by stars (admin)")
    fun ratingSummary(): ResponseEntity<BaseResponse<FeedbackRatingSummaryResponse>> {
        val result = feedbackService.getRatingSummary()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
