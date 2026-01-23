package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.PremiumPackageMapper
import com.exe.vocafy_BE.model.dto.request.PremiumPackageCreateRequest
import com.exe.vocafy_BE.model.dto.request.PremiumPackageUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.PremiumPackageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.entity.PremiumPackage
import com.exe.vocafy_BE.repo.PremiumPackageRepository
import com.exe.vocafy_BE.service.PremiumPackageService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PremiumPackageServiceImpl(
    private val premiumPackageRepository: PremiumPackageRepository,
) : PremiumPackageService {

    @Transactional(readOnly = true)
    override fun getAll(pageable: Pageable): ServiceResult<PageResponse<PremiumPackageResponse>> {
        val page = premiumPackageRepository.findAll(pageable)
        val mapped = page.map(PremiumPackageMapper::toResponse)
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = mapped.content,
                page = mapped.number,
                size = mapped.size,
                totalElements = mapped.totalElements,
                totalPages = mapped.totalPages,
                isFirst = mapped.isFirst,
                isLast = mapped.isLast,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<PremiumPackageResponse> {
        val entity = premiumPackageRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Premium package not found") }
        return ServiceResult(
            message = "Ok",
            result = PremiumPackageMapper.toResponse(entity),
        )
    }

    @Transactional
    override fun create(request: PremiumPackageCreateRequest): ServiceResult<PremiumPackageResponse> {
        val saved = premiumPackageRepository.save(
            PremiumPackage(
                name = request.name,
                description = request.description,
                price = request.price,
                durationDays = request.durationDays,
                active = request.active,
            ),
        )
        return ServiceResult(
            message = "Created",
            result = PremiumPackageMapper.toResponse(saved),
        )
    }

    @Transactional
    override fun update(id: Long, request: PremiumPackageUpdateRequest): ServiceResult<PremiumPackageResponse> {
        val entity = premiumPackageRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Premium package not found") }

        val updated = premiumPackageRepository.save(
            PremiumPackage(
                id = entity.id,
                name = request.name ?: entity.name,
                description = request.description ?: entity.description,
                price = request.price ?: entity.price,
                durationDays = request.durationDays ?: entity.durationDays,
                active = request.active ?: entity.active,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            ),
        )

        return ServiceResult(
            message = "Updated",
            result = PremiumPackageMapper.toResponse(updated),
        )
    }

    @Transactional
    override fun delete(id: Long): ServiceResult<Unit> {
        val entity = premiumPackageRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Premium package not found") }
        premiumPackageRepository.delete(entity)
        return ServiceResult(
            message = "Deleted",
            result = Unit,
        )
    }
}
