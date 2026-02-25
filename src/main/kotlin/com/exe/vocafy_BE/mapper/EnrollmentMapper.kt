package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.response.EnrollmentResponse
import com.exe.vocafy_BE.model.entity.Enrollment

object EnrollmentMapper {
    fun toResponse(entity: Enrollment): EnrollmentResponse =
        EnrollmentResponse(
            id = entity.id ?: 0,
            userId = entity.user.id?.toString().orEmpty(),
            syllabusId = entity.syllabus.id ?: 0,
            startDate = entity.startDate,
            status = entity.status,
            preferredTargetLanguage = entity.preferredTargetLanguage,
            isFocused = entity.isFocused,
        )
}
