package com.exe.vocafy_BE.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.auth")
data class SecurityAuthProperties(
    var whitelist: List<String> = emptyList(),
)

@ConfigurationProperties(prefix = "security.jwt")
data class SecurityJwtProperties(
    var secret: String = "",
    var expirationSeconds: Long = 86400,
    var refreshExpirationSeconds: Long = 604800,
)

@ConfigurationProperties(prefix = "security.dev")
data class SecurityDevProperties(
    var enabled: Boolean = false,
    var token: String = "",
    var allowedEmails: List<String> = emptyList(),
)
