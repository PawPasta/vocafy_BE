package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.CourseVocabulary
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.model.entity.SyllabusTopic
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.model.entity.Vocabulary
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.CourseVocabularyRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.SyllabusTopicRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
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
        syllabusTopicRepository: SyllabusTopicRepository,
        courseVocabularyRepository: CourseVocabularyRepository,
        userRepository: UserRepository,
    ) = ApplicationRunner {
        if (userRepository.count() == 0L) {
            val users = listOf(
                User(email = "admin@vocafy.local", displayName = "Admin User", role = Role.ADMIN, status = Status.ACTIVE),
                User(email = "manager1@vocafy.local", displayName = "Manager One", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "manager2@vocafy.local", displayName = "Manager Two", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "user1@vocafy.local", displayName = "User One", role = Role.USER, status = Status.ACTIVE),
                User(email = "user2@vocafy.local", displayName = "User Two", role = Role.USER, status = Status.ACTIVE),
                User(email = "user3@vocafy.local", displayName = "User Three", role = Role.USER, status = Status.ACTIVE),
                User(email = "user4@vocafy.local", displayName = "User Four", role = Role.USER, status = Status.ACTIVE),
                User(email = "user5@vocafy.local", displayName = "User Five", role = Role.USER, status = Status.ACTIVE),
                User(email = "user6@vocafy.local", displayName = "User Six", role = Role.USER, status = Status.ACTIVE),
                User(email = "user7@vocafy.local", displayName = "User Seven", role = Role.USER, status = Status.ACTIVE),
            )
            userRepository.saveAll(users)
        }

        if (
            syllabusRepository.count() > 0 ||
            courseRepository.count() > 0 ||
            vocabularyRepository.count() > 0 ||
            syllabusTopicRepository.count() > 0 ||
            courseVocabularyRepository.count() > 0
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

        val vocabularies = mutableListOf<Vocabulary>()
        for (i in 1..200) {
            val user = users[(i - 1) % users.size]
            vocabularies.add(
                Vocabulary(
                    jpKanji = "漢字$i",
                    jpKana = "かな$i",
                    jpRomaji = "romaji$i",
                    enWord = "word$i",
                    enIpa = "ipa$i",
                    meaningVi = "Nghia $i",
                    meaningEn = "Meaning $i",
                    meaningJp = "意味 $i",
                    note = if (i % 4 == 0) "Note $i" else null,
                    createdBy = user,
                )
            )
        }
        val savedVocabularies = vocabularyRepository.saveAll(vocabularies)

        val courses = mutableListOf<Course>()
        val topics = mutableListOf<SyllabusTopic>()
        var courseCounter = 1
        for ((syllabusIndex, syllabus) in savedSyllabi.withIndex()) {
            for (topicIndex in 1..4) {
                val topic = SyllabusTopic(
                    syllabus = syllabus,
                    title = "Topic ${syllabusIndex + 1}-$topicIndex",
                    description = if (topicIndex % 2 == 0) "Topic description ${syllabusIndex + 1}-$topicIndex" else null,
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

        val savedTopics = syllabusTopicRepository.saveAll(topics)

        val topicCount = savedTopics.size
        val coursesWithTopics = courses.mapIndexed { index, course ->
            val topic = savedTopics[index % topicCount]
            Course(
                id = course.id,
                title = course.title,
                description = course.description,
                syllabusTopic = topic,
                createdBy = course.createdBy,
                createdAt = course.createdAt,
                updatedAt = course.updatedAt,
            )
        }
        val savedCourses = courseRepository.saveAll(coursesWithTopics)

        val courseVocabularyLinks = mutableListOf<CourseVocabulary>()
        for ((courseIndex, course) in savedCourses.withIndex()) {
            val start = (courseIndex * 20) % savedVocabularies.size
            for (offset in 0 until 20) {
                val vocabulary = savedVocabularies[(start + offset) % savedVocabularies.size]
                courseVocabularyLinks.add(
                    CourseVocabulary(
                        course = course,
                        vocabulary = vocabulary,
                        sortOrder = offset + 1,
                    )
                )
            }
        }
        courseVocabularyRepository.saveAll(courseVocabularyLinks)
    }
}
