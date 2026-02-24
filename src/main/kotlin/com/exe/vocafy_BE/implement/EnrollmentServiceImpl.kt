package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.EnrollmentStatus
import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.EnrollmentMapper
import com.exe.vocafy_BE.mapper.SyllabusMapper
import com.exe.vocafy_BE.model.dto.request.EnrollmentCreateRequest
import com.exe.vocafy_BE.model.dto.request.EnrollmentFocusRequest
import com.exe.vocafy_BE.model.dto.response.EnrollmentResponse
import com.exe.vocafy_BE.model.dto.response.EnrolledSyllabusResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse
import com.exe.vocafy_BE.model.entity.Enrollment
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.repo.EnrollmentRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.SyllabusTargetLanguageRepository
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.service.EnrollmentService
import com.exe.vocafy_BE.util.SecurityUtil
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class EnrollmentServiceImpl(
    private val securityUtil: SecurityUtil,
    private val syllabusRepository: SyllabusRepository,
    private val syllabusTargetLanguageRepository: SyllabusTargetLanguageRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val enrollmentRepository: EnrollmentRepository,
) : EnrollmentService {

    @Transactional
    override fun register(request: EnrollmentCreateRequest): ServiceResult<EnrollmentResponse> {
        val syllabusId = request.syllabusId ?: throw BaseException.BadRequestException("'syllabus_id' can't be null")
        val user = securityUtil.getCurrentUser()
        val syllabus = syllabusRepository.findById(syllabusId)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }
        val allowedTargetLanguages = getAllowedTargetLanguages(syllabusId, syllabus)

        if (!syllabus.active) {
            throw BaseException.BadRequestException("Syllabus is inactive")
        }

        if (syllabus.visibility == SyllabusVisibility.PRIVATE) {
            val subscription = subscriptionRepository.findByUserId(user.id ?: UUID(0, 0))
                ?: throw BaseException.ForbiddenException("Forbidden")
            if (subscription.plan != SubscriptionPlan.VIP) {
                throw BaseException.ForbiddenException("Forbidden")
            }
        }

        val userId = user.id ?: UUID(0, 0)
        val existing = enrollmentRepository.findByUserIdAndSyllabusId(userId, syllabusId)
        if (existing != null) {
            enrollmentRepository.clearFocused(userId)
            val preferredTargetLanguage = resolvePreferredTargetLanguage(
                requested = request.preferredTargetLanguage ?: existing.preferredTargetLanguage,
                allowedTargetLanguages = allowedTargetLanguages,
            )
            val focused = enrollmentRepository.save(
                Enrollment(
                    id = existing.id,
                    user = existing.user,
                    syllabus = existing.syllabus,
                    startDate = existing.startDate,
                    status = existing.status,
                    preferredTargetLanguage = preferredTargetLanguage,
                    isFocused = true,
                )
            )
            return ServiceResult(
                message = "Ok",
                result = EnrollmentMapper.toResponse(focused),
            )
        }

        enrollmentRepository.clearFocused(userId)
        val preferredTargetLanguage = resolvePreferredTargetLanguage(
            requested = request.preferredTargetLanguage,
            allowedTargetLanguages = allowedTargetLanguages,
        )
        val saved = enrollmentRepository.save(
            Enrollment(
                user = user,
                syllabus = syllabus,
                startDate = LocalDate.now(),
                status = EnrollmentStatus.ACTIVE,
                preferredTargetLanguage = preferredTargetLanguage,
                isFocused = true,
            )
        )
        return ServiceResult(
            message = "Created",
            result = EnrollmentMapper.toResponse(saved),
        )
    }

    @Transactional
    override fun focus(request: EnrollmentFocusRequest): ServiceResult<EnrollmentResponse> {
        val syllabusId = request.syllabusId ?: throw BaseException.BadRequestException("'syllabus_id' can't be null")
        val user = securityUtil.getCurrentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val enrollment = enrollmentRepository.findByUserIdAndSyllabusId(userId, syllabusId)
            ?: throw BaseException.NotFoundException("Enrollment not found")
        val allowedTargetLanguages = getAllowedTargetLanguages(syllabusId, enrollment.syllabus)
        val preferredTargetLanguage = resolvePreferredTargetLanguage(
            requested = request.preferredTargetLanguage ?: enrollment.preferredTargetLanguage,
            allowedTargetLanguages = allowedTargetLanguages,
        )
        enrollmentRepository.clearFocused(userId)
        val updated = enrollmentRepository.save(
            Enrollment(
                id = enrollment.id,
                user = enrollment.user,
                syllabus = enrollment.syllabus,
                startDate = enrollment.startDate,
                status = enrollment.status,
                preferredTargetLanguage = preferredTargetLanguage,
                isFocused = true,
            )
        )
        return ServiceResult(
            message = "Updated",
            result = EnrollmentMapper.toResponse(updated),
        )
    }

    @Transactional(readOnly = true)
    override fun getFocusedSyllabus(): ServiceResult<SyllabusResponse> {
        val user = securityUtil.getCurrentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val enrollment = enrollmentRepository.findByUserIdAndIsFocusedTrue(userId)
            ?: throw BaseException.NotFoundException("Focused syllabus not found")
        val syllabus = enrollment.syllabus
        val resolvedSyllabusId = syllabus.id ?: throw BaseException.NotFoundException("Syllabus not found")
        val targetLanguages = getAllowedTargetLanguages(resolvedSyllabusId, syllabus)
        return ServiceResult(
            message = "Ok",
            result = SyllabusMapper.toResponse(
                entity = syllabus,
                includeSensitive = canViewSensitive(),
                targetLanguages = targetLanguages,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun listEnrolledSyllabuses(pageable: Pageable): ServiceResult<PageResponse<EnrolledSyllabusResponse>> {
        val user = securityUtil.getCurrentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val page = enrollmentRepository.findAllByUserId(userId, pageable)
        val syllabusIds = page.content.mapNotNull { it.syllabus.id }
        val targetLanguageMap = loadTargetLanguageMap(syllabusIds)
        val items = page.content.map { enrollment ->
            EnrolledSyllabusResponse(
                enrollmentId = enrollment.id ?: 0,
                status = enrollment.status,
                startDate = enrollment.startDate,
                isFocused = enrollment.isFocused,
                syllabus = SyllabusMapper.toResponse(
                    entity = enrollment.syllabus,
                    includeSensitive = canViewSensitive(),
                    targetLanguages = targetLanguageMap[enrollment.syllabus.id].orEmpty(),
                ),
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

    private fun resolvePreferredTargetLanguage(
        requested: LanguageCode?,
        allowedTargetLanguages: List<LanguageCode>,
    ): LanguageCode {
        if (allowedTargetLanguages.isEmpty()) {
            throw BaseException.BadRequestException("Syllabus target languages are not configured")
        }
        if (requested == null) {
            return allowedTargetLanguages.first()
        }
        if (!allowedTargetLanguages.contains(requested)) {
            throw BaseException.BadRequestException("Preferred target language is not allowed for this syllabus")
        }
        return requested
    }

    private fun getAllowedTargetLanguages(syllabusId: Long, syllabus: Syllabus): List<LanguageCode> {
        val configured = syllabusTargetLanguageRepository.findAllBySyllabusIdOrderByIdAsc(syllabusId)
            .map { it.languageCode }
            .distinct()
        if (configured.isNotEmpty()) {
            return configured
        }
        val studyLanguage = syllabus.studyLanguage ?: deriveStudyLanguageFromLanguageSet(syllabus.languageSet)
        return deriveTargetLanguagesFromLanguageSet(syllabus.languageSet, studyLanguage)
    }

    private fun loadTargetLanguageMap(syllabusIds: List<Long>): Map<Long?, List<LanguageCode>> {
        if (syllabusIds.isEmpty()) {
            return emptyMap()
        }
        return syllabusTargetLanguageRepository
            .findAllBySyllabusIdInOrderBySyllabusIdAscIdAsc(syllabusIds.distinct())
            .groupBy { it.syllabus.id }
            .mapValues { entry -> entry.value.map { it.languageCode } }
    }

    private fun deriveStudyLanguageFromLanguageSet(languageSet: LanguageSet): LanguageCode =
        when (languageSet) {
            LanguageSet.EN_JP -> LanguageCode.JA
            LanguageSet.EN_VI -> LanguageCode.VI
            LanguageSet.JP_VI -> LanguageCode.VI
            LanguageSet.EN_JP_VI -> LanguageCode.JA
        }

    private fun deriveTargetLanguagesFromLanguageSet(languageSet: LanguageSet, studyLanguage: LanguageCode): List<LanguageCode> =
        when (languageSet) {
            LanguageSet.EN_JP -> listOf(LanguageCode.EN, LanguageCode.JA)
            LanguageSet.EN_VI -> listOf(LanguageCode.EN, LanguageCode.VI)
            LanguageSet.JP_VI -> listOf(LanguageCode.JA, LanguageCode.VI)
            LanguageSet.EN_JP_VI -> listOf(LanguageCode.EN, LanguageCode.JA, LanguageCode.VI)
        }.filter { it != studyLanguage }

    private fun canViewSensitive(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication ?: return false
        val jwt = authentication.principal as? Jwt ?: return false
        val role = jwt.getClaimAsString("role") ?: return false
        return role == Role.ADMIN.name || role == Role.MANAGER.name
    }
}
