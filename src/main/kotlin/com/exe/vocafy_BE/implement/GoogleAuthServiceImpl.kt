package com.exe.vocafy_BE.implement

import org.slf4j.LoggerFactory
import com.exe.vocafy_BE.config.SecurityJwtProperties
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.handler.BaseException
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
import org.springframework.beans.factory.annotation.Qualifier
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
import java.util.UUID

@Service
class GoogleAuthServiceImpl(
    @Qualifier("googleJwtDecoder")
    private val googleJwtDecoder: JwtDecoder,
    private val jwtDecoder: JwtDecoder,
    private val jwtEncoder: JwtEncoder,
    private val jwtProperties: SecurityJwtProperties,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val loginSessionRepository: LoginSessionRepository,
) : GoogleAuthService {

    @Transactional
    override fun login(idToken: String): ServiceResult<LoginResponse> {
        if (idToken.isBlank()) {
            throw BaseException.MissingTokenException()
        }

        val decoded = tryDecode(idToken)
        validateGoogleClaims(decoded)

        val email = decoded.getClaimAsString("email").orEmpty()
        val displayName = decoded.getClaimAsString("name").orEmpty().ifBlank { email }
        val picture = decoded.getClaimAsString("picture")

        val user = userRepository.findByEmail(email)
            ?: userRepository.save(
                User(
                    email = email,
                    role = Role.USER,
                    status = Status.ACTIVE,
                )
            )

        if (user.profile == null) {
            profileRepository.save(
                Profile(
                    user = user,
                    displayName = displayName,
                    avatarUrl = picture,
                )
            )
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
            throw BaseException.MissingTokenException()
        }

        val session = loginSessionRepository.findByRefreshTokenAndExpiredFalse(refreshToken)
            ?: throw BaseException.InvalidTokenException()

        val decoded = tryDecodeInternal(refreshToken)
        val tokenType = decoded.getClaimAsString("type")
        if (tokenType != TOKEN_TYPE_REFRESH) {
            throw BaseException.InvalidTokenException()
        }

        val userId = decoded.subject?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?: throw BaseException.InvalidTokenException()
        if (session.user.id != userId) {
            throw BaseException.InvalidTokenException()
        }

        return createSession(session.user)
    }

   private fun tryDecode(idToken: String): Jwt {
        try {
            log.info("Start decode google idToken")
            val decoded = googleJwtDecoder.decode(idToken)
            log.info("Decode success, subject={}", decoded.subject)
            return decoded
        } catch (ex: JwtException) {
            log.error("Google token decode failed", ex)
            throw BaseException.InvalidTokenException()
        }
    }

    private fun validateGoogleClaims(jwt: Jwt) {
        val email = jwt.getClaimAsString("email")
        val emailVerified = jwt.getClaimAsBoolean("email_verified") ?: false

        if (email.isNullOrBlank() || !emailVerified) {
            throw BaseException.InvalidTokenException()
        }
    }

    private fun tryDecodeInternal(token: String): Jwt =
        try {
            jwtDecoder.decode(token)
        } catch (ex: JwtException) {
            throw BaseException.InvalidTokenException()
        }

    private fun createSession(user: User): ServiceResult<LoginResponse> {
        
        val userId = user.id ?: throw BaseException.InvalidTokenException()
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
        private val log = LoggerFactory.getLogger(GoogleAuthServiceImpl::class.java)
        private const val TOKEN_TYPE_ACCESS = "access"
        private const val TOKEN_TYPE_REFRESH = "refresh"
    }
}
