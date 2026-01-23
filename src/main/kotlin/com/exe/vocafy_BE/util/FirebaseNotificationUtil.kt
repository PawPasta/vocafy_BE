package com.exe.vocafy_BE.util

import com.google.firebase.messaging.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Utility class for sending push notifications via Firebase Cloud Messaging (FCM)
 */
@Component
class FirebaseNotificationUtil(
    private val firebaseMessaging: FirebaseMessaging
) {
    private val log = LoggerFactory.getLogger(FirebaseNotificationUtil::class.java)

    /**
     * Send notification to a single user by FCM token
     * @param fcmToken The FCM token of the target device (can be null for web users)
     * @param title Notification title
     * @param body Notification body
     * @param data Additional data payload (optional)
     * @return true if sent successfully, false otherwise
     */
    fun sendToUser(
        fcmToken: String?,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): Boolean {
        if (fcmToken.isNullOrBlank()) {
            log.warn("FCM token is null or blank, skipping notification")
            return false
        }

        return try {
            val message = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .setAndroidConfig(
                    AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(
                            AndroidNotification.builder()
                                .setSound("default")
                                .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                .build()
                        )
                        .build()
                )
                .setApnsConfig(
                    ApnsConfig.builder()
                        .setAps(
                            Aps.builder()
                                .setSound("default")
                                .build()
                        )
                        .build()
                )
                .putAllData(data)
                .build()

            val response = firebaseMessaging.send(message)
            log.info("Notification sent successfully to token: ${fcmToken.take(20)}..., response: $response")
            true
        } catch (e: FirebaseMessagingException) {
            log.error("Failed to send notification: ${e.message}", e)
            false
        }
    }

    /**
     * Send notification to a list of users by their FCM tokens
     * Filters out null/blank tokens automatically
     * @param fcmTokens List of FCM tokens (can contain null values)
     * @param title Notification title
     * @param body Notification body
     * @param data Additional data payload (optional)
     * @return BatchResponse with success/failure counts, or null if no valid tokens
     */
    fun sendToListOfUsers(
        fcmTokens: List<String?>,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): BatchResponse? {
        val validTokens = fcmTokens.filterNotNull().filter { it.isNotBlank() }

        if (validTokens.isEmpty()) {
            log.warn("No valid FCM tokens provided, skipping multicast notification")
            return null
        }

        return try {
            val message = MulticastMessage.builder()
                .addAllTokens(validTokens)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .setAndroidConfig(
                    AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(
                            AndroidNotification.builder()
                                .setSound("default")
                                .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                .build()
                        )
                        .build()
                )
                .setApnsConfig(
                    ApnsConfig.builder()
                        .setAps(
                            Aps.builder()
                                .setSound("default")
                                .build()
                        )
                        .build()
                )
                .putAllData(data)
                .build()

            val response = firebaseMessaging.sendEachForMulticast(message)
            log.info(
                "Multicast notification sent: {} success, {} failed out of {} tokens",
                response.successCount,
                response.failureCount,
                validTokens.size
            )
            response
        } catch (e: FirebaseMessagingException) {
            log.error("Failed to send multicast notification: ${e.message}", e)
            null
        }
    }

    /**
     * Send notification to all standard users (users with valid FCM tokens)
     * This is an alias for sendToListOfUsers for semantic clarity
     * @param fcmTokens List of FCM tokens from standard users
     * @param title Notification title
     * @param body Notification body
     * @param data Additional data payload (optional)
     * @return BatchResponse with success/failure counts, or null if no valid tokens
     */
    fun sendToStandardUsers(
        fcmTokens: List<String?>,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): BatchResponse? {
        return sendToListOfUsers(fcmTokens, title, body, data)
    }

    /**
     * Send data-only message (silent notification) to a user
     * Useful for triggering background sync without showing notification
     * @param fcmToken The FCM token of the target device
     * @param data Data payload
     * @return true if sent successfully, false otherwise
     */
    fun sendDataMessage(
        fcmToken: String?,
        data: Map<String, String>
    ): Boolean {
        if (fcmToken.isNullOrBlank()) {
            log.warn("FCM token is null or blank, skipping data message")
            return false
        }

        return try {
            val message = Message.builder()
                .setToken(fcmToken)
                .putAllData(data)
                .build()

            val response = firebaseMessaging.send(message)
            log.info("Data message sent successfully: $response")
            true
        } catch (e: FirebaseMessagingException) {
            log.error("Failed to send data message: ${e.message}", e)
            false
        }
    }

    /**
     * Send notification to a topic
     * Users must be subscribed to the topic to receive the notification
     * @param topic Topic name
     * @param title Notification title
     * @param body Notification body
     * @param data Additional data payload (optional)
     * @return true if sent successfully, false otherwise
     */
    fun sendToTopic(
        topic: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): Boolean {
        return try {
            val message = Message.builder()
                .setTopic(topic)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .putAllData(data)
                .build()

            val response = firebaseMessaging.send(message)
            log.info("Topic notification sent successfully to '$topic': $response")
            true
        } catch (e: FirebaseMessagingException) {
            log.error("Failed to send topic notification: ${e.message}", e)
            false
        }
    }
}

