package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.VocabularyQuestion
import org.springframework.data.jpa.repository.JpaRepository

interface VocabularyQuestionRepository : JpaRepository<VocabularyQuestion, Long>
