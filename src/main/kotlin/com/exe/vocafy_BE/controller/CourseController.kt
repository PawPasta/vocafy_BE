package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.CourseCreateRequest
import com.exe.vocafy_BE.model.dto.request.CourseUpdateRequest
import com.exe.vocafy_BE.model.dto.request.CourseVocabularyLinkRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.CourseResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.CourseService
import com.exe.vocafy_BE.service.LearningSetService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Courses")
@RestController
@RequestMapping("/api/courses")
class CourseController(
    private val courseService: CourseService,
    private val learningSetService: LearningSetService,
) {

    @PostMapping
    @Operation(summary = "Create course with optional nested vocabularies (admin, manager)")
    fun create(@Valid @RequestBody request: CourseCreateRequest): ResponseEntity<BaseResponse<CourseResponse>> {
        val result = courseService.create(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by id (all)")
    fun getById(@PathVariable id: Long): ResponseEntity<BaseResponse<CourseResponse>> {
        val result = courseService.getById(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping
    @Operation(summary = "List all courses (all)")
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<BaseResponse<PageResponse<CourseResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = courseService.list(pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/by-topic/{topicId}")
    @Operation(summary = "List courses by topic_id (all)")
    fun listByTopicId(
        @PathVariable topicId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<BaseResponse<PageResponse<CourseResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = courseService.listByTopicId(topicId, pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{id}/vocabulary-set")
    @Operation(summary = "Get course vocabulary set (all)")
    fun getVocabularySet(@PathVariable id: Long): ResponseEntity<BaseResponse<LearningSetResponse>> {
        val result = learningSetService.viewCourseVocabularySet(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course with optional nested vocabularies (admin, manager)")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CourseUpdateRequest,
    ): ResponseEntity<BaseResponse<CourseResponse>> {
        val result = courseService.update(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course and all nested vocabularies (admin, manager)")
    fun delete(@PathVariable id: Long): ResponseEntity<BaseResponse<Unit>> {
        val result = courseService.delete(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PostMapping("/{id}/vocabularies/attach")
    @Operation(summary = "Attach vocabularies to course (admin, manager)")
    fun attachVocabularies(
        @PathVariable id: Long,
        @Valid @RequestBody request: CourseVocabularyLinkRequest,
    ): ResponseEntity<BaseResponse<Unit>> {
        val result = courseService.attachVocabularies(id, request.vocabularyIds)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @DeleteMapping("/{id}/vocabularies/{vocabularyId}")
    @Operation(summary = "Detach vocabulary from course (admin, manager)")
    fun detachVocabulary(
        @PathVariable id: Long,
        @PathVariable vocabularyId: Long,
    ): ResponseEntity<BaseResponse<Unit>> {
        val result = courseService.detachVocabulary(id, vocabularyId)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
