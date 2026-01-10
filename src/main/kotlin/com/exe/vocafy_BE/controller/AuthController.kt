package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.GoogleLoginRequest
import com.exe.vocafy_BE.model.dto.request.RefreshTokenRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.LoginResponse
import com.exe.vocafy_BE.service.GoogleAuthService
import org.springframework.http.ResponseEntity
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@Tag(name = "Auth")
@RestController
class AuthController(
    private val googleAuthService: GoogleAuthService,
) {

    @PostMapping("/auth/google")
    fun loginWithGoogle(@Valid @RequestBody request: GoogleLoginRequest): ResponseEntity<BaseResponse<LoginResponse>> {
        val tokens = googleAuthService.login(request.idToken.orEmpty())
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "ok",
                result = tokens,
            )
        )
    }

    @PostMapping("/auth/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<BaseResponse<LoginResponse>> {
        val tokens = googleAuthService.refresh(request.refreshToken.orEmpty())
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "ok",
                result = tokens,
            )
        )
    }
}
