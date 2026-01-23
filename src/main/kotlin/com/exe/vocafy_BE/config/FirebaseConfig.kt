package com.exe.vocafy_BE.config

import com.exe.vocafy_BE.util.FirebaseUtil
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

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

        val inputStream: InputStream = getServiceAccountStream(path)

        val credentials = inputStream.use { GoogleCredentials.fromStream(it) }
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

    private fun getServiceAccountStream(path: String): InputStream {
        // 1. Try explicit classpath prefix
        if (path.startsWith("classpath:")) {
            val resourcePath = path.removePrefix("classpath:")
            val resource = ClassPathResource(resourcePath)
            require(resource.exists()) { "Firebase service account not found in classpath: $resourcePath" }
            return resource.inputStream
        }

        // 2. Try as a physical file
        val file = File(path)
        if (file.exists() && file.isFile) {
            return FileInputStream(file)
        }

        // 3. Fallback: Try as a classpath resource (even without prefix)
        val resource = ClassPathResource(path)
        if (resource.exists()) {
            return resource.inputStream
        }

        throw IllegalArgumentException("Firebase service account JSON not found at: $path (checked filesystem and classpath)")
    }

    @Bean
    fun firebaseAuth(firebaseApp: FirebaseApp): FirebaseAuth = FirebaseAuth.getInstance(firebaseApp)

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging = FirebaseMessaging.getInstance(firebaseApp)

    companion object {
        private val log = LoggerFactory.getLogger(FirebaseConfig::class.java)
    }
}
