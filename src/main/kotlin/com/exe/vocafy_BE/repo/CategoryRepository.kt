package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.Category
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Category>
}
