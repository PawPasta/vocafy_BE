package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.CourseMapper
import com.exe.vocafy_BE.mapper.TopicMapper
import com.exe.vocafy_BE.model.dto.request.TopicCreateRequest
import com.exe.vocafy_BE.model.dto.request.TopicUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.TopicResponse
import com.exe.vocafy_BE.model.entity.Topic
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.SyllabusTopicLinkRepository
import com.exe.vocafy_BE.repo.TopicRepository
import com.exe.vocafy_BE.repo.TopicCourseLinkRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.TopicService
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TopicServiceImpl(
    private val topicRepository: TopicRepository,
    private val courseRepository: CourseRepository,
    private val topicCourseLinkRepository: TopicCourseLinkRepository,
    private val syllabusTopicLinkRepository: SyllabusTopicLinkRepository,
    private val userRepository: UserRepository,
) : TopicService {

    @Transactional
    override fun create(request: TopicCreateRequest): ServiceResult<TopicResponse> {
        val createdBy = currentUser()
        val topic = topicRepository.save(TopicMapper.toEntity(request, createdBy))

        // Link courses by IDs if provided
        val linkedCourses = request.courseIds?.let { ids ->
            linkCoursesToTopic(topic, ids)
        } ?: emptyList()

        return ServiceResult(
            message = "Created",
            result = TopicMapper.toResponse(topic, linkedCourses.map { CourseMapper.toResponse(it, topic.id) }),
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<TopicResponse> {
        val topic = topicRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Topic not found") }

        val courses = topicCourseLinkRepository.findCoursesByTopicId(id)
            .map { CourseMapper.toResponse(it, id) }

        return ServiceResult(
            message = "Ok",
            result = TopicMapper.toResponse(topic, courses, resolveSyllabusId(topic.id ?: 0)),
        )
    }

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): ServiceResult<PageResponse<TopicResponse>> {
        val page = topicRepository.findAll(pageable)
        val items = page.content.map { topic ->
            val topicId = topic.id ?: 0
            val courses = topicCourseLinkRepository.findCoursesByTopicId(topicId)
                .map { CourseMapper.toResponse(it, topicId) }
            TopicMapper.toResponse(topic, courses, resolveSyllabusId(topicId))
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
        val allTopics = syllabusTopicLinkRepository.findTopicsBySyllabusId(syllabusId)
        val page = toPage(allTopics, pageable)
        val items = page.content.map { topic ->
            val topicId = topic.id ?: 0
            val courses = topicCourseLinkRepository.findCoursesByTopicId(topicId)
                .map { CourseMapper.toResponse(it, topicId) }
            TopicMapper.toResponse(topic, courses, syllabusId)
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
            topicCourseLinkRepository.findCoursesByTopicId(id)
        }

        return ServiceResult(
            message = "Updated",
            result = TopicMapper.toResponse(updated, courses.map { CourseMapper.toResponse(it, id) }, resolveSyllabusId(id)),
        )
    }

    @Transactional
    override fun delete(id: Long): ServiceResult<Unit> {
        val topic = topicRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Topic not found") }

        // Unlink all courses and syllabi
        unlinkCoursesFromTopic(id)
        syllabusTopicLinkRepository.deleteAllByTopicId(id)

        topicRepository.delete(topic)

        return ServiceResult(
            message = "Deleted",
            result = Unit,
        )
    }

    @Transactional
    override fun attachCourses(id: Long, courseIds: List<Long>): ServiceResult<Unit> {
        val topic = topicRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Topic not found") }
        linkCoursesToTopic(topic, courseIds)
        return ServiceResult(
            message = "Attached",
            result = Unit,
        )
    }

    @Transactional
    override fun detachCourse(id: Long, courseId: Long): ServiceResult<Unit> {
        topicRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Topic not found") }
        topicCourseLinkRepository.deleteByTopicIdAndCourseId(id, courseId)
        return ServiceResult(
            message = "Detached",
            result = Unit,
        )
    }

    private fun linkCoursesToTopic(topic: Topic, courseIds: List<Long>): List<com.exe.vocafy_BE.model.entity.Course> {
        return courseIds.map { courseId ->
            val course = courseRepository.findById(courseId)
                .orElseThrow { BaseException.NotFoundException("Course with id $courseId not found") }
            val existing = topicCourseLinkRepository.findByTopicIdAndCourseId(topic.id ?: 0, courseId)
            if (existing == null) {
                topicCourseLinkRepository.save(
                    com.exe.vocafy_BE.model.entity.TopicCourseLink(
                        topic = topic,
                        course = course,
                    )
                )
            }
            course
        }
    }

    private fun unlinkCoursesFromTopic(topicId: Long) {
        topicCourseLinkRepository.deleteAllByTopicId(topicId)
    }

    private fun resolveSyllabusId(topicId: Long): Long? {
        return syllabusTopicLinkRepository.findFirstByTopicIdOrderByIdAsc(topicId)
            ?.syllabus
            ?.id
    }

    private fun <T> toPage(items: List<T>, pageable: Pageable): org.springframework.data.domain.Page<T> {
        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(items.size)
        val content = if (start >= items.size) emptyList() else items.subList(start, end)
        return PageImpl(content, pageable, items.size.toLong())
    }

    private fun currentUser(): User {
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
}
