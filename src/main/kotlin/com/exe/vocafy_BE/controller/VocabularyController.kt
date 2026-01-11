package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import com.exe.vocafy_BE.service.VocabularyService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Vocabularies")
@RestController
@RequestMapping("/vocabularies")
class VocabularyController(
    private val vocabularyService: VocabularyService,
) {

    @PostMapping
    fun create(@Valid @RequestBody request: VocabularyCreateRequest): ResponseEntity<BaseResponse<VocabularyResponse>> {
        val result = vocabularyService.create(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<BaseResponse<VocabularyResponse>> {
        val result = vocabularyService.getById(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping
    fun list(): ResponseEntity<BaseResponse<List<VocabularyResponse>>> {
        val result = vocabularyService.list()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: VocabularyUpdateRequest,
    ): ResponseEntity<BaseResponse<VocabularyResponse>> {
        val result = vocabularyService.update(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
