package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.UserMapper
import com.exe.vocafy_BE.model.dto.response.MyProfileResponse
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.UserResponse
import com.exe.vocafy_BE.model.entity.Profile
import com.exe.vocafy_BE.repo.ProfileRepository
import com.exe.vocafy_BE.repo.UserDailyActivityRepository
import com.exe.vocafy_BE.model.dto.response.MyProfileUpdateRequest
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.UserService
import com.exe.vocafy_BE.util.SecurityUtil
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UserServiceImpl(
    private val securityUtil: SecurityUtil,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val userDailyActivityRepository: UserDailyActivityRepository,
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

    @Transactional(readOnly = true)
    override fun getMyProfile(): ServiceResult<MyProfileResponse> {
        val user = securityUtil.getCurrentUser()
        return ServiceResult(
            message = "Ok",
            result = buildMyProfileResponse(user),
        )
    }

    @Transactional
    override fun updateMyProfile(request: MyProfileUpdateRequest): ServiceResult<MyProfileResponse> {
        val user = securityUtil.getCurrentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")

        val profile = user.profile ?: throw BaseException.NotFoundException("Profile not found")

        profileRepository.save(
            Profile(
                id = profile.id,
                user = profile.user,
                displayName = request.displayName.orEmpty(),
                avatarUrl = request.avatarUrl,
                locale = profile.locale,
                timezone = profile.timezone,
            ),
        )

        val refreshed = userRepository.findById(userId)
            .orElseThrow { BaseException.NotFoundException("User not found") }

        return ServiceResult(
            message = "Updated",
            result = buildMyProfileResponse(refreshed),
        )
    }

    private fun buildMyProfileResponse(user: com.exe.vocafy_BE.model.entity.User): MyProfileResponse {
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")

        val latest = userDailyActivityRepository.findTopByUserIdOrderByActivityDateDesc(userId)
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val streakCount = when {
            latest?.activityDate == null -> 0
            latest.activityDate.isEqual(today) -> latest.streakSnapshot
            latest.activityDate.isEqual(yesterday) -> latest.streakSnapshot
            else -> 0
        }

        return MyProfileResponse(
            id = userId,
            email = user.email,
            role = user.role,
            status = user.status,
            lastLoginAt = user.lastLoginAt,
            lastActiveAt = user.lastActiveAt,
            profile = user.profile?.let { com.exe.vocafy_BE.mapper.ProfileMapper.toResponse(it) },
            streakCount = streakCount,
            streakLastDate = latest?.activityDate,
        )
    }
}
