package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.mapper.SubscriptionTransactionMapper
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.SubscriptionTransactionResponse
import com.exe.vocafy_BE.repo.SubscriptionTransactionRepository
import com.exe.vocafy_BE.service.SubscriptionTransactionService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubscriptionTransactionServiceImpl(
    private val subscriptionTransactionRepository: SubscriptionTransactionRepository,
) : SubscriptionTransactionService {

    @Transactional(readOnly = true)
    override fun getAll(pageable: Pageable): ServiceResult<PageResponse<SubscriptionTransactionResponse>> {
        val page = subscriptionTransactionRepository.findAll(pageable)
        val mapped = page.map(SubscriptionTransactionMapper::toResponse)
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
}

