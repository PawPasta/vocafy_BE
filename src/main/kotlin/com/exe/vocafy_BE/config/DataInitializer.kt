package com.exe.vocafy_BE.config

import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.model.entity.User
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
}
