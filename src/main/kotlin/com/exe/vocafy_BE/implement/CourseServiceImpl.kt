package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.CourseMapper
import com.exe.vocafy_BE.model.dto.request.CourseCreateRequest
import com.exe.vocafy_BE.model.dto.request.CourseUpdateRequest
import com.exe.vocafy_BE.model.dto.response.CourseResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.Vocabulary
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.service.CourseService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseServiceImpl(
    private val courseRepository: CourseRepository,
    private val vocabularyRepository: VocabularyRepository,
) : CourseService {

    @Transactional
    override fun create(request: CourseCreateRequest): ServiceResult<CourseResponse> {
        val saved = courseRepository.save(CourseMapper.toEntity(request))

        // Link vocabularies by IDs if provided
        request.vocabularyIds?.let { ids ->
            linkVocabulariesToCourse(saved, ids)
        }

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

    @Transactional(readOnly = true)
    override fun listByTopicId(topicId: Long): ServiceResult<List<CourseResponse>> {
        val items = courseRepository.findAllBySyllabusTopicIdOrderByIdAsc(topicId)
            .map(CourseMapper::toResponse)
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

        // If vocabularyIds are provided, unlink old and link new vocabularies
        if (request.vocabularyIds != null) {
            // Unlink existing vocabularies from this course
            unlinkVocabulariesFromCourse(id)
            // Link new vocabularies
            linkVocabulariesToCourse(updated, request.vocabularyIds)
        }

        return ServiceResult(
            message = "Updated",
            result = CourseMapper.toResponse(updated),
        )
    }

    @Transactional
    override fun delete(id: Long): ServiceResult<Unit> {
        val entity = courseRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Course not found") }

        // Unlink all vocabularies from this course (set course to null)
        unlinkVocabulariesFromCourse(id)

        courseRepository.delete(entity)

        return ServiceResult(
            message = "Deleted",
            result = Unit,
        )
    }

    private fun linkVocabulariesToCourse(course: Course, vocabularyIds: List<Long>) {
        vocabularyIds.forEach { vocabId ->
            val vocab = vocabularyRepository.findById(vocabId)
                .orElseThrow { BaseException.NotFoundException("Vocabulary with id $vocabId not found") }

            val updatedVocab = Vocabulary(
                id = vocab.id,
                note = vocab.note,
                sortOrder = vocab.sortOrder,
                course = course,
                isActive = vocab.isActive,
                isDeleted = vocab.isDeleted,
                createdAt = vocab.createdAt,
                updatedAt = vocab.updatedAt,
            )
            vocabularyRepository.save(updatedVocab)
        }
    }

    private fun unlinkVocabulariesFromCourse(courseId: Long) {
        val vocabs = vocabularyRepository.findAllByCourseIdOrderBySortOrderAscIdAsc(courseId)
        vocabs.forEach { vocab ->
            val updatedVocab = Vocabulary(
                id = vocab.id,
                note = vocab.note,
                sortOrder = vocab.sortOrder,
                course = null,
                isActive = vocab.isActive,
                isDeleted = vocab.isDeleted,
                createdAt = vocab.createdAt,
                updatedAt = vocab.updatedAt,
            )
            vocabularyRepository.save(updatedVocab)
        }
    }
}
