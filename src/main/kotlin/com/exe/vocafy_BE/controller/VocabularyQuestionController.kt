package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.model.dto.response.VocabularyQuestionResponse
import com.exe.vocafy_BE.service.VocabularyQuestionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Vocabulary Questions")
@RestController
@RequestMapping("/vocabulary-questions")
class VocabularyQuestionController(
    private val vocabularyQuestionService: VocabularyQuestionService,
) {

    @GetMapping("/random")
    @Operation(summary = "Get random vocabulary question (all)")
    fun getRandom(): ResponseEntity<BaseResponse<VocabularyQuestionResponse>> {
        val result = vocabularyQuestionService.getRandom()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
