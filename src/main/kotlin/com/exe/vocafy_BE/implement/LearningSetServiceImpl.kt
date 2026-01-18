package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.LearningSetCardType
import com.exe.vocafy_BE.enum.LearningState
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.request.LearningSetCompleteRequest
import com.exe.vocafy_BE.model.dto.request.LearningSetGenerateRequest
import com.exe.vocafy_BE.model.dto.response.LearningSetCardResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetCompleteResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyMeaningResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyMediaResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyTermResponse
import com.exe.vocafy_BE.model.entity.Vocabulary
import com.exe.vocafy_BE.model.entity.UserVocabProgress
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.EnrollmentRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.repo.UserVocabProgressRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.service.LearningSetService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class LearningSetServiceImpl(
    private val userRepository: UserRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val courseRepository: CourseRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val userVocabProgressRepository: UserVocabProgressRepository,
    private val vocabularyTermRepository: VocabularyTermRepository,
    private val vocabularyMeaningRepository: VocabularyMeaningRepository,
    private val vocabularyMediaRepository: VocabularyMediaRepository,
) : LearningSetService {

    @Transactional(readOnly = true)
    override fun generate(request: LearningSetGenerateRequest): ServiceResult<LearningSetResponse> {
        val user = currentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val enrollment = enrollmentRepository.findByUserIdAndIsFocusedTrue(userId)
            ?: throw BaseException.NotFoundException("Focused syllabus not found")
        val syllabusId = enrollment.syllabus.id ?: throw BaseException.NotFoundException("Syllabus not found")
        val courses = courseRepository.findAllBySyllabusIdOrderByTopicSortOrderAscCourseSortOrderAscIdAsc(syllabusId)
        if (courses.isEmpty()) {
            return ServiceResult(
                message = "Ok",
                result = LearningSetResponse(
                    available = false,
                    reason = "NO_VOCAB_TO_LEARN",
                ),
            )
        }

        val vocabByCourse = mutableMapOf<Long, List<Vocabulary>>()
        val allVocabIds = mutableListOf<Long>()
        courses.forEach { course ->
            val vocabularies = vocabularyRepository.findAllByCourseIdOrderBySortOrderAscIdAsc(course.id ?: 0L)
            vocabByCourse[course.id ?: 0L] = vocabularies
            allVocabIds.addAll(vocabularies.mapNotNull { it.id })
        }

        val progressList = if (allVocabIds.isEmpty()) {
            emptyList()
        } else {
            userVocabProgressRepository.findAllByUserIdAndVocabularyIdIn(userId, allVocabIds)
        }
        val progressMap = progressList.associateBy { it.vocabulary.id ?: 0L }

        val currentCourseIndex = resolveCurrentCourseIndex(courses, progressList)
        val perCourseNew = mutableMapOf<Long, List<Vocabulary>>()
        val perCourseReview = mutableMapOf<Long, List<ReviewCandidate>>()

        courses.forEach { course ->
            val vocabularies = vocabByCourse[course.id ?: 0L].orEmpty()
            val newWords = mutableListOf<Vocabulary>()
            val reviewWords = mutableListOf<ReviewCandidate>()
            vocabularies.forEach { vocab ->
                val vocabId = vocab.id ?: 0L
                val progress = progressMap[vocabId]
                if (progress == null) {
                    newWords.add(vocab)
                    return@forEach
                }
                val state = LearningState.fromCode(progress.learningState)
                when (state) {
                    LearningState.UNKNOWN -> newWords.add(vocab)
                    LearningState.INTRODUCED, LearningState.LEARNING -> reviewWords.add(
                        ReviewCandidate(vocab = vocab, progress = progress, state = state)
                    )
                    else -> Unit
                }
            }
            perCourseNew[course.id ?: 0L] = newWords
            perCourseReview[course.id ?: 0L] = reviewWords
        }

        val targetCourseIndex = resolveTargetCourseIndex(courses, perCourseNew, currentCourseIndex)
        val reviewCandidates = mutableListOf<ReviewCandidate>()
        for (index in 0..targetCourseIndex) {
            val courseId = courses[index].id ?: 0L
            reviewCandidates.addAll(perCourseReview[courseId].orEmpty())
        }
        val targetCourseId = courses[targetCourseIndex].id ?: 0L
        val newWords = perCourseNew[targetCourseId].orEmpty()

        if (newWords.isEmpty() && reviewCandidates.isEmpty()) {
            return ServiceResult(
                message = "Ok",
                result = LearningSetResponse(
                    available = false,
                    reason = "NO_VOCAB_TO_LEARN",
                ),
            )
        }

        val cards = if (reviewCandidates.isNotEmpty()) {
            buildCase2Cards(reviewCandidates, newWords)
        } else {
            buildCase1Cards(newWords)
        }
        return ServiceResult(
            message = "Ok",
            result = LearningSetResponse(
                available = true,
                cards = cards,
            ),
        )
    }

    @Transactional
    override fun complete(request: LearningSetCompleteRequest): ServiceResult<LearningSetCompleteResponse> {
        val vocabIds = request.vocabIds?.distinct().orEmpty()
        if (vocabIds.isEmpty()) {
            throw BaseException.BadRequestException("'vocab_ids' can't be empty")
        }
        val user = currentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")

        val vocabMap = vocabularyRepository.findAllById(vocabIds).associateBy { it.id ?: 0L }
        if (vocabMap.size != vocabIds.size) {
            throw BaseException.NotFoundException("Vocabulary not found")
        }

        val existing = userVocabProgressRepository.findAllByUserIdAndVocabularyIdIn(userId, vocabIds)
        val existingMap = existing.associateBy { it.vocabulary.id ?: 0L }
        val toSave = mutableListOf<UserVocabProgress>()
        val now = LocalDateTime.now()

        vocabIds.forEach { vocabId ->
            val vocab = vocabMap[vocabId] ?: return@forEach
            val progress = existingMap[vocabId]
            if (progress == null) {
                toSave.add(
                    UserVocabProgress(
                        user = user,
                        vocabulary = vocab,
                        learningState = LearningState.INTRODUCED.code,
                        exposureCount = 1,
                        lastExposedAt = now,
                    )
                )
                return@forEach
            }
            val state = LearningState.fromCode(progress.learningState)
            val newStateCode = if (state == LearningState.UNKNOWN) {
                LearningState.INTRODUCED.code
            } else {
                progress.learningState
            }
            toSave.add(
                UserVocabProgress(
                    id = progress.id,
                    user = progress.user,
                    vocabulary = progress.vocabulary,
                    learningState = newStateCode,
                    exposureCount = progress.exposureCount + 1,
                    lastExposedAt = now,
                    correctStreak = progress.correctStreak,
                    nextReviewAfter = progress.nextReviewAfter,
                    createdAt = progress.createdAt,
                    updatedAt = progress.updatedAt,
                )
            )
        }

        if (toSave.isNotEmpty()) {
            userVocabProgressRepository.saveAll(toSave)
        }
        return ServiceResult(
            message = "Ok",
            result = LearningSetCompleteResponse(updatedCount = toSave.size),
        )
    }

    @Transactional(readOnly = true)
    override fun viewCourseVocabularySet(courseId: Long): ServiceResult<LearningSetResponse> {
        courseRepository.findById(courseId).orElseThrow {
            BaseException.NotFoundException("COURSE_NOT_FOUND")
        }
        val vocabularies = vocabularyRepository.findAllByCourseIdOrderBySortOrderAscIdAsc(courseId)
        if (vocabularies.isEmpty()) {
            return ServiceResult(
                message = "Ok",
                result = LearningSetResponse(
                    available = false,
                    reason = "NO_VOCAB_IN_COURSE",
                ),
            )
        }
        val cards = buildCardsWithOrder(
            vocabularies.map { vocab -> CardSeed(vocab = vocab, cardType = LearningSetCardType.VIEW) }
        )
        return ServiceResult(
            message = "Ok",
            result = LearningSetResponse(
                available = true,
                cards = cards,
            ),
        )
    }

    private fun buildCase1Cards(newWords: List<Vocabulary>): List<LearningSetCardResponse> {
        if (newWords.isEmpty()) {
            return emptyList()
        }
        val selected = newWords.take(SET_SIZE_MAX)
        return buildCardsWithOrder(
            selected.map { vocab -> CardSeed(vocab = vocab, cardType = LearningSetCardType.NEW) }
        )
    }

    private fun buildCase2Cards(
        reviewCandidates: List<ReviewCandidate>,
        newWords: List<Vocabulary>,
    ): List<LearningSetCardResponse> {
        val sortedReview = reviewCandidates.sortedWith(
            compareBy<ReviewCandidate> { it.statePriority }
                .thenBy { it.progress.exposureCount }
                .thenBy { it.vocab.id ?: 0L }
        )
        val reviewSelected = sortedReview.take(SET_SIZE_MAX)
        val remainingSlots = SET_SIZE_MAX - reviewSelected.size
        val newSelected = if (remainingSlots > 0) newWords.take(remainingSlots) else emptyList()

        val seeds = mutableListOf<CardSeed>()
        seeds.addAll(reviewSelected.map { candidate ->
            CardSeed(vocab = candidate.vocab, cardType = LearningSetCardType.REVIEW)
        })
        seeds.addAll(newSelected.map { vocab ->
            CardSeed(vocab = vocab, cardType = LearningSetCardType.NEW)
        })
        return buildCardsWithOrder(seeds)
    }

    private fun resolveCurrentCourseIndex(
        courses: List<com.exe.vocafy_BE.model.entity.Course>,
        progressList: List<UserVocabProgress>,
    ): Int {
        if (courses.isEmpty()) {
            return 0
        }
        val latest = progressList
            .filter { it.lastExposedAt != null }
            .maxByOrNull { it.lastExposedAt ?: LocalDateTime.MIN }
        if (latest == null) {
            return 0
        }
        val latestCourseId = latest.vocabulary.course.id ?: return 0
        val index = courses.indexOfFirst { it.id == latestCourseId }
        return if (index >= 0) index else 0
    }

    private fun resolveTargetCourseIndex(
        courses: List<com.exe.vocafy_BE.model.entity.Course>,
        perCourseNew: Map<Long, List<Vocabulary>>,
        currentCourseIndex: Int,
    ): Int {
        if (courses.isEmpty()) {
            return 0
        }
        for (index in currentCourseIndex until courses.size) {
            val courseId = courses[index].id ?: 0L
            if (perCourseNew[courseId].orEmpty().isNotEmpty()) {
                return index
            }
        }
        return currentCourseIndex.coerceIn(0, courses.size - 1)
    }

    private fun buildCardsWithOrder(seeds: List<CardSeed>): List<LearningSetCardResponse> {
        return seeds.mapIndexed { index, seed ->
            LearningSetCardResponse(
                orderIndex = index + 1,
                vocabId = seed.vocab.id ?: 0L,
                cardType = seed.cardType,
                vocab = buildVocabularyResponse(seed.vocab),
            )
        }
    }

    private fun buildVocabularyResponse(entity: Vocabulary): VocabularyResponse {
        val vocabId = entity.id ?: 0L
        val terms = vocabularyTermRepository.findAllByVocabularyIdOrderByIdAsc(vocabId)
            .filter { it.languageCode != com.exe.vocafy_BE.enum.LanguageCode.EN }
            .map {
            VocabularyTermResponse(
                id = it.id ?: 0,
                languageCode = it.languageCode,
                scriptType = it.scriptType,
                textValue = it.textValue,
                extraMeta = it.extraMeta,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        }
        val meanings = vocabularyMeaningRepository.findAllByVocabularyIdOrderBySenseOrderAscIdAsc(vocabId).map {
            VocabularyMeaningResponse(
                id = it.id ?: 0,
                languageCode = it.languageCode,
                meaningText = it.meaningText,
                exampleSentence = it.exampleSentence,
                exampleTranslation = it.exampleTranslation,
                partOfSpeech = it.partOfSpeech,
                senseOrder = it.senseOrder,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        }
        val medias = vocabularyMediaRepository.findAllByVocabularyIdOrderByIdAsc(vocabId).map {
            VocabularyMediaResponse(
                id = it.id ?: 0,
                mediaType = it.mediaType,
                url = it.url,
                meta = it.meta,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        }
        return VocabularyResponse(
            id = entity.id ?: 0,
            courseId = entity.course.id ?: 0,
            note = entity.note,
            sortOrder = entity.sortOrder,
            terms = terms,
            meanings = meanings,
            medias = medias,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
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

    private data class ReviewCandidate(
        val vocab: Vocabulary,
        val progress: UserVocabProgress,
        val state: LearningState,
    ) {
        val statePriority: Int
            get() = if (state == LearningState.LEARNING) 0 else 1
    }

    private data class CardSeed(
        val vocab: Vocabulary,
        val cardType: LearningSetCardType,
    )

    companion object {
        private const val SET_SIZE_MAX = 18
    }
}
