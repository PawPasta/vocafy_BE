package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.VocabularyQuestionType
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyQuestionRefResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyQuestionResponse
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyQuestionRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.service.VocabularyQuestionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VocabularyQuestionServiceImpl(
    private val questionRepository: VocabularyQuestionRepository,
    private val termRepository: VocabularyTermRepository,
    private val meaningRepository: VocabularyMeaningRepository,
    private val mediaRepository: VocabularyMediaRepository,
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
}
