package com.exe.vocafy_BE.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "unique_id", nullable = false, unique = true)
    val id: UUID? = null,

    @Column(name = "email", nullable = false, length = 255)
    val email: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    val role: Role = Role.USER,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    val status: Status = Status.ACTIVE,

    @Column(name = "last_login_at")
    var lastLoginAt: LocalDateTime? = null,

    @Column(name = "last_active_at")
    var lastActiveAt: LocalDateTime? = null,

    @Column(name = "sepay_code", length = 100)
    val sepayCode: String? = null,

    @Column(name = "fcm_token", length = 500)
    var fcmToken: String? = null,

    @OneToOne(mappedBy = "user")
    val profile: Profile? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = null,
)
