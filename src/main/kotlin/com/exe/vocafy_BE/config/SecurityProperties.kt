package com.exe.vocafy_BE.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.auth")
data class SecurityAuthProperties(
    var whitelist: List<String> = emptyList(),
)

@ConfigurationProperties(prefix = "security.oauth2.google")
data class GoogleOauth2Properties(
    var issuer: String = "",
    var jwkSetUri: String = "",
)
