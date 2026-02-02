package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.LearningState
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.request.LearningAnswerRequest
import com.exe.vocafy_BE.model.dto.response.LearningStateUpdateResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.entity.UserVocabProgress
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.repo.UserVocabProgressRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.service.LearningProgressService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class LearningProgressServiceImpl(
    private val userRepository: UserRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val userVocabProgressRepository: UserVocabProgressRepository,
    private val termRepository: VocabularyTermRepository,
    private val meaningRepository: VocabularyMeaningRepository,
    private val mediaRepository: VocabularyMediaRepository,
) : LearningProgressService {

    @Transactional
    override fun submitAnswer(request: LearningAnswerRequest): ServiceResult<LearningStateUpdateResponse> {
        val userId = currentUserId()
        val user = userRepository.findById(userId)
            .orElseThrow { BaseException.NotFoundException("User not found") }
        val vocabularyId = resolveVocabularyId(request)
        val vocabulary = vocabularyRepository.findById(vocabularyId)
            .orElseThrow { BaseException.NotFoundException("Vocabulary not found") }

        val expectedAnswerId = resolveExpectedAnswerId(request, vocabularyId)
        val isCorrect = request.answerId == expectedAnswerId
        val existing = userVocabProgressRepository.findByUserIdAndVocabularyId(userId, vocabularyId)
        val now = LocalDateTime.now()
        val currentState = existing?.let { LearningState.fromCode(it.learningState) } ?: LearningState.UNKNOWN

        val prevStateName = normalizeStateName(currentState)
        val prevIndex = normalizeStateIndex(currentState)

        val updatedStreaks = updateStreaks(existing, isCorrect)
        val delta = if (isCorrect) {
            if (updatedStreaks.correctStreak >= 3) 2 else 1
        } else {
            if (updatedStreaks.wrongStreak >= 2) -1 else 0
        }

        val baseIndex = if (prevIndex < 1) 1 else prevIndex
        val nextIndex = (baseIndex + delta).coerceIn(1, 4)
        val newState = denormalizeState(nextIndex)

        val saved = userVocabProgressRepository.save(
            UserVocabProgress(
                id = existing?.id,
                user = user,
                vocabulary = vocabulary,
                learningState = newState.code,
                exposureCount = (existing?.exposureCount ?: 0) + 1,
                lastExposedAt = now,
                correctStreak = updatedStreaks.correctStreak,
                wrongStreak = updatedStreaks.wrongStreak,
                nextReviewAfter = existing?.nextReviewAfter,
                createdAt = existing?.createdAt,
                updatedAt = existing?.updatedAt,
            )
        )

        return ServiceResult(
            message = "Ok",
            result = LearningStateUpdateResponse(
                vocabId = vocabularyId,
                isCorrect = isCorrect,
                prevState = prevStateName,
                newState = normalizeStateName(LearningState.fromCode(saved.learningState)),
                correctStreak = saved.correctStreak,
                wrongStreak = saved.wrongStreak,
            ),
        )
    }

    private data class Streaks(val correctStreak: Short, val wrongStreak: Short)

    private fun updateStreaks(existing: UserVocabProgress?, isCorrect: Boolean): Streaks {
        val currentCorrect = existing?.correctStreak ?: 0
        val currentWrong = existing?.wrongStreak ?: 0
        return if (isCorrect) {
            Streaks((currentCorrect + 1).toShort(), 0)
        } else {
            Streaks(0, (currentWrong + 1).toShort())
        }
    }

    private fun resolveVocabularyId(request: LearningAnswerRequest): Long {
        return when (request.questionType) {
            com.exe.vocafy_BE.enum.VocabularyQuestionType.LOOK_MEANING_INPUT_TERM -> {
                requireRefType("MEANING", request.questionRef.type)
                val meaning = meaningRepository.findById(request.questionRef.id)
                    .orElseThrow { BaseException.NotFoundException("Meaning not found") }
                meaning.vocabulary.id ?: throw BaseException.NotFoundException("Vocabulary not found")
            }
            com.exe.vocafy_BE.enum.VocabularyQuestionType.LOOK_TERM_SELECT_MEANING -> {
                requireRefType("TERM", request.questionRef.type)
                val term = termRepository.findById(request.questionRef.id)
                    .orElseThrow { BaseException.NotFoundException("Term not found") }
                term.vocabulary.id ?: throw BaseException.NotFoundException("Vocabulary not found")
            }
            com.exe.vocafy_BE.enum.VocabularyQuestionType.LISTEN_SELECT_TERM,
            com.exe.vocafy_BE.enum.VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM,
            -> {
                requireRefType("MEDIA", request.questionRef.type)
                val media = mediaRepository.findById(request.questionRef.id)
                    .orElseThrow { BaseException.NotFoundException("Media not found") }
                media.vocabulary.id ?: throw BaseException.NotFoundException("Vocabulary not found")
            }
        }
    }

    private fun resolveExpectedAnswerId(request: LearningAnswerRequest, vocabularyId: Long): Long {
        return when (request.questionType) {
            com.exe.vocafy_BE.enum.VocabularyQuestionType.LOOK_TERM_SELECT_MEANING -> {
                val meaning = meaningRepository.findAllByVocabularyIdOrderBySenseOrderAscIdAsc(vocabularyId)
                    .firstOrNull() ?: throw BaseException.NotFoundException("Meaning not found")
                meaning.id ?: throw BaseException.NotFoundException("Meaning not found")
            }
            com.exe.vocafy_BE.enum.VocabularyQuestionType.LOOK_MEANING_INPUT_TERM,
            com.exe.vocafy_BE.enum.VocabularyQuestionType.LISTEN_SELECT_TERM,
            com.exe.vocafy_BE.enum.VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM,
            -> {
                val term = termRepository.findAllByVocabularyIdOrderByIdAsc(vocabularyId)
                    .firstOrNull() ?: throw BaseException.NotFoundException("Term not found")
                term.id ?: throw BaseException.NotFoundException("Term not found")
            }
        }
    }

    private fun requireRefType(expected: String, actual: String) {
        if (!actual.equals(expected, ignoreCase = true)) {
            throw BaseException.BadRequestException("Invalid question ref type")
        }
    }

    private fun normalizeStateIndex(state: LearningState): Int =
        when (state) {
            LearningState.UNKNOWN -> 0
            LearningState.INTRODUCED -> 1
            LearningState.LEARNING -> 2
            LearningState.FAMILIAR,
            LearningState.RECOGNIZED,
            LearningState.RECALLED,
            LearningState.UNDERSTOOD,
            -> 3
            LearningState.MASTERED -> 4
        }

    private fun normalizeStateName(state: LearningState): String =
        when (state) {
            LearningState.UNKNOWN -> "UNKNOWN"
            LearningState.INTRODUCED -> "INTRODUCED"
            LearningState.LEARNING -> "LEARNING"
            LearningState.FAMILIAR,
            LearningState.RECOGNIZED,
            LearningState.RECALLED,
            LearningState.UNDERSTOOD,
            -> "UNDERSTOOD"
            LearningState.MASTERED -> "MASTERED"
        }

    private fun denormalizeState(index: Int): LearningState =
        when (index) {
            1 -> LearningState.INTRODUCED
            2 -> LearningState.LEARNING
            3 -> LearningState.UNDERSTOOD
            4 -> LearningState.MASTERED
            else -> LearningState.INTRODUCED
        }

    private fun currentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val jwt = authentication.principal as? Jwt
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        return runCatching { UUID.fromString(jwt.subject) }
            .getOrElse { throw BaseException.UnauthorizedException("Unauthorized") }
    }
}
