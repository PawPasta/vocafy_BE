package com.exe.vocafy_BE.util

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Year

@Component
class EmailUtil(
    private val javaMailSender: JavaMailSender
) {

    @Value("\${spring.mail.username}")
    private lateinit var senderEmail: String

    @Async
    fun sendEmail(to: String, subject: String, content: String, isHtml: Boolean = true) {
        try {
            val message: MimeMessage = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setFrom(senderEmail, "Vocafy Support")
            helper.setTo(to)
            helper.setSubject(subject)
            
            val finalContent = if (isHtml) wrapInTemplate(content) else content
            helper.setText(finalContent, isHtml)

            javaMailSender.send(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun wrapInTemplate(bodyContent: String): String {
        return try {
            val resource = ClassPathResource("templates/email-template.html")
            var template = String(resource.inputStream.readAllBytes(), StandardCharsets.UTF_8)
            
            template = template.replace("{{bodyContent}}", bodyContent)
            template = template.replace("{{currentYear}}", Year.now().toString())
            
            template
        } catch (e: Exception) {
            // Fallback if template loading fails
            """
            <html><body>
            $bodyContent
            </body></html>
            """.trimIndent()
        }
    }
}
