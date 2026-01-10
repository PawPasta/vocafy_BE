package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.SyllabusMapper
import com.exe.vocafy_BE.model.dto.request.SyllabusActiveRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusCreateRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusUpdateRequest
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SyllabusService(
    private val syllabusRepository: SyllabusRepository,
    private val userRepository: UserRepository,
) {

    @Transactional
    fun create(request: SyllabusCreateRequest): ServiceResult<SyllabusResponse> {
        val createdBy = resolveUser(request.createdByUserId)
        val entity = SyllabusMapper.toEntity(request, createdBy)
        val saved = syllabusRepository.save(entity)
        return ServiceResult(
            message = "Created",
            result = SyllabusMapper.toResponse(saved),
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ServiceResult<SyllabusResponse> {
        val entity = syllabusRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        return ServiceResult(
            message = "Ok",
            result = SyllabusMapper.toResponse(entity),
        )
    }

    @Transactional(readOnly = true)
    fun list(): ServiceResult<List<SyllabusResponse>> {
        val items = syllabusRepository.findAll().map(SyllabusMapper::toResponse)
        return ServiceResult(
            message = "Ok",
            result = items,
        )
    }

    @Transactional
    fun update(id: Long, request: SyllabusUpdateRequest): ServiceResult<SyllabusResponse> {
        val entity = syllabusRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        val createdBy = resolveUser(request.createdByUserId)
        val updated = syllabusRepository.save(SyllabusMapper.applyUpdate(entity, request, createdBy))
        return ServiceResult(
            message = "Updated",
            result = SyllabusMapper.toResponse(updated),
        )
    }

    @Transactional
    fun updateActive(id: Long, request: SyllabusActiveRequest): ServiceResult<SyllabusResponse> {
        val active = request.active ?: throw BaseException.BadRequestException("'active' can't be null")
        val entity = syllabusRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        val updated = syllabusRepository.save(SyllabusMapper.applyActive(entity, active))
        return ServiceResult(
            message = "Updated",
            result = SyllabusMapper.toResponse(updated),
        )
    }

    private fun resolveUser(userId: String?): User? {
        if (userId.isNullOrBlank()) {
            return null
        }
        val parsed = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw BaseException.BadRequestException("Invalid created_by_user_id")
        return userRepository.findById(parsed)
            .orElseThrow { BaseException.NotFoundException("User not found") }
    }
}
