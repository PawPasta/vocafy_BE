package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProfileRepository : JpaRepository<Profile, UUID>
