package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.UserSyllabus
import org.springframework.data.jpa.repository.JpaRepository

interface UserSyllabusRepository : JpaRepository<UserSyllabus, Long>
