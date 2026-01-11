package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.CourseCreateRequest
import com.exe.vocafy_BE.model.dto.request.CourseUpdateRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.CourseResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.CourseService
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

@Tag(name = "Courses")
@RestController
@RequestMapping("/courses")
class CourseController(
    private val courseService: CourseService,
) {

    @PostMapping
    fun create(@Valid @RequestBody request: CourseCreateRequest): ResponseEntity<BaseResponse<CourseResponse>> {
        val result = courseService.create(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<BaseResponse<CourseResponse>> {
        val result = courseService.getById(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping
    fun list(): ResponseEntity<BaseResponse<List<CourseResponse>>> {
        val result = courseService.list()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CourseUpdateRequest,
    ): ResponseEntity<BaseResponse<CourseResponse>> {
        val result = courseService.update(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
