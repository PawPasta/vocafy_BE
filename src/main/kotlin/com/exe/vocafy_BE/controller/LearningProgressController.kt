package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.LearningAnswerRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.LearningStateUpdateResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.LearningProgressService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Learning Progress")
@RestController
@RequestMapping("/api/learning-progress")
class LearningProgressController(
    private val learningProgressService: LearningProgressService,
) {

    @PostMapping("/answer")
    @Operation(summary = "Submit answer to update learning state (all)")
    fun submitAnswer(
        @Valid @RequestBody request: LearningAnswerRequest,
    ): ResponseEntity<BaseResponse<LearningStateUpdateResponse>> {
        val result = learningProgressService.submitAnswer(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
