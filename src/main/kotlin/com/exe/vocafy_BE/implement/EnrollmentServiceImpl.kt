package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.EnrollmentStatus
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
import com.exe.vocafy_BE.repo.EnrollmentRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.EnrollmentService
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class EnrollmentServiceImpl(
    private val userRepository: UserRepository,
    private val syllabusRepository: SyllabusRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val enrollmentRepository: EnrollmentRepository,
) : EnrollmentService {

    @Transactional
    override fun register(request: EnrollmentCreateRequest): ServiceResult<EnrollmentResponse> {
        val syllabusId = request.syllabusId ?: throw BaseException.BadRequestException("'syllabus_id' can't be null")
        val user = currentUser()
        val syllabus = syllabusRepository.findById(syllabusId)
            .orElseThrow { BaseException.NotFoundException("Syllabus not found") }

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
            val focused = enrollmentRepository.save(
                Enrollment(
                    id = existing.id,
                    user = existing.user,
                    syllabus = existing.syllabus,
                    startDate = existing.startDate,
                    status = existing.status,
                    isFocused = true,
                )
            )
            return ServiceResult(
                message = "Ok",
                result = EnrollmentMapper.toResponse(focused),
            )
        }

        enrollmentRepository.clearFocused(userId)
        val saved = enrollmentRepository.save(
            Enrollment(
                user = user,
                syllabus = syllabus,
                startDate = LocalDate.now(),
                status = EnrollmentStatus.ACTIVE,
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
        val user = currentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val enrollment = enrollmentRepository.findByUserIdAndSyllabusId(userId, syllabusId)
            ?: throw BaseException.NotFoundException("Enrollment not found")
        enrollmentRepository.clearFocused(userId)
        val updated = enrollmentRepository.save(
            Enrollment(
                id = enrollment.id,
                user = enrollment.user,
                syllabus = enrollment.syllabus,
                startDate = enrollment.startDate,
                status = enrollment.status,
                isFocused = true,
            )
        )
        return ServiceResult(
            message = "Updated",
            result = EnrollmentMapper.toResponse(updated),
        )
    }

    private fun currentUser(): com.exe.vocafy_BE.model.entity.User {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val jwt = authentication.principal as? Jwt
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val subject = jwt.subject ?: throw BaseException.BadRequestException("Invalid user_id")
        val parsed = runCatching { UUID.fromString(subject) }.getOrNull()
        if (parsed != null) {
            return userRepository.findById(parsed)
                .orElseThrow { BaseException.NotFoundException("User not found") }
        }
        return userRepository.findByEmail(subject)
            ?: throw BaseException.NotFoundException("User not found")
    }

    @Transactional(readOnly = true)
    override fun getFocusedSyllabus(): ServiceResult<SyllabusResponse> {
        val user = currentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val enrollment = enrollmentRepository.findByUserIdAndIsFocusedTrue(userId)
            ?: throw BaseException.NotFoundException("Focused syllabus not found")
        val syllabus = enrollment.syllabus
        return ServiceResult(
            message = "Ok",
            result = SyllabusMapper.toResponse(
                entity = syllabus,
                includeSensitive = canViewSensitive(),
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun listEnrolledSyllabuses(pageable: Pageable): ServiceResult<PageResponse<EnrolledSyllabusResponse>> {
        val user = currentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val page = enrollmentRepository.findAllByUserId(userId, pageable)
        val items = page.content.map { enrollment ->
            EnrolledSyllabusResponse(
                enrollmentId = enrollment.id ?: 0,
                status = enrollment.status,
                startDate = enrollment.startDate,
                isFocused = enrollment.isFocused,
                syllabus = SyllabusMapper.toResponse(
                    entity = enrollment.syllabus,
                    includeSensitive = canViewSensitive(),
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

    private fun canViewSensitive(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication ?: return false
        val jwt = authentication.principal as? Jwt ?: return false
        val role = jwt.getClaimAsString("role") ?: return false
        return role == Role.ADMIN.name || role == Role.MANAGER.name
    }
}
