package com.exe.vocafy_BE.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import java.io.FileInputStream

@ConfigurationProperties(prefix = "security.firebase")
data class FirebaseUtil(

    @param:Value("\${security.firebase.project-id}")
    var projectId: String,

    @param:Value("\${security.firebase.service-account-path}")
    var serviceAccountPath: String,
)