package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.config.SecurityJwtProperties
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.model.dto.response.LoginResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.entity.LoginSession
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.LoginSessionRepository
import com.exe.vocafy_BE.repo.UserRepository
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
class GoogleAuthService(
    @Qualifier("googleJwtDecoder")
    private val googleJwtDecoder: JwtDecoder,
    private val jwtDecoder: JwtDecoder,
    private val jwtEncoder: JwtEncoder,
    private val jwtProperties: SecurityJwtProperties,
    private val userRepository: UserRepository,
    private val loginSessionRepository: LoginSessionRepository,
) {

    @Transactional
    fun login(idToken: String): ServiceResult<LoginResponse> {
        if (idToken.isBlank()) {
            throw MissingTokenException()
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
                    displayName = displayName,
                    avatarUrl = picture,
                    role = Role.USER,
                    status = Status.ACTIVE,
                )
            )

        return createSession(user)
    }

    @Transactional
    fun refresh(refreshToken: String): ServiceResult<LoginResponse> {
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

    private fun tryDecode(idToken: String): Jwt =
        try {
            googleJwtDecoder.decode(idToken)
        } catch (ex: JwtException) {
            throw InvalidTokenException()
        }

    private fun validateGoogleClaims(jwt: Jwt) {
        val email = jwt.getClaimAsString("email")
        val emailVerified = jwt.getClaimAsBoolean("email_verified") ?: false

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

class MissingTokenException : RuntimeException()

class InvalidTokenException : RuntimeException()
