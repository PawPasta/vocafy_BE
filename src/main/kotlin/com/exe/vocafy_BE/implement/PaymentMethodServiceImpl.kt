package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.PaymentMethodMapper
import com.exe.vocafy_BE.model.dto.request.PaymentMethodActiveRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodCreateRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.PaymentMethodResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.repo.PaymentMethodRepository
import com.exe.vocafy_BE.service.PaymentMethodService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentMethodServiceImpl(
    private val paymentMethodRepository: PaymentMethodRepository,
) : PaymentMethodService {

    @Transactional
    override fun create(request: PaymentMethodCreateRequest): ServiceResult<PaymentMethodResponse> {
        val saved = paymentMethodRepository.save(PaymentMethodMapper.toEntity(request))
        return ServiceResult(
            message = "Created",
            result = PaymentMethodMapper.toResponse(saved),
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<PaymentMethodResponse> {
        val entity = paymentMethodRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Payment method not found") }
        return ServiceResult(
            message = "Ok",
            result = PaymentMethodMapper.toResponse(entity),
        )
    }

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): ServiceResult<PageResponse<PaymentMethodResponse>> {
        val page = paymentMethodRepository.findAll(pageable)
        val items = page.content.map(PaymentMethodMapper::toResponse)
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = items,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                isFirst = page.isFirst,
                isLast = page.isLast,
            ),
        )
    }

    @Transactional
    override fun update(id: Long, request: PaymentMethodUpdateRequest): ServiceResult<PaymentMethodResponse> {
        val entity = paymentMethodRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Payment method not found") }
        val updated = paymentMethodRepository.save(PaymentMethodMapper.applyUpdate(entity, request))
        return ServiceResult(
            message = "Updated",
            result = PaymentMethodMapper.toResponse(updated),
        )
    }

    @Transactional
    override fun updateActive(id: Long, request: PaymentMethodActiveRequest): ServiceResult<PaymentMethodResponse> {
        val active = request.active ?: throw BaseException.BadRequestException("'active' can't be null")
        val entity = paymentMethodRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Payment method not found") }
        val updated = paymentMethodRepository.save(PaymentMethodMapper.applyActive(entity, active))
        return ServiceResult(
            message = "Updated",
            result = PaymentMethodMapper.toResponse(updated),
        )
    }
}
