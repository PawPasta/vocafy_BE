package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.SyllabusMapper
import com.exe.vocafy_BE.model.dto.request.SyllabusActiveRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusCreateRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusUpdateRequest
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse
import com.exe.vocafy_BE.model.dto.response.SyllabusTopicResponse
import com.exe.vocafy_BE.model.dto.response.SyllabusTopicCourseResponse
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.SyllabusTopicRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.service.SyllabusService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SyllabusServiceImpl(
    private val syllabusRepository: SyllabusRepository,
    private val userRepository: UserRepository,
    private val syllabusTopicRepository: SyllabusTopicRepository,
    private val courseRepository: CourseRepository,
) : SyllabusService {

    @Transactional
    override fun create(request: SyllabusCreateRequest): ServiceResult<SyllabusResponse> {
        val createdBy = resolveUser(request.createdByUserId)
        val entity = SyllabusMapper.toEntity(request, createdBy)
        val saved = syllabusRepository.save(entity)
        return ServiceResult(
            message = "Created",
            result = SyllabusMapper.toResponse(saved),
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<SyllabusResponse> {
        val entity = syllabusRepository.findByIdAndActiveTrueAndVisibilityNot(id, SyllabusVisibility.PRIVATE)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        val topics = syllabusTopicRepository.findAllBySyllabusIdOrderBySortOrderAsc(id)
            .map { topic ->
                val courses = courseRepository
                    .findAllBySyllabusTopicIdOrderByIdAsc(topic.id ?: 0)
                    .map { course ->
                        SyllabusTopicCourseResponse(
                            id = course.id ?: 0,
                            title = course.title,
                            description = course.description,
                        )
                    }
                SyllabusTopicResponse(
                    id = topic.id ?: 0,
                    title = topic.title,
                    description = topic.description,
                    sortOrder = topic.sortOrder,
                    courses = courses,
                )
            }
        return ServiceResult(
            message = "Ok",
            result = SyllabusMapper.toResponse(
                entity = entity,
                topics = topics,
                includeSensitive = canViewSensitive(),
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun list(): ServiceResult<List<SyllabusResponse>> {
        val includeSensitive = canViewSensitive()
        val items = syllabusRepository
            .findAllByActiveTrueAndVisibilityNot(SyllabusVisibility.PRIVATE)
            .map { SyllabusMapper.toResponse(it, includeSensitive = includeSensitive) }
        return ServiceResult(
            message = "Ok",
            result = items,
        )
    }

    @Transactional
    override fun update(id: Long, request: SyllabusUpdateRequest): ServiceResult<SyllabusResponse> {
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
    override fun updateActive(id: Long, request: SyllabusActiveRequest): ServiceResult<SyllabusResponse> {
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

    private fun canViewSensitive(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication ?: return false
        val jwt = authentication.principal as? Jwt ?: return false
        val role = jwt.getClaimAsString("role") ?: return false
        return role == Role.ADMIN.name || role == Role.MANAGER.name
    }
}
