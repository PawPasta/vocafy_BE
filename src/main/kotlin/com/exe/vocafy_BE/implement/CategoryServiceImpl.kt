package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.CategoryMapper
import com.exe.vocafy_BE.model.dto.request.CategoryCreateRequest
import com.exe.vocafy_BE.model.dto.request.CategoryUpdateRequest
import com.exe.vocafy_BE.model.dto.response.CategoryResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.repo.CategoryRepository
import com.exe.vocafy_BE.service.CategoryService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryService {

    @Transactional
    override fun create(request: CategoryCreateRequest): ServiceResult<CategoryResponse> {
        val entity = CategoryMapper.toEntity(request)
        val saved = categoryRepository.save(entity)
        return ServiceResult(
            message = "Created",
            result = CategoryMapper.toResponse(saved)
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<CategoryResponse> {
        val entity = categoryRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Category not found") }
        return ServiceResult(
            message = "Ok",
            result = CategoryMapper.toResponse(entity)
        )
    }

    @Transactional(readOnly = true)
    override fun list(name: String?, pageable: Pageable): ServiceResult<PageResponse<CategoryResponse>> {
        val page = if (name.isNullOrBlank()) {
            categoryRepository.findAll(pageable)
        } else {
            categoryRepository.findByNameContainingIgnoreCase(name, pageable)
        }
        val items = page.content.map { CategoryMapper.toResponse(it) }
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = items,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                isFirst = page.isFirst,
                isLast = page.isLast
            )
        )
    }

    @Transactional
    override fun update(id: Long, request: CategoryUpdateRequest): ServiceResult<CategoryResponse> {
        val entity = categoryRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Category not found") }
        val updated = categoryRepository.save(CategoryMapper.applyUpdate(entity, request))
        return ServiceResult(
            message = "Updated",
            result = CategoryMapper.toResponse(updated)
        )
    }

    @Transactional
    override fun delete(id: Long): ServiceResult<Unit> {
        if (!categoryRepository.existsById(id)) {
            throw BaseException.NotFoundException("Category not found")
        }
        categoryRepository.deleteById(id)
        return ServiceResult(
            message = "Deleted",
            result = Unit
        )
    }
}
