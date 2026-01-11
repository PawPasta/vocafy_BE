package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Vocabulary
import org.springframework.data.jpa.repository.JpaRepository

interface VocabularyRepository : JpaRepository<Vocabulary, Long>
