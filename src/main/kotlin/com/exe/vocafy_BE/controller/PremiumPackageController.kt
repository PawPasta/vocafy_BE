package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.PremiumPackageCreateRequest
import com.exe.vocafy_BE.model.dto.request.PremiumPackageUpdateRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.PremiumPackageResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.PremiumPackageService
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

@Tag(name = "Premium Packages")
@RestController
@RequestMapping("/api/premium-packages")
class PremiumPackageController(
    private val premiumPackageService: PremiumPackageService,
) {

    @GetMapping
    @Operation(summary = "Get all premium packages (all fields) - paginated")
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<BaseResponse<PageResponse<PremiumPackageResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = premiumPackageService.getAll(pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get premium package by id (all fields)")
    fun getById(@PathVariable id: Long): ResponseEntity<BaseResponse<PremiumPackageResponse>> {
        val result = premiumPackageService.getById(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PostMapping
    @Operation(summary = "Create premium package")
    fun create(@Valid @RequestBody request: PremiumPackageCreateRequest): ResponseEntity<BaseResponse<PremiumPackageResponse>> {
        val result = premiumPackageService.create(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update premium package")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: PremiumPackageUpdateRequest,
    ): ResponseEntity<BaseResponse<PremiumPackageResponse>> {
        val result = premiumPackageService.update(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete premium package")
    fun delete(@PathVariable id: Long): ResponseEntity<BaseResponse<Unit>> {
        val result = premiumPackageService.delete(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
