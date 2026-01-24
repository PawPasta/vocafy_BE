package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.MyProfileResponse
import com.exe.vocafy_BE.model.dto.response.MyProfileUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.model.dto.response.UserResponse
import com.exe.vocafy_BE.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Users")
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {

    @GetMapping
    @Operation(summary = "Get all users (User + Profile) - paginated")
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<BaseResponse<PageResponse<UserResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = userService.getAll(pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile (User + Profile + streak)")
    fun me(): ResponseEntity<BaseResponse<MyProfileResponse>> {
        val result = userService.getMyProfile()
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile (display_name, avatar_url)")
    fun updateMe(@Valid @RequestBody request: MyProfileUpdateRequest): ResponseEntity<BaseResponse<MyProfileResponse>> {
        val result = userService.updateMyProfile(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
