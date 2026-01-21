package com.exe.vocafy_BE.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.context.annotation.Primary
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.context.SecurityContextHolderFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.OncePerRequestFilter
import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import com.exe.vocafy_BE.repo.LoginSessionRepository
import org.springframework.util.AntPathMatcher
import org.springframework.web.cors.CorsUtils
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import com.exe.vocafy_BE.handler.BaseException

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(
    SecurityAuthProperties::class,
    GoogleOauth2Properties::class,
    SecurityJwtProperties::class,
)
class SecurityConfig(
    private val authProperties: SecurityAuthProperties,
    private val googleOauth2Properties: GoogleOauth2Properties,
    private val jwtProperties: SecurityJwtProperties,
    private val loginSessionRepository: LoginSessionRepository,
) {

    @Bean
    @Primary
    fun jwtDecoder(): JwtDecoder {
        val secret = jwtProperties.secret
        val keySpec = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        val decoder = NimbusJwtDecoder.withSecretKey(keySpec).macAlgorithm(MacAlgorithm.HS256).build()
        val validator = JwtValidators.createDefault()
        decoder.setJwtValidator(validator)
        return decoder
    }

    @Bean("googleJwtDecoder")
    fun googleJwtDecoder(): JwtDecoder {
        val decoder = NimbusJwtDecoder.withJwkSetUri(googleOauth2Properties.jwkSetUri).build()
        decoder.setJwtValidator(IssuerOnlyValidator(googleOauth2Properties.issuer))
        return decoder
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val secret = jwtProperties.secret
        val keySpec = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        return NimbusJwtEncoder(ImmutableSecret(keySpec))
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val whitelistMatchers = authProperties.whitelist.map { AntStyleRequestMatcher(it) } +
            listOf(
                // public APIs (keep both legacy and /api paths during transition)
                AntStyleRequestMatcher("/syllabi", "GET"),
                AntStyleRequestMatcher("/syllabi/*", "GET"),
                AntStyleRequestMatcher("/vocabulary-questions/random", "GET"),
                AntStyleRequestMatcher("/webhook/sepay", "POST"),
                AntStyleRequestMatcher("/payments/packages", "GET"),

                AntStyleRequestMatcher("/api/syllabi", "GET"),
                AntStyleRequestMatcher("/api/syllabi/*", "GET"),
                AntStyleRequestMatcher("/api/vocabulary-questions/random", "GET"),
                AntStyleRequestMatcher("/api/webhook/sepay", "POST"),
                AntStyleRequestMatcher("/api/payments/packages", "GET"),

                // auth endpoints (public)
                AntStyleRequestMatcher("/auth/google", "POST"),
                AntStyleRequestMatcher("/auth/refresh", "POST"),
                AntStyleRequestMatcher("/api/auth/google", "POST"),
                AntStyleRequestMatcher("/api/auth/refresh", "POST"),
            )

        http
            .csrf { it.disable() }
            .cors { }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                if (whitelistMatchers.isNotEmpty()) {
                    auth.requestMatchers(*whitelistMatchers.toTypedArray()).permitAll()
                }
                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
                oauth2.authenticationEntryPoint(InvalidTokenEntryPoint())
            }
            .addFilterAfter(
                MissingTokenFilter(whitelistMatchers),
                SecurityContextHolderFilter::class.java,
            )
            .addFilterAfter(
                AccessTokenSessionFilter(whitelistMatchers, loginSessionRepository),
                MissingTokenFilter::class.java,
            )

        return http.build()
    }
}

class AntStyleRequestMatcher(
    private val pattern: String,
    private val httpMethod: String? = null,
) : RequestMatcher {

    private val antPathMatcher = AntPathMatcher()

    override fun matches(request: HttpServletRequest): Boolean {
        if (!httpMethod.isNullOrBlank() && !request.method.equals(httpMethod, ignoreCase = true)) {
            return false
        }

        val contextPath = request.contextPath ?: ""
        val requestUri = request.requestURI ?: ""
        val path = if (contextPath.isNotBlank() && requestUri.startsWith(contextPath)) {
            requestUri.substring(contextPath.length).ifBlank { "/" }
        } else {
            requestUri
        }

        return antPathMatcher.match(pattern, path)
    }
}

class IssuerOnlyValidator(
    private val issuer: String,
) : OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> {

    override fun validate(token: org.springframework.security.oauth2.jwt.Jwt): OAuth2TokenValidatorResult {
        if (issuer.isBlank()) {
            return OAuth2TokenValidatorResult.success()
        }
        val matchesIssuer = token.issuer?.toString() == issuer
        return if (matchesIssuer) {
            OAuth2TokenValidatorResult.success()
        } else {
            OAuth2TokenValidatorResult.failure(OAuth2Error("invalid_token", "invalid issuer", null))
        }
    }
}

class MissingTokenFilter(
    private val whitelistMatchers: List<RequestMatcher>,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        if (request.method == "OPTIONS") {
            return true
        }
        return whitelistMatchers.any { it.matches(request) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank()) {
            throw BaseException.MissingTokenException()
        }
        if (!authHeader.startsWith("Bearer ")) {
            throw BaseException.InvalidTokenException()
        }
        filterChain.doFilter(request, response)
    }
}

class AccessTokenSessionFilter(
    private val whitelistMatchers: List<RequestMatcher>,
    private val loginSessionRepository: LoginSessionRepository,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        if (request.method == "OPTIONS") {
            return true
        }
        return whitelistMatchers.any { it.matches(request) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            throw BaseException.InvalidTokenException()
        }
        val token = authHeader.removePrefix("Bearer ").trim()
        val session = loginSessionRepository.findByAccessTokenAndExpiredFalse(token)
        if (session == null) {
            throw BaseException.InvalidTokenException()
        }
        filterChain.doFilter(request, response)
    }
}

class InvalidTokenEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: org.springframework.security.core.AuthenticationException,
    ) {
        throw BaseException.InvalidTokenException()
    }
}
