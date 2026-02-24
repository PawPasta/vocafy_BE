package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.VocabularyQuestionType
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyQuestionRefResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyQuestionResponse
import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LearningState
import com.exe.vocafy_BE.enum.MediaType
import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import com.exe.vocafy_BE.model.entity.VocabularyMedia
import com.exe.vocafy_BE.model.entity.VocabularyTerm
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyQuestionRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.repo.UserVocabProgressRepository
import com.exe.vocafy_BE.repo.EnrollmentRepository
import com.exe.vocafy_BE.service.VocabularyQuestionService
import com.exe.vocafy_BE.util.SecurityUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.max

@Service
class VocabularyQuestionServiceImpl(
    private val securityUtil: SecurityUtil,
    private val questionRepository: VocabularyQuestionRepository,
    private val termRepository: VocabularyTermRepository,
    private val meaningRepository: VocabularyMeaningRepository,
    private val mediaRepository: VocabularyMediaRepository,
    private val userVocabProgressRepository: UserVocabProgressRepository,
    private val enrollmentRepository: EnrollmentRepository,
) : VocabularyQuestionService {

    @Transactional(readOnly = true)
    override fun getRandom(): ServiceResult<VocabularyQuestionResponse> {
        val question = questionRepository.findRandom()
            ?: throw BaseException.NotFoundException("Question not found")
        val (questionRefType, answerRefType) = mapRefTypes(question.questionType)

        val questionRef = buildRef(questionRefType, question.questionRefId)
        val answerRef = buildRef(answerRefType, question.answerRefId)
        val questionText = buildQuestionText(question.questionType, questionRef)
        val options = buildOptions(answerRefType, answerRef.id)

        return ServiceResult(
            message = "Ok",
            result = VocabularyQuestionResponse(
                questionType = question.questionType,
                questionText = questionText,
                questionRef = questionRef,
                options = options,
                difficultyLevel = question.difficultyLevel,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun generateLearnedQuestions(count: Int?): ServiceResult<List<VocabularyQuestionResponse>> {
        val userId = securityUtil.getCurrentUserId()
        val targetCount = resolveTargetCount(count)
        val preferredTargetLanguage = enrollmentRepository
            .findByUserIdAndIsFocusedTrue(userId)
            ?.preferredTargetLanguage
        val sampleSize = max(targetCount * 3, 30).coerceAtMost(200)
        val vocabIds = userVocabProgressRepository.findRandomVocabIdsByUserIdAndLearningStateNot(
            userId,
            LearningState.UNKNOWN.code,
            sampleSize,
        ).distinct()

        if (vocabIds.isEmpty()) {
            throw BaseException.NotFoundException("No learned vocabulary found")
        }

        val allowMultiplePerVocab = vocabIds.size < 15
        val questions = mutableListOf<VocabularyQuestionResponse>()
        val usedKeys = mutableSetOf<String>()

        for (vocabId in vocabIds.shuffled()) {
            if (questions.size >= targetCount) break
            val terms = termRepository.findAllByVocabularyIdOrderByIdAsc(vocabId)
            if (terms.isEmpty()) continue
            val meanings = meaningRepository.findAllByVocabularyIdOrderBySenseOrderAscIdAsc(vocabId)
            val medias = mediaRepository.findAllByVocabularyIdOrderByIdAsc(vocabId)

            val term = terms.first()
            val meaning = selectMeaningByPreference(meanings, preferredTargetLanguage)
            val audioMedia = medias.firstOrNull { it.mediaType == MediaType.AUDIO_EN || it.mediaType == MediaType.AUDIO_JP }
            val imageMedia = medias.firstOrNull { it.mediaType == MediaType.IMAGE }

            val candidates = mutableListOf<VocabularyQuestionType>()
            if (meaning != null) {
                candidates.add(VocabularyQuestionType.LOOK_TERM_SELECT_MEANING)
                candidates.add(VocabularyQuestionType.LOOK_MEANING_INPUT_TERM)
            }
            if (audioMedia != null) {
                candidates.add(VocabularyQuestionType.LISTEN_SELECT_TERM)
            }
            if (imageMedia != null) {
                candidates.add(VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM)
            }
            if (candidates.isEmpty()) continue

            val typesToUse = if (allowMultiplePerVocab) candidates.shuffled() else listOf(candidates.random())
            for (type in typesToUse) {
                if (questions.size >= targetCount) break
                val response = buildQuestionFromVocab(type, term, meaning, audioMedia, imageMedia, usedKeys)
                if (response != null) {
                    questions.add(response)
                }
            }
        }

        return ServiceResult(
            message = "Ok",
            result = questions,
        )
    }

    private fun mapRefTypes(type: VocabularyQuestionType): Pair<String, String> =
        when (type) {
            VocabularyQuestionType.LISTEN_SELECT_TERM -> "MEDIA" to "TERM"
            VocabularyQuestionType.LOOK_TERM_SELECT_MEANING -> "TERM" to "MEANING"
            VocabularyQuestionType.LOOK_MEANING_INPUT_TERM -> "MEANING" to "TERM"
            VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM -> "MEDIA" to "TERM"
        }

    private fun buildRef(type: String, refId: Long): VocabularyQuestionRefResponse =
        when (type) {
            "TERM" -> {
                val term = termRepository.findById(refId)
                    .orElseThrow { BaseException.NotFoundException("Term not found") }
                VocabularyQuestionRefResponse(
                    type = type,
                    id = refId,
                    text = term.textValue,
                )
            }
            "MEANING" -> {
                val meaning = meaningRepository.findById(refId)
                    .orElseThrow { BaseException.NotFoundException("Meaning not found") }
                VocabularyQuestionRefResponse(
                    type = type,
                    id = refId,
                    text = meaning.meaningText,
                )
            }
            "MEDIA" -> {
                val media = mediaRepository.findById(refId)
                    .orElseThrow { BaseException.NotFoundException("Media not found") }
                VocabularyQuestionRefResponse(
                    type = type,
                    id = refId,
                    url = media.url,
                )
            }
            else -> throw BaseException.BadRequestException("Invalid ref type")
        }

    private fun buildOptions(refType: String, correctId: Long): List<VocabularyQuestionRefResponse> {
        val optionIds = when (refType) {
            "TERM" -> {
                val correct = termRepository.findById(correctId)
                    .orElseThrow { BaseException.NotFoundException("Term not found") }
                termRepository.findRandomIdsExcludeAndLanguageCode(
                    correctId,
                    correct.languageCode.name,
                    3,
                ) + correctId
            }
            "MEANING" -> {
                val correct = meaningRepository.findById(correctId)
                    .orElseThrow { BaseException.NotFoundException("Meaning not found") }
                meaningRepository.findRandomIdsExcludeAndLanguageCode(
                    correctId,
                    correct.languageCode.name,
                    3,
                ) + correctId
            }
            else -> throw BaseException.BadRequestException("Invalid option type")
        }
        val uniqueIds = optionIds.distinct()
        if (uniqueIds.size < 4) {
            throw BaseException.BadRequestException("Not enough options")
        }
        val options = when (refType) {
            "TERM" -> termRepository.findAllById(uniqueIds).map {
                VocabularyQuestionRefResponse(
                    type = refType,
                    id = it.id ?: 0,
                    text = it.textValue,
                )
            }
            "MEANING" -> meaningRepository.findAllById(uniqueIds).map {
                VocabularyQuestionRefResponse(
                    type = refType,
                    id = it.id ?: 0,
                    text = it.meaningText,
                )
            }
            else -> emptyList()
        }
        return options.shuffled().take(4)
    }

    private fun buildQuestionText(
        type: VocabularyQuestionType,
        questionRef: VocabularyQuestionRefResponse,
    ): String =
        when (type) {
            VocabularyQuestionType.LISTEN_SELECT_TERM ->
                "Listen to the audio and select the correct term."
            VocabularyQuestionType.LOOK_TERM_SELECT_MEANING ->
                "Look at the term \"${questionRef.text.orEmpty()}\" and select the correct meaning."
            VocabularyQuestionType.LOOK_MEANING_INPUT_TERM ->
                "Look at the meaning \"${questionRef.text.orEmpty()}\" and input the correct term."
            VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM ->
                "Look at the image and select the correct term."
        }

    private fun resolveTargetCount(requested: Int?): Int {
        val defaultCount = 20
        val value = requested ?: defaultCount
        return value.coerceIn(15, 20)
    }

    private fun selectMeaningByPreference(
        meanings: List<VocabularyMeaning>,
        preferredTargetLanguage: LanguageCode?,
    ): VocabularyMeaning? {
        if (meanings.isEmpty()) {
            return null
        }
        if (preferredTargetLanguage != null) {
            meanings.firstOrNull { it.languageCode == preferredTargetLanguage }?.let { return it }
        }
        meanings.firstOrNull { it.languageCode == LanguageCode.EN }?.let { return it }
        return meanings.first()
    }

    private fun buildQuestionFromVocab(
        type: VocabularyQuestionType,
        term: VocabularyTerm,
        meaning: VocabularyMeaning?,
        audioMedia: VocabularyMedia?,
        imageMedia: VocabularyMedia?,
        usedKeys: MutableSet<String>,
    ): VocabularyQuestionResponse? {
        val questionRef = when (type) {
            VocabularyQuestionType.LISTEN_SELECT_TERM -> audioMedia?.let {
                VocabularyQuestionRefResponse(type = "MEDIA", id = it.id ?: 0, url = it.url)
            }
            VocabularyQuestionType.LOOK_TERM_SELECT_MEANING -> VocabularyQuestionRefResponse(
                type = "TERM",
                id = term.id ?: 0,
                text = term.textValue,
            )
            VocabularyQuestionType.LOOK_MEANING_INPUT_TERM -> meaning?.let {
                VocabularyQuestionRefResponse(type = "MEANING", id = it.id ?: 0, text = it.meaningText)
            }
            VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM -> imageMedia?.let {
                VocabularyQuestionRefResponse(type = "MEDIA", id = it.id ?: 0, url = it.url)
            }
        } ?: return null

        val answerRef = when (type) {
            VocabularyQuestionType.LISTEN_SELECT_TERM,
            VocabularyQuestionType.LOOK_MEANING_INPUT_TERM,
            VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM,
            -> VocabularyQuestionRefResponse(type = "TERM", id = term.id ?: 0, text = term.textValue)
            VocabularyQuestionType.LOOK_TERM_SELECT_MEANING -> {
                val meaningValue = meaning ?: return null
                VocabularyQuestionRefResponse(type = "MEANING", id = meaningValue.id ?: 0, text = meaningValue.meaningText)
            }
        }

        val key = "${type.name}:${questionRef.id}:${answerRef.id}"
        if (!usedKeys.add(key)) {
            return null
        }

        val options = try {
            buildOptions(answerRef.type, answerRef.id)
        } catch (_: BaseException.BadRequestException) {
            return null
        }
        val questionText = buildQuestionText(type, questionRef)

        return VocabularyQuestionResponse(
            questionType = type,
            questionText = questionText,
            questionRef = questionRef,
            options = options,
            difficultyLevel = 1,
        )
    }
}
