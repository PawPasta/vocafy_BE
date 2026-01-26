package com.exe.vocafy_BE.controller

import com.exe.vocafy_BE.model.dto.request.CategoryCreateRequest
import com.exe.vocafy_BE.model.dto.request.CategoryUpdateRequest
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.model.dto.response.CategoryResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ResponseFactory
import com.exe.vocafy_BE.service.CategoryService
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

@Tag(name = "Category")
@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    @Operation(summary = "Create category (admin, manager)")
    fun create(@Valid @RequestBody request: CategoryCreateRequest): ResponseEntity<BaseResponse<CategoryResponse>> {
        val result = categoryService.create(request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id (all)")
    fun getById(@PathVariable id: Long): ResponseEntity<BaseResponse<CategoryResponse>> {
        val result = categoryService.getById(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @GetMapping
    @Operation(summary = "List categories (all)")
    fun list(
        @RequestParam(required = false) name: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<BaseResponse<PageResponse<CategoryResponse>>> {
        val pageable = PageRequest.of(page, size)
        val result = categoryService.list(name, pageable)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category (admin, manager)")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CategoryUpdateRequest
    ): ResponseEntity<BaseResponse<CategoryResponse>> {
        val result = categoryService.update(id, request)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category (admin, manager)")
    fun delete(@PathVariable id: Long): ResponseEntity<BaseResponse<Unit>> {
        val result = categoryService.delete(id)
        return ResponseEntity.ok(ResponseFactory.success(result))
    }
}
