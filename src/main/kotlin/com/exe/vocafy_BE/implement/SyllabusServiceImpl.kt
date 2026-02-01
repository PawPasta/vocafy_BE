package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.SyllabusMapper
import com.exe.vocafy_BE.model.dto.request.SyllabusActiveRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusCreateRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse
import com.exe.vocafy_BE.model.dto.response.SyllabusTopicResponse
import com.exe.vocafy_BE.model.dto.response.SyllabusTopicCourseResponse
import com.exe.vocafy_BE.model.entity.Category
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.CategoryRepository
import com.exe.vocafy_BE.repo.SyllabusTopicLinkRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.repo.TopicRepository
import com.exe.vocafy_BE.repo.TopicCourseLinkRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.SyllabusService
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SyllabusServiceImpl(
    private val syllabusRepository: SyllabusRepository,
    private val userRepository: UserRepository,
    private val topicRepository: TopicRepository,
    private val syllabusTopicLinkRepository: SyllabusTopicLinkRepository,
    private val topicCourseLinkRepository: TopicCourseLinkRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val categoryRepository: CategoryRepository,
) : SyllabusService {

    @Transactional
    override fun create(request: SyllabusCreateRequest): ServiceResult<SyllabusResponse> {
        val createdBy = resolveUser(request.createdByUserId)
        val category = resolveCategory(request.categoryId)
        val entity = SyllabusMapper.toEntity(request, createdBy, category)
        val saved = syllabusRepository.save(entity)

        // Link topics by IDs if provided
        request.topicIds?.let { ids ->
            linkTopicsToSyllabus(saved, ids)
        }

        return ServiceResult(
            message = "Created",
            result = SyllabusMapper.toResponse(saved),
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<SyllabusResponse> {
        val entity = syllabusRepository.findByIdAndActiveTrue(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        if (entity.visibility == SyllabusVisibility.PRIVATE && !canViewPrivate()) {
            throw BaseException.ForbiddenException("Forbidden")
        }
        val topics = syllabusTopicLinkRepository.findTopicsBySyllabusId(id)
            .map { topic ->
                val courses = topicCourseLinkRepository
                    .findCoursesByTopicId(topic.id ?: 0)
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
                    totalDays = topic.totalDays,
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
    override fun list(pageable: Pageable): ServiceResult<PageResponse<SyllabusResponse>> {
        val includeSensitive = canViewSensitive()
        val canViewPrivate = canViewPrivate()
        val page = syllabusRepository.findAllByActiveTrue(pageable)
        val items = page.content
            .filter { it.visibility != SyllabusVisibility.PRIVATE || canViewPrivate }
            .map { SyllabusMapper.toResponse(it, includeSensitive = includeSensitive) }
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

    @Transactional(readOnly = true)
    override fun listByUserId(userId: UUID, pageable: Pageable): ServiceResult<PageResponse<SyllabusResponse>> {
        val role = currentRole()
        if (role != Role.ADMIN.name && role != Role.MANAGER.name) {
            throw BaseException.ForbiddenException("Forbidden")
        }
        val page = syllabusRepository.findAllByCreatedById(userId, pageable)
        val items = page.content.map { SyllabusMapper.toResponse(it, includeSensitive = true) }
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

    @Transactional(readOnly = true)
    override fun listMine(pageable: Pageable): ServiceResult<PageResponse<SyllabusResponse>> {
        val userId = currentUserId() ?: throw BaseException.UnauthorizedException("Unauthorized")
        val page = syllabusRepository.findAllByCreatedById(userId, pageable)
        val includeSensitive = canViewSensitive()
        val items = page.content.map { SyllabusMapper.toResponse(it, includeSensitive = includeSensitive) }
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
    override fun update(id: Long, request: SyllabusUpdateRequest): ServiceResult<SyllabusResponse> {
        val entity = syllabusRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        val createdBy = resolveUser(request.createdByUserId)
        val category = resolveCategory(request.categoryId)
        val updated = syllabusRepository.save(SyllabusMapper.applyUpdate(entity, request, createdBy, category))

        // If topicIds are provided, unlink old and link new topics
        if (request.topicIds != null) {
            unlinkTopicsFromSyllabus(id)
            linkTopicsToSyllabus(updated, request.topicIds)
        }

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

    @Transactional
    override fun delete(id: Long): ServiceResult<Unit> {
        val entity = syllabusRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }

        // Unlink all topics from this syllabus (set syllabus to null)
        unlinkTopicsFromSyllabus(id)

        syllabusRepository.delete(entity)

        return ServiceResult(
            message = "Deleted",
            result = Unit,
        )
    }

    @Transactional
    override fun attachTopics(id: Long, topicIds: List<Long>): ServiceResult<Unit> {
        val syllabus = syllabusRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        linkTopicsToSyllabus(syllabus, topicIds)
        return ServiceResult(
            message = "Attached",
            result = Unit,
        )
    }

    @Transactional
    override fun detachTopic(id: Long, topicId: Long): ServiceResult<Unit> {
        syllabusRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        syllabusTopicLinkRepository.deleteBySyllabusIdAndTopicId(id, topicId)
        return ServiceResult(
            message = "Detached",
            result = Unit,
        )
    }

    private fun linkTopicsToSyllabus(syllabus: Syllabus, topicIds: List<Long>) {
        topicIds.forEach { topicId ->
            val topic = topicRepository.findById(topicId)
                .orElseThrow { BaseException.NotFoundException("Topic with id $topicId not found") }
            val existing = syllabusTopicLinkRepository.findBySyllabusIdAndTopicId(syllabus.id ?: 0, topicId)
            if (existing == null) {
                syllabusTopicLinkRepository.save(
                    com.exe.vocafy_BE.model.entity.SyllabusTopicLink(
                        syllabus = syllabus,
                        topic = topic,
                    )
                )
            }
        }
    }

    private fun unlinkTopicsFromSyllabus(syllabusId: Long) {
        syllabusTopicLinkRepository.deleteAllBySyllabusId(syllabusId)
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

    private fun resolveCategory(categoryId: Long?): Category? {
        if (categoryId == null) {
            return null
        }
        return categoryRepository.findById(categoryId)
            .orElseThrow { BaseException.NotFoundException("Category not found") }
    }

    private fun canViewSensitive(): Boolean {
        val role = currentRole() ?: return false
        return role == Role.ADMIN.name || role == Role.MANAGER.name
    }

    private fun canViewPrivate(): Boolean {
        val role = currentRole()
        if (role == Role.ADMIN.name || role == Role.MANAGER.name) {
            return true
        }
        val userId = currentUserId() ?: return false
        val subscription = subscriptionRepository.findByUserId(userId) ?: return false
        return subscription.plan == SubscriptionPlan.VIP
    }

    private fun currentRole(): String? {
        val authentication = SecurityContextHolder.getContext().authentication ?: return null
        val jwt = authentication.principal as? Jwt ?: return null
        return jwt.getClaimAsString("role")
    }

    private fun currentUserId(): java.util.UUID? {
        val authentication = SecurityContextHolder.getContext().authentication ?: return null
        val jwt = authentication.principal as? Jwt ?: return null
        val subject = jwt.subject ?: return null
        val parsed = runCatching { java.util.UUID.fromString(subject) }.getOrNull()
        if (parsed != null) {
            return parsed
        }
        val user = userRepository.findByEmail(subject)
        return user?.id
    }
}
