package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.SyllabusTargetLanguage
import org.springframework.data.jpa.repository.JpaRepository

interface SyllabusTargetLanguageRepository : JpaRepository<SyllabusTargetLanguage, Long> {
    fun findAllBySyllabusIdOrderByIdAsc(syllabusId: Long): List<SyllabusTargetLanguage>
    fun findAllBySyllabusIdInOrderBySyllabusIdAscIdAsc(syllabusIds: List<Long>): List<SyllabusTargetLanguage>
    fun deleteAllBySyllabusId(syllabusId: Long)
}
