package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.SyllabusActiveRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusCreateRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusUpdateRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse
import com.exe.vocafy_BE.service.SyllabusService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Syllabus")
@RestController
@RequestMapping("/api/syllabi")
class SyllabusController(
    private val syllabusService: SyllabusService,
) {

    @PostMapping
    @Operation(summary = "Create syllabus (admin, manager)")
    fun create(@Valid @RequestBody request: SyllabusCreateRequest): ResponseEntity<BaseResponse<SyllabusResponse>> {
        val result = syllabusService.create(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get syllabus by id (all)")
    fun getById(@PathVariable id: Long): ResponseEntity<BaseResponse<SyllabusResponse>> {
        val result = syllabusService.getById(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping
    @Operation(summary = "List syllabi (all)")
    fun list(): ResponseEntity<BaseResponse<List<SyllabusResponse>>> {
        val result = syllabusService.list()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update syllabus (admin, manager)")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: SyllabusUpdateRequest,
    ): ResponseEntity<BaseResponse<SyllabusResponse>> {
        val result = syllabusService.update(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PatchMapping("/{id}/active")
    @Operation(summary = "Update syllabus active status (admin, manager)")
    fun updateActive(
        @PathVariable id: Long,
        @Valid @RequestBody request: SyllabusActiveRequest,
    ): ResponseEntity<BaseResponse<SyllabusResponse>> {
        val result = syllabusService.updateActive(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
