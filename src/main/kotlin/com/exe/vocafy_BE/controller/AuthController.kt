package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.GoogleLoginRequest
import com.exe.vocafy_BE.model.dto.request.RefreshTokenRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.LoginResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.GoogleAuthService
import org.springframework.http.ResponseEntity
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RequestMapping
import jakarta.servlet.http.HttpServletRequest

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val googleAuthService: GoogleAuthService,
) {

    @PostMapping("/firebase")
    @Operation(summary = "Login with Firebase ID token (all)")
    fun loginWithFirebase(@Valid @RequestBody request: GoogleLoginRequest): ResponseEntity<BaseResponse<LoginResponse>> {
        val tokens = googleAuthService.login(request.idToken.orEmpty(), request.fcmToken)
        return ResponseEntity.ok(
            ResponseFactory.success(tokens)
        )
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token (all)")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<BaseResponse<LoginResponse>> {
        val tokens = googleAuthService.refresh(request.refreshToken.orEmpty())
        return ResponseEntity.ok(
            ResponseFactory.success(tokens)
        )
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout (authenticated)")
    fun logout(request: HttpServletRequest): ResponseEntity<BaseResponse<Unit>> {
        val authHeader = request.getHeader("Authorization")
        val accessToken = if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else {
            ""
        }
        val result = googleAuthService.logout(accessToken)
        return ResponseEntity.ok(
            ResponseFactory.success(result)
        )
    }
}
