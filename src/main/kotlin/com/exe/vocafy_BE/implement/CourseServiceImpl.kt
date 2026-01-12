package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.CourseMapper
import com.exe.vocafy_BE.model.dto.request.CourseCreateRequest
import com.exe.vocafy_BE.model.dto.request.CourseUpdateRequest
import com.exe.vocafy_BE.model.dto.response.CourseResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.service.CourseService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseServiceImpl(
    private val courseRepository: CourseRepository,
) : CourseService {

    @Transactional
    override fun create(request: CourseCreateRequest): ServiceResult<CourseResponse> {
        val saved = courseRepository.save(CourseMapper.toEntity(request))
        return ServiceResult(
            message = "Created",
            result = CourseMapper.toResponse(saved),
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<CourseResponse> {
        val entity = courseRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Course not found") }
        return ServiceResult(
            message = "Ok",
            result = CourseMapper.toResponse(entity),
        )
    }

    @Transactional(readOnly = true)
    override fun list(): ServiceResult<List<CourseResponse>> {
        val items = courseRepository.findAll().map(CourseMapper::toResponse)
        return ServiceResult(
            message = "Ok",
            result = items,
        )
    }

    @Transactional
    override fun update(id: Long, request: CourseUpdateRequest): ServiceResult<CourseResponse> {
        val entity = courseRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Course not found") }
        val updated = courseRepository.save(CourseMapper.applyUpdate(entity, request))
        return ServiceResult(
            message = "Updated",
            result = CourseMapper.toResponse(updated),
        )
    }
}
