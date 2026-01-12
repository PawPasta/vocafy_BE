package com.exe.vocafy_BE.repo

import com.exe.vocafy_BE.model.entity.PaymentMethod
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentMethodRepository : JpaRepository<PaymentMethod, Long>
