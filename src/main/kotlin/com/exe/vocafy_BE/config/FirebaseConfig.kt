package com.exe.vocafy_BE.config

import com.exe.vocafy_BE.util.FirebaseUtil
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.FileInputStream

@Configuration
@EnableConfigurationProperties(FirebaseUtil::class)
class FirebaseConfig(
    private val firebaseUtil: FirebaseUtil,
) {

    @Bean
    fun firebaseApp(): FirebaseApp {
        FirebaseApp.getApps().firstOrNull()?.let { return it }

        val path = firebaseUtil.serviceAccountPath.trim()
        require(path.isNotBlank()) {
            "Missing Firebase service account path. Set security.firebase.service-account-path or FIREBASE_SERVICE_ACCOUNT_PATH"
        }

        val file = File(path)
        require(file.exists() && file.isFile) {
            "Firebase service account JSON not found at: $path"
        }

        val credentials = FileInputStream(file).use { GoogleCredentials.fromStream(it) }
        val optionsBuilder = FirebaseOptions.builder().setCredentials(credentials)
        if (firebaseUtil.projectId.isNotBlank()) {
            optionsBuilder.setProjectId(firebaseUtil.projectId)
        }

        val app = FirebaseApp.initializeApp(optionsBuilder.build())
        log.info(
            "FirebaseApp initialized{}",
            if (firebaseUtil.projectId.isNotBlank()) " (projectId=${firebaseUtil.projectId})" else "",
        )
        return app
    }

    @Bean
    fun firebaseAuth(firebaseApp: FirebaseApp): FirebaseAuth = FirebaseAuth.getInstance(firebaseApp)

    companion object {
        private val log = LoggerFactory.getLogger(FirebaseConfig::class.java)
    }
}

