package com.exe.vocafy_BE.util

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

object PageUtil {
    fun <T> toPage(items: List<T>, pageable: Pageable): Page<T> {
        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(items.size)
        val content = if (start >= items.size) emptyList() else items.subList(start, end)
        return PageImpl(content, pageable, items.size.toLong())
    }
}
