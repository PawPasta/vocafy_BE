package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.TopicCreateRequest
import com.exe.vocafy_BE.model.dto.request.TopicUpdateRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.model.dto.response.TopicResponse
import com.exe.vocafy_BE.service.TopicService
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

@Tag(name = "Topics")
@RestController
@RequestMapping("/api/topics")
class TopicController(
    private val topicService: TopicService,
) {

    @PostMapping
    @Operation(summary = "Create topic with optional nested courses and vocabularies (admin, manager)")
    fun create(@Valid @RequestBody request: TopicCreateRequest): ResponseEntity<BaseResponse<TopicResponse>> {
        val result = topicService.create(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get topic by id (all)")
    fun getById(@PathVariable id: Long): ResponseEntity<BaseResponse<TopicResponse>> {
        val result = topicService.getById(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping
    @Operation(summary = "List all topics (all)")
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<BaseResponse<PageResponse<TopicResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = topicService.list(pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/by-syllabus/{syllabusId}")
    @Operation(summary = "List topics by syllabus_id (all)")
    fun listBySyllabusId(
        @PathVariable syllabusId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<BaseResponse<PageResponse<TopicResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = topicService.listBySyllabusId(syllabusId, pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update topic with optional nested courses and vocabularies (admin, manager)")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: TopicUpdateRequest,
    ): ResponseEntity<BaseResponse<TopicResponse>> {
        val result = topicService.update(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete topic and all nested courses and vocabularies (admin, manager)")
    fun delete(@PathVariable id: Long): ResponseEntity<BaseResponse<Unit>> {
        val result = topicService.delete(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}

