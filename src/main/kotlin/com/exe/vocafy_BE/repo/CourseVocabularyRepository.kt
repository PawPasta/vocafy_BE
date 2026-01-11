package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.CourseVocabulary
import org.springframework.data.jpa.repository.JpaRepository

interface CourseVocabularyRepository : JpaRepository<CourseVocabulary, Long>
