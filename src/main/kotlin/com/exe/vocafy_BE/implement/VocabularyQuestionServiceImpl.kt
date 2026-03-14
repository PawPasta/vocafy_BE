package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.enum.VocabularyQuestionType
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyQuestionRefResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyQuestionResponse
import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.LearningState
import com.exe.vocafy_BE.enum.MediaType
import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import com.exe.vocafy_BE.model.entity.VocabularyMedia
import com.exe.vocafy_BE.model.entity.VocabularyTerm
import com.exe.vocafy_BE.repo.CourseVocabularyLinkRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyQuestionRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.repo.UserVocabProgressRepository
import com.exe.vocafy_BE.repo.EnrollmentRepository
import com.exe.vocafy_BE.repo.TopicCourseLinkRepository
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
    private val topicCourseLinkRepository: TopicCourseLinkRepository,
    private val courseVocabularyLinkRepository: CourseVocabularyLinkRepository,
) : VocabularyQuestionService {

    @Transactional(readOnly = true)
    override fun getRandom(): ServiceResult<VocabularyQuestionResponse> {
        repeat(RANDOM_QUESTION_ATTEMPTS) {
            val question = questionRepository.findRandom()
                ?: throw BaseException.NotFoundException("Question not found")
            if (!MEDIA_BASED_QUESTION_ENABLED && isMediaQuestionType(question.questionType)) {
                return@repeat
            }
            try {
                return ServiceResult(
                    message = "Ok",
                    result = buildStoredQuestionResponse(question),
                )
            } catch (_: BaseException) {
                return@repeat
            }
        }
        throw BaseException.NotFoundException("Question not found")
    }

    @Transactional(readOnly = true)
    override fun generateLearnedQuestions(count: Int?): ServiceResult<List<VocabularyQuestionResponse>> {
        val userId = securityUtil.getCurrentUserId()
        val targetCount = resolveTargetCount(count)
        val focusedEnrollment = enrollmentRepository.findByUserIdAndIsFocusedTrue(userId)
        val preferredTargetLanguage = focusedEnrollment?.preferredTargetLanguage
        val studyLanguage = focusedEnrollment?.syllabus?.let {
            resolveStudyLanguage(it.studyLanguage, it.languageSet)
        }
        val sampleSize = max(targetCount * 3, 30).coerceAtMost(200)
        val vocabIds = resolveLearnedVocabIds(
            userId = userId,
            focusedSyllabusId = focusedEnrollment?.syllabus?.id,
            limit = sampleSize,
        )

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

            val term = selectTermByStudyLanguage(terms, studyLanguage, preferredTargetLanguage)
            val meaning = selectMeaningByPreference(meanings, preferredTargetLanguage)
            val audioMedia = medias.firstOrNull { it.mediaType == MediaType.AUDIO_EN || it.mediaType == MediaType.AUDIO_JP }
            val imageMedia = medias.firstOrNull { it.mediaType == MediaType.IMAGE }

            val candidates = mutableListOf<VocabularyQuestionType>()
            if (meaning != null) {
                candidates.add(VocabularyQuestionType.LOOK_TERM_SELECT_MEANING)
                candidates.add(VocabularyQuestionType.LOOK_MEANING_INPUT_TERM)
            }
            if (MEDIA_BASED_QUESTION_ENABLED && audioMedia != null) {
                candidates.add(VocabularyQuestionType.LISTEN_SELECT_TERM)
            }
            if (MEDIA_BASED_QUESTION_ENABLED && imageMedia != null) {
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

        if (questions.isEmpty()) {
            throw BaseException.NotFoundException("No questions available for current syllabus")
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

    private fun buildStoredQuestionResponse(
        question: com.exe.vocafy_BE.model.entity.VocabularyQuestion,
    ): VocabularyQuestionResponse {
        val (questionRefType, answerRefType) = mapRefTypes(question.questionType)
        val questionRef = buildRef(questionRefType, question.questionRefId)
        val answerRef = buildRef(answerRefType, question.answerRefId)
        return VocabularyQuestionResponse(
            questionType = question.questionType,
            questionText = buildQuestionText(question.questionType, questionRef),
            questionRef = questionRef,
            options = buildOptions(answerRefType, answerRef.id),
            difficultyLevel = question.difficultyLevel,
        )
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
                resolveOptionIds(
                    correctId = correctId,
                    primary = termRepository.findRandomIdsExcludeAndLanguageCode(
                        correctId,
                        correct.languageCode.name,
                        3,
                    ),
                    fallback = termRepository.findRandomIdsExclude(correctId, 12),
                )
            }
            "MEANING" -> {
                val correct = meaningRepository.findById(correctId)
                    .orElseThrow { BaseException.NotFoundException("Meaning not found") }
                resolveOptionIds(
                    correctId = correctId,
                    primary = meaningRepository.findRandomIdsExcludeAndLanguageCode(
                        correctId,
                        correct.languageCode.name,
                        3,
                    ),
                    fallback = meaningRepository.findRandomIdsExclude(correctId, 12),
                )
            }
            else -> throw BaseException.BadRequestException("Invalid option type")
        }
        if (optionIds.size < 4) {
            throw BaseException.BadRequestException("Not enough options")
        }
        val options = when (refType) {
            "TERM" -> termRepository.findAllById(optionIds).map {
                VocabularyQuestionRefResponse(
                    type = refType,
                    id = it.id ?: 0,
                    text = it.textValue,
                )
            }
            "MEANING" -> meaningRepository.findAllById(optionIds).map {
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

    private fun selectTermByStudyLanguage(
        terms: List<VocabularyTerm>,
        studyLanguage: LanguageCode?,
        preferredTargetLanguage: LanguageCode?,
    ): VocabularyTerm {
        studyLanguage?.let { language ->
            terms.firstOrNull { it.languageCode == language }?.let { return it }
        }
        preferredTargetLanguage?.let { language ->
            terms.firstOrNull { it.languageCode != language }?.let { return it }
        }
        return terms.first()
    }

    private fun resolveOptionIds(
        correctId: Long,
        primary: List<Long>,
        fallback: List<Long>,
    ): List<Long> {
        val distractors = (primary + fallback)
            .filter { it != correctId }
            .distinct()
            .take(3)
        val finalIds = (distractors + correctId).distinct()
        return if (finalIds.size == 4) finalIds.shuffled() else finalIds
    }

    private fun resolveLearnedVocabIds(
        userId: java.util.UUID,
        focusedSyllabusId: Long?,
        limit: Int,
    ): List<Long> {
        if (focusedSyllabusId == null) {
            return userVocabProgressRepository.findRandomVocabIdsByUserIdAndLearningStateNot(
                userId,
                LearningState.UNKNOWN.code,
                limit,
            ).distinct()
        }
        val syllabusVocabIds = topicCourseLinkRepository.findCoursesBySyllabusId(focusedSyllabusId)
            .flatMap { course -> courseVocabularyLinkRepository.findVocabulariesByCourseId(course.id ?: 0L) }
            .mapNotNull { it.id }
            .distinct()
        if (syllabusVocabIds.isEmpty()) {
            return emptyList()
        }
        return userVocabProgressRepository.findAllByUserIdAndVocabularyIdIn(userId, syllabusVocabIds)
            .filter { LearningState.fromCode(it.learningState) != LearningState.UNKNOWN }
            .mapNotNull { it.vocabulary.id }
            .distinct()
            .shuffled()
            .take(limit)
    }

    private fun resolveStudyLanguage(studyLanguage: LanguageCode?, languageSet: LanguageSet): LanguageCode =
        studyLanguage ?: when (languageSet) {
            LanguageSet.EN_JP -> LanguageCode.JA
            LanguageSet.EN_VI -> LanguageCode.VI
            LanguageSet.JP_VI -> LanguageCode.VI
            LanguageSet.EN_JP_VI -> LanguageCode.JA
        }

    private fun buildQuestionFromVocab(
        type: VocabularyQuestionType,
        term: VocabularyTerm,
        meaning: VocabularyMeaning?,
        audioMedia: VocabularyMedia?,
        imageMedia: VocabularyMedia?,
        usedKeys: MutableSet<String>,
    ): VocabularyQuestionResponse? {
        if (!MEDIA_BASED_QUESTION_ENABLED && isMediaQuestionType(type)) {
            return null
        }
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

    private fun isMediaQuestionType(type: VocabularyQuestionType): Boolean =
        type == VocabularyQuestionType.LISTEN_SELECT_TERM ||
            type == VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM

    companion object {
        // TODO: Re-enable media-based question generation when media assets are fully prepared.
        private const val MEDIA_BASED_QUESTION_ENABLED = false
        private const val RANDOM_QUESTION_ATTEMPTS = 10
    }
}
