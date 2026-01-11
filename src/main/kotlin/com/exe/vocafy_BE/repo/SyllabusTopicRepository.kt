package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.SyllabusTopic
import org.springframework.data.jpa.repository.JpaRepository

interface SyllabusTopicRepository : JpaRepository<SyllabusTopic, Long>
