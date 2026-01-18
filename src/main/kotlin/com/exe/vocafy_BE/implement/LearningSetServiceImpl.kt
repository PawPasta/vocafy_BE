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
import java.util.UUID
import kotlin.math.floor

@Service
class LearningSetServiceImpl(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val userVocabProgressRepository: UserVocabProgressRepository,
    private val vocabularyTermRepository: VocabularyTermRepository,
    private val vocabularyMeaningRepository: VocabularyMeaningRepository,
    private val vocabularyMediaRepository: VocabularyMediaRepository,
) : LearningSetService {

    @Transactional(readOnly = true)
    override fun generate(request: LearningSetGenerateRequest): ServiceResult<LearningSetResponse> {
        val courseId = request.courseId ?: throw BaseException.BadRequestException("'course_id' can't be null")
        courseRepository.findById(courseId).orElseThrow {
            BaseException.NotFoundException("COURSE_NOT_FOUND")
        }

        val vocabularies = vocabularyRepository.findAllByCourseIdOrderBySortOrderAscIdAsc(courseId)
        val vocabIds = vocabularies.mapNotNull { it.id }
        val user = currentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val progressList = if (vocabIds.isEmpty()) {
            emptyList()
        } else {
            userVocabProgressRepository.findAllByUserIdAndVocabularyIdIn(userId, vocabIds)
        }
        val progressMap = progressList.associateBy { it.vocabulary.id ?: 0L }

        val newWords = mutableListOf<Vocabulary>()
        val reviewCandidates = mutableListOf<ReviewCandidate>()

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
                LearningState.INTRODUCED, LearningState.LEARNING -> reviewCandidates.add(
                    ReviewCandidate(vocab = vocab, progress = progress, state = state)
                )
                else -> Unit
            }
        }

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

        vocabIds.forEach { vocabId ->
            val vocab = vocabMap[vocabId] ?: return@forEach
            val progress = existingMap[vocabId]
            if (progress == null) {
                toSave.add(
                    UserVocabProgress(
                        user = user,
                        vocabulary = vocab,
                        learningState = LearningState.INTRODUCED.code,
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
        val drafts = vocabularies.map { vocab ->
            CardDraft(
                vocabId = vocab.id ?: 0L,
                cardType = LearningSetCardType.VIEW,
                vocab = buildVocabularyResponse(vocab),
            )
        }
        val cards = drafts.mapIndexed { index, draft ->
            LearningSetCardResponse(
                orderIndex = index + 1,
                vocabId = draft.vocabId,
                cardType = draft.cardType,
                vocab = draft.vocab,
            )
        }
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
        val newWordCount = minOf(newWords.size, MAX_NEW_WORDS)
        val repeatPerWord = computeRepeatPerWord(newWordCount)
        val selected = newWords.take(newWordCount)
        val drafts = mutableListOf<CardDraft>()
        selected.forEach { vocab ->
            val vocabId = vocab.id ?: 0L
            val vocabResponse = buildVocabularyResponse(vocab)
            repeat(repeatPerWord) {
                drafts.add(
                    CardDraft(
                        vocabId = vocabId,
                        cardType = LearningSetCardType.NEW,
                        vocab = vocabResponse,
                    )
                )
            }
        }
        return orderCards(drafts)
    }

    private fun buildCase2Cards(
        reviewCandidates: List<ReviewCandidate>,
        newWords: List<Vocabulary>,
    ): List<LearningSetCardResponse> {
        val maxReviewCards = floor(SET_SIZE_MAX * REVIEW_RATIO_MAX).toInt()
        val sortedReview = reviewCandidates.sortedWith(
            compareBy<ReviewCandidate> { it.statePriority }
                // No exposure_count in schema; use lower correctStreak as a proxy for lower exposure.
                .thenBy { it.progress.correctStreak }
                .thenBy { it.vocab.id ?: 0L }
        )
        val reviewCount = minOf(sortedReview.size, maxReviewCards)
        val reviewSelected = sortedReview.take(reviewCount)
        val remainingSlots = SET_SIZE_MAX - reviewCount

        val reviewDrafts = reviewSelected.map { candidate ->
            val vocabId = candidate.vocab.id ?: 0L
            CardDraft(
                vocabId = vocabId,
                cardType = LearningSetCardType.REVIEW,
                vocab = buildVocabularyResponse(candidate.vocab),
            )
        }

        if (newWords.isEmpty() || remainingSlots < 2) {
            return orderCards(reviewDrafts)
        }

        val newWordCount = minOf(newWords.size, floor(remainingSlots / 2.0).toInt(), MAX_NEW_WORDS)
        if (newWordCount == 0) {
            return orderCards(reviewDrafts)
        }
        val repeatPerNew = computeRepeatPerNew(remainingSlots, newWordCount)
        val newSelected = newWords.take(newWordCount)
        val newDrafts = mutableListOf<CardDraft>()
        newSelected.forEach { vocab ->
            val vocabId = vocab.id ?: 0L
            val vocabResponse = buildVocabularyResponse(vocab)
            repeat(repeatPerNew) {
                newDrafts.add(
                    CardDraft(
                        vocabId = vocabId,
                        cardType = LearningSetCardType.NEW,
                        vocab = vocabResponse,
                    )
                )
            }
        }
        return orderCards(reviewDrafts + newDrafts)
    }

    private fun computeRepeatPerWord(newWordCount: Int): Int {
        var repeatPerWord = SET_SIZE_MAX / newWordCount
        if (repeatPerWord < MIN_REPEAT) {
            repeatPerWord = MIN_REPEAT
        }
        if (repeatPerWord > MAX_REPEAT) {
            repeatPerWord = MAX_REPEAT
        }
        return repeatPerWord
    }

    private fun computeRepeatPerNew(remainingSlots: Int, newWordCount: Int): Int {
        var repeatPerNew = remainingSlots / newWordCount
        if (repeatPerNew < MIN_REPEAT) {
            repeatPerNew = MIN_REPEAT
        }
        if (repeatPerNew > MAX_REPEAT) {
            repeatPerNew = MAX_REPEAT
        }
        return repeatPerNew
    }

    private fun orderCards(drafts: List<CardDraft>): List<LearningSetCardResponse> {
        if (drafts.isEmpty()) {
            return emptyList()
        }
        val buckets = drafts.groupBy { it.vocabId }.map { (vocabId, items) ->
            CardBucket(
                vocabId = vocabId,
                cardType = items.first().cardType,
                vocab = items.first().vocab,
                remaining = items.size,
            )
        }.toMutableList()
        val ordered = mutableListOf<LearningSetCardResponse>()
        var lastVocabId: Long? = null
        var lastType: LearningSetCardType? = null
        var orderIndex = 1

        while (buckets.isNotEmpty()) {
            var candidates = buckets.filter { it.vocabId != lastVocabId }
            if (candidates.isEmpty()) {
                candidates = buckets
            }
            val hasAltType = candidates.any { it.cardType != lastType }
            if (hasAltType && lastType != null) {
                candidates = candidates.filter { it.cardType != lastType }
            }
            val chosen = candidates.maxWithOrNull(
                compareBy<CardBucket> { it.remaining }
                    .thenBy { it.vocabId }
            ) ?: break
            ordered.add(
                LearningSetCardResponse(
                    orderIndex = orderIndex++,
                    vocabId = chosen.vocabId,
                    cardType = chosen.cardType,
                    vocab = chosen.vocab,
                )
            )
            chosen.remaining -= 1
            lastVocabId = chosen.vocabId
            lastType = chosen.cardType
            if (chosen.remaining <= 0) {
                buckets.removeIf { it.vocabId == chosen.vocabId }
            }
        }
        return ordered
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

    private data class CardDraft(
        val vocabId: Long,
        val cardType: LearningSetCardType,
        val vocab: VocabularyResponse,
    )

    private data class CardBucket(
        val vocabId: Long,
        val cardType: LearningSetCardType,
        val vocab: VocabularyResponse,
        var remaining: Int,
    )

    companion object {
        private const val SET_SIZE_MAX = 18
        private const val MAX_NEW_WORDS = 6
        private const val MIN_REPEAT = 2
        private const val MAX_REPEAT = 4
        private const val REVIEW_RATIO_MAX = 0.6
    }
}
