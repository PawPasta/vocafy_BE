package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.LearningSetCardType
import com.exe.vocafy_BE.enum.LearningState
import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.request.LearningSetCompleteRequest
import com.exe.vocafy_BE.model.dto.request.LearningSetGenerateRequest
import com.exe.vocafy_BE.model.dto.response.LearningSetCardResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetCompleteResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetVocabularyMeaningResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetVocabularyMediaResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetVocabularyResponse
import com.exe.vocafy_BE.model.dto.response.LearningSetVocabularyTermResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.entity.Enrollment
import com.exe.vocafy_BE.model.entity.UserDailyActivity
import com.exe.vocafy_BE.model.entity.Vocabulary
import com.exe.vocafy_BE.model.entity.VocabularyExample
import com.exe.vocafy_BE.model.entity.VocabularyExampleTranslation
import com.exe.vocafy_BE.model.entity.UserVocabProgress
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.CourseVocabularyLinkRepository
import com.exe.vocafy_BE.repo.EnrollmentRepository
import com.exe.vocafy_BE.repo.UserDailyActivityRepository
import com.exe.vocafy_BE.repo.UserStudyBudgetRepository
import com.exe.vocafy_BE.repo.UserVocabProgressRepository
import com.exe.vocafy_BE.repo.VocabularyExampleRepository
import com.exe.vocafy_BE.repo.VocabularyExampleTranslationRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.service.LearningSetService
import com.exe.vocafy_BE.util.SecurityUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Service
class LearningSetServiceImpl(
    private val securityUtil: SecurityUtil,
    private val enrollmentRepository: EnrollmentRepository,
    private val courseRepository: CourseRepository,
    private val courseVocabularyLinkRepository: CourseVocabularyLinkRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val userVocabProgressRepository: UserVocabProgressRepository,
    private val userDailyActivityRepository: UserDailyActivityRepository,
    private val userStudyBudgetRepository: UserStudyBudgetRepository,
    private val vocabularyTermRepository: VocabularyTermRepository,
    private val vocabularyMeaningRepository: VocabularyMeaningRepository,
    private val vocabularyExampleRepository: VocabularyExampleRepository,
    private val vocabularyExampleTranslationRepository: VocabularyExampleTranslationRepository,
    private val vocabularyMediaRepository: VocabularyMediaRepository,
) : LearningSetService {

    @Transactional
    override fun generate(request: LearningSetGenerateRequest): ServiceResult<LearningSetResponse> {
        val user = securityUtil.getCurrentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val requestedSyllabusId = request.syllabusId
        val enrollment = resolveFocusedEnrollment(userId, requestedSyllabusId)
        val preferredTargetLanguage = enrollment.preferredTargetLanguage
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
            val vocabularies = courseVocabularyLinkRepository.findVocabulariesByCourseId(course.id ?: 0L)
            vocabByCourse[course.id ?: 0L] = vocabularies
            allVocabIds.addAll(vocabularies.mapNotNull { it.id })
        }

        val progressList = if (allVocabIds.isEmpty()) {
            emptyList()
        } else {
            userVocabProgressRepository.findAllByUserIdAndVocabularyIdIn(userId, allVocabIds)
        }
        val progressMap = progressList.associateBy { it.vocabulary.id ?: 0L }
        val todayNewCount = countTodayNew(userId)

        val currentCourseIndex = resolveCurrentCourseIndex(courses, progressList)
        val perCourseNew = mutableMapOf<Long, List<Vocabulary>>()
        val perCourseReview = mutableMapOf<Long, List<ReviewCandidate>>()

        courses.forEach courseLoop@{ course ->
            val vocabularies = vocabByCourse[course.id ?: 0L].orEmpty()
            val newWords = mutableListOf<Vocabulary>()
            val reviewWords = mutableListOf<ReviewCandidate>()
            vocabularies.forEach vocabLoop@{ vocab ->
                val vocabId = vocab.id ?: 0L
                val progress = progressMap[vocabId]
                if (progress == null) {
                    newWords.add(vocab)
                    return@vocabLoop
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
            buildCase2Cards(reviewCandidates, newWords, todayNewCount, preferredTargetLanguage)
        } else {
            buildCase1Cards(newWords, preferredTargetLanguage)
        }
        return ServiceResult(
            message = "Ok",
            result = LearningSetResponse(
                available = true,
                cards = cards,
            ),
        )
    }

    private fun resolveFocusedEnrollment(userId: UUID, requestedSyllabusId: Long?): Enrollment {
        val focused = enrollmentRepository.findByUserIdAndIsFocusedTrue(userId)
        if (requestedSyllabusId == null) {
            return focused ?: throw BaseException.NotFoundException("Focused syllabus not found")
        }
        if (focused != null && focused.syllabus.id == requestedSyllabusId) {
            return focused
        }
        val target = enrollmentRepository.findByUserIdAndSyllabusId(userId, requestedSyllabusId)
            ?: throw BaseException.NotFoundException("Enrollment not found")
        enrollmentRepository.clearFocused(userId)
        return enrollmentRepository.save(
            Enrollment(
                id = target.id,
                user = target.user,
                syllabus = target.syllabus,
                startDate = target.startDate,
                status = target.status,
                preferredTargetLanguage = target.preferredTargetLanguage,
                isFocused = true,
            )
        )
    }

    @Transactional
    override fun complete(request: LearningSetCompleteRequest): ServiceResult<LearningSetCompleteResponse> {
        val vocabIds = request.vocabIds?.distinct().orEmpty()
        if (vocabIds.isEmpty()) {
            throw BaseException.BadRequestException("'vocab_ids' can't be empty")
        }
        val user = securityUtil.getCurrentUser()
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
                        wrongStreak = 0,
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
                    wrongStreak = progress.wrongStreak,
                    nextReviewAfter = progress.nextReviewAfter,
                    createdAt = progress.createdAt,
                    updatedAt = progress.updatedAt,
                )
            )
        }

        if (toSave.isNotEmpty()) {
            userVocabProgressRepository.saveAll(toSave)
        }
        updateStreakOnActivity(user)
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
        val vocabularies = courseVocabularyLinkRepository.findVocabulariesByCourseId(courseId)
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

    private fun buildCase1Cards(
        newWords: List<Vocabulary>,
        preferredTargetLanguage: LanguageCode?,
    ): List<LearningSetCardResponse> {
        if (newWords.isEmpty()) {
            return emptyList()
        }
        val selected = newWords.take(SET_SIZE_MAX)
        return buildCardsWithOrder(
            selected.map { vocab -> CardSeed(vocab = vocab, cardType = LearningSetCardType.NEW) },
            preferredTargetLanguage,
        )
    }

    private fun buildCase2Cards(
        reviewCandidates: List<ReviewCandidate>,
        newWords: List<Vocabulary>,
        todayNewCount: Int,
        preferredTargetLanguage: LanguageCode?,
    ): List<LearningSetCardResponse> {
        val sortedReview = reviewCandidates.sortedWith(
            compareBy<ReviewCandidate> { it.statePriority }
                .thenBy { it.progress.exposureCount }
                .thenBy { it.vocab.id ?: 0L }
        )
        val extraReview = (todayNewCount / REVIEW_BOOST_STEP) * REVIEW_BOOST_PER_STEP
        val reviewLimit = (DEFAULT_REVIEW_LIMIT + extraReview).coerceAtMost(SET_SIZE_MAX)
        val reviewSelected = sortedReview.take(reviewLimit)
        val remainingSlots = SET_SIZE_MAX - reviewSelected.size
        val newSelected = if (remainingSlots > 0) newWords.take(remainingSlots) else emptyList()

        val seeds = mutableListOf<CardSeed>()
        seeds.addAll(reviewSelected.map { candidate ->
            CardSeed(vocab = candidate.vocab, cardType = LearningSetCardType.REVIEW)
        })
        seeds.addAll(newSelected.map { vocab ->
            CardSeed(vocab = vocab, cardType = LearningSetCardType.NEW)
        })
        return buildCardsWithOrder(seeds, preferredTargetLanguage)
    }

    private fun resolveCurrentCourseIndex(
        courses: List<com.exe.vocafy_BE.model.entity.Course>,
        progressList: List<UserVocabProgress>,
    ): Int {
        if (courses.isEmpty()) {
            return 0
        }
        val courseIds = courses.mapNotNull { it.id }
        if (courseIds.isEmpty()) {
            return 0
        }
        val latest = progressList
            .filter { it.lastExposedAt != null }
            .maxByOrNull { it.lastExposedAt ?: LocalDateTime.MIN }
        if (latest == null) {
            return 0
        }
        val latestCourseId = courseVocabularyLinkRepository
            .findFirstByVocabularyIdAndCourseIdIn(latest.vocabulary.id ?: 0L, courseIds)
            ?.course
            ?.id
            ?: return 0
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

    private fun countTodayNew(userId: UUID): Int {
        val today = LocalDate.now()
        val start = today.atStartOfDay()
        val end = LocalDateTime.of(today, LocalTime.MAX).plusNanos(1)
        return userVocabProgressRepository.countNewToday(userId, start, end).toInt()
    }

    private fun updateStreakOnActivity(user: com.exe.vocafy_BE.model.entity.User) {
        val userId = user.id ?: return
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val latest = userDailyActivityRepository.findTopByUserIdOrderByActivityDateDesc(userId)
        val newStreak = when {
            latest?.activityDate == null -> 1
            latest.activityDate.isEqual(today) -> latest.streakSnapshot
            latest.activityDate.isEqual(yesterday) -> latest.streakSnapshot + 1
            else -> 1
        }

        val todayActivity = userDailyActivityRepository.findByUserIdAndActivityDate(userId, today)
        if (todayActivity == null) {
            userDailyActivityRepository.save(
                UserDailyActivity(
                    user = user,
                    activityDate = today,
                    isGoalCompleted = true,
                    streakSnapshot = newStreak,
                )
            )
        } else {
            userDailyActivityRepository.save(
                UserDailyActivity(
                    id = todayActivity.id,
                    user = todayActivity.user,
                    activityDate = todayActivity.activityDate,
                    isGoalCompleted = true,
                    streakSnapshot = newStreak,
                    createdAt = todayActivity.createdAt,
                    updatedAt = todayActivity.updatedAt,
                )
            )
        }

        val budget = userStudyBudgetRepository.findByUserId(userId) ?: return
        userStudyBudgetRepository.save(
            com.exe.vocafy_BE.model.entity.UserStudyBudget(
                id = budget.id,
                user = budget.user,
                dailyMinutes = budget.dailyMinutes,
                dailyCardLimit = budget.dailyCardLimit,
                usedCardsToday = budget.usedCardsToday,
                streakCount = newStreak,
                createdAt = budget.createdAt,
                updatedAt = budget.updatedAt,
            )
        )
    }

    private fun buildCardsWithOrder(
        seeds: List<CardSeed>,
        preferredTargetLanguage: LanguageCode? = null,
    ): List<LearningSetCardResponse> {
        return seeds.mapIndexed { index, seed ->
            LearningSetCardResponse(
                orderIndex = index + 1,
                vocabId = seed.vocab.id ?: 0L,
                cardType = seed.cardType,
                vocab = buildVocabularyResponse(seed.vocab, preferredTargetLanguage),
            )
        }
    }

    private fun buildVocabularyResponse(
        entity: Vocabulary,
        preferredTargetLanguage: LanguageCode? = null,
    ): LearningSetVocabularyResponse {
        val vocabId = entity.id ?: 0L
        val termRows = vocabularyTermRepository.findAllByVocabularyIdOrderByIdAsc(vocabId)
        val studyLanguage = termRows.firstOrNull { it.languageCode != LanguageCode.EN }?.languageCode
            ?: termRows.firstOrNull()?.languageCode
        val terms = termRows
            .filter { it.languageCode != com.exe.vocafy_BE.enum.LanguageCode.EN }
            .map {
            LearningSetVocabularyTermResponse(
                id = it.id ?: 0,
                languageCode = it.languageCode,
                scriptType = it.scriptType,
                textValue = it.textValue,
                extraMeta = it.extraMeta,
            )
        }
        val examples = vocabularyExampleRepository.findAllByVocabularyIdOrderBySortOrderAscIdAsc(vocabId)
        val exampleTranslations = examples.mapNotNull { it.id }
            .takeIf { it.isNotEmpty() }
            ?.let { vocabularyExampleTranslationRepository.findAllByVocabularyExampleIdInOrderByIdAsc(it) }
            .orEmpty()
            .groupBy { it.vocabularyExample.id ?: 0L }
        val allMeanings = vocabularyMeaningRepository.findAllByVocabularyIdOrderBySenseOrderAscIdAsc(vocabId)
        val meanings = if (preferredTargetLanguage == null) {
            allMeanings
        } else {
            allMeanings.filter { it.languageCode == preferredTargetLanguage }.ifEmpty {
                throw BaseException.BadRequestException(
                    "Learning set is not ready for preferred target language '$preferredTargetLanguage'",
                )
            }
        }.map {
            val selectedExample = resolveVocabularyExample(
                examples = examples,
                studyLanguage = studyLanguage,
                meaningLanguage = it.languageCode,
                senseOrder = it.senseOrder,
            )
            val selectedTranslation = resolveVocabularyExampleTranslation(
                example = selectedExample,
                translationsByExampleId = exampleTranslations,
                preferredTargetLanguage = preferredTargetLanguage,
                meaningLanguage = it.languageCode,
            )
            if (preferredTargetLanguage != null && selectedTranslation == null) {
                throw BaseException.BadRequestException(
                    "Learning set is not ready for preferred target language '$preferredTargetLanguage'",
                )
            }
            LearningSetVocabularyMeaningResponse(
                id = it.id ?: 0,
                languageCode = it.languageCode,
                meaningText = it.meaningText,
                exampleSentence = selectedExample?.sentenceText,
                exampleTranslation = selectedTranslation?.translationText,
                partOfSpeech = it.partOfSpeech,
                senseOrder = it.senseOrder,
            )
        }
        val medias = vocabularyMediaRepository.findAllByVocabularyIdOrderByIdAsc(vocabId).map {
            LearningSetVocabularyMediaResponse(
                id = it.id ?: 0,
                mediaType = it.mediaType,
                url = it.url,
                meta = it.meta,
            )
        }
        return LearningSetVocabularyResponse(
            id = entity.id ?: 0,
            courseId = courseVocabularyLinkRepository
                .findFirstByVocabularyIdOrderByIdAsc(vocabId)
                ?.course
                ?.id,
            createdByUserId = entity.createdBy.id?.toString(),
            note = entity.note,
            sortOrder = entity.sortOrder,
            isActive = entity.isActive,
            isDeleted = entity.isDeleted,
            terms = terms,
            meanings = meanings,
            medias = medias,
        )
    }

    private fun resolveVocabularyExample(
        examples: List<VocabularyExample>,
        studyLanguage: LanguageCode?,
        meaningLanguage: LanguageCode,
        senseOrder: Int?,
    ): VocabularyExample? {
        if (examples.isEmpty()) {
            return null
        }
        val targetSortOrder = senseOrder ?: 1
        val languagePriority = listOfNotNull(studyLanguage, meaningLanguage).distinct()
        languagePriority.forEach { languageCode ->
            examples.firstOrNull { it.languageCode == languageCode && it.sortOrder == targetSortOrder }?.let {
                return it
            }
            examples.firstOrNull { it.languageCode == languageCode }?.let {
                return it
            }
        }
        return examples.firstOrNull { it.sortOrder == targetSortOrder } ?: examples.firstOrNull()
    }

    private fun resolveVocabularyExampleTranslation(
        example: VocabularyExample?,
        translationsByExampleId: Map<Long, List<VocabularyExampleTranslation>>,
        preferredTargetLanguage: LanguageCode?,
        meaningLanguage: LanguageCode,
    ): VocabularyExampleTranslation? {
        val exampleId = example?.id ?: return null
        val translations = translationsByExampleId[exampleId].orEmpty()
        if (translations.isEmpty()) {
            return null
        }
        val language = preferredTargetLanguage ?: meaningLanguage
        return translations.firstOrNull { it.languageCode == language }
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
        private const val DEFAULT_REVIEW_LIMIT = 10
        private const val REVIEW_BOOST_STEP = 5
        private const val REVIEW_BOOST_PER_STEP = 2
    }
}
