package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.LearningSetCompleteRequest
import com.exe.vocafy_BE.model.dto.request.LearningSetGenerateRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetCompleteResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.LearningSetService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Learning Sets")
@RestController
@RequestMapping("/learning-sets")
class LearningSetController(
    private val learningSetService: LearningSetService,
) {

    @PostMapping
    @Operation(summary = "Generate learning set (all)")
    fun generate(
        @RequestBody(required = false) request: LearningSetGenerateRequest?,
    ): ResponseEntity<BaseResponse<LearningSetResponse>> {
        val result = learningSetService.generate(request ?: LearningSetGenerateRequest())
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PostMapping("/complete")
    @Operation(summary = "Complete learning set (all)")
    fun complete(
        @Valid @RequestBody request: LearningSetCompleteRequest,
    ): ResponseEntity<BaseResponse<LearningSetCompleteResponse>> {
        val result = learningSetService.complete(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
