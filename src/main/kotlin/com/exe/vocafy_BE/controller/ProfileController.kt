package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.ProfileResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.ProfileService
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
@RequestMapping("/profiles")
class ProfileController(
    private val profileService: ProfileService,
) {

    @GetMapping("/{userId}")
    fun getByUserId(@PathVariable userId: String): ResponseEntity<BaseResponse<ProfileResponse>> {
        val result = profileService.getByUserId(userId)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{userId}")
    fun update(
        @PathVariable userId: String,
        @Valid @RequestBody request: ProfileUpdateRequest,
    ): ResponseEntity<BaseResponse<ProfileResponse>> {
        val result = profileService.update(userId, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
