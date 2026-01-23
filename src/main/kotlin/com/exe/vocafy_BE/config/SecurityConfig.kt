package com.exe.vocafy_BE.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.context.SecurityContextHolderFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.util.AntPathMatcher
import org.springframework.web.cors.CorsUtils
import org.springframework.web.filter.OncePerRequestFilter
import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.response.BaseResponse
import com.exe.vocafy_BE.repo.LoginSessionRepository
import com.nimbusds.jose.jwk.source.ImmutableSecret
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(
    SecurityAuthProperties::class,
    SecurityJwtProperties::class,
)
class SecurityConfig(
    private val authProperties: SecurityAuthProperties,
    private val jwtProperties: SecurityJwtProperties,
    private val loginSessionRepository: LoginSessionRepository,
) {

    @Bean
    @Primary
    fun jwtDecoder(): JwtDecoder {
        val secret = jwtProperties.secret
        val keySpec = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        val decoder = NimbusJwtDecoder.withSecretKey(keySpec).macAlgorithm(MacAlgorithm.HS256).build()
        decoder.setJwtValidator(JwtValidators.createDefault())
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
                AntStyleRequestMatcher("/api/syllabus", "GET"),
                AntStyleRequestMatcher("/api/syllabus/*", "GET"),
                AntStyleRequestMatcher("/api/topics", "GET"),
                AntStyleRequestMatcher("/api/topics/*", "GET"),
                AntStyleRequestMatcher("/api/topics/by-syllabus/*", "GET"),
                AntStyleRequestMatcher("/api/courses", "GET"),
                AntStyleRequestMatcher("/api/courses/*", "GET"),
                AntStyleRequestMatcher("/api/courses/by-topic/*", "GET"),
                AntStyleRequestMatcher("/api/courses/*/vocabulary-set", "GET"),
                AntStyleRequestMatcher("/api/vocabularies", "GET"),
                AntStyleRequestMatcher("/api/vocabularies/*", "GET"),
                AntStyleRequestMatcher("/api/vocabularies/by-course/*", "GET"),
                AntStyleRequestMatcher("/api/vocabulary-questions/random", "GET"),
                AntStyleRequestMatcher("/api/webhook/sepay", "POST"),
                AntStyleRequestMatcher("/api/payments/packages", "GET"),

                // new public list endpoints
                AntStyleRequestMatcher("/api/users", "GET"),
                AntStyleRequestMatcher("/api/premium-packages", "GET"),
                AntStyleRequestMatcher("/api/subscription-transactions", "GET"),

                // auth endpoints (public)
                AntStyleRequestMatcher("/api/auth/google", "POST"),
                AntStyleRequestMatcher("/api/auth/firebase", "POST"),
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

class MissingTokenFilter(
    private val whitelistMatchers: List<RequestMatcher>,
) : OncePerRequestFilter() {

    private val objectMapper = ObjectMapper()

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
            writeErrorResponse(response, BaseException.MissingTokenException())
            return
        }
        if (!authHeader.startsWith("Bearer ")) {
            writeErrorResponse(response, BaseException.InvalidTokenException())
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun writeErrorResponse(response: HttpServletResponse, exception: BaseException) {
        response.status = exception.statusCode
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        val errorResponse = BaseResponse<Nothing>(
            success = false,
            message = exception.message,
        )
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}

class AccessTokenSessionFilter(
    private val whitelistMatchers: List<RequestMatcher>,
    private val loginSessionRepository: LoginSessionRepository,
) : OncePerRequestFilter() {

    private val objectMapper = ObjectMapper()

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
            writeErrorResponse(response, BaseException.InvalidTokenException())
            return
        }
        val token = authHeader.removePrefix("Bearer ").trim()
        val session = loginSessionRepository.findByAccessTokenAndExpiredFalse(token)
        if (session == null) {
            writeErrorResponse(response, BaseException.InvalidTokenException())
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun writeErrorResponse(response: HttpServletResponse, exception: BaseException) {
        response.status = exception.statusCode
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        val errorResponse = BaseResponse<Nothing>(
            success = false,
            message = exception.message,
        )
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}

class InvalidTokenEntryPoint : AuthenticationEntryPoint {

    private val objectMapper = ObjectMapper()

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: org.springframework.security.core.AuthenticationException,
    ) {
        val exception = BaseException.InvalidTokenException()
        response.status = exception.statusCode
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        val errorResponse = BaseResponse<Nothing>(
            success = false,
            message = exception.message,
        )
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
