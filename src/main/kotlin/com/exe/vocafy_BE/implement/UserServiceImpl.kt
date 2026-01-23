package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.mapper.UserMapper
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.UserResponse
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    @Transactional(readOnly = true)
    override fun getAll(pageable: Pageable): ServiceResult<PageResponse<UserResponse>> {
        val page = userRepository.findAll(pageable)
        val mapped = page.map(UserMapper::toResponse)
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
