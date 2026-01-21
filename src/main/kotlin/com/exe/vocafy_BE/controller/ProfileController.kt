package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.ProfileResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.ProfileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.exe.vocafy_BE.model.dto.request.ProfileUpdateRequest

@Tag(name = "Profiles")
@RestController
@RequestMapping("/api/profiles")
class ProfileController(
    private val profileService: ProfileService,
) {

    @GetMapping("/me")
    @Operation(summary = "Get my profile (all)")
    fun getMe(): ResponseEntity<BaseResponse<ProfileResponse>> {
        val result = profileService.getMe()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get profile by user id (all)")
    fun getByUserId(@PathVariable userId: String): ResponseEntity<BaseResponse<ProfileResponse>> {
        val result = profileService.getByUserId(userId)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile (all)")
    fun updateMe(@Valid @RequestBody request: ProfileUpdateRequest): ResponseEntity<BaseResponse<ProfileResponse>> {
        val result = profileService.updateMe(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
