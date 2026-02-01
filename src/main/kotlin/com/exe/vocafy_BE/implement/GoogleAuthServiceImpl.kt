package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.config.SecurityJwtProperties
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.model.dto.response.LoginResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.entity.LoginSession
import com.exe.vocafy_BE.model.entity.Profile
import com.exe.vocafy_BE.model.entity.Subscription
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.LoginSessionRepository
import com.exe.vocafy_BE.repo.ProfileRepository
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.GoogleAuthService
import com.exe.vocafy_BE.util.EmailUtil
import com.exe.vocafy_BE.handler.BaseException.InvalidTokenException
import com.exe.vocafy_BE.handler.BaseException.MissingTokenException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

@Service
class GoogleAuthServiceImpl(
    private val firebaseAuth: FirebaseAuth,
    private val jwtDecoder: JwtDecoder,
    private val jwtEncoder: JwtEncoder,
    private val jwtProperties: SecurityJwtProperties,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val loginSessionRepository: LoginSessionRepository,
    private val emailUtil: EmailUtil,
) : GoogleAuthService {

    @Transactional
    override fun login(idToken: String, fcmToken: String?): ServiceResult<LoginResponse> {
        if (idToken.isBlank()) {
            throw MissingTokenException()
        }

        val decoded = tryVerifyFirebaseIdToken(idToken)
        validateFirebaseClaims(decoded)

        val email = decoded.email.orEmpty()
        val displayName = decoded.name.orEmpty().ifBlank { email }
        val picture = decoded.picture

        var user = userRepository.findByEmail(email)
        if (user == null) {
            user = userRepository.save(
                User(
                    email = email,
                    role = Role.USER,
                    status = Status.ACTIVE,
                    fcmToken = fcmToken,
                    lastLoginAt = LocalDateTime.now(),
                    lastActiveAt = LocalDateTime.now()
                )
            )
            emailUtil.sendEmail(email, "Welcome to VOCAFY", "Chào mừng bạn gia nhập vào  gia đình VOCAFY");
        } else {
            user.lastLoginAt = LocalDateTime.now()
            user.lastActiveAt = LocalDateTime.now()
            if (fcmToken != null && user.fcmToken != fcmToken) {
                user.fcmToken = fcmToken
            }
            user = userRepository.save(user)
        }

        var profile = profileRepository.findByUserId(user.id!!)
        if (profile == null) {
            profileRepository.save(
                Profile(
                    user = user,
                    displayName = displayName,
                    avatarUrl = picture,
                )
            )
        } else {
            // Update profile info if needed, e.g. if avatar or display name changed on Google side
            // For now, let's update avatar if it's different and not null
            if (picture != null && profile.avatarUrl != picture) {
                profile.avatarUrl = picture
            }
            if (displayName.isNotBlank() && profile.displayName != displayName) {
                profile.displayName = displayName
            }
            profileRepository.save(profile)
        }

        val userId = user.id
        if (userId != null && subscriptionRepository.findByUserId(userId) == null) {
            subscriptionRepository.save(
                Subscription(
                    user = user,
                    plan = SubscriptionPlan.FREE,
                )
            )
        }

        return createSession(user)
    }

    @Transactional
    override fun refresh(refreshToken: String): ServiceResult<LoginResponse> {
        if (refreshToken.isBlank()) {
            throw MissingTokenException()
        }

        val session = loginSessionRepository.findByRefreshTokenAndExpiredFalse(refreshToken)
            ?: throw InvalidTokenException()

        val decoded = tryDecodeInternal(refreshToken)
        val tokenType = decoded.getClaimAsString("type")
        if (tokenType != TOKEN_TYPE_REFRESH) {
            throw InvalidTokenException()
        }

        val userId = decoded.subject?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?: throw InvalidTokenException()
        if (session.user.id != userId) {
            throw InvalidTokenException()
        }

        return createSession(session.user)
    }

    @Transactional
    override fun logout(accessToken: String): ServiceResult<Unit> {
        if (accessToken.isBlank()) {
            throw MissingTokenException()
        }

        val session = loginSessionRepository.findByAccessTokenAndExpiredFalse(accessToken)
            ?: throw InvalidTokenException()

        loginSessionRepository.expireSession(session.id!!)

        return ServiceResult(
            message = "Logged out",
            result = Unit
        )
    }

    private fun tryVerifyFirebaseIdToken(idToken: String): FirebaseToken =
        try {
            firebaseAuth.verifyIdToken(idToken)
        } catch (ex: IllegalArgumentException) {
            throw InvalidTokenException()
        } catch (ex: FirebaseAuthException) {
            throw InvalidTokenException()
        }

    private fun validateFirebaseClaims(token: FirebaseToken) {
        val email = token.email
        val emailVerified = token.isEmailVerified

        if (email.isNullOrBlank() || !emailVerified) {
            throw InvalidTokenException()
        }
    }

    private fun tryDecodeInternal(token: String): Jwt =
        try {
            jwtDecoder.decode(token)
        } catch (ex: JwtException) {
            throw InvalidTokenException()
        }

    private fun createSession(user: User): ServiceResult<LoginResponse> {
        val userId = user.id ?: throw InvalidTokenException()
        loginSessionRepository.expireActiveSessions(userId)
        val accessToken = issueToken(user, TOKEN_TYPE_ACCESS, jwtProperties.expirationSeconds)
        val refreshToken = issueToken(user, TOKEN_TYPE_REFRESH, jwtProperties.refreshExpirationSeconds)
        loginSessionRepository.save(
            LoginSession(
                user = user,
                accessToken = accessToken,
                refreshToken = refreshToken,
                expired = false,
            )
        )
        return ServiceResult(
            message = "Ok",
            result = LoginResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
            ),
        )
    }

    private fun issueToken(user: User, type: String, expirationSeconds: Long): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .subject(user.id?.toString() ?: user.email)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expirationSeconds))
            .claim("email", user.email)
            .claim("role", user.role.name)
            .claim("status", user.status.name)
            .claim("type", type)
            .build()

        val headers = JwsHeader.with(MacAlgorithm.HS256).build()
        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).tokenValue
    }

    companion object {
        private const val TOKEN_TYPE_ACCESS = "access"
        private const val TOKEN_TYPE_REFRESH = "refresh"
    }
}
