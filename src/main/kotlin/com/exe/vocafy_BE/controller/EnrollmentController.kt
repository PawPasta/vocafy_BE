package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.EnrollmentCreateRequest
import com.exe.vocafy_BE.model.dto.request.EnrollmentFocusRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.model.dto.response.EnrollmentResponse
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse
import com.exe.vocafy_BE.service.EnrollmentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Enrollments")
@RestController
@RequestMapping("/enrollments")
class EnrollmentController(
    private val enrollmentService: EnrollmentService,
) {

    @PostMapping
    @Operation(summary = "Register to syllabus (all)")
    fun register(@Valid @RequestBody request: EnrollmentCreateRequest): ResponseEntity<BaseResponse<EnrollmentResponse>> {
        val result = enrollmentService.register(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/focused")
    @Operation(summary = "Get focused syllabus (all)")
    fun getFocusedSyllabus(): ResponseEntity<BaseResponse<SyllabusResponse>> {
        val result = enrollmentService.getFocusedSyllabus()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PatchMapping("/focused")
    @Operation(summary = "Set focused syllabus (all)")
    fun focus(@Valid @RequestBody request: EnrollmentFocusRequest): ResponseEntity<BaseResponse<EnrollmentResponse>> {
        val result = enrollmentService.focus(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
