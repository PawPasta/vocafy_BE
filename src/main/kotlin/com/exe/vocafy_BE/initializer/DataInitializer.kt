package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataInitializer {

    @Bean
    fun seedUsers(userRepository: UserRepository) = ApplicationRunner {
        if (userRepository.count() > 0) {
            return@ApplicationRunner
        }

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

    @Bean
    fun seedSyllabi(
        syllabusRepository: SyllabusRepository,
        userRepository: UserRepository,
    ) = ApplicationRunner {
        if (syllabusRepository.count() > 0) {
            return@ApplicationRunner
        }

        val users = userRepository.findAll()
        if (users.isEmpty()) {
            return@ApplicationRunner
        }

        val languageSets = LanguageSet.values()
        val visibilities = SyllabusVisibility.values()
        val sourceTypes = SyllabusSourceType.values()
        val items = mutableListOf<Syllabus>()

        for (i in 1..50) {
            val user = users[(i - 1) % users.size]
            items.add(
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

        syllabusRepository.saveAll(items)
    }
}
