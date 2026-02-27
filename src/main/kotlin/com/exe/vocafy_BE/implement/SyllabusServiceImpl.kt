package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
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
import com.exe.vocafy_BE.model.entity.SyllabusTargetLanguage
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.CategoryRepository
import com.exe.vocafy_BE.repo.SyllabusTopicLinkRepository
import com.exe.vocafy_BE.repo.SyllabusTargetLanguageRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.repo.TopicRepository
import com.exe.vocafy_BE.repo.TopicCourseLinkRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.SyllabusService
import com.exe.vocafy_BE.util.SecurityUtil
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SyllabusServiceImpl(
    private val securityUtil: SecurityUtil,
    private val syllabusRepository: SyllabusRepository,
    private val userRepository: UserRepository,
    private val topicRepository: TopicRepository,
    private val syllabusTopicLinkRepository: SyllabusTopicLinkRepository,
    private val topicCourseLinkRepository: TopicCourseLinkRepository,
    private val syllabusTargetLanguageRepository: SyllabusTargetLanguageRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val categoryRepository: CategoryRepository,
) : SyllabusService {

    @Transactional
    override fun create(request: SyllabusCreateRequest): ServiceResult<SyllabusResponse> {
        val createdBy = resolveUser(request.createdByUserId)
        val category = resolveCategory(request.categoryId)
        val languageConfig = resolveLanguageConfigForCreate(request)
        val entity = SyllabusMapper.toEntity(
            request = request,
            createdBy = createdBy,
            category = category,
            languageSet = languageConfig.languageSet,
            studyLanguage = languageConfig.studyLanguage,
        )
        val saved = syllabusRepository.save(entity)
        saveTargetLanguages(saved.id ?: 0, languageConfig.targetLanguages)

        // Link topics by IDs if provided
        request.topicIds?.let { ids ->
            linkTopicsToSyllabus(saved, ids)
        }

        return ServiceResult(
            message = "Created",
            result = SyllabusMapper.toResponse(saved, targetLanguages = languageConfig.targetLanguages),
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<SyllabusResponse> {
        val entity = syllabusRepository.findByIdAndActiveTrue(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        if (entity.visibility == SyllabusVisibility.PRIVATE && !canViewPrivate()) {
            throw BaseException.ForbiddenException("Forbidden")
        }
        val targetLanguages = getTargetLanguagesForSyllabus(id)
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
                targetLanguages = targetLanguages,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): ServiceResult<PageResponse<SyllabusResponse>> {
        val includeSensitive = canViewSensitive()
        val canViewPrivate = canViewPrivate()
        val page = syllabusRepository.findAllByActiveTrue(pageable)
        val visibleItems = page.content
            .filter { it.visibility != SyllabusVisibility.PRIVATE || canViewPrivate }
        val targetLanguageMap = loadTargetLanguageMap(visibleItems)
        val items = visibleItems.map {
            SyllabusMapper.toResponse(
                it,
                includeSensitive = includeSensitive,
                targetLanguages = targetLanguageMap[it.id].orEmpty(),
            )
        }
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
        val role = securityUtil.getCurrentRole()
        if (role != Role.ADMIN.name && role != Role.MANAGER.name) {
            throw BaseException.ForbiddenException("Forbidden")
        }
        val page = syllabusRepository.findAllByCreatedById(userId, pageable)
        val targetLanguageMap = loadTargetLanguageMap(page.content)
        val items = page.content.map {
            SyllabusMapper.toResponse(
                it,
                includeSensitive = true,
                targetLanguages = targetLanguageMap[it.id].orEmpty(),
            )
        }
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
        val userId = securityUtil.getCurrentUserIdOrNull() ?: throw BaseException.UnauthorizedException("Unauthorized")
        val page = syllabusRepository.findAllByCreatedById(userId, pageable)
        val includeSensitive = canViewSensitive()
        val targetLanguageMap = loadTargetLanguageMap(page.content)
        val items = page.content.map {
            SyllabusMapper.toResponse(
                it,
                includeSensitive = includeSensitive,
                targetLanguages = targetLanguageMap[it.id].orEmpty(),
            )
        }
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
        val existingTargetLanguages = getTargetLanguagesForSyllabus(id)
        val languageConfig = resolveLanguageConfigForUpdate(
            request = request,
            current = entity,
            existingTargetLanguages = existingTargetLanguages,
        )
        val updated = syllabusRepository.save(
            SyllabusMapper.applyUpdate(
                entity = entity,
                request = request,
                createdBy = createdBy,
                category = category,
                languageSet = languageConfig.languageSet,
                studyLanguage = languageConfig.studyLanguage,
            )
        )
        saveTargetLanguages(id, languageConfig.targetLanguages)

        // If topicIds are provided, unlink old and link new topics
        if (request.topicIds != null) {
            unlinkTopicsFromSyllabus(id)
            linkTopicsToSyllabus(updated, request.topicIds)
        }

        return ServiceResult(
            message = "Updated",
            result = SyllabusMapper.toResponse(updated, targetLanguages = languageConfig.targetLanguages),
        )
    }

    @Transactional
    override fun updateActive(id: Long, request: SyllabusActiveRequest): ServiceResult<SyllabusResponse> {
        val active = request.active ?: throw BaseException.BadRequestException("'active' can't be null")
        val entity = syllabusRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        val updated = syllabusRepository.save(SyllabusMapper.applyActive(entity, active))
        val targetLanguages = getTargetLanguagesForSyllabus(id)
        return ServiceResult(
            message = "Updated",
            result = SyllabusMapper.toResponse(updated, targetLanguages = targetLanguages),
        )
    }

    @Transactional
    override fun delete(id: Long): ServiceResult<Unit> {
        val entity = syllabusRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }

        // Unlink all topics from this syllabus (set syllabus to null)
        unlinkTopicsFromSyllabus(id)
        syllabusTargetLanguageRepository.deleteAllBySyllabusId(id)

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

    private fun resolveLanguageConfigForCreate(request: SyllabusCreateRequest): LanguageConfig {
        val studyLanguage = request.studyLanguage
            ?: request.languageSet?.let { deriveStudyLanguageFromLanguageSet(it) }
            ?: throw BaseException.BadRequestException("'study_language' can't be null")

        val rawTargetLanguages = request.targetLanguages
            ?: request.languageSet?.let { deriveTargetLanguagesFromLanguageSet(it, studyLanguage) }
            ?: throw BaseException.BadRequestException("'target_languages' can't be empty")

        val targetLanguages = validateTargetLanguages(studyLanguage, rawTargetLanguages)
        val languageSet = deriveLanguageSet(studyLanguage, targetLanguages)

        if (request.languageSet != null && request.languageSet != languageSet) {
            throw BaseException.BadRequestException("'language_set' is inconsistent with study/target languages")
        }

        return LanguageConfig(
            studyLanguage = studyLanguage,
            targetLanguages = targetLanguages,
            languageSet = languageSet,
        )
    }

    private fun resolveLanguageConfigForUpdate(
        request: SyllabusUpdateRequest,
        current: Syllabus,
        existingTargetLanguages: List<LanguageCode>,
    ): LanguageConfig {
        val fallbackLanguageSet = request.languageSet ?: current.languageSet
        val studyLanguage = request.studyLanguage
            ?: current.studyLanguage
            ?: deriveStudyLanguageFromLanguageSet(fallbackLanguageSet)

        val rawTargetLanguages = when {
            request.targetLanguages != null -> request.targetLanguages
            request.languageSet != null -> deriveTargetLanguagesFromLanguageSet(request.languageSet, studyLanguage)
            existingTargetLanguages.isNotEmpty() -> existingTargetLanguages
            else -> deriveTargetLanguagesFromLanguageSet(current.languageSet, studyLanguage)
        }

        val targetLanguages = validateTargetLanguages(studyLanguage, rawTargetLanguages)
        val languageSet = deriveLanguageSet(studyLanguage, targetLanguages)

        if (request.languageSet != null && request.languageSet != languageSet) {
            throw BaseException.BadRequestException("'language_set' is inconsistent with study/target languages")
        }

        return LanguageConfig(
            studyLanguage = studyLanguage,
            targetLanguages = targetLanguages,
            languageSet = languageSet,
        )
    }

    private fun validateTargetLanguages(studyLanguage: LanguageCode, targetLanguages: List<LanguageCode>): List<LanguageCode> {
        val normalized = targetLanguages.distinct()
        if (normalized.isEmpty()) {
            throw BaseException.BadRequestException("'target_languages' can't be empty")
        }
        if (normalized.contains(studyLanguage)) {
            throw BaseException.BadRequestException("'target_languages' cannot include study_language")
        }
        return normalized
    }

    private fun deriveLanguageSet(studyLanguage: LanguageCode, targetLanguages: List<LanguageCode>): LanguageSet {
        val languages = (targetLanguages + studyLanguage).toSet()
        return when (languages) {
            setOf(LanguageCode.EN, LanguageCode.JA) -> LanguageSet.EN_JP
            setOf(LanguageCode.EN, LanguageCode.VI) -> LanguageSet.EN_VI
            setOf(LanguageCode.JA, LanguageCode.VI) -> LanguageSet.JP_VI
            setOf(LanguageCode.EN, LanguageCode.JA, LanguageCode.VI) -> LanguageSet.EN_JP_VI
            else -> throw BaseException.BadRequestException("Unsupported language combination")
        }
    }

    private fun deriveStudyLanguageFromLanguageSet(languageSet: LanguageSet): LanguageCode =
        when (languageSet) {
            LanguageSet.EN_JP -> LanguageCode.JA
            LanguageSet.EN_VI -> LanguageCode.VI
            LanguageSet.JP_VI -> LanguageCode.VI
            LanguageSet.EN_JP_VI -> LanguageCode.JA
        }

    private fun deriveTargetLanguagesFromLanguageSet(languageSet: LanguageSet, studyLanguage: LanguageCode): List<LanguageCode> {
        val base = when (languageSet) {
            LanguageSet.EN_JP -> listOf(LanguageCode.EN, LanguageCode.JA)
            LanguageSet.EN_VI -> listOf(LanguageCode.EN, LanguageCode.VI)
            LanguageSet.JP_VI -> listOf(LanguageCode.JA, LanguageCode.VI)
            LanguageSet.EN_JP_VI -> listOf(LanguageCode.EN, LanguageCode.JA, LanguageCode.VI)
        }.filter { it != studyLanguage }

        if (base.isEmpty()) {
            throw BaseException.BadRequestException("'target_languages' can't be empty")
        }
        return base
    }

    private fun saveTargetLanguages(syllabusId: Long, targetLanguages: List<LanguageCode>) {
        syllabusTargetLanguageRepository.deleteAllBySyllabusId(syllabusId)
        if (targetLanguages.isEmpty()) {
            return
        }
        val syllabusRef = syllabusRepository.getReferenceById(syllabusId)
        val entities = targetLanguages.map { language ->
            SyllabusTargetLanguage(
                syllabus = syllabusRef,
                languageCode = language,
            )
        }
        syllabusTargetLanguageRepository.saveAll(entities)
    }

    private fun getTargetLanguagesForSyllabus(syllabusId: Long): List<LanguageCode> {
        return syllabusTargetLanguageRepository.findAllBySyllabusIdOrderByIdAsc(syllabusId)
            .map { it.languageCode }
    }

    private fun loadTargetLanguageMap(syllabi: List<Syllabus>): Map<Long?, List<LanguageCode>> {
        val syllabusIds = syllabi.mapNotNull { it.id }
        if (syllabusIds.isEmpty()) {
            return emptyMap()
        }
        return syllabusTargetLanguageRepository
            .findAllBySyllabusIdInOrderBySyllabusIdAscIdAsc(syllabusIds)
            .groupBy { it.syllabus.id }
            .mapValues { entry -> entry.value.map { it.languageCode } }
    }

    private data class LanguageConfig(
        val studyLanguage: LanguageCode,
        val targetLanguages: List<LanguageCode>,
        val languageSet: LanguageSet,
    )

    private fun canViewSensitive(): Boolean {
        val role = securityUtil.getCurrentRole() ?: return false
        return role == Role.ADMIN.name || role == Role.MANAGER.name
    }

    private fun canViewPrivate(): Boolean {
        val role = securityUtil.getCurrentRole()
        if (role == Role.ADMIN.name || role == Role.MANAGER.name) {
            return true
        }
        val userId = securityUtil.getCurrentUserIdOrNull() ?: return false
        val subscription = subscriptionRepository.findByUserId(userId) ?: return false
        return subscription.plan == SubscriptionPlan.VIP
    }
}
