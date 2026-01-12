package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.ProfileMapper
import com.exe.vocafy_BE.model.dto.request.ProfileUpdateRequest
import com.exe.vocafy_BE.model.dto.response.ProfileResponse
import com.exe.vocafy_BE.model.entity.Profile
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.repo.ProfileRepository
import com.exe.vocafy_BE.service.ProfileService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
) : ProfileService {

    @Transactional(readOnly = true)
    override fun getByUserId(userId: String): ServiceResult<ProfileResponse> {
        val parsed = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw BaseException.BadRequestException("Invalid user_id")
        val profile = profileRepository.findByUserId(parsed)
            ?: throw BaseException.NotFoundException("Profile not found")
        return ServiceResult(
            message = "Ok",
            result = ProfileMapper.toResponse(profile),
        )
    }

    @Transactional
    override fun update(userId: String, request: ProfileUpdateRequest): ServiceResult<ProfileResponse> {
        val parsed = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw BaseException.BadRequestException("Invalid user_id")
        val profile = profileRepository.findByUserId(parsed)
            ?: throw BaseException.NotFoundException("Profile not found")
        val updated = profileRepository.save(
            Profile(
                id = profile.id,
                user = profile.user,
                displayName = request.displayName.orEmpty(),
                avatarUrl = request.avatarUrl,
                locale = request.locale,
                timezone = request.timezone,
            )
        )
        return ServiceResult(
            message = "Updated",
            result = ProfileMapper.toResponse(updated),
        )
    }
}
