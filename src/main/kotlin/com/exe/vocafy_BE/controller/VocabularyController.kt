package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import com.exe.vocafy_BE.service.VocabularyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Vocabularies")
@RestController
@RequestMapping("/api/vocabularies")
class VocabularyController(
    private val vocabularyService: VocabularyService,
) {

    @PostMapping
    @Operation(summary = "Create vocabulary with terms, meanings, and medias (admin, manager)")
    fun create(@Valid @RequestBody request: VocabularyCreateRequest): ResponseEntity<BaseResponse<VocabularyResponse>> {
        val result = vocabularyService.create(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vocabulary by id (all)")
    fun getById(@PathVariable id: Long): ResponseEntity<BaseResponse<VocabularyResponse>> {
        val result = vocabularyService.getById(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping
    @Operation(summary = "List all vocabularies (all)")
    fun list(): ResponseEntity<BaseResponse<List<VocabularyResponse>>> {
        val result = vocabularyService.list()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/by-course/{courseId}")
    @Operation(summary = "List vocabularies by course_id (all)")
    fun listByCourseId(@PathVariable courseId: Long): ResponseEntity<BaseResponse<List<VocabularyResponse>>> {
        val result = vocabularyService.listByCourseId(courseId)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vocabulary with terms, meanings, and medias (admin, manager)")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: VocabularyUpdateRequest,
    ): ResponseEntity<BaseResponse<VocabularyResponse>> {
        val result = vocabularyService.update(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vocabulary and all nested terms, meanings, medias (admin, manager)")
    fun delete(@PathVariable id: Long): ResponseEntity<BaseResponse<Unit>> {
        val result = vocabularyService.delete(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
