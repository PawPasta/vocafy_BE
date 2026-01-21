package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.PremiumPackage
import org.springframework.data.jpa.repository.JpaRepository

interface PremiumPackageRepository : JpaRepository<PremiumPackage, Long> {
    fun findByActiveTrue(): List<PremiumPackage>
}
