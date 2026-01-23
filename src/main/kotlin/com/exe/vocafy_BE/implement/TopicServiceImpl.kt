package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.CourseMapper
import com.exe.vocafy_BE.mapper.TopicMapper
import com.exe.vocafy_BE.model.dto.request.TopicCreateRequest
import com.exe.vocafy_BE.model.dto.request.TopicUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.TopicResponse
import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.Topic
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.TopicRepository
import com.exe.vocafy_BE.service.TopicService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TopicServiceImpl(
    private val topicRepository: TopicRepository,
    private val courseRepository: CourseRepository,
) : TopicService {

    @Transactional
    override fun create(request: TopicCreateRequest): ServiceResult<TopicResponse> {
        val topic = topicRepository.save(TopicMapper.toEntity(request))

        // Link courses by IDs if provided
        val linkedCourses = request.courseIds?.let { ids ->
            linkCoursesToTopic(topic, ids)
        } ?: emptyList()

        return ServiceResult(
            message = "Created",
            result = TopicMapper.toResponse(topic, linkedCourses.map { CourseMapper.toResponse(it) }),
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<TopicResponse> {
        val topic = topicRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Topic not found") }

        val courses = courseRepository.findAllBySyllabusTopicIdOrderByIdAsc(id)
            .map { CourseMapper.toResponse(it) }

        return ServiceResult(
            message = "Ok",
            result = TopicMapper.toResponse(topic, courses),
        )
    }

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): ServiceResult<PageResponse<TopicResponse>> {
        val page = topicRepository.findAll(pageable)
        val items = page.content.map { topic ->
            val courses = courseRepository.findAllBySyllabusTopicIdOrderByIdAsc(topic.id ?: 0)
                .map { CourseMapper.toResponse(it) }
            TopicMapper.toResponse(topic, courses)
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
    override fun listBySyllabusId(syllabusId: Long, pageable: Pageable): ServiceResult<PageResponse<TopicResponse>> {
        val page = topicRepository.findAllBySyllabusId(syllabusId, pageable)
        val items = page.content.map { topic ->
            val courses = courseRepository.findAllBySyllabusTopicIdOrderByIdAsc(topic.id ?: 0)
                .map { CourseMapper.toResponse(it) }
            TopicMapper.toResponse(topic, courses)
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
    override fun update(id: Long, request: TopicUpdateRequest): ServiceResult<TopicResponse> {
        val topic = topicRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Topic not found") }

        val updated = topicRepository.save(TopicMapper.applyUpdate(topic, request))

        // If courseIds are provided, unlink old and link new courses
        val courses = if (request.courseIds != null) {
            unlinkCoursesFromTopic(id)
            linkCoursesToTopic(updated, request.courseIds)
        } else {
            courseRepository.findAllBySyllabusTopicIdOrderByIdAsc(id)
        }

        return ServiceResult(
            message = "Updated",
            result = TopicMapper.toResponse(updated, courses.map { CourseMapper.toResponse(it) }),
        )
    }

    @Transactional
    override fun delete(id: Long): ServiceResult<Unit> {
        val topic = topicRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Topic not found") }

        // Unlink all courses from this topic (set topic to null)
        unlinkCoursesFromTopic(id)

        topicRepository.delete(topic)

        return ServiceResult(
            message = "Deleted",
            result = Unit,
        )
    }

    private fun linkCoursesToTopic(topic: Topic, courseIds: List<Long>): List<Course> {
        return courseIds.map { courseId ->
            val course = courseRepository.findById(courseId)
                .orElseThrow { BaseException.NotFoundException("Course with id $courseId not found") }

            val updatedCourse = Course(
                id = course.id,
                title = course.title,
                description = course.description,
                sortOrder = course.sortOrder,
                syllabusTopic = topic,
                isActive = course.isActive,
                isDeleted = course.isDeleted,
                createdAt = course.createdAt,
                updatedAt = course.updatedAt,
            )
            courseRepository.save(updatedCourse)
        }
    }

    private fun unlinkCoursesFromTopic(topicId: Long) {
        val courses = courseRepository.findAllBySyllabusTopicIdOrderByIdAsc(topicId)
        courses.forEach { course ->
            val updatedCourse = Course(
                id = course.id,
                title = course.title,
                description = course.description,
                sortOrder = course.sortOrder,
                syllabusTopic = null,
                isActive = course.isActive,
                isDeleted = course.isDeleted,
                createdAt = course.createdAt,
                updatedAt = course.updatedAt,
            )
            courseRepository.save(updatedCourse)
        }
    }
}

