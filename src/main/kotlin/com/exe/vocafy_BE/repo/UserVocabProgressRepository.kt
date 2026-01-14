package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.UserVocabProgress
import org.springframework.data.jpa.repository.JpaRepository

interface UserVocabProgressRepository : JpaRepository<UserVocabProgress, Long>
