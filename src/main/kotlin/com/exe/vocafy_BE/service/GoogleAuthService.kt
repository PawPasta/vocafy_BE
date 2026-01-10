package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.config.GoogleOauth2Properties
import com.exe.vocafy_BE.config.SecurityJwtProperties
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.model.entity.User
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
import java.time.Instant

@Service
class GoogleAuthService(
    @Qualifier("googleJwtDecoder")
    private val googleJwtDecoder: JwtDecoder,
    private val jwtEncoder: JwtEncoder,
    private val jwtProperties: SecurityJwtProperties,
    private val googleOauth2Properties: GoogleOauth2Properties,
    private val userRepository: UserRepository,
) {

    fun login(idToken: String): String {
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

        return issueToken(user)
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
        val audience = jwt.audience
        val clientId = googleOauth2Properties.clientId

        if (email.isNullOrBlank() || !emailVerified) {
            throw InvalidTokenException()
        }
        if (clientId.isBlank() || !audience.contains(clientId)) {
            throw InvalidTokenException()
        }
    }

    private fun issueToken(user: User): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .subject(user.id?.toString() ?: user.email)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(jwtProperties.expirationSeconds))
            .claim("email", user.email)
            .claim("role", user.role.name)
            .claim("status", user.status.name)
            .build()

        val headers = JwsHeader.with(MacAlgorithm.HS256).build()
        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).tokenValue
    }
}

class MissingTokenException : RuntimeException()

class InvalidTokenException : RuntimeException()
