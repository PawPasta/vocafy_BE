package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.GoogleLoginRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.LoginResponse
import com.exe.vocafy_BE.service.GoogleAuthService
import com.exe.vocafy_BE.service.InvalidTokenException
import com.exe.vocafy_BE.service.MissingTokenException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val googleAuthService: GoogleAuthService,
) {

    @PostMapping("/auth/google")
    fun loginWithGoogle(@RequestBody request: GoogleLoginRequest): ResponseEntity<BaseResponse<LoginResponse>> {
        val idToken = request.idToken.orEmpty()
        return try {
            val token = googleAuthService.login(idToken)
            ResponseEntity.ok(
                BaseResponse(
                    statusCode = 200,
                    message = "ok",
                    result = LoginResponse(token),
                )
            )
        } catch (ex: MissingTokenException) {
            ResponseEntity.badRequest().body(
                BaseResponse(
                    statusCode = 400,
                    message = "missing",
                )
            )
        } catch (ex: InvalidTokenException) {
            ResponseEntity.status(401).body(
                BaseResponse(
                    statusCode = 401,
                    message = "invalid",
                )
            )
        }
    }
}
