package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.Profile
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.model.entity.Topic
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.model.entity.Vocabulary
import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import com.exe.vocafy_BE.model.entity.VocabularyMedia
import com.exe.vocafy_BE.model.entity.VocabularyTerm
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.ProfileRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.TopicRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.MediaType
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.ScriptType
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataInitializer {

    @Bean
    fun seedLearningData(
        syllabusRepository: SyllabusRepository,
        courseRepository: CourseRepository,
        vocabularyRepository: VocabularyRepository,
        topicRepository: TopicRepository,
        userRepository: UserRepository,
        profileRepository: ProfileRepository,
        vocabularyTermRepository: VocabularyTermRepository,
        vocabularyMeaningRepository: VocabularyMeaningRepository,
        vocabularyMediaRepository: VocabularyMediaRepository,
    ) = ApplicationRunner {
        if (userRepository.count() == 0L) {
            val users = listOf(
                User(email = "admin@vocafy.local", role = Role.ADMIN, status = Status.ACTIVE),
                User(email = "manager1@vocafy.local", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "manager2@vocafy.local", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "user1@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user2@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user3@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user4@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user5@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user6@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user7@vocafy.local", role = Role.USER, status = Status.ACTIVE),
            )
            val savedUsers = userRepository.saveAll(users)
            val profiles = savedUsers.mapIndexed { index, user ->
                Profile(
                    user = user,
                    displayName = when (index) {
                        0 -> "Admin User"
                        1 -> "Manager One"
                        2 -> "Manager Two"
                        else -> "User ${index - 2}"
                    },
                )
            }
            userRepository.flush()
            profileRepository.saveAll(profiles)
        }

        if (
            syllabusRepository.count() > 0 ||
            courseRepository.count() > 0 ||
            vocabularyRepository.count() > 0 ||
            topicRepository.count() > 0 ||
            vocabularyTermRepository.count() > 0 ||
            vocabularyMeaningRepository.count() > 0 ||
            vocabularyMediaRepository.count() > 0
        ) {
            return@ApplicationRunner
        }

        val users = userRepository.findAll()
        if (users.isEmpty()) {
            return@ApplicationRunner
        }

        val languageSets = LanguageSet.values()
        val visibilities = SyllabusVisibility.values()
        val sourceTypes = SyllabusSourceType.values()
        val syllabi = mutableListOf<Syllabus>()

        val syllabusCount = 5
        for (i in 1..syllabusCount) {
            val user = users[(i - 1) % users.size]
            syllabi.add(
                Syllabus(
                    title = "Syllabus $i",
                    description = if (i % 3 == 0) "Sample syllabus description $i" else null,
                    totalDays = 7 + (i % 24),
                    languageSet = languageSets[(i - 1) % languageSets.size],
                    visibility = visibilities[(i - 1) % visibilities.size],
                    sourceType = sourceTypes[(i - 1) % sourceTypes.size],
                    createdBy = user,
                    active = i % 5 != 0,
                )
            )
        }

        val savedSyllabi = syllabusRepository.saveAll(syllabi)

        val courses = mutableListOf<Course>()
        val topics = mutableListOf<Topic>()
        var courseCounter = 1
        for ((syllabusIndex, syllabus) in savedSyllabi.withIndex()) {
            for (topicIndex in 1..4) {
                val topic = Topic(
                    syllabus = syllabus,
                    title = "Topic ${syllabusIndex + 1}-$topicIndex",
                    description = if (topicIndex % 2 == 0) "Topic description ${syllabusIndex + 1}-$topicIndex" else null,
                    totalDays = 3 + (topicIndex % 5),
                    sortOrder = topicIndex,
                )
                topics.add(topic)
                for (courseIndex in 1..5) {
                    val user = users[(courseCounter - 1) % users.size]
                    val course = Course(
                        title = "Course $courseCounter",
                        description = if (courseCounter % 3 == 0) "Course description $courseCounter" else null,
                        createdBy = user,
                    )
                    courses.add(course)
                    courseCounter += 1
                }
            }
        }

        val savedTopics = topicRepository.saveAll(topics)

        val topicCount = savedTopics.size
        val coursesWithTopics = courses.mapIndexed { index, course ->
            val topic = savedTopics[index % topicCount]
            Course(
                id = course.id,
                title = course.title,
                description = course.description,
                syllabusTopic = topic,
                createdAt = course.createdAt,
                updatedAt = course.updatedAt,
            )
        }
        val savedCourses = courseRepository.saveAll(coursesWithTopics)

        val vocabularies = mutableListOf<Vocabulary>()
        val terms = mutableListOf<VocabularyTerm>()
        val meanings = mutableListOf<VocabularyMeaning>()
        val medias = mutableListOf<VocabularyMedia>()
        var vocabCounter = 1
        for ((courseIndex, course) in savedCourses.withIndex()) {
            for (offset in 1..20) {
                val vocab = Vocabulary(
                    course = course,
                    note = if (vocabCounter % 4 == 0) "Note $vocabCounter" else null,
                    sortOrder = offset,
                )
                vocabularies.add(vocab)
                vocabCounter += 1
            }
        }
        val savedVocabularies = vocabularyRepository.saveAll(vocabularies)

        for ((index, vocab) in savedVocabularies.withIndex()) {
            val base = index + 1
            terms.add(
                VocabularyTerm(
                    vocabulary = vocab,
                    languageCode = LanguageCode.JA,
                    scriptType = ScriptType.KANJI,
                    textValue = "漢字$base",
                )
            )
            terms.add(
                VocabularyTerm(
                    vocabulary = vocab,
                    languageCode = LanguageCode.EN,
                    scriptType = ScriptType.LATIN,
                    textValue = "word$base",
                )
            )
            meanings.add(
                VocabularyMeaning(
                    vocabulary = vocab,
                    languageCode = LanguageCode.EN,
                    meaningText = "Meaning $base",
                    partOfSpeech = PartOfSpeech.NOUN,
                    senseOrder = 1,
                )
            )
            medias.add(
                VocabularyMedia(
                    vocabulary = vocab,
                    mediaType = MediaType.IMAGE,
                    url = "https://example.com/media/$base.png",
                )
            )
        }

        vocabularyTermRepository.saveAll(terms)
        vocabularyMeaningRepository.saveAll(meanings)
        vocabularyMediaRepository.saveAll(medias)
    }
}
