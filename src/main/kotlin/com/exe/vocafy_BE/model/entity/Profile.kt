package com.exe.vocafy_BE.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "profiles")
class Profile(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "unique_id", nullable = false, unique = true)
    val id: UUID? = null,

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "unique_id", nullable = false)
    val user: User,

    @Column(name = "display_name", nullable = false, length = 100)
    var displayName: String,

    @Column(name = "avatar_url", length = 512)
    var avatarUrl: String? = null,

    @Column(name = "locale", length = 10)
    var locale: String? = null,

    @Column(name = "timezone", length = 40)
    var timezone: String? = null,
)
